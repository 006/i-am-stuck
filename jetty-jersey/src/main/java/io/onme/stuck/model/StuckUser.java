/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2024 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.model;

/**
 * @Ttron Feb 11, 2025 
 */
public class StuckUser
{
	private String alias;

	private String avatar;

	private String openId;

	private String role;

	/**
	 * @return the alias
	 */
	public String getAlias()
	{
		return alias;
	}


	/**
	 * @return the avatar
	 */
	public String getAvatar()
	{
		return avatar;
	}


	/**
	 * @return the openId
	 */
	public String getOpenId()
	{
		return openId;
	}


	/**
	 * @return the role
	 */
	public String getRole()
	{
		return role;
	}


	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias)
	{
		this.alias = alias;
	}


	/**
	 * @param avatar the avatar to set
	 */
	public void setAvatar(String avatar)
	{
		this.avatar = avatar;
	}


	/**
	 * @param openId the openId to set
	 */
	public void setOpenId(String openId)
	{
		this.openId = openId;
	}


	/**
	 * @param role the role to set
	 */
	public void setRole(String role)
	{
		this.role = role;
	}
}
