/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.search;

import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;

import java.util.Calendar;

/**
 * @author Brian Wing Shun Chan
 */
public class DAOParamUtil {

	public static boolean getBoolean(
		HttpServletRequest httpServletRequest, String param) {

		return GetterUtil.getBoolean(getString(httpServletRequest, param));
	}

	public static boolean getBoolean(
		PortletRequest portletRequest, String param) {

		return GetterUtil.getBoolean(getString(portletRequest, param));
	}

	public static int getInteger(
		HttpServletRequest httpServletRequest, String param) {

		return GetterUtil.getInteger(getString(httpServletRequest, param));
	}

	public static int getInteger(PortletRequest portletRequest, String param) {
		return GetterUtil.getInteger(getString(portletRequest, param));
	}

	public static String getISODate(
		HttpServletRequest httpServletRequest, String param) {

		int month = ParamUtil.getInteger(httpServletRequest, param + "Month");
		int day = ParamUtil.getInteger(httpServletRequest, param + "Day");
		int year = ParamUtil.getInteger(httpServletRequest, param + "Year");

		if ((month >= 0) && (day > 0) && (year > 0)) {
			Calendar cal = CalendarFactoryUtil.getCalendar();

			int minute = ParamUtil.getInteger(
				httpServletRequest, param + "Minute", -1);
			int hour = ParamUtil.getInteger(
				httpServletRequest, param + "Hour", -1);

			if ((hour == -1) || (minute == -1)) {
				cal.set(year, month, day);
			}
			else {
				int amPm = ParamUtil.getInteger(
					httpServletRequest, param + "AmPm");

				if (amPm == Calendar.PM) {
					hour += 12;
				}

				cal.set(year, month, day, hour, minute, 0);
			}

			DateFormat isoFormat = DateUtil.getISOFormat();

			return isoFormat.format(cal.getTime());
		}

		return null;
	}

	public static String getISODate(
		PortletRequest portletRequest, String param) {

		int month = ParamUtil.getInteger(portletRequest, param + "Month");
		int day = ParamUtil.getInteger(portletRequest, param + "Day");
		int year = ParamUtil.getInteger(portletRequest, param + "Year");

		if ((month >= 0) && (day > 0) && (year > 0)) {
			Calendar cal = CalendarFactoryUtil.getCalendar();

			int minute = ParamUtil.getInteger(
				portletRequest, param + "Minute", -1);
			int hour = ParamUtil.getInteger(portletRequest, param + "Hour", -1);

			if ((hour == -1) || (minute == -1)) {
				cal.set(year, month, day);
			}
			else {
				int amPm = ParamUtil.getInteger(portletRequest, param + "AmPm");

				if (amPm == Calendar.PM) {
					hour += 12;
				}

				cal.set(year, month, day, hour, minute, 0);
			}

			DateFormat isoFormat = DateUtil.getISOFormat();

			return isoFormat.format(cal.getTime());
		}

		return null;
	}

	public static long getLong(
		HttpServletRequest httpServletRequest, String param) {

		return GetterUtil.getLong(getString(httpServletRequest, param));
	}

	public static long getLong(PortletRequest portletRequest, String param) {
		return GetterUtil.getLong(getString(portletRequest, param));
	}

	public static short getShort(
		HttpServletRequest httpServletRequest, String param) {

		return GetterUtil.getShort(getString(httpServletRequest, param));
	}

	public static short getShort(PortletRequest portletRequest, String param) {
		return GetterUtil.getShort(getString(portletRequest, param));
	}

	public static String getString(
		HttpServletRequest httpServletRequest, String param) {

		String value = ParamUtil.getString(httpServletRequest, param);

		if (Validator.isNull(value)) {
			return null;
		}

		return value;
	}

	public static String getString(
		PortletRequest portletRequest, String param) {

		String value = ParamUtil.getString(portletRequest, param);

		if (Validator.isNull(value)) {
			return null;
		}

		return value;
	}

}