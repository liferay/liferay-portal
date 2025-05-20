/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v2_6_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Johnny Duong
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgradePatcherBuild();
	}

	protected void updatePatcherBuildStatusURL(
			long patcherBuildId, String statusURL)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"update OSB_PatcherBuild set statusURL = ? where " +
					"patcherBuildId = " + patcherBuildId);

			ps.setString(1, statusURL);

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

	protected void upgradePatcherBuild() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select patcherBuildId, statusURL from OSB_PatcherBuild");

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherBuildId = rs.getLong("patcherBuildId");
				String statusURL = rs.getString("statusURL");

				updatePatcherBuildStatusURL(
					patcherBuildId,
					statusURL.replace(
						"liferay-fix-pack-builder", "fix-pack-builder"));
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

}