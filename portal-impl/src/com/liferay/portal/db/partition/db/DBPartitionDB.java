/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alberto Chaparro
 */
public interface DBPartitionDB {

	public default String getCatalog(
			Connection connection, String partitionName)
		throws SQLException {

		DBInspector dbInspector = new DBInspector(connection);

		return dbInspector.getCatalog();
	}

	public String getCreatePartitionSQL(
			Connection connection, String partitionName)
		throws SQLException;

	public default List<String> getCreateRulesSQL(String partitionName)
		throws SQLException {

		return new ArrayList<>();
	}

	public default String getCreateTableSQL(
			Connection connection, String fromPartitionName,
			String toPartitionName, String tableName)
		throws SQLException {

		return getCreateTableSQL(
			connection, fromPartitionName, toPartitionName, tableName,
			tableName);
	}

	public String getCreateTableSQL(
			Connection connection, String fromPartitionName,
			String toPartitionName, String toTableName, String fromTableName)
		throws SQLException;

	public default String getCreateViewSQL(
		String fromPartitionName, String toPartitionName, String viewName) {

		return StringBundler.concat(
			"create or replace view ", toPartitionName, StringPool.PERIOD,
			viewName, " as select * from ", fromPartitionName,
			StringPool.PERIOD, viewName);
	}

	public String getDefaultPartitionName(Connection connection)
		throws SQLException;

	public String getDropPartitionSQL(String partitionName);

	public default String getDropTableSQL(
		String partitionName, String tableName) {

		return StringBundler.concat(
			"drop table if exists ", partitionName, StringPool.PERIOD,
			tableName, " cascade");
	}

	public default String getDropViewSQL(
		String partitionName, String viewName) {

		return StringBundler.concat(
			"drop view if exists ", partitionName, StringPool.PERIOD, viewName);
	}

	public String[] getRenamePartitionSQL(
			Connection connection, String sourcePartitionName,
			String targetPartitionName)
		throws SQLException;

	public default String getSafeAlterTable(String alterTableSQL) {
		return alterTableSQL;
	}

	public default String getSchema(
		Connection connection, String partitionName) {

		DBInspector dbInspector = new DBInspector(connection);

		return dbInspector.getSchema();
	}

	public boolean isDDLTransactional();

	public void setPartition(Connection connection, String partitionName)
		throws SQLException;

}