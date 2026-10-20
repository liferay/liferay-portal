/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.settings.internal;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.settings.BaseSettings;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.settings.LocationVariableResolver;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.GetterUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Iván Zaera
 */
public class ConfigurationBeanSettings
	extends BaseSettings implements Settings {

	public ConfigurationBeanSettings(
		LocationVariableResolver locationVariableResolver,
		Object configurationBean, Settings parentSettings) {

		super(parentSettings);

		if (locationVariableResolver == null) {
			throw new NullPointerException(
				"Location variable resolver is null");
		}

		if (configurationBean == null) {
			throw new NullPointerException("Configuration bean is null");
		}

		_locationVariableResolver = locationVariableResolver;
		_configurationBean = configurationBean;

		Class<?> clazz = configurationBean.getClass();

		for (Method method : clazz.getMethods()) {
			_methods.put(method.getName(), method);
		}
	}

	@Override
	protected String doGetValue(String key) {
		Object object = _getProperty(key);

		if (object == null) {
			return null;
		}

		String value = null;

		if (object instanceof LocalizedValuesMap) {
			LocalizedValuesMap localizedValuesMap = (LocalizedValuesMap)object;

			value = localizedValuesMap.getDefaultValue();
		}
		else {
			value = object.toString();
		}

		if (_locationVariableResolver.isLocationVariable(value)) {
			return _locationVariableResolver.resolve(value);
		}

		return value;
	}

	@Override
	protected String[] doGetValues(String key) {
		Object object = _getProperty(key);

		if (object == null) {
			return null;
		}

		return GetterUtil.getStringValues(object);
	}

	private Object _getProperty(String key) {
		try {
			Method method = _methods.get(key);

			if (method == null) {
				return null;
			}

			return method.invoke(_configurationBean);
		}
		catch (InvocationTargetException invocationTargetException) {
			throw new SystemException(
				"Unable to read property " + key, invocationTargetException);
		}
		catch (IllegalAccessException illegalAccessException) {
			throw new SystemException(
				"Unable to read property " + key, illegalAccessException);
		}
	}

	private final Object _configurationBean;
	private final LocationVariableResolver _locationVariableResolver;
	private final Map<String, Method> _methods = new HashMap<>();

}