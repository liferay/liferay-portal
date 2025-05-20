/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v6_7_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Kiana Suetani
 */
public class UpgradePatcherProductVersion extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try {
			runSQL(
				"update OSB_PatcherProductVersion set name = replace(name, '" +
					_PRODUCT_VERSION_SUFFIX_DE + "', '') where name like '%" +
						_PRODUCT_VERSION_SUFFIX_DE + "'");
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static final String _PRODUCT_VERSION_SUFFIX_DE = " DE";

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherProductVersion.class);

}