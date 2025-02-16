/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2024 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.model;

import static cn.ttron.metadata.GeoConstants.KEY_ALTITUDE;
import static cn.ttron.metadata.GeoConstants.KEY_GEO_HASH;
import static cn.ttron.metadata.GeoConstants.KEY_LATITUDE;
import static cn.ttron.metadata.GeoConstants.KEY_LONGITUDE;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * @Ttron Jan 14, 2025 
 */
public class LocatedEntry implements StuckConstants
{
	private static final long serialVersionUID = -7423033847507115511L;

	@XmlAttribute(name = KEY_ALTITUDE)
	private double altitude;

	@XmlAttribute(name = KEY_GEO_HASH)
	private String geohash;

	@XmlAttribute(name = KEY_LATITUDE)
	// @Column(name = COLUMN_LATITUDE, type = DataType.DOUBLE)
	private double latitude;

	@XmlAttribute(name = KEY_LONGITUDE)
	private double longitude;

	/**
	 * @return the altitude
	 */
	@XmlTransient
	public double getAltitude()
	{
		return altitude;
	}


	/**
	 * @return the geohash
	 */
	@XmlTransient
	public String getGeohash()
	{
		return geohash;
	}


	/**
	 * @return the latitude
	 */
	@XmlTransient
	public double getLatitude()
	{
		return latitude;
	}


	/**
	 * @return the longitude
	 */
	@XmlTransient
	public double getLongitude()
	{
		return longitude;
	}


	/**
	 * @param altitude the altitude to set
	 */
	public void setAltitude(double altitude)
	{
		this.altitude = altitude;
	}


	/**
	 * @param geohash the geohash to set
	 */
	public void setGeohash(String geohash)
	{
		this.geohash = geohash;
	}


	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}


	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

}
