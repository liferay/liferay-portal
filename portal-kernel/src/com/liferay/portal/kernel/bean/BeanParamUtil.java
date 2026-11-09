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

}