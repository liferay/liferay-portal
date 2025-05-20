/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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