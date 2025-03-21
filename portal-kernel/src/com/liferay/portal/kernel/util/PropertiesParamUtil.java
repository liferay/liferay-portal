/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Brian Wing Shun Chan
 */
public class PropertiesParamUtil {

	public static boolean getBoolean(
		Properties properties, HttpServletRequest httpServletRequest,
		String param) {

		return getBoolean(
			properties, httpServletRequest, param, GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(
		Properties properties, HttpServletRequest httpServletRequest,
		String param, boolean defaultValue) {

		String propertiesValue = properties.getProperty(param, null);

		boolean getterUtilValue = GetterUtil.getBoolean(
			propertiesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static boolean getBoolean(
		Properties properties, PortletRequest portletRequest, String param) {

		return getBoolean(
			properties, portletRequest, param, GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(
		Properties properties, PortletRequest portletRequest, String param,
		boolean defaultValue) {

		String propertiesValue = properties.getProperty(param, null);

		boolean getterUtilValue = GetterUtil.getBoolean(
			propertiesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static boolean getBoolean(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param) {

		return getBoolean(
			unicodeProperties, httpServletRequest, param,
			GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param,
		boolean defaultValue) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		boolean getterUtilValue = GetterUtil.getBoolean(
			propertiesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static boolean getBoolean(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param) {

		return getBoolean(
			unicodeProperties, portletRequest, param,
			GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param, boolean defaultValue) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		boolean getterUtilValue = GetterUtil.getBoolean(
			propertiesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static double getDouble(
		Properties properties, HttpServletRequest httpServletRequest,
		String param) {

		return getDouble(
			properties, httpServletRequest, param, GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDouble(
		Properties properties, HttpServletRequest httpServletRequest,
		String param, double defaultValue) {

		String propertiesValue = properties.getProperty(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			propertiesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static double getDouble(
		Properties properties, HttpServletRequest httpServletRequest,
		String param, double defaultValue, Locale locale) {

		String propertiesValue = properties.getProperty(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			propertiesValue, defaultValue);

		return ParamUtil.getDouble(
			httpServletRequest, param, getterUtilValue, locale);
	}

	public static double getDouble(
		Properties properties, HttpServletRequest httpServletRequest,
		String param, Locale locale) {

		return getDouble(
			properties, httpServletRequest, param, GetterUtil.DEFAULT_DOUBLE,
			locale);
	}

	public static double getDouble(
		Properties properties, PortletRequest portletRequest, String param) {

		return getDouble(
			properties, portletRequest, param, GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDouble(
		Properties properties, PortletRequest portletRequest, String param,
		double defaultValue) {

		String propertiesValue = properties.getProperty(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			propertiesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static double getDouble(
		Properties properties, PortletRequest portletRequest, String param,
		double defaultValue, Locale locale) {

		String propertiesValue = properties.getProperty(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			propertiesValue, defaultValue);

		return ParamUtil.getDouble(
			portletRequest, param, getterUtilValue, locale);
	}

	public static double getDouble(
		Properties properties, PortletRequest portletRequest, String param,
		Locale locale) {

		return getDouble(
			properties, portletRequest, param, GetterUtil.DEFAULT_DOUBLE,
			locale);
	}

	public static double getDouble(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param) {

		return getDouble(
			unicodeProperties, httpServletRequest, param,
			GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDouble(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param,
		double defaultValue) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			propertiesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static double getDouble(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param,
		double defaultValue, Locale locale) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			propertiesValue, defaultValue);

		return ParamUtil.getDouble(
			httpServletRequest, param, getterUtilValue, locale);
	}

	public static double getDouble(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param, Locale locale) {

		return getDouble(
			unicodeProperties, httpServletRequest, param,
			GetterUtil.DEFAULT_DOUBLE, locale);
	}

	public static double getDouble(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param) {

		return getDouble(
			unicodeProperties, portletRequest, param,
			GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDouble(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param, double defaultValue) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			propertiesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static double getDouble(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param, double defaultValue, Locale locale) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		double getterUtilValue = GetterUtil.getDouble(
			propertiesValue, defaultValue);

		return ParamUtil.getDouble(
			portletRequest, param, getterUtilValue, locale);
	}

	public static double getDouble(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param, Locale locale) {

		return getDouble(
			unicodeProperties, portletRequest, param, GetterUtil.DEFAULT_DOUBLE,
			locale);
	}

	public static int getInteger(
		Properties properties, HttpServletRequest httpServletRequest,
		String param) {

		return getInteger(
			properties, httpServletRequest, param, GetterUtil.DEFAULT_INTEGER);
	}

	public static int getInteger(
		Properties properties, HttpServletRequest httpServletRequest,
		String param, int defaultValue) {

		String propertiesValue = properties.getProperty(param, null);

		int getterUtilValue = GetterUtil.getInteger(
			propertiesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static int getInteger(
		Properties properties, PortletRequest portletRequest, String param) {

		return getInteger(
			properties, portletRequest, param, GetterUtil.DEFAULT_INTEGER);
	}

	public static int getInteger(
		Properties properties, PortletRequest portletRequest, String param,
		int defaultValue) {

		String propertiesValue = properties.getProperty(param, null);

		int getterUtilValue = GetterUtil.getInteger(
			propertiesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static int getInteger(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param) {

		return getInteger(
			unicodeProperties, httpServletRequest, param,
			GetterUtil.DEFAULT_INTEGER);
	}

	public static int getInteger(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param, int defaultValue) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		int getterUtilValue = GetterUtil.getInteger(
			propertiesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static int getInteger(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param) {

		return getInteger(
			unicodeProperties, portletRequest, param,
			GetterUtil.DEFAULT_INTEGER);
	}

	public static int getInteger(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param, int defaultValue) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		int getterUtilValue = GetterUtil.getInteger(
			propertiesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static long getLong(
		Properties properties, HttpServletRequest httpServletRequest,
		String param) {

		return getLong(
			properties, httpServletRequest, param, GetterUtil.DEFAULT_LONG);
	}

	public static long getLong(
		Properties properties, HttpServletRequest httpServletRequest,
		String param, long defaultValue) {

		String propertiesValue = properties.getProperty(param, null);

		long getterUtilValue = GetterUtil.getLong(
			propertiesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static long getLong(
		Properties properties, PortletRequest portletRequest, String param) {

		return getLong(
			properties, portletRequest, param, GetterUtil.DEFAULT_LONG);
	}

	public static long getLong(
		Properties properties, PortletRequest portletRequest, String param,
		long defaultValue) {

		String propertiesValue = properties.getProperty(param, null);

		long getterUtilValue = GetterUtil.getLong(
			propertiesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static long getLong(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param) {

		return getLong(
			unicodeProperties, httpServletRequest, param,
			GetterUtil.DEFAULT_LONG);
	}

	public static long getLong(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param,
		long defaultValue) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		long getterUtilValue = GetterUtil.getLong(
			propertiesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static long getLong(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param) {

		return getLong(
			unicodeProperties, portletRequest, param, GetterUtil.DEFAULT_LONG);
	}

	public static long getLong(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param, long defaultValue) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		long getterUtilValue = GetterUtil.getLong(
			propertiesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static UnicodeProperties getProperties(
		HttpServletRequest httpServletRequest, String prefix) {

		UnicodeProperties unicodeProperties = new UnicodeProperties(true);

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();

		for (String param : parameterMap.keySet()) {
			if (param.startsWith(prefix)) {
				String key = param.substring(
					prefix.length(), param.length() - 2);

				String value = httpServletRequest.getParameter(param);

				unicodeProperties.setProperty(key, value);
			}
		}

		return unicodeProperties;
	}

	public static UnicodeProperties getProperties(
		PortletRequest portletRequest, String prefix) {

		UnicodeProperties unicodeProperties = new UnicodeProperties(true);

		Map<String, String[]> parameterMap = portletRequest.getParameterMap();

		for (String param : parameterMap.keySet()) {
			if (param.startsWith(prefix)) {
				String key = param.substring(
					prefix.length(), param.length() - 2);

				String[] values = portletRequest.getParameterValues(param);

				// Portlets that opt-in to Portlet 3.0 functionality might have
				// null values in the array. Make the array compatible with the
				// call to StringUtil.merge(String[]) below by replacing each
				// null value with the empty string.

				unicodeProperties.setProperty(
					key,
					StringUtil.merge(
						values, s -> Objects.toString(s, StringPool.BLANK),
						StringPool.COMMA));
			}
		}

		return unicodeProperties;
	}

	public static UnicodeProperties getProperties(
		ServiceContext serviceContext, String prefix) {

		UnicodeProperties unicodeProperties = new UnicodeProperties(true);

		Map<String, Serializable> attributes = serviceContext.getAttributes();

		for (String param : attributes.keySet()) {
			if (param.startsWith(prefix)) {
				String key = param.substring(
					prefix.length(), param.length() - 2);

				String value = ParamUtil.getString(serviceContext, param);

				unicodeProperties.setProperty(key, value);
			}
		}

		return unicodeProperties;
	}

	public static String getString(
		Properties properties, HttpServletRequest httpServletRequest,
		String param) {

		return getString(
			properties, httpServletRequest, param, GetterUtil.DEFAULT_STRING);
	}

	public static String getString(
		Properties properties, HttpServletRequest httpServletRequest,
		String param, String defaultValue) {

		String propertiesValue = properties.getProperty(param, null);

		String getterUtilValue = GetterUtil.getString(
			propertiesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static String getString(
		Properties properties, PortletRequest portletRequest, String param) {

		return getString(
			properties, portletRequest, param, GetterUtil.DEFAULT_STRING);
	}

	public static String getString(
		Properties properties, PortletRequest portletRequest, String param,
		String defaultValue) {

		String propertiesValue = properties.getProperty(param, null);

		String getterUtilValue = GetterUtil.getString(
			propertiesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

	public static String getString(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param) {

		return getString(
			unicodeProperties, httpServletRequest, param,
			GetterUtil.DEFAULT_STRING);
	}

	public static String getString(
		UnicodeProperties unicodeProperties,
		HttpServletRequest httpServletRequest, String param,
		String defaultValue) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		String getterUtilValue = GetterUtil.getString(
			propertiesValue, defaultValue);

		return ParamUtil.get(httpServletRequest, param, getterUtilValue);
	}

	public static String getString(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param) {

		return getString(
			unicodeProperties, portletRequest, param,
			GetterUtil.DEFAULT_STRING);
	}

	public static String getString(
		UnicodeProperties unicodeProperties, PortletRequest portletRequest,
		String param, String defaultValue) {

		String propertiesValue = unicodeProperties.getProperty(param, null);

		String getterUtilValue = GetterUtil.getString(
			propertiesValue, defaultValue);

		return ParamUtil.get(portletRequest, param, getterUtilValue);
	}

}