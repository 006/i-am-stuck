package io.onme.stuck;

/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2025 Ttron Kidman. All rights reserved.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @Ttron Feb 6, 2025 
 */
public class TestAccessTokenVendor extends TestBase
{
	/**
	 * use the last line of this file
	 * @return
	 */
	protected static String loadAccessToken()
	{
		String filePath = "src/test/resources/c_access_token.txt";
		File accessTokenFile = new File( filePath );
		String accessToken = "";
		try
		{
			BufferedReader reader = new BufferedReader( new FileReader( accessTokenFile ) );
			String line = null;
			while ((line = reader.readLine()) != null)
				accessToken = line;
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return accessToken;
	}
}
