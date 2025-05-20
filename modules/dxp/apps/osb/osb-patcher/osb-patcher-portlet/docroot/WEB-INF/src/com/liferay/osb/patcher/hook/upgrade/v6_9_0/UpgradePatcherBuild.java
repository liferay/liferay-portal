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

package com.liferay.osb.patcher.hook.upgrade.v6_9_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Kiana Suetani
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!tableHasColumn("OSB_PatcherBuild", "patcherAccountId")) {
			return;
		}

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement("select * from OSB_PatcherAccount");

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherAccountId = rs.getLong("patcherAccountId");
				String accountEntryCode = rs.getString("accountEntryCode");

				runSQL(
					"update OSB_PatcherBuild set patcherAccountId = " +
						patcherAccountId + " where accountEntryCode = '" +
							accountEntryCode + "'");
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

}