/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.settings.internal;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.settings.SettingsDescriptor;

import java.lang.reflect.Method;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Iván Zaera
 */
public class ConfigurationBeanClassSettingsDescriptor
	implements SettingsDescriptor {

	public ConfigurationBeanClassSettingsDescriptor(
		Class<?> configurationBeanClass) {

		_configurationBeanClass = configurationBeanClass;
	}

	@Override
	public Set<String> getAllKeys() {
		return new HashSet<>(
			_allKeysDCLSingleton.getSingleton(this::_createAllKeys));
	}

	@Override
	public Set<String> getMultiValuedKeys() {
		return new HashSet<>(
			_multiValuedKeysDCLSingleton.getSingleton(
				this::_createMultiValuedKeys));
	}

	private Set<String> _createAllKeys() {
		Set<String> allKeys = new HashSet<>();

		for (Method method : _configurationBeanClass.getMethods()) {
			allKeys.add(method.getName());
		}

		return allKeys;
	}

	private Set<String> _createMultiValuedKeys() {
		Set<String> multiValuedKeys = new HashSet<>();

		for (Method method : _configurationBeanClass.getMethods()) {
			if (method.getReturnType() != String[].class) {
				continue;
			}

			multiValuedKeys.add(method.getName());
		}

		return multiValuedKeys;
	}

	private final DCLSingleton<Set<String>> _allKeysDCLSingleton =
		new DCLSingleton<>();
	private final Class<?> _configurationBeanClass;
	private final DCLSingleton<Set<String>> _multiValuedKeysDCLSingleton =
		new DCLSingleton<>();

}