/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v1_1_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherBuild();
	}

	protected void updatePatcherBuild() throws Exception {
		runSQL("alter table OSB_PatcherBuild add patcherBuildKey LONG");

		runSQL("update OSB_PatcherBuild set patcherBuildKey = " + increment());

		runSQL("alter table OSB_PatcherBuild add latestBuild BOOLEAN");

		runSQL("update OSB_PatcherBuild set latestBuild = 1");

		runSQL(
			"alter table OSB_PatcherBuild add patcherBuildVersion " + "DOUBLE");

		runSQL("update OSB_PatcherBuild set patcherBuildVersion = 1.0");
	}

}