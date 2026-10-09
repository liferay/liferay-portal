/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.BaseAllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.DefaultAllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;

import java.util.List;
import java.util.Set;

/**
 * @author Luis Ortiz
 */
public class CompanyDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_dropOrphanObjectTables();

		upgrade(new UpdateCompanyIdByGroupIdDataCleanupPreupgradeProcess());

		upgrade(
			new DefaultAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
				"companyId", "Company"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null,
				"[$SOURCE_TABLE_ALIAS$].ownerType = " +
					PortletKeys.PREFS_OWNER_TYPE_COMPANY,
				"ownerId", "PortalPreferences", "companyId", "Company"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null,
				"[$SOURCE_TABLE_ALIAS$].ownerType = " +
					PortletKeys.PREFS_OWNER_TYPE_COMPANY,
				"ownerId", "PortletPreferences", "companyId", "Company"));
	}

	protected class UpdateCompanyIdByGroupIdDataCleanupPreupgradeProcess
		extends BaseAllTablesOrphanReferencesDataCleanupPreupgradeProcess {

		public UpdateCompanyIdByGroupIdDataCleanupPreupgradeProcess() {
			super("groupId", "Group_");
		}

		@Override
		protected void cleanUp(
				String sourceColumnName, String sourceTableName,
				String[] targetColumnNames, String targetTableName)
			throws Exception {

			DBInspector dbInspector = new DBInspector(connection);

			String companyIdColumnName = dbInspector.normalizeName("companyId");

			if (!dbInspector.hasColumn(sourceTableName, companyIdColumnName)) {
				return;
			}

			String updateSql = _getUpdateSQL(
				companyIdColumnName, sourceColumnName, sourceTableName,
				targetColumnNames[0], targetTableName);

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(updateSql)) {

				int count = preparedStatement.executeUpdate();

				DataCleanupLoggingUtil.logUpdate(
					_log, count, sourceTableName, companyIdColumnName, null,
					"missing values were populated from table " +
						targetTableName);
			}
		}

		private String _getUpdateSQL(
			String companyIdColumnName, String sourceColumnName,
			String sourceTableName, String targetColumnName,
			String targetTableName) {

			if ((DBManagerUtil.getDBType() == DBType.MARIADB) ||
				(DBManagerUtil.getDBType() == DBType.MYSQL)) {

				return StringBundler.concat(
					"update ", sourceTableName, " inner join ", targetTableName,
					" on ", targetTableName, StringPool.PERIOD,
					targetColumnName, " = ", sourceTableName, StringPool.PERIOD,
					sourceColumnName, " set ", sourceTableName,
					StringPool.PERIOD, companyIdColumnName, " = ",
					targetTableName, StringPool.PERIOD, companyIdColumnName,
					" where coalesce(", sourceTableName, StringPool.PERIOD,
					companyIdColumnName, ", 0) = 0 and ", sourceTableName,
					StringPool.PERIOD, sourceColumnName, " > 0");
			}

			String setSubquery = StringBundler.concat(
				"(select distinct ", companyIdColumnName, " from ",
				targetTableName, " where ", targetTableName, StringPool.PERIOD,
				targetColumnName, " = ", sourceTableName, StringPool.PERIOD,
				sourceColumnName, ")");

			String existsSubquery = StringBundler.concat(
				"exists (select 1 from ", targetTableName, " where ",
				targetTableName, StringPool.PERIOD, targetColumnName, " = ",
				sourceTableName, StringPool.PERIOD, sourceColumnName, ")");

			return StringBundler.concat(
				"update ", sourceTableName, " set ", companyIdColumnName, " = ",
				setSubquery, " where coalesce(", companyIdColumnName,
				", 0) = 0 and ", sourceColumnName, " > 0 and ", existsSubquery);
		}

	}

	private void _dropOrphanObjectTables() throws Exception {
		Set<Long> companyIds = SetUtil.fromArray(
			PortalInstancePool.getCompanyIds());

		DBInspector dbInspector = new DBInspector(connection);

		List<String> tableNames = dbInspector.getTableNames(null);

		for (String tableName : tableNames) {
			long companyId = _getCompanyIdFromTableName(tableName);

			if ((companyId <= 0) || companyIds.contains(companyId)) {
				continue;
			}

			dropTable(tableName);

			DataCleanupLoggingUtil.logDrop(
				_log, tableName,
				"it belonged to a nonexistent company: " + companyId);
		}
	}

	private long _getCompanyIdFromTableName(String tableName) {
		String[] tableNameParts = StringUtil.split(
			tableName, StringPool.UNDERLINE);

		if ((tableNameParts.length > 1) &&
			(StringUtil.equalsIgnoreCase(tableNameParts[0], "l") ||
			 StringUtil.equalsIgnoreCase(tableNameParts[0], "o"))) {

			return GetterUtil.getLong(tableNameParts[1]);
		}

		if ((tableNameParts.length > 2) &&
			StringUtil.equalsIgnoreCase(
				tableNameParts[tableNameParts.length - 2], "x")) {

			return GetterUtil.getLong(
				tableNameParts[tableNameParts.length - 1]);
		}

		return -1;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyDataCleanupPreupgradeProcess.class);

}