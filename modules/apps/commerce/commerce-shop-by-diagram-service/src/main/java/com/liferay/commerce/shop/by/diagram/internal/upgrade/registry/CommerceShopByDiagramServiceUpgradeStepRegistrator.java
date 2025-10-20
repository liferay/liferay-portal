/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.internal.upgrade.registry;

import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Cheryl Tang
 */
@Component(service = UpgradeStepRegistrator.class)
public class CommerceShopByDiagramServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected void doUpgrade() throws Exception {
					if (hasTable("CSDiagramEntry") &&
						hasTable("CSDiagramPin") &&
						hasTable("CSDiagramSetting")) {

						upgradeModuleTableMVCCVersions();
					}
				}

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"CSDiagramEntry", "CSDiagramPin", "CSDiagramSetting"
					};
				}

			});

		registry.register(
			"1.1.0", "1.2.0",
			new CTModelUpgradeProcess(
				"CSDiagramEntry", "CSDiagramPin", "CSDiagramSetting") {

				@Override
				protected void doUpgrade() throws Exception {
					if (hasTable("CSDiagramEntry") &&
						hasTable("CSDiagramPin") &&
						hasTable("CSDiagramSetting")) {

						super.doUpgrade();
					}
				}

			});

		registry.register("1.2.0", "1.2.1", new DummyUpgradeStep());

		registry.register(
			"1.2.1", "1.3.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"CSDiagramEntry"};
				}

			});
	}

}