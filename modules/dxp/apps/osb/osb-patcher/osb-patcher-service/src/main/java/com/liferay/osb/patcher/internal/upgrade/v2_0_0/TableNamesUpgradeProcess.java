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
			alterTableName("OSB_PatcherAccount", "PatcherAccount");
			alterTableName(
				"OSB_PatcherAccounts_PatcherBuilds",
				"PatcherAccounts_PatcherBuilds");
			alterTableName("OSB_PatcherBuild", "PatcherBuild");
			alterTableName("OSB_PatcherBuildRel", "PatcherBuildRel");
			alterTableName(
				"OSB_PatcherBuilds_PatcherFixes", "PatcherBuilds_PatcherFixes");
			alterTableName("OSB_PatcherFix", "PatcherFix");
			alterTableName("OSB_PatcherFixComponent", "PatcherFixComponent");
			alterTableName("OSB_PatcherFixPack", "PatcherFixPack");
			alterTableName("OSB_PatcherFixRel", "PatcherFixRel");
			alterTableName(
				"OSB_PatcherFixes_PatcherFixPacks",
				"PatcherFixes_PatcherFixPacks");
			alterTableName(
				"OSB_PatcherProductVersion", "PatcherProductVersion");
			alterTableName(
				"OSB_PatcherProjectVersion", "PatcherProjectVersion");
			alterTableName("OSB_PatcherTicketHint", "PatcherTicketHint");
		}
	}

}