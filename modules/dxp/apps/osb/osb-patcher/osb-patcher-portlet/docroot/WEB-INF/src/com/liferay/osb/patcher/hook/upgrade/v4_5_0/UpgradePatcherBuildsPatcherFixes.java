/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v4_5_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringBundler;

/**
 * @author Johnny Duong
 */
public class UpgradePatcherBuildsPatcherFixes extends UpgradeProcess {

	protected void deletePatcherBuildPatcherFixes() throws Exception {
		for (int i = 0; i < _DELETE_BUILD_ID_FIX_ID.length; i++) {
			StringBundler sb = new StringBundler(5);

			sb.append("delete from OSB_PatcherBuilds_PatcherFixes where ");
			sb.append("patcherBuildId = ");
			sb.append(_DELETE_BUILD_ID_FIX_ID[i][0]);
			sb.append(" and patcherFixId = ");
			sb.append(_DELETE_BUILD_ID_FIX_ID[i][1]);

			runSQL(sb.toString());
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		deletePatcherBuildPatcherFixes();

		replacePatcherBuildPatcherFixes();
	}

	protected void replacePatcherBuildPatcherFixes() throws Exception {
		for (int i = 0; i < _REPLACE_FROM_BUILD_ID_FIX_ID.length; i++) {
			StringBundler sb = new StringBundler(9);

			sb.append("update OSB_PatcherBuilds_PatcherFixes ");
			sb.append("set patcherBuildId = ");
			sb.append(_REPLACE_TO_BUILD_ID_FIX_ID[i][0]);
			sb.append(", patcherFixId = ");
			sb.append(_REPLACE_TO_BUILD_ID_FIX_ID[i][1]);
			sb.append(" where patcherBuildId = ");
			sb.append(_REPLACE_FROM_BUILD_ID_FIX_ID[i][0]);
			sb.append(" and patcherFixId = ");
			sb.append(_REPLACE_FROM_BUILD_ID_FIX_ID[i][1]);

			runSQL(sb.toString());
		}
	}

	private static final long[][] _DELETE_BUILD_ID_FIX_ID = {
		{2485491, 2485513}, {2515446, 2539846}, {2526109, 2485513},
		{3450147, 3450510}, {3450147, 3450513}, {3450147, 3450516}
	};

	private static final long[][] _REPLACE_FROM_BUILD_ID_FIX_ID = {
		{3451252, 3450510}, {3451252, 3450513}, {3452886, 3450510},
		{3452886, 3450513}, {3453155, 3450513}, {3455580, 3450513},
		{3457035, 3450513}
	};

	private static final long[][] _REPLACE_TO_BUILD_ID_FIX_ID = {
		{3451252, 3066188}, {3451252, 3060747}, {3452886, 3066188},
		{3452886, 3060747}, {3453155, 3060747}, {3455580, 3060747},
		{3457035, 3060747}
	};

}