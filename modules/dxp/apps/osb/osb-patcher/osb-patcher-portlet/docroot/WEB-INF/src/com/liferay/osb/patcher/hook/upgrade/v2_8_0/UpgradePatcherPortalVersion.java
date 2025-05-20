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

package com.liferay.osb.patcher.hook.upgrade.v2_8_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringBundler;

/**
 * @author Johnny Duong
 * @author Eddie Olson
 */
public class UpgradePatcherPortalVersion extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherPortalVersion();
	}

	protected void updatePatcherPortalVersion() throws Exception {
		if (!tableHasColumn(
				"OSB_PatcherPortalVersion", "rootPatcherPortalVersionId")) {

			runSQL(
				"alter table OSB_PatcherPortalVersion add " +
					"rootPatcherPortalVersionId LONG");
		}

		runSQL(
			"update OSB_PatcherPortalVersion set rootPatcherPortalVersionId " +
				"= 0");

		for (int i = 0; i < _PATCHER_PORTAL_VERSION_IDS.length; i++) {
			StringBundler sb = new StringBundler(5);

			sb.append("update OSB_PatcherPortalVersion ");
			sb.append("set rootPatcherPortalVersionId = ");
			sb.append(_ROOT_PATCHER_PORTAL_VERSION_IDS[i]);
			sb.append(" where patcherPortalVersionId = ");
			sb.append(_PATCHER_PORTAL_VERSION_IDS[i]);

			runSQL(sb.toString());
		}
	}

	private static final long[] _PATCHER_PORTAL_VERSION_IDS = {
		2248809, 2448741
	};

	private static final long[] _ROOT_PATCHER_PORTAL_VERSION_IDS = {
		221718, 10549
	};

}