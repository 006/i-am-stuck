/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2025 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.martix;

import static cn.ttron.util.Empty.isEmpty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.json.JSONObject;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @Ttron Feb 5, 2025 
 */
public class TestSynapse
{
	private static final Logger LOG = LogManager.getLogger( TestSynapse.class );

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String accessToken = args[0];
		getUser( accessToken );
	}


	/**
	 * @param accessToken
	 */
	private static void getUser(String accessToken)
	{
		final Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build();
		try
		{
			WebTarget target = client.target( "http://192.168.6.221:18008/_synapse/admin/v2/users/@ttron:synase.tsst.xyz" );
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
}
