/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v4_2_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFixPack extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFixPack();
	}

	protected void updatePatcherFixPack() throws Exception {
		runSQL("alter table OSB_PatcherFixPack drop column verified");
	}

}