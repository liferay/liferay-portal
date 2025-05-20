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

package com.liferay.osb.patcher.hook.upgrade.v2_4_0;

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
		for (long patcherBuildId : _PATCHER_BUILD_IDS) {
			runSQL(
				"update OSB_PatcherBuild set latestBuild = 0 where " +
					"patcherBuildId = " + patcherBuildId);
		}

		for (long patcherBuildId : _PATCHER_BUILD_IDS_DELETE) {
			runSQL(
				"delete from OSB_PatcherBuilds_PatcherFixes where " +
					"patcherBuildId = " + patcherBuildId);
		}

		for (long patcherBuildId : _PATCHER_BUILD_IDS_LATEST) {
			runSQL(
				"update OSB_PatcherBuild set latestBuild = 1 where " +
					"patcherBuildId = " + patcherBuildId);
		}

		for (long patcherFixId : _PATCHER_FIX_IDS) {
			runSQL(
				"delete from OSB_PatcherBuild where patcherFixId = " +
					patcherFixId);

			runSQL(
				"delete from OSB_PatcherBuilds_PatcherFixes where " +
					"patcherFixId = " + patcherFixId);

			runSQL(
				"delete from OSB_PatcherFixRel where patcherFixId1 = " +
					patcherFixId);

			runSQL(
				"delete from OSB_PatcherFixRel where patcherFixId2 = " +
					patcherFixId);
		}
	}

	private static final long[] _PATCHER_BUILD_IDS = {
		1048205, 1084707, 1128383, 1135466, 1202270, 1207841, 1270191, 1311381,
		1323316, 1409013, 1438478
	};

	private static final long[] _PATCHER_BUILD_IDS_DELETE = {
		1048719, 1055055, 1085494, 1085526, 1132445, 1136792, 1136818, 1170336,
		1201430, 1202843, 1202875, 1208422, 1208606, 1210601, 1232168, 1232176,
		1235764, 1264578, 1265120, 1270400, 1277845, 1313363, 1318453, 1321826,
		1323439, 1346158, 1353472, 1353488, 1366956, 1368142, 1372563, 1405655,
		1405699, 1442408, 1442449, 1442472, 1442543, 1443369, 1443374, 1443391,
		1469030, 1469056, 1489811, 1491674, 1491733, 1502807, 1559127, 1565160,
		1565206, 1581065, 1581095, 1581127, 1605381
	};

	private static final long[] _PATCHER_BUILD_IDS_LATEST = {
		956021, 1334952
	};

	private static final long[] _PATCHER_FIX_IDS = {
		1048722, 1208427, 1321829, 1366961
	};

}