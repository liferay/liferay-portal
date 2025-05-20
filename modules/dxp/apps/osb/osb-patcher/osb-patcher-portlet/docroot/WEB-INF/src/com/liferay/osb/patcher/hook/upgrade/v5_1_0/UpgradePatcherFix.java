/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v5_1_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Calvin Keum
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix();
	}

	protected void updatePatcherFix() throws Exception {
		try {
			runSQL("alter table OSB_PatcherFix drop index IX_8BFFC3A0");

			if (tableHasColumn("OSB_PatcherFix", "patcherFixKey")) {
				runSQL(
					"alter_column_name OSB_PatcherFix patcherFixKey " +
						"key_ VARCHAR(75)");
			}

			if (tableHasColumn("OSB_PatcherFix", "patcherFixVersion")) {
				runSQL(
					"alter_column_name OSB_PatcherFix patcherFixVersion " +
						"keyVersion DOUBLE");

				runSQL(
					"create unique index IX_687F0E48 on OSB_PatcherFix " +
						"(key_, keyVersion)");
			}
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherFix.class);

}