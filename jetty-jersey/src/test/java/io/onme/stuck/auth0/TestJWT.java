/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2024 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.auth0;

import static io.onme.stuck.LocalCache.AUTH0_DOMAIN;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAKey;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.onme.stuck.TestAccessTokenVendor;

/**
 * @Ttron Jan 24, 2025 
 */
public class TestJWT extends TestAccessTokenVendor
{
	private static final Logger LOG = LogManager.getLogger( TestJWT.class );

	public static void main(String[] args)
	{
		loadConfigration();

		verifyJWT();
	}


	private static void verifyJWT()
	{
		String accessToken = loadAccessToken();
		LOG.info( "Original JWT: {}", accessToken );

		JwkProvider jwkProvider = new JwkProviderBuilder( "https://" + AUTH0_DOMAIN + "/" )
				// cache up to 10 JWKs for up to 24 hours
				.cached( 10, 24, TimeUnit.HOURS ).build();
		LOG.info( "{}{}{}", "-".repeat( 32 ), "Decode", "-".repeat( 32 ) );
		DecodedJWT decodedJWT = JWT.decode( accessToken );
		String keyId = decodedJWT.getKeyId();
		LOG.info( "Algorithm: {}", decodedJWT.getAlgorithm() );
		LOG.info( "Key Id: {}", keyId );
		LOG.info( "Audienceudience: {}", decodedJWT.getAudience() );
		LOG.info( "Issuer: {}", decodedJWT.getIssuer() );
		LOG.info( "ExpiresAt: {}/{}", decodedJWT.getExpiresAtAsInstant().toEpochMilli(), decodedJWT.getExpiresAt() );
		String payload = decodedJWT.getPayload();
		LOG.info( "Payload as Base64: {}", payload );
		LOG.info( "{}{}{}", "-".repeat( 32 ), "Decode Payload", "-".repeat( 32 ) );
		JSONObject payloadJson = new JSONObject( new String( Base64.getUrlDecoder().decode( payload ), StandardCharsets.UTF_8 ) );
		LOG.info( "Payload as Json: {}", payloadJson );

		try
		{
			Algorithm algorithm = Algorithm.RSA256( (RSAKey) jwkProvider.get( keyId ).getPublicKey() );
			JWTVerifier verifier = JWT.require( algorithm )
					// specify any specific claim validations
					.withIssuer( "https://dev-msx36x4pag8s22w3.ca.auth0.com/" )
					// reusable verifier instance
					.build();
			DecodedJWT verifiedJWT = verifier.verify( accessToken );
		}
		catch (JWTVerificationException | IllegalArgumentException | JwkException e)
		{
			// Invalid signature/claims
			e.printStackTrace();
		}
	}
}
