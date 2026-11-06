/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.metatype.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Map;

/**
 * @author Jorge Ferrer
 */
public class ParameterMapUtil {

	public static <T> T setParameterMap(
			Class<T> clazz, T configurationBean,
			Map<String, String[]> parameterMap)
		throws ConfigurationException {

		ParameterMapInvocationHandler<T> parameterMapInvocationHandler =
			new ParameterMapInvocationHandler<>(
				clazz, configurationBean, parameterMap);

		return parameterMapInvocationHandler.createProxy();
	}

	public static <T> T setParameterMap(
			Class<T> clazz, T configurationBean,
			Map<String, String[]> parameterMap, String parameterPrefix,
			String parameterSuffix)
		throws ConfigurationException {

		ParameterMapInvocationHandler<T> parameterMapInvocationHandler =
			new ParameterMapInvocationHandler<>(
				clazz, configurationBean, parameterMap, parameterPrefix,
				parameterSuffix);

		return parameterMapInvocationHandler.createProxy();
	}

	private static class ParameterMapInvocationHandler<S>
		implements InvocationHandler {

		public ParameterMapInvocationHandler(
			Class<?> clazz, Object bean, Map<String, String[]> parameterMap) {

			this(clazz, bean, parameterMap, StringPool.BLANK, StringPool.BLANK);
		}

		public ParameterMapInvocationHandler(
			Class<?> clazz, Object bean, Map<String, String[]> parameterMap,
			String parameterPrefix, String parameterSuffix) {

			_clazz = clazz;
			_bean = bean;
			_parameterMap = parameterMap;
			_parameterPrefix = parameterPrefix;
			_parameterSuffix = parameterSuffix;
		}

		public S createProxy() {
			return (S)ProxyUtil.newProxyInstance(
				_clazz.getClassLoader(), new Class<?>[] {_clazz}, this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] arguments)
			throws InvocationTargetException {

			Object result = null;

			try {
				result = _invokeMap(method);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			if (result != null) {
				return result;
			}

			try {
				return _invokeConfigurationInstance(method, arguments);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				return null;
			}
		}

		private String _getMapValue(String name) {
			String[] values = _getMapValueArray(name);

			if (values == null) {
				return null;
			}

			return values[0];
		}

		private String[] _getMapValueArray(String name) {
			String[] values = _parameterMap.get(
				_parameterPrefix + name + _parameterSuffix);

			if (ArrayUtil.isEmpty(values)) {
				return null;
			}

			return values;
		}

		private Object _invokeConfigurationInstance(
				Method method, Object[] arguments)
			throws IllegalAccessException, InvocationTargetException,
				   NoSuchMethodException {

			Class<?> clazz = _bean.getClass();

			method = clazz.getMethod(
				method.getName(), method.getParameterTypes());

			return method.invoke(_bean, arguments);
		}

		private Object _invokeMap(Method method)
			throws IllegalAccessException, InstantiationException,
				   InvocationTargetException, NoSuchMethodException {

			Class<?> returnType = method.getReturnType();

			if (returnType.equals(boolean.class)) {
				String value = _getMapValue(method.getName());

				if (value == null) {
					return null;
				}

				return Boolean.valueOf(value);
			}
			else if (returnType.equals(double.class)) {
				String value = _getMapValue(method.getName());

				if (value == null) {
					return null;
				}

				return Double.valueOf(_getMapValue(method.getName()));
			}
			else if (returnType.equals(float.class)) {
				String value = _getMapValue(method.getName());

				if (value == null) {
					return null;
				}

				return Float.valueOf(_getMapValue(method.getName()));
			}
			else if (returnType.equals(int.class)) {
				String value = _getMapValue(method.getName());

				if (value == null) {
					return null;
				}

				return Integer.valueOf(_getMapValue(method.getName()));
			}
			else if (returnType.equals(LocalizedValuesMap.class)) {
				return _getMapValue(method.getName());
			}
			else if (returnType.equals(long.class)) {
				String value = _getMapValue(method.getName());

				if (value == null) {
					return null;
				}

				return Long.valueOf(_getMapValue(method.getName()));
			}
			else if (returnType.equals(String.class)) {
				return _getMapValue(method.getName());
			}
			else if (returnType.equals(String[].class)) {
				return _getMapValueArray(method.getName());
			}

			String value = _getMapValue(method.getName());

			if (value == null) {
				return null;
			}

			if (returnType.isEnum()) {
				Method valueOfMethod = returnType.getDeclaredMethod(
					"valueOf", String.class);

				return valueOfMethod.invoke(returnType, value);
			}

			Constructor<?> constructor = returnType.getConstructor(
				String.class);

			return constructor.newInstance(value);
		}

		private static final Log _log = LogFactoryUtil.getLog(
			ParameterMapInvocationHandler.class);

		private final Object _bean;
		private final Class<?> _clazz;
		private final Map<String, String[]> _parameterMap;
		private final String _parameterPrefix;
		private final String _parameterSuffix;

	}

}