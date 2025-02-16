/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2025 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.restful;

import static cn.ttron.metadata.MetadataConstants.KEY_UNID;
import static cn.ttron.util.Empty.isEmpty;
import static io.onme.stuck.LocalCache.REDIS_POOL;
import static io.onme.stuck.LocalCache.TALKJS_ACS_TKN;
import static io.onme.stuck.LocalCache.TALKJS_APP_ID;
import static io.onme.stuck.LocalCache.TALKJS_APP_SECRET;
import static io.onme.stuck.model.StuckConstants.STATE_IN_TALK;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import cn.ttron.metadata.Result;
import io.onme.stuck.LocalCache;
import io.onme.stuck.model.StuckSpot;
import io.onme.stuck.model.StuckUser;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import redis.clients.jedis.Jedis;

/**
 * @Ttron Feb 6, 2025 
 */
@Path("talkjs")
public class TalkJSResource extends StuckResource
{
	private static final Logger LOG = LogManager.getLogger( TalkJSResource.class );

	private String getAccessToken()
	{
		String token = null;

		try (Jedis jedis = REDIS_POOL.getResource())
		{
			token = jedis.get( "TALKJS_ACS_TKN" );
			if (isEmpty( token ))
			{
				token = renewTalkJSAccessToken();
				jedis.set( "TALKJS_ACS_TKN", token );
				jedis.expire( "TALKJS_ACS_TKN", 110 );
			}
		}
		return token;
	}


	private String renewTalkJSAccessToken()
	{
		Algorithm algorithm = Algorithm.HMAC256( TALKJS_APP_SECRET );
		String token = JWT.create().withClaim( "tokenType", "app" ).withIssuer( TALKJS_APP_ID )
				.withExpiresAt( new Date( System.currentTimeMillis() + 120 * 1000 ) ).sign( algorithm );
		// .withExpiresAt( new Date( System.currentTimeMillis() + 30 * 24 * 3600 * 1000 ) ).sign( algorithm );
		// will be fail
		// LOG.debug( "New token: {}", token );
		TALKJS_ACS_TKN = token;

		return token;
	}


	@POST
	@Produces({ APPLICATION_XML, APPLICATION_JSON })
	public Response newConversation(@FormParam("openid") String openId, @FormParam("spot_unid") String spotUnid,
			@FormParam("geohash") String geohash, @FormParam("alias") String alias, @FormParam("avatar") String avatar,
			@FormParam("email") String email)
	{
		LOG.info( "{} new conversation on spot: {}, Geohash: {}", openId, spotUnid, geohash );
		String conversationId = geohash + "_" + alias;
		authScope( "chat" );

		String token = getAccessToken();

		StuckUser user = fetchUser( token, openId );
		StuckSpot spot = fetchSpot( spotUnid );
		if (user == null)
		{
			user = new StuckUser();
			user.setOpenId( openId );
			user.setAlias( alias );
			user.setAvatar( avatar );

			if (spot != null && spot.getOpenIdStucker().equalsIgnoreCase( openId ))
				user.setRole( "stucker" );
			else
				user.setRole( "saver" );
			pushUser( token, user );
		}

		boolean created = false;

		try (Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build())
		{
			// { "participants": ["12081", "8029"], "subject": "What a lovely day!", "custom": { "category":
			// "order_inquiry" } }
			JSONObject json = new JSONObject();
			JSONArray participants = new JSONArray();
			participants.put( user.getOpenId() );
			// participants.put( "12081" );
			json.put( "participants", participants );
			json.put( "subject", alias + " stuck at " + geohash );
			JSONArray welcomeMessages = new JSONArray();
			welcomeMessages.put( "I am " + alias + ", stuck at " + geohash );
			welcomeMessages.put( "Need a pull/tow, please help me out." );
			json.put( "welcomeMessages", welcomeMessages );
			LOG.debug( json );

			WebTarget target = client.target( "https://api.talkjs.com/v1/" + TALKJS_APP_ID + "/conversations/" + conversationId );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE ).header( "Authorization", "Bearer " + token )
					.put( Entity.json( json.toString() ) );
			String data = response.readEntity( String.class );

			// if (!isEmpty( data ))
			LOG.debug( "New Conversation<={}|{}", response.getStatus(), data );

			if (response.getStatus() == 200)
				created = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (created)
		{
			try (Connection connection = LocalCache.DATASOURCE.getConnection())
			{
				StringBuilder select = new StringBuilder();
				select.append( "insert into SK_CONVERSATION(OPEN_ID_1ST,ID_CVSN,ID_SPOT" );
				select.append( ") values(?,?,?)" );
				PreparedStatement pstmt = connection.prepareStatement( select.toString() );
				pstmt.setString( 1, openId );
				pstmt.setString( 2, conversationId );
				pstmt.setInt( 3, spot != null ? spot.getAIID() : 0 );

				LOG.trace( "Insert SK_CONVERSATION: {}", pstmt.toString() );
				pstmt.execute();
			}
			catch (SQLException e)
			{
				LOG.error( "Insert SK_CONVERSATION: {}", e.getLocalizedMessage() );
			}

			log( openId, spot != null ? spot.getAIID() : 0, "New conversation: " + conversationId );

			try (Connection connection = LocalCache.DATASOURCE.getConnection())
			{
				StringBuilder select = new StringBuilder();
				select.append( "update SK_SPOT set DATIME_LAST=now(),ID_STATE=?, ID_CVSN=? where UNID=?" );
				PreparedStatement pstmt = connection.prepareStatement( select.toString() );
				pstmt.setInt( 1, STATE_IN_TALK );// In talk
				pstmt.setString( 2, conversationId );
				pstmt.setString( 3, spotUnid );
				LOG.trace( "Update SK_SPOT: {}", pstmt.toString() );
				pstmt.executeUpdate();
			}
			catch (SQLException e)
			{
				LOG.error( "Update SK_SPOT: {}", e.getLocalizedMessage() );
			}
			sendMessage( token, conversationId, openId, "Help me!" );
		}

		Result result = new Result( "200", "Successful." );
		result.setContext( conversationId );
		return Response.ok().entity( result ).build();
	}


	@PUT
	@Path("{unid}")
	@Produces({ APPLICATION_XML, APPLICATION_JSON })
	public Response joinConversation(@PathParam(KEY_UNID) String conversationId, @FormParam("openid") String openId,
			@FormParam("spot_unid") String spotUnid, @FormParam("alias") String alias, @FormParam("avatar") String avatar,
			@FormParam("email") String email)
	{
		LOG.info( "{} join conversation: {}, spot: {}", openId, conversationId, spotUnid );
		authScope( "chat" );

		String token = getAccessToken();

		StuckSpot spot = fetchSpot( spotUnid );

		boolean joined = false;
		try (Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build())
		{
			WebTarget target = client.target( "https://api.talkjs.com/v1/" + TALKJS_APP_ID + "/conversations/" + conversationId
					+ "/participants/" + openId );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE ).header( "Authorization", "Bearer " + token )
					.put( Entity.json( "{ \"access\": \"ReadWrite\", \"notify\": true }" ) );
			String data = response.readEntity( String.class );

			// if (!isEmpty( data ))
			LOG.debug( "New Conversation<={}|{}", response.getStatus(), data );
			if (response.getStatus() == 200)
				joined = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (joined)
		{
			try (Connection connection = LocalCache.DATASOURCE.getConnection())
			{
				StringBuilder select = new StringBuilder();
				select.append( "update SK_SPOT set DATIME_LAST=now() where UNID=?" );
				PreparedStatement pstmt = connection.prepareStatement( select.toString() );
				pstmt.setString( 1, spotUnid );
				LOG.trace( "Update SK_SPOT: {}", pstmt.toString() );
				pstmt.executeUpdate();
			}
			catch (SQLException e)
			{
				LOG.error( "Update SK_SPOT: {}", e.getLocalizedMessage() );
			}

			log( openId, spot != null ? spot.getAIID() : 0, "Join conversation: " + conversationId );
		}

		Result result = new Result( "200", "Successful." );
		result.setContext( conversationId );
		return Response.ok().entity( result ).build();
	}


	private static void sendMessage(String token, String conversationId, String senderId, String... content)
	{
		try (Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build())
		{
			for ( String line : content )
			{
				JSONArray json = new JSONArray();
				JSONObject msg = new JSONObject();
				json.put( msg );
				msg.put( "type", "UserMessage" );
				msg.put( "text", line );
				msg.put( "sender", senderId );

				WebTarget target = client.target(
						"https://api.talkjs.com/v1/" + TALKJS_APP_ID + "/conversations/" + conversationId + "/messages" );
				Response response = target.request( MediaType.APPLICATION_JSON_TYPE ).header( "Authorization", "Bearer " + token )
						.post( Entity.json( json.toString() ) );
				String data = response.readEntity( String.class );
				// if (!isEmpty( data ))
				LOG.info( "Message Sent<={}|{}", response.getStatus(), data );
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * @param openId
	 * @param string
	 */
	private void pushUser(String token, StuckUser user)
	{
		// { "name":"Alice", "email": ["alice@example.com"], "photoUrl":
		// "https://yoursite.com/userpictures/12081.png", "welcomeMessage": "Hey there! :-)", "role": "buyer"
		// }

		JSONObject json = new JSONObject();
		json.put( "name", user.getAlias() );
		json.put( "photoUrl", user.getAvatar() );
		json.put( "role", user.getRole() );

		try (Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build())
		{
			WebTarget target = client.target( "https://api.talkjs.com/v1/" + TALKJS_APP_ID + "/users/" + user.getOpenId() );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE ).header( "Authorization", "Bearer " + token )
					.put( Entity.json( json.toString() ) );
			String data = response.readEntity( String.class );

			// if (!isEmpty( data ))
			LOG.debug( "New User<={}|{}", response.getStatus(), data );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * @param openId
	 * @return
	 */
	private StuckUser fetchUser(String token, String openId)
	{
		StuckUser user = null;
		try (Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build())
		{
			WebTarget target = client.target( "https://api.talkjs.com/v1/" + TALKJS_APP_ID + "/users/" + openId );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE ).header( "Authorization", "Bearer " + token )
					.get();
			String data = response.readEntity( String.class );
			LOG.debug( "Get User<={}|{}", response.getStatus(), data );

			if (response.getStatus() == 200 && !isEmpty( data ))
			{
				JSONObject json = new JSONObject( data );
				user = new StuckUser();
				user.setOpenId( json.getString( "id" ) );
				user.setRole( json.getString( "" ) );
				user.setAlias( json.getString( "name" ) );
				user.setAvatar( json.getString( "photoUrl" ) );
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return user;
	}
}
