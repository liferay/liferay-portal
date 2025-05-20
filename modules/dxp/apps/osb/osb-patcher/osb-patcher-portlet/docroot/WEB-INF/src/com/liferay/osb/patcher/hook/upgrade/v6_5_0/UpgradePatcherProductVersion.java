/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v6_5_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixModelImpl;
import com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Kiana Suetani
 */
public class UpgradePatcherProductVersion extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try {
			for (String table : _TABLES_WITH_PRODUCT_VERSION) {
				if (!tableHasColumn(table, "patcherProductVersionId")) {
					continue;
				}

				runSQL(
					"update " + table + " set patcherProductVersionId = " +
						"productVersion");
			}
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static final String[] _TABLES_WITH_PRODUCT_VERSION = {
		PatcherBuildModelImpl.TABLE_NAME, PatcherFixModelImpl.TABLE_NAME,
		PatcherProjectVersionModelImpl.TABLE_NAME
	};

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherProductVersion.class);

}