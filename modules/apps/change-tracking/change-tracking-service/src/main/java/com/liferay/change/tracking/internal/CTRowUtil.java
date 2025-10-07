/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import java.io.Serializable;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Preston Crary
 */
public class CTRowUtil {

	public static int copyCTRows(
			CTPersistence<?> ctPersistence, Connection connection,
			String selectSQL)
		throws SQLException {

		Map<String, Integer> tableColumnsMap =
			ctPersistence.getTableColumnsMap();

		if (_isPostgresBlobTable(tableColumnsMap)) {
			StringBundler sb = new StringBundler(
				(3 * tableColumnsMap.size()) + 4);

			sb.append("insert into ");
			sb.append(ctPersistence.getTableName());
			sb.append(" (");

			for (String columnName : tableColumnsMap.keySet()) {
				sb.append(columnName);
				sb.append(", ");
			}

			sb.setStringAt(") values (?", sb.index() - 1);

			for (int i = 1; i < tableColumnsMap.size(); i++) {
				sb.append(", ?");
			}

			sb.append(")");

			try (PreparedStatement selectPreparedStatement =
					connection.prepareStatement(selectSQL);
				PreparedStatement insertPreparedStatement =
					connection.prepareStatement(sb.toString());
				ResultSet resultSet = selectPreparedStatement.executeQuery()) {

				while (resultSet.next()) {
					int parameterIndex = 1;

					for (int type : tableColumnsMap.values()) {
						if (type == Types.BLOB) {
							Blob blob = resultSet.getBlob(parameterIndex);

							insertPreparedStatement.setBlob(
								parameterIndex, blob.getBinaryStream());
						}
						else {
							insertPreparedStatement.setObject(
								parameterIndex,
								resultSet.getObject(parameterIndex));
						}

						parameterIndex++;
					}

					insertPreparedStatement.addBatch();
				}

				int result = 0;

				for (int count : insertPreparedStatement.executeBatch()) {
					result += count;
				}

				return result;
			}
		}

		StringBundler sb = new StringBundler((2 * tableColumnsMap.size()) + 4);

		sb.append("insert into ");
		sb.append(ctPersistence.getTableName());
		sb.append(" (");

		for (String name : tableColumnsMap.keySet()) {
			sb.append(name);
			sb.append(", ");
		}

		sb.setStringAt(") ", sb.index() - 1);

		sb.append(selectSQL);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sb.toString())) {

			return preparedStatement.executeUpdate();
		}
	}

	public static String getConstraintConflictsSQL(
		String tableName, String primaryColumnName,
		String[] uniqueIndexColumnNames, long targetCTCollectionId) {

		StringBundler sb = new StringBundler(
			(3 * uniqueIndexColumnNames.length) + 9);

		sb.append("select ");
		sb.append(primaryColumnName);
		sb.append(" from ");
		sb.append(tableName);
		sb.append(" where ctCollectionId = ");
		sb.append(targetCTCollectionId);
		sb.append(" and ");
		sb.append(primaryColumnName);
		sb.append(" != ?");

		for (String uniqueIndexColumnName : uniqueIndexColumnNames) {
			sb.append(" and ");
			sb.append(uniqueIndexColumnName);
			sb.append(" = ?");
		}

		return sb.toString();
	}

	public static String getConstraintEntitiesSQL(
		String tableName, String primaryColumnName,
		String[] uniqueIndexColumnNames, long ctCollectionId,
		Set<Long> primaryKeys) {

		StringBundler sb = new StringBundler();

		sb.append("select ");
		sb.append(primaryColumnName);
		sb.append(", ");

		for (String uniqueIndexColumnName : uniqueIndexColumnNames) {
			sb.append(uniqueIndexColumnName);
			sb.append(", ");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(" from ");
		sb.append(tableName);
		sb.append(" where ctCollectionId = ");
		sb.append(ctCollectionId);
		sb.append(" and (");
		sb.append(primaryColumnName);
		sb.append(" in (");

		int i = 0;

		for (Serializable primaryKey : primaryKeys) {
			if (i == _BATCH_SIZE) {
				sb.setStringAt(")", sb.index() - 1);

				sb.append(" or ");
				sb.append(primaryColumnName);
				sb.append(" in (");

				i = 0;
			}

			sb.append(primaryKey);
			sb.append(", ");

			i++;
		}

		sb.setStringAt(")", sb.index() - 1);

		sb.append(")");

		return sb.toString();
	}

	public static String getUpdateMVCCVersionSQL(
		long ctCollectionId, List<Serializable> primaryKeys,
		String primaryKeyName, String tableName) {

		StringBundler sb = new StringBundler();

		sb.append("select ");
		sb.append(primaryKeyName);
		sb.append(", mvccVersion from ");
		sb.append(tableName);
		sb.append(" where ctCollectionId = ");
		sb.append(ctCollectionId);
		sb.append(" and (");
		sb.append(primaryKeyName);
		sb.append(" in (");

		int i = 0;

		for (Serializable serializable : primaryKeys) {
			if (i == _BATCH_SIZE) {
				sb.setStringAt(")", sb.index() - 1);

				sb.append(" or ");
				sb.append(primaryKeyName);
				sb.append(" in (");

				i = 0;
			}

			sb.append(serializable);
			sb.append(", ");

			i++;
		}

		sb.setStringAt(")", sb.index() - 1);

		sb.append(")");

		return sb.toString();
	}

	private static boolean _isPostgresBlobTable(
		Map<String, Integer> tableColumnsMap) {

		if (DBManagerUtil.getDBType() != DBType.POSTGRESQL) {
			return false;
		}

		Collection<Integer> values = tableColumnsMap.values();

		return values.contains(Types.BLOB);
	}

	private CTRowUtil() {
	}

	private static final int _BATCH_SIZE = 1000;

}