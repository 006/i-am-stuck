/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2025 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.restful;

import static cn.ttron.util.Empty.isEmpty;

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

import io.onme.stuck.LocalCache;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

/**
 * @Ttron Feb 6, 2025 
 */
public class OAuth2Resource
{
	private static final Logger LOG = LogManager.getLogger( OAuth2Resource.class );

	@Context
	protected HttpHeaders httpHeaders;

	@Context
	protected HttpServletResponse response;

	protected boolean authScope(String scope)
	{
		String authorizationHeader = httpHeaders.getHeaderString( "Authorization" );
		// LOG.debug( "Authorization: {}", authorizationHeader );
		if (isEmpty( authorizationHeader ))
		{
			LOG.error( "No Authorization Header" );
			throw new NotAuthorizedException( response );
		}
		String accessToken = authorizationHeader.replace( "Bearer ", "" );
		return verifyJWT( accessToken, scope );
	}


	private boolean verifyJWT(String accessToken, String scope)
	{
		String auth0URL = "https://" + LocalCache.AUTH0_DOMAIN + "/";
		DecodedJWT decodedJWT = JWT.decode( accessToken );
		String keyId = decodedJWT.getKeyId();
		String payload = decodedJWT.getPayload();
		// LOG.info( "Payload as Json: {}", payloadJson );
		try
		{
			Algorithm algorithm = LocalCache.JWA;
			if (algorithm == null)
			{
				JwkProvider jwkProvider = new JwkProviderBuilder( auth0URL ).cached( 10, 24, TimeUnit.HOURS ).build();
				algorithm = Algorithm.RSA256( (RSAKey) jwkProvider.get( keyId ).getPublicKey() );
				LocalCache.JWA = algorithm;// FIXME replace with new instance every day
			}
			JWTVerifier verifier = JWT.require( algorithm ).withIssuer( auth0URL ).build();
			DecodedJWT verifiedJWT = verifier.verify( accessToken );
		}
		catch (JWTVerificationException | IllegalArgumentException | JwkException e)
		{
			// Invalid signature/claims
			// e.printStackTrace();
			LOG.error( "Invalid JWT: {}", e.getLocalizedMessage() );
			throw new NotAuthorizedException( response );
		}

		JSONObject payloadJson = new JSONObject( new String( Base64.getUrlDecoder().decode( payload ), StandardCharsets.UTF_8 ) );
		String scopeGranted = payloadJson.getString( "scope" );
		if (isEmpty( scopeGranted ))
			return false;
		else
			return scopeGranted.contains( scope );
	}
}
