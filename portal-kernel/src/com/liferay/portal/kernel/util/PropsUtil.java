/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.internal.configuration.ConfigurationFactoryImpl;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.List;
import java.util.Properties;

/**
 * @author Brian Wing Shun Chan
 */
public class PropsUtil {

	public static boolean contains(String key) {
		return _configuration.contains(key);
	}

	public static String get(String key) {
		return _configuration.get(key);
	}

	public static String get(String key, Filter filter) {
		return _configuration.get(key, filter);
	}

	public static String[] getArray(String key) {
		return _configuration.getArray(key);
	}

	public static String[] getArray(String key, Filter filter) {
		return _configuration.getArray(key, filter);
	}

	public static List<String> getLoadedSources() {
		return _configuration.getLoadedSources();
	}

	public static Properties getProperties() {
		return getProperties(false);
	}

	public static Properties getProperties(boolean includeSystem) {
		Properties properties = _configuration.getProperties();

		if (!includeSystem) {
			return properties;
		}

		Properties systemCompanyProperties = _configuration.getProperties();

		Properties mergedProperties =
			(Properties)systemCompanyProperties.clone();

		mergedProperties.putAll(properties);

		return mergedProperties;
	}

	public static Properties getProperties(
		String prefix, boolean removePrefix) {

		return _configuration.getProperties(prefix, removePrefix);
	}

	public static void set(String key, String value) {
		_configuration.set(key, value);
	}

	private static String _getDefaultLiferayHome() {
		String defaultLiferayHome = null;

		if (ServerDetector.isJBoss()) {
			defaultLiferayHome = SystemProperties.get("jboss.home.dir") + "/..";
		}
		else if (ServerDetector.isWebLogic()) {
			defaultLiferayHome =
				SystemProperties.get("env.DOMAIN_HOME") + "/..";
		}
		else if (ServerDetector.isTomcat()) {
			defaultLiferayHome = SystemProperties.get("catalina.base") + "/..";
		}
		else {
			defaultLiferayHome = SystemProperties.get("user.dir") + "/liferay";
		}

		defaultLiferayHome = StringUtil.replace(
			defaultLiferayHome, CharPool.BACK_SLASH, CharPool.SLASH);

		defaultLiferayHome = StringUtil.replace(
			defaultLiferayHome, StringPool.DOUBLE_SLASH, StringPool.SLASH);

		if (defaultLiferayHome.endsWith("/..")) {
			int pos = defaultLiferayHome.lastIndexOf(
				CharPool.SLASH, defaultLiferayHome.length() - 4);

			if (pos != -1) {
				defaultLiferayHome = defaultLiferayHome.substring(0, pos);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Default Liferay home " + defaultLiferayHome);
		}

		return defaultLiferayHome;
	}

	private static final Log _log = LogFactoryUtil.getLog(PropsUtil.class);

	private static final Configuration _configuration;

	static {

		// Default liferay home directory

		SystemProperties.set(
			PropsKeys.DEFAULT_LIFERAY_HOME, _getDefaultLiferayHome());

		// Liferay home directory

		_configuration = ConfigurationFactoryImpl.CONFIGURATION_PORTAL;

		String liferayHome = _configuration.get(PropsKeys.LIFERAY_HOME);

		if (_log.isDebugEnabled()) {
			_log.debug("Configured Liferay home " + liferayHome);
		}

		SystemProperties.set(PropsKeys.LIFERAY_HOME, liferayHome);

		// Ehcache disk directory

		SystemProperties.set(
			"ehcache.disk.store.dir", liferayHome + "/data/ehcache");
	}

}