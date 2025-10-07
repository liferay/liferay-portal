/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.log4j;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.internal.log4j.Log4jConfigUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Brian Wing Shun Chan
 * @author Tomas Polesovsky
 */
public class Log4JUtil {

	public static void configureLog4J(ClassLoader classLoader) {
		configureLog4J(classLoader.getResource("META-INF/portal-log4j.xml"));

		try {
			Enumeration<URL> enumeration = classLoader.getResources(
				"META-INF/portal-log4j-ext.xml");

			while (enumeration.hasMoreElements()) {
				configureLog4J(enumeration.nextElement());
			}
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to load portal-log4j-ext.xml", ioException);
			}
		}
	}

	public static void configureLog4J(URL url) {
		if (url == null) {
			return;
		}

		String urlContent = null;

		try {
			urlContent = URLUtil.toString(url);
		}
		catch (Exception exception) {
			_log.error(exception);

			return;
		}

		urlContent = StringUtil.replace(
			urlContent, "@liferay.home@", _getLiferayHome());

		Map<String, String> priorities = null;

		if (ServerDetector.getServerId() != null) {
			priorities = Log4jConfigUtil.configureLog4J(urlContent);
		}
		else {
			priorities = Log4jConfigUtil.configureLog4J(
				urlContent, "TEXT_FILE", "XML_FILE");
		}

		for (Map.Entry<String, String> entry : priorities.entrySet()) {
			Logger jdkLogger = Logger.getLogger(entry.getKey());

			jdkLogger.setLevel(Log4jConfigUtil.getJDKLevel(entry.getValue()));
		}
	}

	public static File getCompanyLogDirectory(long companyId) {
		return Log4jConfigUtil.getCompanyLogDirectory(companyId);
	}

	public static Map<String, String> getCustomLogSettings() {
		return new HashMap<>(_customLogSettings);
	}

	public static Map<String, String> getPriorities() {
		return Log4jConfigUtil.getPriorities();
	}

	public static void setLevel(String name, String priority, boolean custom) {
		Log4jConfigUtil.setLevel(name, priority);

		Logger jdkLogger = Logger.getLogger(name);

		jdkLogger.setLevel(Log4jConfigUtil.getJDKLevel(priority));

		if (custom) {
			_customLogSettings.put(name, priority);
		}
	}

	public static void shutdownLog4J() {
		Log4jConfigUtil.shutdownLog4J();
	}

	private static String _escapeXMLAttribute(String s) {
		return StringUtil.replace(
			s,
			new char[] {
				CharPool.AMPERSAND, CharPool.APOSTROPHE, CharPool.LESS_THAN,
				CharPool.QUOTE
			},
			new String[] {"&amp;", "&apos;", "&lt;", "&quot;"});
	}

	private static String _getLiferayHome() {
		if (_liferayHome == null) {
			_liferayHome = _escapeXMLAttribute(
				PropsUtil.get(PropsKeys.LIFERAY_HOME));
		}

		return _liferayHome;
	}

	private static final Log _log = LogFactoryUtil.getLog(Log4JUtil.class);

	private static final Map<String, String> _customLogSettings =
		new ConcurrentHashMap<>();
	private static String _liferayHome;

}