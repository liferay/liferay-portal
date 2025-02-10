/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alberto Chaparro
 */
public class DBPartitionMySQLDB implements DBPartitionDB {

	@Override
	public String getCatalog(Connection connection, String partitionName)
		throws SQLException {

		return partitionName;
	}

	@Override
	public String getCreatePartitionSQL(
			Connection connection, String partitionName)
		throws SQLException {

		return StringBundler.concat(
			"create schema if not exists ", partitionName, " character set ",
			_getSessionCharsetEncoding(connection));
	}

	@Override
	public String getCreateTableSQL(
		Connection connection, String fromPartitionName, String toPartitionName,
		String fromTableName, String toTableName) {

		return StringBundler.concat(
			"create table if not exists ", toPartitionName, StringPool.PERIOD,
			toTableName, " like ", fromPartitionName, StringPool.PERIOD,
			fromTableName);
	}

	@Override
	public String getDefaultPartitionName(Connection connection)
		throws SQLException {

		if (_defaultPartitionName == null) {
			_defaultPartitionName = connection.getCatalog();
		}

		return _defaultPartitionName;
	}

	@Override
	public String getDropPartitionSQL(String partitionName) {
		return "drop schema if exists " + partitionName;
	}

	@Override
	public String[] getRenamePartitionSQL(
			Connection connection, String sourcePartitionName,
			String targetPartitionName)
		throws SQLException {

		List<String> queries = new ArrayList<>();

		queries.add(getCreatePartitionSQL(connection, targetPartitionName));

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		try (ResultSet resultSet = databaseMetaData.getTables(
				getCatalog(connection, sourcePartitionName),
				getSchema(connection, sourcePartitionName), null,
				new String[] {"TABLE"})) {

			while (resultSet.next()) {
				String tableName = resultSet.getString("TABLE_NAME");

				queries.add(
					StringBundler.concat(
						"rename table ",
						sourcePartitionName + StringPool.PERIOD, tableName,
						" to ", targetPartitionName, StringPool.PERIOD,
						tableName, StringPool.SEMICOLON));
			}
		}

		queries.add(getDropPartitionSQL(sourcePartitionName));

		return queries.toArray(new String[0]);
	}

	@Override
	public boolean isDDLTransactional() {
		return false;
	}

	@Override
	public void setPartition(Connection connection, String partitionName)
		throws SQLException {

		connection.setCatalog(partitionName);
	}

	private String _getSessionCharsetEncoding(Connection connection)
		throws SQLException {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select variable_value from " +
					"performance_schema.session_variables where " +
						"variable_name = 'character_set_client'");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				return resultSet.getString("variable_value");
			}

			return "utf8";
		}
	}

	private static String _defaultPartitionName;

}