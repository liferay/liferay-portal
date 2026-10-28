/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.constants.BackgroundTaskPortletKeys;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

	@Before
	public void setUp() throws Exception {
		_lockOwner = RandomTestUtil.randomString();
		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() throws Exception {
		for (String lockKey : _lockKeys) {
			_lockManager.unlock(
				BackgroundTaskExecutor.class.getName(), lockKey, _lockOwner);
		}

		for (long backgroundTaskId : _backgroundTaskIds) {
			_backgroundTaskManager.deleteBackgroundTask(backgroundTaskId);
		}

		for (ServiceRegistration<BackgroundTaskExecutor> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	@Test
	public void testDoReceive() throws Exception {
		String taskExecutorClassName = _registerBackgroundTaskExecutor(
			BackgroundTaskConstants.ISOLATION_LEVEL_GROUP,
			BackgroundTaskResult.SUCCESS);

		long groupId1 = RandomTestUtil.randomLong();
		long groupId2 = RandomTestUtil.randomLong();

		_lock(taskExecutorClassName + StringPool.POUND + groupId1);
		_lock(taskExecutorClassName + StringPool.POUND + groupId2);

		long backgroundTaskId1 = _addQueuedBackgroundTask(
			groupId1, taskExecutorClassName);
		long backgroundTaskId2 = _addQueuedBackgroundTask(
			groupId2, taskExecutorClassName);
		long backgroundTaskId3 = _addQueuedBackgroundTask(
			groupId2, taskExecutorClassName);

		_unlock(taskExecutorClassName + StringPool.POUND + groupId2);

		Message message = new Message();

		message.put(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
			backgroundTaskId3);
		message.put("companyId", _user.getCompanyId());
		message.put("status", BackgroundTaskConstants.STATUS_QUEUED);
		message.put("taskExecutorClassName", taskExecutorClassName);

		_messageBus.sendMessage(
			DestinationNames.BACKGROUND_TASK_STATUS, message);

		Assert.assertEquals(
			Arrays.asList(backgroundTaskId3, backgroundTaskId2),
			_executedBackgroundTaskIds);
		Assert.assertEquals(
			BackgroundTaskConstants.STATUS_QUEUED,
			_getStatus(backgroundTaskId1));
		Assert.assertEquals(
			BackgroundTaskConstants.STATUS_SUCCESSFUL,
			_getStatus(backgroundTaskId2));
		Assert.assertEquals(
			BackgroundTaskConstants.STATUS_SUCCESSFUL,
			_getStatus(backgroundTaskId3));
	}

	@Test
	public void testExecuteBackgroundTasks() throws Exception {
		_testExecuteBackgroundTasks(BackgroundTaskResult.COMPLETED_WITH_ERRORS);
		_testExecuteBackgroundTasks(BackgroundTaskResult.SUCCESS);
	}

	@Test
	public void testSendUserNotifications() throws Exception {
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

	private long _addQueuedBackgroundTask(
			long groupId, String taskExecutorClassName)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskManager.addBackgroundTask(
				_user.getUserId(), groupId, RandomTestUtil.randomString(),
				taskExecutorClassName, new HashMap<>(), new ServiceContext());

		long backgroundTaskId = backgroundTask.getBackgroundTaskId();

		_backgroundTaskIds.add(backgroundTaskId);

		Assert.assertEquals(
			BackgroundTaskConstants.STATUS_QUEUED,
			_getStatus(backgroundTaskId));

		return backgroundTaskId;
	}

	private int _getStatus(long backgroundTaskId) {
		BackgroundTask backgroundTask =
			_backgroundTaskManager.fetchBackgroundTask(backgroundTaskId);

		return backgroundTask.getStatus();
	}

	private void _lock(String lockKey) {
		_lockManager.lock(
			BackgroundTaskExecutor.class.getName(), lockKey, _lockOwner);

		_lockKeys.add(lockKey);
	}

	private String _registerBackgroundTaskExecutor(
		int isolationLevel, BackgroundTaskResult backgroundTaskResult) {

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
						BackgroundTask backgroundTask =
							(BackgroundTask)arguments[0];

						_executedBackgroundTaskIds.add(
							backgroundTask.getBackgroundTaskId());

						return backgroundTaskResult;
					}
					else if (Objects.equals(methodName, "getIsolationLevel")) {
						return isolationLevel;
					}
					else if (Objects.equals(methodName, "isSerial")) {
						return true;
					}

					return null;
				});

		String taskExecutorClassName = RandomTestUtil.randomString();

		Bundle bundle = FrameworkUtil.getBundle(
			BackgroundTaskStatusMessageListenerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistrations.add(
			bundleContext.registerService(
				BackgroundTaskExecutor.class, backgroundTaskExecutor,
				HashMapDictionaryBuilder.<String, Object>put(
					"background.task.executor.class.name", taskExecutorClassName
				).build()));

		return taskExecutorClassName;
	}

	private void _testExecuteBackgroundTasks(
			BackgroundTaskResult backgroundTaskResult)
		throws Exception {

		String taskExecutorClassName = _registerBackgroundTaskExecutor(
			BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY,
			backgroundTaskResult);

		String lockKey =
			taskExecutorClassName + StringPool.POUND + _user.getCompanyId();

		_lock(lockKey);

		long backgroundTaskId1 = _addQueuedBackgroundTask(
			BackgroundTaskConstants.GROUP_ID_DEFAULT, taskExecutorClassName);
		long backgroundTaskId2 = _addQueuedBackgroundTask(
			BackgroundTaskConstants.GROUP_ID_DEFAULT, taskExecutorClassName);

		_unlock(lockKey);

		_backgroundTaskManager.resumeBackgroundTask(backgroundTaskId1);

		Assert.assertEquals(
			backgroundTaskResult.getStatus(), _getStatus(backgroundTaskId1));
		Assert.assertEquals(
			backgroundTaskResult.getStatus(), _getStatus(backgroundTaskId2));
	}

	private void _unlock(String lockKey) {
		_lockManager.unlock(
			BackgroundTaskExecutor.class.getName(), lockKey, _lockOwner);

		_lockKeys.remove(lockKey);
	}

	private final List<Long> _backgroundTaskIds = new ArrayList<>();

	@Inject
	private BackgroundTaskManager _backgroundTaskManager;

	private final List<Long> _executedBackgroundTaskIds = new ArrayList<>();

	@Inject
	private JSONFactory _jsonFactory;

	private final List<String> _lockKeys = new ArrayList<>();

	@Inject
	private LockManager _lockManager;

	private String _lockOwner;

	@Inject
	private MessageBus _messageBus;

	private final List<ServiceRegistration<BackgroundTaskExecutor>>
		_serviceRegistrations = new ArrayList<>();

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}