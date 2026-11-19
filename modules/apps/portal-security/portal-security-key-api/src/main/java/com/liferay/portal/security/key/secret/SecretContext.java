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

	public static SecretContext fromConfiguration(
		String key, Dictionary<String, ?> properties) {

		if (properties == null) {
			throw new IllegalArgumentException("Properties are null");
		}

		Map<String, Object> propertiesMap = new HashMap<>();

		for (String name : Collections.list(properties.keys())) {
			propertiesMap.put(name, properties.get(name));
		}

		return fromConfiguration(key, propertiesMap);
	}

	public static SecretContext fromConfiguration(
		String key, Map<String, ?> properties) {

		if (properties == null) {
			throw new IllegalArgumentException("Properties are null");
		}

		return new SecretContext(key, Collections.unmodifiableMap(properties));
	}

	public String getKey() {
		return _key;
	}

	public Map<String, Object> getProperties() {
		return _properties;
	}

	private SecretContext(String key, Map<String, Object> properties) {
		if (Validator.isNull(key)) {
			throw new IllegalArgumentException("Key is null");
		}

		_key = key;
		_properties = properties;
	}

	private final String _key;
	private final Map<String, Object> _properties;

}