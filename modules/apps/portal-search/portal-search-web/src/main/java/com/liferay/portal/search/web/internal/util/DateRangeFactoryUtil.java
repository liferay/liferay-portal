/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Adam Brandizzi
 * @author André de Oliveira
 */
public class DateRangeFactoryUtil {

	public static JSONArray getDefaultRangesJSONArray(Calendar calendar) {
		JSONArray rangesJSONArray = JSONFactoryUtil.createJSONArray();

		Map<String, String> map = getRangeStrings(calendar);

		map.forEach(
			(key, value) -> rangesJSONArray.put(
				JSONUtil.put(
					"label", key
				).put(
					"range", value
				)));

		return rangesJSONArray;
	}

	public static String getRangeString(String label, Calendar calendar) {
		return replaceAliases(_rangeMap.get(label), calendar);
	}

	public static String getRangeString(
		String from, String to, TimeZone timeZone) {

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss", LocaleUtil.US, TimeZoneUtil.GMT);
		DateFormat userTimeZoneDateFormat =
			DateFormatFactoryUtil.getSimpleDateFormat(
				"yyyyMMddHHmmss", LocaleUtil.US, timeZone);

		String normalizedFrom = _normalizeRangeBoundary(from, "000000");
		String normalizedTo = _normalizeRangeBoundary(to, "235959");

		try {
			String fromUTC = dateFormat.format(
				userTimeZoneDateFormat.parse(normalizedFrom));
			String toUTC = dateFormat.format(
				userTimeZoneDateFormat.parse(normalizedTo));

			return StringBundler.concat("[", fromUTC, " TO ", toUTC, "]");
		}
		catch (ParseException parseException) {
			throw new RuntimeException(parseException);
		}
	}

	public static Map<String, String> getRangeStrings(Calendar calendar) {
		Map<String, String> map = new LinkedHashMap<>();

		for (String label : _rangeMap.keySet()) {
			map.put(label, getRangeString(label, calendar));
		}

		return map;
	}

	public static JSONArray replaceAliases(
		JSONArray rangesJSONArray, Calendar calendar) {

		JSONArray normalizedRangesJSONArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < rangesJSONArray.length(); i++) {
			JSONObject rangeJSONObject = rangesJSONArray.getJSONObject(i);

			normalizedRangesJSONArray.put(
				JSONUtil.put(
					"label", rangeJSONObject.getString("label")
				).put(
					"range",
					replaceAliases(rangeJSONObject.getString("range"), calendar)
				));
		}

		return normalizedRangesJSONArray;
	}

	public static String replaceAliases(String rangeString, Calendar calendar) {
		if (Validator.isNull(rangeString)) {
			return StringPool.BLANK;
		}

		Calendar now = (Calendar)calendar.clone();

		Calendar pastHour = (Calendar)now.clone();

		pastHour.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY) - 1);

		Calendar past24Hours = (Calendar)now.clone();

		past24Hours.set(
			Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 1);

		Calendar pastWeek = (Calendar)now.clone();

		pastWeek.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 7);

		Calendar pastMonth = (Calendar)now.clone();

		pastMonth.set(Calendar.MONTH, now.get(Calendar.MONTH) - 1);

		Calendar pastYear = (Calendar)now.clone();

		pastYear.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss", LocaleUtil.US);

		return StringUtil.replace(
			rangeString, _ALIASES,
			new String[] {
				dateFormat.format(pastHour.getTime()),
				dateFormat.format(past24Hours.getTime()),
				dateFormat.format(pastWeek.getTime()),
				dateFormat.format(pastMonth.getTime()),
				dateFormat.format(pastYear.getTime()),
				dateFormat.format(now.getTime())
			});
	}

	public static void validateRanges(String ranges)
		throws JSONException, ParseException {

		JSONArray rangesJSONArray = JSONFactoryUtil.createJSONArray(ranges);

		for (int i = 0; i < rangesJSONArray.length(); i++) {
			String range = rangesJSONArray.getJSONObject(
				i
			).getString(
				"range"
			);

			if (!range.contains(" TO ") || !StringUtil.startsWith(range, "[") ||
				!StringUtil.endsWith(range, "]")) {

				throw new IllegalArgumentException(
					"Invalid range syntax " + range);
			}

			String from = range.split("TO")[0].trim();

			from = from.substring(1);

			_validateDateFormat(from);

			String to = range.split("TO")[1].trim();

			to = to.substring(0, to.length() - 1);

			_validateDateFormat(to);
		}
	}

	private static String _normalizeRangeBoundary(
		String dateString, String pad) {

		dateString = StringUtil.replace(dateString, '-', "");

		return dateString + pad;
	}

	private static void _validateDateFormat(String date) throws ParseException {
		if (!ArrayUtil.contains(_ALIASES, date) && !date.contains("now")) {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				"yyyyMMddHHmmss");

			dateFormat.parse(date);
		}
	}

	private static final String[] _ALIASES = {
		"past-hour", "past-24-hours", "past-week", "past-month", "past-year",
		StringPool.STAR
	};

	private static final Map<String, String> _rangeMap =
		LinkedHashMapBuilder.put(
			"past-hour", "[past-hour TO *]"
		).put(
			"past-24-hours", "[past-24-hours TO *]"
		).put(
			"past-week", "[past-week TO *]"
		).put(
			"past-month", "[past-month TO *]"
		).put(
			"past-year", "[past-year TO *]"
		).build();

}