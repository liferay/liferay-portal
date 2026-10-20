/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.settings.internal;

import com.liferay.portal.kernel.settings.BaseSettings;
import com.liferay.portal.kernel.settings.LocationVariableResolver;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Jorge Ferrer
 * @author Iván Zaera
 */
public class PropertiesSettings extends BaseSettings {

	public PropertiesSettings(
		LocationVariableResolver locationVariableResolver,
		Properties properties) {

		this(locationVariableResolver, properties, null);
	}

	@SuppressWarnings("unchecked")
	public PropertiesSettings(
		LocationVariableResolver locationVariableResolver,
		Properties properties, Settings parentSettings) {

		super(parentSettings);

		_locationVariableResolver = locationVariableResolver;

		_properties = new HashMap<>((Map)properties);
	}

	@Override
	protected String doGetValue(String key) {
		return readProperty(key);
	}

	@Override
	protected String[] doGetValues(String key) {
		return StringUtil.split(doGetValue(key));
	}

	protected String readProperty(String key) {
		String value = _properties.get(key);

		if (_locationVariableResolver.isLocationVariable(value)) {
			return _locationVariableResolver.resolve(value);
		}

		return value;
	}

	private final LocationVariableResolver _locationVariableResolver;
	private final Map<String, String> _properties;

}