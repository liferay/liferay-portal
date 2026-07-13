/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.transaction;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionLifecycleListener;
import com.liferay.portal.kernel.transaction.TransactionLifecycleManager;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * @author Shuyang Zhou
 */
public class DefaultTransactionExecutorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		TransactionLifecycleManager.register(
			_recordTransactionLifecycleListener);
	}

	@After
	public void tearDown() {
		TransactionLifecycleManager.unregister(
			_recordTransactionLifecycleListener);
	}

	@Test
	public void testCommit() throws Throwable {
		RecordPlatformTransactionManager recordPlatformTransactionManager =
			new RecordPlatformTransactionManager();

		TransactionExecutor transactionExecutor = createTransactionExecutor(
			recordPlatformTransactionManager);

		TransactionAttributeAdapter transactionAttributeAdapter =
			_newTransactionAttributeAdapter(throwable -> false);

		transactionExecutor.execute(transactionAttributeAdapter, () -> null);

		recordPlatformTransactionManager.verify(
			transactionAttributeAdapter,
			RecordPlatformTransactionManager.TRANSACTION_STATUS, null);

		_recordTransactionLifecycleListener.verify(null);
	}

	@Test
	public void testCommitWithAppException() throws Throwable {
		RecordPlatformTransactionManager recordPlatformTransactionManager =
			new RecordPlatformTransactionManager();

		TransactionExecutor transactionExecutor = createTransactionExecutor(
			recordPlatformTransactionManager);

		TransactionAttributeAdapter transactionAttributeAdapter =
			_newTransactionAttributeAdapter(throwable -> false);

		try {
			transactionExecutor.execute(
				transactionAttributeAdapter,
				() -> {
					throw appException;
				});

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertSame(appException, throwable);
		}

		recordPlatformTransactionManager.verify(
			transactionAttributeAdapter,
			RecordPlatformTransactionManager.TRANSACTION_STATUS, null);

		_recordTransactionLifecycleListener.verify(null);
	}

	@Test
	public void testCommitWithAppExceptionWithCommitException()
		throws Throwable {

		RecordPlatformTransactionManager recordPlatformTransactionManager =
			new RecordPlatformTransactionManager() {

				@Override
				public void commit(TransactionStatus transactionStatus) {
					ReflectionUtil.throwException(commitException);
				}

			};

		TransactionExecutor transactionExecutor = createTransactionExecutor(
			recordPlatformTransactionManager);

		TransactionAttributeAdapter transactionAttributeAdapter =
			_newTransactionAttributeAdapter(throwable -> false);

		try {
			transactionExecutor.execute(
				transactionAttributeAdapter,
				() -> {
					throw appException;
				});

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertSame(commitException, throwable);

			Throwable[] throwables = commitException.getSuppressed();

			Assert.assertEquals(
				Arrays.toString(throwables), 1, throwables.length);
			Assert.assertEquals(appException, throwables[0]);
		}

		recordPlatformTransactionManager.verify(
			transactionAttributeAdapter, null, null);

		_recordTransactionLifecycleListener.verify(commitException);
	}

	@Test
	public void testCommitWithCommitException() throws Throwable {
		RecordPlatformTransactionManager recordPlatformTransactionManager =
			new RecordPlatformTransactionManager() {

				@Override
				public void commit(TransactionStatus transactionStatus) {
					ReflectionUtil.throwException(commitException);
				}

			};

		TransactionExecutor transactionExecutor = createTransactionExecutor(
			recordPlatformTransactionManager);

		TransactionAttributeAdapter transactionAttributeAdapter =
			_newTransactionAttributeAdapter(throwable -> false);

		try {
			transactionExecutor.execute(
				transactionAttributeAdapter, () -> null);

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertSame(commitException, throwable);
		}

		recordPlatformTransactionManager.verify(
			transactionAttributeAdapter, null, null);

		_recordTransactionLifecycleListener.verify(commitException);
	}

	@Test
	public void testFailingTransactionLifecycleListeners() {
		FailingTransactionLifecycleListener
			failingTransactionLifecycleListener =
				new FailingTransactionLifecycleListener();

		TransactionLifecycleManager.register(
			failingTransactionLifecycleListener);

		try {
			TransactionExecutor transactionExecutor = createTransactionExecutor(
				new RecordPlatformTransactionManager());

			try {
				transactionExecutor.execute(
					new TestTransactionAttributeAdapter(false), () -> null);

				Assert.fail();
			}
			catch (Throwable throwable) {
				Assert.assertEquals("createThrowable", throwable.getMessage());

				Throwable[] throwables = throwable.getSuppressed();

				Assert.assertEquals(
					Arrays.toString(throwables), 1, throwables.length);
				Assert.assertEquals(
					"commitThrowable", throwables[0].getMessage());
			}

			Exception exception1 = new Exception();

			try {
				transactionExecutor.execute(
					new TestTransactionAttributeAdapter(false),
					() -> {
						throw exception1;
					});

				Assert.fail();
			}
			catch (Throwable throwable) {
				Assert.assertSame(exception1, throwable);

				Throwable[] throwables = throwable.getSuppressed();

				Assert.assertEquals(
					Arrays.toString(throwables), 1, throwables.length);
				Assert.assertSame(
					"createThrowable", throwables[0].getMessage());

				throwables = throwables[0].getSuppressed();

				Assert.assertEquals(
					Arrays.toString(throwables), 1, throwables.length);
				Assert.assertSame(
					"commitThrowable", throwables[0].getMessage());
			}

			try {
				transactionExecutor.execute(
					new TestTransactionAttributeAdapter(true), () -> null);

				Assert.fail();
			}
			catch (Throwable throwable) {
				Assert.assertEquals("createThrowable", throwable.getMessage());

				Throwable[] throwables = throwable.getSuppressed();

				Assert.assertEquals(
					Arrays.toString(throwables), 1, throwables.length);
				Assert.assertEquals(
					"commitThrowable", throwables[0].getMessage());
			}

			Exception exception2 = new Exception();

			try {
				transactionExecutor.execute(
					new TestTransactionAttributeAdapter(true),
					() -> {
						throw exception2;
					});

				Assert.fail();
			}
			catch (Throwable throwable) {
				Assert.assertSame(exception2, throwable);

				Throwable[] throwables = throwable.getSuppressed();

				Assert.assertEquals(
					Arrays.toString(throwables), 1, throwables.length);
				Assert.assertSame(
					"createThrowable", throwables[0].getMessage());

				throwables = throwables[0].getSuppressed();

				Assert.assertEquals(
					Arrays.toString(throwables), 1, throwables.length);
				Assert.assertSame(
					"rollbackThrowable", throwables[0].getMessage());
			}
		}
		finally {
			TransactionLifecycleManager.unregister(
				failingTransactionLifecycleListener);
		}
	}

	@Test
	public void testGetPlatformTransactionManager() {
		RecordPlatformTransactionManager recordPlatformTransactionManager =
			new RecordPlatformTransactionManager();

		TransactionExecutor transactionExecutor = createTransactionExecutor(
			recordPlatformTransactionManager);

		Assert.assertSame(
			recordPlatformTransactionManager,
			transactionExecutor.getPlatformTransactionManager());
	}

	@Test
	public void testRollbackOnAppException() throws Throwable {
		RecordPlatformTransactionManager recordPlatformTransactionManager =
			new RecordPlatformTransactionManager();

		TransactionExecutor transactionExecutor = createTransactionExecutor(
			recordPlatformTransactionManager);

		TransactionAttributeAdapter transactionAttributeAdapter =
			_newTransactionAttributeAdapter(
				throwable -> throwable == appException);

		try {
			transactionExecutor.execute(
				transactionAttributeAdapter,
				() -> {
					throw appException;
				});

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertSame(appException, throwable);
		}

		recordPlatformTransactionManager.verify(
			transactionAttributeAdapter, null,
			RecordPlatformTransactionManager.TRANSACTION_STATUS);

		_recordTransactionLifecycleListener.verify(appException);
	}

	@Test
	public void testRollbackOnAppExceptionWithRollbackException()
		throws Throwable {

		RecordPlatformTransactionManager recordPlatformTransactionManager =
			new RecordPlatformTransactionManager() {

				@Override
				public void rollback(TransactionStatus transactionStatus) {
					ReflectionUtil.throwException(rollbackException);
				}

			};

		TransactionExecutor transactionExecutor = createTransactionExecutor(
			recordPlatformTransactionManager);

		TransactionAttributeAdapter transactionAttributeAdapter =
			_newTransactionAttributeAdapter(
				throwable -> throwable == appException);

		try {
			transactionExecutor.execute(
				transactionAttributeAdapter,
				() -> {
					throw appException;
				});

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertSame(rollbackException, throwable);

			Throwable[] throwables = rollbackException.getSuppressed();

			Assert.assertEquals(
				Arrays.toString(throwables), 1, throwables.length);
			Assert.assertEquals(appException, throwables[0]);
		}

		recordPlatformTransactionManager.verify(
			transactionAttributeAdapter, null, null);

		_recordTransactionLifecycleListener.verify(appException);
	}

	@Test
	public void testTransactionExecutorMethods() throws Throwable {
		RecordPlatformTransactionManager recordPlatformTransactionManager =
			new RecordPlatformTransactionManager();

		TransactionExecutor transactionExecutor = createTransactionExecutor(
			recordPlatformTransactionManager);

		TransactionAttributeAdapter transactionAttributeAdapter =
			_newTransactionAttributeAdapter(
				throwable -> throwable == appException);

		assertTransactionExecutorThreadLocal(transactionExecutor, false);

		TransactionStatusAdapter transactionStatusAdapter =
			transactionExecutor.start(transactionAttributeAdapter);

		assertTransactionExecutorThreadLocal(transactionExecutor, true);

		recordPlatformTransactionManager.verify(
			transactionAttributeAdapter, null, null);

		try {
			transactionExecutor.rollback(
				appException, transactionAttributeAdapter,
				transactionStatusAdapter);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertSame(appException, exception);
		}

		assertTransactionExecutorThreadLocal(transactionExecutor, false);

		recordPlatformTransactionManager.verify(
			transactionAttributeAdapter, null,
			RecordPlatformTransactionManager.TRANSACTION_STATUS);

		recordPlatformTransactionManager.setRollbackTransactionStatus(null);

		transactionStatusAdapter = transactionExecutor.start(
			transactionAttributeAdapter);

		assertTransactionExecutorThreadLocal(transactionExecutor, true);

		transactionExecutor.commit(
			transactionAttributeAdapter, transactionStatusAdapter);

		assertTransactionExecutorThreadLocal(transactionExecutor, false);

		recordPlatformTransactionManager.verify(
			transactionAttributeAdapter,
			RecordPlatformTransactionManager.TRANSACTION_STATUS, null);
	}

	protected void assertTransactionExecutorThreadLocal(
		TransactionExecutor transactionExecutor, boolean inTransaction) {

		if (inTransaction) {
			Assert.assertSame(
				transactionExecutor,
				TransactionExecutorThreadLocal.getCurrentTransactionExecutor());
		}
		else {
			Assert.assertNull(
				TransactionExecutorThreadLocal.getCurrentTransactionExecutor());
		}
	}

	protected TransactionExecutor createTransactionExecutor(
		PlatformTransactionManager platformTransactionManager) {

		return new DefaultTransactionExecutor(platformTransactionManager);
	}

	protected final Exception appException = new Exception();
	protected final Exception commitException = new Exception();
	protected final Exception rollbackException = new Exception();

	private TransactionAttributeAdapter _newTransactionAttributeAdapter(
		Predicate<Throwable> predicate) {

		return new TransactionAttributeAdapter(
			(TransactionAttribute)ProxyUtil.newProxyInstance(
				TransactionAttribute.class.getClassLoader(),
				new Class<?>[] {TransactionAttribute.class},
				new InvocationHandler() {

					@Override
					public Object invoke(
						Object proxy, Method method, Object[] args) {

						if (Objects.equals(method.getName(), "rollbackOn")) {
							return predicate.test((Throwable)args[0]);
						}

						if (Objects.equals(
								method.getName(), "getPropagationBehavior")) {

							return TransactionDefinition.PROPAGATION_REQUIRED;
						}

						throw new UnsupportedOperationException(
							method.toString());
					}

				}));
	}

	private final RecordTransactionLifecycleListener
		_recordTransactionLifecycleListener =
			new RecordTransactionLifecycleListener();

	private static class FailingTransactionLifecycleListener
		implements TransactionLifecycleListener {

		@Override
		public void committed(
			com.liferay.portal.kernel.transaction.TransactionAttribute
				transactionAttribute,
			com.liferay.portal.kernel.transaction.TransactionStatus
				transactionStatus) {

			throw new RuntimeException("commitThrowable");
		}

		@Override
		public void created(
			com.liferay.portal.kernel.transaction.TransactionAttribute
				transactionAttribute,
			com.liferay.portal.kernel.transaction.TransactionStatus
				transactionStatus) {

			throw new RuntimeException("createThrowable");
		}

		@Override
		public void rollbacked(
			com.liferay.portal.kernel.transaction.TransactionAttribute
				transactionAttribute,
			com.liferay.portal.kernel.transaction.TransactionStatus
				transactionStatus,
			Throwable throwable) {

			throw new RuntimeException("rollbackThrowable");
		}

	}

	private static class RecordTransactionLifecycleListener
		implements TransactionLifecycleListener {

		@Override
		public void committed(
			com.liferay.portal.kernel.transaction.TransactionAttribute
				transactionAttribute,
			com.liferay.portal.kernel.transaction.TransactionStatus
				transactionStatus) {

			_committed = true;
		}

		@Override
		public void created(
			com.liferay.portal.kernel.transaction.TransactionAttribute
				transactionAttribute,
			com.liferay.portal.kernel.transaction.TransactionStatus
				transactionStatus) {

			_created = true;
		}

		@Override
		public void rollbacked(
			com.liferay.portal.kernel.transaction.TransactionAttribute
				transactionAttribute,
			com.liferay.portal.kernel.transaction.TransactionStatus
				transactionStatus,
			Throwable throwable) {

			_throwable = throwable;
		}

		public void verify(Throwable throwable) {
			Assert.assertTrue(_created);

			if (throwable == null) {
				Assert.assertTrue(_committed);
			}
			else {
				Assert.assertFalse(_committed);
				Assert.assertSame(throwable, _throwable);
			}
		}

		private boolean _committed;
		private boolean _created;
		private Throwable _throwable;

	}

	private static class TestTransactionAttributeAdapter
		extends TransactionAttributeAdapter {

		@Override
		public Propagation getPropagation() {
			return Propagation.REQUIRED;
		}

		@Override
		public boolean rollbackOn(Throwable throwable) {
			return _rollback;
		}

		private TestTransactionAttributeAdapter(boolean rollback) {
			super(null);

			_rollback = rollback;
		}

		private final boolean _rollback;

	}

}