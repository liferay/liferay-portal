/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v3_0_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFixRel extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFixRel();
	}

	protected void updatePatcherFixRel() throws Exception {
		runSQL(
			"delete from OSB_PatcherFixRel where childPatcherFixId = 2539846");
	}

}