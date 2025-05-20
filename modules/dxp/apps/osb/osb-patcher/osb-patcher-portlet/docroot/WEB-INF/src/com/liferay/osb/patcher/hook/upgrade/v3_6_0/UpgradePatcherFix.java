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

package com.liferay.osb.patcher.hook.upgrade.v3_6_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.compat.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix1();
		updatePatcherFix2();
	}

	protected void removePatcherFixes(long[] patcherFixIds) throws Exception {
		runSQL(
			"delete from OSB_PatcherFix where patcherFixId in (" +
				StringUtil.merge(patcherFixIds) + ")");
	}

	protected void removePatcherFixParents(long[] patcherFixIds)
		throws Exception {

		runSQL(
			"delete from OSB_PatcherFixRel where childPatcherFixId in (" +
				StringUtil.merge(patcherFixIds) + ")");
	}

	protected void updateChildPatcherFixRels(
			long patcherFixId, long[] patcherFixIds)
		throws Exception {

		runSQL(
			"update OSB_PatcherFixRel set parentPatcherFixId = " +
				patcherFixId + " where parentPatcherFixId in (" +
					StringUtil.merge(patcherFixIds) + ")");
	}

	protected void updateParentPatcherFixRels(
			long patcherFixId, long[] patcherFixIds)
		throws Exception {

		runSQL(
			"update OSB_PatcherFixRel set childPatcherFixId = " + patcherFixId +
				" where childPatcherFixId in (" +
					StringUtil.merge(patcherFixIds) + ")");
	}

	protected void updatePatcherBuildsPatcherFixes(
			long patcherFixId, long[] patcherFixIds)
		throws Exception {

		for (long curPatcherFixId : patcherFixIds) {
			try {
				runSQL(
					"update OSB_PatcherBuilds_PatcherFixes set patcherFixId " +
						"= " + patcherFixId + " where patcherFixId = " +
							curPatcherFixId);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	protected void updatePatcherFix1() throws Exception {
		for (int i = 0; i < _ROOT_PATCHER_FIX_IDS.length; i++) {
			updatePatcherBuildsPatcherFixes(
				_ROOT_PATCHER_FIX_IDS[i], _REMOVE_PATCHER_FIX_IDS[i]);

			updateChildPatcherFixRels(
				_ROOT_PATCHER_FIX_IDS[i], _REMOVE_PATCHER_FIX_IDS[i]);

			removePatcherFixParents(_REMOVE_PATCHER_FIX_IDS[i]);

			removePatcherFixes(_REMOVE_PATCHER_FIX_IDS[i]);

			runSQL(
				"update OSB_PatcherFix set latestFix = 1 where patcherFixId" +
					" = " + _ROOT_PATCHER_FIX_IDS[i]);
		}

		removePatcherFixParents(new long[] {3035339, 3301896});
	}

	protected void updatePatcherFix2() throws Exception {
		long rootPatcherFixId = 2659421;
		long[] removePatcherFixIds = {3221875, 3221872};

		updatePatcherBuildsPatcherFixes(rootPatcherFixId, removePatcherFixIds);

		updateChildPatcherFixRels(rootPatcherFixId, removePatcherFixIds);

		updateParentPatcherFixRels(
			rootPatcherFixId, new long[] {removePatcherFixIds[0]});
		removePatcherFixParents(removePatcherFixIds);

		removePatcherFixes(removePatcherFixIds);

		runSQL(
			"update OSB_PatcherFix set latestFix = 1 where patcherFixId = " +
				rootPatcherFixId);
	}

	private static final long[][] _REMOVE_PATCHER_FIX_IDS = {
		{2694879}, {2949928}, {3252641}, {3174105}, {3382902, 3268164, 3174102},
		{3266695, 3197509, 3120950}, {3336331}, {3382899}, {3302259}, {3305651},
		{3305654},
		{
			3422333, 3395236, 3395233, 3394817, 3394619, 3394616, 3394544,
			3394541, 3393660, 3389846
		}
	};

	private static final long[] _ROOT_PATCHER_FIX_IDS = {
		2436594, 2664887, 3060674, 3060747, 3066188, 3099512, 3109309, 3289124,
		3301896, 3305098, 3305101, 3382896
	};

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherFix.class);

}