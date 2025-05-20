/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v6_3_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Kiana Suetani
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!tableHasColumn("OSB_PatcherBuild", "qaStatus")) {
			return;
		}

		try {
			runSQL(
				"update OSB_PatcherBuild set qaStatus = " +
					_STATUS_BUILD_QA_FAILED_MANUALLY);
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static final int _STATUS_BUILD_QA_FAILED_MANUALLY = 221;

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherBuild.class);

}