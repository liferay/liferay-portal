/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.util;

import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Michele Vigilante
 */
public class DSRUtil {

	public static Integer getExpirationDays() {
		Date expirationDate = LicenseManagerUtil.getAppExpirationDate(App.DSR);

		if (_isExpired(expirationDate) || (expirationDate == null)) {
			return null;
		}

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		int days = DateUtil.getDaysBetween(calendar.getTime(), expirationDate);

		if (days > _EXPIRATION_WARNING_DAYS) {
			return null;
		}

		return days;
	}

	public static boolean isExpired() {
		return _isExpired(LicenseManagerUtil.getAppExpirationDate(App.DSR));
	}

	private static boolean _isExpired(Date expirationDate) {
		if (!LicenseManagerUtil.isAppEnabled(App.DSR)) {
			return true;
		}

		if (expirationDate == null) {
			return false;
		}

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		return !expirationDate.after(calendar.getTime());
	}

	private static final int _EXPIRATION_WARNING_DAYS = 30;

}