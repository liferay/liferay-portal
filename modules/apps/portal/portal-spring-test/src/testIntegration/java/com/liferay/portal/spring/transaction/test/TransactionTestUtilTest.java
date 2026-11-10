/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.transaction.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.dao.orm.hibernate.VerifySessionFactoryWrapper;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class TransactionTestUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testInvoke() throws Throwable {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Collection<ServiceReference<SessionFactory>> serviceReferences =
			bundleContext.getServiceReferences(
				SessionFactory.class,
				"(origin.bundle.symbolic.name=com.liferay.portal.language." +
					"override.service)");

		Assert.assertEquals(
			serviceReferences.toString(), 1, serviceReferences.size());

		Iterator<ServiceReference<SessionFactory>> iterator =
			serviceReferences.iterator();

		ServiceReference<SessionFactory> serviceReference = iterator.next();

		SessionFactory sessionFactory = bundleContext.getService(
			serviceReference);

		Bundle bundle = serviceReference.getBundle();

		Class<?> clazz = bundle.loadClass(
			"com.liferay.portal.language.override.service.impl." +
				"PLOEntryLocalServiceImpl");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				VerifySessionFactoryWrapper.class.getName(),
				LoggerTestUtil.ERROR)) {

			TransactionTestUtil.invoke(
				clazz, _transactionConfig, () -> _openSession(sessionFactory));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

			TransactionInvokerUtil.invoke(
				_transactionConfig, () -> _openSession(sessionFactory));

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Failed session factory verification", logEntry.getMessage());
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	@Test
	public void testInvokeUnsafeFunction() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Collection<ServiceReference<SessionFactory>> serviceReferences =
			bundleContext.getServiceReferences(
				SessionFactory.class,
				"(origin.bundle.symbolic.name=com.liferay.portal.language." +
					"override.service)");

		Assert.assertEquals(
			serviceReferences.toString(), 1, serviceReferences.size());

		Iterator<ServiceReference<SessionFactory>> iterator =
			serviceReferences.iterator();

		ServiceReference<SessionFactory> serviceReference = iterator.next();

		SessionFactory sessionFactory = bundleContext.getService(
			serviceReference);

		ServiceReference<?> ploEntryPersistenceServiceReference =
			bundleContext.getServiceReference(
				"com.liferay.portal.language.override.service.persistence." +
					"PLOEntryPersistence");

		Object ploEntryPersistence = bundleContext.getService(
			ploEntryPersistenceServiceReference);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				VerifySessionFactoryWrapper.class.getName(),
				LoggerTestUtil.ERROR)) {

			Assert.assertSame(
				ploEntryPersistence,
				TransactionTestUtil.invoke(
					ploEntryPersistence,
					curPLOEntryPersistence -> {
						_openSession(sessionFactory);

						return curPLOEntryPersistence;
					}));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

			Error error = new Error();

			try {
				TransactionTestUtil.invoke(
					ploEntryPersistence,
					curPLOEntryPersistence -> {
						throw error;
					});

				Assert.fail();
			}
			catch (Exception exception) {
				Assert.assertSame(error, exception.getCause());
			}
		}
		finally {
			bundleContext.ungetService(ploEntryPersistenceServiceReference);
			bundleContext.ungetService(serviceReference);
		}
	}

	private Object _openSession(SessionFactory sessionFactory) {
		Session session = sessionFactory.openSession();

		sessionFactory.closeSession(session);

		return null;
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.SUPPORTS, new Class<?>[] {Exception.class});

}