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

package com.liferay.osb.patcher.hook.upgrade.v5_8_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Calvin Keum
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select * from OSB_PatcherBuild where lesaticket like '%- %' " +
					"OR lesaticket like '% -%'");

			rs = ps.executeQuery();

			while (rs.next()) {
				String lesaTicket = rs.getString("lesaTicket");

				lesaTicket = lesaTicket.replaceAll(
					"\\s*-\\s*", StringPool.DASH);

				lesaTicket = lesaTicket.replaceAll("\\s", StringPool.DASH);

				long patcherBuildId = rs.getLong("patcherBuildId");

				_updatePatcherBuildLESATicket(lesaTicket, patcherBuildId);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private void _updatePatcherBuildLESATicket(
			String lesaTicket, long patcherBuildId)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"update OSB_PatcherBuild set lesaTicket = ? where " +
					"patcherBuildId = ?");

			ps.setString(1, lesaTicket);
			ps.setLong(2, patcherBuildId);

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

}