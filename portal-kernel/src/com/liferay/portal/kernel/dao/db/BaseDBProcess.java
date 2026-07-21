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
import com.liferay.portal.kernel.db.UpgradeExecutorServiceUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.framework.ThrowableCollector;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.recorder.UpgradeLogProgressTracker;
import com.liferay.portal.kernel.upgrade.recorder.UpgradeSQLRecorder;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.NotificationThreadLocal;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
			PropsValues.DATABASE_PARTITION_ENABLED ?
				CompanyThreadLocal.getCompanyId() : CompanyConstants.SYSTEM);

		_closeConnections(connectionsMap);
	}

	protected void closeConnections(Thread thread) {
		Map<Thread, Connection> connectionsMap = _connectionsMaps.get(
			PropsValues.DATABASE_PARTITION_ENABLED ?
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
		Connection connection = (Connection)ProxyUtil.newProxyInstance(
			ClassLoader.getSystemClassLoader(),
			new Class<?>[] {Connection.class},
			new ConnectionThreadProxyInvocationHandler());

		connection = UpgradeSQLRecorder.getConnectionWrapper(
			connection, ClassUtil.getClassName(this));

		return UpgradeLogProgressTracker.wrap(
			connection, ClassUtil.getClassName(this));
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

	protected <K, V> void processConcurrently(
			Map<K, V> map,
			UnsafeConsumer<Map.Entry<K, V>, Exception> unsafeConsumer,
			String exceptionMessage)
		throws Exception {

		Set<Map.Entry<K, V>> set = map.entrySet();

		Iterator<Map.Entry<K, V>> iterator = set.iterator();

		_processConcurrently(
			map.size(), null,
			() -> {
				if (!iterator.hasNext()) {
					return null;
				}

				return iterator.next();
			},
			unsafeConsumer, null, exceptionMessage);
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

			_connectionCount.incrementAndGet();

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				_processConcurrently(
					Integer.MAX_VALUE, updateSQL,
					() -> {
						if (resultSet.next()) {
							return unsafeFunction.apply(resultSet);
						}

						return null;
					},
					null, unsafeBiConsumer, exceptionMessage);
			}
			finally {
				_connectionCount.decrementAndGet();
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

			_connectionCount.incrementAndGet();

			try (ResultSet resultSet = statement.executeQuery(sql)) {
				_processConcurrently(
					Integer.MAX_VALUE, null,
					() -> {
						if (resultSet.next()) {
							return unsafeFunction.apply(resultSet);
						}

						return null;
					},
					unsafeConsumer, null, exceptionMessage);
			}
			finally {
				_connectionCount.decrementAndGet();
			}
		}
	}

	protected <T> void processConcurrently(
			T[] array, UnsafeConsumer<T, Exception> unsafeConsumer,
			String exceptionMessage)
		throws Exception {

		AtomicInteger atomicInteger = new AtomicInteger();

		_processConcurrently(
			array.length, null,
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

	private void _closeConnection(Connection connection) {
		Boolean autoCommit = _autoCommits.remove(connection);

		if (autoCommit != null) {
			if (!autoCommit && _log.isWarnEnabled()) {
				_log.warn(
					"Closing a connection that still has an autoCommit " +
						"override");
			}

			try {
				connection.rollback();
			}
			catch (SQLException sqlException) {
				_log.error(
					"Unable to finish a transactional connection",
					sqlException);
			}

			try {
				connection.setAutoCommit(autoCommit);
			}
			catch (SQLException sqlException) {
				_log.error("Unable to set autoCommit", sqlException);
			}
		}

		DataAccess.cleanUp(connection);
	}

	private void _closeConnections(Map<Thread, Connection> connectionsMap) {
		if (MapUtil.isEmpty(connectionsMap)) {
			return;
		}

		Collection<Connection> connections = connectionsMap.values();

		Iterator<Connection> iterator = connections.iterator();

		while (iterator.hasNext()) {
			Connection connection = iterator.next();

			iterator.remove();

			_closeConnection(connection);
		}
	}

	private void _closeConnections(
		Map<Thread, Connection> connectionsMap, Thread thread) {

		if (connectionsMap == null) {
			return;
		}

		Connection connection = connectionsMap.remove(thread);

		if (connection != null) {
			_closeConnection(connection);
		}
	}

	private PreparedStatement _getConcurrentPreparedStatement(
		String updateSQL,
		Map<Thread, PreparedStatement> preparedStatementHashMap) {

		return preparedStatementHashMap.computeIfAbsent(
			Thread.currentThread(),
			k -> {
				try {
					PreparedStatement preparedStatement =
						AutoBatchPreparedStatementUtil.autoBatch(
							connection, updateSQL);

					Map<Thread, Connection> connectionsMap =
						_connectionsMaps.get(
							PropsValues.DATABASE_PARTITION_ENABLED ?
								CompanyThreadLocal.getCompanyId() :
									CompanyConstants.SYSTEM);

					if (connectionsMap == null) {
						return preparedStatement;
					}

					Connection workerConnection = connectionsMap.get(
						Thread.currentThread());

					if (workerConnection == null) {
						return preparedStatement;
					}

					return (PreparedStatement)ProxyUtil.newProxyInstance(
						ClassLoader.getSystemClassLoader(),
						new Class<?>[] {PreparedStatement.class},
						new BatchCommitInvocationHandler(
							workerConnection, preparedStatement));
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

	private <T> void _process(
			String updateSQL, UnsafeSupplier<T, Exception> unsafeSupplier,
			UnsafeConsumer<T, Exception> unsafeConsumer,
			UnsafeBiConsumer<T, PreparedStatement, Exception> unsafeBiConsumer,
			Map<Thread, PreparedStatement> preparedStatementHashMap)
		throws Exception {

		PreparedStatement preparedStatement = null;

		try {
			T t = unsafeSupplier.get();

			while (t != null) {
				if (Validator.isNull(updateSQL)) {
					unsafeConsumer.accept(t);
				}
				else {
					preparedStatement = _getConcurrentPreparedStatement(
						updateSQL, preparedStatementHashMap);

					unsafeBiConsumer.accept(t, preparedStatement);
				}

				t = unsafeSupplier.get();
			}

			if (preparedStatement != null) {
				preparedStatement.executeBatch();
			}
		}
		finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	private <T> void _processConcurrently(
			int itemCount, String updateSQL,
			UnsafeSupplier<T, Exception> unsafeSupplier,
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

		DB db = DBManagerUtil.getDB();

		int size = 0;

		if (db.getDBType() != DBType.HYPERSONIC) {
			int parallelCompanies = 1;

			if (PropsValues.DATABASE_PARTITION_ENABLED) {
				long[] companyIds = PortalInstancePool.getCompanyIds();

				Runtime runtime = Runtime.getRuntime();

				parallelCompanies = Math.min(
					companyIds.length - 1, runtime.availableProcessors());
			}

			int availableConnectionCount =
				UpgradeExecutorServiceUtil.getDataExecutorServicePoolSize() -
					_connectionCount.get();

			size = Math.min(
				itemCount,
				availableConnectionCount / Math.max(1, parallelCompanies));
		}

		if (size <= 0) {
			_process(
				updateSQL, unsafeSupplier, unsafeConsumer, unsafeBiConsumer,
				new ConcurrentHashMap<>());

			return;
		}

		List<Future<Void>> futures = new ArrayList<>();
		Map<Thread, PreparedStatement> preparedStatementHashMap =
			new ConcurrentHashMap<>();
		AtomicBoolean producerFinished = new AtomicBoolean();
		BlockingQueue<T> queue = new ArrayBlockingQueue<>(size);
		ThrowableCollector throwableCollector = new ThrowableCollector();

		boolean notificationEnabled = NotificationThreadLocal.isEnabled();
		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		Callable<Void> callable = () -> {
			_connectionCount.incrementAndGet();

			try {
				NotificationThreadLocal.setEnabled(notificationEnabled);
				WorkflowThreadLocal.setEnabled(workflowEnabled);

				_process(
					updateSQL,
					() -> {
						while (!producerFinished.get() || !queue.isEmpty()) {
							T t = queue.poll(1, TimeUnit.SECONDS);

							if (t != null) {
								return t;
							}
						}

						return null;
					},
					unsafeConsumer, unsafeBiConsumer, preparedStatementHashMap);
			}
			catch (Exception exception) {
				throwableCollector.collect(exception);
			}
			finally {
				closeConnections(Thread.currentThread());

				_connectionCount.decrementAndGet();
			}

			return null;
		};

		ExecutorService executorService =
			UpgradeExecutorServiceUtil.getDataExecutorService();

		try {
			for (int i = 0; i < size; i++) {
				futures.add(
					executorService.submit(
						new CompanyInheritableThreadLocalCallable<>(callable)));
			}

			try {
				T t = unsafeSupplier.get();

				while (t != null) {
					if (queue.offer(t, 1, TimeUnit.SECONDS)) {
						t = unsafeSupplier.get();
					}
					else if (throwableCollector.getThrowable() != null) {
						return;
					}
				}
			}
			finally {
				producerFinished.set(true);
			}
		}
		finally {
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
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(BaseDBProcess.class);

	private static final AtomicInteger _connectionCount = new AtomicInteger();

	private final Map<Connection, Boolean> _autoCommits =
		new ConcurrentHashMap<>();
	private final Map<Long, Map<Thread, Connection>> _connectionsMaps =
		new ConcurrentHashMap<>();

	private class BatchCommitInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			String methodName = method.getName();

			boolean addBatch = methodName.equals("addBatch");

			if (addBatch && !_transaction) {
				_startTransaction();
			}

			Object result;

			try {
				result = method.invoke(_preparedStatement, args);
			}
			catch (InvocationTargetException invocationTargetException) {
				Throwable causeThrowable = invocationTargetException.getCause();

				if (_transaction) {
					try {
						_finishTransaction(false);
					}
					catch (SQLException sqlException) {
						causeThrowable.addSuppressed(sqlException);
					}
				}

				throw causeThrowable;
			}

			if (addBatch) {
				if (++_count >= PropsValues.HIBERNATE_JDBC_BATCH_SIZE) {
					_finishTransaction(true);
				}
			}
			else if (_transaction && methodName.equals("executeBatch")) {
				_finishTransaction(true);
			}

			return result;
		}

		private BatchCommitInvocationHandler(
			Connection connection, PreparedStatement preparedStatement) {

			_connection = connection;
			_preparedStatement = preparedStatement;
		}

		private void _finishTransaction(boolean commit) throws SQLException {
			_count = 0;
			_transaction = false;

			try {
				if (commit) {
					_connection.commit();
				}
				else {
					_connection.rollback();
				}
			}
			finally {
				Boolean autoCommit = _autoCommits.remove(_connection);

				try {
					_connection.setAutoCommit(
						GetterUtil.getBoolean(autoCommit, true));
				}
				catch (SQLException sqlException) {
					_log.error("Unable to set auto commit", sqlException);
				}
			}
		}

		private void _startTransaction() throws SQLException {
			boolean autoCommit = _connection.getAutoCommit();

			if (_autoCommits.putIfAbsent(_connection, autoCommit) != null) {
				return;
			}

			if (autoCommit) {
				try {
					_connection.setAutoCommit(false);
				}
				catch (SQLException sqlException) {
					_autoCommits.remove(_connection);

					throw sqlException;
				}
			}

			_transaction = true;
		}

		private final Connection _connection;
		private int _count;
		private final PreparedStatement _preparedStatement;
		private boolean _transaction;

	}

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
					PropsValues.DATABASE_PARTITION_ENABLED ?
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