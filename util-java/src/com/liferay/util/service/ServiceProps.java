/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.service;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;

import java.util.Properties;

/**
 * @author Brian Wing Shun Chan
 */
public class ServiceProps {

	public static boolean contains(String key) {
		return _serviceProps._configuration.contains(key);
	}

	public static String get(String key) {
		return _serviceProps._configuration.get(key);
	}

	public static String get(String key, Filter filter) {
		return _serviceProps._configuration.get(key, filter);
	}

	public static String[] getArray(String key) {
		return _serviceProps._configuration.getArray(key);
	}

	public static String[] getArray(String key, Filter filter) {
		return _serviceProps._configuration.getArray(key, filter);
	}

	public static Properties getProperties() {
		return _serviceProps._configuration.getProperties();
	}

	public static void set(String key, String value) {
		_serviceProps._configuration.set(key, value);
	}

	private ServiceProps() {
		_configuration = ConfigurationFactoryUtil.getConfiguration(
			PortletClassLoaderUtil.getClassLoader(), "service");
	}

	private static final ServiceProps _serviceProps = new ServiceProps();

	private final Configuration _configuration;

}