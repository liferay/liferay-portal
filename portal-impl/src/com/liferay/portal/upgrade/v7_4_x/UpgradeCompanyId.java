/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.upgrade.BaseCompanyIdUpgradeProcess;
import com.liferay.portal.kernel.util.PortletKeys;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alberto Chaparro
 */
public class UpgradeCompanyId extends BaseCompanyIdUpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		disableProcessConcurrently();

		super.doUpgrade();
	}

	@Override
	protected TableUpdater[] getTableUpdaters() {
		return new TableUpdater[] {
			new PortalPreferencesTableUpdater("PortalPreferences"),
			new TableUpdater(
				"PortalPreferenceValue", "PortalPreferences",
				"portalPreferencesId")
		};
	}

	private class PortalPreferencesTableUpdater extends TableUpdater {

		public PortalPreferencesTableUpdater(String tableName) {
			super(tableName, "", "");
		}

		@Override
		public void update(Connection connection)
			throws IOException, SQLException {

			long[] companyIds = PortalInstancePool.getCompanyIds();

			if (companyIds.length == 1) {
				runSQL(connection, getUpdateSQL(String.valueOf(companyIds[0])));

				return;
			}

			// Company

			runSQL(
				connection,
				_getUpdateSQL(
					"Company", "companyId", "ownerId",
					PortletKeys.PREFS_OWNER_TYPE_COMPANY));

			// Group

			runSQL(
				connection,
				_getUpdateSQL(
					"Group_", "groupId", "ownerId",
					PortletKeys.PREFS_OWNER_TYPE_GROUP));

			// Layout

			runSQL(
				connection,
				_getUpdateSQL(
					"Layout", "plid", "ownerId",
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT));

			// LayoutRevision

			runSQL(
				connection,
				_getUpdateSQL(
					"LayoutRevision", "layoutRevisionId", "ownerId",
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT));

			// Organization

			runSQL(
				connection,
				_getUpdateSQL(
					"Organization_", "organizationId", "ownerId",
					PortletKeys.PREFS_OWNER_TYPE_ORGANIZATION));

			// PortletItem

			runSQL(
				connection,
				_getUpdateSQL(
					"PortletItem", "portletItemId", "ownerId",
					PortletKeys.PREFS_OWNER_TYPE_ARCHIVED));

			// User_

			runSQL(
				connection,
				_getUpdateSQL(
					"User_", "userId", "ownerId",
					PortletKeys.PREFS_OWNER_TYPE_USER));

			// PortalPreferences companyId 0

			runSQL(
				connection,
				"update PortalPreferences set companyId = 0 where ownerId = 0");
		}

		private String _getSelectSQL(
				String foreignTableName, String foreignColumnName,
				String columnName)
			throws SQLException {

			List<Long> companyIds = new ArrayList<>();

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select distinct companyId from " + foreignTableName);

				ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next()) {
					companyIds.add(resultSet.getLong("companyId"));
				}
			}

			if (companyIds.size() == 1) {
				return String.valueOf(companyIds.get(0));
			}

			return StringBundler.concat(
				"select companyId from ", foreignTableName, " where ",
				foreignTableName, ".", foreignColumnName, " = ", getTableName(),
				".", columnName);
		}

		private String _getUpdateSQL(
				String foreignTableName, String foreignColumnName,
				String columnName, int ownerType)
			throws SQLException {

			return StringBundler.concat(
				getUpdateSQL(
					_getSelectSQL(
						foreignTableName, foreignColumnName, columnName)),
				" where ownerType = ", ownerType,
				" and (companyId is null or companyId = 0)");
		}

	}

}