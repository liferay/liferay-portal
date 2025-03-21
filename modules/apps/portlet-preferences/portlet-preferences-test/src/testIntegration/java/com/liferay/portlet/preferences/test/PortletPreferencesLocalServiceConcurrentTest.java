/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.preferences.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.persistence.PortletPreferencesPersistence;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.SynchronousInvocationHandler;
import com.liferay.portal.kernel.test.constants.ServiceTestConstants;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.service.impl.PortletPreferencesLocalServiceImpl;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.spring.transaction.DefaultTransactionExecutor;
import com.liferay.portal.test.rule.ExpectedDBType;
import com.liferay.portal.test.rule.ExpectedLog;
import com.liferay.portal.test.rule.ExpectedLogs;
import com.liferay.portal.test.rule.ExpectedMultipleLogs;
import com.liferay.portal.test.rule.ExpectedType;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matthew Tambara
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class PortletPreferencesLocalServiceConcurrentTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws NoSuchMethodException {
		Assume.assumeTrue(PropsValues.RETRY_ADVICE_MAX_RETRIES != 0);

		if ((PropsValues.RETRY_ADVICE_MAX_RETRIES > 0) &&
			(_threadCount > PropsValues.RETRY_ADVICE_MAX_RETRIES)) {

			_threadCount = PropsValues.RETRY_ADVICE_MAX_RETRIES;
		}

		AopInvocationHandler aopInvocationHandler =
			ProxyUtil.fetchInvocationHandler(
				_portletPreferencesLocalService, AopInvocationHandler.class);

		PortletPreferencesLocalServiceImpl portletPreferencesLocalServiceImpl =
			(PortletPreferencesLocalServiceImpl)
				aopInvocationHandler.getTarget();

		PortletPreferencesPersistence portletPreferencesPersistence =
			portletPreferencesLocalServiceImpl.
				getPortletPreferencesPersistence();

		ReflectionTestUtil.setFieldValue(
			portletPreferencesLocalServiceImpl, "portletPreferencesPersistence",
			ProxyUtil.newProxyInstance(
				PortletPreferencesPersistence.class.getClassLoader(),
				new Class<?>[] {PortletPreferencesPersistence.class},
				new SynchronousInvocationHandler(
					_threadCount,
					() -> ReflectionTestUtil.setFieldValue(
						portletPreferencesLocalServiceImpl,
						"portletPreferencesPersistence",
						portletPreferencesPersistence),
					PortletPreferencesPersistence.class.getMethod(
						"update", BaseModel.class),
					portletPreferencesPersistence)));
	}

	@ExpectedMultipleLogs(
		expectedMultipleLogs = {
			@ExpectedLogs(
				expectedLogs = {
					@ExpectedLog(
						expectedLog = "Application exception overridden by commit exception",
						expectedType = ExpectedType.PREFIX
					)
				},
				level = "ERROR", loggerClass = DefaultTransactionExecutor.class
			),
			@ExpectedLogs(
				expectedLogs = {
					@ExpectedLog(
						expectedDBType = ExpectedDBType.DB2,
						expectedLog = "Batch failure",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.DB2,
						expectedLog = "DB2 SQL Error: SQLCODE=-803",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.HYPERSONIC,
						expectedLog = "integrity constraint violation",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MARIADB,
						expectedLog = "Duplicate entry '",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MYSQL,
						expectedLog = "Duplicate entry '",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.ORACLE,
						expectedLog = "ORA-00001: unique constraint",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.POSTGRESQL,
						expectedLog = "Batch entry",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.POSTGRESQL,
						expectedLog = "duplicate key",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.SQLSERVER,
						expectedLog = "Cannot insert duplicate key row",
						expectedType = ExpectedType.PREFIX
					)
				},
				level = "ERROR", loggerClass = SqlExceptionHelper.class
			),
			@ExpectedLogs(
				expectedLogs = {
					@ExpectedLog(
						expectedDBType = ExpectedDBType.NONE,
						expectedLog = "HHH000315: Exception executing batch [java.sql.BatchUpdateException",
						expectedType = ExpectedType.PREFIX
					)
				},
				level = "ERROR", loggerClass = BatchingBatch.class
			)
		}
	)
	@Test
	public void testAddPortletPreferencesConcurrently() throws Exception {
		SynchronousInvocationHandler.enable();

		try {
			long ownerId = RandomTestUtil.randomLong();
			int ownerType = RandomTestUtil.randomInt();
			long plid = RandomTestUtil.randomLong();
			String portletId = RandomTestUtil.randomString(
				UniqueStringRandomizerBumper.INSTANCE);

			Callable<PortletPreferences> callable =
				() -> _portletPreferencesLocalService.getPreferences(
					TestPropsValues.getCompanyId(), ownerId, ownerType, plid,
					portletId);

			List<FutureTask<PortletPreferences>> futureTasks =
				new ArrayList<>();

			for (int i = 0; i < _threadCount; i++) {
				FutureTask<PortletPreferences> futureTask = new FutureTask<>(
					callable);

				String className =
					PortletPreferencesLocalServiceConcurrentTest.class.
						getName();

				Thread thread = new Thread(
					futureTask, className + "-concurrent-getPreferences-" + i);

				thread.start();

				futureTasks.add(futureTask);
			}

			Set<PortletPreferences> portletPreferencesSet = new HashSet<>();

			for (FutureTask<PortletPreferences> futureTask : futureTasks) {
				portletPreferencesSet.add(futureTask.get());
			}

			Assert.assertEquals(
				portletPreferencesSet.toString(), 1,
				portletPreferencesSet.size());

			_portletPreferences =
				_portletPreferencesLocalService.getPortletPreferences(
					ownerId, ownerType, plid, portletId);
		}
		finally {
			SynchronousInvocationHandler.disable();
		}
	}

	@DeleteAfterTestRun
	private com.liferay.portal.kernel.model.PortletPreferences
		_portletPreferences;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	private int _threadCount = ServiceTestConstants.THREAD_COUNT;

}