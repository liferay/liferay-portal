/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.category.property.internal.upgrade.registry;

import com.liferay.asset.category.property.internal.upgrade.v1_0_0.UpgradeClassNames;
import com.liferay.asset.category.property.internal.upgrade.v2_0_0.util.AssetCategoryPropertyTable;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(service = UpgradeStepRegistrator.class)
public class AssetCategoryPropertyServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register("0.0.1", "1.0.0", new UpgradeClassNames());

		registry.register(
			"1.0.0", "2.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {AssetCategoryPropertyTable.class}));

		registry.register(
			"2.0.0", "2.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"AssetCategoryProperty"};
				}

			});

		registry.register(
			"2.1.0", "2.2.0",
			UpgradeProcessFactory.alterColumnType(
				"AssetCategoryProperty", "key_", "VARCHAR(255) null"),
			UpgradeProcessFactory.alterColumnType(
				"AssetCategoryProperty", "value", "VARCHAR(255) null"));

		registry.register(
			"2.2.0", "2.3.0",
			new CTModelUpgradeProcess("AssetCategoryProperty"));

		registry.register(
			"2.3.0", "2.4.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"AssetCategoryProperty"};
				}

			});
	}

}