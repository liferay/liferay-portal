/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.StatementWrapper;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import java.io.Closeable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import org.junit.Assert;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Shuyang Zhou
 */
public class TransactionalTestRule implements TestRule {

	public static final TransactionalTestRule INSTANCE =
		new TransactionalTestRule(Propagation.SUPPORTS, null);

	public TransactionalTestRule(Propagation propagation) {
		this(propagation, null);
	}

	public TransactionalTestRule(
		Propagation propagation, String originBundleSymbolicName) {

		_originBundleSymbolicName = originBundleSymbolicName;

		_transactionConfig = TransactionConfig.Factory.create(
			propagation,
			new Class<?>[] {PortalException.class, SystemException.class});
	}

	@Override
	public Statement apply(Statement statement, final Description description) {
		Statement currentStatement = statement;

		while (true) {
			if (currentStatement instanceof StatementWrapper) {
				StatementWrapper statementWrapper =
					(StatementWrapper)currentStatement;

				currentStatement = statementWrapper.getStatement();

				continue;
			}

			if (currentStatement instanceof RunRules) {
				currentStatement = ReflectionTestUtil.getFieldValue(
					currentStatement, "statement");

				continue;
			}

			if (currentStatement instanceof RunBefores) {
				replaceFrameworkMethods(currentStatement, "befores");

				currentStatement = ReflectionTestUtil.getFieldValue(
					currentStatement, "next");

				continue;
			}

			if (currentStatement instanceof RunAfters) {
				replaceFrameworkMethods(currentStatement, "afters");

				currentStatement = ReflectionTestUtil.getFieldValue(
					currentStatement, "next");

				continue;
			}

			return new StatementWrapper(statement) {

				@Override
				public void evaluate() throws Throwable {
					try (Closeable closeable = _installTransactionExecutor(
							_originBundleSymbolicName)) {

						TransactionInvokerUtil.invoke(
							getTransactionConfig(
								description.getAnnotation(Transactional.class)),
							() -> {
								try {
									statement.evaluate();
								}
								catch (Throwable throwable) {
									ReflectionUtil.throwException(throwable);
								}

								return null;
							});
					}
				}

			};
		}
	}

	public TransactionConfig getTransactionConfig(Transactional transactional) {
		if (transactional != null) {
			return TransactionConfig.Factory.create(
				transactional.isolation(), transactional.propagation(),
				transactional.readOnly(), transactional.timeout(),
				transactional.rollbackFor(),
				transactional.rollbackForClassName(),
				transactional.noRollbackFor(),
				transactional.noRollbackForClassName());
		}

		return _transactionConfig;
	}

	protected void replaceFrameworkMethods(Statement statement, String name) {
		List<FrameworkMethod> newFrameworkMethods = new ArrayList<>();

		List<FrameworkMethod> frameworkMethods =
			ReflectionTestUtil.<List<FrameworkMethod>>getFieldValue(
				statement, name);

		for (FrameworkMethod frameworkMethod : frameworkMethods) {
			if (frameworkMethod instanceof TransactionalFrameworkMethod) {
				newFrameworkMethods.add(frameworkMethod);

				continue;
			}

			newFrameworkMethods.add(
				new TransactionalFrameworkMethod(
					frameworkMethod.getMethod(),
					getTransactionConfig(
						frameworkMethod.getAnnotation(Transactional.class)),
					_originBundleSymbolicName));
		}

		ReflectionTestUtil.setFieldValue(statement, name, newFrameworkMethods);
	}

	protected static class TransactionalFrameworkMethod
		extends FrameworkMethod {

		@Override
		public Object invokeExplosively(Object target, Object... params)
			throws Throwable {

			try (Closeable closeable = _installTransactionExecutor(
					_originBundleSymbolicName)) {

				return TransactionInvokerUtil.invoke(
					_transactionConfig,
					() -> {
						try {
							return TransactionalFrameworkMethod.super.
								invokeExplosively(target, params);
						}
						catch (Throwable throwable) {
							ReflectionUtil.throwException(throwable);
						}

						return null;
					});
			}
		}

		protected TransactionalFrameworkMethod(
			Method method, TransactionConfig transactionConfig,
			String originBundleSymbolicName) {

			super(method);

			_transactionConfig = transactionConfig;
			_originBundleSymbolicName = originBundleSymbolicName;
		}

		private final String _originBundleSymbolicName;
		private final TransactionConfig _transactionConfig;

	}

	private static Closeable _installTransactionExecutor(
			String originBundleSymbolicName)
		throws Exception {

		if (originBundleSymbolicName == null) {
			return () -> {
			};
		}

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		Class<?> clazz = classLoader.loadClass(
			"com.liferay.portal.spring.transaction." +
				"TransactionExecutorThreadLocal");

		Field field = clazz.getDeclaredField("_transactionExecutorDeque");

		field.setAccessible(true);

		ThreadLocal<Deque<Object>> deque =
			(ThreadLocal<Deque<Object>>)field.get(null);

		Deque<Object> transactionExecutorDeque = deque.get();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceReference<?>[] serviceReferences =
			bundleContext.getAllServiceReferences(
				"com.liferay.portal.spring.transaction.TransactionExecutor",
				"(origin.bundle.symbolic.name=" + originBundleSymbolicName +
					")");

		Assert.assertEquals(
			StringBundler.concat(
				"Expected 1 TransactionExecutor for ", originBundleSymbolicName,
				", actually have ", Arrays.toString(serviceReferences)),
			1, serviceReferences.length);

		ServiceReference<?> serviceReference = serviceReferences[0];

		Object portletTransactionExecutor = bundleContext.getService(
			serviceReference);

		if (portletTransactionExecutor == transactionExecutorDeque.peek()) {
			return () -> {
			};
		}

		transactionExecutorDeque.push(portletTransactionExecutor);

		return () -> {
			transactionExecutorDeque.pop();

			bundleContext.ungetService(serviceReference);
		};
	}

	private final String _originBundleSymbolicName;
	private final TransactionConfig _transactionConfig;

}