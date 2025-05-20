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

package com.liferay.osb.patcher.hook.upgrade.v3_5_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.compat.portal.kernel.util.ArrayUtil;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.osb.patcher.model.PatcherFixRel;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixRelLocalServiceUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFix extends UpgradeProcess {

	protected void addParentPatcherFixes(
			long patcherFixId, List<Long> parentPatcherFixIds)
		throws Exception {

		for (long parentPatcherFixId : parentPatcherFixIds) {
			long patcherFixRelId = CounterLocalServiceUtil.increment();

			PatcherFixRel patcherFixRel =
				PatcherFixRelLocalServiceUtil.createPatcherFixRel(
					patcherFixRelId);

			patcherFixRel.setChildPatcherFixId(patcherFixId);
			patcherFixRel.setParentPatcherFixId(parentPatcherFixId);

			PatcherFixRelLocalServiceUtil.updatePatcherFixRel(patcherFixRel);
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix();
	}

	protected List<Long> getParentPatcherFixIds(long patcherFixId)
		throws Exception {

		List<Long> parentPatcherFixIds = new ArrayList<Long>();

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select parentPatcherFixId from OSB_PatcherFixRel where " +
					"childPatcherFixId = " + patcherFixId);

			rs = ps.executeQuery();

			while (rs.next()) {
				long parentPatcherFixId = rs.getLong("parentPatcherFixId");

				parentPatcherFixIds.add(parentPatcherFixId);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}

		return parentPatcherFixIds;
	}

	protected List<Long> getPatcherBuildIds(long patcherFixId)
		throws Exception {

		List<Long> patcherBuildIds = new ArrayList<Long>();

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select patcherBuildId from OSB_PatcherBuilds_PatcherFixes " +
					"where patcherFixId = " + patcherFixId);

			rs = ps.executeQuery();

			while (rs.next()) {
				long patcherBuildId = rs.getLong("patcherBuildId");

				patcherBuildIds.add(patcherBuildId);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}

		return patcherBuildIds;
	}

	protected void updatePatcherFix() throws Exception {
		long patcherFixId1 = 3174108;

		runSQL(
			"delete from OSB_PatcherFixes_PatcherFixPacks where patcherFixId " +
				"= " + patcherFixId1);

		long patcherFixId2 = 3035339;

		List<Long> patcherBuildIds = getPatcherBuildIds(patcherFixId1);

		runSQL(
			"delete from OSB_PatcherBuilds_PatcherFixes where patcherFixId = " +
				patcherFixId1);

		PatcherBuildLocalServiceUtil.addPatcherFixPatcherBuilds(
			patcherFixId2, ArrayUtil.toLongArray(patcherBuildIds));

		List<Long> parentPatcherFixIds = getParentPatcherFixIds(patcherFixId1);

		runSQL(
			"delete from OSB_PatcherFixRel where childPatcherFixId = " +
				patcherFixId1);

		addParentPatcherFixes(patcherFixId2, parentPatcherFixIds);

		runSQL(
			"delete from OSB_PatcherFix where patcherFixId = " + patcherFixId1);

		runSQL(
			"update OSB_PatcherFix set latestFix = 1 where patcherFixId = " +
				patcherFixId2);
	}

}