/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.view.count.service.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.increment.BufferedIncrementThreadLocal;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.view.count.model.ViewCountEntry;
import com.liferay.view.count.service.ViewCountEntryLocalService;
import com.liferay.view.count.service.persistence.ViewCountEntryFinder;
import com.liferay.view.count.service.persistence.ViewCountEntryPK;

import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class ViewCountEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_className = _classNameLocalService.getClassName(
			ViewCountEntryLocalServiceTest.class.getName());
	}

	@Test
	public void testLazyCreationWithRaceCondition() throws Throwable {
		Assume.assumeFalse(
			"HSQL does not allow concurrent Session assess, skip test.",
			DBManagerUtil.getDBType() == DBType.HYPERSONIC);

		long classPK = 0;
		int viewCount = 100;

		ViewCountEntryPK viewCountEntryPK = new ViewCountEntryPK(
			TestPropsValues.getCompanyId(), _className.getClassNameId(),
			classPK);

		Assert.assertNull(
			_viewCountEntryLocalService.fetchViewCountEntry(viewCountEntryPK));

		CountDownLatch countDownLatch = new CountDownLatch(2);
		SessionFactory sessionFactory = ReflectionTestUtil.getFieldValue(
			_viewCountEntryFinder, "_sessionFactory");
		List<ViewCountEntry> viewCountEntries = new CopyOnWriteArrayList<>();

		ReflectionTestUtil.setFieldValue(
			_viewCountEntryFinder, "_sessionFactory",
			_createSessionFactoryProxy(
				countDownLatch, sessionFactory, viewCountEntries));

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				SqlExceptionHelper.class.getName(), LoggerTestUtil.OFF)) {

			FutureTask<Void> futureTask = new FutureTask<>(
				new CompanyInheritableThreadLocalCallable<>(
					() -> {
						try (SafeCloseable safeCloseable =
								BufferedIncrementThreadLocal.
									setForceSyncWithSafeCloseable(true)) {

							_viewCountEntryLocalService.incrementViewCount(
								TestPropsValues.getCompanyId(),
								_className.getClassNameId(), classPK,
								viewCount);
						}

						return null;
					}));

			Thread thread = new Thread(
				futureTask, "Inner View Count Incrementer");

			thread.start();

			_viewCountEntryLocalService.incrementViewCount(
				TestPropsValues.getCompanyId(), _className.getClassNameId(),
				classPK, viewCount);

			futureTask.get();
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_viewCountEntryFinder, "_sessionFactory", sessionFactory);
		}

		_viewCountEntry = _viewCountEntryLocalService.getViewCountEntry(
			viewCountEntryPK);

		Assert.assertEquals(viewCount * 2, _viewCountEntry.getViewCount());
	}

	private Object _createSessionFactoryProxy(
		CountDownLatch countDownLatch, SessionFactory sessionFactory,
		List<ViewCountEntry> viewCountEntries) {

		return ProxyUtil.newProxyInstance(
			SessionFactory.class.getClassLoader(),
			new Class<?>[] {SessionFactory.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "openSession")) {
					return _createSessionProxy(
						countDownLatch, sessionFactory.openSession(),
						viewCountEntries);
				}

				return method.invoke(sessionFactory, args);
			});
	}

	private Object _createSessionProxy(
		CountDownLatch countDownLatch, Session session,
		List<ViewCountEntry> viewCountEntries) {

		return ProxyUtil.newProxyInstance(
			Session.class.getClassLoader(), new Class<?>[] {Session.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "get") &&
					(countDownLatch.getCount() > 0)) {

					countDownLatch.countDown();

					countDownLatch.await();

					ViewCountEntry viewCountEntry =
						(ViewCountEntry)method.invoke(session, args);

					viewCountEntries.add(viewCountEntry);

					Assert.assertNull(viewCountEntries.get(0));

					if (viewCountEntries.size() == 2) {
						if (DBManagerUtil.getDBType() == DBType.SQLSERVER) {
							Assert.assertNotNull(viewCountEntries.get(1));
						}
						else {
							Assert.assertNull(viewCountEntries.get(1));
						}
					}

					return viewCountEntry;
				}

				try {
					return method.invoke(session, args);
				}
				catch (InvocationTargetException invocationTargetException) {
					throw invocationTargetException.getCause();
				}
			});
	}

	@DeleteAfterTestRun
	private static ClassName _className;

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static ViewCountEntryFinder _viewCountEntryFinder;

	@Inject
	private static ViewCountEntryLocalService _viewCountEntryLocalService;

	@DeleteAfterTestRun
	private ViewCountEntry _viewCountEntry;

}