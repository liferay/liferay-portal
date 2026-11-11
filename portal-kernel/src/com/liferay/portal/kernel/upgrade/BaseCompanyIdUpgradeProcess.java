/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.db.DBTypeToSQLMap;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortletKeys;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class BaseCompanyIdUpgradeProcess extends UpgradeProcess {

	protected void disableProcessConcurrently() {
		_processConcurrently = false;
	}

	@Override
	protected void doUpgrade() throws Exception {
		if ((DBManagerUtil.getDBType() == DBType.SQLSERVER) ||
			!_processConcurrently) {

			for (TableUpdater tableUpdater : getTableUpdaters()) {
				_addCompanyIdColumn(tableUpdater);
			}
		}
		else {
			processConcurrently(
				getTableUpdaters(),
				tableUpdater -> _addCompanyIdColumn(tableUpdater), null);
		}
	}

	protected abstract TableUpdater[] getTableUpdaters();

	protected class PortletPreferencesTableUpdater extends TableUpdater {

		public PortletPreferencesTableUpdater(String tableName) {
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
					"Layout", "plid", "plid",
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT));

			// LayoutRevision

			runSQL(
				connection,
				_getUpdateSQL(
					"LayoutRevision", "layoutRevisionId", "plid",
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
					long companyId = resultSet.getLong("companyId");

					companyIds.add(companyId);
				}
			}

			if (companyIds.size() == 1) {
				return String.valueOf(companyIds.get(0));
			}

			return StringBundler.concat(
				"select distinct companyId from ", foreignTableName, " where ",
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

	protected class TableUpdater {

		public TableUpdater(
			String tableName, String foreignTableName, String columnName) {

			_tableName = tableName;
			_columnName = columnName;

			_foreignNamesArray = new String[][] {
				{foreignTableName, columnName}
			};
		}

		public TableUpdater(
			String tableName, String columnName, String[][] foreignNamesArray) {

			_tableName = tableName;
			_columnName = columnName;
			_foreignNamesArray = foreignNamesArray;
		}

		public String getTableName() {
			return _tableName;
		}

		public void update(Connection connection) throws Exception {
			for (String[] foreignNames : _foreignNamesArray) {
				runSQL(
					getUpdateDBTypeToSQLMap(
						connection, foreignNames[0], foreignNames[1]));
			}
		}

		protected DBTypeToSQLMap getUpdateDBTypeToSQLMap(
				Connection connection, String foreignTableName,
				String foreignColumnName)
			throws SQLException {

			DBTypeToSQLMap dbTypeToSQLMap = null;

			List<Long> companyIds = new ArrayList<>();

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select companyId from Company");

				ResultSet resultSet = preparedStatement.executeQuery()) {

				while (resultSet.next()) {
					companyIds.add(resultSet.getLong("companyId"));
				}
			}

			if (companyIds.size() == 1) {
				dbTypeToSQLMap = new DBTypeToSQLMap(
					getUpdateSQL(String.valueOf(companyIds.get(0))));
			}
			else {
				dbTypeToSQLMap = new DBTypeToSQLMap(
					getUpdateSQL(
						StringBundler.concat(
							"select max(companyId) from ", foreignTableName,
							" where ", foreignTableName, ".", foreignColumnName,
							" > 0 and ", foreignTableName, ".",
							foreignColumnName, " = ", _tableName, ".",
							_columnName)));

				dbTypeToSQLMap.add(
					DBType.POSTGRESQL,
					getUpdateSQL(
						StringBundler.concat(
							"select ", foreignTableName, ".companyId from ",
							foreignTableName, " where ", foreignTableName, ".",
							foreignColumnName, " > 0 and ", foreignTableName,
							".", foreignColumnName, " = ", _tableName, ".",
							_columnName, " order by ", foreignTableName,
							".companyId desc limit 1")));
			}

			return dbTypeToSQLMap;
		}

		protected String getUpdateSQL(String selectSQL) {
			return StringBundler.concat(
				"update ", _tableName, " set companyId = (", selectSQL, ")");
		}

		private final String _columnName;
		private final String[][] _foreignNamesArray;
		private final String _tableName;

	}

	private void _addCompanyIdColumn(TableUpdater tableUpdater)
		throws Exception {

		String tableName = tableUpdater.getTableName();

		try (LoggingTimer loggingTimer = new LoggingTimer(tableName)) {
			alterTableAddColumn(tableName, "companyId", "LONG");

			tableUpdater.update(connection);
		}
	}

	private boolean _processConcurrently = true;

}