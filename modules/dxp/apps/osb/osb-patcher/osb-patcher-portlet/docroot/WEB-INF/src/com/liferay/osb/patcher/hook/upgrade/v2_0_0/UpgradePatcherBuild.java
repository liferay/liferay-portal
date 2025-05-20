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

package com.liferay.osb.patcher.hook.upgrade.v2_0_0;

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
		for (long patcherBuildId : _PATCHER_BUILD_IDS_6130) {
			runSQL(
				"update OSB_PatcherBuild set patcherPortalVersionId = " +
					"10549 where patcherBuildId = " + patcherBuildId);
		}

		for (long patcherBuildId : _PATCHER_BUILD_IDS_6210) {
			runSQL(
				"update OSB_PatcherBuild set patcherPortalVersionId = " +
					"221718 where patcherBuildId = " + patcherBuildId);
		}

		for (long patcherBuildId : _PATCHER_BUILD_IDS_DELETE) {
			runSQL(
				"delete from OSB_PatcherBuild where patcherBuildId = " +
					patcherBuildId);

			runSQL(
				"delete from OSB_PatcherBuilds_PatcherFixes where " +
					"patcherBuildId = " + patcherBuildId);
		}
	}

	private static final long[] _PATCHER_BUILD_IDS_6130 = {
		352729, 431292, 700900, 771666, 771676, 771767, 771825, 887842, 888193
	};

	private static final long[] _PATCHER_BUILD_IDS_6210 = {
		510078, 701051, 771522, 837389, 851448
	};

	private static final long[] _PATCHER_BUILD_IDS_DELETE = {
		345992, 352729, 401269
	};

}