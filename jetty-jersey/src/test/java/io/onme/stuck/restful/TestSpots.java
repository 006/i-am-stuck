/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2024 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.restful;

import static cn.ttron.util.Empty.isEmpty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;

import io.onme.stuck.TestAccessTokenVendor;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @Ttron Jan 14, 2025 
 */
public class TestSpots extends TestAccessTokenVendor
{
	private static final Logger LOG = LogManager.getLogger( TestSpots.class );

	public static void main(String[] args)
	{
		getSpots();
		getUpdateSpot();
	}


	/**
	 * 
	 */
	private static void getUpdateSpot()
	{
		// TODO Auto-generated method stub
	}


	private static void getSpots()
	{
		// JSONObject json = new JSONObject();
		// json.put( "mobiles", sim.getCellphone() );
		// json.put( "context", content );
		final Client client = ClientBuilder.newBuilder().register( MoxyJsonFeature.class ).build();
		try
		{
			WebTarget target = client.target( "http://localhost:8080/spot" );
			Response response = target.request( MediaType.APPLICATION_JSON_TYPE )
					.header( "Authorization", "Bearer " + loadAccessToken() ).get();
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
}
