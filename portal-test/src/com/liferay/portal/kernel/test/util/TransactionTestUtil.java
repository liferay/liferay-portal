/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import java.io.Closeable;

import java.util.Deque;
import java.util.concurrent.Callable;

import org.junit.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Shuyang Zhou
 */
public class TransactionTestUtil {

	public static Closeable installTransactionExecutor(
		String originBundleSymbolicName) {

		Object transactionExecutor =
			_transactionExecutorServiceTrackerMap.getService(
				originBundleSymbolicName);

		Assert.assertNotNull(
			"No transaction executor was found for " + originBundleSymbolicName,
			transactionExecutor);

		Deque<Object> transactionExecutorDeque =
			_transactionExecutorDeque.get();

		if (transactionExecutor == transactionExecutorDeque.peek()) {
			return () -> {
			};
		}

		transactionExecutorDeque.push(transactionExecutor);

		return transactionExecutorDeque::pop;
	}

	public static <T> T invoke(
			Class<?> clazz, TransactionConfig transactionConfig,
			Callable<T> callable)
		throws Throwable {

		Bundle bundle = FrameworkUtil.getBundle(clazz);

		try (Closeable closeable = installTransactionExecutor(
				bundle.getSymbolicName())) {

			return TransactionInvokerUtil.invoke(transactionConfig, callable);
		}
	}

	public static <T, R> R invoke(
			T target, UnsafeFunction<T, R, Exception> unsafeFunction)
		throws Exception {

		try {
			return invoke(
				target.getClass(), _transactionConfig,
				() -> unsafeFunction.apply(target));
		}
		catch (Throwable throwable) {
			return ReflectionUtil.throwException(throwable);
		}
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.SUPPORTS, new Class<?>[] {Exception.class});
	private static final ThreadLocal<Deque<Object>> _transactionExecutorDeque;
	private static final ServiceTrackerMap<String, Object>
		_transactionExecutorServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				SystemBundleUtil.getBundleContext(), null,
				"(&(objectClass=com.liferay.portal.spring.transaction." +
					"TransactionExecutor)(origin.bundle.symbolic.name=*))",
				new PropertyServiceReferenceMapper<>(
					"origin.bundle.symbolic.name"));

	static {
		try {
			ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

			Class<?> clazz = classLoader.loadClass(
				"com.liferay.portal.spring.transaction." +
					"TransactionExecutorThreadLocal");

			_transactionExecutorDeque = ReflectionTestUtil.getFieldValue(
				clazz, "_transactionExecutorDeque");
		}
		catch (ClassNotFoundException classNotFoundException) {
			throw new ExceptionInInitializerError(classNotFoundException);
		}
	}

}