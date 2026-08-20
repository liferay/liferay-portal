/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.classname.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class ClassNameLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testConcurrentGetClassName() throws Exception {

		// Pause the first resolver at its class name pool write, let the test
		// thread resolve the same new value to completion, then release the
		// paused thread into the unique index violation and its rollback. The
		// paused thread must come back with the winner's committed row, and
		// the pool must never serve an ID whose insert rolled back.

		String value = ClassNameLocalServiceTest.class.getName();

		// A leftover row from an earlier run would turn the first resolution
		// into a plain read, so drop it

		ClassName className = _classNameLocalService.fetchClassName(value);

		if (className.getClassNameId() > 0) {
			_classNameLocalService.deleteClassName(className);
		}

		className = null;

		FutureTask<ClassName> futureTask = new FutureTask<>(
			new CompanyInheritableThreadLocalCallable<>(
				() -> _classNameLocalService.getClassName(value)));

		Thread thread = new Thread(futureTask, "Class Name Local Service Test");

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		Class<?> classNamePoolClass = classLoader.loadClass(
			"com.liferay.portal.service.impl.ClassNameLocalServiceImpl$" +
				"ClassNamePool");

		Map<Long, Map<String, Long>> classNameIdsMap =
			ReflectionTestUtil.getFieldValue(
				classNamePoolClass, "_classNameIdsMap");

		Long companyId = CompanyConstants.SYSTEM;

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			companyId = CompanyThreadLocal.getNonsystemCompanyId();
		}

		Map<String, Long> classNameIds = classNameIdsMap.get(companyId);

		CountDownLatch pausedCountDownLatch = new CountDownLatch(1);
		CountDownLatch resumeCountDownLatch = new CountDownLatch(1);

		classNameIdsMap.put(
			companyId,
			(Map<String, Long>)ProxyUtil.newDelegateProxyInstance(
				classLoader, Map.class,
				new Object() {

					public Object put(Object key, Object putValue) {
						if (value.equals(key) &&
							(Thread.currentThread() == thread)) {

							pausedCountDownLatch.countDown();

							try {
								resumeCountDownLatch.await();
							}
							catch (InterruptedException interruptedException) {
								ReflectionUtil.throwException(
									interruptedException);
							}
						}

						return classNameIds.put((String)key, (Long)putValue);
					}

				},
				classNameIds));

		// With the pool publish deferred to the commit callback, the paused
		// thread only pauses after its insert commits, so the resolution on
		// the test thread reads the committed row and no unique index
		// violation occurs. Capture the SQL error logger so a regression that
		// revives the violation surfaces as an asserted log entry

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"org.hibernate.engine.jdbc.spi.SqlExceptionHelper",
				LoggerTestUtil.ERROR)) {

			thread.start();

			pausedCountDownLatch.await();

			className = _classNameLocalService.getClassName(value);

			resumeCountDownLatch.countDown();

			ClassName pausedThreadClassName = futureTask.get();

			Assert.assertEquals(
				className.getClassNameId(),
				pausedThreadClassName.getClassNameId());

			long pooledClassNameId = _classNameLocalService.getClassNameId(
				value);

			Assert.assertEquals(className.getClassNameId(), pooledClassNameId);

			ClassName persistedClassName = _classNameLocalService.getClassName(
				pooledClassNameId);

			Assert.assertEquals(value, persistedClassName.getValue());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
		}
		finally {
			classNameIdsMap.put(companyId, classNameIds);

			if (className != null) {
				_classNameLocalService.deleteClassName(className);
			}
		}
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

}