/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v2_7_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgradePatcherBuild();
	}

	protected void upgradePatcherBuild() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			StringBundler sb = new StringBundler(4);

			sb.append("select OSB_PatcherBuild.patcherBuildId ");
			sb.append("from OSB_PatcherBuild inner join OSB_PatcherFixPack ");
			sb.append("on OSB_PatcherBuild.patcherBuildId = ");
			sb.append("OSB_PatcherFixPack.patcherBuildId");

			ps = con.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherBuildId = rs.getLong("patcherBuildId");

				runSQL(
					"update OSB_PatcherBuild set type_ = 1 where " +
						"patcherBuildId = " + patcherBuildId);
			}

			ps = con.prepareStatement(
				"select patcherBuildKey from OSB_PatcherBuild where type_ = 1");

			rs = ps.executeQuery();

			while (rs.next()) {
				String patcherBuildKey = rs.getString("patcherBuildKey");

				ps = con.prepareStatement(
					"update OSB_PatcherBuild set type_ = 1 where " +
						"patcherBuildKey = ?");

				ps.setString(1, patcherBuildKey);

				ps.executeUpdate();
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

}