/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.upgrade.v1_0_0;

import com.liferay.portal.configuration.persistence.upgrade.ConfigurationUpgradeStepFactory;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author me
 */
public class ElasticsearchUpgradeProcessUtil {

	public static void doUpgrade(
			ConfigurationAdmin configurationAdmin,
			ConfigurationUpgradeStepFactory configurationUpgradeStepFactory)
		throws Exception {

		ElasticsearchConfigurationUpgradeProcess upgradeProcess =
			new ElasticsearchConfigurationUpgradeProcess(
				configurationAdmin, configurationUpgradeStepFactory);

		upgradeProcess.doUpgrade();
	}

}
