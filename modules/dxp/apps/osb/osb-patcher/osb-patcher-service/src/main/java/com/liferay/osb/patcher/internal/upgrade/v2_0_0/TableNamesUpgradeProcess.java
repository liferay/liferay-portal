/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.internal.upgrade.v2_0_0;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Brian Wing Shun Chan
 */
public class TableNamesUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (hasTable("OSB_PatcherAccount")) {
			alterTableName("OSB_PatcherAccount", "OSBPatcher_PatcherAccount");
			alterTableName(
				"OSB_PatcherAccounts_PatcherBuilds",
				"OSBPatcher_PAccounts_PBuilds");
			alterTableName("OSB_PatcherBuild", "OSBPatcher_PatcherBuild");
			alterTableName("OSB_PatcherBuildRel", "OSBPatcher_PatcherBuildRel");
			alterTableName(
				"OSB_PatcherBuilds_PatcherFixes", "OSBPatcher_PBuilds_PFixes");
			alterTableName("OSB_PatcherFix", "OSBPatcher_PatcherFix");
			alterTableName(
				"OSB_PatcherFixes_PatcherFixPacks",
				"OSBPatcher_PFixes_PFixPacks");
			alterTableName(
				"OSB_PatcherFixComponent", "OSBPatcher_PatcherFixComponent");
			alterTableName("OSB_PatcherFixPack", "OSBPatcher_PatcherFixPack");
			alterTableName("OSB_PatcherFixRel", "OSBPatcher_PatcherFixRel");
			alterTableName(
				"OSB_PatcherProductVersion", "OSBPatcher_PProductVersion");
			alterTableName(
				"OSB_PatcherProjectVersion", "OSBPatcher_PProjectVersion");
			alterTableName(
				"OSB_PatcherTicketHint", "OSBPatcher_PatcherTicketHint");
		}
	}

}