/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.framework.ThrowableCollector;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.recorder.UpgradeSQLRecorder;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.NotificationThreadLocal;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.NamingException;

import javax.sql.DataSource;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Hugo Huijser
 * @author Brian Wing Shun Chan
 */
public abstract class BaseDBProcess implements DBProcess {

	@Override
	public void runSQL(Connection connection, String template)
		throws IOException, SQLException {

		DB db = DBManagerUtil.getDB();

		db.runSQL(connection, template);
	}

	@Override
	public void runSQL(DBTypeToSQLMap dbTypeToSQLMap)
		throws IOException, SQLException {

		DB db = DBManagerUtil.getDB();

		if (connection == null) {
			db.runSQL(dbTypeToSQLMap);
		}
		else {
			db.runSQL(connection, dbTypeToSQLMap);
		}
	}

	@Override
	public void runSQL(String template) throws IOException, SQLException {
		DB db = DBManagerUtil.getDB();

		if (connection == null) {
			db.runSQL(template);
		}
		else {
			db.runSQL(connection, template);
		}
	}

	@Override
	public void runSQL(String[] templates) throws IOException, SQLException {
		DB db = DBManagerUtil.getDB();

		if (connection == null) {
			db.runSQL(templates);
		}
		else {
			db.runSQL(connection, templates);
		}
	}

	@Override
	public void runSQLFile(String path)
		throws IOException, NamingException, SQLException {

		runSQLFile(path, true);
	}

	@Override
	public void runSQLFile(String path, boolean failOnError)
		throws IOException, NamingException, SQLException {

		try (LoggingTimer loggingTimer = new LoggingTimer(path)) {
			InputStream inputStream = _getInputStream(path);

			if (inputStream == null) {
				_log.error("Invalid path " + path);

				if (failOnError) {
					throw new IOException("Invalid path " + path);
				}

				return;
			}

			String template = StringUtil.read(inputStream);

			runSQLTemplate(template, failOnError);
		}
	}

	@Override
	public void runSQLTemplate(String template, boolean failOnError)
		throws IOException, NamingException, SQLException {

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			if (template.endsWith(".sql") ||
				(_getInputStream(template) != null)) {

				runSQLFile(template, failOnError);

				return;
			}

			DB db = DBManagerUtil.getDB();

			if (connection == null) {
				db.runSQLTemplate(template, failOnError);
			}
			else {
				db.runSQLTemplate(connection, template, failOnError);
			}
		}
	}

	protected void addIndexes(
			Connection connection, List<IndexMetadata> indexMetadatas)
		throws IOException, SQLException {

		DB db = DBManagerUtil.getDB();

		db.addIndexes(connection, indexMetadatas);
	}

	protected void alterColumnName(
			String tableName, String oldColumnName, String newColumnDefinition)
		throws Exception {

		String newColumnName = StringUtil.extractFirst(
			newColumnDefinition, StringPool.SPACE);

		String newColumnType = newColumnDefinition.substring(
			newColumnName.length() + 1);

		if (!hasColumn(tableName, oldColumnName)) {
			if (hasColumnType(tableName, newColumnName, newColumnType)) {
				return;
			}

			throw new SQLException(
				StringBundler.concat(
					"Column ", tableName, StringPool.PERIOD, oldColumnName,
					" does not exist"));
		}

		if (!hasColumnType(tableName, oldColumnName, newColumnType)) {
			throw new SQLException(
				StringBundler.concat(
					"Type change is not allowed when altering column name. ",
					"Column ", tableName, StringPool.PERIOD, oldColumnName,
					" has different type than ", newColumnType));
		}

		DBInspector dbInspector = new DBInspector(connection);

		if (StringUtil.equals(
				dbInspector.normalizeName(oldColumnName),
				dbInspector.normalizeName(newColumnName))) {

			return;
		}

		DB db = DBManagerUtil.getDB();

		db.alterColumnName(
			connection, tableName, oldColumnName, newColumnDefinition);
	}

	protected void alterColumnType(
			String tableName, String columnName, String newColumnType)
		throws Exception {

		if (!hasColumn(tableName, columnName)) {
			throw new SQLException(
				StringBundler.concat(
					"Column ", tableName, StringPool.PERIOD, columnName,
					" does not exist"));
		}

		if (!hasColumnType(tableName, columnName, newColumnType)) {
			DB db = DBManagerUtil.getDB();

			db.alterColumnType(
				connection, tableName, columnName, newColumnType);
		}
	}

	protected void alterTableAddColumn(
			String tableName, String columnName, String columnType)
		throws Exception {

		if (!hasColumn(tableName, columnName)) {
			DB db = DBManagerUtil.getDB();

			db.alterTableAddColumn(
				connection, tableName, columnName, columnType);

			return;
		}

		if (!hasColumnType(tableName, columnName, columnType)) {
			throw new SQLException(
				StringBundler.concat(
					"Column ", tableName, StringPool.PERIOD, columnName,
					" already exists with different type than ", columnType));
		}
	}

	protected void alterTableDropColumn(String tableName, String columnName)
		throws Exception {

		if (hasColumn(tableName, columnName)) {
			DB db = DBManagerUtil.getDB();

			db.alterTableDropColumn(connection, tableName, columnName);
		}
	}

	protected void alterTableName(String tableName, String newTableName)
		throws Exception {

		runSQL(
			StringBundler.concat(
				"alter_table_name ", tableName, StringPool.SPACE,
				newTableName));
	}

	protected void closeConnections() {
		Map<Thread, Connection> connectionsMap = _connectionsMaps.get(
			DBPartition.isPartitionEnabled() ?
				CompanyThreadLocal.getCompanyId() : CompanyConstants.SYSTEM);

		_closeConnections(connectionsMap);
	}

	protected void closeConnections(Thread thread) {
		Map<Thread, Connection> connectionsMap = _connectionsMaps.get(
			DBPartition.isPartitionEnabled() ?
				CompanyThreadLocal.getCompanyId() : CompanyConstants.SYSTEM);

		_closeConnections(connectionsMap, thread);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #hasTable(String)}
	 */
	@Deprecated
	protected boolean doHasTable(String tableName) throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		return dbInspector.hasTable(tableName);
	}

	protected void dropIndexes(List<String> indexNames, String tableName)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		db.dropIndexes(connection, indexNames, tableName);
	}

	protected List<IndexMetadata> dropIndexes(
			String tableName, String columnName)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		return db.dropIndexes(connection, tableName, columnName);
	}

	protected void dropTable(String tableName) throws Exception {
		runSQL("DROP_TABLE_IF_EXISTS(" + tableName + ")");
	}

	protected Connection getConnection() throws Exception {
		return UpgradeSQLRecorder.getConnectionWrapper(
			(Connection)ProxyUtil.newProxyInstance(
				ClassLoader.getSystemClassLoader(),
				new Class<?>[] {Connection.class},
				new ConnectionThreadProxyInvocationHandler()),
			ClassUtil.getClassName(this));
	}

	protected String[] getPrimaryKeyColumnNames(
			Connection connection, String tableName)
		throws SQLException {

		DB db = DBManagerUtil.getDB();

		return db.getPrimaryKeyColumnNames(connection, tableName);
	}

	protected boolean hasColumn(String tableName, String columnName)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		return dbInspector.hasColumn(tableName, columnName);
	}

	protected boolean hasColumnType(
			String tableName, String columnName, String columnType)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		return dbInspector.hasColumnType(tableName, columnName, columnType);
	}

	protected boolean hasIndex(String tableName, String indexName)
		throws Exception {

		DBInspector dbInspector = new DBInspector(connection);

		return dbInspector.hasIndex(tableName, indexName);
	}

	protected boolean hasRows(Connection connection, String tableName) {
		DBInspector dbInspector = new DBInspector(connection);

		return dbInspector.hasRows(tableName);
	}

	protected boolean hasRows(String tableName) throws Exception {
		return hasRows(connection, tableName);
	}

	protected boolean hasTable(String tableName) throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		return dbInspector.hasTable(tableName);
	}

	protected boolean hasView(String viewName) throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		return dbInspector.hasView(viewName);
	}

	protected void process(UnsafeConsumer<Long, Exception> unsafeConsumer)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		db.process(unsafeConsumer);
	}

	protected void processConcurrently(
			String sql, String updateSQL,
			UnsafeFunction<ResultSet, Object[], Exception> unsafeFunction,
			UnsafeBiConsumer<Object[], PreparedStatement, Exception>
				unsafeBiConsumer,
			String exceptionMessage)
		throws Exception {

		processConcurrently(
			sql,
			preparedStatement -> {
			},
			updateSQL, unsafeFunction, unsafeBiConsumer, exceptionMessage);
	}

	protected void processConcurrently(
			String sql,
			UnsafeConsumer<PreparedStatement, Exception> unsafeConsumer,
			String updateSQL,
			UnsafeFunction<ResultSet, Object[], Exception> unsafeFunction,
			UnsafeBiConsumer<Object[], PreparedStatement, Exception>
				unsafeBiConsumer,
			String exceptionMessage)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setFetchSize(
				PropsValues.UPGRADE_CONCURRENT_FETCH_SIZE);

			unsafeConsumer.accept(preparedStatement);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				_processConcurrently(
					updateSQL,
					() -> {
						if (resultSet.next()) {
							return unsafeFunction.apply(resultSet);
						}

						return null;
					},
					null, unsafeBiConsumer, exceptionMessage);
			}
		}
	}

	protected void processConcurrently(
			String sql,
			UnsafeFunction<ResultSet, Object[], Exception> unsafeFunction,
			UnsafeConsumer<Object[], Exception> unsafeConsumer,
			String exceptionMessage)
		throws Exception {

		try (Statement statement = connection.createStatement()) {
			statement.setFetchSize(PropsValues.UPGRADE_CONCURRENT_FETCH_SIZE);

			try (ResultSet resultSet = statement.executeQuery(sql)) {
				_processConcurrently(
					null,
					() -> {
						if (resultSet.next()) {
							return unsafeFunction.apply(resultSet);
						}

						return null;
					},
					unsafeConsumer, null, exceptionMessage);
			}
		}
	}

	protected <T> void processConcurrently(
			T[] array, UnsafeConsumer<T, Exception> unsafeConsumer,
			String exceptionMessage)
		throws Exception {

		AtomicInteger atomicInteger = new AtomicInteger();

		_processConcurrently(
			null,
			() -> {
				int index = atomicInteger.getAndIncrement();

				if (index < array.length) {
					return array[index];
				}

				return null;
			},
			unsafeConsumer, null, exceptionMessage);
	}

	protected void removePrimaryKey(String tableName) throws Exception {
		DB db = DBManagerUtil.getDB();

		db.removePrimaryKey(connection, tableName);
	}

	protected Connection connection;

	private void _closeConnections(Map<Thread, Connection> connectionsMap) {
		if (MapUtil.isEmpty(connectionsMap)) {
			return;
		}

		Collection<Connection> connections = connectionsMap.values();

		try {
			Iterator<Connection> iterator = connections.iterator();

			while (iterator.hasNext()) {
				Connection connection = iterator.next();

				iterator.remove();

				connection.close();
			}
		}
		catch (SQLException sqlException) {
			_log.error(sqlException);
		}
	}

	private void _closeConnections(
		Map<Thread, Connection> connectionsMap, Thread thread) {

		if (MapUtil.isEmpty(connectionsMap)) {
			return;
		}

		try {
			for (Map.Entry<Thread, Connection> entry :
					connectionsMap.entrySet()) {

				if (entry.getKey() == thread) {
					Connection connection = entry.getValue();

					connectionsMap.remove(entry.getKey());

					connection.close();

					return;
				}
			}
		}
		catch (SQLException sqlException) {
			_log.error(sqlException);
		}
	}

	private PreparedStatement _getConcurrentPreparedStatement(
		String updateSQL,
		Map<Thread, PreparedStatement> preparedStatementHashMap) {

		return preparedStatementHashMap.computeIfAbsent(
			Thread.currentThread(),
			k -> {
				try {
					return AutoBatchPreparedStatementUtil.autoBatch(
						connection, updateSQL);
				}
				catch (SQLException sqlException) {
					throw new RuntimeException(sqlException);
				}
			});
	}

	private Connection _getConnection() {
		try {
			Bundle bundle = FrameworkUtil.getBundle(getClass());

			if (bundle != null) {
				BundleContext bundleContext = bundle.getBundleContext();

				Collection<ServiceReference<DataSource>> serviceReferences =
					bundleContext.getServiceReferences(
						DataSource.class,
						StringBundler.concat(
							"(origin.bundle.symbolic.name=",
							bundle.getSymbolicName(), ")"));

				Iterator<ServiceReference<DataSource>> iterator =
					serviceReferences.iterator();

				if (iterator.hasNext()) {
					ServiceReference<DataSource> serviceReference =
						iterator.next();

					DataSource dataSource = bundleContext.getService(
						serviceReference);

					try {
						if (dataSource != null) {
							return dataSource.getConnection();
						}
					}
					finally {
						bundleContext.ungetService(serviceReference);
					}
				}
			}

			return DataAccess.getConnection();
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	private int _getFixedThreadPoolSize() {
		if (_fixedThreadPoolSize.get() != 0) {
			return _fixedThreadPoolSize.get();
		}

		long[] companyIds = PortalInstancePool.getCompanyIds();

		int maximumPoolSize = GetterUtil.getInteger(
			PropsUtil.get("jdbc.default.maximumPoolSize"));

		Runtime runtime = Runtime.getRuntime();

		int expectedMaxConnectionsCount =
			Math.min(companyIds.length - 1, runtime.availableProcessors()) *
				runtime.availableProcessors();

		if (expectedMaxConnectionsCount > (0.9 * maximumPoolSize)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"The database is close to reaching ", maximumPoolSize,
						" connections. Consider increasing the property ",
						"\"jdbc.default.maximumPoolSize\" to improve ",
						"performance. Upgrade processes will continue in ",
						"single threaded mode."));
			}

			_fixedThreadPoolSize.set(1);
		}
		else {
			_fixedThreadPoolSize.set(runtime.availableProcessors());
		}

		return _fixedThreadPoolSize.get();
	}

	private InputStream _getInputStream(String path) {
		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/portal/tools/sql/dependencies/" + path);

		if (inputStream == null) {
			inputStream = classLoader.getResourceAsStream(path);
		}

		if (inputStream == null) {
			Thread currentThread = Thread.currentThread();

			classLoader = currentThread.getContextClassLoader();

			inputStream = classLoader.getResourceAsStream(path);
		}

		return inputStream;
	}

	private <T> void _processConcurrently(
			String updateSQL, UnsafeSupplier<T, Exception> unsafeSupplier,
			UnsafeConsumer<T, Exception> unsafeConsumer,
			UnsafeBiConsumer<T, PreparedStatement, Exception> unsafeBiConsumer,
			String exceptionMessage)
		throws Exception {

		Objects.requireNonNull(unsafeSupplier);

		if (Validator.isNull(updateSQL)) {
			Objects.requireNonNull(unsafeConsumer);
		}
		else {
			Objects.requireNonNull(unsafeBiConsumer);
		}

		ExecutorService executorService = Executors.newFixedThreadPool(
			_getFixedThreadPoolSize());

		List<Future<Void>> futures = new ArrayList<>();
		Map<Thread, PreparedStatement> preparedStatementHashMap =
			new ConcurrentHashMap<>();
		Set<Thread> threads = new CopyOnWriteArraySet<>();
		ThrowableCollector throwableCollector = new ThrowableCollector();

		try {
			boolean notificationEnabled = NotificationThreadLocal.isEnabled();
			boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

			T next = null;

			while ((next = unsafeSupplier.get()) != null) {
				T current = next;

				Future<Void> future = executorService.submit(
					new CompanyInheritableThreadLocalCallable<>(
						() -> {
							NotificationThreadLocal.setEnabled(
								notificationEnabled);
							WorkflowThreadLocal.setEnabled(workflowEnabled);

							try {
								threads.add(Thread.currentThread());

								if (Validator.isNull(updateSQL)) {
									unsafeConsumer.accept(current);
								}
								else {
									unsafeBiConsumer.accept(
										current,
										_getConcurrentPreparedStatement(
											updateSQL,
											preparedStatementHashMap));
								}
							}
							catch (Exception exception) {
								throwableCollector.collect(exception);
							}

							return null;
						}));

				if (futures.size() >=
						PropsValues.
							UPGRADE_CONCURRENT_PROCESS_FUTURE_LIST_MAX_SIZE) {

					for (Future<Void> curFuture : futures) {
						curFuture.get();
					}

					futures.clear();
				}

				futures.add(future);
			}
		}
		finally {
			try {
				executorService.shutdown();

				for (Future<Void> future : futures) {
					future.get();
				}

				Throwable throwable = throwableCollector.getThrowable();

				if (throwable != null) {
					if (exceptionMessage != null) {
						throw new Exception(exceptionMessage, throwable);
					}

					ReflectionUtil.throwException(throwable);
				}

				try {
					for (PreparedStatement preparedStatement :
							preparedStatementHashMap.values()) {

						preparedStatement.executeBatch();

						preparedStatement.close();
					}
				}
				catch (Exception exception) {
					_log.error(exceptionMessage, exception);

					throw exception;
				}
			}
			finally {
				for (Thread thread : threads) {
					closeConnections(thread);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(BaseDBProcess.class);

	private static final AtomicInteger _fixedThreadPoolSize = new AtomicInteger(
		0);

	private final Map<Long, Map<Thread, Connection>> _connectionsMaps =
		new ConcurrentHashMap<>();

	private class ConnectionThreadProxyInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			String methodName = method.getName();

			if (methodName.equals("close")) {
				for (Map<Thread, Connection> connectionsMap :
						_connectionsMaps.values()) {

					_closeConnections(connectionsMap);
				}

				return null;
			}

			Map<Thread, Connection> connectionsMap =
				_connectionsMaps.computeIfAbsent(
					DBPartition.isPartitionEnabled() ?
						CompanyThreadLocal.getCompanyId() :
							CompanyConstants.SYSTEM,
					key -> new ConcurrentHashMap<>());

			return method.invoke(
				connectionsMap.compute(
					Thread.currentThread(),
					(thread, connection) -> {
						try {
							if ((connection != null) &&
								!connection.isClosed()) {

								return connection;
							}
						}
						catch (Exception exception) {
							if (_log.isDebugEnabled()) {
								_log.debug(exception);
							}
						}

						return _getConnection();
					}),
				args);
		}

	}

}