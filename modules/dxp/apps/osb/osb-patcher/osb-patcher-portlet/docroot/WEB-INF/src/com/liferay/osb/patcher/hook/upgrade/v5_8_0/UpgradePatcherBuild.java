/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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