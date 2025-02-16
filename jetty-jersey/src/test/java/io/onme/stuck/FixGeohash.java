/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2025 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck;

import static cn.ttron.metadata.MetadataConstants.COLUMN_AIID;
import static cn.ttron.metadata.MetadataConstants.COLUMN_UNID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import ch.hsr.geohash.GeoHash;
import io.onme.stuck.model.StuckSpot;

/**
 * @Ttron Feb 15, 2025 
 */
public class FixGeohash extends TestBase
{
	public static void main(String[] args)
	{
		Properties props = loadConfigration();

		List<StuckSpot> spots = new LinkedList<>();

		try (Connection connection = LocalCache.DATASOURCE.getConnection())
		{
			StringBuilder select = new StringBuilder();
			select.append( "select * from SK_SPOT where FLAG_DEL=0" );
			select.append( " order by AIID" );
			PreparedStatement pstmt = connection.prepareStatement( select.toString() );
			LOG.trace( "Query SK_SPOT: {}", pstmt.toString() );
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				StuckSpot spot = new StuckSpot();
				spots.add( spot );
				spot.setAIID( rs.getInt( COLUMN_AIID ) );
				spot.setUnid( rs.getString( COLUMN_UNID ) );
				spot.setLongitude( rs.getDouble( "LON" ) );
				spot.setLatitude( rs.getDouble( "LAT" ) );
			}
		}
		catch (SQLException e)
		{
			LOG.error( "Query SK_SPOT: {}", e.getLocalizedMessage() );
		}

		for ( StuckSpot spot : spots )
			spot.setGeohash( GeoHash.geoHashString( spot.getLongitude(), spot.getLatitude() ) );

		try (Connection connection = LocalCache.DATASOURCE.getConnection())
		{
			StringBuilder select = new StringBuilder();
			select.append( "update SK_SPOT set GEO_HASH=? where AIID=?" );
			PreparedStatement pstmt = connection.prepareStatement( select.toString() );
			for ( StuckSpot spot : spots )
			{
				pstmt.setString( 1, spot.getGeohash() );
				pstmt.setLong( 2, spot.getAIID() );
				LOG.trace( "Update SK_SPOT: {}", pstmt.toString() );
				pstmt.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			LOG.error( "Update SK_SPOT: {}", e.getLocalizedMessage() );
		}
	}
}
