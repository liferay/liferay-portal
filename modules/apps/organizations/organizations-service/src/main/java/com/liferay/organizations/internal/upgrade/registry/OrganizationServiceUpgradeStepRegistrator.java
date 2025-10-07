/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.internal.upgrade.registry;

import com.liferay.organizations.internal.configuration.OrganizationTypeConfiguration;
import com.liferay.organizations.internal.upgrade.v1_0_0.OrganizationTypesConfigurationUpgradeProcess;
import com.liferay.portal.configuration.persistence.upgrade.ConfigurationUpgradeStepFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = UpgradeStepRegistrator.class)
public class OrganizationServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new OrganizationTypesConfigurationUpgradeProcess(
				_configurationAdmin));

		registry.register(
			"1.0.0", "1.0.1",
			_configurationUpgradeStepFactory.createUpgradeStep(
				"com.liferay.organizations.service.internal.configuration." +
					"OrganizationTypeConfiguration",
				OrganizationTypeConfiguration.class.getName()));
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationUpgradeStepFactory _configurationUpgradeStepFactory;

}