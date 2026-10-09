/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.file.install.properties.ConfigurationHandler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Constants;

/**
 * @author Shuyang Zhou
 */
public class ConfigurationOverridePropertiesUtil {

	public static Map<String, Object> getOverrideProperties(String pid) {
		return _overridePropertiesMap.get(pid);
	}

	public static Map<String, Map<String, Object>> getOverridePropertiesMap() {
		return _overridePropertiesMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationOverridePropertiesUtil.class);

	private static final Map<String, Map<String, Object>>
		_overridePropertiesMap;

	static {
		Properties properties = PropsUtil.getProperties(
			"configuration.override.", true);

		Map<String, Map<String, Object>> overridePropertiesMap =
			new HashMap<>();

		for (String key : properties.stringPropertyNames()) {
			int index = key.indexOf(CharPool.UNDERLINE);

			if (index <= 0) {
				continue;
			}

			Map<String, Object> overrideProperties =
				overridePropertiesMap.computeIfAbsent(
					key.substring(0, index), pid -> new HashMap<>());

			try {
				String valueString = properties.getProperty(key);

				Object value = ConfigurationHandler.read(valueString);

				if ((value == null) && !valueString.isEmpty()) {
					_log.error(
						StringBundler.concat(
							"Key ", key,
							" was overridden with incorrectly formatted ",
							"content"));
				}
				else {
					overrideProperties.put(key.substring(index + 1), value);
				}
			}
			catch (IOException ioException) {
				_log.error("Unable to parse property", ioException);
			}
		}

		for (Map.Entry<String, Map<String, Object>> entry :
				overridePropertiesMap.entrySet()) {

			Map<String, Object> map = entry.getValue();

			map.put(Constants.SERVICE_PID, entry.getKey());

			entry.setValue(Collections.unmodifiableMap(map));
		}

		_overridePropertiesMap = Collections.unmodifiableMap(
			overridePropertiesMap);
	}

}