/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.internal.upgrade.v2_0_0;

import com.liferay.portal.kernel.upgrade.BaseCompanyIdUpgradeProcess;

/**
 * @author Eudaldo Alonso
 */
public class UpgradeCompanyId extends BaseCompanyIdUpgradeProcess {

	@Override
	protected TableUpdater[] getTableUpdaters() {
		return new TableUpdater[] {
			new TableUpdater(
				"OSBPatcher_PAccounts_PBuilds", "PatcherAccount",
				"patcherAccountId"),
			new TableUpdater(
				"OSBPatcher_PBuilds_PFixes", "PatcherBuild", "patcherBuildId"),
			new TableUpdater("OSBPatcher_PatcherBuildRel", "User_", "userId"),
			new TableUpdater("OSBPatcher_PatcherFixRel", "User_", "userId"),
			new TableUpdater(
				"OSBPatcher_PFixes_PFixPacks", "PatcherFix", "patcherFixId")
		};
	}

}