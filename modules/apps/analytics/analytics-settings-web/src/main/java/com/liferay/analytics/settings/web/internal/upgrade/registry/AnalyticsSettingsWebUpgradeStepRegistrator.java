/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.upgrade.registry;

import com.liferay.analytics.settings.web.internal.upgrade.v1_0_2.AnalyticsDispatchTriggersUpgradeProcess;
import com.liferay.analytics.settings.web.internal.upgrade.v1_0_3.AnalyticsAdministratorUserUpgradeProcess;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.executor.DispatchTaskExecutor;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rachael Koestartyo
 */
@Component(service = UpgradeStepRegistrator.class)
public class AnalyticsSettingsWebUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new com.liferay.analytics.settings.web.internal.upgrade.v1_0_0.
				AnalyticsConfigurationPreferencesUpgradeProcess(
					_configurationAdmin));

		registry.register(
			"1.0.0", "1.0.1",
			new com.liferay.analytics.settings.web.internal.upgrade.v1_0_1.
				AnalyticsConfigurationPreferencesUpgradeProcess(
					_companyLocalService, _configurationAdmin));

		registry.register(
			"1.0.1", "1.0.2",
			new AnalyticsDispatchTriggersUpgradeProcess(
				_configurationAdmin, _dispatchLogLocalService,
				_dispatchTaskExecutor, _dispatchTriggerLocalService,
				_userLocalService));

		registry.register(
			"1.0.2", "1.0.3",
			new AnalyticsAdministratorUserUpgradeProcess(
				_companyLocalService, _configurationAdmin, _roleLocalService,
				_userLocalService));

		registry.register(
			"1.0.3", "1.0.4",
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"update DispatchTrigger set dispatchTaskClusterMode = ",
					DispatchTaskClusterMode.SINGLE_NODE_PERSISTED.getMode(),
					" where dispatchTaskClusterMode = ",
					DispatchTaskClusterMode.ALL_NODES.getMode(),
					" and dispatchTaskExecutorType in ",
					"('export-analytics-asset-entities', ",
					"'export-analytics-dxp-entities')")));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private DispatchLogLocalService _dispatchLogLocalService;

	@Reference(
		target = "(dispatch.task.executor.type=export-analytics-dxp-entities)"
	)
	private DispatchTaskExecutor _dispatchTaskExecutor;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}