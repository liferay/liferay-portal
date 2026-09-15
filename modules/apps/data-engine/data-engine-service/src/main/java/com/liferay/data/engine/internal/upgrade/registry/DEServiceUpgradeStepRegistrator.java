/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.internal.upgrade.registry;

import com.liferay.data.engine.internal.upgrade.v1_0_0.SchemaUpgradeProcess;
import com.liferay.data.engine.internal.upgrade.v2_0_0.UpgradeCompanyId;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jeyvison Nascimento
 */
@Component(service = UpgradeStepRegistrator.class)
public class DEServiceUpgradeStepRegistrator implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register("1.0.0", "1.1.0", new SchemaUpgradeProcess());

		registry.register("1.1.0", "1.1.1", new DummyUpgradeStep());

		registry.register("1.1.1", "2.0.0", new UpgradeCompanyId());

		registry.register(
			"2.0.0", "2.1.0",
			UpgradeProcessFactory.addColumns(
				"DEDataDefinitionFieldLink", "createDate DATE null",
				"modifiedDate DATE null", "lastPublishDate DATE null"));

		registry.register(
			"2.1.0", "2.1.1",
			UpgradeProcessFactory.alterColumnType(
				"DEDataDefinitionFieldLink", "fieldName", "VARCHAR(75) null"));

		registry.register(
			"2.1.1", "2.1.2",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"DEDataDefinitionFieldLink", "DEDataListView"
					};
				}

			});

		registry.register(
			"2.1.2", "2.2.0",
			new CTModelUpgradeProcess(
				"DEDataDefinitionFieldLink", "DEDataListView"));

		registry.register(
			"2.2.0", "2.2.1",
			UpgradeProcessFactory.alterColumnType(
				"DEDataDefinitionFieldLink", "fieldName", "VARCHAR(255) null"));

		registry.register(
			"2.2.1", "2.2.2",
			UpgradeProcessFactory.alterColumnType(
				"DEDataListView", "appliedFilters", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"DEDataListView", "fieldNames", "TEXT null"));
	}

}