/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.consent.management.platform.integration.internal.upgrade.registry;

import com.liferay.consent.management.platform.integration.internal.upgrade.v1_1_0.ConsentManagementPlatformConfigurationUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Christian Moura
 */
@Component(service = UpgradeStepRegistrator.class)
public class ConsentManagementPlatformIntegrationUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"1.0.0", "1.1.0",
			new ConsentManagementPlatformConfigurationUpgradeProcess());
	}

}