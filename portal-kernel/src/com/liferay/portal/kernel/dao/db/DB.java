/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.util.ObjectValuePair;

import java.io.IOException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface DB {

	public static final int SQL_SIZE_NONE = -1;

	public static final int SQL_VARCHAR_MAX_SIZE = Integer.MAX_VALUE;

	public static final int SQL_VARCHAR_MAX_SIZE_THRESHOLD = 9999999;

	public void addIndexes(
			Connection connection, List<IndexMetadata> indexMetadatas)
		throws IOException, SQLException;

	public void alterColumnName(
			Connection connection, String tableName, String oldColumnName,
			String newColumnDefinition)
		throws Exception;

	public void alterColumnType(
			Connection connection, String tableName, String columnName,
			String newColumnType)
		throws Exception;

	public void alterTableAddColumn(
			Connection connection, String tableName, String columnName,
			String columnType)
		throws Exception;

	public void alterTableDropColumn(
			Connection connection, String tableName, String columnName)
		throws Exception;

	public String buildSQL(String template) throws IOException, SQLException;

	public void copyTableRows(
			Connection connection, String sourceTableName,
			String targetTableName, Map<String, String> columnNamesMap,
			Map<String, String> defaultValuesMap)
		throws Exception;

	public void copyTableStructure(
			Connection connection, String tableName, String newTableName)
		throws Exception;

	public void dropIndexes(
			Connection connection, List<String> indexNames, String tableName)
		throws Exception;

	public List<IndexMetadata> dropIndexes(
			Connection connection, String tableName, String columnName)
		throws IOException, SQLException;

	public String getCharacterSet(Connection connection) throws SQLException;

	public DBType getDBType();

	public String getDefaultValue(String columnDef);

	public List<Index> getIndexes(Connection connection) throws SQLException;

	public List<IndexMetadata> getIndexMetadatas(
			Connection connection, String tableName, String columnName,
			boolean onlyUnique)
		throws SQLException;

	public ResultSet getIndexResultSet(
			Connection connection, String tableName, boolean onlyUnique)
		throws SQLException;

	public int getMajorVersion();

	public int getMinorVersion();

	public default String getNewUuidFunctionName() {
		return null;
	}

	public String getPopulateSQL(String databaseName, String sqlContent);

	public String[] getPrimaryKeyColumnNames(
			Connection connection, String tableName)
		throws SQLException;

	public String getRecreateSQL(String databaseName);

	public Integer getSQLType(String templateType);

	public Integer getSQLTypeDecimalDigits(String templateType);

	public Integer getSQLTypeSize(String templateType);

	public String getTemplateBlob();

	public String getTemplateFalse();

	public String getTemplateTrue();

	public String getVersionString();

	public boolean isSupportsAlterColumnName();

	public boolean isSupportsAlterColumnType();

	public boolean isSupportsCharacterSet(Connection connection)
		throws SQLException;

	public boolean isSupportsDBPartition();

	public boolean isSupportsInlineDistinct();

	public default boolean isSupportsNewUuidFunction() {
		return false;
	}

	public boolean isSupportsQueryingAfterException();

	public boolean isSupportsScrollableResults();

	public boolean isSupportsStringCaseSensitiveQuery();

	public boolean isSupportsUpdateWithInnerJoin();

	public void process(UnsafeConsumer<Long, Exception> unsafeConsumer)
		throws Exception;

	public void removePrimaryKey(Connection connection, String tableName)
		throws Exception;

	public void renameTables(
			Connection connection,
			ObjectValuePair<String, String>... tableNameObjectValuePairs)
		throws Exception;

	public default void runSQL(
			Connection connection, DBTypeToSQLMap dbTypeToSQLMap)
		throws IOException, SQLException {

		String sql = dbTypeToSQLMap.get(getDBType());

		runSQL(connection, new String[] {sql});
	}

	public void runSQL(Connection connection, String sql)
		throws IOException, SQLException;

	public void runSQL(Connection connection, String[] sqls)
		throws IOException, SQLException;

	public default void runSQL(DBTypeToSQLMap dbTypeToSQLMap)
		throws IOException, SQLException {

		String sql = dbTypeToSQLMap.get(getDBType());

		runSQL(new String[] {sql});
	}

	public void runSQL(String sql) throws IOException, SQLException;

	public void runSQL(String[] sqls) throws IOException, SQLException;

	public void runSQLTemplate(
			Connection connection, String template, boolean failOnError)
		throws IOException, NamingException, SQLException;

	public void runSQLTemplate(String template, boolean failOnError)
		throws IOException, NamingException, SQLException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #runSQLTemplate(Connection, String, boolean)}
	 */
	@Deprecated
	public default void runSQLTemplateString(
			Connection connection, String template, boolean failOnError)
		throws IOException, NamingException, SQLException {

		runSQLTemplate(connection, template, failOnError);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #runSQLTemplate(String, boolean)}
	 */
	@Deprecated
	public default void runSQLTemplateString(
			String template, boolean failOnError)
		throws IOException, NamingException, SQLException {

		runSQLTemplate(template, failOnError);
	}

	public void setSupportsStringCaseSensitiveQuery(
		boolean supportsStringCaseSensitiveQuery);

	public AutoCloseable syncTables(
			Connection connection, String sourceTableName,
			String targetTableName, Map<String, String> columnNamesMap,
			Map<String, String> defaultValuesMap)
		throws Exception;

	public void updateIndexes(
			Connection connection, String tableName, String indexesSQL,
			boolean dropStaleIndexes)
		throws Exception;

	public void updatePrimaryKey(
			Connection connection, String tableName,
			String[] primaryKeyColumnNames)
		throws Exception;

}