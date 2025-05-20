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

package com.liferay.osb.patcher.hook.upgrade.v3_0_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherBuild();
	}

	protected void updatePatcherBuild() throws Exception {
		runSQL(
			"delete from OSB_PatcherBuilds_PatcherFixes where patcherBuildId " +
				"= 1973240");

		runSQL("delete from OSB_PatcherBuild where patcherBuildId = 1973240");

		runSQL(
			"update OSB_PatcherBuild set latestBuild = 0 where " +
				"patcherBuildId = 2674966");

		runSQL(
			"update OSB_PatcherBuild set patcherBuildVersion = 1.1 where " +
				"patcherBuildId = 2675598");

		try {
			runSQL(
				"create unique index IX_8B73E919 on OSB_PatcherBuild " +
					"(patcherBuildKey, patcherBuildVersion)");
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(UpgradePatcherBuild.class);

}