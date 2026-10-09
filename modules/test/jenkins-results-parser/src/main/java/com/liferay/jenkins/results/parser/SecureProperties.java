/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Yoo
 */
public class SecureProperties extends Properties {

	public SecureProperties() {
	}

	public SecureProperties(Properties properties) {
		if (properties instanceof SecureProperties) {
			SecureProperties secureProperties = (SecureProperties)properties;

			for (Object key : properties.keySet()) {
				put(key, secureProperties.get(key, false));
			}

			return;
		}

		for (Object key : properties.keySet()) {
			put(key, properties.get(key));
		}
	}

	@Override
	public synchronized Object get(Object key) {
		return get(key, true);
	}

	public synchronized Object get(Object key, boolean getSecret) {
		String value = _getReferencedValue(new ArrayList<>(), (String)key);

		if (!getSecret) {
			return value;
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(value) &&
			SecretsUtil.isSecretProperty(value)) {

			value = SecretsUtil.getSecret(value);

			put(key, value);
		}

		return value;
	}

	@Override
	public String getProperty(String key) {
		return (String)get(key);
	}

	private String _getReferencedValue(List<String> previousKeys, String key) {
		if (previousKeys.contains(key)) {
			if (previousKeys.size() > 1) {
				StringBuilder sb = new StringBuilder();

				sb.append("Found circular property reference chain:\n");

				for (String previousKey : previousKeys) {
					sb.append(previousKey);
					sb.append(" -> ");
				}

				sb.append(key);

				throw new IllegalStateException(sb.toString());
			}

			return JenkinsResultsParserUtil.combine("${", key, "}");
		}

		previousKeys.add(key);

		if (!containsKey(key)) {
			return key;
		}

		String value = JenkinsResultsParserUtil.getFilteredPropertyValue(
			(String)super.get(key));

		Matcher matcher = _nestedPropertyPattern.matcher(value);

		String newValue = value;

		while (matcher.find()) {
			String propertyGroup = matcher.group(0);

			String propertyName = matcher.group(1);

			if (!containsKey(propertyName)) {
				continue;
			}

			newValue = newValue.replace(
				propertyGroup,
				_getReferencedValue(
					new ArrayList<>(previousKeys), propertyName));
		}

		return newValue;
	}

	private static final Pattern _nestedPropertyPattern = Pattern.compile(
		"\\$\\{([^\\}]+)\\}");

}