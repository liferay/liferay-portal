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

package com.liferay.osb.patcher.hook.upgrade.v1_4_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;

import java.util.List;

/**
 * @author Calvin Keum
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherBuild();
	}

	protected void updatePatcherBuild() throws Exception {
		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuilds(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (PatcherBuild patcherBuild : patcherBuilds) {
			if ((patcherBuild.getStatus() != _STATUS_BUILD_PENDING_MERGE) &&
				(patcherBuild.getStatus() !=
					_STATUS_BUILD_PENDING_MERGE_ONLY)) {

				continue;
			}

			List<PatcherFix> patcherFixes =
				PatcherFixLocalServiceUtil.getPatcherBuildPatcherFixs(
					patcherBuild.getPatcherBuildId());

			for (PatcherFix patcherFix : patcherFixes) {
				if (patcherFix.getStatus() != _STATUS_FIX_PENDING_CONFLICT) {
					continue;
				}

				if (patcherBuild.getStatus() == _STATUS_BUILD_PENDING_MERGE) {
					patcherBuild.setStatus(_STATUS_BUILD_PENDING_CONFLICT);
				}
				else {
					patcherBuild.setStatus(
						_STATUS_BUILD_PENDING_CONFLICT_MERGE_ONLY);
				}

				PatcherBuildLocalServiceUtil.updatePatcherBuild(patcherBuild);

				break;
			}
		}
	}

	private static final int _STATUS_BUILD_PENDING_CONFLICT = 208;

	private static final int _STATUS_BUILD_PENDING_CONFLICT_MERGE_ONLY = 209;

	private static final int _STATUS_BUILD_PENDING_MERGE = 201;

	private static final int _STATUS_BUILD_PENDING_MERGE_ONLY = 206;

	private static final int _STATUS_FIX_PENDING_CONFLICT = 102;

}