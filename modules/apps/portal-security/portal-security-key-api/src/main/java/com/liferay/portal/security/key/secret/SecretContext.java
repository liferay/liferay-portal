/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.secret;

import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Caio Farias
 */
public class SecretContext {

	public static SecretContext createConfigurationSecretContext(
		String key, Dictionary<String, ?> properties) {

		if (properties == null) {
			throw new IllegalArgumentException("Properties are null");
		}

		Map<String, Object> propertiesMap = new HashMap<>();

		for (String name : Collections.list(properties.keys())) {
			propertiesMap.put(name, properties.get(name));
		}

		return createConfigurationSecretContext(key, propertiesMap);
	}

	public static SecretContext createConfigurationSecretContext(
		String key, Map<String, ?> properties) {

		if (properties == null) {
			throw new IllegalArgumentException("Properties are null");
		}

		return new SecretContext(
			key, Collections.unmodifiableMap(new HashMap<>(properties)),
			Type.CONFIGURATION);
	}

	public String getKey() {
		return _key;
	}

	public Map<String, Object> getProperties() {
		return _properties;
	}

	public Type getType() {
		return _type;
	}

	public enum Type {

		COMPANY, CONFIGURATION, SETTINGS

	}

	private SecretContext(
		String key, Map<String, Object> properties, Type type) {

		if (Validator.isNull(key)) {
			throw new IllegalArgumentException("Key is null");
		}

		_key = key;
		_properties = properties;
		_type = type;
	}

	private final String _key;
	private final Map<String, Object> _properties;
	private final Type _type;

}