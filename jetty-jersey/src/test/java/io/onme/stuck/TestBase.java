/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2024 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import io.onme.stuck.restful.SpotResource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * @Ttron Feb 6, 2025 
 */
public class TestBase
{
	protected static final Logger LOG = LogManager.getLogger( SpotResource.class );

	protected static Properties loadConfigration()
	{
		Properties props = new Properties();
		File file = new File( "stuck.properties" );
		try
		{
			props.load( new FileInputStream( file ) );
		}
		catch (IOException e)
		{
			// LOG.error( "Error while read stuck.properties", e );
		}

		DataSource dataSource = new DataSource();
		PoolProperties p = new PoolProperties();
		p.setDriverClassName( "org.mariadb.jdbc.Driver" );
		p.setUrl( "jdbc:mariadb://" + props.getProperty( "db.nobites.host", "" ) + ":"
				+ props.getProperty( "db.nobites.port", "3306" ) + "/" + props.getProperty( "db.nobites", "test" ) );
		p.setUsername( props.getProperty( "db.nobites.user", "ttron" ) );
		p.setPassword( props.getProperty( "db.nobites.passwd", "" ) );
		// p.setJmxEnabled( false );
		p.setTestWhileIdle( false );
		p.setTestOnBorrow( true );
		p.setValidationQuery( "SELECT 1" );
		p.setTestOnReturn( false );
		p.setValidationInterval( 30000 );// The default value is 3000 (3 seconds).
		p.setTimeBetweenEvictionRunsMillis( 5000 );// The default value is 5000 (5 seconds).
		p.setMinEvictableIdleTimeMillis( 30000 );// The default value is 60000 (60 seconds).
		p.setMaxActive( 128 );
		p.setMaxIdle( 5 );
		p.setInitialSize( 1 );
		p.setMaxWait( 10000 );
		p.setMinIdle( 1 );
		p.setRemoveAbandonedTimeout( 60 );// The default value is 60 (60 seconds).
		p.setLogAbandoned( true );
		p.setRemoveAbandoned( true );
		p.setJdbcInterceptors( "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
				+ "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
				+ "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer" );
		LOG.debug( p.toString() );
		dataSource.setPoolProperties( p );

		LocalCache.DATASOURCE = dataSource;
		// APIBase.DATASOURCE = dataSource;

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal( 15 );
		JedisPool redisPool = new JedisPool( config, props.getProperty( "redis.host", "redis.cube" ),
				Integer.parseInt( props.getProperty( "redis.port", "6379" ) ), Protocol.DEFAULT_TIMEOUT,
				props.getProperty( "redis.passwd", "tooroot" ), 2 );// use db 2
		LocalCache.REDIS_POOL = redisPool;

		LocalCache.AUTH0_DOMAIN = props.getProperty( "AUTH0_DOMAIN", "a.b.c" );
		LocalCache.TALKJS_APP_ID = props.getProperty( "TALKJS_APP_ID", "a.b.c" );
		LocalCache.TALKJS_APP_SECRET = props.getProperty( "TALKJS_APP_SECRET", "a.b.c" );

		return props;
	}
}
