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

package com.liferay.osb.patcher.hook.upgrade.v5_6_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Johnny Duong
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try {
			if (!tableHasColumn("OSB_PatcherBuild", "productVersion")) {
				return;
			}

			runSQL(
				"update OSB_PatcherBuild set productVersion = " +
					_TYPE_PRODUCT_VERSION_6X);
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static final int _TYPE_PRODUCT_VERSION_6X = 1;

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherBuild.class);

}