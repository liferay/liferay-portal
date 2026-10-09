/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FastDateFormatConstants;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.text.Format;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.time.FastDateFormat;

/**
 * @author Brian Wing Shun Chan
 */
public class FastDateFormatFactoryImpl implements FastDateFormatFactory {

	@Override
	public Format getDate(int style, Locale locale, TimeZone timeZone) {
		DateOrTimeCacheKey dateOrTimeCacheKey = new DateOrTimeCacheKey(
			style, locale, timeZone);

		Format format = _dateFormats.get(dateOrTimeCacheKey);

		if (format == null) {
			format = FastDateFormat.getDateInstance(style, timeZone, locale);

			_dateFormats.put(dateOrTimeCacheKey, format);
		}

		return format;
	}

	@Override
	public Format getDate(Locale locale) {
		return getDate(locale, null);
	}

	@Override
	public Format getDate(Locale locale, TimeZone timeZone) {
		return getDate(FastDateFormatConstants.SHORT, locale, timeZone);
	}

	@Override
	public Format getDate(TimeZone timeZone) {
		return getDate(LocaleUtil.getDefault(), timeZone);
	}

	@Override
	public Format getDateTime(
		int dateStyle, int timeStyle, Locale locale, TimeZone timeZone) {

		DateAndTimeCacheKey dateAndTimeCacheKey = new DateAndTimeCacheKey(
			dateStyle, timeStyle, locale, timeZone);

		Format format = _dateTimeFormats.get(dateAndTimeCacheKey);

		if (format == null) {
			format = FastDateFormat.getDateTimeInstance(
				dateStyle, timeStyle, timeZone, locale);

			_dateTimeFormats.put(dateAndTimeCacheKey, format);
		}

		return format;
	}

	@Override
	public Format getDateTime(Locale locale) {
		return getDateTime(locale, null);
	}

	@Override
	public Format getDateTime(Locale locale, TimeZone timeZone) {
		return getDateTime(
			FastDateFormatConstants.SHORT, FastDateFormatConstants.SHORT,
			locale, timeZone);
	}

	@Override
	public Format getDateTime(TimeZone timeZone) {
		return getDateTime(LocaleUtil.getDefault(), timeZone);
	}

	@Override
	public Format getSimpleDateFormat(String pattern) {
		return getSimpleDateFormat(pattern, LocaleUtil.getDefault(), null);
	}

	@Override
	public Format getSimpleDateFormat(String pattern, Locale locale) {
		return getSimpleDateFormat(pattern, locale, null);
	}

	@Override
	public Format getSimpleDateFormat(
		String pattern, Locale locale, TimeZone timeZone) {

		SimpleDateCacheKey simpleDateCacheKey = new SimpleDateCacheKey(
			pattern, locale, timeZone);

		Format format = _simpleDateFormats.get(simpleDateCacheKey);

		if (format == null) {
			format = FastDateFormat.getInstance(pattern, timeZone, locale);

			_simpleDateFormats.put(simpleDateCacheKey, format);
		}

		return format;
	}

	@Override
	public Format getSimpleDateFormat(String pattern, TimeZone timeZone) {
		return getSimpleDateFormat(pattern, LocaleUtil.getDefault(), timeZone);
	}

	@Override
	public Format getTime(int style, Locale locale, TimeZone timeZone) {
		DateOrTimeCacheKey dateOrTimeCacheKey = new DateOrTimeCacheKey(
			style, locale, timeZone);

		Format format = _timeFormats.get(dateOrTimeCacheKey);

		if (format == null) {
			format = FastDateFormat.getTimeInstance(style, timeZone, locale);

			_timeFormats.put(dateOrTimeCacheKey, format);
		}

		return format;
	}

	@Override
	public Format getTime(Locale locale) {
		return getTime(locale, null);
	}

	@Override
	public Format getTime(Locale locale, TimeZone timeZone) {
		return getTime(FastDateFormatConstants.SHORT, locale, timeZone);
	}

	@Override
	public Format getTime(TimeZone timeZone) {
		return getTime(LocaleUtil.getDefault(), timeZone);
	}

	protected String getKey(Object... arguments) {
		StringBundler sb = new StringBundler((arguments.length * 2) - 1);

		for (int i = 0; i < arguments.length; i++) {
			sb.append(arguments[i]);

			if ((i + 1) >= arguments.length) {
				continue;
			}

			sb.append(StringPool.UNDERLINE);
		}

		return sb.toString();
	}

	private final Map<DateOrTimeCacheKey, Format> _dateFormats =
		new ConcurrentHashMap<>();
	private final Map<DateAndTimeCacheKey, Format> _dateTimeFormats =
		new ConcurrentHashMap<>();
	private final Map<SimpleDateCacheKey, Format> _simpleDateFormats =
		new ConcurrentHashMap<>();
	private final Map<DateOrTimeCacheKey, Format> _timeFormats =
		new ConcurrentHashMap<>();

	private static class DateAndTimeCacheKey {

		@Override
		public boolean equals(Object object) {
			DateAndTimeCacheKey dateAndTimeCacheKey =
				(DateAndTimeCacheKey)object;

			if ((dateAndTimeCacheKey._dateStyle == _dateStyle) &&
				(dateAndTimeCacheKey._timeStyle == _timeStyle) &&
				Objects.equals(dateAndTimeCacheKey._locale, _locale) &&
				Objects.equals(dateAndTimeCacheKey._timeZone, _timeZone)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _dateStyle);

			hashCode = HashUtil.hash(hashCode, _timeStyle);
			hashCode = HashUtil.hash(hashCode, _locale);

			return HashUtil.hash(hashCode, _timeZone);
		}

		private DateAndTimeCacheKey(
			int dateStyle, int timeStyle, Locale locale, TimeZone timeZone) {

			_dateStyle = dateStyle;
			_timeStyle = timeStyle;
			_locale = locale;
			_timeZone = timeZone;
		}

		private final int _dateStyle;
		private final Locale _locale;
		private final int _timeStyle;
		private final TimeZone _timeZone;

	}

	private static class DateOrTimeCacheKey {

		@Override
		public boolean equals(Object object) {
			DateOrTimeCacheKey dateOrTimeCacheKey = (DateOrTimeCacheKey)object;

			if ((dateOrTimeCacheKey._style == _style) &&
				Objects.equals(dateOrTimeCacheKey._locale, _locale) &&
				Objects.equals(dateOrTimeCacheKey._timeZone, _timeZone)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _style);

			hashCode = HashUtil.hash(hashCode, _locale);

			return HashUtil.hash(hashCode, _timeZone);
		}

		private DateOrTimeCacheKey(
			int style, Locale locale, TimeZone timeZone) {

			_style = style;
			_locale = locale;
			_timeZone = timeZone;
		}

		private final Locale _locale;
		private final int _style;
		private final TimeZone _timeZone;

	}

	private static class SimpleDateCacheKey {

		@Override
		public boolean equals(Object object) {
			SimpleDateCacheKey simpleDateCacheKey = (SimpleDateCacheKey)object;

			if (Objects.equals(simpleDateCacheKey._pattern, _pattern) &&
				Objects.equals(simpleDateCacheKey._locale, _locale) &&
				Objects.equals(simpleDateCacheKey._timeZone, _timeZone)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _pattern);

			hashCode = HashUtil.hash(hashCode, _locale);

			return HashUtil.hash(hashCode, _timeZone);
		}

		private SimpleDateCacheKey(
			String pattern, Locale locale, TimeZone timeZone) {

			_pattern = pattern;
			_locale = locale;
			_timeZone = timeZone;
		}

		private final Locale _locale;
		private final String _pattern;
		private final TimeZone _timeZone;

	}

}