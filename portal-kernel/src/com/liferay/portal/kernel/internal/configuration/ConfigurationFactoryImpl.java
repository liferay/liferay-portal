/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.configuration;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactory;
import com.liferay.portal.kernel.util.AggregateClassLoader;

/**
 * @author Brian Wing Shun Chan
 */
public class ConfigurationFactoryImpl implements ConfigurationFactory {

	public static final Configuration CONFIGURATION_PORTAL;

	static {
		ClassLoader classLoader =
			ConfigurationFactoryImpl.class.getClassLoader();

		Class<?> clazz = classLoader.getClass();

		ClassLoader classLoaderClassLoader = clazz.getClassLoader();

		if (classLoaderClassLoader != null) {
			classLoader = AggregateClassLoader.getAggregateClassLoader(
				classLoader, classLoaderClassLoader);
		}

		CONFIGURATION_PORTAL = new ConfigurationImpl(classLoader, "portal");
	}

	@Override
	public Configuration getConfiguration(
		ClassLoader classLoader, String name) {

		if (classLoader.getResource(name + ".properties") == null) {
			return null;
		}

		return new ConfigurationImpl(classLoader, name);
	}

}