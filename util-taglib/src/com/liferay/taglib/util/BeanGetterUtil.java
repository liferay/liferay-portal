/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Shuyang Zhou
 */
public class BeanGetterUtil {

	public static long getLong(Object bean, String name) {
		return GetterUtil.getLong(_getValue(bean, name, false));
	}

	public static Object getObjectSilent(Object bean, String name) {
		return _getValue(bean, name, true);
	}

	public static String getStringSilent(Object bean, String name) {
		return getStringSilent(bean, name, GetterUtil.DEFAULT_STRING);
	}

	public static String getStringSilent(
		Object bean, String name, String defaultValue) {

		Object value = _getValue(bean, name, true);

		if (value == null) {
			return defaultValue;
		}

		return String.valueOf(value);
	}

	private static Method _findMethod(Class<?> clazz, String methodName) {
		Method method = ReflectionUtil.fetchMethod(clazz, methodName);

		if ((method == null) || (method.getDeclaringClass() == Object.class) ||
			Modifier.isStatic(method.getModifiers())) {

			return null;
		}

		return method;
	}

	private static Method _getMethod(Class<?> clazz, String name) {
		Map<String, Object> methods = _methodsClassValue.get(clazz);

		Object method = methods.get(name);

		if (method == null) {
			String suffix = StringUtil.upperCaseFirstLetter(name);

			method = _findMethod(clazz, "get".concat(suffix));

			if (method == null) {
				method = _findMethod(clazz, "is".concat(suffix));

				if (method == null) {
					method = _NULL_METHOD;
				}
			}

			methods.put(name, method);
		}

		if (method == _NULL_METHOD) {
			return null;
		}

		return (Method)method;
	}

	private static Object _getValue(Object bean, String name, boolean silent) {
		if (bean == null) {
			return null;
		}

		Class<?> clazz = bean.getClass();

		Method method = _getMethod(clazz, name);

		if (method == null) {
			_logFailure(clazz, name, silent, null);

			return null;
		}

		try {
			return method.invoke(bean);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			_logFailure(clazz, name, silent, reflectiveOperationException);
		}

		return null;
	}

	private static void _logFailure(
		Class<?> clazz, String name, boolean silent, Throwable throwable) {

		if (silent) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to get property ", name, " of ",
						clazz.getName()),
					throwable);
			}
		}
		else {
			_log.error(
				StringBundler.concat(
					"Unable to get property ", name, " of ", clazz.getName()),
				throwable);
		}
	}

	private static final Object _NULL_METHOD = new Object();

	private static final Log _log = LogFactoryUtil.getLog(BeanGetterUtil.class);

	private static final ClassValue<Map<String, Object>> _methodsClassValue =
		new ClassValue<>() {

			@Override
			protected Map<String, Object> computeValue(Class<?> clazz) {
				return new ConcurrentHashMap<>();
			}

		};

}