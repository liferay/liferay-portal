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

package com.liferay.osb.patcher.hook.upgrade.v2_2_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringBundler;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherBuild();
	}

	protected void updatePatcherBuild() throws Exception {
		for (long patcherBuildId : _PATCHER_BUILD_IDS) {
			runSQL(
				"update OSB_PatcherBuild set latestBuild = 0 where " +
					"patcherBuildId = " + patcherBuildId);
		}

		StringBundler sb = new StringBundler(5);

		sb.append("update OSB_PatcherBuild ");
		sb.append("set accountEntryName = \"TUDORTMUNDSP\", ");
		sb.append("patcherBuildKey = ");
		sb.append("\"9a50ee745830f813be1bc931ba8154c2cd0824d2\" ");
		sb.append("where patcherBuildId = 218723");

		runSQL(sb.toString());
	}

	private static final long[] _PATCHER_BUILD_IDS = {
		414605, 640861, 738389
	};

}