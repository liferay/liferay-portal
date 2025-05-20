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

package com.liferay.osb.patcher.hook.upgrade.v1_6_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.compat.portal.kernel.util.StringUtil;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Calvin Keum
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherBuild();
	}

	protected void updatePatcherBuild() throws Exception {
		_removePatcherBuildDuplicates();

		runSQL(
			"alter_column_type OSB_PatcherBuild patcherBuildKey VARCHAR(75)");

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select patcherBuildId, patcherPortalVersionId, name, " +
					"accountEntryName from OSB_PatcherBuild");

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherBuildId = rs.getLong("patcherBuildId");
				long patcherPortalVersionId = rs.getLong(
					"patcherPortalVersionId");
				String name = rs.getString("name");
				String accountEntryName = rs.getString("accountEntryName");

				_updateAccountEntryName(accountEntryName);

				StringBundler sb = new StringBundler(4);

				sb.append(PatcherBuild.class.getName());
				sb.append(patcherPortalVersionId);
				sb.append(name);
				sb.append(accountEntryName);

				String patcherBuildKey = DigesterUtil.digestHex(
					StringUtil.toUpperCase(sb.toString()));

				_updatePatcherBuildKey(patcherBuildId, patcherBuildKey);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}

		runSQL(
			"create unique index IX_8B73E919 on OSB_PatcherBuild " +
				"(patcherBuildKey, patcherBuildVersion)");
	}

	private void _removePatcherBuildDuplicates() throws Exception {
		for (long patcherBuildId : _PATCHER_BUILD_IDS) {
			runSQL(
				"delete from OSB_PatcherBuild where patcherBuildId = " +
					patcherBuildId);

			runSQL(
				"delete from OSB_PatcherBuilds_PatcherFixes where " +
					"patcherBuildId = " + patcherBuildId);
		}

		for (long patcherFixId : _PATCHER_FIX_IDS) {
			runSQL(
				"delete from OSB_PatcherBuild where patcherFixId = " +
					patcherFixId);

			runSQL(
				"delete from OSB_PatcherBuilds_PatcherFixes where " +
					"patcherFixId = " + patcherFixId);
		}
	}

	private void _updateAccountEntryName(String accountEntryName)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"update OSB_PatcherBuild set accountEntryName = ? where " +
					"accountEntryName = ?");

			ps.setString(1, StringUtil.toUpperCase(accountEntryName));
			ps.setString(2, accountEntryName);

			rs = ps.executeQuery();
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private void _updatePatcherBuildKey(
			long patcherBuildId, String patcherBuildKey)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"update OSB_PatcherBuild set patcherBuildKey = ? where " +
					"patcherBuildId = " + patcherBuildId);

			ps.setString(1, patcherBuildKey);

			rs = ps.executeQuery();
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private static final long[] _PATCHER_BUILD_IDS = {435537};

	private static final long[] _PATCHER_FIX_IDS = {435223, 435270};

}