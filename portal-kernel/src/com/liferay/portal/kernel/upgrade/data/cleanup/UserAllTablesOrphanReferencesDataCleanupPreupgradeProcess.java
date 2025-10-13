/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.DataCleanupLoggingUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

/**
 * @author Luis Ortiz
 */
public class UserAllTablesOrphanReferencesDataCleanupPreupgradeProcess
	extends BaseAllTablesOrphanReferencesDataCleanupPreupgradeProcess {

	public UserAllTablesOrphanReferencesDataCleanupPreupgradeProcess() {
		super("userId", "User_");
	}

	@Override
	protected void cleanUp(
			String sourceColumnName, String sourceTableName,
			String[] targetColumnNames, String targetTableName)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		if (!dbInspector.hasColumn(sourceTableName, "companyId")) {
			return;
		}

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select [$SOURCE_TABLE_ALIAS$].", sourceColumnName, ", ",
					"[$SOURCE_TABLE_ALIAS$].companyId, count(1) from ",
					sourceTableName, " [$SOURCE_TABLE_ALIAS$]",
					OrphanReferencesDataCleanupUtil.getWhereClause(
						connection, null, sourceColumnName, sourceTableName,
						targetColumnNames, targetTableName),
					" group by [$SOURCE_TABLE_ALIAS$].", sourceColumnName,
					", [$SOURCE_TABLE_ALIAS$].companyId"));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				StringBundler.concat(
					"delete from ", sourceTableName, " where ",
					sourceColumnName, " = ? and companyId = ?"));
			PreparedStatement preparedStatement3 = connection.prepareStatement(
				StringBundler.concat(
					"update ", sourceTableName, " set ", sourceColumnName,
					" = ? where ", sourceColumnName, " = ? and companyId = ?"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				long companyId = resultSet.getLong(2);
				long count = resultSet.getLong(3);
				long userId = resultSet.getLong(1);

				if (_isPartOfUniqueIndex(
						connection, sourceColumnName, sourceTableName)) {

					preparedStatement2.setLong(1, userId);
					preparedStatement2.setLong(2, companyId);

					preparedStatement2.executeUpdate();

					DataCleanupLoggingUtil.logDelete(
						_log, count, sourceTableName,
						StringBundler.concat(
							sourceColumnName, StringPool.SPACE, userId,
							" was not found in column",
							(targetColumnNames.length > 1) ? "s " : " ",
							String.join(", ", targetColumnNames),
							" from table ", targetTableName));

					continue;
				}

				long newUserId = 0;

				try {
					newUserId = _getAdminUserId(connection, companyId);
				}
				catch (NoSuchUserException noSuchUserException) {
					if (_log.isWarnEnabled()) {
						_log.warn(noSuchUserException);
					}

					continue;
				}

				preparedStatement3.setLong(1, newUserId);

				preparedStatement3.setLong(2, userId);
				preparedStatement3.setLong(3, companyId);

				preparedStatement3.executeUpdate();

				DataCleanupLoggingUtil.logUpdate(
					_log, count, sourceTableName, sourceColumnName, newUserId,
					StringBundler.concat(
						sourceColumnName, StringPool.SPACE, userId,
						" was not found in column",
						(targetColumnNames.length > 1) ? "s " : " ",
						String.join(", ", targetColumnNames), " from table ",
						targetTableName));
			}
		}
	}

	private long _getAdminUserId(Connection connection, long companyId)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		boolean hasColumn = dbInspector.hasColumn("User_", "type_");

		StringBundler sb = new StringBundler(6);

		sb.append("select User_.userId from User_ inner join Users_Roles on ");
		sb.append("User_.userId = Users_Roles.userId inner join Role_ on ");
		sb.append("Users_Roles.roleId = Role_.roleId where Role_.name = ? ");
		sb.append("and User_.companyId = ? and Role_.companyId = ?");

		if (hasColumn) {
			sb.append(" and User_.type_ = ?");
		}

		sb.append(" order by User_.userId asc");

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sb.toString())) {

			preparedStatement.setString(1, RoleConstants.ADMINISTRATOR);
			preparedStatement.setLong(2, companyId);
			preparedStatement.setLong(3, companyId);

			if (hasColumn) {
				preparedStatement.setInt(4, UserConstants.TYPE_REGULAR);
			}

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (!resultSet.next()) {
					throw new NoSuchUserException(
						"No admin user found for company " + companyId);
				}

				return resultSet.getLong(1);
			}
		}
	}

	private boolean _isPartOfUniqueIndex(
			Connection connection, String sourceColumnName,
			String sourceTableName)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		List<IndexMetadata> indexes = db.getIndexMetadatas(
			connection, sourceTableName, sourceColumnName, true);

		if (!indexes.isEmpty()) {
			return true;
		}

		String[] columnNames = db.getPrimaryKeyColumnNames(
			connection, sourceTableName);

		return ArrayUtil.contains(columnNames, sourceColumnName);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserAllTablesOrphanReferencesDataCleanupPreupgradeProcess.class);

}