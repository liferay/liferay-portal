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

package com.liferay.osb.patcher.hook.upgrade.v5_1_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Calvin Keum
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherBuild();
	}

	protected void updatePatcherBuild() throws Exception {
		try {
			runSQL("alter table OSB_PatcherBuild drop index IX_8B73E919");

			if (tableHasColumn("OSB_PatcherBuild", "patcherBuildKey")) {
				runSQL(
					"alter_column_name OSB_PatcherBuild patcherBuildKey " +
						"key_ VARCHAR(75)");
			}

			if (tableHasColumn("OSB_PatcherBuild", "patcherBuildVersion")) {
				runSQL(
					"alter_column_name OSB_PatcherBuild patcherBuildVersion " +
						"keyVersion DOUBLE");

				runSQL(
					"create unique index IX_4C479721 on OSB_PatcherBuild " +
						"(key_, keyVersion)");
			}

			if (tableHasColumn("OSB_PatcherBuild", "ticketEntryName")) {
				runSQL(
					"alter_column_name OSB_PatcherBuild ticketEntryName " +
						"lesaTicket VARCHAR(75)");
			}
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	private static Log _log = LogFactoryUtil.getLog(UpgradePatcherBuild.class);

}