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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ThrowableCollector;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.upgrade.recorder.UpgradeSQLRecorder;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.NotificationThreadLocal;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
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
import java.util.concurrent.ConcurrentHashMap;
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

		if (hasColumnType(tableName, oldColumnName, newColumnType)) {
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
		else {
			throw new SQLException(
				StringBundler.concat(
					"Type change is not allowed when altering column name. ",
					"Column ", tableName, StringPool.PERIOD, oldColumnName,
					" has different type than ", newColumnType));
		}
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

		if (hasColumn(tableName, columnName)) {
			if (!hasColumnType(tableName, columnName, columnType)) {
				throw new SQLException(
					StringBundler.concat(
						"Column ", tableName, StringPool.PERIOD, columnName,
						" already exists with different type than ",
						columnType));
			}

			return;
		}

		DB db = DBManagerUtil.getDB();

		db.alterTableAddColumn(connection, tableName, columnName, columnType);
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

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #hasTable(String)}
	 */
	@Deprecated
	protected boolean doHasTable(String tableName) throws Exception {
		DBInspector dbInspector = new DBInspector(connection);

		return dbInspector.hasTable(tableName);
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

		int fetchSize = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.UPGRADE_CONCURRENT_FETCH_SIZE));

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setFetchSize(fetchSize);

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

		int fetchSize = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.UPGRADE_CONCURRENT_FETCH_SIZE));

		try (Statement statement = connection.createStatement()) {
			statement.setFetchSize(fetchSize);

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

		Runtime runtime = Runtime.getRuntime();

		ExecutorService executorService = Executors.newFixedThreadPool(
			runtime.availableProcessors());

		ThrowableCollector throwableCollector = new ThrowableCollector();

		List<Future<Void>> futures = new ArrayList<>();

		Map<Thread, PreparedStatement> preparedStatementHashMap =
			new ConcurrentHashMap<>();

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

				int futuresMaxSize = GetterUtil.getInteger(
					PropsUtil.get(
						PropsKeys.
							UPGRADE_CONCURRENT_PROCESS_FUTURE_LIST_MAX_SIZE));

				if (futures.size() >= futuresMaxSize) {
					for (Future<Void> curFuture : futures) {
						curFuture.get();
					}

					futures.clear();
				}

				futures.add(future);
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

	private static final Log _log = LogFactoryUtil.getLog(BaseDBProcess.class);

	private class ConnectionThreadProxyInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			String methodName = method.getName();

			if (methodName.equals("close")) {
				Collection<Connection> connections = _connectionMap.values();

				Iterator<Connection> iterator = connections.iterator();

				while (iterator.hasNext()) {
					Connection connection = iterator.next();

					iterator.remove();

					method.invoke(connection, args);
				}

				return null;
			}

			return method.invoke(
				_connectionMap.computeIfAbsent(
					Thread.currentThread(), thread -> _getConnection()),
				args);
		}

		private final Map<Thread, Connection> _connectionMap =
			new ConcurrentHashMap<>();

	}

}