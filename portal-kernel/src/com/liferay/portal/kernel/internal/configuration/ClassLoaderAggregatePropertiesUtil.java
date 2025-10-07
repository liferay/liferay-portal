/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.configuration;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.EnvPropertiesUtil;
import com.liferay.portal.kernel.util.SystemProperties;

/**
 * @author Shuyang Zhou
 */
public class ClassLoaderAggregatePropertiesUtil {

	public static ClassLoaderAggregateProperties create(
		ClassLoader classLoader, String componentName) {

		SystemProperties.set("base.path", ".");

		ClassLoaderAggregateProperties classLoaderAggregateProperties =
			new ClassLoaderAggregateProperties(classLoader, componentName);

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Properties for ", componentName, " loaded from ",
					classLoaderAggregateProperties.loadedSources()));
		}

		EnvPropertiesUtil.loadEnvOverrides(
			_ENV_OVERRIDE_PREFIX, classLoaderAggregateProperties::setProperty);

		return classLoaderAggregateProperties;
	}

	private static final String _ENV_OVERRIDE_PREFIX = "LIFERAY_";

	private static final Log _log = LogFactoryUtil.getLog(
		ClassLoaderAggregatePropertiesUtil.class);

}