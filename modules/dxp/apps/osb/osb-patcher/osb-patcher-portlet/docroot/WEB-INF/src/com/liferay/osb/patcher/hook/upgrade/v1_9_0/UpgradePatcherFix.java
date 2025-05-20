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

package com.liferay.osb.patcher.hook.upgrade.v1_9_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix();
	}

	protected boolean hasLatestPatcherFix(String patcherFixKey)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select * from OSB_PatcherFix where patcherFixKey = ? and " +
					"latestFix = 1");

			ps.setString(1, patcherFixKey);

			rs = ps.executeQuery();

			return rs.next();
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void updateLatestPatcherFix(String patcherFixKey)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select patcherFixId from OSB_PatcherFix where patcherFixKey " +
					"= ? order by patcherFixVersion desc");

			ps.setString(1, patcherFixKey);

			rs = ps.executeQuery();

			if (!rs.next()) {
				return;
			}

			long patcherFixId = rs.getLong("patcherFixId");

			runSQL(
				"update OSB_PatcherFix set latestFix = 1 where patcherFixId " +
					"= " + patcherFixId);
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void updatePatcherFix() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select distinct patcherFixKey from OSB_PatcherFix");

			rs = ps.executeQuery();

			while (rs.next()) {
				String patcherFixKey = rs.getString("patcherFixKey");

				if (hasLatestPatcherFix(patcherFixKey)) {
					continue;
				}

				updateLatestPatcherFix(patcherFixKey);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

}