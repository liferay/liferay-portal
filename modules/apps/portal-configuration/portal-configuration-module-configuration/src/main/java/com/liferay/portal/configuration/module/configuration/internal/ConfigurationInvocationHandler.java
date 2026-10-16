/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.module.configuration.internal;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.settings.TypedSettings;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Map;

/**
 * @author Iván Zaera
 */
public class ConfigurationInvocationHandler<S> implements InvocationHandler {

	public ConfigurationInvocationHandler(
		Class<S> clazz, TypedSettings typedSettings) {

		_clazz = clazz;
		_typedSettings = typedSettings;

		_overrideProperties =
			ConfigurationOverridePropertiesUtil.getOverrideProperties(
				clazz.getName());
	}

	public S createProxy() {
		return (S)ProxyUtil.newProxyInstance(
			_clazz.getClassLoader(), new Class<?>[] {_clazz}, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws InvocationTargetException {

		try {
			if (_overrideProperties != null) {
				Object overrideValue = _overrideProperties.get(
					method.getName());

				if (overrideValue != null) {
					return overrideValue;
				}
			}

			return _invokeTypedSettings(method);
		}
		catch (InvocationTargetException invocationTargetException) {
			throw invocationTargetException;
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
	}

	private Object _getValue(Class<?> returnType, String key)
		throws ReflectiveOperationException {

		if (returnType.equals(boolean.class) ||
			returnType.equals(double.class) || returnType.equals(float.class) ||
			returnType.equals(int.class) || returnType.equals(long.class)) {

			String value = _typedSettings.getValue(key, null);

			if (value == null) {
				return value;
			}
			else if (returnType.equals(boolean.class)) {
				return GetterUtil.getBoolean(value);
			}
			else if (returnType.equals(double.class)) {
				return GetterUtil.getDouble(value);
			}
			else if (returnType.equals(float.class)) {
				return GetterUtil.getFloat(value);
			}
			else if (returnType.equals(int.class)) {
				return GetterUtil.getInteger(value);
			}
			else if (returnType.equals(long.class)) {
				return GetterUtil.getLong(value);
			}
		}
		else if (returnType.equals(LocalizedValuesMap.class)) {
			LocalizedValuesMap localizedValuesMap =
				_typedSettings.getLocalizedValuesMap(key);

			if (localizedValuesMap.getDefaultValue() == null) {
				return null;
			}

			return localizedValuesMap;
		}
		else if (returnType.equals(String.class)) {
			return _typedSettings.getValue(key, null);
		}
		else if (returnType.equals(String[].class)) {
			return _typedSettings.getValues(key, null);
		}
		else if (returnType.isEnum()) {
			String value = _typedSettings.getValue(key, null);

			if (value == null) {
				return value;
			}

			Method valueOfMethod = returnType.getDeclaredMethod(
				"valueOf", String.class);

			return valueOfMethod.invoke(returnType, value);
		}

		Constructor<?> constructor = returnType.getConstructor(String.class);

		return constructor.newInstance(_typedSettings.getValue(key, null));
	}

	private Object _invokeTypedSettings(Method method)
		throws ReflectiveOperationException {

		Class<?> returnType = method.getReturnType();

		Meta.AD annotation = method.getAnnotation(Meta.AD.class);

		if ((annotation != null) && !Meta.NULL.equals(annotation.id())) {
			Object value = _getValue(returnType, annotation.id());

			if (value != null) {
				return value;
			}
		}

		if (returnType.equals(boolean.class)) {
			return _typedSettings.getBooleanValue(method.getName());
		}
		else if (returnType.equals(double.class)) {
			return _typedSettings.getDoubleValue(method.getName());
		}
		else if (returnType.equals(float.class)) {
			return _typedSettings.getFloatValue(method.getName());
		}
		else if (returnType.equals(int.class)) {
			return _typedSettings.getIntegerValue(method.getName());
		}
		else if (returnType.equals(LocalizedValuesMap.class)) {
			return _typedSettings.getLocalizedValuesMap(method.getName());
		}
		else if (returnType.equals(long.class)) {
			return _typedSettings.getLongValue(method.getName());
		}
		else if (returnType.equals(String.class)) {
			return _typedSettings.getValue(method.getName());
		}
		else if (returnType.equals(String[].class)) {
			return _typedSettings.getValues(method.getName());
		}
		else if (returnType.isEnum()) {
			Method valueOfMethod = returnType.getDeclaredMethod(
				"valueOf", String.class);

			return valueOfMethod.invoke(
				returnType, _typedSettings.getValue(method.getName()));
		}

		Constructor<?> constructor = returnType.getConstructor(String.class);

		return constructor.newInstance(
			_typedSettings.getValue(method.getName()));
	}

	private final Class<S> _clazz;
	private final Map<String, Object> _overrideProperties;
	private final TypedSettings _typedSettings;

}