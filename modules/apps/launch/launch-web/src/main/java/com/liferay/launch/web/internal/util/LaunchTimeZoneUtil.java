/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneComparator;
import com.liferay.portal.kernel.util.TimeZoneUtil;

import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * @author Jorge González
 */
public class LaunchTimeZoneUtil {

	public static JSONArray getTimeZonesJSONArray(Locale locale) {
		Set<TimeZone> timeZones = new TreeSet<>(new TimeZoneComparator());

		for (String timeZoneId : PropsUtil.getArray(PropsKeys.TIME_ZONES)) {
			timeZones.add(TimeZoneUtil.getTimeZone(timeZoneId));
		}

		return JSONUtil.toJSONArray(
			timeZones,
			timeZone -> JSONUtil.put(
				"label", _getTimeZoneLabel(locale, timeZone)
			).put(
				"value", timeZone.getID()
			),
			_log);
	}

	private static String _getTimeZoneLabel(Locale locale, TimeZone timeZone) {
		StringBundler sb = new StringBundler(7);

		sb.append("(UTC");

		Date date = new Date();

		int offset = timeZone.getOffset(date.getTime());

		if (offset != 0) {
			sb.append(
				String.format(
					" %+03d:%02d", offset / Time.HOUR,
					Math.abs(offset % Time.HOUR) / Time.MINUTE));
		}

		sb.append(") ");
		sb.append(
			timeZone.getDisplayName(
				timeZone.inDaylightTime(date), TimeZone.LONG, locale));

		String timeZoneId = timeZone.getID();

		if (timeZoneId.equals("America/Phoenix")) {
			sb.append(" (");
			sb.append(timeZoneId);
			sb.append(")");
		}

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchTimeZoneUtil.class);

}