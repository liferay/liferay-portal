/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Vendel Töreki
 */
@RunWith(Arquillian.class)
public class BackgroundTaskMessageListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDoReceive() throws Exception {
		_user = UserTestUtil.addUser();

		String message = RandomTestUtil.randomString();

		BackgroundTaskExecutor backgroundTaskExecutor =
			(BackgroundTaskExecutor)ProxyUtil.newProxyInstance(
				BackgroundTaskExecutor.class.getClassLoader(),
				new Class<?>[] {BackgroundTaskExecutor.class},
				(proxy, method, arguments) -> {
					String methodName = method.getName();

					if (Objects.equals(methodName, "clone")) {
						return proxy;
					}
					else if (Objects.equals(methodName, "execute")) {
						throw new IllegalStateException(message);
					}
					else if (Objects.equals(methodName, "getIsolationLevel")) {
						return BackgroundTaskConstants.
							ISOLATION_LEVEL_NOT_ISOLATED;
					}
					else if (Objects.equals(methodName, "handleException")) {
						return _STATUS_MESSAGE;
					}
					else if (Objects.equals(methodName, "isSerial")) {
						return false;
					}

					return null;
				});

		Class<?> backgroundTaskExecutorClass =
			backgroundTaskExecutor.getClass();

		Bundle bundle = FrameworkUtil.getBundle(
			BackgroundTaskMessageListenerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<BackgroundTaskExecutor> serviceRegistration =
			bundleContext.registerService(
				BackgroundTaskExecutor.class, backgroundTaskExecutor,
				HashMapDictionaryBuilder.<String, Object>put(
					"background.task.executor.class.name",
					backgroundTaskExecutorClass.getName()
				).build());

		try {
			BackgroundTask backgroundTask =
				_backgroundTaskLocalService.getBackgroundTask(
					_backgroundTaskManager.addBackgroundTask(
						_user.getUserId(),
						BackgroundTaskConstants.GROUP_ID_DEFAULT,
						BackgroundTaskMessageListenerTest.class.getName(),
						backgroundTaskExecutorClass.getName(), new HashMap<>(),
						new ServiceContext()
					).getBackgroundTaskId());

			Assert.assertEquals(
				BackgroundTaskConstants.STATUS_FAILED,
				backgroundTask.getStatus());

			String errorStackTrace = backgroundTask.getErrorStackTrace();

			Assert.assertTrue(
				errorStackTrace, errorStackTrace.contains("\tat "));
			Assert.assertTrue(
				errorStackTrace,
				errorStackTrace.contains(
					IllegalStateException.class.getName()));
			Assert.assertTrue(
				errorStackTrace, errorStackTrace.contains(message));

			Assert.assertEquals(
				_STATUS_MESSAGE, backgroundTask.getStatusMessage());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private static final String _STATUS_MESSAGE =
		"Unable to execute background task";

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private BackgroundTaskManager _backgroundTaskManager;

	@DeleteAfterTestRun
	private User _user;

}