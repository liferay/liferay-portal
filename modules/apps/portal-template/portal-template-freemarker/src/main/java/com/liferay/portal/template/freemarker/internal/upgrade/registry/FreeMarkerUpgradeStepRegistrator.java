/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal.upgrade.registry;

import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.template.freemarker.internal.upgrade.v1_0_0.FreeMarkerEngineConfigurationUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(service = UpgradeStepRegistrator.class)
public class FreeMarkerUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.registerReleaseCreationUpgradeSteps(
			new FreeMarkerEngineConfigurationUpgradeProcess(
				_configurationAdmin));

		registry.register("0.0.1", "1.0.0", new DummyUpgradeStep());
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

}