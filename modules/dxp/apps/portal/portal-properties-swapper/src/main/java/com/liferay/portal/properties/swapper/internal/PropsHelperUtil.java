/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.properties.swapper.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.InvalidPathException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Shuyang Zhou
 */
public class PropsHelperUtil {

	public static boolean isCustomized(String key) {
		for (Map.Entry<String, Properties> entry : _propertiesMap.entrySet()) {
			Properties properties = entry.getValue();

			if (properties.containsKey(key)) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Found customized value for key ", key, " in ",
							entry.getKey()));
				}

				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PropsHelperUtil.class);

	private static final Map<String, Properties> _propertiesMap =
		new LinkedHashMap<>();

	static {
		String[] propertiesLocations = PropsUtil.getArray(
			"include-and-override");

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		for (int i = propertiesLocations.length - 1; i >= 0; i--) {
			String propertiesLocation = propertiesLocations[i];

			try {
				File propertiesFile = new File(propertiesLocation);

				if (propertiesFile.exists()) {
					try (InputStream inputStream = new FileInputStream(
							propertiesFile)) {

						Properties properties = new Properties();

						properties.load(inputStream);

						_propertiesMap.put(propertiesLocation, properties);
					}
				}
				else {
					try (InputStream inputStream =
							classLoader.getResourceAsStream(
								propertiesLocation)) {

						if (inputStream != null) {
							Properties properties = new Properties();

							properties.load(inputStream);

							_propertiesMap.put(propertiesLocation, properties);
						}
					}
				}
			}
			catch (IOException ioException) {
				_log.error("Unable to read " + propertiesLocation, ioException);
			}
			catch (InvalidPathException invalidPathException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to parse " + propertiesLocation,
						invalidPathException);
				}
			}
		}
	}

}