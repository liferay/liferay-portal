/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.bean;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * @author Brian Wing Shun Chan
 */
public class BeanParamUtil {

	public static <T> double getBigDecimalAsDouble(
		T bean, HttpServletRequest httpServletRequest, String param,
		Function<T, BigDecimal> function, double defaultValue) {

		if (bean != null) {
			defaultValue = GetterUtil.getDouble(
				function.apply(bean), defaultValue);
		}

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static boolean getBoolean(
		Object bean, HttpServletRequest httpServletRequest, String param) {

		return getBoolean(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(
		Object bean, HttpServletRequest httpServletRequest, String param,
		boolean defaultValue) {

		defaultValue = BeanPropertiesUtil.getBoolean(bean, param, defaultValue);

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static boolean getBoolean(
		Object bean, PortletRequest portletRequest, String param) {

		return getBoolean(
			bean, portletRequest, param, GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBoolean(
		Object bean, PortletRequest portletRequest, String param,
		boolean defaultValue) {

		defaultValue = BeanPropertiesUtil.getBoolean(bean, param, defaultValue);

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static <T> boolean getBoolean(
		T bean, HttpServletRequest httpServletRequest, String param,
		Function<T, Boolean> function) {

		return getBoolean(
			bean, httpServletRequest, param, function,
			GetterUtil.DEFAULT_BOOLEAN);
	}

	public static <T> boolean getBoolean(
		T bean, HttpServletRequest httpServletRequest, String param,
		Function<T, Boolean> function, boolean defaultValue) {

		if (bean != null) {
			defaultValue = function.apply(bean);
		}

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static <T> boolean getBoolean(
		T bean, PortletRequest portletRequest, String param,
		Function<T, Boolean> function) {

		return getBoolean(
			bean, portletRequest, param, function, GetterUtil.DEFAULT_BOOLEAN);
	}

	public static <T> boolean getBoolean(
		T bean, PortletRequest portletRequest, String param,
		Function<T, Boolean> function, boolean defaultValue) {

		if (bean != null) {
			defaultValue = function.apply(bean);
		}

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static boolean getBooleanSilent(
		Object bean, HttpServletRequest httpServletRequest, String param) {

		return getBooleanSilent(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBooleanSilent(
		Object bean, HttpServletRequest httpServletRequest, String param,
		boolean defaultValue) {

		defaultValue = BeanPropertiesUtil.getBooleanSilent(
			bean, param, defaultValue);

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static boolean getBooleanSilent(
		Object bean, PortletRequest portletRequest, String param) {

		return getBooleanSilent(
			bean, portletRequest, param, GetterUtil.DEFAULT_BOOLEAN);
	}

	public static boolean getBooleanSilent(
		Object bean, PortletRequest portletRequest, String param,
		boolean defaultValue) {

		defaultValue = BeanPropertiesUtil.getBooleanSilent(
			bean, param, defaultValue);

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static double getDouble(
		Object bean, HttpServletRequest httpServletRequest, String param) {

		return getDouble(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDouble(
		Object bean, HttpServletRequest httpServletRequest, String param,
		double defaultValue) {

		defaultValue = BeanPropertiesUtil.getDouble(bean, param, defaultValue);

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static double getDouble(
		Object bean, HttpServletRequest httpServletRequest, String param,
		double defaultValue, Locale locale) {

		defaultValue = BeanPropertiesUtil.getDouble(bean, param, defaultValue);

		return ParamUtil.getDouble(
			httpServletRequest, param, defaultValue, locale);
	}

	public static double getDouble(
		Object bean, HttpServletRequest httpServletRequest, String param,
		Locale locale) {

		return getDouble(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_DOUBLE, locale);
	}

	public static double getDouble(
		Object bean, PortletRequest portletRequest, String param) {

		return getDouble(
			bean, portletRequest, param, GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDouble(
		Object bean, PortletRequest portletRequest, String param,
		double defaultValue) {

		defaultValue = BeanPropertiesUtil.getDouble(bean, param, defaultValue);

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static double getDouble(
		Object bean, PortletRequest portletRequest, String param,
		double defaultValue, Locale locale) {

		defaultValue = BeanPropertiesUtil.getDouble(bean, param, defaultValue);

		return ParamUtil.getDouble(portletRequest, param, defaultValue, locale);
	}

	public static double getDouble(
		Object bean, PortletRequest portletRequest, String param,
		Locale locale) {

		return getDouble(
			bean, portletRequest, param, GetterUtil.DEFAULT_DOUBLE, locale);
	}

	public static <T> double getDouble(
		T bean, HttpServletRequest httpServletRequest, String param,
		ToDoubleFunction<T> toDoubleFunction) {

		return getDouble(
			bean, httpServletRequest, param, toDoubleFunction,
			GetterUtil.DEFAULT_DOUBLE);
	}

	public static <T> double getDouble(
		T bean, HttpServletRequest httpServletRequest, String param,
		ToDoubleFunction<T> toDoubleFunction, double defaultValue) {

		if (bean != null) {
			defaultValue = toDoubleFunction.applyAsDouble(bean);
		}

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static double getDoubleSilent(
		Object bean, HttpServletRequest httpServletRequest, String param) {

		return getDoubleSilent(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDoubleSilent(
		Object bean, HttpServletRequest httpServletRequest, String param,
		double defaultValue) {

		defaultValue = BeanPropertiesUtil.getDoubleSilent(
			bean, param, defaultValue);

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static double getDoubleSilent(
		Object bean, HttpServletRequest httpServletRequest, String param,
		double defaultValue, Locale locale) {

		defaultValue = BeanPropertiesUtil.getDoubleSilent(
			bean, param, defaultValue);

		return ParamUtil.getDouble(
			httpServletRequest, param, defaultValue, locale);
	}

	public static double getDoubleSilent(
		Object bean, HttpServletRequest httpServletRequest, String param,
		Locale locale) {

		return getDoubleSilent(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_DOUBLE, locale);
	}

	public static double getDoubleSilent(
		Object bean, PortletRequest portletRequest, String param) {

		return getDoubleSilent(
			bean, portletRequest, param, GetterUtil.DEFAULT_DOUBLE);
	}

	public static double getDoubleSilent(
		Object bean, PortletRequest portletRequest, String param,
		double defaultValue) {

		defaultValue = BeanPropertiesUtil.getDoubleSilent(
			bean, param, defaultValue);

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static double getDoubleSilent(
		Object bean, PortletRequest portletRequest, String param,
		double defaultValue, Locale locale) {

		defaultValue = BeanPropertiesUtil.getDoubleSilent(
			bean, param, defaultValue);

		return ParamUtil.getDouble(portletRequest, param, defaultValue, locale);
	}

	public static double getDoubleSilent(
		Object bean, PortletRequest portletRequest, String param,
		Locale locale) {

		return getDoubleSilent(
			bean, portletRequest, param, GetterUtil.DEFAULT_DOUBLE, locale);
	}

	public static int getInteger(
		Object bean, HttpServletRequest httpServletRequest, String param) {

		return getInteger(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_INTEGER);
	}

	public static int getInteger(
		Object bean, HttpServletRequest httpServletRequest, String param,
		int defaultValue) {

		defaultValue = BeanPropertiesUtil.getInteger(bean, param, defaultValue);

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static int getInteger(
		Object bean, PortletRequest portletRequest, String param) {

		return getInteger(
			bean, portletRequest, param, GetterUtil.DEFAULT_INTEGER);
	}

	public static int getInteger(
		Object bean, PortletRequest portletRequest, String param,
		int defaultValue) {

		defaultValue = BeanPropertiesUtil.getInteger(bean, param, defaultValue);

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static <T> int getInteger(
		T bean, HttpServletRequest httpServletRequest, String param,
		ToIntFunction<T> toIntFunction) {

		return getInteger(
			bean, httpServletRequest, param, toIntFunction,
			GetterUtil.DEFAULT_INTEGER);
	}

	public static <T> int getInteger(
		T bean, HttpServletRequest httpServletRequest, String param,
		ToIntFunction<T> toIntFunction, int defaultValue) {

		if (bean != null) {
			defaultValue = toIntFunction.applyAsInt(bean);
		}

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static int getIntegerSilent(
		Object bean, HttpServletRequest httpServletRequest, String param) {

		return getIntegerSilent(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_INTEGER);
	}

	public static int getIntegerSilent(
		Object bean, HttpServletRequest httpServletRequest, String param,
		int defaultValue) {

		defaultValue = BeanPropertiesUtil.getIntegerSilent(
			bean, param, defaultValue);

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static int getIntegerSilent(
		Object bean, PortletRequest portletRequest, String param) {

		return getIntegerSilent(
			bean, portletRequest, param, GetterUtil.DEFAULT_INTEGER);
	}

	public static int getIntegerSilent(
		Object bean, PortletRequest portletRequest, String param,
		int defaultValue) {

		defaultValue = BeanPropertiesUtil.getIntegerSilent(
			bean, param, defaultValue);

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static long getLong(
		Object bean, HttpServletRequest httpServletRequest, String param) {

		return getLong(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_LONG);
	}

	public static long getLong(
		Object bean, HttpServletRequest httpServletRequest, String param,
		long defaultValue) {

		defaultValue = BeanPropertiesUtil.getLong(bean, param, defaultValue);

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static long getLong(
		Object bean, PortletRequest portletRequest, String param) {

		return getLong(bean, portletRequest, param, GetterUtil.DEFAULT_LONG);
	}

	public static long getLong(
		Object bean, PortletRequest portletRequest, String param,
		long defaultValue) {

		defaultValue = BeanPropertiesUtil.getLong(bean, param, defaultValue);

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static <T> long getLong(
		T bean, HttpServletRequest httpServletRequest, String param,
		ToLongFunction<T> toLongFunction) {

		return getLong(
			bean, httpServletRequest, param, toLongFunction,
			GetterUtil.DEFAULT_LONG);
	}

	public static <T> long getLong(
		T bean, HttpServletRequest httpServletRequest, String param,
		ToLongFunction<T> toLongFunction, long defaultValue) {

		if (bean != null) {
			defaultValue = toLongFunction.applyAsLong(bean);
		}

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static <T> long getLong(
		T bean, PortletRequest portletRequest, String param,
		ToLongFunction<T> toLongFunction) {

		return getLong(
			bean, portletRequest, param, toLongFunction,
			GetterUtil.DEFAULT_LONG);
	}

	public static <T> long getLong(
		T bean, PortletRequest portletRequest, String param,
		ToLongFunction<T> toLongFunction, long defaultValue) {

		if (bean != null) {
			defaultValue = toLongFunction.applyAsLong(bean);
		}

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static long getLongSilent(
		Object bean, HttpServletRequest httpServletRequest, String param) {

		return getLongSilent(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_LONG);
	}

	public static long getLongSilent(
		Object bean, HttpServletRequest httpServletRequest, String param,
		long defaultValue) {

		defaultValue = BeanPropertiesUtil.getLongSilent(
			bean, param, defaultValue);

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static long getLongSilent(
		Object bean, PortletRequest portletRequest, String param) {

		return getLongSilent(
			bean, portletRequest, param, GetterUtil.DEFAULT_LONG);
	}

	public static long getLongSilent(
		Object bean, PortletRequest portletRequest, String param,
		long defaultValue) {

		defaultValue = BeanPropertiesUtil.getLongSilent(
			bean, param, defaultValue);

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static String getString(
		Object bean, HttpServletRequest httpServletRequest, String param) {

		return getString(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_STRING);
	}

	public static String getString(
		Object bean, HttpServletRequest httpServletRequest, String param,
		String defaultValue) {

		defaultValue = BeanPropertiesUtil.getString(bean, param, defaultValue);

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static String getString(
		Object bean, PortletRequest portletRequest, String param) {

		return getString(
			bean, portletRequest, param, GetterUtil.DEFAULT_STRING);
	}

	public static String getString(
		Object bean, PortletRequest portletRequest, String param,
		String defaultValue) {

		defaultValue = BeanPropertiesUtil.getString(bean, param, defaultValue);

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static <T> String getString(
		T bean, HttpServletRequest httpServletRequest, String param,
		Function<T, String> function) {

		return getString(
			bean, httpServletRequest, param, function,
			GetterUtil.DEFAULT_STRING);
	}

	public static <T> String getString(
		T bean, HttpServletRequest httpServletRequest, String param,
		Function<T, String> function, String defaultValue) {

		if (bean != null) {
			defaultValue = function.apply(bean);
		}

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static <T> String getString(
		T bean, PortletRequest portletRequest, String param,
		Function<T, String> function) {

		return getString(
			bean, portletRequest, param, function, GetterUtil.DEFAULT_STRING);
	}

	public static <T> String getString(
		T bean, PortletRequest portletRequest, String param,
		Function<T, String> function, String defaultValue) {

		if (bean != null) {
			defaultValue = function.apply(bean);
		}

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

	public static String getStringSilent(
		Object bean, HttpServletRequest httpServletRequest, String param) {

		return getStringSilent(
			bean, httpServletRequest, param, GetterUtil.DEFAULT_STRING);
	}

	public static String getStringSilent(
		Object bean, HttpServletRequest httpServletRequest, String param,
		String defaultValue) {

		defaultValue = BeanPropertiesUtil.getStringSilent(
			bean, param, defaultValue);

		return ParamUtil.get(httpServletRequest, param, defaultValue);
	}

	public static String getStringSilent(
		Object bean, PortletRequest portletRequest, String param) {

		return getStringSilent(
			bean, portletRequest, param, GetterUtil.DEFAULT_STRING);
	}

	public static String getStringSilent(
		Object bean, PortletRequest portletRequest, String param,
		String defaultValue) {

		defaultValue = BeanPropertiesUtil.getStringSilent(
			bean, param, defaultValue);

		return ParamUtil.get(portletRequest, param, defaultValue);
	}

}