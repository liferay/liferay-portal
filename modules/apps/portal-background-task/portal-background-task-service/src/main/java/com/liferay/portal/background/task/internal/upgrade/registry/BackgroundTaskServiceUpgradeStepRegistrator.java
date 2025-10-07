/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal.upgrade.registry;

import com.liferay.portal.background.task.internal.upgrade.v1_0_0.SchemaUpgradeProcess;
import com.liferay.portal.background.task.internal.upgrade.v1_0_0.UpgradeKernelPackage;
import com.liferay.portal.background.task.internal.upgrade.v2_0_0.util.BackgroundTaskTable;
import com.liferay.portal.background.task.internal.upgrade.v2_0_1.BackgroundTaskCompanyIdUpgradeProcess;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocalManager;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina Rodríguez
 */
@Component(service = UpgradeStepRegistrator.class)
public class BackgroundTaskServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"0.0.1", "0.0.2", new SchemaUpgradeProcess(),
			new UpgradeKernelPackage());

		registry.register(
			"0.0.2", "1.0.0",
			UpgradeProcessFactory.alterColumnType(
				"BackgroundTask", "name", "VARCHAR(255) null"),
			UpgradeProcessFactory.alterColumnName(
				"BackgroundTask", "taskContext", "taskContextMap TEXT null"));

		registry.register(
			"1.0.0", "2.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {BackgroundTaskTable.class}));

		registry.register(
			"2.0.0", "2.0.1",
			new BackgroundTaskCompanyIdUpgradeProcess(
				_backgroundTaskThreadLocalManager));
	}

	@Reference
	private BackgroundTaskThreadLocalManager _backgroundTaskThreadLocalManager;

}