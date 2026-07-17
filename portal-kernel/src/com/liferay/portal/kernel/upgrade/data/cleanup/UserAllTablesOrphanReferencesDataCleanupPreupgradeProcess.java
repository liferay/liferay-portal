/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade.data.cleanup;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

		DB db = DBManagerUtil.getDB();

		List<SafeCloseable> safeCloseables =
			OrphanReferencesDataCleanupUtil.addTemporaryIndexes(
				new String[] {sourceColumnName}, connection, db,
				sourceTableName);

		safeCloseables.addAll(
			OrphanReferencesDataCleanupUtil.addTemporaryIndexes(
				targetColumnNames, connection, db, targetTableName));

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ",
					OrphanReferencesDataCleanupUtil.getSourceTableAlias(),
					StringPool.PERIOD, sourceColumnName, ", ",
					OrphanReferencesDataCleanupUtil.getSourceTableAlias(),
					".companyId, count(1) as count from ", sourceTableName, " ",
					OrphanReferencesDataCleanupUtil.getSourceTableAlias(),
					OrphanReferencesDataCleanupUtil.getWhereClause(
						connection, null, null, sourceColumnName,
						sourceTableName, targetColumnNames, targetTableName),
					" group by ",
					OrphanReferencesDataCleanupUtil.getSourceTableAlias(),
					StringPool.PERIOD, sourceColumnName, ", ",
					OrphanReferencesDataCleanupUtil.getSourceTableAlias(),
					".companyId"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"delete from ", sourceTableName, " where ",
						sourceColumnName, " = ? and companyId = ?"));
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"update ", sourceTableName, " set ", sourceColumnName,
						" = ? where ", sourceColumnName,
						" = ? and companyId = ?"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			boolean partOfUniqueIndex = _isPartOfUniqueIndex(
				connection, sourceColumnName, sourceTableName);

			while (resultSet.next()) {
				long companyId = resultSet.getLong("companyId");
				long count = resultSet.getLong("count");
				long userId = resultSet.getLong(sourceColumnName);

				if (_deleteTableNames.contains(sourceTableName) ||
					partOfUniqueIndex) {

					preparedStatement2.setLong(1, userId);
					preparedStatement2.setLong(2, companyId);

					preparedStatement2.addBatch();

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

				long newUserId = _getAdminUserId(connection, companyId);

				if (newUserId == 0) {
					continue;
				}

				preparedStatement3.setLong(1, newUserId);

				preparedStatement3.setLong(2, userId);
				preparedStatement3.setLong(3, companyId);

				preparedStatement3.addBatch();

				DataCleanupLoggingUtil.logUpdate(
					_log, count, sourceTableName, sourceColumnName, newUserId,
					StringBundler.concat(
						sourceColumnName, StringPool.SPACE, userId,
						" was not found in column",
						(targetColumnNames.length > 1) ? "s " : " ",
						String.join(", ", targetColumnNames), " from table ",
						targetTableName));
			}

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();
		}
		finally {
			for (SafeCloseable safeCloseable : safeCloseables) {
				safeCloseable.close();
			}
		}
	}

	private long _getAdminUserId(Connection connection, long companyId)
		throws Exception {

		Long adminUserId = _adminUserIds.get(companyId);

		if (adminUserId != null) {
			return adminUserId;
		}

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
				long userId = 0;

				if (resultSet.next()) {
					userId = resultSet.getLong(1);
				}
				else {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"No admin user found for company " + companyId);
					}
				}

				_adminUserIds.put(companyId, userId);

				return userId;
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

	private static final Set<String> _deleteTableNames = new TreeSet<>(
		String.CASE_INSENSITIVE_ORDER) {

		{
			addAll(
				Set.of(
					"MFAEmailOTPEntry", "MFAFIDO2CredentialEntry",
					"MFATimeBasedOTPEntry", "OAuth2Authorization",
					"OpenIdConnectSession", "OpenIdConnectUser",
					"SamlIdpSpSession", "SamlIdpSsoSession", "SamlPeerBinding",
					"SamlSpSession"));
		}
	};

	private final Map<Long, Long> _adminUserIds = new HashMap<>();

}