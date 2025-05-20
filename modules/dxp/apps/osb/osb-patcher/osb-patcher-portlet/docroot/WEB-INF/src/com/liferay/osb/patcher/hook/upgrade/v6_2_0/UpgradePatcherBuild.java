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

package com.liferay.osb.patcher.hook.upgrade.v6_2_0;

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
		if (tableHasColumn("OSB_PatcherBuild", "originalName")) {
			updatePatcherBuild();
		}
	}

	protected void updatePatcherBuild() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select * from OSB_PatcherBuild where productVersion = ? and " +
					"childBuild = ?");

			ps.setInt(1, _TYPE_PRODUCT_VERSION_7X);
			ps.setBoolean(2, false);

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherBuildId = rs.getLong("patcherBuildId");
				String name = rs.getString("name");

				runSQL(
					"update OSB_PatcherBuild set originalName = '" + name +
						"' where patcherBuildId = " + patcherBuildId);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private static final int _TYPE_PRODUCT_VERSION_7X = 2;

}