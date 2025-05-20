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

package com.liferay.osb.patcher.hook.upgrade.v3_2_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.compat.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Johnny Duong
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix();
	}

	protected void updatePatcherFix() throws Exception {
		if (!tableHasColumn("OSB_PatcherFix", "obsolete")) {
			runSQL("alter table OSB_PatcherFix add obsolete BOOLEAN");

			runSQL("update OSB_PatcherFix set obsolete = 0");
		}

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		Set<Long> patcherFixIds = new HashSet<Long>();

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select patcherFixId from OSB_PatcherFix where type_ = 3 " +
					"or latestFix = 0");

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherFixId = rs.getLong("patcherFixId");

				patcherFixIds.add(patcherFixId);
			}

			_updatePatcherFixObsolete(patcherFixIds);

			_updateChildPatcherFixIds(patcherFixIds);
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private void _updateChildPatcherFixIds(Set<Long> patcherFixIds)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		Set<Long> childPatcherFixIds = new HashSet<Long>();

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select distinct childPatcherFixId from OSB_PatcherFixRel " +
					"where parentPatcherFixId in (" +
						StringUtil.merge(patcherFixIds) + ")");

			rs = ps.executeQuery();

			while (rs.next()) {
				long childPatcherFixId = rs.getLong("childPatcherFixId");

				childPatcherFixIds.add(childPatcherFixId);
			}

			if (!childPatcherFixIds.isEmpty()) {
				_updatePatcherFixObsolete(childPatcherFixIds);

				_updateChildPatcherFixIds(childPatcherFixIds);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	private void _updatePatcherFixObsolete(Set<Long> patcherFixIds)
		throws Exception {

		runSQL(
			"update OSB_PatcherFix set obsolete = 1 where patcherFixId " +
				"in (" + StringUtil.merge(patcherFixIds) + ")");
	}

}