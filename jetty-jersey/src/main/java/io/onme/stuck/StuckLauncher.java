/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2025 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;

import jakarta.servlet.DispatcherType;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * @Ttron Jan 13, 2025 
 */
public class StuckLauncher
{
	public static final String KEY_BUNDLE_CONTEXT = "osgi-bundle-context";

	public static final String KEY_BUNDLE_DATASOURCE = "osgi-bundle-datasource";

	public static final String KEY_BUNDLE_REDIS = "osgi-bundle-redis";

	public static final String KEY_WS_APPLICATION = "jakarta.ws.rs.Application";

	final static Logger LOG = LogManager.getLogger( StuckLauncher.class );

	/**
	 * @param context
	 * @return
	 */
	private static Server getServer(ServletContextHandler context)
	{
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setName( "server" );

		// Create a Server instance.
		Server server = new Server( threadPool );

		// Create a ServerConnector to accept connections from clients.
		ServerConnector connector = new ServerConnector( server );

		// The port to listen to.
		connector.setPort( 8080 );
		// The address to bind to.
		connector.setHost( "0.0.0.0" );

		// The TCP accept queue size.
		connector.setAcceptQueueSize( 128 );

		// Add the Connector to the Server
		server.addConnector( connector );

		// Create the server level handler list.
		HandlerList handlers = new HandlerList();
		// Make sure DefaultHandler is last (for error handling reasons)
		handlers.setHandlers( new Handler[] { context, new DefaultHandler() } );
		server.setHandler( handlers );
		// server.setHandler( context );
		return server;
	}


	/**
	 * @return
	 */
	private static Properties loadConfiguration()
	{
		Properties props = new Properties();
		File file = new File( "stuck.properties" );
		try
		{
			props.load( new FileInputStream( file ) );
		}
		catch (IOException e)
		{
			LOG.error( "Error while read stuck.properties", e );
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
				props.getProperty( "redis.passwd", "passwd" ), 1 );// use db 1
		LocalCache.REDIS_POOL = redisPool;

		LocalCache.AUTH0_DOMAIN = props.getProperty( "AUTH0_DOMAIN", "a.b.c" );
		LocalCache.TALKJS_APP_ID = props.getProperty( "TALKJS_APP_ID", "a.b.c" );
		LocalCache.TALKJS_APP_SECRET = props.getProperty( "TALKJS_APP_SECRET", "a.b.c" );

		return props;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Properties props = loadConfiguration();

		ServletContextHandler context = new ServletContextHandler( ServletContextHandler.NO_SESSIONS );
		context.setContextPath( "/" );
		ServletHolder servletHolder = context.addServlet( ServletContainer.class, "/*" );
		// servletHolder.setInitParameter( ServerProperties.TRACING, "ALL" );
		// servletHolder.setInitParameter( ServerProperties.TRACING_THRESHOLD, "TRACE" );
		servletHolder.setInitParameter( KEY_WS_APPLICATION, RestfulApplication.class.getName() );

		// jerseyHandler.getServletContext().setAttribute( KEY_BUNDLE_CONTEXT, context );
		context.getServletContext().setAttribute( KEY_BUNDLE_DATASOURCE, LocalCache.DATASOURCE );
		context.getServletContext().setAttribute( KEY_BUNDLE_REDIS, LocalCache.REDIS_POOL );

		// Add the filter, and then use the provided FilterHolder to configure it
		FilterHolder cors = context.addFilter( CrossOriginFilter.class, "/*", EnumSet.of( DispatcherType.REQUEST ) );
		cors.setInitParameter( CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*" );
		cors.setInitParameter( CrossOriginFilter.ACCESS_CONTROL_ALLOW_HEADERS_HEADER, "*" );
		cors.setInitParameter( CrossOriginFilter.ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true" );
		cors.setInitParameter( CrossOriginFilter.ACCESS_CONTROL_ALLOW_METHODS_HEADER, "GET,POST,PUT,DELETE,OPTIONS,HEAD" );
		cors.setInitParameter( CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,PUT,DELETE,OPTIONS,HEAD" );
		cors.setInitParameter( CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*" );
		cors.setInitParameter( CrossOriginFilter.ALLOWED_HEADERS_PARAM, "*" );
		// cors.setInitParameter( CrossOriginFilter.ALLOWED_HEADERS_PARAM,
		// "X-Requested-With,Content-Type,Accept,Origin,Authorization" );

		// Create and configure a ThreadPool.
		Server server = getServer( context );

		// Start the Server so it starts accepting connections from clients.
		try
		{
			server.start();
			server.join();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			server.destroy();
		}
	}
}
