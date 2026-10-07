/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.jdbc;

import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrimitiveIntList;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Shuyang Zhou
 * @author Preston Crary
 */
public class AutoBatchPreparedStatementUtil {

	public static PreparedStatement autoBatch(Connection connection, String sql)
		throws SQLException {

		return _autoBatch(connection, sql, false);
	}

	public static PreparedStatement autoBatchWithResults(
			Connection connection, String sql)
		throws SQLException {

		return _autoBatch(connection, sql, true);
	}

	public static PreparedStatement concurrentAutoBatch(
			Connection connection, String sql)
		throws SQLException {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		if (databaseMetaData.supportsBatchUpdates()) {
			return (PreparedStatement)ProxyUtil.newProxyInstance(
				ClassLoader.getSystemClassLoader(), _INTERFACES,
				new ConcurrentBatchInvocationHandler(connection, sql));
		}

		return (PreparedStatement)ProxyUtil.newProxyInstance(
			ClassLoader.getSystemClassLoader(), _INTERFACES,
			new ConcurrentNoBatchInvocationHandler(connection, sql));
	}

	private static PreparedStatement _autoBatch(
			Connection connection, String sql, boolean withResults)
		throws SQLException {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		if (databaseMetaData.supportsBatchUpdates()) {
			return (PreparedStatement)ProxyUtil.newProxyInstance(
				ClassLoader.getSystemClassLoader(), _INTERFACES,
				new BatchInvocationHandler(connection, sql, withResults));
		}

		return (PreparedStatement)ProxyUtil.newProxyInstance(
			ClassLoader.getSystemClassLoader(), _INTERFACES,
			new NoBatchInvocationHandler(connection, sql, withResults));
	}

	private static final int _HIBERNATE_JDBC_BATCH_SIZE = GetterUtil.getInteger(
		PropsUtil.get(PropsKeys.HIBERNATE_JDBC_BATCH_SIZE));

	private static final Class<?>[] _INTERFACES = new Class<?>[] {
		PreparedStatement.class
	};

	private static final Method _addBatchMethod;
	private static final Method _closeMethod;
	private static final Method _executeBatchMethod;
	private static final Method _getConnectionMethod;
	private static final Snapshot<PortalExecutorManager>
		_portalExecutorManagerSnapshot = new Snapshot<>(
			AutoBatchPreparedStatementUtil.class, PortalExecutorManager.class);

	static {
		try {
			_addBatchMethod = PreparedStatement.class.getMethod("addBatch");
			_closeMethod = PreparedStatement.class.getMethod("close");
			_executeBatchMethod = PreparedStatement.class.getMethod(
				"executeBatch");
			_getConnectionMethod = PreparedStatement.class.getMethod(
				"getConnection");
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new ExceptionInInitializerError(noSuchMethodException);
		}
	}

	private static class BatchInvocationHandler
		extends NoBatchInvocationHandler {

		@Override
		protected void doAddBatch() throws SQLException {
			PreparedStatement localPreparedStatement = getPreparedStatement();

			localPreparedStatement.addBatch();

			if (++_count >= _HIBERNATE_JDBC_BATCH_SIZE) {
				_count = 0;

				addRowCounts(localPreparedStatement.executeBatch());
			}
		}

		@Override
		protected int[] doExecuteBatch() throws SQLException {
			if (_count > 0) {
				_count = 0;

				PreparedStatement localPreparedStatement =
					getPreparedStatement();

				addRowCounts(localPreparedStatement.executeBatch());
			}

			return super.doExecuteBatch();
		}

		private BatchInvocationHandler(
			Connection connection, String sql, boolean withResults) {

			super(connection, sql, withResults);
		}

		private int _count;

	}

	private static class ConcurrentBatchInvocationHandler
		extends ConcurrentNoBatchInvocationHandler {

		@Override
		protected void doAddBatch() throws SQLException {
			PreparedStatement localPreparedStatement = getPreparedStatement();

			localPreparedStatement.addBatch();

			if (++_count >= _HIBERNATE_JDBC_BATCH_SIZE) {
				_count = 0;

				executeAsync(PreparedStatement::executeBatch);
			}
		}

		@Override
		protected int[] doExecuteBatch() throws SQLException {
			if (_count > 0) {
				_count = 0;

				executeAsync(PreparedStatement::executeBatch);
			}

			return null;
		}

		private ConcurrentBatchInvocationHandler(
			Connection connection, String sql) {

			super(connection, sql);
		}

		private int _count;

	}

	private static class ConcurrentNoBatchInvocationHandler
		extends PreparedStatementInvocationHandler {

		@Override
		protected void doAddBatch() throws SQLException {
			executeAsync(PreparedStatement::executeUpdate);
		}

		@Override
		protected void doClose() throws Throwable {
			Throwable throwable1 = null;

			for (Future<Void> future : _futures) {
				try {
					future.get();
				}
				catch (Throwable throwable2) {
					if (throwable2 instanceof ExecutionException) {
						throwable2 = throwable2.getCause();
					}

					if (throwable1 == null) {
						throwable1 = throwable2;
					}
					else {
						throwable1.addSuppressed(throwable2);
					}
				}
			}

			if (throwable1 != null) {
				throw throwable1;
			}
		}

		@Override
		protected int[] doExecuteBatch() throws SQLException {
			return null;
		}

		protected void executeAsync(
				UnsafeConsumer<PreparedStatement, SQLException>
					actionUnsafeConsumer)
			throws SQLException {

			NoticeableExecutorService noticeableExecutorService =
				_portalExecutorManager.getPortalExecutor(
					ConcurrentNoBatchInvocationHandler.class.getName());

			PreparedStatement localPreparedStatement = getPreparedStatement();

			NoticeableFuture<Void> noticeableFuture =
				noticeableExecutorService.submit(
					() -> {
						try {
							actionUnsafeConsumer.accept(localPreparedStatement);
						}
						finally {
							localPreparedStatement.close();
						}

						return null;
					});

			_futures.add(noticeableFuture);

			noticeableFuture.addFutureListener(
				future -> {
					try {
						future.get();

						_futures.remove(future);
					}
					catch (Throwable throwable) {
					}
				});

			preparedStatement = null;
		}

		private ConcurrentNoBatchInvocationHandler(
			Connection connection, String sql) {

			super(connection, sql);
		}

		private final Set<Future<Void>> _futures = Collections.newSetFromMap(
			new ConcurrentHashMap<>());
		private final PortalExecutorManager _portalExecutorManager =
			_portalExecutorManagerSnapshot.get();

	}

	private static class NoBatchInvocationHandler
		extends PreparedStatementInvocationHandler {

		protected void addRowCounts(int... rowCounts) {
			if (_primitiveIntList != null) {
				_primitiveIntList.addAll(rowCounts);
			}
		}

		@Override
		protected void doAddBatch() throws SQLException {
			PreparedStatement localPreparedStatement = getPreparedStatement();

			addRowCounts(localPreparedStatement.executeUpdate());
		}

		@Override
		protected void doClose() throws Throwable {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}

		@Override
		protected int[] doExecuteBatch() throws SQLException {
			if (_primitiveIntList == null) {
				return null;
			}

			int[] rowCounts = _primitiveIntList.getArray();

			_primitiveIntList = new PrimitiveIntList();

			return rowCounts;
		}

		private NoBatchInvocationHandler(
			Connection connection, String sql, boolean withResults) {

			super(connection, sql);

			if (withResults) {
				_primitiveIntList = new PrimitiveIntList();
			}
		}

		private PrimitiveIntList _primitiveIntList;

	}

	private abstract static class PreparedStatementInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			if (method.equals(_getConnectionMethod)) {
				return _connection;
			}

			if (method.equals(_closeMethod)) {
				doClose();

				return null;
			}

			if (method.equals(_addBatchMethod)) {
				doAddBatch();

				return null;
			}

			if (method.equals(_executeBatchMethod)) {
				return doExecuteBatch();
			}

			return method.invoke(getPreparedStatement(), args);
		}

		protected PreparedStatementInvocationHandler(
			Connection connection, String sql) {

			_connection = connection;
			_sql = sql;
		}

		protected abstract void doAddBatch() throws SQLException;

		protected abstract void doClose() throws Throwable;

		protected abstract int[] doExecuteBatch() throws SQLException;

		protected PreparedStatement getPreparedStatement() throws SQLException {
			if (preparedStatement == null) {
				preparedStatement = _connection.prepareStatement(_sql);
			}

			return preparedStatement;
		}

		protected PreparedStatement preparedStatement;

		private final Connection _connection;
		private final String _sql;

	}

}