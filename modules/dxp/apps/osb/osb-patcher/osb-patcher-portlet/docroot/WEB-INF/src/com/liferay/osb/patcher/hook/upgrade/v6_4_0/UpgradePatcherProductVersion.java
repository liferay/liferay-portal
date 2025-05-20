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

package com.liferay.osb.patcher.hook.upgrade.v6_4_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixModelImpl;
import com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/**
 * @author Kiana Suetani
 */
public class UpgradePatcherProductVersion extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"insert into OSB_PatcherProductVersion (" +
					"patcherProductVersionId, companyId, userId, userName, " +
						"createDate, modifiedDate, name) values (?, ?, ?, ?, ?, " +
							"?, ?)");

			long patcherProductVersionId = CounterLocalServiceUtil.increment();

			ps.setLong(1, patcherProductVersionId);

			ps.setLong(2, 10154);
			ps.setLong(3, 10158);
			ps.setString(4, StringPool.BLANK);

			Timestamp now = new Timestamp(System.currentTimeMillis());

			ps.setTimestamp(5, now);
			ps.setTimestamp(6, now);

			ps.setString(7, _NAME_PRODUCT_VERSION_6X_PORTAL);

			ps.executeUpdate();

			_updateProductVersion(
				patcherProductVersionId, _TYPE_PRODUCT_VERSION_6X);

			patcherProductVersionId = CounterLocalServiceUtil.increment();

			ps.setLong(1, patcherProductVersionId);

			ps.setString(7, _NAME_PRODUCT_VERSION_7X_PORTAL);

			ps.executeUpdate();

			_updateProductVersion(
				patcherProductVersionId, _TYPE_PRODUCT_VERSION_7X);
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

	private void _updateProductVersion(
			long newPatcherProductVersionId, int oldPatcherProductVersionId)
		throws Exception {

		try {
			for (String table : _TABLES_WITH_PRODUCT_VERSION) {
				runSQL(
					"update " + table + " set productVersion = " +
						newPatcherProductVersionId + " where productVersion " +
							"= " + oldPatcherProductVersionId);
			}
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static final String _NAME_PRODUCT_VERSION_6X_PORTAL = "Portal 6.x";

	private static final String _NAME_PRODUCT_VERSION_7X_PORTAL = "DXP 7.0 DE";

	private static final String[] _TABLES_WITH_PRODUCT_VERSION = {
		PatcherBuildModelImpl.TABLE_NAME, PatcherFixModelImpl.TABLE_NAME,
		PatcherProjectVersionModelImpl.TABLE_NAME
	};

	private static final int _TYPE_PRODUCT_VERSION_6X = 1;

	private static final int _TYPE_PRODUCT_VERSION_7X = 2;

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherProductVersion.class);

}