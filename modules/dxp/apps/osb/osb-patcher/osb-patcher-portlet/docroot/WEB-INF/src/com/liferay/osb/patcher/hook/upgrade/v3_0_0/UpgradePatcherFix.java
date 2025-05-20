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

package com.liferay.osb.patcher.hook.upgrade.v3_0_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix();
	}

	protected void updatePatcherFix() throws Exception {
		deletePatcherFixes();

		try {
			runSQL(
				"create unique index IX_8BFFC3A0 on OSB_PatcherFix " +
					"(patcherFixKey, patcherFixVersion)"
			);
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	private void deletePatcherFixes() throws Exception {
		long[] patcherFixIds = {2539846, 2659479, 2659480};

		for (long patcherFixId : patcherFixIds) {
			runSQL(
				"delete from OSB_PatcherFix where patcherFixId = " +
					patcherFixId);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(UpgradePatcherFix.class);

}