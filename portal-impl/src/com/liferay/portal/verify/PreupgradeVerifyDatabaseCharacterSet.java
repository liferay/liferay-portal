/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.DBResourceUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Set;

/**
 * @author Jorge Avalos
 */
public class PreupgradeVerifyDatabaseCharacterSet
	extends PreupgradeVerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		DB db = DBManagerUtil.getDB();

		if (!db.isSupportsCharacterSet(connection)) {
			throw new VerifyException(
				"Unsupported database character set: " +
					db.getCharacterSet(connection));
		}

		if (!(db.getDBType() == DBType.MYSQL) ||
			!(db.getDBType() == DBType.MARIADB)) return;

			Set<String> portalTables =
				DBResourceUtil.getServiceComponentModuleTableNames(connection);

			portalTables.addAll(DBResourceUtil.getPortalTableNames(connection));

			portalTables.addAll(DBResourceUtil.getModuleTableNames(connection));

			String sql = StringBundler.concat(
				"select character_set_name, collation_name, table_name from ",
				"information_schema.columns join information_schema.schemata ",
				"on information_schema.columns.table_schema = ",
				"information_schema.schemata.schema_name where ",
				"information_schema.columns.table_schema = database() and ",
				"information_schema.columns.collation_name is not null and",
				"(information_schema.columns.character_set_name != ",
				"information_schema.schemata.default_character_set_name or ",
				"information_schema.columns.collation_name != ",
				"information_schema.schemata.default_collation_name)");

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(sql)) {

				ResultSet resultSet = preparedStatement.executeQuery();

				while (resultSet.next()) {
					DBInspector dbInspector = new DBInspector(connection);

					String tableName = resultSet.getString("table_name");

					if (portalTables.contains(
							dbInspector.normalizeName(tableName))) {

						throw new VerifyException(
							StringBundler.concat(
								"Mixed database character set and collation:\n",
								resultSet.getString("character_set_name"),
								StringPool.FORWARD_SLASH,
								resultSet.getString("collation_name"), " on ",
								tableName));
					}
				}
			}
	}

	@Override
	protected boolean isSkipDBPartitions() {
		DB db = DBManagerUtil.getDB();

		if (db.getDBType() != DBType.MYSQL) {
			return true;
		}

		return false;
	}

}