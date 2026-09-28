/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.upgrade.registry;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.security.script.management.web.internal.upgrade.v1_0_0.ScriptManagementConfigurationUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = UpgradeStepRegistrator.class)
public class ScriptManagementWebUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new ScriptManagementConfigurationUpgradeProcess(
				_configurationProvider, _jsonFactory));

		registry.register(
			"1.0.0", "1.1.0",
			new com.liferay.portal.security.script.management.web.internal.
				upgrade.v1_1_0.ScriptManagementConfigurationUpgradeProcess(
					_configurationProvider, _jsonFactory));

		registry.register(
			"1.1.0", "1.2.0",
			new com.liferay.portal.security.script.management.web.internal.
				upgrade.v1_2_0.ScriptManagementConfigurationUpgradeProcess(
					_configurationProvider, _jsonFactory));
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSONFactory _jsonFactory;

}