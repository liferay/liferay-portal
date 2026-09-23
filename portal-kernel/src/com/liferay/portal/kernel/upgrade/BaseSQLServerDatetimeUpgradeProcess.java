/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;

import java.util.Map;

/**
 * @author José Ángel Jiménez
 */
public class BaseSQLServerDatetimeUpgradeProcess extends UpgradeProcess {

	public BaseSQLServerDatetimeUpgradeProcess(Class<?>[] tableClasses) {
		_tableClasses = tableClasses;
	}

	@Override
	protected void doUpgrade() throws Exception {
		if (DBManagerUtil.getDBType() != DBType.SQLSERVER) {
			return;
		}

		for (Class<?> tableClass : _tableClasses) {
			_upgradeTable(tableClass);
		}
	}

	private void _upgradeTable(Class<?> tableClass) throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		String catalog = dbInspector.getCatalog();
		String schema = dbInspector.getSchema();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		String tableName = dbInspector.normalizeName(
			getTableName(tableClass), databaseMetaData);

		try (ResultSet tableResultSet = databaseMetaData.getTables(
				catalog, schema, tableName, null)) {

			if (!tableResultSet.next()) {
				_log.error(
					StringBundler.concat(
						"Table ", tableName, " does not exist"));

				return;
			}

			String newTypeName = dbInspector.normalizeName(_NEW_TYPE);

			String newTypeDefinition = StringBundler.concat(
				newTypeName, "(", _NEW_SIZE, ")");

			Map<String, Integer> tableColumnsMap = getTableColumnsMap(
				tableClass);

			for (Map.Entry<String, Integer> entry :
					tableColumnsMap.entrySet()) {

				if (entry.getValue() != Types.TIMESTAMP) {
					continue;
				}

				String columnName = dbInspector.normalizeName(
					entry.getKey(), databaseMetaData);

				try (ResultSet columnResultSet = databaseMetaData.getColumns(
						null, null, tableName, columnName)) {

					if (!columnResultSet.next()) {
						_log.error(
							StringBundler.concat(
								"Column ", columnName,
								" does not exist in table ", tableName));

						continue;
					}

					if (newTypeName.equals(
							columnResultSet.getString("TYPE_NAME")) &&
						(_NEW_SIZE == columnResultSet.getInt(
							"DECIMAL_DIGITS"))) {

						if (_log.isInfoEnabled()) {
							_log.info(
								StringBundler.concat(
									"Column ", columnName, " in table ",
									tableName, " already is ",
									newTypeDefinition));
						}

						continue;
					}

					alterColumnType(tableName, columnName, newTypeDefinition);
				}
			}
		}
	}

	private static final int _NEW_SIZE = 6;

	private static final String _NEW_TYPE = "datetime2";

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSQLServerDatetimeUpgradeProcess.class);

	private final Class<?>[] _tableClasses;

}