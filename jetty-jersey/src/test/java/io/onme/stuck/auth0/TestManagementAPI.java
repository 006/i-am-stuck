/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2024 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.auth0;

import static cn.ttron.util.Empty.isEmpty;
import static io.onme.stuck.LocalCache.AUTH0_DOMAIN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.json.JSONArray;
import org.json.JSONObject;

import io.onme.stuck.TestAccessTokenVendor;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @Ttron Feb 6, 2025 
 */
public class TestManagementAPI extends TestAccessTokenVendor
{
	private static final Logger LOG = LogManager.getLogger( TestManagementAPI.class );

	/**
	 * @param args
	 * @param filePath
	 */
	private static void askAccessToken(String clientId, String clientSecret, String filePath)
	{
		final Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build();
		try
		{
			JSONObject json = new JSONObject();
			json.put( "client_id", clientId );
			json.put( "client_secret", clientSecret );
			json.put( "grant_type", "client_credentials" );
			json.put( "audience", "https://" + AUTH0_DOMAIN + "/api/v2/" );

			WebTarget target = client.target( "https://" + AUTH0_DOMAIN + "/oauth/token" );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE )
					// .header( "Authorization", "Bearer this is your OAuth2 token" )
					.post( Entity.entity( json.toString(), MediaType.APPLICATION_JSON ) );
			String data = response.readEntity( String.class );
			if (!isEmpty( data ))
			{
				LOG.info( data );
				JSONObject resp = new JSONObject( data );
				String token = resp.getString( "access_token" );
				if (!isEmpty( token ))
					try
					{
						BufferedWriter writer = new BufferedWriter(
								new OutputStreamWriter( new FileOutputStream( filePath ), "UTF-8" ) );
						writer.write( token );
						writer.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
			}
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
	private static void getSigningKey(String accessToken)
	{
		final Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build();
		try
		{
			// WebTarget target = client.target(
			// "https://"+AUTH0_DOMAIN+"/api/v2/keys/signing" );
			WebTarget target = client.target( "https://" + AUTH0_DOMAIN + "/api/v2/keys/signing/6Zlqx5vWMKYt82hCIONNC" );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE )
					.header( "Authorization", "Bearer " + accessToken ).get();
			String data = response.readEntity( String.class );
			LOG.info( data );
			if (!isEmpty( data ))
			{
				data = data.replaceAll( "\\\\r\\\\n", System.lineSeparator() );
				data = data.replaceAll( "cert\":\"", "cert\":\"" + System.lineSeparator() );
				data = data.replaceAll( "pkcs7\":\"", "pkcs7\":\"" + System.lineSeparator() );
				String finalName = "src/test/resources/keys.txt";
				try
				{
					BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( finalName ) ) );
					writer.write( data );
					writer.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
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
	private static void getUsers(String accessToken)
	{
		final Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build();
		try
		{
			// JSONObject json = new JSONObject( jsonStr );
			WebTarget target = client.target( "https://" + AUTH0_DOMAIN + "/api/v2/users" );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE )
					.header( "Authorization", "Bearer " + accessToken ).get();
			String data = response.readEntity( String.class );
			if (!isEmpty( data ))
				LOG.info( (new JSONArray( data )).toString( 2 ) );
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


	public static void main(String[] args)
	{
		Properties props = loadConfigration();

		String filePath = "src/test/resources/m_access_token.txt";
		File accessTokenFile = new File( filePath );
		if (!accessTokenFile.exists())
			askAccessToken( props.getProperty( "AUTH0_CLIENT_ID" ), props.getProperty( "AUTH0_CLIENT_SECRET" ), filePath );
		String accessToken = "";
		try
		{
			BufferedReader reader = new BufferedReader( new FileReader( accessTokenFile ) );
			String line = null;
			while ((line = reader.readLine()) != null)
				accessToken = line;
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (!isEmpty( accessToken ))
			getUsers( accessToken );
	}
}
