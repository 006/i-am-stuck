
/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2025 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.talkjs;

import static cn.ttron.util.Empty.isEmpty;
import static io.onme.stuck.LocalCache.TALKJS_ACS_TKN;
import static io.onme.stuck.LocalCache.TALKJS_APP_ID;
import static io.onme.stuck.LocalCache.TALKJS_APP_SECRET;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.json.JSONArray;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import io.onme.stuck.LocalCache;
import io.onme.stuck.TestBase;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @Ttron Feb 3, 2025 
 */
public class TestTalkRestAPI extends TestBase
{

	private static final Logger LOG = LogManager.getLogger( TestTalkRestAPI.class );

	private static void createConversation(String accessToken, String conversationId, String jsonStr)
	{
		final Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build();
		try
		{
			JSONObject json = new JSONObject( jsonStr );
			WebTarget target = client.target( "https://api.talkjs.com/v1/" + TALKJS_APP_ID + "/conversations/" + conversationId );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE )
					.header( "Authorization", "Bearer " + accessToken ).put( Entity.json( json.toString() ) );
			String data = response.readEntity( String.class );
			if (!isEmpty( data ))
				LOG.info( data );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			client.close();
		}
	}


	/**
	 * @param accessToken
	 */
	private static void getAllUsers(String accessToken)
	{
		final Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build();
		try
		{

			WebTarget target = client.target( "https://api.talkjs.com/v1/" + TALKJS_APP_ID + "/users" );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE )
					.header( "Authorization", "Bearer " + accessToken ).get();
			String data = response.readEntity( String.class );
			if (!isEmpty( data ))
				LOG.info( (new JSONObject( data )).toString( 2 ) );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			client.close();
		}
	}


	private static void joinConversation(String accessToken, String conversationId, String userId)
	{
		final Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build();
		try
		{
			JSONObject json = new JSONObject();
			json.put( "notify", "MentionsOnly" );
			json.put( "access", "ReadWrite" );

			WebTarget target = client.target( "https://api.talkjs.com/v1/" + TALKJS_APP_ID + "/conversations/" + conversationId
					+ "/participants/" + userId );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE )
					.header( "Authorization", "Bearer " + accessToken ).post( Entity.json( json.toString() ) );
			String data = response.readEntity( String.class );
			if (!isEmpty( data ))
				LOG.info( data );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			client.close();
		}
	}


	private static String generateJWT(String secretKey)
	{
		Algorithm algorithm = Algorithm.HMAC256( secretKey );
		String token = JWT.create().withClaim( "tokenType", "app" ).withIssuer( TALKJS_APP_ID )
				.withExpiresAt( new Date( System.currentTimeMillis() + 30 * 1000 ) ).sign( algorithm );
		System.out.println( token );
		return token;
	}


	private static void renewTalkJSAccessToken()
	{
		Algorithm algorithm = Algorithm.HMAC256( TALKJS_APP_SECRET );
		String token = JWT.create().withClaim( "tokenType", "app" ).withIssuer( TALKJS_APP_ID )
				.withExpiresAt( new Date( System.currentTimeMillis() + 60 * 1000 ) ).sign( algorithm );
		LOG.info( "New token: {}", token );
		TALKJS_ACS_TKN = token;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// String accessToken =
		// "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlblR5cGUiOiJhcHAiLCJpc3MiOiJ0eXZWQVdIaCIsImV4cCI6MTczODY0MzE0OH0.YvLA_0ohLEQoh8SVv5Q1N58uQ1hDkdF1kn5dpJLRGRE";
		// // if (isEmpty( accessToken ))
		// accessToken = generateJWT( args[0] );
		//
		// String filePath = "src/test/resources/zzz.txt";
		// File accessTokenFile = new File( filePath );
		// String[] zzz = new String[10];
		// int i = 0;
		// try
		// {
		// BufferedReader reader = new BufferedReader( new FileReader( accessTokenFile ) );
		// String line = null;
		// while ((line = reader.readLine()) != null)
		// zzz[i++] = line;
		// reader.close();
		// }
		// catch (IOException e)
		// {
		// e.printStackTrace();
		// }

		loadConfigration();

		renewTalkJSAccessToken();

		String accessToken = LocalCache.TALKJS_ACS_TKN;

		// getAllUsers( accessToken );

		// pushUser( accessToken, 12081, zzz[0] );
		// pushUser( accessToken, 8029, zzz[1] );
		// createConversation( accessToken, "order_391_fc", zzz[2] );
		// sendMessage( accessToken, "order_391_fc", "12081", "Messge from 12081" );
		sendMessage( accessToken, "order_391_fc", "8029", "Messge from 8029" );
		joinConversation( accessToken, "order_391_fc", "nina" );
		sendMessage( accessToken, "order_391_fc", "nina", "Messge from nina" );
	}


	private static void pushUser(String accessToken, int userId, String jsonStr)
	{
		final Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build();
		try
		{
			JSONObject json = new JSONObject( jsonStr );
			WebTarget target = client.target( "https://api.talkjs.com/v1/" + TALKJS_APP_ID + "/users/" + userId );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE )
					.header( "Authorization", "Bearer " + accessToken ).put( Entity.json( json.toString() ) );
			String data = response.readEntity( String.class );
			if (!isEmpty( data ))
				LOG.info( data );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			client.close();
		}
	}


	private static void sendMessage(String accessToken, String conversationId, String senderId, String content)
	{
		// {"errorCode":"BAD_REQUEST","reasons":{"1":"`sender` is not a part of participants list."}}
		// [{ "text": "Hello Alice!", "sender": "8029", "type": "UserMessage" }]

		try (Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build())
		{
			JSONArray json = new JSONArray();
			JSONObject msg = new JSONObject();
			json.put( msg );
			msg.put( "type", "UserMessage" );
			msg.put( "text", content );
			msg.put( "sender", senderId );

			WebTarget target = client
					.target( "https://api.talkjs.com/v1/" + TALKJS_APP_ID + "/conversations/" + conversationId + "/messages" );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE )
					.header( "Authorization", "Bearer " + accessToken ).post( Entity.json( json.toString() ) );
			String data = response.readEntity( String.class );
			if (!isEmpty( data ))
				LOG.info( data );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
