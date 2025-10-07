/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.test.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DatabaseTestUtil {

	public static void createSchema(String schemaName) throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL("create schema " + schemaName);
	}

	public static void destroyDataSource(DataSource dataSource)
		throws Exception {

		DataSourceFactoryUtil.destroyDataSource(dataSource);
	}

	public static void dropSchema(String schemaName) throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL("drop schema " + schemaName + " cascade");
	}

	public static List<String> getIndexColumnNames(DataSource dataSource)
		throws Exception {

		List<String> indexColumnNames = new ArrayList<>();

		DB db = DBManagerUtil.getDB();

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getTables(
					connection.getCatalog(), connection.getSchema(), null,
					new String[] {"TABLE"})) {

				while (resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");

					indexColumnNames.addAll(
						_getIndexColumnNames(connection, db, tableName, false));
				}
			}
		}

		Collections.sort(indexColumnNames);

		return indexColumnNames;
	}

	public static String getSchemaURL(String schemaName) {
		String jdbcURL = PropsValues.JDBC_DEFAULT_URL;

		if (jdbcURL.contains("?")) {
			return jdbcURL + "&currentSchema=" + schemaName;
		}

		return jdbcURL + "?currentSchema=" + schemaName;
	}

	public static List<String> getTableColumnNames(DataSource dataSource)
		throws Exception {

		List<String> tableColumnNames = new ArrayList<>();

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getColumns(
					connection.getCatalog(), connection.getSchema(), null,
					null)) {

				while (resultSet.next()) {
					tableColumnNames.add(
						StringUtil.merge(
							new Object[] {
								resultSet.getString("TABLE_NAME"),
								resultSet.getString("COLUMN_NAME"),
								resultSet.getInt("DATA_TYPE"),
								resultSet.getInt("COLUMN_SIZE"),
								resultSet.getInt("DECIMAL_DIGITS"),
								resultSet.getString("COLUMN_DEF"),
								resultSet.getString("IS_NULLABLE"),
								resultSet.getString("IS_AUTOINCREMENT")
							}));
				}
			}
		}

		Collections.sort(tableColumnNames);

		return tableColumnNames;
	}

	public static Set<String> getViewNames(DataSource dataSource)
		throws Exception {

		Set<String> viewNames = new HashSet<>();

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getTables(
					connection.getCatalog(), connection.getSchema(), null,
					new String[] {"VIEW"})) {

				while (resultSet.next()) {
					viewNames.add(resultSet.getString("TABLE_NAME"));
				}
			}
		}

		return viewNames;
	}

	public static void importFile(File file, DataSource targetDataSource)
		throws Exception {

		importSQL(FileUtil.read(file), targetDataSource);
	}

	public static void importSQL(String sql, DataSource targetDataSource)
		throws Exception {

		try (Connection connection = targetDataSource.getConnection()) {
			DB db = DBManagerUtil.getDB(
				DBManagerUtil.getDBType(), targetDataSource);

			db.runSQLTemplate(connection, sql, true);
		}
	}

	public static DataSource initDataSource(String schemaName)
		throws Exception {

		return DataSourceFactoryUtil.initDataSource(
			PropsValues.JDBC_DEFAULT_DRIVER_CLASS_NAME,
			getSchemaURL(schemaName), PropsValues.JDBC_DEFAULT_USERNAME,
			PropsValues.JDBC_DEFAULT_PASSWORD, StringPool.BLANK);
	}

	private static List<String> _getIndexColumnNames(
			Connection connection, DB db, String tableName, boolean unique)
		throws Exception {

		List<String> indexColumnNames = new ArrayList<>();

		try (ResultSet resultSet = db.getIndexResultSet(
				connection, tableName, unique)) {

			while (resultSet.next()) {
				indexColumnNames.add(
					StringUtil.merge(
						new Object[] {
							tableName, resultSet.getString("NON_UNIQUE"),
							resultSet.getString("INDEX_NAME"),
							resultSet.getString("COLUMN_NAME"),
							resultSet.getShort("ORDINAL_POSITION")
						}));
			}
		}

		return indexColumnNames;
	}

}