/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Preston Crary
 */
public class DBColumnSizeUpgradeProcess extends UpgradeProcess {

	public DBColumnSizeUpgradeProcess(
		DBType dbType, String oldTypeName, int oldSize, int oldDecimalDigits,
		String newColumnType) {

		this(dbType, oldTypeName, oldSize, newColumnType);

		_oldDecimalDigits = oldDecimalDigits;
	}

	public DBColumnSizeUpgradeProcess(
		DBType dbType, String oldTypeName, int oldSize, String newColumnType) {

		_dbType = dbType;
		_oldTypeName = oldTypeName;
		_oldSize = oldSize;
		_newColumnType = newColumnType;
	}

	@Override
	protected void doUpgrade() throws Exception {
		if (DBManagerUtil.getDBType() == _dbType) {
			_upgradeTables();
		}
	}

	private void _upgradeTables() throws Exception {
		DatabaseMetaData databaseMetaData = connection.getMetaData();

		DBInspector dbInspector = new DBInspector(connection);

		String catalog = dbInspector.getCatalog();
		String schema = dbInspector.getSchema();

		List<String> tableColumnNames = new ArrayList<>();

		try (LoggingTimer loggingTimer = new LoggingTimer();
			ResultSet tableResultSet = databaseMetaData.getTables(
				catalog, schema, null, new String[] {"TABLE"})) {

			while (tableResultSet.next()) {
				String tableName = tableResultSet.getString("TABLE_NAME");

				try (ResultSet columnResultSet = databaseMetaData.getColumns(
						catalog, schema, tableName, null)) {

					while (columnResultSet.next()) {
						int decimalDigits = columnResultSet.getInt(
							"DECIMAL_DIGITS");
						int size = columnResultSet.getInt("COLUMN_SIZE");

						if (((_oldDecimalDigits != null) &&
							 (decimalDigits != _oldDecimalDigits)) ||
							(size != _oldSize) ||
							!StringUtil.equalsIgnoreCase(
								_oldTypeName,
								columnResultSet.getString("TYPE_NAME"))) {

							continue;
						}

						tableColumnNames.add(
							tableName + StringPool.PERIOD +
								columnResultSet.getString("COLUMN_NAME"));
					}
				}
			}
		}

		for (String tableColumnName : tableColumnNames) {
			String[] splits = StringUtil.split(
				tableColumnName, StringPool.PERIOD);

			try {
				alterColumnType(splits[0], splits[1], _newColumnType);
			}
			catch (SQLException sqlException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to alter length of column ", splits[0],
							" for table ", splits[1]),
						sqlException);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DBColumnSizeUpgradeProcess.class);

	private final DBType _dbType;
	private final String _newColumnType;
	private Integer _oldDecimalDigits;
	private final int _oldSize;
	private final String _oldTypeName;

}