/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.upgrade;

import com.liferay.portal.kernel.util.KeyValuePair;

/**
 * @author Drew Brokke
 */
public interface PrefsPropsToConfigurationUpgradeHelper {

	public void mapConfigurations(
			Class<?> configurationClass, KeyValuePair... keyValuePairs)
		throws Exception;

	public void mapConfigurations(
			long companyId, Class<?> configurationClass,
			KeyValuePair... keyValuePairs)
		throws Exception;

}