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

package com.liferay.osb.patcher.hook.upgrade.v7_3_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.compat.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kiana Suetani
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		Set<Long> patcherFixIds = new HashSet<>();

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select * from OSB_PatcherFix where type_ = 6 AND committish" +
					" != ''");

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherFixId = rs.getLong("patcherFixId");

				patcherFixIds.add(patcherFixId);
			}

			runSQL(
				"update OSB_PatcherFix set type_ = 0 where patcherFixId in (" +
					StringUtil.merge(patcherFixIds) + ")");

			runSQL(
				"delete from OSB_PatcherFixRel where childPatcherFixId in (" +
					StringUtil.merge(patcherFixIds) + ")");
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

}