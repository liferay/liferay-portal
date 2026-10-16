/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.internal.upgrade.registry;

import com.liferay.ai.creator.openai.internal.upgrade.v1_1_0.AICreatorOpenAIConfigurationUpgradeProcess;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = UpgradeStepRegistrator.class)
public class AICreatorOpenAIUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register("0.0.1", "1.0.0", new DummyUpgradeStep());

		registry.register(
			"1.0.0", "1.1.0",
			new AICreatorOpenAIConfigurationUpgradeProcess(
				_configurationAdmin, _configurationProvider));

		registry.register(
			"1.1.0", "1.2.0",
			new com.liferay.ai.creator.openai.internal.upgrade.v1_2_0.
				AICreatorOpenAIConfigurationUpgradeProcess(
					_configurationAdmin));
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

}