/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v4_8_0;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherAccount
	extends com.liferay.osb.patcher.hook.upgrade.v4_3_0.UpgradePatcherAccount {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherAccount();
	}

	@Override
	protected void updatePatcherAccount() throws Exception {
		runSQL("delete from OSB_PatcherAccount");
		runSQL("delete from OSB_PatcherAccounts_PatcherBuilds");

		super.updatePatcherAccount();
	}

}