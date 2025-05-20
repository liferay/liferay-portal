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

package com.liferay.osb.patcher.hook.upgrade.v5_3_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.util.BigDecimalUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Calvin Keum
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherBuild();
		updatePatcherBuildLESAFields();
	}

	protected void updatePatcherBuild() throws Exception {
		if (tableHasColumn("OSB_PatcherBuild", "latestBuild") &&
			tableHasColumn("OSB_PatcherBuild", "latestKeyBuild")) {

			runSQL("update OSB_PatcherBuild set latestKeyBuild = latestBuild");
		}
	}

	protected void updatePatcherBuildLESAFields() throws Exception {
		if (!tableHasColumn("OSB_PatcherBuild", "latestLESATicketBuild") ||
			!tableHasColumn("OSB_PatcherBuild", "lesaTicketVersion")) {

			return;
		}

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select lesaTicket, count(lesaTicket) from OSB_PatcherBuild " +
					"group by lesaTicket");

			rs = ps.executeQuery();

			while (rs.next()) {
				String lesaTicket = rs.getString("lesaTicket");
				int count = rs.getInt(2);

				_setPatcherBuildLESAFields(lesaTicket, count);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private void _setPatcherBuildLESAFields(String lesaTicket, int size)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select * from OSB_PatcherBuild where lesaTicket = ? " +
					"ORDER BY createDate ASC");

			ps.setString(1, lesaTicket);

			rs = ps.executeQuery();

			boolean latestLESATicketBuild = false;
			double lesaTicketVersion = 1;

			while (rs.next()) {
				long patcherBuildId = rs.getLong("patcherBuildId");

				if (size <= 1) {
					latestLESATicketBuild = true;
				}

				_updatePatcherBuildLESAFields(
					latestLESATicketBuild, lesaTicketVersion, patcherBuildId);

				lesaTicketVersion = BigDecimalUtil.add(lesaTicketVersion, 0.1);

				size--;
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private void _updatePatcherBuildLESAFields(
			boolean latestLESATicketBuild, double lesaTicketVersion,
			long patcherBuildId)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"update OSB_PatcherBuild set latestLESATicketBuild = ?, " +
					"lesaTicketVersion = ? where patcherBuildId = ?");

			ps.setBoolean(1, latestLESATicketBuild);
			ps.setDouble(2, lesaTicketVersion);
			ps.setLong(3, patcherBuildId);

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

}