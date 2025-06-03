/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.configuration;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.kernel.configuration.Filter;

/**
 * @author Eudaldo Alonso
 */
public class PatcherConfigurationUtil {

	public static String get(String key) {
		return _configuration.get(key);
	}

	public static String get(String key, Filter filter) {
		return _configuration.get(key, filter);
	}

	public static String[] getArray(String key) {
		return _configuration.getArray(key);
	}

	private static final Configuration _configuration =
		ConfigurationFactoryUtil.getConfiguration(
			PatcherConfigurationUtil.class.getClassLoader(), "portlet");

}