/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2024 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.postgis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Ttron Jan 14, 2025 
 */
public class TestPostGIS
{
	private static final Logger LOG = LogManager.getLogger( TestPostGIS.class );

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String url = "jdbc:postgresql://localhost:5432/postgres";
		Properties props = new Properties();
		props.setProperty( "user", "postgres" );
		props.setProperty( "password", "mysecretpassword" );
		// props.setProperty( "ssl", "true" );
		try (Connection conn = DriverManager.getConnection( url, props ))
		{
			String sql = "select * from nobites.\"NB_SPOT\"";// has to be double quated
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			while (rs.next())
			{
				long aiid = rs.getLong( "AIID" );
				String unid = rs.getString( "UNID" );
				String name = rs.getString( "NAME" );
				LOG.info( "{}-{}-{}", aiid, unid, name );
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
