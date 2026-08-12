/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Eudaldo Alonso
 */
public class DateRangeUtil {

	public static Date getEndDate(String rangeEnd) {
		try {
			Calendar calendar = Calendar.getInstance();

			DateFormat dateFormat = _getDateFormat();

			calendar.setTime(dateFormat.parse(rangeEnd));

			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MILLISECOND, 59);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);

			return calendar.getTime();
		}
		catch (ParseException parseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(parseException);
			}
		}

		return null;
	}

	public static Date getPreviousStartDate(
		String rangeEnd, Integer rangeKey, String rangeStart) {

		Calendar calendar = Calendar.getInstance();

		if (Validator.isNotNull(rangeEnd) && Validator.isNotNull(rangeStart)) {
			try {
				calendar.setTime(getStartDate(rangeEnd, null, rangeStart));

				DateFormat dateFormat = _getDateFormat();

				int delta = DateUtil.getDaysBetween(
					dateFormat.parse(rangeStart), dateFormat.parse(rangeEnd));

				calendar.add(Calendar.DAY_OF_MONTH, -delta);
			}
			catch (ParseException parseException) {
				if (_log.isDebugEnabled()) {
					_log.debug(parseException);
				}
			}
		}
		else if (rangeKey != null) {
			calendar.add(Calendar.DAY_OF_MONTH, -(rangeKey * 2));
		}
		else {
			return null;
		}

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
	}

	public static Date getStartDate(
		String rangeEnd, Integer rangeKey, String rangeStart) {

		Calendar calendar = Calendar.getInstance();

		if (Validator.isNotNull(rangeEnd) && Validator.isNotNull(rangeStart)) {
			try {
				DateFormat dateFormat = _getDateFormat();

				calendar.setTime(dateFormat.parse(rangeStart));
			}
			catch (ParseException parseException) {
				if (_log.isDebugEnabled()) {
					_log.debug(parseException);
				}
			}
		}
		else if (rangeKey != null) {
			calendar.add(Calendar.DAY_OF_MONTH, -rangeKey);
		}
		else {
			return null;
		}

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
	}

	private static DateFormat _getDateFormat() {
		return DateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd");
	}

	private static final Log _log = LogFactoryUtil.getLog(DateRangeUtil.class);

}