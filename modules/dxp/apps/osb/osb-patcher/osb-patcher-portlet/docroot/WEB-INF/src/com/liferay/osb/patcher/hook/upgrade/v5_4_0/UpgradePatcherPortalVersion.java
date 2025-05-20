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

package com.liferay.osb.patcher.hook.upgrade.v5_4_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * @author Calvin Keum
 */
public class UpgradePatcherPortalVersion extends UpgradeProcess {

	protected void addPatcherProject(
			long patcherProjectVersionId, long companyId, long userId,
			String userName, Timestamp createDate, Timestamp modifiedDate,
			long rootPatcherProjectVersionId, String name, String committish,
			String repositoryName)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"insert into OSB_PatcherProjectVersion (" +
					"patcherProjectVersionId, companyId, userId, userName, " +
						"createDate, modifiedDate, " +
							"rootPatcherProjectVersionId, name, committish, " +
								"repositoryName, productVersion) values " +
									"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			ps.setLong(1, patcherProjectVersionId);
			ps.setLong(2, companyId);
			ps.setLong(3, userId);
			ps.setString(4, userName);
			ps.setTimestamp(5, createDate);
			ps.setTimestamp(6, modifiedDate);
			ps.setLong(7, rootPatcherProjectVersionId);
			ps.setString(8, name);
			ps.setString(9, committish);
			ps.setString(10, repositoryName);
			ps.setInt(11, _TYPE_PRODUCT_VERSION_6X);

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

	protected void copyPatcherPortalPatcherProject() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select patcherPortalVersionId, companyId, userId, " +
					"userName, createDate, modifiedDate, " +
						"rootPatcherPortalVersionId, " +
							"name, committish, repositoryName " +
								"from OSB_PatcherPortalVersion");

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherPortalVersionId = rs.getLong(
					"patcherPortalVersionId");
				long companyId = rs.getLong("companyId");
				long userId = rs.getLong("userId");
				String userName = rs.getString("userName");
				Timestamp createDate = rs.getTimestamp("createDate");
				Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
				long rootPatcherPortalVersionId = rs.getLong(
					"rootPatcherPortalVersionId");
				String name = rs.getString("name");
				String committish = rs.getString("committish");
				String repositoryName = rs.getString("repositoryName");

				addPatcherProject(
					patcherPortalVersionId, companyId, userId, userName,
					createDate, modifiedDate, rootPatcherPortalVersionId, name,
					committish, repositoryName);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		try {
			if (!hasTable("OSB_PatcherPortalVersion") &&
				!hasTable("OSB_PatcherProjectVersion")) {

				return;
			}

			copyPatcherPortalPatcherProject();

			runSQL("drop table OSB_PatcherPortalVersion");
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static final int _TYPE_PRODUCT_VERSION_6X = 1;

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherPortalVersion.class);

}