/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.upgrade.registry;

import com.liferay.feature.flag.web.internal.upgrade.v1_0_0.FeatureFlagUpgradeProcess;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@Component(service = UpgradeStepRegistrator.class)
public class FeatureFlagUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		System.out.println("Registering upgrade");
		System.out.println("Dependencies are");
		System.out.println(_companyLocalService);
		System.out.println(_portalPreferencesLocalService);

		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new DummyUpgradeStep());

		registry.register(
			"1.0.0", "1.0.1",
			new FeatureFlagUpgradeProcess(
				_companyLocalService, _portalPreferencesLocalService, "1.0.0"));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private PortalPreferencesLocalService _portalPreferencesLocalService;

}