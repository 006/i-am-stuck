/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2025 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.model;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

/**
 * @Ttron Jan 13, 2025 
 */
@XmlRootElement(name = "spot")
@XmlType
public class StuckSpot extends LocatedEntry
{
	@XmlAttribute(name = KEY_ISO_3166)
	private String a3;

	@XmlAttribute(name = KEY_AIID)
	private int aiid;

	@XmlAttribute(name = "conversation_id")
	private String conversationId;

	@XmlAttribute(name = KEY_DESCRIPTION)
	private String description;

	@XmlAttribute(name = KEY_EPOCH_LAST)
	private long epochLast;

	@XmlAttribute(name = "openid_saver")
	private String openIdSaver;

	@XmlAttribute(name = "openid_stucker")
	private String openIdStucker;

	@XmlAttribute(name = "phone")
	private String phone;

	@XmlAttribute(name = "province")
	private String province;

	@XmlAttribute(name = "state")
	private int state;

	@XmlAttribute(name = KEY_UNID)
	private String unid;

	@XmlAttribute(name = "vehicle_color")
	private String vehicleColor;

	/**
	 * @return the ISO 3166 A3
	 */
	@XmlTransient
	public String getA3()
	{
		return a3;
	}


	/**
	 * @return the aiid
	 */
	@XmlTransient
	public int getAIID()
	{
		return aiid;
	}


	/**
	 * @return the conversationId
	 */
	@XmlTransient
	public String getConversationId()
	{
		return conversationId;
	}


	/**
	 * @return the description
	 */
	@XmlTransient
	public String getDescription()
	{
		return description;
	}


	/**
	 * @return the epochLast
	 */
	@XmlTransient
	public long getEpochLast()
	{
		return epochLast;
	}


	/**
	 * @return the openIdSaver
	 */
	@XmlTransient
	public String getOpenIdSaver()
	{
		return openIdSaver;
	}


	/**
	 * @return the openIdStucker
	 */
	@XmlTransient
	public String getOpenIdStucker()
	{
		return openIdStucker;
	}


	/**
	 * @return the phone
	 */
	@XmlTransient
	public String getPhone()
	{
		return phone;
	}


	/**
	 * @return the province
	 */
	@XmlTransient
	public String getProvince()
	{
		return province;
	}


	/**
	 * @return the state
	 */
	@XmlTransient
	public int getState()
	{
		return state;
	}


	/**
	 * @return the unid
	 */
	@XmlTransient
	public String getUnid()
	{
		return unid;
	}


	/**
	 * @return the vehicleColor
	 */
	@XmlTransient
	public String getVehicleColor()
	{
		return vehicleColor;
	}


	/**
	 * @param a3 the ISO 3166 A3 to set
	 */
	@XmlTransient
	public void setA3(String a3)
	{
		this.a3 = a3;
	}


	/**
	 * @param aiid the aiid to set
	 */
	public void setAIID(int aiid)
	{
		this.aiid = aiid;
	}


	/**
	 * @param conversationId the conversationId to set
	 */
	public void setConversationId(String conversationId)
	{
		this.conversationId = conversationId;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}


	/**
	 * @param epochLast the epochLast to set
	 */
	public void setEpochLast(long epochLast)
	{
		this.epochLast = epochLast;
	}


	/**
	 * @param openIdSaver the openIdSaver to set
	 */
	public void setOpenIdSaver(String openIdSaver)
	{
		this.openIdSaver = openIdSaver;
	}


	/**
	 * @param openIdStucker the openIdStucker to set
	 */
	public void setOpenIdStucker(String openIdStucker)
	{
		this.openIdStucker = openIdStucker;
	}


	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone)
	{
		this.phone = phone;
	}


	/**
	 * @param province the province to set
	 */
	public void setProvince(String province)
	{
		this.province = province;
	}


	/**
	 * @param state the state to set
	 */
	public void setState(int state)
	{
		this.state = state;
	}


	/**
	 * @param unid the unid to set
	 */
	public void setUnid(String unid)
	{
		this.unid = unid;
	}


	/**
	 * @param vehicleColor the vehicleColor to set
	 */
	public void setVehicleColor(String vehicleColor)
	{
		this.vehicleColor = vehicleColor;
	}
}
