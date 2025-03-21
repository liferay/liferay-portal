/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.param.converter;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * @author Ivica Cardic
 */
public class DateParamConverter implements ParamConverter<Date> {

	@Override
	public Date fromString(String string) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			_getPattern(string));

		try {
			return simpleDateFormat.parse(string);
		}
		catch (ParseException parseException) {
			throw new WebApplicationException(parseException);
		}
	}

	@Override
	public String toString(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			_PATTERN_DATE_TIME);

		return simpleDateFormat.format(date);
	}

	private String _getPattern(String string) {
		if (string.contains("T")) {
			if (string.contains(".")) {
				return _PATTERN_DATE_TIME;
			}

			return _PATTERN_DATE_TIME_WITHOUT_MILLIS;
		}

		return _PATTERN_DATE;
	}

	private static final String _PATTERN_DATE = "yyyy-MM-dd";

	private static final String _PATTERN_DATE_TIME =
		"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private static final String _PATTERN_DATE_TIME_WITHOUT_MILLIS =
		"yyyy-MM-dd'T'HH:mm:ss'Z'";

}