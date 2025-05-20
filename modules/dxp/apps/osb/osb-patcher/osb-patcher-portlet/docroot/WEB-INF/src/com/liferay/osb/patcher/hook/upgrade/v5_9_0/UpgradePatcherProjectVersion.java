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

package com.liferay.osb.patcher.hook.upgrade.v5_9_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Johnny Duong
 */
public class UpgradePatcherProjectVersion extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try {
			if (!hasTable("OSB_PatcherProjectVersion")) {
				return;
			}

			long basePatcherProjectVersionId =
				CounterLocalServiceUtil.increment();

			addFixPackBasePatcherProjectVersion(basePatcherProjectVersionId);

			updateFixPackDEPatcherProjectVersions(basePatcherProjectVersionId);

			for (int i = 0; i < 5; i++) {
				CounterLocalServiceUtil.increment(
					PatcherBuild.class.getName() + StringPool.POUND +
						basePatcherProjectVersionId);
			}
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	private void addFixPackBasePatcherProjectVersion(
			long basePatcherProjectVersionId)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"insert into OSB_PatcherProjectVersion (" +
					"patcherProjectVersionId, companyId, userId, userName, " +
					"createDate, modifiedDate, rootPatcherProjectVersionId, " +
					"name, committish, repositoryName, fixedIssues, " +
					"productVersion) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
					"?, ?)");

			ps.setLong(1, basePatcherProjectVersionId);
			ps.setLong(2, 10154);
			ps.setLong(3, 10158);
			ps.setString(4, StringPool.BLANK);

			Timestamp now = new Timestamp(System.currentTimeMillis());

			ps.setTimestamp(5, now);
			ps.setTimestamp(6, now);

			ps.setLong(7, 0);
			ps.setString(8, _FIX_PACK_BASE_7010);
			ps.setString(9, _FIX_PACK_BASE_7010);
			ps.setString(10, _LIFERAY_PORTAL_EE);
			ps.setString(11, StringPool.BLANK);
			ps.setInt(12, _TYPE_PRODUCT_VERSION_7X);

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

	private void updateFixPackDEPatcherProjectVersions(
			long basePatcherProjectVersionId)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select * from OSB_PatcherProjectVersion where " +
					"productVersion = ?");

			ps.setInt(1, _TYPE_PRODUCT_VERSION_7X);

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherProjectVersionId = rs.getLong(
					"patcherProjectVersionId");
				String committish = rs.getString("committish");

				Pattern pattern = Pattern.compile(_FIX_PACK_DE_REGEX);

				Matcher matcher = pattern.matcher(committish);

				if (matcher.find()) {
					StringBundler sb = new StringBundler(5);

					sb.append("update OSB_PatcherProjectVersion set ");
					sb.append("rootPatcherProjectVersionId = ");
					sb.append(basePatcherProjectVersionId);
					sb.append(" where patcherProjectVersionId = ");
					sb.append(patcherProjectVersionId);

					runSQL(sb.toString());
				}
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private static final String _FIX_PACK_BASE_7010 = "fix-pack-base-7010";

	private static final String _FIX_PACK_DE_REGEX = "^fix-pack-de-\\d+-7010$";

	private static final String _LIFERAY_PORTAL_EE = "liferay-portal-ee";

	private static final int _TYPE_PRODUCT_VERSION_7X = 2;

	private static Log _log = LogFactoryUtil.getLog(
			UpgradePatcherProjectVersion.class);

}