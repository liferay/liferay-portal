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

package com.liferay.osb.patcher.hook.upgrade.v2_2_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFixRel extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFixRel();
	}

	protected void removePatcherFixRelOrphans() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			StringBundler sb = new StringBundler(6);

			sb.append("select distinct patcherFixRelId ");
			sb.append("from OSB_PatcherFixRel ");
			sb.append("left join OSB_PatcherFix on ");
			sb.append("OSB_PatcherFixRel.patcherFixId1 = ");
			sb.append("OSB_PatcherFix.patcherFixId ");
			sb.append("where OSB_PatcherFix.patcherFixId is null");

			ps = con.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			removePatcherFixRels(rs);
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void removePatcherFixRels(ResultSet rs) throws Exception {
		while (rs.next()) {
			long patcherFixRelId = rs.getLong("patcherFixRelId");

			runSQL(
				"delete from OSB_PatcherFixRel where patcherFixRelId = " +
					patcherFixRelId);
		}
	}

	protected void removePatcherFixRelWidows() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			StringBundler sb = new StringBundler(6);

			sb.append("select distinct patcherFixRelId ");
			sb.append("from OSB_PatcherFixRel ");
			sb.append("left join OSB_PatcherFix on ");
			sb.append("OSB_PatcherFixRel.patcherFixId2 = ");
			sb.append("OSB_PatcherFix.patcherFixId ");
			sb.append("where OSB_PatcherFix.patcherFixId is null");

			ps = con.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			removePatcherFixRels(rs);
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void updatePatcherFixRel() throws Exception {
		removePatcherFixRelOrphans();
		removePatcherFixRelWidows();
	}

}