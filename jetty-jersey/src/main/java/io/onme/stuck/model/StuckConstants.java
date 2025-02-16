/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2024 Ttron Kidman. All rights reserved.
 */
package io.onme.stuck.model;

import cn.ttron.metadata.MetadataConstants;

/**
 * @Ttron Jan 14, 2025 
 */
public interface StuckConstants extends MetadataConstants
{

	public static final String KEY_COUNT_BAD = "count_n_1";

	public static final String KEY_COUNT_EXAUHST = "count_n_2";

	public static final String KEY_COUNT_GOOD = "count_p_1";

	public static final String KEY_COUNT_GREAT = "count_p_2";

	public static final String KEY_COUNT_NEUTRAL = "count_z";

	public static final String KEY_LATITUDE_SPOT = "lat_spot";

	public static final String KEY_LATITUDE_USER = "lat_user";

	public static final String KEY_LONGITUDE_SPOT = "lon_spot";

	public static final String KEY_LONGITUDE_USER = "lon_user";

	public static final String KEY_BITES_RANK = "bites_rank";

	public static final String KEY_BITES_PER_HOUR = "b_p_h";

	public static final int STATE_STUCK = 0;

	public static final int STATE_IN_TALK = 1;

	public static final int STATE_SAVED = 2;
}
