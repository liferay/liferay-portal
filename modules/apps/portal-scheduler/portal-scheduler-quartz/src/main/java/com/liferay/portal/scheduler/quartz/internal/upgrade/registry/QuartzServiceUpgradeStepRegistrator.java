/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.quartz.internal.upgrade.registry;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.scheduler.quartz.internal.upgrade.v1_0_1.QuartzUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(service = UpgradeStepRegistrator.class)
public class QuartzServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"0.0.1", "1.0.0",
			new com.liferay.portal.scheduler.quartz.internal.upgrade.v1_0_0.
				QuartzUpgradeProcess());

		registry.register(
			"1.0.0", "1.0.1",
			new QuartzUpgradeProcess(_companyLocalService, _jsonFactory));

		registry.register(
			"1.0.1", "1.0.2",
			new QuartzUpgradeProcess(_companyLocalService, _jsonFactory));

		registry.register(
			"1.0.2", "1.0.3",
			new com.liferay.portal.scheduler.quartz.internal.upgrade.v1_0_3.
				QuartzDBPartitionUpgradeProcess());

		registry.register(
			"1.0.3", "1.0.4",
			new QuartzUpgradeProcess(_companyLocalService, _jsonFactory));

		registry.register(
			"1.0.4", "1.0.5",
			new com.liferay.portal.scheduler.quartz.internal.upgrade.v1_0_5.
				QuartzTriggersUpgradeProcess());

		registry.register(
			"1.0.5", "1.0.6",
			new com.liferay.portal.scheduler.quartz.internal.upgrade.v1_0_6.
				QuartzDispatchGroupUpgradeProcess(
					_companyLocalService, _jsonFactory));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}