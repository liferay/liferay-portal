/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.upgrade.registry;

import com.liferay.exportimport.internal.upgrade.v1_0_0.PublisherRequestUpgradeProcess;
import com.liferay.exportimport.internal.upgrade.v1_0_1.SystemEventsUpgradeProcess;
import com.liferay.exportimport.internal.upgrade.v1_0_1.UpgradeBackgroundTaskExecutorClassNames;
import com.liferay.exportimport.internal.upgrade.v1_0_2.ExportImportServiceConfigurationUpgradeProcess;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Kocsis
 */
@Component(service = UpgradeStepRegistrator.class)
public class ExportImportServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "0.0.2",
			new com.liferay.exportimport.internal.upgrade.v1_0_0.
				UpgradeBackgroundTaskExecutorClassNames());

		registry.register(
			"0.0.2", "1.0.0",
			new PublisherRequestUpgradeProcess(
				_exportImportConfigurationLocalService, _groupLocalService,
				_schedulerEngineHelper, _userLocalService));

		registry.register(
			"1.0.0", "1.0.0.step-1",
			new SystemEventsUpgradeProcess(
				_groupLocalService, _systemEventLocalService));

		registry.register(
			"1.0.0.step-1", "1.0.1",
			new UpgradeBackgroundTaskExecutorClassNames());

		registry.register(
			"1.0.1", "1.0.2",
			new ExportImportServiceConfigurationUpgradeProcess(
				_companyLocalService, _configurationAdmin,
				_configurationProvider));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.portal.background.task.service)(&(release.schema.version>=2.0.0)(!(release.schema.version>=3.0.0))))"
	)
	private Release _release;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private SystemEventLocalService _systemEventLocalService;

	@Reference
	private UserLocalService _userLocalService;

}