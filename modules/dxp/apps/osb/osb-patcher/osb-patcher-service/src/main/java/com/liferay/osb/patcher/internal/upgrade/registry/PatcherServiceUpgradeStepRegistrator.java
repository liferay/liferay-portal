/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.internal.upgrade.registry;

import com.liferay.osb.patcher.internal.upgrade.v2_0_0.SchemaUpgradeProcess;
import com.liferay.osb.patcher.internal.upgrade.v2_0_0.UpgradeCompanyId;
import com.liferay.osb.patcher.internal.upgrade.v2_0_0.UpgradePortletId;
import com.liferay.osb.patcher.internal.upgrade.v2_0_0.UpgradeThemeId;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.release.ReleaseRenamingUpgradeStep;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = UpgradeStepRegistrator.class)
public class PatcherServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerReleaseCreationUpgradeSteps(
			new ReleaseRenamingUpgradeStep(
				"com.liferay.osb.patcher.service", "osb-patcher-portlet",
				_releaseLocalService));

		registry.register("0.0.1", "7.3.0", new DummyUpgradeStep());

		registry.register(
			"7.3.0", "7.4.0",
			UpgradeProcessFactory.runSQL(
				"delete from OSB_PatcherFixPack where patcherBuildId = 0"));

		registry.register("7.4.0", "7.4.1", new SchemaUpgradeProcess());

		registry.register(
			"7.4.1", "7.4.2",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"OSBPatcher_PProductVersion",
						"OSBPatcher_PProjectVersion",
						"OSBPatcher_PatcherAccount", "OSBPatcher_PatcherBuild",
						"OSBPatcher_PatcherBuildRel", "OSBPatcher_PatcherFix",
						"OSBPatcher_PatcherFixComponent",
						"OSBPatcher_PatcherFixPack", "OSBPatcher_PatcherFixRel",
						"OSBPatcher_PatcherTicketHint"
					};
				}

			});

		registry.register("7.4.2", "7.4.3", new UpgradeCompanyId());

		registry.register("7.4.3", "7.4.4", new UpgradePortletId());

		registry.register("7.4.4", "7.4.5", new UpgradeThemeId());

		registry.register(
			"7.4.5", "8.0.0",
			UpgradeProcessFactory.alterColumnName(
				"OSBPatcher_PatcherBuild", "originalName", "initialName"),
			UpgradeProcessFactory.alterColumnType(
				"OSBPatcher_PatcherBuild", "name", "STRING null"));
	}

	@Reference
	private ReleaseLocalService _releaseLocalService;

}