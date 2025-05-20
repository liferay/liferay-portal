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

package com.liferay.osb.patcher.hook.upgrade.v4_1_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Johnny Duong
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix();
	}

	protected void updatePatcherFix() throws Exception {
		runSQL(
			"delete from OSB_PatcherFixRel where childPatcherFixId = 4546409 " +
				"or parentPatcherFixId = 4546409");

		runSQL(
			"delete from OSB_PatcherBuilds_PatcherFixes where patcherFixId =" +
				" 4546409");

		runSQL("delete from OSB_PatcherFix where patcherFixId = 4546409");

		runSQL(
			"update OSB_PatcherFix set patcherFixVersion = 1 where " +
				"patcherFixId = 4547613");
	}

}