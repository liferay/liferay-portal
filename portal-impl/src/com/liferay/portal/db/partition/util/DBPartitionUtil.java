/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.util;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.jdbc.util.DataSourceWrapper;
import com.liferay.portal.db.partition.db.DBPartitionDB;
import com.liferay.portal.db.partition.db.DBPartitionMySQLDB;
import com.liferay.portal.db.partition.db.DBPartitionPostgreSQLDB;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnectionUtil;
import com.liferay.portal.kernel.dao.jdbc.util.ConnectionWrapper;
import com.liferay.portal.kernel.dao.jdbc.util.StatementWrapper;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.module.framework.ThrowableCollector;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.spring.hibernate.DialectDetector;
import com.liferay.portal.util.PropsValues;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sql.DataSource;

/**
 * @author Alberto Chaparro
 */
public class DBPartitionUtil {

	public static boolean addDBPartition(long companyId)
		throws PortalException {

		if (!DBPartition.isPartitionEnabled() ||
			(companyId == _defaultCompanyId)) {

			return false;
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_defaultCompanyId)) {

			_addDBPartition(companyId);
		}

		return true;
	}

	public static void checkDatabasePartitionSchemaNamePrefix()
		throws PortalException {

		if (DBPartition.isPartitionEnabled() &&
			(_DATABASE_PARTITION_SCHEMA_NAME_PREFIX.length() > 11)) {

			throw new PortalException(
				"The value for property " +
					"\"database.partition.schema.name.prefix\" is greater " +
						"than 11 characters");
		}
	}

	public static boolean copyDBPartition(long fromCompanyId, long toCompanyId)
		throws PortalException {

		if (!DBPartition.isPartitionEnabled() ||
			(fromCompanyId == _defaultCompanyId)) {

			return false;
		}

		_copyDBPartition(fromCompanyId, toCompanyId);

		return true;
	}

	public static boolean extractDBPartition(long companyId)
		throws PortalException {

		if (!DBPartition.isPartitionEnabled() ||
			(companyId == _defaultCompanyId)) {

			return false;
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_defaultCompanyId)) {

			_extractDBPartition(companyId);
		}

		return true;
	}

	public static void forEachCompanyId(
			UnsafeConsumer<Long, Exception> unsafeConsumer)
		throws Exception {

		if (!DBPartition.isPartitionEnabled()) {
			unsafeConsumer.accept(null);

			return;
		}

		if (CompanyThreadLocal.isLocked()) {
			unsafeConsumer.accept(CompanyThreadLocal.getCompanyId());

			return;
		}

		if (_DATABASE_PARTITION_THREAD_POOL_ENABLED) {
			_forEachCompanyIdConcurrently(unsafeConsumer);

			return;
		}

		long[] companyIds = PortalInstancePool.getCompanyIds();

		if (ArrayUtil.isEmpty(companyIds)) {
			unsafeConsumer.accept(null);
		}
		else {
			try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
					_defaultCompanyId)) {

				unsafeConsumer.accept(_defaultCompanyId);
			}

			for (long companyId : companyIds) {
				if (companyId != _defaultCompanyId) {
					try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
							companyId)) {

						unsafeConsumer.accept(companyId);
					}
				}
			}
		}
	}

	public static List<String> getConfigurationPids(long companyId)
		throws SQLException {

		List<String> pids = new ArrayList<>();

		Connection connection = CurrentConnectionUtil.getConnection(
			InfrastructureUtil.getDataSource());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select configurationId from ", getPartitionName(companyId),
					".Configuration_ where dictionary like ",
					"'%org.apache.felix.configadmin.revision%'"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				pids.add(resultSet.getString(1));
			}
		}

		return pids;
	}

	public static Map<String, String> getConfigurations(long companyId)
		throws SQLException {

		Connection connection = CurrentConnectionUtil.getConnection(
			InfrastructureUtil.getDataSource());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select configurationId, dictionary from ",
					getPartitionName(companyId), ".Configuration_"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Map<String, String> configurations = new HashMap<>();

			while (resultSet.next()) {
				configurations.put(
					resultSet.getString(1), resultSet.getString(2));
			}

			return configurations;
		}
	}

	public static String getPartitionKey(Object key) {
		if (!DBPartition.isPartitionEnabled()) {
			return key.toString();
		}

		return key + StringPool.AT + CompanyThreadLocal.getNonsystemCompanyId();
	}

	public static String getPartitionName(long companyId) {
		if ((companyId == CompanyConstants.SYSTEM) ||
			(companyId == _defaultCompanyId)) {

			return _defaultPartitionName;
		}

		return _DATABASE_PARTITION_SCHEMA_NAME_PREFIX + companyId;
	}

	public static boolean insertDBPartition(long companyId)
		throws PortalException {

		if (!DBPartition.isPartitionEnabled()) {
			return false;
		}

		_insertDBPartition(companyId);

		return true;
	}

	public static boolean removeDBPartition(long companyId)
		throws PortalException {

		if (!DBPartition.isPartitionEnabled() ||
			(companyId == _defaultCompanyId)) {

			return false;
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_defaultCompanyId)) {

			_dropDBPartition(companyId);
		}

		return true;
	}

	public static void replaceByTable(
			Connection connection, boolean copyData, String viewName)
		throws Exception {

		long companyId = CompanyThreadLocal.getNonsystemCompanyId();

		if (companyId == _defaultCompanyId) {
			return;
		}

		String partitionName = getPartitionName(companyId);

		try (Statement statement = connection.createStatement()) {
			statement.execute(
				_dbPartitionDB.getDropViewSQL(partitionName, viewName));

			statement.execute(
				_dbPartitionDB.getCreateTableSQL(
					connection, _defaultPartitionName, partitionName,
					viewName));

			if (copyData) {
				statement.executeUpdate(
					_getCopyDataSQL(
						_defaultPartitionName, partitionName, viewName,
						_getColumnNames(connection, viewName),
						StringPool.BLANK));
			}
		}
	}

	public static void setDefaultCompanyId(Connection connection)
		throws SQLException {

		if (DBPartition.isPartitionEnabled()) {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select companyId from Company where webId = '" +
							PropsValues.COMPANY_DEFAULT_WEB_ID + "'");
				ResultSet resultSet = preparedStatement.executeQuery()) {

				if (resultSet.next()) {
					_defaultCompanyId = resultSet.getLong(1);
				}
			}
		}
	}

	public static void setDefaultCompanyId(long companyId) {
		if (DBPartition.isPartitionEnabled()) {
			_defaultCompanyId = companyId;
		}
	}

	public static DataSource wrapDataSource(DataSource dataSource)
		throws SQLException {

		if (!DBPartition.isPartitionEnabled()) {
			return dataSource;
		}

		DB db = DBManagerUtil.getDB(
			DBManagerUtil.getDBType(DialectDetector.getDialect(dataSource)),
			dataSource);

		if (!db.isSupportsDBPartition()) {
			throw new Error(
				"Database partitioning is not supported for " + db.getDBType());
		}

		if (db.getDBType() == DBType.MYSQL) {
			_dbPartitionDB = new DBPartitionMySQLDB();
		}
		else if (db.getDBType() == DBType.POSTGRESQL) {
			_dbPartitionDB = new DBPartitionPostgreSQLDB();
		}

		try (Connection connection = dataSource.getConnection()) {
			_defaultPartitionName = _dbPartitionDB.getDefaultPartitionName(
				connection);
		}

		return new DataSourceWrapper(dataSource) {

			@Override
			public Connection getConnection() throws SQLException {
				return _getConnectionWrapper(super.getConnection());
			}

			@Override
			public Connection getConnection(String userName, String password)
				throws SQLException {

				return _getConnectionWrapper(super.getConnection());
			}

		};
	}

	private static void _addDBPartition(long companyId) throws PortalException {
		Connection connection = CurrentConnectionUtil.getConnection(
			InfrastructureUtil.getDataSource());

		String partitionName = getPartitionName(companyId);

		try (AutoCloseable autoCloseable = _disableAutoCommit(connection);
			PreparedStatement preparedStatement = connection.prepareStatement(
				_dbPartitionDB.getCreatePartitionSQL(
					connection, partitionName))) {

			preparedStatement.executeUpdate();

			List<Long> companyIds = ListUtil.fromArray(
				PortalInstancePool.getCompanyIds());
			DBInspector dbInspector = new DBInspector(connection);

			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getTables(
					_dbPartitionDB.getCatalog(
						connection, _defaultPartitionName),
					_dbPartitionDB.getSchema(connection, _defaultPartitionName),
					null, new String[] {"TABLE"});
				Statement statement = connection.createStatement()) {

				while (resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");

					if (dbInspector.isObjectTable(companyIds, tableName)) {
						continue;
					}

					if (dbInspector.isControlTable(tableName)) {
						statement.executeUpdate(
							_dbPartitionDB.getCreateViewSQL(
								_defaultPartitionName, partitionName,
								tableName));
					}
					else {
						statement.executeUpdate(
							_dbPartitionDB.getCreateTableSQL(
								connection, _defaultPartitionName,
								partitionName, tableName));

						if (dbInspector.isPartitionedControlTable(tableName)) {
							statement.executeUpdate(
								_getCopyDataSQL(
									_defaultPartitionName, partitionName,
									tableName,
									_getColumnNames(connection, tableName),
									StringPool.BLANK));
						}
					}
				}
			}

			try (Statement statement = connection.createStatement()) {
				for (String createRuleSQL :
						_dbPartitionDB.getCreateRulesSQL(partitionName)) {

					statement.executeUpdate(createRuleSQL);
				}
			}

			connection.commit();
		}
		catch (Exception exception) {
			if (!_dbPartitionDB.isDDLTransactional()) {
				try (Statement statement = connection.createStatement()) {
					statement.executeUpdate(
						_dbPartitionDB.getDropPartitionSQL(partitionName));
				}
				catch (SQLException sqlException) {
					throw new PortalException(
						"Unable to roll back schema creation", sqlException);
				}
			}

			throw new PortalException(exception);
		}
	}

	private static void _copyDBPartition(long fromCompanyId, long toCompanyId)
		throws PortalException {

		Connection connection = CurrentConnectionUtil.getConnection(
			InfrastructureUtil.getDataSource());
		List<String> quartzTableNames = new ArrayList<>();
		String sourcePartitionName = getPartitionName(fromCompanyId);
		String targetPartitionName = getPartitionName(toCompanyId);

		try (SafeCloseable safeCloseable1 =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(fromCompanyId);
			AutoCloseable autoCloseable = _disableAutoCommit(connection)) {

			_copySchema(connection, sourcePartitionName, targetPartitionName);

			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getTables(
					_dbPartitionDB.getCatalog(connection, sourcePartitionName),
					_dbPartitionDB.getSchema(connection, sourcePartitionName),
					null, new String[] {"TABLE", "VIEW"});
				Statement statement = connection.createStatement()) {

				DBInspector dbInspector = new DBInspector(connection);

				DB db = DBManagerUtil.getDB();

				while (resultSet.next()) {
					String fromTableName = resultSet.getString("TABLE_NAME");

					if (Objects.equals(
							resultSet.getString("TABLE_TYPE"), "VIEW")) {

						if (_isCopyableQuartzTable(fromTableName)) {
							_copyQuartzTableRow(
								fromCompanyId, fromTableName, toCompanyId,
								statement);

							quartzTableNames.add(fromTableName);
						}

						continue;
					}

					String toTableName = StringUtil.replace(
						fromTableName, String.valueOf(fromCompanyId),
						String.valueOf(toCompanyId));

					String partitionTableName =
						targetPartitionName + StringPool.PERIOD + toTableName;

					if (fromTableName.contains(String.valueOf(fromCompanyId))) {
						try (SafeCloseable safeCloseable2 =
								CompanyThreadLocal.
									setCompanyIdWithSafeCloseable(
										toCompanyId)) {

							db.runSQL(
								connection,
								StringBundler.concat(
									"alter_table_name ", fromTableName,
									StringPool.SPACE, toTableName));
						}
					}

					if (StringUtil.equalsIgnoreCase(
							fromTableName, "Configuration_")) {

						continue;
					}

					if (dbInspector.hasColumn(fromTableName, "companyId")) {
						statement.executeUpdate(
							StringBundler.concat(
								"update ", partitionTableName, " set ",
								"companyId = ", toCompanyId, " where ",
								"companyId = ", fromCompanyId));
					}

					if (StringUtil.startsWith(
							StringUtil.lowerCase(fromTableName), "object") &&
						dbInspector.hasColumn(fromTableName, "dbTableName")) {

						statement.executeUpdate(
							StringBundler.concat(
								"update ", partitionTableName, " set ",
								"dbTableName = REPLACE(dbTableName, '",
								fromCompanyId, "', '", toCompanyId,
								"') where dbTableName like '%", fromCompanyId,
								"%'"));
					}

					if (StringUtil.equalsIgnoreCase(fromTableName, "Group_")) {
						statement.executeUpdate(
							StringBundler.concat(
								"update ", partitionTableName, " set classPK ",
								"= ", toCompanyId, " where classPK = ",
								fromCompanyId));
						statement.executeUpdate(
							StringBundler.concat(
								"update ", partitionTableName, " set groupKey ",
								"= '", toCompanyId, "' where groupKey = '",
								fromCompanyId, "'"));
					}

					if (StringUtil.equalsIgnoreCase(
							fromTableName, "PortalPreferences")) {

						statement.executeUpdate(
							StringBundler.concat(
								"update ", partitionTableName, " set ownerId ",
								"= ", toCompanyId, " where ownerId = ",
								fromCompanyId));
					}

					if (StringUtil.equalsIgnoreCase(
							fromTableName, "PortletPreferences")) {

						statement.executeUpdate(
							StringBundler.concat(
								"update ", partitionTableName, " set ownerId ",
								"= ", toCompanyId, " where ownerId = ",
								fromCompanyId));
					}

					if (StringUtil.equalsIgnoreCase(
							fromTableName, "ResourcePermission")) {

						statement.executeUpdate(
							StringBundler.concat(
								"update ", partitionTableName, " set primKey ",
								"= '", toCompanyId, "', primKeyId = ",
								toCompanyId, " where primKey = '",
								fromCompanyId, "' and scope = ",
								ResourceConstants.SCOPE_COMPANY));
					}
				}
			}

			connection.commit();

			_reloadQuartzJobs(fromCompanyId, toCompanyId);
		}
		catch (Exception exception1) {
			if (!_dbPartitionDB.isDDLTransactional() ||
				(exception1 instanceof SchedulerException)) {

				try (Statement statement = connection.createStatement()) {
					for (String tableName : quartzTableNames) {
						_deleteData(
							tableName, _defaultPartitionName, statement,
							_getQuartzWhereClauseSQL(toCompanyId, tableName));
					}

					statement.executeUpdate(
						_dbPartitionDB.getDropPartitionSQL(
							targetPartitionName));
				}
				catch (Exception exception2) {
					throw new PortalException(
						"Unable to roll back schema creation", exception2);
				}
			}

			throw new PortalException(exception1);
		}
	}

	private static void _copyQuartzTableRow(
			long fromCompanyId, String tableName, long toCompanyId,
			Statement statement)
		throws Exception {

		if (StringUtil.endsWith(tableName, "JOB_DETAILS")) {
			_replaceCompanyIdQuartzColumns(
				fromCompanyId, toCompanyId, tableName, statement, "job_name");
		}
		else if (StringUtil.equalsIgnoreCase(tableName, "QUARTZ_TRIGGERS") ||
				 StringUtil.equalsIgnoreCase(
					 tableName, "QUARTZ_FIRED_TRIGGERS")) {

			_replaceCompanyIdQuartzColumns(
				fromCompanyId, toCompanyId, tableName, statement, "job_name",
				"trigger_name");
		}
		else {
			_replaceCompanyIdQuartzColumns(
				fromCompanyId, toCompanyId, tableName, statement,
				"trigger_name");
		}
	}

	private static void _copySchema(
			Connection connection, String sourcePartitionName,
			String targetPartitionName)
		throws SQLException {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				_dbPartitionDB.getCreatePartitionSQL(
					connection, targetPartitionName))) {

			preparedStatement.executeUpdate();

			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getTables(
					_dbPartitionDB.getCatalog(connection, sourcePartitionName),
					_dbPartitionDB.getSchema(connection, sourcePartitionName),
					null, new String[] {"TABLE", "VIEW"});
				Statement statement = connection.createStatement()) {

				while (resultSet.next()) {
					String fromTableName = resultSet.getString("TABLE_NAME");

					if (Objects.equals(
							resultSet.getString("TABLE_TYPE"), "VIEW")) {

						statement.executeUpdate(
							_dbPartitionDB.getCreateViewSQL(
								_defaultPartitionName, targetPartitionName,
								fromTableName));

						continue;
					}

					statement.executeUpdate(
						_dbPartitionDB.getCreateTableSQL(
							connection, sourcePartitionName,
							targetPartitionName, fromTableName, fromTableName));

					if (StringUtil.equalsIgnoreCase(
							fromTableName, "Configuration_")) {

						continue;
					}

					statement.executeUpdate(
						_getCopyDataSQL(
							sourcePartitionName, targetPartitionName,
							fromTableName, fromTableName,
							_getColumnNames(connection, fromTableName),
							StringPool.BLANK));
				}
			}

			try (Statement statement = connection.createStatement()) {
				for (String createRuleSQL :
						_dbPartitionDB.getCreateRulesSQL(targetPartitionName)) {

					statement.executeUpdate(createRuleSQL);
				}
			}
		}
	}

	private static void _deleteCompanyData(
			long companyId, String tableName, String fromPartitionName,
			Statement statement)
		throws Exception {

		_deleteData(
			tableName, fromPartitionName, statement,
			" where companyId = " + companyId);
	}

	private static void _deleteData(
			String tableName, String fromPartitionName, Statement statement,
			String whereClause)
		throws Exception {

		statement.executeUpdate(
			StringBundler.concat(
				"delete from ", fromPartitionName, StringPool.PERIOD, tableName,
				whereClause));
	}

	private static AutoCloseable _disableAutoCommit(Connection connection)
		throws Exception {

		boolean autoCommit = connection.getAutoCommit();

		connection.setAutoCommit(false);

		return () -> connection.setAutoCommit(autoCommit);
	}

	private static void _dropDBPartition(long companyId)
		throws PortalException {

		Connection connection = CurrentConnectionUtil.getConnection(
			InfrastructureUtil.getDataSource());

		DBInspector dbInspector = new DBInspector(connection);

		try {
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getTables(
					_dbPartitionDB.getCatalog(
						connection, _defaultPartitionName),
					_dbPartitionDB.getSchema(connection, _defaultPartitionName),
					null, new String[] {"TABLE"});
				Statement statement = connection.createStatement()) {

				while (resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");

					if (!dbInspector.isControlTable(tableName)) {
						continue;
					}

					if (dbInspector.hasColumn(tableName, "companyId")) {
						_deleteCompanyData(
							companyId, tableName, _defaultPartitionName,
							statement);
					}
					else if (_isCopyableQuartzTable(tableName)) {
						_deleteData(
							tableName, _defaultPartitionName, statement,
							_getQuartzWhereClauseSQL(companyId, tableName));
					}
				}

				statement.executeUpdate(
					_dbPartitionDB.getDropPartitionSQL(
						getPartitionName(companyId)));
			}
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	private static void _extractDBPartition(long companyId)
		throws PortalException {

		Connection connection = CurrentConnectionUtil.getConnection(
			InfrastructureUtil.getDataSource());

		DBInspector dbInspector = new DBInspector(connection);

		String extractedPartitionName = _getExtractedPartitionName(companyId);

		try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(companyId);
			AutoCloseable autoCloseable = _disableAutoCommit(connection)) {

			_copySchema(
				connection, getPartitionName(companyId),
				extractedPartitionName);

			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getTables(
					_dbPartitionDB.getCatalog(
						connection, _defaultPartitionName),
					_dbPartitionDB.getSchema(connection, _defaultPartitionName),
					null, new String[] {"TABLE"});
				Statement statement = connection.createStatement()) {

				while (resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");

					if (StringUtil.equalsIgnoreCase(
							tableName, "Configuration_")) {

						statement.executeUpdate(
							_getCopyDataSQL(
								getPartitionName(companyId),
								extractedPartitionName, tableName, tableName,
								_getColumnNames(connection, tableName),
								StringPool.BLANK));

						continue;
					}

					if (dbInspector.isControlTable(tableName)) {
						_extractTable(
							companyId, connection, tableName, statement,
							dbInspector, false);
					}
				}
			}

			connection.commit();
		}
		catch (Exception exception) {
			if (!_dbPartitionDB.isDDLTransactional()) {
				try (Statement statement = connection.createStatement()) {
					statement.executeUpdate(
						_dbPartitionDB.getDropPartitionSQL(
							extractedPartitionName));
				}
				catch (SQLException sqlException) {
					throw new PortalException(
						"Unable to roll back schema creation", sqlException);
				}
			}

			throw new PortalException(
				"Extraction of database partition was rolled back", exception);
		}
	}

	private static void _extractTable(
			long companyId, Connection connection, String tableName,
			Statement statement, DBInspector dbInspector,
			boolean deleteSourceData)
		throws Exception {

		String extractedPartitionName = _getExtractedPartitionName(companyId);

		statement.executeUpdate(
			_dbPartitionDB.getDropViewSQL(extractedPartitionName, tableName));

		statement.executeUpdate(
			_dbPartitionDB.getCreateTableSQL(
				connection, _defaultPartitionName, extractedPartitionName,
				tableName));

		if (dbInspector.hasColumn(tableName, "companyId")) {
			_moveCompanyData(
				companyId, _defaultPartitionName, extractedPartitionName,
				tableName, statement, deleteSourceData);
		}
		else if (_isCopyableQuartzTable(tableName)) {
			_moveData(
				_defaultPartitionName, extractedPartitionName, tableName,
				_getColumnNames(statement.getConnection(), tableName),
				statement, _getQuartzWhereClauseSQL(companyId, tableName),
				deleteSourceData);
		}
		else {
			statement.executeUpdate(
				_getCopyDataSQL(
					_defaultPartitionName, extractedPartitionName, tableName,
					_getColumnNames(statement.getConnection(), tableName),
					StringPool.BLANK));
		}
	}

	private static void _forEachCompanyIdConcurrently(
			UnsafeConsumer<Long, Exception> unsafeConsumer)
		throws Exception {

		Runtime runtime = Runtime.getRuntime();

		ExecutorService executorService = Executors.newFixedThreadPool(
			runtime.availableProcessors());

		List<Future<Void>> futures = new ArrayList<>();

		ThrowableCollector throwableCollector = new ThrowableCollector();

		try {
			long[] companyIds = PortalInstancePool.getCompanyIds();

			if (ArrayUtil.isEmpty(companyIds)) {
				unsafeConsumer.accept(null);
			}
			else {
				try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
						_defaultCompanyId)) {

					unsafeConsumer.accept(_defaultCompanyId);
				}

				for (long companyId : companyIds) {
					if (companyId == _defaultCompanyId) {
						continue;
					}

					Future<Void> future = executorService.submit(
						() -> {
							try (SafeCloseable safeCloseable =
									CompanyThreadLocal.lock(companyId)) {

								unsafeConsumer.accept(companyId);
							}
							catch (Exception exception) {
								throwableCollector.collect(exception);
							}

							return null;
						});

					futures.add(future);
				}
			}
		}
		finally {
			executorService.shutdown();

			for (Future<Void> future : futures) {
				future.get();
			}
		}

		Throwable throwable = throwableCollector.getThrowable();

		if (throwable != null) {
			ReflectionUtil.throwException(throwable);
		}
	}

	private static List<String> _getColumnNames(
			Connection connection, String tableName)
		throws SQLException {

		List<String> columnNames = new ArrayList<>();

		DBInspector dbInspector = new DBInspector(connection);

		try (ResultSet resultSet = dbInspector.getColumnsResultSet(tableName)) {
			while (resultSet.next()) {
				columnNames.add(resultSet.getString("COLUMN_NAME"));
			}
		}

		return columnNames;
	}

	private static Connection _getConnectionWrapper(Connection connection) {
		return new ConnectionWrapper(connection) {

			@Override
			public Statement createStatement() throws SQLException {
				_setPartition();

				return _wrapStatement(super.createStatement());
			}

			@Override
			public Statement createStatement(
					int resultSetType, int resultSetConcurrency)
				throws SQLException {

				_setPartition();

				return _wrapStatement(
					super.createStatement(resultSetType, resultSetConcurrency));
			}

			@Override
			public Statement createStatement(
					int resultSetType, int resultSetConcurrency,
					int resultSetHoldability)
				throws SQLException {

				_setPartition();

				return _wrapStatement(
					super.createStatement(
						resultSetType, resultSetConcurrency,
						resultSetHoldability));
			}

			@Override
			public String getCatalog() throws SQLException {
				return _dbPartitionDB.getCatalog(
					connection,
					getPartitionName(CompanyThreadLocal.getCompanyId()));
			}

			@Override
			public String getSchema() {
				return _dbPartitionDB.getSchema(
					connection,
					getPartitionName(CompanyThreadLocal.getCompanyId()));
			}

			@Override
			public PreparedStatement prepareStatement(String sql)
				throws SQLException {

				_setPartition();

				return super.prepareStatement(sql);
			}

			@Override
			public PreparedStatement prepareStatement(
					String sql, int autoGeneratedKeys)
				throws SQLException {

				_setPartition();

				return super.prepareStatement(sql, autoGeneratedKeys);
			}

			@Override
			public PreparedStatement prepareStatement(
					String sql, int resultSetType, int resultSetConcurrency)
				throws SQLException {

				_setPartition();

				return super.prepareStatement(
					sql, resultSetType, resultSetConcurrency);
			}

			@Override
			public PreparedStatement prepareStatement(
					String sql, int resultSetType, int resultSetConcurrency,
					int resultSetHoldability)
				throws SQLException {

				_setPartition();

				return super.prepareStatement(
					sql, resultSetType, resultSetConcurrency,
					resultSetHoldability);
			}

			@Override
			public PreparedStatement prepareStatement(
					String sql, int[] columnIndexes)
				throws SQLException {

				_setPartition();

				return super.prepareStatement(sql, columnIndexes);
			}

			@Override
			public PreparedStatement prepareStatement(
					String sql, String[] columnNames)
				throws SQLException {

				_setPartition();

				return super.prepareStatement(sql, columnNames);
			}

			private void _setPartition() throws SQLException {
				long companyId = CompanyThreadLocal.getCompanyId();

				String partitionName = getPartitionName(companyId);

				_dbPartitionDB.setPartition(connection, partitionName);

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Using database partition ", partitionName,
							" and company ", companyId));
				}
			}

		};
	}

	private static String _getCopyDataSQL(
		String fromPartitionName, String toPartitionName, String tableName,
		List<String> columnNames, String whereClause) {

		return _getCopyDataSQL(
			fromPartitionName, toPartitionName, tableName, tableName,
			columnNames, whereClause);
	}

	private static String _getCopyDataSQL(
		String fromPartitionName, String toPartitionName, String fromTableName,
		String toTableName, List<String> columnNames, String whereClause) {

		return StringBundler.concat(
			"insert into ", toPartitionName, StringPool.PERIOD, toTableName,
			StringPool.OPEN_PARENTHESIS, StringUtil.merge(columnNames),
			") select ", StringUtil.merge(columnNames), " from ",
			fromPartitionName, StringPool.PERIOD, fromTableName, whereClause);
	}

	private static String _getExtractedPartitionName(long companyId) {
		return _DATABASE_EXTRACTED_PARTITION_SCHEMA_NAME_PREFIX + companyId;
	}

	private static String _getQuartzWhereClauseSQL(
		long companyId, String tableName) {

		if (StringUtil.endsWith(tableName, "JOB_DETAILS")) {
			return " where job_name like '%@" + companyId + "'";
		}

		return " where trigger_name like '%@" + companyId + "'";
	}

	private static void _insertDBPartition(long companyId)
		throws PortalException {

		String sourcePartitionName = _getExtractedPartitionName(companyId);
		String targetPartitionName = getPartitionName(companyId);

		Connection connection = CurrentConnectionUtil.getConnection(
			InfrastructureUtil.getDataSource());

		try {
			if (_dbPartitionDB.existsPartition(
					connection, targetPartitionName)) {

				throw new IllegalArgumentException(
					StringBundler.concat(
						"Database partition ", targetPartitionName,
						" already exists"));
			}

			if (!_dbPartitionDB.existsPartition(
					connection, sourcePartitionName)) {

				throw new IllegalArgumentException(
					"Unable to insert the database partition " +
						sourcePartitionName + " because it does not exist");
			}
		}
		catch (SQLException sqlException) {
			throw new PortalException(sqlException);
		}

		AutoCloseable autoCloseable = null;
		List<String> copiedTableNames = new ArrayList<>();

		try (Statement statement = connection.createStatement()) {
			for (String query :
					_dbPartitionDB.getRenamePartitionSQLs(
						connection, sourcePartitionName, targetPartitionName)) {

				statement.executeUpdate(query);
			}

			autoCloseable = _disableAutoCommit(connection);

			DBInspector dbInspector = new DBInspector(connection);

			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet resultSet = databaseMetaData.getTables(
					_dbPartitionDB.getCatalog(
						connection, _defaultPartitionName),
					_dbPartitionDB.getSchema(connection, _defaultPartitionName),
					null, new String[] {"TABLE"})) {

				while (resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");

					if (!dbInspector.isControlTable(tableName)) {
						continue;
					}

					if (dbInspector.hasColumn(tableName, "companyId")) {
						statement.executeUpdate(
							_getCopyDataSQL(
								targetPartitionName, _defaultPartitionName,
								tableName,
								_getColumnNames(connection, tableName),
								" where companyId = " + companyId));

						copiedTableNames.add(tableName);
					}
					else if (_isCopyableQuartzTable(tableName)) {
						statement.executeUpdate(
							_getCopyDataSQL(
								targetPartitionName, _defaultPartitionName,
								tableName,
								_getColumnNames(connection, tableName),
								_getQuartzWhereClauseSQL(
									companyId, tableName)));

						copiedTableNames.add(tableName);
					}

					statement.executeUpdate(
						_dbPartitionDB.getDropTableSQL(
							targetPartitionName, tableName));

					statement.executeUpdate(
						_dbPartitionDB.getCreateViewSQL(
							_defaultPartitionName, targetPartitionName,
							tableName));
				}

				connection.commit();
			}
		}
		catch (Exception exception1) {
			if (_dbPartitionDB.isDDLTransactional()) {
				throw new PortalException(exception1);
			}

			try (Statement statement = connection.createStatement()) {
				DBInspector dbInspector = new DBInspector(connection);

				for (String query :
						_dbPartitionDB.getRenamePartitionSQLs(
							connection, targetPartitionName,
							sourcePartitionName)) {

					statement.executeUpdate(query);
				}

				for (String copiedTableName : copiedTableNames) {
					_extractTable(
						companyId, connection, copiedTableName, statement,
						dbInspector, true);
				}

				connection.commit();
			}
			catch (Exception exception2) {
				throw new PortalException(
					StringBundler.concat(
						"Unable to roll back the data inserted into the ",
						"default schema for tables ", copiedTableNames,
						" and company ID ", companyId),
					exception2);
			}

			throw new PortalException(exception1);
		}
		finally {
			if (autoCloseable != null) {
				try {
					autoCloseable.close();
				}
				catch (Exception exception) {
					throw new PortalException(exception);
				}
			}
		}
	}

	private static boolean _isCopyableQuartzTable(String tableName) {
		if (StringUtil.startsWith(tableName, _QUARTZ_TABLE_NAME_PREFIX) &&
			(StringUtil.endsWith(tableName, "JOB_DETAILS") ||
			 StringUtil.endsWith(tableName, "TRIGGERS"))) {

			return true;
		}

		return false;
	}

	private static boolean _isSkip(Connection connection, String tableName)
		throws SQLException {

		try {
			DBInspector dbInspector = new DBInspector(connection);

			if ((dbInspector.isControlTable(tableName) &&
				 (CompanyThreadLocal.getNonsystemCompanyId() !=
					 _defaultCompanyId)) ||
				dbInspector.hasView(tableName)) {

				return true;
			}
		}
		catch (Exception exception) {
			throw new SQLException(
				"Unable to check if the table " + tableName +
					" is a control table",
				exception);
		}

		return false;
	}

	private static void _moveCompanyData(
			long companyId, String fromPartitionName, String toPartitionName,
			String tableName, Statement statement, boolean deleteSourceData)
		throws Exception {

		_moveData(
			fromPartitionName, toPartitionName, tableName,
			_getColumnNames(statement.getConnection(), tableName), statement,
			" where companyId = " + companyId, deleteSourceData);
	}

	private static void _moveData(
			String fromPartitionName, String toPartitionName, String tableName,
			List<String> columnNames, Statement statement, String whereClause,
			boolean deleteSourceData)
		throws Exception {

		statement.executeUpdate(
			_getCopyDataSQL(
				fromPartitionName, toPartitionName, tableName, columnNames,
				whereClause));

		if (deleteSourceData) {
			_deleteData(tableName, fromPartitionName, statement, whereClause);
		}
	}

	private static void _reloadQuartzJobs(long fromCompanyId, long toCompanyId)
		throws SchedulerException {

		for (SchedulerResponse schedulerResponse :
				SchedulerEngineHelperUtil.getScheduledJobs()) {

			Message message = schedulerResponse.getMessage();

			String jobName = schedulerResponse.getJobName();

			if ((message.getLong("companyId") != fromCompanyId) ||
				!jobName.contains(String.valueOf(toCompanyId))) {

				continue;
			}

			SchedulerEngineHelperUtil.delete(
				jobName, schedulerResponse.getGroupName(),
				schedulerResponse.getStorageType());

			message.remove(SchedulerEngine.JOB_STATE);

			message.put("companyId", toCompanyId);

			SchedulerEngineHelperUtil.schedule(
				schedulerResponse.getTrigger(),
				schedulerResponse.getStorageType(),
				schedulerResponse.getDescription(),
				schedulerResponse.getDestinationName(), message);
		}
	}

	private static void _replaceCompanyIdQuartzColumns(
			long fromCompanyId, long toCompanyId, String tableName,
			Statement statement, String... replaceColumnNames)
		throws Exception {

		List<String> columnNames = _getColumnNames(
			statement.getConnection(), tableName);

		List<String> replaceSQLs = new ArrayList<>();

		for (String replaceColumnName : replaceColumnNames) {
			replaceSQLs.add(
				StringBundler.concat(
					"replace (", replaceColumnName, ", '@", fromCompanyId,
					"', '@", toCompanyId, "') as ", replaceColumnName));

			columnNames.removeIf(
				value -> value.equalsIgnoreCase(replaceColumnName));
		}

		statement.executeUpdate(
			StringBundler.concat(
				"insert into ", tableName, "(",
				StringUtil.merge(replaceColumnNames), ", ",
				StringUtil.merge(columnNames), ") select ",
				StringUtil.merge(replaceSQLs), ", ",
				StringUtil.merge(columnNames), " from ", tableName,
				_getQuartzWhereClauseSQL(fromCompanyId, tableName)));
	}

	private static Statement _wrapStatement(Statement statement) {
		return new StatementWrapper(statement) {

			@Override
			public int executeUpdate(String sql) throws SQLException {
				Connection connection = statement.getConnection();

				String lowerCaseSQL = StringUtil.toLowerCase(sql);

				String[] query = sql.split(StringPool.SPACE);

				if ((StringUtil.startsWith(lowerCaseSQL, "alter table") &&
					 _isSkip(connection, query[2])) ||
					(StringUtil.startsWith(lowerCaseSQL, "create index") &&
					 _isSkip(connection, query[4])) ||
					(StringUtil.startsWith(
						lowerCaseSQL, "create unique index") &&
					 _isSkip(connection, query[5]))) {

					return 0;
				}
				else if (StringUtil.startsWith(lowerCaseSQL, "drop index")) {
					if ((query.length >= 5) && _isSkip(connection, query[4])) {
						return 0;
					}
					else if (query.length <= 4) {
						sql = StringUtil.replace(
							sql, "drop index ", "drop index if exists ");

						sql = StringUtil.replace(
							sql, "DROP INDEX ", "DROP INDEX IF EXISTS ");
					}
				}

				if (!StringUtil.startsWith(lowerCaseSQL, "alter table")) {
					return super.executeUpdate(sql);
				}

				sql = _dbPartitionDB.getSafeAlterTable(sql);

				int returnValue = super.executeUpdate(sql);

				try {
					DBInspector dbInspector = new DBInspector(connection);
					String tableName = query[2];

					if (!dbInspector.isControlTable(tableName)) {
						return returnValue;
					}

					for (long companyId : PortalInstancePool.getCompanyIds()) {
						if (companyId == _defaultCompanyId) {
							continue;
						}

						super.execute(
							_dbPartitionDB.getCreateViewSQL(
								_defaultPartitionName,
								getPartitionName(companyId), tableName));
					}

					return returnValue;
				}
				catch (Exception exception) {
					throw new SQLException(exception);
				}
			}

		};
	}

	private static final String
		_DATABASE_EXTRACTED_PARTITION_SCHEMA_NAME_PREFIX = "lextracted_";

	private static final String _DATABASE_PARTITION_SCHEMA_NAME_PREFIX =
		GetterUtil.get(
			PropsUtil.get("database.partition.schema.name.prefix"),
			"lpartition_");

	private static final boolean _DATABASE_PARTITION_THREAD_POOL_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get("database.partition.thread.pool.enabled"), true);

	private static final String _QUARTZ_TABLE_NAME_PREFIX = GetterUtil.get(
		PropsUtil.get("persisted.scheduler.org.quartz.jobStore.tablePrefix"),
		"QUARTZ_");

	private static final Log _log = LogFactoryUtil.getLog(
		DBPartitionUtil.class);

	private static DBPartitionDB _dbPartitionDB;
	private static volatile long _defaultCompanyId;
	private static String _defaultPartitionName;

}