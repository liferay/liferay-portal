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

package com.liferay.osb.patcher.hook.upgrade.v4_4_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Johnny Duong
 */
public class UpgradePatcherBuild extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherBuild();
	}

	protected void updatePatcherBuild() throws Exception {
		if (tableHasColumn("OSB_PatcherBuild", "statusURL")) {
			runSQL(
				"alter_column_name OSB_PatcherBuild statusURL jenkinsResults " +
					"LONGTEXT");

			_updatePatcherBuildJenkinsResults();
		}
	}

	private String _parseJobName(String statusURL) {
		String jobName = StringPool.BLANK;

		Pattern pattern = Pattern.compile(_REGEX);

		Matcher matcher = pattern.matcher(statusURL);

		if (matcher.find()) {
			jobName = matcher.group(1);
		}

		return jobName;
	}

	private String _toJSONArrayString(String statusURL) throws Exception {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		statusURL = HttpUtil.decodeURL(statusURL);

		jsonObject.put("jobName", _parseJobName(statusURL));
		jsonObject.put("status", StringPool.BLANK);
		jsonObject.put("statusURL", statusURL);

		jsonArray.put(jsonObject);

		return jsonArray.toString();
	}

	private void _updatePatcherBuildJenkinsResults() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select * from OSB_PatcherBuild where jenkinsResults <> '' " +
					"and jenkinsResults is not null");

			rs = ps.executeQuery();

			while (rs.next()) {
				String jenkinsResults = rs.getString("jenkinsResults");
				long patcherBuildId = rs.getLong("patcherBuildId");

				_updatePatcherBuildJenkinsResults(
					jenkinsResults, patcherBuildId);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private void _updatePatcherBuildJenkinsResults(
			String jenkinsResults, long patcherBuildId)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"update OSB_PatcherBuild set jenkinsResults = ? where " +
					"patcherBuildId = ?");

			ps.setString(1, _toJSONArrayString(jenkinsResults));
			ps.setLong(2, patcherBuildId);

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

	private static String _REGEX = ".*/job/(.*)/[0-9]+.*$";

}