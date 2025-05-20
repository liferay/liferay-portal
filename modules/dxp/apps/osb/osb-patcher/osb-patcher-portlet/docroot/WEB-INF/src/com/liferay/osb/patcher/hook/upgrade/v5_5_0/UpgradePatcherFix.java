/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v5_5_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Johnny Duong
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try {
			if (!tableHasColumn("OSB_PatcherFix", "productVersion")) {
				return;
			}

			runSQL(
				"update OSB_PatcherFix set productVersion = " +
					_TYPE_PRODUCT_VERSION_6X);
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static final int _TYPE_PRODUCT_VERSION_6X = 1;

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherFix.class);

}