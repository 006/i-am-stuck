/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2025 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.restful;

import static cn.ttron.metadata.GeoConstants.COLUMN_GEO_HASH;
import static cn.ttron.metadata.MetadataConstants.COLUMN_UNID;
import static cn.ttron.metadata.MetadataConstants.KEY_DESCRIPTION;
import static cn.ttron.util.Empty.isEmpty;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.hsr.geohash.GeoHash;
import cn.ttron.metadata.Result;
import cn.ttron.metadata.UNID;
import cn.ttron.protocol.util.DateHelper;
import io.onme.stuck.LocalCache;
import io.onme.stuck.model.StuckSpot;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

/**
 * @Ttron Jan 13, 2025 
 */
@Path("spot")
public class SpotResource extends StuckResource
{
	private static final Logger LOG = LogManager.getLogger( SpotResource.class );

	@POST
	@Produces({ APPLICATION_XML, APPLICATION_JSON })
	public Response createSpot(@FormParam("openid") String openId, @FormParam("lon") double lon, @FormParam("lat") double lat,
			@FormParam("vehicle_color") String vehicleColor, @FormParam("phone") String phone,
			@FormParam(KEY_DESCRIPTION) String description) throws URISyntaxException
	{
		LOG.info( "Create Stuck Spot by {} at ({},{})", openId, lon, lat );
		authScope( "spot" );
		String unid = UNID.getUnid();
		String geoHash = GeoHash.geoHashString( lon, lat );
		long spotId = 0;
		try (Connection connection = LocalCache.DATASOURCE.getConnection())
		{
			StringBuilder select = new StringBuilder();
			select.append( "insert into SK_SPOT(UNID,MAKER,OPEN_ID_STUCKER,LON,LAT,COLOR_VEHICLE" );
			select.append( ",GEO_HASH,CELLPHONE,DATIME_LAST,CONTENT) values(?,'jetty-jersey',?,?,?,?,?,?,?,?)" );
			PreparedStatement pstmt = connection.prepareStatement( select.toString(), Statement.RETURN_GENERATED_KEYS );
			pstmt.setString( 1, unid );
			pstmt.setString( 2, openId );
			pstmt.setDouble( 3, lon );
			pstmt.setDouble( 4, lat );
			pstmt.setString( 5, vehicleColor );
			pstmt.setString( 6, geoHash );
			pstmt.setString( 7, phone );
			pstmt.setString( 8, DateHelper.getDateYYYY_MM_DD() );
			pstmt.setString( 9, description );
			LOG.trace( "Insert SK_SPOT: {}", pstmt.toString() );
			pstmt.execute();

			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next())
				spotId = rs.getLong( 1 );
			rs.close();
		}
		catch (SQLException e)
		{
			LOG.error( "Insert SK_SPOT: {}", e.getLocalizedMessage() );
		}

		log( openId, (int) spotId, "Report stuck at: " + geoHash );

		return Response.ok().entity( new Result( "200", "Successful." ) ).build();
	}


	@GET
	@Produces({ APPLICATION_XML, APPLICATION_JSON })
	public Collection<StuckSpot> getAll(@QueryParam("west") double west, @QueryParam("east") double east,
			@QueryParam("north") double north, @QueryParam("south") double south, @QueryParam("limit") int limit)
	{
		LOG.info( "GET spots inside: ({}, {}, {}, {}), max count: {}", west, east, north, south, limit );

		// authScope( "spot" ); No authorization needed

		List<StuckSpot> spots = new LinkedList<>();
		try (Connection connection = LocalCache.DATASOURCE.getConnection())
		{
			StringBuilder select = new StringBuilder();
			select.append( "select * from SK_SPOT where FLAG_DEL=0" );
			select.append( " and (LON>? and LON<? and LAT>? and LAT<?)" );
			// saved within the past 6 hours
			select.append( " and (ID_STATE<>2 or TIMESTAMPDIFF(HOUR, DATIME_LAST, NOW())<6)" );
			select.append( " order by AIID limit 0,?" );
			PreparedStatement pstmt = connection.prepareStatement( select.toString() );
			pstmt.setDouble( 1, west );
			pstmt.setDouble( 2, east );
			pstmt.setDouble( 3, south );
			pstmt.setDouble( 4, north );
			pstmt.setInt( 5, limit == 0 ? 20 : limit );
			LOG.trace( "Query SK_SPOT: {}", pstmt.toString() );
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				StuckSpot spot = new StuckSpot();
				spots.add( spot );
				// spot.setAIID( rs.getInt( COLUMN_AIID ) );@_@ hide
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

		if (isEmpty( spots ))
			throw new NotFoundException();
		else
			LOG.info( "{} spots", spots.size() );
		return spots;
	}
}
