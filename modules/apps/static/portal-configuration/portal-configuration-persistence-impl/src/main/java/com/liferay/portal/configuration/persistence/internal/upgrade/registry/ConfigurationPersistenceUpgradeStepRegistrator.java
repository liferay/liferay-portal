/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.internal.upgrade.registry;

import com.liferay.portal.configuration.persistence.internal.upgrade.v2_0_0.ConfigurationDBPartitionUpgradeProcess;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sam Ziemer
 */
@Component(service = UpgradeStepRegistrator.class)
public class ConfigurationPersistenceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"0.0.1", "0.0.2",
			UpgradeProcessFactory.alterColumnType(
				"Configuration_", "configurationId", "VARCHAR(512) not null"));

		registry.register(
			"0.0.2", "1.0.0",
			new com.liferay.portal.configuration.persistence.internal.upgrade.
				v1_0_0.ConfigurationUpgradeProcess());

		registry.register(
			"1.0.0", "1.0.1",
			new com.liferay.portal.configuration.persistence.internal.upgrade.
				v1_0_1.ConfigurationUpgradeProcess());

		registry.register(
			"1.0.1", "1.0.2",
			UpgradeProcessFactory.alterColumnType(
				"Configuration_", "configurationId", "VARCHAR(512) not null"));

		registry.register(
			"1.0.2", "1.0.3",
			new com.liferay.portal.configuration.persistence.internal.upgrade.
				v1_0_3.ConfigurationUpgradeProcess(_configurationAdmin));

		registry.register("1.0.3", "1.0.4", new DummyUpgradeProcess());

		registry.register(
			"1.0.4", "2.0.0", new ConfigurationDBPartitionUpgradeProcess());

		registry.register(
			"2.0.0", "2.0.1",
			new com.liferay.portal.configuration.persistence.internal.upgrade.
				v2_0_1.ConfigurationUpgradeProcess(
					_configurationAdmin, _groupLocalService));
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private GroupLocalService _groupLocalService;

}