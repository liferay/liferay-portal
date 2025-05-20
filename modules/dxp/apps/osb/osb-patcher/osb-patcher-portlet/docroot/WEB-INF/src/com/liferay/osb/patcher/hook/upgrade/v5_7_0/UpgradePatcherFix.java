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

package com.liferay.osb.patcher.hook.upgrade.v5_7_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Johnny Duong
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select * from OSB_PatcherFix where jenkinsResults <> '' and " +
					"jenkinsResults is not null");

			rs = ps.executeQuery();

			while (rs.next()) {
				String jenkinsResults = rs.getString("jenkinsResults");

				for (int i = 0; i < _SEARCH_FOR.length; i++) {
					jenkinsResults = StringUtil.replace(
						jenkinsResults, _SEARCH_FOR[i], _REPLACE_WITH[i]);
				}

				long patcherFixId = rs.getLong("patcherFixId");

				_updatePatcherFixJenkinsResults(jenkinsResults, patcherFixId);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private void _updatePatcherFixJenkinsResults(
			String jenkinsResults, long patcherFixId)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"update OSB_PatcherFix set jenkinsResults = ? where " +
					"patcherFixId = ?");

			ps.setString(1, jenkinsResults);
			ps.setLong(2, patcherFixId);

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

	private static final String[] _REPLACE_WITH = {
		"fixpack-builder-agent(production)", "fixpack-builder-dist(production)"
	};

	private static final String[] _SEARCH_FOR = {
		"fixpack-builder-agent", "fixpack-builder-dist"
	};

}