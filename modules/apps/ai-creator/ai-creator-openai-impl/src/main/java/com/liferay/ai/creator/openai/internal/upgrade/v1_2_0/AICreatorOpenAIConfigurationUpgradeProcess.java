/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.internal.upgrade.v1_2_0;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Akhash Ramprakash
 */
public class AICreatorOpenAIConfigurationUpgradeProcess extends UpgradeProcess {

	public AICreatorOpenAIConfigurationUpgradeProcess(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(enableDALLEToCreateImages=*)");

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			properties.put(
				"enableOpenAIImageGeneration",
				properties.remove("enableDALLEToCreateImages"));

			configuration.update(properties);
		}
	}

	private final ConfigurationAdmin _configurationAdmin;

}