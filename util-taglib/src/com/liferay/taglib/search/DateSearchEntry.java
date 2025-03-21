/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.text.Format;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Julio Camarero
 */
public class DateSearchEntry extends TextSearchEntry {

	public Date getDate() {
		return _date;
	}

	@Override
	public String getName(HttpServletRequest httpServletRequest) {
		if (_date != null) {
			Object[] localeAndTimeZone = getLocaleAndTimeZone(
				httpServletRequest);

			Locale locale = (Locale)localeAndTimeZone[0];

			Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
				locale, (TimeZone)localeAndTimeZone[1]);

			return StringBundler.concat(
				"<span class=\"lfr-portal-tooltip\" title=\"",
				dateTimeFormat.format(_date), "\">",
				LanguageUtil.format(
					locale, _getMessageKey(),
					new Object[] {
						LanguageUtil.getTimeDescription(
							locale, _getTimeDelta(), true),
						HtmlUtil.escape(_userName)
					},
					false),
				"</span>");
		}

		return StringPool.BLANK;
	}

	public void setDate(Date date) {
		_date = date;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	protected Object[] getLocaleAndTimeZone(
		HttpServletRequest httpServletRequest) {

		if ((_locale != null) && (_timeZone != null)) {
			return new Object[] {_locale, _timeZone};
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_locale = themeDisplay.getLocale();
		_timeZone = themeDisplay.getTimeZone();

		return new Object[] {_locale, _timeZone};
	}

	private String _getMessageKey() {
		if (_date.before(new Date())) {
			if (_userName == null) {
				return "x-ago";
			}

			return "x-ago-by-x";
		}

		if (_userName == null) {
			return "within-x";
		}

		return "within-x-by-x";
	}

	private long _getTimeDelta() {
		if (_date.before(new Date())) {
			return System.currentTimeMillis() - _date.getTime();
		}

		return _date.getTime() - System.currentTimeMillis();
	}

	private Date _date;
	private Locale _locale;
	private TimeZone _timeZone;
	private String _userName;

}