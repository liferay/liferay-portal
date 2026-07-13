/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.transaction;

import com.liferay.portal.kernel.internal.spring.transaction.ReadOnlyTransactionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionLifecycleManager;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * @author Preston Crary
 */
public class ReadOnlyTransactionThreadLocalTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new CodeCoverageAssertor() {

				@Override
				public void appendAssertClasses(List<Class<?>> assertClasses) {
					assertClasses.add(ReadOnlyTransactionThreadLocal.class);

					assertClasses.add(
						ReadOnlyTransactionThreadLocal.
							TRANSACTION_LIFECYCLE_LISTENER.getClass());
				}

			},
			LiferayUnitTestRule.INSTANCE);

	@Before
	public void setUp() {
		TransactionLifecycleManager.register(
			ReadOnlyTransactionThreadLocal.TRANSACTION_LIFECYCLE_LISTENER);

		TransactionStatus transactionStatus =
			(TransactionStatus)ProxyUtil.newProxyInstance(
				TransactionStatus.class.getClassLoader(),
				new Class<?>[] {TransactionStatus.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "isNewTransaction")) {
						return true;
					}

					throw new UnsupportedOperationException(method.toString());
				});

		_transactionExecutor = new DefaultTransactionExecutor(
			new RecordPlatformTransactionManager() {

				@Override
				public TransactionStatus getTransaction(
					TransactionDefinition transactionDefinition) {

					super.getTransaction(transactionDefinition);

					return transactionStatus;
				}

			});
	}

	@After
	public void tearDown() {
		TransactionLifecycleManager.unregister(
			ReadOnlyTransactionThreadLocal.TRANSACTION_LIFECYCLE_LISTENER);
	}

	@Test
	public void testConstructor() {
		new ReadOnlyTransactionThreadLocal();
	}

	@Test
	public void testNestedReadOnly() throws Throwable {
		boolean readOnly = _transactionExecutor.execute(
			new TestTransactionAttributeAdapter(false),
			() -> _transactionExecutor.execute(
				new TestTransactionAttributeAdapter(true),
				ReadOnlyTransactionThreadLocal::isReadOnly));

		Assert.assertTrue(readOnly);
	}

	@Test
	public void testNestedReadWrite() throws Throwable {
		boolean readOnly = _transactionExecutor.execute(
			new TestTransactionAttributeAdapter(true),
			() -> _transactionExecutor.execute(
				new TestTransactionAttributeAdapter(false),
				ReadOnlyTransactionThreadLocal::isReadOnly));

		Assert.assertFalse(readOnly);
	}

	@Test
	public void testReadOnly() throws Throwable {
		boolean readOnly = _transactionExecutor.execute(
			new TestTransactionAttributeAdapter(true),
			ReadOnlyTransactionThreadLocal::isReadOnly);

		Assert.assertTrue(readOnly);
	}

	@Test
	public void testReadOnlyWithRollback() {
		Exception exception = new Exception();

		try {
			_transactionExecutor.execute(
				new TestTransactionAttributeAdapter(true),
				() -> {
					Assert.assertTrue(
						ReadOnlyTransactionThreadLocal.isReadOnly());

					throw exception;
				});

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertSame(exception, throwable);
		}

		Assert.assertFalse(ReadOnlyTransactionThreadLocal.isReadOnly());
	}

	@Test
	public void testReadWrite() throws Throwable {
		boolean readOnly = _transactionExecutor.execute(
			new TestTransactionAttributeAdapter(false),
			ReadOnlyTransactionThreadLocal::isReadOnly);

		Assert.assertFalse(readOnly);
	}

	@Test
	public void testStrictReadOnlyIsNotWritable() throws Throwable {
		try {
			_transactionExecutor.execute(
				new TestTransactionAttributeAdapter(false, true),
				ReadOnlyTransactionThreadLocal::isReadOnly);

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
			Assert.assertEquals(
				"Strict read only transaction is not writable",
				illegalStateException.getMessage());
		}
	}

	@Test
	public void testStrictReadOnlyNestedReadOnly() throws Throwable {
		boolean readOnly = _transactionExecutor.execute(
			new TestTransactionAttributeAdapter(true, true),
			() -> _transactionExecutor.execute(
				new TestTransactionAttributeAdapter(true),
				ReadOnlyTransactionThreadLocal::isReadOnly));

		Assert.assertTrue(readOnly);
	}

	@Test
	public void testStrictReadOnlyNestedTransactionIsNotWritable()
		throws Throwable {

		try {
			_transactionExecutor.execute(
				new TestTransactionAttributeAdapter(true, true),
				() -> _transactionExecutor.execute(
					new TestTransactionAttributeAdapter(false),
					ReadOnlyTransactionThreadLocal::isReadOnly));

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
			Assert.assertEquals(
				"Nested strict read only transaction is not writable",
				illegalStateException.getMessage());
		}
	}

	@Test
	public void testWithoutTransaction() {
		Assert.assertFalse(ReadOnlyTransactionThreadLocal.isReadOnly());
	}

	private TransactionExecutor _transactionExecutor;

	private static class TestTransactionAttributeAdapter
		extends TransactionAttributeAdapter {

		@Override
		public Propagation getPropagation() {
			return Propagation.REQUIRED;
		}

		@Override
		public boolean isReadOnly() {
			return _readOnly;
		}

		@Override
		public boolean rollbackOn(Throwable throwable) {
			return true;
		}

		private TestTransactionAttributeAdapter(boolean readOnly) {
			super(null);

			_readOnly = readOnly;
		}

		private TestTransactionAttributeAdapter(
			boolean readOnly, boolean strictReadOnly) {

			super(null, strictReadOnly);

			_readOnly = readOnly;
		}

		private final boolean _readOnly;

	}

}