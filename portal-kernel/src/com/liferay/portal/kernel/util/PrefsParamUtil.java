/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 */
public class PrefsParamUtil {

	public static boolean getBoolean(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param) {

		return getBoolean(
			portletPreferences, httpServletRequest, param,
			GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param,
		boolean defaultValue) {

		String preferencesValue = portletPreferences.getValue(param, null);

		boolean getterUtilValue = GetterUtil.getBoolean(
			preferencesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static boolean getBoolean(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param) {

		return getBoolean(
			portletPreferences, portletRequest, param,
			GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param, boolean defaultValue) {

		String preferencesValue = portletPreferences.getValue(param, null);

		boolean getterUtilValue = GetterUtil.getBoolean(
			preferencesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static double getDouble(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param) {

		return getDouble(
			portletPreferences, httpServletRequest, param,
			GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDouble(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param,
		double defaultValue) {

		String preferencesValue = portletPreferences.getValue(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			preferencesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static double getDouble(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param,
		double defaultValue, Locale locale) {

		String preferencesValue = portletPreferences.getValue(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			preferencesValue, defaultValue);

		return ParamUtil.getDouble(
			httpServletRequest, param, getterUtilValue, locale);
	}

	public static double getDouble(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param, Locale locale) {

		return getDouble(
			portletPreferences, httpServletRequest, param,
			GetterUtil.DEFAULT_DOUBLE, locale);
	}

	public static double getDouble(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param) {

		return getDouble(
			portletPreferences, portletRequest, param,
			GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDouble(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param, double defaultValue) {

		String preferencesValue = portletPreferences.getValue(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			preferencesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static double getDouble(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param, double defaultValue, Locale locale) {

		String preferencesValue = portletPreferences.getValue(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			preferencesValue, defaultValue);

		return ParamUtil.getDouble(
			portletRequest, param, getterUtilValue, locale);
	}

	public static double getDouble(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param, Locale locale) {

		return getDouble(
			portletPreferences, portletRequest, param,
			GetterUtil.DEFAULT_DOUBLE, locale);
	}

	public static int getInteger(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param) {

		return getInteger(
			portletPreferences, httpServletRequest, param,
			GetterUtil.DEFAULT_INTEGER);
	}

	public static int getInteger(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param, int defaultValue) {

		String preferencesValue = portletPreferences.getValue(param, null);

		int getterUtilValue = GetterUtil.getInteger(
			preferencesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static int getInteger(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param) {

		return getInteger(
			portletPreferences, portletRequest, param,
			GetterUtil.DEFAULT_INTEGER);
	}

	public static int getInteger(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param, int defaultValue) {

		String preferencesValue = portletPreferences.getValue(param, null);

		int getterUtilValue = GetterUtil.getInteger(
			preferencesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static long getLong(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param) {

		return getLong(
			portletPreferences, httpServletRequest, param,
			GetterUtil.DEFAULT_LONG);
	}

	public static long getLong(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param,
		long defaultValue) {

		String preferencesValue = portletPreferences.getValue(param, null);

		long getterUtilValue = GetterUtil.getLong(
			preferencesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static long getLong(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param) {

		return getLong(
			portletPreferences, portletRequest, param, GetterUtil.DEFAULT_LONG);
	}

	public static long getLong(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param, long defaultValue) {

		String preferencesValue = portletPreferences.getValue(param, null);

		long getterUtilValue = GetterUtil.getLong(
			preferencesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static String getString(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param) {

		return getString(
			portletPreferences, httpServletRequest, param,
			GetterUtil.DEFAULT_STRING);
	}

	public static String getString(
		PortletPreferences portletPreferences,
		HttpServletRequest httpServletRequest, String param,
		String defaultValue) {

		String preferencesValue = portletPreferences.getValue(param, null);

		String getterUtilValue = GetterUtil.getString(
			preferencesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param) {

		return getString(
			portletPreferences, portletRequest, param,
			GetterUtil.DEFAULT_STRING);
	}

	public static String getString(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		String param, String defaultValue) {

		String preferencesValue = portletPreferences.getValue(param, null);

		String getterUtilValue = GetterUtil.getString(
			preferencesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

}