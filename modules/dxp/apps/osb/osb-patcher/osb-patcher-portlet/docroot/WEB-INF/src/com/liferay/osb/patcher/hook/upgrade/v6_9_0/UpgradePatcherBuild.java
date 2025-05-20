/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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