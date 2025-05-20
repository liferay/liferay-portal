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

package com.liferay.osb.patcher.hook.upgrade.v6_1_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Johnny Duong
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix();
	}

	protected void updatePatcherFix() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select * from OSB_PatcherFix where key_ in (select key_ " +
					"from OSB_PatcherFix where type_ = ? and keyVersion = ?)");

			ps.setInt(1, _TYPE_GENERATED_PRIVATE_PUBLIC);
			ps.setDouble(2, 1.1);

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherFixId = rs.getLong("patcherFixId");
				double keyVersion = rs.getDouble("keyVersion");
				int type = rs.getInt("type_");

				if ((type == _TYPE_GENERATED_PRIVATE_PUBLIC) &&
					(keyVersion == 1.1)) {

					runSQL(
						"update OSB_PatcherFix set keyVersion = 1.0 where " +
							"patcherFixId = " + patcherFixId);
				}
				else if ((type != _TYPE_GENERATED_PRIVATE_PUBLIC) &&
						 (keyVersion == 1.0)) {

					runSQL(
						"update OSB_PatcherFix set latestFix = 1 where " +
							"patcherFixId = " + patcherFixId);
				}
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private static final int _TYPE_GENERATED_PRIVATE_PUBLIC = 5;

}