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

package com.liferay.osb.patcher.hook.upgrade.v3_7_0;

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
		updatePatcherFix();
	}

	protected void removeParentPatcherFixes(long[] patcherFixIds)
		throws Exception {

		runSQL(
			"delete from OSB_PatcherFixRel where childPatcherFixId in (" +
				StringUtil.merge(patcherFixIds) + ")");
	}

	protected void removePatcherFixes(long[] patcherFixIds) throws Exception {
		runSQL(
			"delete from OSB_PatcherFix where patcherFixId in (" +
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
				_log.error(e, e);
			}
		}
	}

	protected void updatePatcherFix() throws Exception {
		runSQL(
			"delete from OSB_PatcherFixes_PatcherFixPacks where patcherFixId " +
				"= 2659421 and patcherFixPackId in (2659437, 2659457)");

		runSQL(
			"update OSB_PatcherFix set dependencies = 'wcm-core->platform' " +
				"where patcherFixId = 2659421");

		for (int i = 0; i < _ROOT_PATCHER_FIX_IDS.length; i++) {
			updatePatcherBuildsPatcherFixes(
				_ROOT_PATCHER_FIX_IDS[i], _REMOVE_PATCHER_FIX_IDS[i]);

			updateChildPatcherFixRels(
				_ROOT_PATCHER_FIX_IDS[i], _REMOVE_PATCHER_FIX_IDS[i]);

			removeParentPatcherFixes(_REMOVE_PATCHER_FIX_IDS[i]);

			removePatcherFixes(_REMOVE_PATCHER_FIX_IDS[i]);

			runSQL(
				"update OSB_PatcherFix set latestFix = 1 where patcherFixId" +
					" = " + _ROOT_PATCHER_FIX_IDS[i]);
		}
	}

	private static final long[][] _REMOVE_PATCHER_FIX_IDS = new long[][] {
		{3450510}, {3450516}, {3453526}, {3450513}, {3382896}
	};

	private static final long[] _ROOT_PATCHER_FIX_IDS = {
		3066188, 3035339, 3060674, 3060747, 3359201,
	};

	private static Log _log = LogFactoryUtil.getLog(UpgradePatcherFix.class);

}