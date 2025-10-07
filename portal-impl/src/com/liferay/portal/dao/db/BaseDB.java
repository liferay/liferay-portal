/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.db.Index;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.db.IndexMetadataFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ThrowableCollector;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

/**
 * @author Alexander Chow
 * @author Ganesh Ram
 * @author Brian Wing Shun Chan
 * @author Daniel Kocsis
 */
public abstract class BaseDB implements DB {

	@Override
	public void addIndexes(
			Connection connection, List<IndexMetadata> indexMetadatas)
		throws IOException, SQLException {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		DBInspector dbInspector = new DBInspector(connection);

		Map<String, Map<String, Integer>> columnTableSizes = new HashMap<>();

		for (IndexMetadata indexMetadata : indexMetadatas) {
			String normalizedTableName = dbInspector.normalizeName(
				indexMetadata.getTableName(), databaseMetaData);

			if (_isSkipIndexOperation(connection, normalizedTableName)) {
				continue;
			}

			if (columnTableSizes.get(normalizedTableName) == null) {
				try (ResultSet resultSet = databaseMetaData.getColumns(
						dbInspector.getCatalog(), dbInspector.getSchema(),
						normalizedTableName, null)) {

					Map<String, Integer> columnSizes = new HashMap<>();

					while (resultSet.next()) {
						int columnType = resultSet.getInt("DATA_TYPE");

						if (!ArrayUtil.contains(
								SQL_VARCHAR_TYPES, columnType)) {

							continue;
						}

						columnSizes.put(
							dbInspector.normalizeName(
								resultSet.getString("COLUMN_NAME"),
								databaseMetaData),
							resultSet.getInt("COLUMN_SIZE"));
					}

					columnTableSizes.put(normalizedTableName, columnSizes);
				}
			}

			String[] columnNames = indexMetadata.getColumnNames();

			int[] columnSizes = new int[columnNames.length];

			for (int i = 0; i < columnNames.length; i++) {
				columnSizes[i] = MapUtil.getInteger(
					columnTableSizes.get(normalizedTableName), columnNames[i],
					0);
			}

			runSQL(
				_applyMaxStringIndexLengthLimitation(
					indexMetadata.getCreateSQL(columnSizes)));
		}
	}

	@Override
	public void alterColumnName(
			Connection connection, String tableName, String oldColumnName,
			String newColumnDefinition)
		throws Exception {

		StringBundler sb = new StringBundler(6);

		sb.append("alter_column_name ");
		sb.append(tableName);
		sb.append(StringPool.SPACE);
		sb.append(oldColumnName);
		sb.append(StringPool.SPACE);
		sb.append(newColumnDefinition);

		runSQL(connection, sb.toString());
	}

	@Override
	public void alterColumnType(
			Connection connection, String tableName, String columnName,
			String newColumnType)
		throws Exception {

		StringBundler sb = new StringBundler(6);

		sb.append("alter_column_type ");
		sb.append(tableName);
		sb.append(StringPool.SPACE);
		sb.append(columnName);
		sb.append(StringPool.SPACE);
		sb.append(newColumnType);

		runSQL(connection, sb.toString());
	}

	@Override
	public void alterTableAddColumn(
			Connection connection, String tableName, String columnName,
			String columnType)
		throws Exception {

		StringBundler sb = new StringBundler(6);

		sb.append("alter table ");
		sb.append(tableName);
		sb.append(" add ");
		sb.append(columnName);
		sb.append(StringPool.SPACE);
		sb.append(columnType);

		runSQL(connection, sb.toString());
	}

	@Override
	public void alterTableDropColumn(
			Connection connection, String tableName, String columnName)
		throws Exception {

		StringBundler sb = new StringBundler(4);

		sb.append("alter table ");
		sb.append(tableName);
		sb.append(" drop column ");
		sb.append(columnName);

		runSQL(connection, sb.toString());
	}

	@Override
	public abstract String buildSQL(String template)
		throws IOException, SQLException;

	@Override
	public void copyTableRows(
			Connection connection, String sourceTableName,
			String targetTableName, Map<String, String> columnNamesMap,
			Map<String, String> defaultValuesMap)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("insert into ");
		sb.append(targetTableName);
		sb.append(" (");

		String[] sourceColumnNames = ArrayUtil.toStringArray(
			columnNamesMap.keySet());

		String[] targetColumnNames = TransformUtil.transform(
			sourceColumnNames, columnNamesMap::get, String.class);

		sb.append(StringUtil.merge(targetColumnNames, ", "));

		sb.append(") select ");

		for (int i = 0; i < sourceColumnNames.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			String defaultValue = defaultValuesMap.get(targetColumnNames[i]);

			if (defaultValue != null) {
				sb.append("COALESCE(");
			}

			sb.append(sourceTableName);
			sb.append(".");
			sb.append(sourceColumnNames[i]);

			if (defaultValue != null) {
				sb.append(", ");
				sb.append(defaultValue);
				sb.append(")");
			}
		}

		sb.append(" from ");
		sb.append(sourceTableName);
		sb.append(" left join ");
		sb.append(targetTableName);
		sb.append(" on ");

		String[] primaryKeyColumnNames = getPrimaryKeyColumnNames(
			connection, sourceTableName);

		for (int i = 0; i < primaryKeyColumnNames.length; i++) {
			String primaryKeyColumnName = primaryKeyColumnNames[i];

			sb.append(sourceTableName);
			sb.append(".");
			sb.append(primaryKeyColumnName);
			sb.append(" = ");
			sb.append(targetTableName);
			sb.append(".");
			sb.append(columnNamesMap.get(primaryKeyColumnName));

			if (i < (primaryKeyColumnNames.length - 1)) {
				sb.append(" and ");
			}
		}

		sb.append(" where ");
		sb.append(targetTableName);
		sb.append(".");
		sb.append(columnNamesMap.get(primaryKeyColumnNames[0]));
		sb.append(" IS NULL");

		runSQL(sb.toString());
	}

	@Override
	public void copyTableStructure(
			Connection connection, String tableName, String newTableName)
		throws Exception {

		runSQL(connection, getCopyTableStructureSQL(tableName, newTableName));

		addPrimaryKey(
			connection, newTableName,
			getPrimaryKeyColumnNames(connection, tableName));

		List<IndexMetadata> indexMetadatas = new ArrayList<>();

		String indexNamePrefix = StringPool.BLANK;

		if (!isSupportsDuplicatedIndexName()) {
			indexNamePrefix = "TMP_";
		}

		for (IndexMetadata indexMetadata :
				getIndexMetadatas(connection, tableName, null, false)) {

			indexMetadatas.add(
				new IndexMetadata(
					indexNamePrefix.concat(indexMetadata.getIndexName()),
					newTableName, indexMetadata.isUnique(),
					indexMetadata.getColumnNames()));
		}

		addIndexes(connection, indexMetadatas);
	}

	@Override
	public void dropIndexes(
			Connection connection, List<String> indexNames, String tableName)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		for (String indexName : indexNames) {
			if (_log.isInfoEnabled()) {
				_log.info(
					String.format(
						"Dropping index %s from table %s", indexName,
						tableName));
			}

			if (dbInspector.hasIndex(tableName, indexName)) {
				runSQL(
					StringBundler.concat(
						"drop index ", indexName, " on ", tableName));
			}
		}
	}

	@Override
	public List<IndexMetadata> dropIndexes(
			Connection connection, String tableName, String columnName)
		throws IOException, SQLException {

		if (_isSkipIndexOperation(connection, tableName)) {
			return Collections.emptyList();
		}

		List<IndexMetadata> indexMetadatas = getIndexMetadatas(
			connection, tableName, columnName, false);

		for (IndexMetadata indexMetadata : indexMetadatas) {
			runSQL(connection, indexMetadata.getDropSQL());
		}

		return indexMetadatas;
	}

	@Override
	public DBType getDBType() {
		return _dbType;
	}

	@Override
	public String getDefaultValue(String columnDef) {
		Matcher matcher = _defaultValuePattern.matcher(columnDef);

		if (matcher.find()) {
			return matcher.group(2);
		}

		return StringUtil.trim(columnDef);
	}

	@Override
	public List<Index> getIndexes(Connection connection) throws SQLException {
		return TransformUtil.transform(
			getIndexMetadatas(connection, null, null, false),
			index -> new Index(
				index.getIndexName(), index.getTableName(), index.isUnique()));
	}

	@Override
	public List<IndexMetadata> getIndexMetadatas(
			Connection connection, String tableName, String columnName,
			boolean onlyUnique)
		throws SQLException {

		if (_isSkipIndexOperation(connection, tableName)) {
			return Collections.emptyList();
		}

		List<IndexMetadata> indexMetadatas = new ArrayList<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		DB db = DBManagerUtil.getDB();

		DBInspector dbInspector = new DBInspector(connection);

		String catalog = dbInspector.getCatalog();
		String schema = dbInspector.getSchema();

		String normalizedTableName = tableName;

		if (normalizedTableName != null) {
			normalizedTableName = dbInspector.normalizeName(
				tableName, databaseMetaData);
		}

		String normalizedColumnName = columnName;

		if (normalizedColumnName != null) {
			normalizedColumnName = dbInspector.normalizeName(
				columnName, databaseMetaData);
		}

		try (ResultSet tableResultSet = databaseMetaData.getTables(
				catalog, schema, normalizedTableName, new String[] {"TABLE"})) {

			while (tableResultSet.next()) {
				normalizedTableName = dbInspector.normalizeName(
					tableResultSet.getString("TABLE_NAME"), databaseMetaData);

				try (ResultSet indexResultSet = db.getIndexResultSet(
						connection, normalizedTableName, onlyUnique)) {

					boolean unique = false;

					String[] columnNames = new String[0];
					String previousIndexName = null;

					while (indexResultSet.next()) {
						String indexName = indexResultSet.getString(
							"INDEX_NAME");

						if (indexName == null) {
							continue;
						}

						String lowerCaseIndexName = StringUtil.toLowerCase(
							indexName);

						if (!lowerCaseIndexName.startsWith("liferay_") &&
							!lowerCaseIndexName.startsWith("ix_")) {

							continue;
						}

						if ((previousIndexName != null) &&
							!previousIndexName.equals(indexName)) {

							if ((normalizedColumnName == null) ||
								ArrayUtil.contains(
									columnNames, normalizedColumnName)) {

								indexMetadatas.add(
									new IndexMetadata(
										previousIndexName, normalizedTableName,
										unique, columnNames));
							}

							columnNames = new String[0];
						}

						previousIndexName = indexName;

						unique = !indexResultSet.getBoolean("NON_UNIQUE");

						columnNames = ArrayUtil.append(
							columnNames,
							getIndexColumnName(
								dbInspector.normalizeName(
									indexResultSet.getString("COLUMN_NAME"),
									databaseMetaData)));
					}

					if ((previousIndexName != null) &&
						((normalizedColumnName == null) ||
						 ArrayUtil.contains(
							 columnNames, normalizedColumnName))) {

						indexMetadatas.add(
							new IndexMetadata(
								previousIndexName, normalizedTableName, unique,
								columnNames));
					}
				}
			}
		}

		return new ArrayList<>(indexMetadatas);
	}

	@Override
	public ResultSet getIndexResultSet(
			Connection connection, String tableName, boolean onlyUnique)
		throws SQLException {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		DBInspector dbInspector = new DBInspector(connection);

		return databaseMetaData.getIndexInfo(
			dbInspector.getCatalog(), dbInspector.getSchema(), tableName,
			onlyUnique, false);
	}

	@Override
	public int getMajorVersion() {
		return _majorVersion;
	}

	@Override
	public int getMinorVersion() {
		return _minorVersion;
	}

	@Override
	public String[] getPrimaryKeyColumnNames(
			Connection connection, String tableName)
		throws SQLException {

		if (_isSkipIndexOperation(connection, tableName)) {
			return new String[0];
		}

		List<PrimaryKey> primaryKeys = _getPrimaryKeys(connection, tableName);

		String[] primaryKeyColumnNames = new String[primaryKeys.size()];

		for (PrimaryKey primaryKey : primaryKeys) {
			primaryKeyColumnNames[primaryKey._keySeq - 1] =
				primaryKey._columnName;
		}

		return primaryKeyColumnNames;
	}

	@Override
	public Integer getSQLType(String templateType) {
		return _sqlTypes.get(templateType);
	}

	@Override
	public Integer getSQLTypeDecimalDigits(String templateType) {
		return _sqlTypeDecimalDigits.get(templateType);
	}

	@Override
	public Integer getSQLTypeSize(String templateType) {
		return _sqlTypeSizes.get(templateType);
	}

	@Override
	public String getTemplateBlob() {
		return getTemplate()[5];
	}

	@Override
	public String getTemplateFalse() {
		return getTemplate()[2];
	}

	@Override
	public String getTemplateTrue() {
		return getTemplate()[1];
	}

	@Override
	public String getVersionString() {
		return _majorVersion + StringPool.PERIOD + _minorVersion;
	}

	@Override
	public boolean isSupportsAlterColumnName() {
		return true;
	}

	@Override
	public boolean isSupportsAlterColumnType() {
		return true;
	}

	@Override
	public boolean isSupportsDBPartition() {
		return false;
	}

	@Override
	public boolean isSupportsInlineDistinct() {
		return true;
	}

	@Override
	public boolean isSupportsQueryingAfterException() {
		return true;
	}

	@Override
	public boolean isSupportsScrollableResults() {
		return true;
	}

	@Override
	public boolean isSupportsStringCaseSensitiveQuery() {
		return _supportsStringCaseSensitiveQuery;
	}

	@Override
	public boolean isSupportsUpdateWithInnerJoin() {
		return true;
	}

	@Override
	public void process(UnsafeConsumer<Long, Exception> unsafeConsumer)
		throws Exception {

		DBPartitionUtil.forEachCompanyId(unsafeConsumer);
	}

	@Override
	public void removePrimaryKey(Connection connection, String tableName)
		throws Exception {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		DBInspector dbInspector = new DBInspector(connection);

		String normalizedTableName = dbInspector.normalizeName(
			tableName, databaseMetaData);

		runSQL(
			StringBundler.concat(
				"alter table ", normalizedTableName, " drop primary key"));
	}

	@Override
	public void renameTables(
			Connection connection,
			ObjectValuePair<String, String>... tableNameObjectValuePairs)
		throws Exception {

		if (tableNameObjectValuePairs.length == 0) {
			return;
		}

		for (ObjectValuePair<String, String> tableNameObjectValuePair :
				tableNameObjectValuePairs) {

			if (tableNameObjectValuePair == null) {
				throw new IllegalArgumentException(
					"Table name object value pair is null");
			}

			if (Objects.isNull(tableNameObjectValuePair.getKey())) {
				throw new IllegalArgumentException(
					"Table name object value pair key is null");
			}

			if (Objects.isNull(tableNameObjectValuePair.getValue())) {
				throw new IllegalArgumentException(
					"Table name object value pair value is null");
			}
		}

		doRenameTables(connection, tableNameObjectValuePairs);
	}

	@Override
	public void runSQL(Connection connection, String sql)
		throws IOException, SQLException {

		runSQL(connection, new String[] {sql});
	}

	@Override
	public void runSQL(Connection connection, String[] sqls)
		throws IOException, SQLException {

		try (Statement s = connection.createStatement()) {
			for (String sql : sqls) {
				sql = buildSQL(sql);

				if (Validator.isNull(sql)) {
					continue;
				}

				sql = SQLTransformer.transform(sql.trim());

				if (sql.endsWith(";")) {
					sql = sql.substring(0, sql.length() - 1);
				}

				if (sql.endsWith("\ngo")) {
					sql = sql.substring(0, sql.length() - 3);
				}

				if (sql.endsWith("\n/")) {
					sql = sql.substring(0, sql.length() - 2);
				}

				if (_log.isDebugEnabled()) {
					_log.debug(sql);
				}

				try {
					s.executeUpdate(sql);
				}
				catch (SQLException sqlException) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"SQL: ", sql, "\nSQL state: ",
								sqlException.getSQLState(), "\nVendor: ",
								getDBType(), "\nVendor error code: ",
								sqlException.getErrorCode(),
								"\nVendor error message: ",
								sqlException.getMessage()));
					}

					throw sqlException;
				}
			}
		}
	}

	@Override
	public void runSQL(String sql) throws IOException, SQLException {
		runSQL(new String[] {sql});
	}

	@Override
	public void runSQL(String[] sqls) throws IOException, SQLException {
		try (Connection connection = DataAccess.getConnection()) {
			runSQL(connection, sqls);
		}
	}

	@Override
	public void runSQLTemplate(
			Connection connection, String template, boolean failOnError)
		throws IOException, NamingException, SQLException {

		template = StringUtil.trim(template);

		if ((template == null) || template.isEmpty()) {
			return;
		}

		if (!template.endsWith(StringPool.SEMICOLON)) {
			template += StringPool.SEMICOLON;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(template))) {

			StringBundler sb = new StringBundler();

			String line = null;

			Thread currentThread = Thread.currentThread();

			ClassLoader classLoader = currentThread.getContextClassLoader();

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("##")) {
					continue;
				}

				if (line.startsWith("@include ")) {
					int pos = line.indexOf(" ");

					int end = line.length();

					if (StringUtil.endsWith(line, StringPool.SEMICOLON)) {
						end -= 1;
					}

					String includeFileName = line.substring(pos + 1, end);

					InputStream inputStream = classLoader.getResourceAsStream(
						"com/liferay/portal/tools/sql/dependencies/" +
							includeFileName);

					if (inputStream == null) {
						inputStream = classLoader.getResourceAsStream(
							includeFileName);
					}

					String include = StringUtil.read(inputStream);

					include = replaceTemplate(include);

					runSQLTemplate(connection, include, true);
				}
				else {
					sb.append(line);
					sb.append(StringPool.NEW_LINE);

					if (line.endsWith(";")) {
						String sql = sb.toString();

						sb.setIndex(0);

						try {
							if (!sql.equals("COMMIT_TRANSACTION;\n")) {
								runSQL(connection, sql);
							}
							else {
								if (_log.isDebugEnabled()) {
									_log.debug("Skip commit sql");
								}
							}
						}
						catch (IOException ioException) {
							if (failOnError) {
								throw ioException;
							}
							else if (_log.isWarnEnabled()) {
								_log.warn(ioException);
							}
						}
						catch (SecurityException securityException) {
							if (failOnError) {
								throw securityException;
							}
							else if (_log.isWarnEnabled()) {
								_log.warn(securityException);
							}
						}
						catch (SQLException sqlException) {
							if (failOnError) {
								throw sqlException;
							}

							String message = GetterUtil.getString(
								sqlException.getMessage());

							if (!message.startsWith("Duplicate key name") &&
								_log.isWarnEnabled()) {

								_log.warn(message + ": " + buildSQL(sql));
							}

							if (message.startsWith("Duplicate entry") ||
								message.startsWith(
									"Specified key was too long")) {

								_log.error(line);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void runSQLTemplate(String template, boolean failOnError)
		throws IOException, NamingException, SQLException {

		try (Connection connection = DataAccess.getConnection()) {
			runSQLTemplate(connection, template, failOnError);
		}
	}

	@Override
	public void setSupportsStringCaseSensitiveQuery(
		boolean supportsStringCaseSensitiveQuery) {

		if (_log.isDebugEnabled()) {
			if (supportsStringCaseSensitiveQuery) {
				_log.debug("Database supports case sensitive queries");
			}
			else {
				_log.debug("Database does not support case sensitive queries");
			}
		}

		_supportsStringCaseSensitiveQuery = supportsStringCaseSensitiveQuery;

		SQLTransformer.reloadSQLTransformer();
	}

	@Override
	public AutoCloseable syncTables(
			Connection connection, String sourceTableName,
			String targetTableName, Map<String, String> columnNamesMap,
			Map<String, String> defaultValuesMap)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		String deleteTriggerName = dbInspector.normalizeName(
			"delete_" + sourceTableName);

		String[] sourcePrimaryKeyColumnNames = getPrimaryKeyColumnNames(
			connection, sourceTableName);

		String[] targetPrimaryKeyColumnNames = TransformUtil.transform(
			sourcePrimaryKeyColumnNames, columnNamesMap::get, String.class);

		createSyncDeleteTrigger(
			connection, sourceTableName, targetTableName, deleteTriggerName,
			sourcePrimaryKeyColumnNames, targetPrimaryKeyColumnNames);

		String insertTriggerName = dbInspector.normalizeName(
			"insert_" + sourceTableName);
		String[] sourceColumnNames = TransformUtil.transformToArray(
			columnNamesMap.entrySet(), Map.Entry::getKey, String.class);
		String[] targetColumnNames = TransformUtil.transformToArray(
			columnNamesMap.entrySet(), Map.Entry::getValue, String.class);

		createSyncInsertTrigger(
			connection, sourceTableName, targetTableName, insertTriggerName,
			sourceColumnNames, targetColumnNames, sourcePrimaryKeyColumnNames,
			targetPrimaryKeyColumnNames, defaultValuesMap);

		String updateTriggerName = dbInspector.normalizeName(
			"update_" + sourceTableName);

		createSyncUpdateTrigger(
			connection, sourceTableName, targetTableName, updateTriggerName,
			sourceColumnNames, targetColumnNames, sourcePrimaryKeyColumnNames,
			targetPrimaryKeyColumnNames, defaultValuesMap);

		return () -> {
			dropTrigger(connection, sourceTableName, deleteTriggerName);
			dropTrigger(connection, sourceTableName, insertTriggerName);
			dropTrigger(connection, sourceTableName, updateTriggerName);
		};
	}

	@Override
	public void updateIndexes(
			Connection connection, String tableName, String indexesSQL,
			boolean dropIndexes)
		throws Exception {

		if (_isSkipIndexOperation(connection, tableName)) {
			return;
		}

		List<Index> indexes = _getIndexes(connection, tableName);

		Set<String> validIndexNames;

		if (dropIndexes) {
			validIndexNames = dropIndexes(connection, indexesSQL, indexes);
		}
		else {
			validIndexNames = new HashSet<>();

			for (Index index : indexes) {
				String indexName = StringUtil.toUpperCase(index.getIndexName());

				validIndexNames.add(indexName);
			}
		}

		_addIndexes(
			connection, _applyMaxStringIndexLengthLimitation(indexesSQL),
			validIndexNames);
	}

	public void updatePrimaryKey(
			Connection connection, String tableName,
			String[] primaryKeyColumnNames)
		throws Exception {

		if (_isSkipIndexOperation(connection, tableName)) {
			return;
		}

		DBInspector dbInspector = new DBInspector(connection);

		if (!dbInspector.hasTable(tableName)) {
			return;
		}

		for (String columnName : primaryKeyColumnNames) {
			if (!dbInspector.hasColumn(tableName, columnName)) {
				if (StringUtil.equalsIgnoreCase(columnName, "ctCollectionId")) {
					primaryKeyColumnNames = ArrayUtil.filter(
						primaryKeyColumnNames,
						name -> !StringUtil.equalsIgnoreCase(
							name, "ctCollectionId"));
				}
				else {
					if (_log.isDebugEnabled()) {
						_log.debug(
							StringBundler.concat(
								"Unable to recreate primary key for table ",
								tableName, " because column ", columnName,
								" does not exist"));
					}

					return;
				}
			}
		}

		String[] actualPrimaryKeyColumns = getPrimaryKeyColumnNames(
			connection, tableName);

		if (ArrayUtil.equalsIgnoreCase(
				actualPrimaryKeyColumns, primaryKeyColumnNames)) {

			return;
		}

		if (ArrayUtil.isNotEmpty(actualPrimaryKeyColumns)) {
			removePrimaryKey(connection, tableName);
		}

		addPrimaryKey(connection, tableName, primaryKeyColumnNames);
	}

	protected BaseDB(DBType dbType, int majorVersion, int minorVersion) {
		_dbType = dbType;
		_majorVersion = majorVersion;
		_minorVersion = minorVersion;

		String[] actual = getTemplate();

		for (int i = 0; i < TEMPLATE.length; i++) {
			_templates.put(TEMPLATE[i], actual[i]);
		}

		String[] templateTypes = ArrayUtil.clone(TEMPLATE, 5, 16);

		for (int i = 0; i < templateTypes.length; i++) {
			String actualType = StringUtil.trim(
				_templates.get(templateTypes[i]));

			String templateType = StringUtil.trim(templateTypes[i]);

			_sqlTypes.put(templateType, getSQLTypes()[i]);

			Matcher matcher = _sqlTypeDecimalDigitsPattern.matcher(actualType);

			_sqlTypeDecimalDigits.put(
				templateType,
				matcher.matches() ? GetterUtil.getInteger(matcher.group(1)) :
					DB.SQL_SIZE_NONE);

			if (templateType.equals("DATE")) {
				_sqlTypeSizes.put(templateType, DB.SQL_SIZE_NONE);

				continue;
			}
			else if (templateType.equals("STRING") ||
					 templateType.equals("TEXT")) {

				_sqlTypeSizes.put(
					templateType, getSQLVarcharSizes().get(templateType));

				continue;
			}

			matcher = _sqlTypeSizePattern.matcher(actualType);

			_sqlTypeSizes.put(
				templateType,
				matcher.matches() ?
					GetterUtil.getInteger(matcher.group(1), DB.SQL_SIZE_NONE) :
						DB.SQL_SIZE_NONE);
		}
	}

	protected void addPrimaryKey(
			Connection connection, String tableName, String[] columnNames)
		throws IOException, SQLException {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		DBInspector dbInspector = new DBInspector(connection);

		StringBundler sb = new StringBundler();

		sb.append("alter table ");
		sb.append(dbInspector.normalizeName(tableName, databaseMetaData));
		sb.append(" add primary key (");

		for (String columnName : columnNames) {
			sb.append(columnName);
			sb.append(", ");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		runSQL(sb.toString());
	}

	protected String[] buildColumnNameTokens(String line) {
		Matcher matcher = _alterColumnNamePattern.matcher(line);

		if (!matcher.find()) {
			throw new IllegalArgumentException(
				"Invalid alter column name statement");
		}

		String defaultValue = matcher.group(5);
		String nullable = matcher.group(6);

		if (defaultValue != null) {
			nullable = "not null";
		}
		else {
			defaultValue = StringPool.BLANK;

			if (nullable == null) {
				nullable = StringPool.BLANK;
			}
		}

		return new String[] {
			matcher.group(1), matcher.group(2), matcher.group(3),
			matcher.group(4), defaultValue, StringUtil.toLowerCase(nullable)
		};
	}

	protected String[] buildColumnTypeTokens(String line) {
		Matcher matcher = _alterColumnTypePattern.matcher(line);

		if (!matcher.find()) {
			throw new IllegalArgumentException(
				"Invalid alter column type statement");
		}

		String defaultValue = matcher.group(4);
		String nullable = matcher.group(5);

		if (defaultValue != null) {
			nullable = "not null";
		}
		else if (nullable == null) {
			defaultValue = StringPool.BLANK;

			if (nullable == null) {
				nullable = StringPool.BLANK;
			}
		}

		return new String[] {
			matcher.group(1), matcher.group(2), "", matcher.group(3),
			defaultValue, StringUtil.toLowerCase(nullable)
		};
	}

	protected String[] buildTableNameTokens(String line) {
		String[] words = StringUtil.split(line, CharPool.SPACE);

		return new String[] {words[1], words[2]};
	}

	protected void createSyncDeleteTrigger(
			Connection connection, String sourceTableName,
			String targetTableName, String triggerName,
			String[] sourcePrimaryKeyColumnNames,
			String[] targetPrimaryKeyColumnNames)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("create trigger ");
		sb.append(triggerName);
		sb.append(" after delete on ");
		sb.append(sourceTableName);
		sb.append(" for each row delete from ");
		sb.append(targetTableName);
		sb.append(" where ");

		for (int i = 0; i < sourcePrimaryKeyColumnNames.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(targetPrimaryKeyColumnNames[i]);
			sb.append(" = old.");
			sb.append(sourcePrimaryKeyColumnNames[i]);
		}

		runSQL(connection, sb.toString());
	}

	protected void createSyncInsertTrigger(
			Connection connection, String sourceTableName,
			String targetTableName, String triggerName,
			String[] sourceColumnNames, String[] targetColumnNames,
			String[] sourcePrimaryKeyColumnNames,
			String[] targetPrimaryKeyColumnNames,
			Map<String, String> defaultValuesMap)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("create trigger ");
		sb.append(triggerName);
		sb.append(" after insert on ");
		sb.append(sourceTableName);
		sb.append(" for each row insert into ");
		sb.append(targetTableName);
		sb.append(" (");
		sb.append(StringUtil.merge(targetColumnNames, ", "));
		sb.append(") values (");

		for (int i = 0; i < sourceColumnNames.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			String defaultValue = defaultValuesMap.get(targetColumnNames[i]);

			if (defaultValue != null) {
				sb.append("COALESCE(");
			}

			sb.append("new.");
			sb.append(sourceColumnNames[i]);

			if (defaultValue != null) {
				sb.append(", ");
				sb.append(defaultValue);
				sb.append(")");
			}
		}

		sb.append(")");

		runSQL(connection, sb.toString());
	}

	protected void createSyncUpdateTrigger(
			Connection connection, String sourceTableName,
			String targetTableName, String triggerName,
			String[] sourceColumnNames, String[] targetColumnNames,
			String[] sourcePrimaryKeyColumnNames,
			String[] targetPrimaryKeyColumnNames,
			Map<String, String> defaultValuesMap)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("create trigger ");
		sb.append(triggerName);
		sb.append(" after update on ");
		sb.append(sourceTableName);
		sb.append(" for each row update ");
		sb.append(targetTableName);
		sb.append(" set ");

		for (int i = 0; i < sourceColumnNames.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append(targetColumnNames[i]);
			sb.append(" = ");

			String defaultValue = defaultValuesMap.get(targetColumnNames[i]);

			if (defaultValue != null) {
				sb.append("COALESCE(");
			}

			sb.append("new.");
			sb.append(sourceColumnNames[i]);

			if (defaultValue != null) {
				sb.append(", ");
				sb.append(defaultValue);
				sb.append(")");
			}
		}

		sb.append(" where ");

		for (int i = 0; i < sourcePrimaryKeyColumnNames.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(targetPrimaryKeyColumnNames[i]);
			sb.append(" = old.");
			sb.append(sourcePrimaryKeyColumnNames[i]);
		}

		runSQL(connection, sb.toString());
	}

	protected void doRenameTables(
			Connection connection,
			ObjectValuePair<String, String>... tableNameObjectValuePairs)
		throws Exception {

		if (isSupportsDDLRollback()) {
			boolean autoCommit = connection.getAutoCommit();

			try {
				connection.setAutoCommit(false);

				for (ObjectValuePair<String, String> tableNameObjectValuePair :
						tableNameObjectValuePairs) {

					runSQL(
						connection,
						getRenameTableSQL(
							tableNameObjectValuePair.getKey(),
							tableNameObjectValuePair.getValue()));
				}

				connection.commit();
			}
			catch (Exception exception) {
				connection.rollback();

				throw exception;
			}
			finally {
				connection.setAutoCommit(autoCommit);
			}
		}
		else {
			int index = 0;
			ObjectValuePair<String, String> tableNameObjectValuePair = null;

			try {
				while (index < tableNameObjectValuePairs.length) {
					tableNameObjectValuePair = tableNameObjectValuePairs[index];

					runSQL(
						connection,
						getRenameTableSQL(
							tableNameObjectValuePair.getKey(),
							tableNameObjectValuePair.getValue()));

					index++;
				}
			}
			catch (Exception exception1) {
				_log.error(
					StringBundler.concat(
						"Unable to rename table ",
						tableNameObjectValuePair.getKey(), " to ",
						tableNameObjectValuePair.getValue(),
						". Attempting to rollback."));

				try {
					while (index > 0) {
						tableNameObjectValuePair =
							tableNameObjectValuePairs[--index];

						runSQL(
							connection,
							getRenameTableSQL(
								tableNameObjectValuePair.getValue(),
								tableNameObjectValuePair.getKey()));
					}

					if (_log.isInfoEnabled()) {
						_log.info("Successfully rolled back table renames");
					}
				}
				catch (Exception exception2) {
					_log.fatal("Unable to roll back table renames", exception2);
				}

				throw exception1;
			}
		}
	}

	protected Set<String> dropIndexes(
			Connection connection, String indexesSQL, List<Index> indexes)
		throws IOException, SQLException {

		if (_log.isDebugEnabled()) {
			_log.debug("Dropping stale indexes");
		}

		Set<String> validIndexNames = new HashSet<>();

		if (indexes.isEmpty()) {
			return validIndexNames;
		}

		String indexesSQLLowerCase = StringUtil.toLowerCase(indexesSQL);

		String[] lines = StringUtil.splitLines(indexesSQL);

		Set<String> indexNames = new HashSet<>();

		for (String line : lines) {
			if (Validator.isNull(line)) {
				continue;
			}

			IndexMetadata indexMetadata =
				IndexMetadataFactoryUtil.createIndexMetadata(line);

			indexNames.add(
				StringUtil.toLowerCase(indexMetadata.getIndexName()));
		}

		for (Index index : indexes) {
			String indexNameUpperCase = StringUtil.toUpperCase(
				index.getIndexName());

			String indexNameLowerCase = StringUtil.toLowerCase(
				indexNameUpperCase);

			validIndexNames.add(indexNameUpperCase);

			if (indexNames.contains(indexNameLowerCase)) {
				boolean unique = index.isUnique();

				if (unique &&
					indexesSQLLowerCase.contains(
						"create unique index " + indexNameLowerCase + " ")) {

					continue;
				}

				if (!unique &&
					indexesSQLLowerCase.contains(
						"create index " + indexNameLowerCase + " ")) {

					continue;
				}
			}

			validIndexNames.remove(indexNameUpperCase);

			String sql = StringBundler.concat(
				"drop index ", indexNameUpperCase, " on ",
				index.getTableName());

			if (_log.isInfoEnabled()) {
				_log.info(sql);
			}

			runSQL(connection, sql);
		}

		return validIndexNames;
	}

	protected void dropTrigger(
			Connection connection, String tableName, String triggerName)
		throws Exception {

		runSQL(connection, "drop trigger " + triggerName);
	}

	protected String getCopyTableStructureSQL(
		String tableName, String newTableName) {

		return StringBundler.concat(
			"create table ", newTableName, " as select * from ", tableName,
			" where 1 = 0");
	}

	protected String getIndexColumnName(String indexColumnName) {
		return indexColumnName;
	}

	protected String getRenameTableSQL(
		String oldTableName, String newTableName) {

		return StringBundler.concat(
			"alter table ", oldTableName, " rename to ", newTableName);
	}

	protected abstract int[] getSQLTypes();

	protected Map<String, Integer> getSQLVarcharSizes() {
		return HashMapBuilder.put(
			"STRING", SQL_SIZE_NONE
		).put(
			"TEXT", SQL_SIZE_NONE
		).build();
	}

	protected abstract String[] getTemplate();

	protected boolean isSupportsDDLRollback() {
		return true;
	}

	protected boolean isSupportsDuplicatedIndexName() {
		return true;
	}

	protected String limitColumnLength(String column, int length) {
		return StringBundler.concat(column, "\\(", length, "\\)");
	}

	protected String replaceTemplate(String template) {
		if (Validator.isNull(template)) {
			return null;
		}

		StringBundler sb = null;

		int endIndex = 0;

		Matcher matcher = _templatePattern.matcher(template);

		while (matcher.find()) {
			int startIndex = matcher.start();

			if (sb == null) {
				sb = new StringBundler();
			}

			sb.append(template.substring(endIndex, startIndex));

			endIndex = matcher.end();

			String matched = template.substring(startIndex, endIndex);

			sb.append(_templates.get(matched));
		}

		if (sb == null) {
			return _applyMaxStringIndexLengthLimitation(template);
		}

		if (template.length() > endIndex) {
			sb.append(template.substring(endIndex));
		}

		return _applyMaxStringIndexLengthLimitation(sb.toString());
	}

	protected abstract String reword(String data)
		throws IOException, SQLException;

	protected static final String ALTER_COLUMN_NAME = "alter_column_name ";

	protected static final String ALTER_COLUMN_TYPE = "alter_column_type ";

	protected static final String ALTER_TABLE_NAME = "alter_table_name ";

	protected static final String CREATE_TABLE = "create table ";

	protected static final String DROP_INDEX = "drop index";

	protected static final String DROP_PRIMARY_KEY = "drop primary key";

	protected static final String[] RENAME_TABLE_TEMPLATE = {
		"@old-table@", "@new-table@"
	};

	protected static final String[] REWORD_TEMPLATE = {
		"@table@", "@old-column@", "@new-column@", "@type@", "@default@",
		"@nullable@"
	};

	protected static final int[] SQL_VARCHAR_TYPES = {
		Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.NVARCHAR, Types.VARCHAR
	};

	protected static final String[] TEMPLATE = {
		"##", "TRUE", "FALSE", "'01/01/1970'", "CURRENT_TIMESTAMP", " BLOB",
		" SBLOB", " BIGDECIMAL", " BOOLEAN", " DATE", " DOUBLE", " INTEGER",
		" LONG", " STRING", " TEXT", " VARCHAR", " IDENTITY",
		"COMMIT_TRANSACTION"
	};

	protected static final Pattern columnTypePattern = Pattern.compile(
		"(^\\w+)", Pattern.CASE_INSENSITIVE);

	private void _addIndexes(
			Connection connection, String indexesSQL,
			Set<String> validIndexNames)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Adding indexes");
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(indexesSQL))) {

			String sql = null;

			ThrowableCollector throwableCollector = new ThrowableCollector();

			while ((sql = unsyncBufferedReader.readLine()) != null) {
				if (Validator.isNull(sql)) {
					continue;
				}

				int y = sql.indexOf(" on ");

				int x = sql.lastIndexOf(" ", y - 1);

				String indexName = sql.substring(x + 1, y);

				if (validIndexNames.contains(indexName)) {
					continue;
				}

				if (_log.isInfoEnabled()) {
					_log.info(sql);
				}

				try {
					runSQL(connection, sql);
				}
				catch (SQLException sqlException) {
					if (_log.isDebugEnabled()) {
						_log.debug(sqlException.getMessage() + ": " + sql);
					}

					throwableCollector.collect(sqlException);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(exception.getMessage() + ": " + sql);
					}
				}
			}

			throwableCollector.rethrow();
		}
	}

	private String _applyMaxStringIndexLengthLimitation(String template) {
		if (!template.contains("[$COLUMN_LENGTH:")) {
			return template;
		}

		DBType dbType = getDBType();

		int stringIndexMaxLength = GetterUtil.getInteger(
			PropsUtil.get(
				PropsKeys.DATABASE_STRING_INDEX_MAX_LENGTH,
				new Filter(dbType.getName())),
			-1);

		Matcher matcher = _columnLengthPattern.matcher(template);

		if (stringIndexMaxLength < 0) {
			return matcher.replaceAll("$1");
		}

		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			int length = Integer.valueOf(matcher.group(2));

			if (length > stringIndexMaxLength) {
				matcher.appendReplacement(
					sb,
					limitColumnLength(matcher.group(1), stringIndexMaxLength));
			}
			else {
				matcher.appendReplacement(sb, matcher.group(1));
			}
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private List<Index> _getIndexes(Connection connection, String tableName)
		throws Exception {

		return TransformUtil.transform(
			getIndexMetadatas(connection, tableName, null, false),
			index -> new Index(
				index.getIndexName(), index.getTableName(), index.isUnique()));
	}

	private List<PrimaryKey> _getPrimaryKeys(
			Connection connection, String tableName)
		throws SQLException {

		List<PrimaryKey> primaryKeys = new ArrayList<>();

		DatabaseMetaData databaseMetaData = connection.getMetaData();
		DBInspector dbInspector = new DBInspector(connection);

		try (ResultSet resultSet = databaseMetaData.getPrimaryKeys(
				dbInspector.getCatalog(), dbInspector.getSchema(),
				dbInspector.normalizeName(tableName, databaseMetaData))) {

			while (resultSet.next()) {
				primaryKeys.add(
					new PrimaryKey(
						dbInspector.normalizeName(
							resultSet.getString("COLUMN_NAME"),
							databaseMetaData),
						resultSet.getInt("KEY_SEQ")));
			}
		}

		return primaryKeys;
	}

	private boolean _isSkipIndexOperation(
		Connection connection, String tableName) {

		if (!DBPartition.isPartitionEnabled() ||
			(CompanyThreadLocal.getNonsystemCompanyId() ==
				PortalInstancePool.getDefaultCompanyId())) {

			return false;
		}

		DBInspector dbInspector = new DBInspector(connection);

		return dbInspector.isControlTable(tableName);
	}

	private static final Log _log = LogFactoryUtil.getLog(BaseDB.class);

	private static final Pattern _alterColumnNamePattern;
	private static final Pattern _alterColumnTypePattern;
	private static final Pattern _columnLengthPattern = Pattern.compile(
		"([^,(\\s]+)\\[\\$COLUMN_LENGTH:(\\d+)\\$\\]");
	private static final Pattern _defaultValuePattern = Pattern.compile(
		"^(')?(\\d+|.*)\\1(::.*| )?", Pattern.CASE_INSENSITIVE);
	private static final Pattern _sqlTypeDecimalDigitsPattern = Pattern.compile(
		"^\\w+(?:\\(\\d+,\\s(\\d+)\\))", Pattern.CASE_INSENSITIVE);
	private static final Pattern _sqlTypeSizePattern = Pattern.compile(
		"^\\w+(?:\\((\\d+).*\\))", Pattern.CASE_INSENSITIVE);
	private static final Pattern _templatePattern;

	static {
		StringBundler sb = new StringBundler((TEMPLATE.length * 5) - 6);

		for (int i = 0; i < TEMPLATE.length; i++) {
			String variable = TEMPLATE[i];

			if (variable.equals("##") || variable.equals("'01/01/1970'")) {
				sb.append(variable);
			}
			else {
				sb.append("(?<!\\[\\$)");
				sb.append(variable);
				sb.append("(?!\\$\\])");

				sb.append("\\b");
			}

			sb.append(StringPool.PIPE);
		}

		sb.setIndex(sb.index() - 1);

		_templatePattern = Pattern.compile(sb.toString());

		String dataTypeRegex = "(\\w+(?:\\([^\\)]+\\))?)";
		String defaultAndNullableRegex =
			"(?:(?:DEFAULT\\s+('?.*[^']'?)\\s+NOT\\s+NULL)|((?:NOT\\s+)?NULL))";

		_alterColumnNamePattern = Pattern.compile(
			StringBundler.concat(
				"^ALTER_COLUMN_NAME\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+",
				dataTypeRegex, "\\s*", defaultAndNullableRegex, "?;?$"),
			Pattern.CASE_INSENSITIVE);
		_alterColumnTypePattern = Pattern.compile(
			StringBundler.concat(
				"^ALTER_COLUMN_TYPE\\s+(\\S+)\\s+(\\S+)\\s+", dataTypeRegex,
				"\\s*", defaultAndNullableRegex, "?;?$"),
			Pattern.CASE_INSENSITIVE);
	}

	private final DBType _dbType;
	private final int _majorVersion;
	private final int _minorVersion;
	private final Map<String, Integer> _sqlTypeDecimalDigits = new HashMap<>();
	private final Map<String, Integer> _sqlTypes = new HashMap<>();
	private final Map<String, Integer> _sqlTypeSizes = new HashMap<>();
	private boolean _supportsStringCaseSensitiveQuery = true;
	private final Map<String, String> _templates = new HashMap<>();

	private static class PrimaryKey {

		private PrimaryKey(String columnName, int keySeq) {
			_columnName = columnName;
			_keySeq = keySeq;
		}

		private final String _columnName;
		private final int _keySeq;

	}

}