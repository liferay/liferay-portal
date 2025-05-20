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
			_log.error(e, e);
		}
	}

	private static final String[] _TABLES_WITH_PRODUCT_VERSION = {
		PatcherBuildModelImpl.TABLE_NAME, PatcherFixModelImpl.TABLE_NAME,
			PatcherProjectVersionModelImpl.TABLE_NAME};

	private static Log _log = LogFactoryUtil.getLog(
		UpgradePatcherProductVersion.class);

}