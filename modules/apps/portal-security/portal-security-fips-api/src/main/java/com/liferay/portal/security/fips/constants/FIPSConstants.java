/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.constants;

import java.util.concurrent.TimeUnit;

/**
 * @author Manuele Castro
 */
public class FIPSConstants {

	public static final String FIPS_SESSION_MAXIMUM_AGE =
		"FIPS_SESSION_MAXIMUM_AGE";

	public static final String PASSWORD_POLICY_NAME_CRYPTO_OFFICER =
		"Crypto Officer Password Policy";

	public static final int SESSION_IDLE_TIMEOUT_MAX_MINUTES =
		(int)TimeUnit.HOURS.toMinutes(12);

	public static final int SESSION_MAXIMUM_AGE_MAX_MINUTES =
		(int)TimeUnit.DAYS.toMinutes(30);

	public static final String TIME_UNIT_DAYS = "days";

	public static final String TIME_UNIT_HOURS = "hours";

	public static final String TIME_UNIT_MINUTES = "minutes";

}