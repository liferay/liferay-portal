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
public class UpgradePatcherBuildsPatcherFixes extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherBuildsPatcherFixes();
	}

	protected void removePatcherBuildsPatcherFixesOrphans() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			StringBundler sb = new StringBundler(7);

			sb.append("select distinct ");
			sb.append("OSB_PatcherBuilds_PatcherFixes.patcherFixId ");
			sb.append("from OSB_PatcherBuilds_PatcherFixes ");
			sb.append("left join OSB_PatcherBuild on ");
			sb.append("OSB_PatcherBuilds_PatcherFixes.patcherBuildId = ");
			sb.append("OSB_PatcherBuild.patcherBuildId ");
			sb.append("where OSB_PatcherBuild.patcherBuildId is null");

			ps = con.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			removeRecords(rs, "patcherFixId");
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void removePatcherBuildsPatcherFixesWidows() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			StringBundler sb = new StringBundler(7);

			sb.append("select distinct ");
			sb.append("OSB_PatcherBuilds_PatcherFixes.patcherBuildId ");
			sb.append("from OSB_PatcherBuilds_PatcherFixes ");
			sb.append("left join OSB_PatcherFix on ");
			sb.append("OSB_PatcherBuilds_PatcherFixes.patcherFixId = ");
			sb.append("OSB_PatcherFix.patcherFixId ");
			sb.append("where OSB_PatcherFix.patcherFixId is null");

			ps = con.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			removeRecords(rs, "patcherBuildId");
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void removeRecords(ResultSet rs, String columnName)
		throws Exception {

		while (rs.next()) {
			long patcherId = rs.getLong(columnName);

			StringBundler sb = new StringBundler(4);

			sb.append("delete from OSB_PatcherBuilds_PatcherFixes where ");
			sb.append(columnName);
			sb.append(" = ");
			sb.append(patcherId);

			runSQL(sb.toString());
		}
	}

	protected void updatePatcherBuildsPatcherFixes() throws Exception {
		removePatcherBuildsPatcherFixesOrphans();
		removePatcherBuildsPatcherFixesWidows();
	}

}