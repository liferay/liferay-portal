/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.rule.ClassTestRule;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.Deque;
import java.util.Map;

import org.junit.runner.Description;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Preston Crary
 */
public class ChangeTrackingTestRule extends ClassTestRule<AutoCloseable> {

	public static final ChangeTrackingTestRule INSTANCE =
		new ChangeTrackingTestRule();

	@Override
	protected void afterClass(
			Description description, AutoCloseable autoCloseable)
		throws Exception {

		autoCloseable.close();
	}

	@Override
	protected AutoCloseable beforeClass(Description description)
		throws Exception {

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		Class<?> clazz = classLoader.loadClass(
			"com.liferay.portal.spring.transaction." +
				"TransactionExecutorThreadLocal");

		Field field = clazz.getDeclaredField("_transactionExecutorDeque");

		field.setAccessible(true);

		ThreadLocal<Deque<Object>> deque =
			(ThreadLocal<Deque<Object>>)field.get(null);

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				ChainableMethodAdvice.class, new CTTestRuleAdvice(deque), null);

		return serviceRegistration::unregister;
	}

	private ChangeTrackingTestRule() {
	}

	private static final CentralizedThreadLocal<Boolean> _ctSafe =
		new CentralizedThreadLocal<>(
			ChangeTrackingTestRule.class + "._ctSafe", () -> Boolean.FALSE);

	private static class CTTestRuleAdvice extends ChainableMethodAdvice {

		@Override
		public Object createMethodContext(
			Class<?> targetClass, Method method,
			Map<Class<? extends Annotation>, Annotation> annotations) {

			Transactional transactional = (Transactional)annotations.get(
				Transactional.class);

			if ((transactional == null) || !transactional.enabled()) {
				return null;
			}

			CTAware ctAware = (CTAware)annotations.get(CTAware.class);

			if (ctAware != null) {
				if (ctAware.onProduction()) {
					return CTMode.READ_ONLY;
				}

				return null;
			}

			if (transactional.readOnly()) {
				return CTMode.READ_ONLY;
			}

			if (transactional.propagation() == Propagation.REQUIRES_NEW) {
				return CTMode.REQUIRES_NEW;
			}

			return CTMode.STRICT;
		}

		@Override
		public Object invoke(
				AopMethodInvocation aopMethodInvocation, Object[] arguments)
			throws Throwable {

			if (_ctSafe.get()) {
				return aopMethodInvocation.proceed(arguments);
			}

			CTMode ctMode = aopMethodInvocation.getAdviceMethodContext();

			if ((ctMode == CTMode.REQUIRES_NEW) ||
				!_hasCurrentTransactionExecutor()) {

				try (SafeCloseable safeCloseable = _ctSafe.setWithSafeCloseable(
						Boolean.TRUE)) {

					return aopMethodInvocation.proceed(arguments);
				}
			}
			else if (ctMode == CTMode.READ_ONLY) {
				return aopMethodInvocation.proceed(arguments);
			}

			throw new CTTransactionException(
				"CT transaction validation failure. Nested operation using " +
					aopMethodInvocation.getThis() +
						" can only be performed in production mode.");
		}

		private CTTestRuleAdvice(
			ThreadLocal<Deque<Object>> transactionExecutorDeque) {

			_transactionExecutorDeque = transactionExecutorDeque;
		}

		private boolean _hasCurrentTransactionExecutor() {
			Deque<Object> deque = _transactionExecutorDeque.get();

			if (deque.peek() == null) {
				return false;
			}

			return true;
		}

		private final ThreadLocal<Deque<Object>> _transactionExecutorDeque;

		private enum CTMode {

			READ_ONLY, REQUIRES_NEW, STRICT

		}

	}

}