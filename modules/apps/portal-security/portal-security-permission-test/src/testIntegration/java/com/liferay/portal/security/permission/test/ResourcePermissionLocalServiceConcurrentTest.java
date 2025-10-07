/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionPersistence;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.SynchronousInvocationHandler;
import com.liferay.portal.kernel.test.constants.ServiceTestConstants;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.service.impl.ResourcePermissionLocalServiceImpl;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.spring.transaction.DefaultTransactionExecutor;
import com.liferay.portal.test.rule.ExpectedDBType;
import com.liferay.portal.test.rule.ExpectedLog;
import com.liferay.portal.test.rule.ExpectedLogs;
import com.liferay.portal.test.rule.ExpectedMultipleLogs;
import com.liferay.portal.test.rule.ExpectedType;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
 * @author William Newbury
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class ResourcePermissionLocalServiceConcurrentTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws NoSuchMethodException {
		Assume.assumeTrue(PropsValues.RETRY_ADVICE_MAX_RETRIES != 0);

		if ((PropsValues.RETRY_ADVICE_MAX_RETRIES > 0) &&
			(_threadCount > PropsValues.RETRY_ADVICE_MAX_RETRIES)) {

			_threadCount = PropsValues.RETRY_ADVICE_MAX_RETRIES;
		}

		_actionId = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);
		_name = RandomTestUtil.randomString(
			UniqueStringRandomizerBumper.INSTANCE);

		_resourceAction = _resourceActionLocalService.addResourceAction(
			_name, _actionId, RandomTestUtil.randomLong());

		AopInvocationHandler aopInvocationHandler =
			ProxyUtil.fetchInvocationHandler(
				_resourcePermissionLocalService, AopInvocationHandler.class);

		ServiceWrapper<ResourcePermissionLocalService>
			resourcePermissionLocalServiceWrapper =
				(ServiceWrapper<ResourcePermissionLocalService>)
					aopInvocationHandler.getTarget();

		final ResourcePermissionLocalServiceImpl
			resourcePermissionLocalServiceImpl =
				(ResourcePermissionLocalServiceImpl)
					resourcePermissionLocalServiceWrapper.getWrappedService();

		final ResourcePermissionPersistence resourcePermissionPersistence =
			resourcePermissionLocalServiceImpl.
				getResourcePermissionPersistence();

		ReflectionTestUtil.setFieldValue(
			resourcePermissionLocalServiceImpl, "resourcePermissionPersistence",
			ProxyUtil.newProxyInstance(
				ResourcePermissionPersistence.class.getClassLoader(),
				new Class<?>[] {ResourcePermissionPersistence.class},
				new SynchronousInvocationHandler(
					_threadCount,
					new Runnable() {

						@Override
						public void run() {
							ReflectionTestUtil.setFieldValue(
								resourcePermissionLocalServiceImpl,
								"resourcePermissionPersistence",
								resourcePermissionPersistence);
						}

					},
					ResourcePermissionPersistence.class.getMethod(
						"update", BaseModel.class),
					resourcePermissionPersistence)));
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
	public void testAddResourcePermissionConcurrently() throws Exception {
		SynchronousInvocationHandler.enable();

		try {
			String primKey = RandomTestUtil.randomString(
				UniqueStringRandomizerBumper.INSTANCE);

			Callable<ResourcePermission> callable = () -> {
				Role role = _roleLocalService.getRole(
					TestPropsValues.getCompanyId(), RoleConstants.GUEST);

				_resourcePermissionLocalService.addResourcePermission(
					TestPropsValues.getCompanyId(), _name, 0, primKey,
					role.getRoleId(), _actionId);

				return _resourcePermissionLocalService.fetchResourcePermission(
					TestPropsValues.getCompanyId(), _name, 0, primKey,
					role.getRoleId());
			};

			List<FutureTask<ResourcePermission>> futureTasks =
				new ArrayList<>();

			for (int i = 0; i < _threadCount; i++) {
				FutureTask<ResourcePermission> futureTask = new FutureTask<>(
					callable);

				String className =
					ResourcePermissionLocalServiceConcurrentTest.class.
						getName();

				Thread thread = new Thread(
					futureTask,
					className + "-concurrent-addResourcePermission-" + i);

				thread.start();

				futureTasks.add(futureTask);
			}

			Set<ResourcePermission> resourcePermissions = new HashSet<>();

			for (FutureTask<ResourcePermission> futureTask : futureTasks) {
				resourcePermissions.add(futureTask.get());
			}

			Assert.assertEquals(
				resourcePermissions.toString(), 1, resourcePermissions.size());

			Iterator<ResourcePermission> iterator =
				resourcePermissions.iterator();

			_resourcePermission = iterator.next();

			Assert.assertEquals(
				_resourceAction.getBitwiseValue(),
				_resourcePermission.getActionIds());
		}
		finally {
			SynchronousInvocationHandler.disable();
		}
	}

	private String _actionId;
	private String _name;

	@DeleteAfterTestRun
	private ResourceAction _resourceAction;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@DeleteAfterTestRun
	private ResourcePermission _resourcePermission;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private int _threadCount = ServiceTestConstants.THREAD_COUNT;

}