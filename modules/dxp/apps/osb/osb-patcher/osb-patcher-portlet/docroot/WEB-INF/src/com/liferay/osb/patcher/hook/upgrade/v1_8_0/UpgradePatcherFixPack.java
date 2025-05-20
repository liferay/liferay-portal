/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v1_8_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.osb.patcher.model.impl.PatcherFixPackBaseImpl;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFixPack extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFixPack();
	}

	protected void updatePatcherFixPack() throws Exception {
		if (hasTable("OSB_PatcherFixPack")) {
			return;
		}

		runSQL(PatcherFixPackBaseImpl.TABLE_SQL_CREATE);

		runSQL(
			"create unique index IX_1678347B on OSB_PatcherFixPack " +
				"(patcherFixComponentId, patcherPortalVersionId, name, " +
					"version)");
	}

}