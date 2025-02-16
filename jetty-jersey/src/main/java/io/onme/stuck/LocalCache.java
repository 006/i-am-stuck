/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2025 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck;

import org.apache.tomcat.jdbc.pool.DataSource;

import com.auth0.jwt.algorithms.Algorithm;

import redis.clients.jedis.JedisPool;

/**
 * @Ttron Jan 13, 2025 
 */
public class LocalCache
{
	public static DataSource DATASOURCE;

	public static JedisPool REDIS_POOL;

	public static String AUTH0_DOMAIN;

	public static Algorithm JWA;

	/**
	 * TalkJS Access Token (JWT)
	 */
	@Deprecated
	public static String TALKJS_ACS_TKN;

	/**
	 * TalkJS App Id
	 */
	public static String TALKJS_APP_ID;

	public static String TALKJS_APP_SECRET;
}
