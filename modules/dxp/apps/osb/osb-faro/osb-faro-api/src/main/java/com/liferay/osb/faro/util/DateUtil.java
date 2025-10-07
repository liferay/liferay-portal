/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * @author Marcos Martins
 */
public class DateUtil {

	public static final String PATTERN_DATE = "yyyy-MM-dd";

	public static final String PATTERN_DATE_TIME =
		"yyyy-MM-dd['T']HH:mm[:ss][.SSS]['Z']";

	public static String formatDate(Date date, String pattern) {
		DateFormat dateFormat = new SimpleDateFormat(pattern);

		return dateFormat.format(date);
	}

	public static Date parseDate(String dateString, String pattern)
		throws Exception {

		DateFormat dateFormat = new SimpleDateFormat(pattern);

		return dateFormat.parse(dateString);
	}

}