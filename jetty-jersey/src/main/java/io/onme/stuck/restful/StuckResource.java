/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2024 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.restful;

import static cn.ttron.metadata.GeoConstants.COLUMN_GEO_HASH;
import static cn.ttron.metadata.MetadataConstants.COLUMN_AIID;
import static cn.ttron.metadata.MetadataConstants.COLUMN_UNID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.onme.stuck.LocalCache;
import io.onme.stuck.model.StuckSpot;

/**
 * @Ttron Feb 12, 2025 
 */
public class StuckResource extends OAuth2Resource
{
	private static final Logger LOG = LogManager.getLogger( StuckResource.class );

	protected StuckSpot fetchSpot(String spotUnid)
	{
		StuckSpot spot = null;
		try (Connection connection = LocalCache.DATASOURCE.getConnection())
		{
			StringBuilder select = new StringBuilder();
			select.append( "select * from SK_SPOT where FLAG_DEL=0 and UNID=?" );
			PreparedStatement pstmt = connection.prepareStatement( select.toString() );
			pstmt.setString( 1, spotUnid );
			LOG.trace( "Query SK_SPOT: {}", pstmt.toString() );
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
			{
				spot = new StuckSpot();
				spot.setAIID( rs.getInt( COLUMN_AIID ) );
				spot.setUnid( rs.getString( COLUMN_UNID ) );
				spot.setOpenIdStucker( rs.getString( "OPEN_ID_STUCKER" ) );
				spot.setOpenIdSaver( rs.getString( "OPEN_ID_SAVER" ) );
				spot.setState( rs.getInt( "ID_STATE" ) );
				spot.setLongitude( rs.getDouble( "LON" ) );
				spot.setLatitude( rs.getDouble( "LAT" ) );
				spot.setA3( rs.getString( "ISO_3166_A3" ) );
				spot.setProvince( rs.getString( "PRVC" ) );
				spot.setVehicleColor( rs.getString( "COLOR_VEHICLE" ) );
				spot.setGeohash( rs.getString( COLUMN_GEO_HASH ) );
				spot.setPhone( rs.getString( "CELLPHONE" ) );
				spot.setDescription( rs.getString( "CONTENT" ) );
				spot.setConversationId( rs.getString( "ID_CVSN" ) );
				Timestamp last = rs.getTimestamp( "DATIME_LAST" );
				if (last != null)
					spot.setEpochLast( last.getTime() );
			}
		}
		catch (SQLException e)
		{
			LOG.error( "Query SK_SPOT: {}", e.getLocalizedMessage() );
		}
		return spot;
	}


	protected void log(String openId, int spotId, String content)
	{
		try (Connection connection = LocalCache.DATASOURCE.getConnection())
		{
			StringBuilder select = new StringBuilder();
			select.append( "insert into SK_LOG_SPOT(OPEN_ID,ID_SPOT" );
			select.append( ",CONTENT) values(?,?,?)" );
			PreparedStatement pstmt = connection.prepareStatement( select.toString() );
			pstmt.setString( 1, openId );
			pstmt.setInt( 2, spotId );
			pstmt.setString( 3, content );
			LOG.trace( "Insert SK_LOG_SPOT: {}", pstmt.toString() );
			pstmt.execute();
		}
		catch (SQLException e)
		{
			LOG.error( "Insert SK_LOG_SPOT: {}", e.getLocalizedMessage() );
		}
	}
}
