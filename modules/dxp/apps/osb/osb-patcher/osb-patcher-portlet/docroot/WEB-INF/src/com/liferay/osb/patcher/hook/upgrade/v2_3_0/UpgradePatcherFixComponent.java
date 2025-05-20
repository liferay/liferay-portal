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

package com.liferay.osb.patcher.hook.upgrade.v2_3_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.compat.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFixComponent extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFixComponent();
	}

	protected void updatePatcherFixComponent() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select patcherFixComponentId, name from " +
					"OSB_PatcherFixComponent");

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherFixComponentId = rs.getLong(
					"patcherFixComponentId");
				String name = rs.getString("name");

				updatePatcherFixComponentName(patcherFixComponentId, name);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void updatePatcherFixComponentName(
			long patcherFixComponentId, String name)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"update OSB_PatcherFixComponent set name = ? where " +
					"patcherFixComponentId = " + patcherFixComponentId);

			ps.setString(1, StringUtil.toLowerCase(name));

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

}