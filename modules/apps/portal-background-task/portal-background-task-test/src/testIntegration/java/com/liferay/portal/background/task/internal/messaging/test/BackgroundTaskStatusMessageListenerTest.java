/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.background.task.constants.BackgroundTaskPortletKeys;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.List;
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
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class BackgroundTaskStatusMessageListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testSendUserNotifications() throws Exception {
		_user = UserTestUtil.addUser();

		BackgroundTaskExecutor backgroundTaskExecutor =
			(BackgroundTaskExecutor)ProxyUtil.newProxyInstance(
				BackgroundTaskExecutor.class.getClassLoader(),
				new Class<?>[] {BackgroundTaskExecutor.class},
				(proxy, method, argus) -> {
					if (Objects.equals(method.getName(), "clone")) {
						return proxy;
					}
					else if (Objects.equals(method.getName(), "execute")) {
						return new BackgroundTaskResult(
							BackgroundTaskConstants.STATUS_FAILED);
					}
					else if (Objects.equals(
								method.getName(), "getIsolationLevel")) {

						return BackgroundTaskConstants.
							ISOLATION_LEVEL_NOT_ISOLATED;
					}
					else if (Objects.equals(method.getName(), "isSerial")) {
						return false;
					}

					return null;
				});

		Class<?> backgroundTaskExecutorClass =
			backgroundTaskExecutor.getClass();

		Bundle bundle = FrameworkUtil.getBundle(
			BackgroundTaskStatusMessageListenerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<BackgroundTaskExecutor> serviceRegistration =
			bundleContext.registerService(
				BackgroundTaskExecutor.class, backgroundTaskExecutor,
				HashMapDictionaryBuilder.<String, Object>put(
					"background.task.executor.class.name",
					backgroundTaskExecutorClass.getName()
				).build());

		try {
			_backgroundTaskManager.addBackgroundTask(
				_user.getUserId(), BackgroundTaskConstants.GROUP_ID_DEFAULT,
				BackgroundTaskStatusMessageListenerTest.class.getName(),
				backgroundTaskExecutorClass.getName(), new HashMap<>(),
				new ServiceContext());

			List<UserNotificationEvent> userNotificationEvents =
				_userNotificationEventLocalService.getUserNotificationEvents(
					_user.getUserId());

			Assert.assertEquals(
				userNotificationEvents.toString(), 1,
				userNotificationEvents.size());

			UserNotificationEvent userNotificationEvent =
				userNotificationEvents.get(0);

			Assert.assertEquals(
				BackgroundTaskPortletKeys.BACKGROUND_TASK,
				userNotificationEvent.getType());

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				userNotificationEvent.getPayload());

			Assert.assertEquals(
				BackgroundTaskStatusMessageListenerTest.class.getName(),
				jsonObject.getString("name"));
			Assert.assertEquals(
				backgroundTaskExecutorClass.getName(),
				jsonObject.getString("taskExecutorClassName"));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Inject
	private BackgroundTaskManager _backgroundTaskManager;

	@Inject
	private JSONFactory _jsonFactory;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}