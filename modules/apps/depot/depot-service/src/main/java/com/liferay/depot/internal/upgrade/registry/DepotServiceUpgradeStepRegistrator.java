/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.upgrade.registry;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.internal.upgrade.v2_2_0.util.DepotEntryPinTable;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(service = UpgradeStepRegistrator.class)
public class DepotServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.1.0",
			UpgradeProcessFactory.addColumns(
				"DepotEntryGroupRel", "ddmStructuresAvailable BOOLEAN"));

		registry.register(
			"1.1.0", "1.2.0",
			new com.liferay.depot.internal.upgrade.v1_2_0.
				DepotEntryGroupRelUpgradeProcess());

		registry.register(
			"1.2.0", "2.0.0",
			UpgradeProcessFactory.addColumns(
				"DepotEntryGroupRel", "userId LONG",
				"userName VARCHAR(75) null", "lastPublishDate DATE null"));

		registry.register(
			"2.0.0", "2.1.0",
			new CTModelUpgradeProcess(
				"DepotAppCustomization", "DepotEntry", "DepotEntryGroupRel"));

		registry.register("2.1.0", "2.2.0", DepotEntryPinTable.create());

		registry.register(
			"2.2.0", "2.3.0",
			UpgradeProcessFactory.addColumns("DepotEntry", "type_ INTEGER"),
			UpgradeProcessFactory.addColumns(
				"DepotEntryGroupRel", "type_ INTEGER"),
			UpgradeProcessFactory.runSQL(
				"update DepotEntry set type_ = " +
					DepotConstants.TYPE_ASSET_LIBRARY),
			UpgradeProcessFactory.runSQL(
				"update DepotEntryGroupRel set type_ = " +
					DepotConstants.TYPE_ASSET_LIBRARY));
	}

}