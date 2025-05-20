/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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
				_log.error(e);
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

	private static final long[][] _REMOVE_PATCHER_FIX_IDS = {
		{3450510}, {3450516}, {3453526}, {3450513}, {3382896}
	};

	private static final long[] _ROOT_PATCHER_FIX_IDS = {
		3066188, 3035339, 3060674, 3060747, 3359201
	};

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherFix.class);

}