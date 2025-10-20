/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.internal.upgrade.registry;

import com.liferay.portal.kernel.upgrade.BaseUuidUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Igor Beslic
 */
@Component(service = UpgradeStepRegistrator.class)
public class DispatchServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "2.0.0",
			UpgradeProcessFactory.addColumns(
				"DispatchTrigger", "endDate DATE null", "startDate DATE null"),
			UpgradeProcessFactory.alterColumnName(
				"DispatchTrigger", "typeSettings", "taskSettings TEXT null"),
			UpgradeProcessFactory.alterColumnName(
				"DispatchTrigger", "type_", "taskType VARCHAR(75) null"));

		registry.register(
			"2.0.0", "2.1.0",
			UpgradeProcessFactory.addColumns(
				"DispatchTrigger", "overlapAllowed BOOLEAN"));

		registry.register(
			"2.1.0", "3.0.0",
			UpgradeProcessFactory.alterColumnName(
				"DispatchTrigger", "taskType",
				"taskExecutorType VARCHAR(75) null"));

		registry.register(
			"3.0.0", "3.1.0",
			UpgradeProcessFactory.addColumns(
				"DispatchTrigger", "taskClusterMode INTEGER"));

		registry.register(
			"3.1.0", "3.1.1",
			UpgradeProcessFactory.runSQL(
				"delete from ResourceAction where name = '90' and actionId = " +
					"'ADD_DISPATCH_TRIGGER'"));

		registry.register(
			"3.1.1", "4.0.0",
			UpgradeProcessFactory.alterColumnName(
				"DispatchTrigger", "taskClusterMode",
				"dispatchTaskClusterMode INTEGER null"),
			UpgradeProcessFactory.alterColumnName(
				"DispatchTrigger", "taskExecutorType",
				"dispatchTaskExecutorType VARCHAR(75) null"),
			UpgradeProcessFactory.alterColumnName(
				"DispatchTrigger", "taskSettings",
				"dispatchTaskSettings TEXT null"));

		registry.register(
			"4.0.0", "4.0.1",
			UpgradeProcessFactory.alterColumnType(
				"DispatchTrigger", "dispatchTaskExecutorType",
				"VARCHAR(75) null"),
			UpgradeProcessFactory.alterColumnType(
				"DispatchTrigger", "dispatchTaskSettings", "TEXT null"));

		registry.register(
			"4.0.1", "4.1.0",
			UpgradeProcessFactory.addColumns(
				"DispatchTrigger", "externalReferenceCode VARCHAR(75) null"));

		registry.register(
			"4.1.0", "4.2.0",
			new BaseUuidUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"DispatchTrigger"};
				}

			});

		registry.register(
			"4.2.0", "4.3.0",
			new com.liferay.dispatch.internal.upgrade.v4_3_0.
				DispatchTriggerUpgradeProcess());
	}

}