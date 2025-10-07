/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.jdbc;

import com.liferay.petra.concurrent.NoticeableThreadPoolExecutor;
import com.liferay.petra.concurrent.ThreadPoolHandlerAdapter;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.concurrent.DefaultNoticeableFuture;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.SwappableSecurityManager;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 * @author Preston Crary
 */
@NewEnv(type = NewEnv.Type.CLASSLOADER)
public class AutoBatchPreparedStatementUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Before
	public void setUp() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			PortalExecutorManager.class,
			(PortalExecutorManager)ProxyUtil.newProxyInstance(
				AutoBatchPreparedStatementUtilTest.class.getClassLoader(),
				new Class<?>[] {PortalExecutorManager.class},
				new PortalExecutorManagerInvocationHandler()),
			null);
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testCINITFailure() throws ClassNotFoundException {
		PropsUtil.set(PropsKeys.HIBERNATE_JDBC_BATCH_SIZE, "0");

		final NoSuchMethodException noSuchMethodException =
			new NoSuchMethodException();
		final AtomicInteger counter = new AtomicInteger();

		try (SwappableSecurityManager swappableSecurityManager =
				new SwappableSecurityManager() {

					@Override
					public void checkPackageAccess(String pkg) {
						if (pkg.equals("java.sql") &&
							(counter.getAndIncrement() == 1)) {

							ReflectionUtil.throwException(
								noSuchMethodException);
						}
					}

				}) {

			swappableSecurityManager.install();

			Class.forName(AutoBatchPreparedStatementUtil.class.getName());
		}
		catch (ExceptionInInitializerError eiie) {
			Assert.assertSame(noSuchMethodException, eiie.getCause());
		}
	}

	@Test
	public void testConcurrentCancellationException() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				PortalExecutorManager.class,
				(PortalExecutorManager)ProxyUtil.newProxyInstance(
					AutoBatchPreparedStatementUtilTest.class.getClassLoader(),
					new Class<?>[] {PortalExecutorManager.class},
					(proxy, method, args) -> {
						if (Objects.equals(
								method.getName(), "getPortalExecutor")) {

							return new NoticeableThreadPoolExecutor(
								1, 1, 60, TimeUnit.SECONDS,
								new LinkedBlockingQueue<>(1),
								Executors.defaultThreadFactory(),
								new ThreadPoolExecutor.AbortPolicy(),
								new ThreadPoolHandlerAdapter()) {

								@Override
								public void execute(Runnable runnable) {
									Future<?> future = (Future<?>)runnable;

									future.cancel(true);
								}

							};
						}

						return null;
					}),
				MapUtil.singletonDictionary(
					"service.ranking", Integer.MAX_VALUE));

		try {
			PropsUtil.set(PropsKeys.HIBERNATE_JDBC_BATCH_SIZE, "0");

			doTestConcurrentCancellationException(true);
			doTestConcurrentCancellationException(false);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testConcurrentExecutionException() {
		PropsUtil.set(PropsKeys.HIBERNATE_JDBC_BATCH_SIZE, "0");

		doTestConcurrentExecutionExceptions(true);
		doTestConcurrentExecutionExceptions(false);
	}

	@Test
	public void testConcurrentWaitingForFutures() throws SQLException {
		PropsUtil.set(PropsKeys.HIBERNATE_JDBC_BATCH_SIZE, "0");

		doTestConcurrentWaitingForFutures(true);
		doTestConcurrentWaitingForFutures(false);
	}

	@Test
	public void testConstructor() throws ReflectiveOperationException {
		PropsUtil.set(PropsKeys.HIBERNATE_JDBC_BATCH_SIZE, "0");

		Constructor<AutoBatchPreparedStatementUtil> constructor =
			AutoBatchPreparedStatementUtil.class.getDeclaredConstructor();

		Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));

		constructor.setAccessible(true);

		constructor.newInstance();
	}

	@Test
	public void testNotSupportBatchUpdates() throws Exception {
		PropsUtil.set(PropsKeys.HIBERNATE_JDBC_BATCH_SIZE, "0");

		doTestNotSupportBatchUpdates();
		doTestNotSupportBatchUpdatesConcurrent();
	}

	@Test
	public void testSupportBatchUpdates() throws Exception {
		PropsUtil.set(PropsKeys.HIBERNATE_JDBC_BATCH_SIZE, "2");

		doTestSupportBaseUpdates();
		doTestSupportBaseUpdatesConcurrent();
	}

	protected void doTestConcurrentCancellationException(
		boolean supportBatchUpdates) {

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					(Connection)ProxyUtil.newProxyInstance(
						ClassLoader.getSystemClassLoader(),
						new Class<?>[] {Connection.class},
						new ConnectionInvocationHandler(
							new PreparedStatementInvocationHandler(
								supportBatchUpdates))),
					StringPool.BLANK)) {

			preparedStatement.addBatch();

			preparedStatement.executeBatch();

			preparedStatement.addBatch();

			preparedStatement.executeBatch();
		}
		catch (Throwable throwable) {
			Assert.assertSame(
				CancellationException.class, throwable.getClass());

			Throwable[] throwables = throwable.getSuppressed();

			Assert.assertEquals(
				Arrays.toString(throwables), 1, throwables.length);

			Throwable firstThrowable = throwables[0];

			Assert.assertSame(
				CancellationException.class, firstThrowable.getClass());

			return;
		}

		throw new IllegalStateException("Should have returned from catch");
	}

	protected void doTestConcurrentExecutionExceptions(
		boolean supportBatchUpdates) {

		PreparedStatementInvocationHandler preparedStatementInvocationHandler =
			new PreparedStatementInvocationHandler(supportBatchUpdates);

		Set<Throwable> throwables = Collections.newSetFromMap(
			new IdentityHashMap<>());

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					(Connection)ProxyUtil.newProxyInstance(
						ClassLoader.getSystemClassLoader(),
						new Class<?>[] {Connection.class},
						new ConnectionInvocationHandler(
							preparedStatementInvocationHandler)),
					StringPool.BLANK)) {

			RuntimeException runtimeException1 = new RuntimeException();

			throwables.add(runtimeException1);

			preparedStatementInvocationHandler.setRuntimeException(
				runtimeException1);

			preparedStatement.addBatch();

			preparedStatement.executeBatch();

			RuntimeException runtimeException2 = new RuntimeException();

			throwables.add(runtimeException2);

			preparedStatementInvocationHandler.setRuntimeException(
				runtimeException2);

			preparedStatement.addBatch();

			preparedStatement.executeBatch();
		}
		catch (Throwable throwable) {
			Assert.assertTrue(
				throwables.toString(), throwables.contains(throwable));

			Throwable[] suppressedThrowables = throwable.getSuppressed();

			Assert.assertEquals(
				Arrays.toString(suppressedThrowables), 1,
				suppressedThrowables.length);
			Assert.assertTrue(
				throwables.toString(),
				throwables.contains(suppressedThrowables[0]));

			return;
		}

		throw new IllegalStateException("Should have returned from catch");
	}

	protected void doTestConcurrentWaitingForFutures(
			boolean supportBatchUpdates)
		throws SQLException {

		TestNoticeableFuture<Void> testNoticeableFuture =
			new TestNoticeableFuture<>();

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					(Connection)ProxyUtil.newProxyInstance(
						ClassLoader.getSystemClassLoader(),
						new Class<?>[] {Connection.class},
						new ConnectionInvocationHandler(
							new PreparedStatementInvocationHandler(
								supportBatchUpdates))),
					StringPool.BLANK)) {

			Set<Future<Void>> futures = ReflectionTestUtil.getFieldValue(
				ProxyUtil.getInvocationHandler(preparedStatement), "_futures");

			futures.add(testNoticeableFuture);
		}

		Assert.assertTrue(testNoticeableFuture.hasCalledGet());
	}

	protected void doTestNotSupportBatchUpdates() throws Exception {
		PreparedStatementInvocationHandler preparedStatementInvocationHandler =
			new PreparedStatementInvocationHandler(false);

		List<Method> methods = preparedStatementInvocationHandler.getMethods();

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					(Connection)ProxyUtil.newProxyInstance(
						ClassLoader.getSystemClassLoader(),
						new Class<?>[] {Connection.class},
						new ConnectionInvocationHandler(
							preparedStatementInvocationHandler)),
					"")) {

			Assert.assertTrue(methods.toString(), methods.isEmpty());

			// Calling addBatch fallbacks to executeUpdate

			preparedStatement.addBatch();

			Assert.assertEquals(methods.toString(), 1, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("executeUpdate"),
				methods.remove(0));

			// Calling executeBatch does nothing

			Assert.assertArrayEquals(
				new int[0], preparedStatement.executeBatch());
			Assert.assertTrue(methods.toString(), methods.isEmpty());

			// Other methods like execute pass through

			preparedStatement.execute();

			Assert.assertEquals(methods.toString(), 1, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("execute"),
				methods.remove(0));
		}

		Assert.assertEquals(methods.toString(), 1, methods.size());
		Assert.assertEquals(
			PreparedStatement.class.getMethod("close"), methods.remove(0));

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					(Connection)ProxyUtil.newProxyInstance(
						ClassLoader.getSystemClassLoader(),
						new Class<?>[] {Connection.class},
						new ConnectionInvocationHandler(
							preparedStatementInvocationHandler)),
					"")) {
		}

		Assert.assertTrue(methods.toString(), methods.isEmpty());
	}

	protected void doTestNotSupportBatchUpdatesConcurrent() throws Exception {
		PreparedStatementInvocationHandler preparedStatementInvocationHandler =
			new PreparedStatementInvocationHandler(false);

		List<Method> methods = preparedStatementInvocationHandler.getMethods();

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					(Connection)ProxyUtil.newProxyInstance(
						ClassLoader.getSystemClassLoader(),
						new Class<?>[] {Connection.class},
						new ConnectionInvocationHandler(
							preparedStatementInvocationHandler)),
					StringPool.BLANK)) {

			Assert.assertTrue(methods.toString(), methods.isEmpty());

			// Calling addBatch fallbacks to executeUpdate

			preparedStatement.addBatch();

			Assert.assertEquals(methods.toString(), 2, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("executeUpdate"),
				methods.remove(0));
			Assert.assertEquals(
				PreparedStatement.class.getMethod("close"), methods.remove(0));

			// Calling executeBatch does nothing

			Assert.assertArrayEquals(
				new int[0], preparedStatement.executeBatch());
			Assert.assertTrue(methods.toString(), methods.isEmpty());

			// Other methods like execute pass through

			preparedStatement.execute();

			Assert.assertEquals(methods.toString(), 1, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("execute"),
				methods.remove(0));
		}
	}

	protected void doTestSupportBaseUpdates() throws Exception {
		PreparedStatementInvocationHandler preparedStatementInvocationHandler =
			new PreparedStatementInvocationHandler(true);

		List<Method> methods = preparedStatementInvocationHandler.getMethods();

		Connection connection = (Connection)ProxyUtil.newProxyInstance(
			ClassLoader.getSystemClassLoader(),
			new Class<?>[] {Connection.class},
			new ConnectionInvocationHandler(
				preparedStatementInvocationHandler));

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(connection, "")) {

			Assert.assertSame(connection, preparedStatement.getConnection());

			InvocationHandler invocationHandler =
				ProxyUtil.getInvocationHandler(preparedStatement);

			Assert.assertEquals(
				Integer.valueOf(0),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));

			Assert.assertTrue(methods.toString(), methods.isEmpty());

			// Protection for executing empty batch

			Assert.assertArrayEquals(
				new int[0], preparedStatement.executeBatch());
			Assert.assertTrue(methods.toString(), methods.isEmpty());
			Assert.assertEquals(
				Integer.valueOf(0),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));

			// Calling addBatch passes through when within the Hibernate JDBC
			// batch size

			preparedStatement.addBatch();

			Assert.assertEquals(methods.toString(), 1, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("addBatch"),
				methods.remove(0));
			Assert.assertEquals(
				Integer.valueOf(1),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));

			// Calling addBatch passes through and triggers executeBatch when
			// exceeding the Hibernate JDBC batch size

			preparedStatement.addBatch();

			Assert.assertEquals(methods.toString(), 2, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("addBatch"),
				methods.remove(0));
			Assert.assertEquals(
				PreparedStatement.class.getMethod("executeBatch"),
				methods.remove(0));
			Assert.assertEquals(
				Integer.valueOf(0),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));

			// Calling addBatch passes through when within the Hibernate JDBC
			// batch size

			preparedStatement.addBatch();

			Assert.assertEquals(methods.toString(), 1, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("addBatch"),
				methods.remove(0));
			Assert.assertEquals(
				Integer.valueOf(1),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));

			// Calling executeBatch passes through when batch is not empty

			preparedStatement.executeBatch();

			Assert.assertEquals(methods.toString(), 1, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("executeBatch"),
				methods.remove(0));
			Assert.assertEquals(
				Integer.valueOf(0),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));

			// Other methods like execute pass through

			preparedStatement.execute();

			Assert.assertEquals(methods.toString(), 1, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("execute"),
				methods.remove(0));
			Assert.assertEquals(
				Integer.valueOf(0),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));
		}

		Assert.assertEquals(methods.toString(), 1, methods.size());
		Assert.assertEquals(
			PreparedStatement.class.getMethod("close"), methods.remove(0));
	}

	protected void doTestSupportBaseUpdatesConcurrent() throws Exception {
		PreparedStatementInvocationHandler preparedStatementInvocationHandler =
			new PreparedStatementInvocationHandler(true);

		List<Method> methods = preparedStatementInvocationHandler.getMethods();

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					(Connection)ProxyUtil.newProxyInstance(
						ClassLoader.getSystemClassLoader(),
						new Class<?>[] {Connection.class},
						new ConnectionInvocationHandler(
							preparedStatementInvocationHandler)),
					StringPool.BLANK)) {

			InvocationHandler invocationHandler =
				ProxyUtil.getInvocationHandler(preparedStatement);

			Assert.assertEquals(
				Integer.valueOf(0),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));

			Assert.assertTrue(methods.toString(), methods.isEmpty());

			// Protection for executing empty batch

			Assert.assertArrayEquals(
				new int[0], preparedStatement.executeBatch());
			Assert.assertTrue(methods.toString(), methods.isEmpty());
			Assert.assertEquals(
				Integer.valueOf(0),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));

			// Calling addBatch passes through when within the Hibernate JDBC
			// batch size

			preparedStatement.addBatch();

			Assert.assertEquals(methods.toString(), 1, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("addBatch"),
				methods.remove(0));
			Assert.assertEquals(
				Integer.valueOf(1),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));

			// Calling addBatch passes through and triggers executeBatch when
			// exceeding the Hibernate JDBC batch size

			preparedStatement.addBatch();

			Assert.assertEquals(methods.toString(), 3, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("addBatch"),
				methods.remove(0));
			Assert.assertEquals(
				PreparedStatement.class.getMethod("executeBatch"),
				methods.remove(0));
			Assert.assertEquals(
				Integer.valueOf(0),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));
			Assert.assertEquals(
				PreparedStatement.class.getMethod("close"), methods.remove(0));

			// Calling addBatch passes through when within the Hibernate JDBC
			// batch size

			preparedStatement.addBatch();

			Assert.assertEquals(methods.toString(), 1, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("addBatch"),
				methods.remove(0));
			Assert.assertEquals(
				Integer.valueOf(1),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));

			// Calling executeBatch passes through when batch is not empty

			preparedStatement.executeBatch();

			Assert.assertEquals(methods.toString(), 2, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("executeBatch"),
				methods.remove(0));
			Assert.assertEquals(
				Integer.valueOf(0),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));
			Assert.assertEquals(
				PreparedStatement.class.getMethod("close"), methods.remove(0));

			// Other methods like execute pass through

			preparedStatement.execute();

			Assert.assertEquals(methods.toString(), 1, methods.size());
			Assert.assertEquals(
				PreparedStatement.class.getMethod("execute"),
				methods.remove(0));
			Assert.assertEquals(
				Integer.valueOf(0),
				ReflectionTestUtil.getFieldValue(invocationHandler, "_count"));
		}
	}

	private ServiceRegistration<?> _serviceRegistration;

	private static class ConnectionInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws NoSuchMethodException {

			if (method.equals(Connection.class.getMethod("getMetaData"))) {
				return ProxyUtil.newProxyInstance(
					ClassLoader.getSystemClassLoader(),
					new Class<?>[] {DatabaseMetaData.class},
					new DatabaseMetaDataInvocationHandler(
						_preparedStatementInvocationHandler.
							_supportBatchUpdates));
			}

			if (method.equals(
					Connection.class.getMethod(
						"prepareStatement", String.class))) {

				return ProxyUtil.newProxyInstance(
					ClassLoader.getSystemClassLoader(),
					new Class<?>[] {PreparedStatement.class},
					_preparedStatementInvocationHandler);
			}

			throw new UnsupportedOperationException();
		}

		private ConnectionInvocationHandler(
			PreparedStatementInvocationHandler
				preparedStatementInvocationHandler) {

			_preparedStatementInvocationHandler =
				preparedStatementInvocationHandler;
		}

		private final PreparedStatementInvocationHandler
			_preparedStatementInvocationHandler;

	}

	private static class DatabaseMetaDataInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws NoSuchMethodException {

			if (method.equals(
					DatabaseMetaData.class.getMethod("supportsBatchUpdates"))) {

				return _supportBatchUpdates;
			}

			throw new UnsupportedOperationException();
		}

		private DatabaseMetaDataInvocationHandler(boolean supportBatchUpdates) {
			_supportBatchUpdates = supportBatchUpdates;
		}

		private final boolean _supportBatchUpdates;

	}

	private static class PreparedStatementInvocationHandler
		implements InvocationHandler {

		public List<Method> getMethods() {
			return _methods;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws NoSuchMethodException {

			_methods.add(method);

			if (method.equals(PreparedStatement.class.getMethod("addBatch")) ||
				method.equals(PreparedStatement.class.getMethod("close"))) {

				return null;
			}

			if (method.equals(PreparedStatement.class.getMethod("execute"))) {
				return false;
			}

			if (method.equals(
					PreparedStatement.class.getMethod("executeBatch"))) {

				if (_runtimeException != null) {
					throw _runtimeException;
				}

				return new int[0];
			}

			if (method.equals(
					PreparedStatement.class.getMethod("executeUpdate"))) {

				if (_runtimeException != null) {
					throw _runtimeException;
				}

				return 0;
			}

			throw new UnsupportedOperationException();
		}

		public void setRuntimeException(RuntimeException runtimeException) {
			_runtimeException = runtimeException;
		}

		private PreparedStatementInvocationHandler(
			boolean supportBatchUpdates) {

			_supportBatchUpdates = supportBatchUpdates;
		}

		private final List<Method> _methods = new ArrayList<>();
		private RuntimeException _runtimeException;
		private final boolean _supportBatchUpdates;

	}

	private static final class TestNoticeableFuture<T>
		extends DefaultNoticeableFuture<T> {

		@Override
		public T get() {
			_calledGet = true;

			return null;
		}

		public boolean hasCalledGet() {
			return _calledGet;
		}

		private boolean _calledGet;

	}

}