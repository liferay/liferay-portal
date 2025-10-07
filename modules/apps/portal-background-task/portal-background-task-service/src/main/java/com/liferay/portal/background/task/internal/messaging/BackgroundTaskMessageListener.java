/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal.messaging;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.background.task.internal.BackgroundTaskImpl;
import com.liferay.portal.background.task.internal.BackgroundTaskInExecutionUtil;
import com.liferay.portal.background.task.internal.SerialBackgroundTaskExecutor;
import com.liferay.portal.background.task.internal.ThreadLocalAwareBackgroundTaskExecutor;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutorRegistry;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistry;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocal;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskThreadLocalManager;
import com.liferay.portal.kernel.backgroundtask.ClassLoaderAwareBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.ServletContextClassLoaderPool;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael C. Han
 */
public class BackgroundTaskMessageListener extends BaseMessageListener {

	public BackgroundTaskMessageListener(
		BackgroundTaskExecutorRegistry backgroundTaskExecutorRegistry,
		BackgroundTaskLocalService backgroundTaskLocalService,
		BackgroundTaskStatusRegistry backgroundTaskStatusRegistry,
		BackgroundTaskThreadLocalManager backgroundTaskThreadLocalManager,
		LockManager lockManager, MessageBus messageBus) {

		_backgroundTaskExecutorRegistry = backgroundTaskExecutorRegistry;
		_backgroundTaskLocalService = backgroundTaskLocalService;
		_backgroundTaskStatusRegistry = backgroundTaskStatusRegistry;
		_backgroundTaskThreadLocalManager = backgroundTaskThreadLocalManager;
		_lockManager = lockManager;
		_messageBus = messageBus;
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		long backgroundTaskId = (Long)message.get(
			BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID);

		try (SafeCloseable safeCloseable1 =
				BackgroundTaskThreadLocal.setBackgroundTaskIdWithSafeCloseable(
					backgroundTaskId);
			SafeCloseable safeCloseable2 =
				BackgroundTaskInExecutionUtil.setInExecutionWithSafeCloseable(
					backgroundTaskId)) {

			ServiceContext serviceContext = new ServiceContext();

			BackgroundTask backgroundTask =
				_backgroundTaskLocalService.amendBackgroundTask(
					backgroundTaskId, null,
					BackgroundTaskConstants.STATUS_IN_PROGRESS, serviceContext);

			if (backgroundTask == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to find background task " + backgroundTaskId);
				}

				return;
			}

			BackgroundTaskExecutor backgroundTaskExecutor = null;

			int status = backgroundTask.getStatus();
			String statusMessage = null;

			try {
				ClassLoader classLoader = _getBackgroundTaskExecutorClassLoader(
					backgroundTask);

				backgroundTaskExecutor = _wrapBackgroundTaskExecutor(
					backgroundTask, classLoader);

				_backgroundTaskStatusRegistry.registerBackgroundTaskStatus(
					backgroundTaskId,
					backgroundTaskExecutor.
						getBackgroundTaskStatusMessageTranslator());

				backgroundTask =
					_backgroundTaskLocalService.fetchBackgroundTask(
						backgroundTask.getBackgroundTaskId());

				BackgroundTaskResult backgroundTaskResult =
					backgroundTaskExecutor.execute(
						new BackgroundTaskImpl(backgroundTask));

				status = backgroundTaskResult.getStatus();
				statusMessage = backgroundTaskResult.getStatusMessage();
			}
			catch (DuplicateLockException duplicateLockException) {
				status = BackgroundTaskConstants.STATUS_QUEUED;

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to acquire lock, queuing background task " +
							backgroundTaskId,
						duplicateLockException);
				}
				else if (_log.isInfoEnabled()) {
					_log.info(
						"Unable to acquire lock, queuing background task " +
							backgroundTaskId);
				}
			}
			catch (Exception exception) {
				status = BackgroundTaskConstants.STATUS_FAILED;

				if (exception instanceof SystemException) {
					Throwable throwable = exception.getCause();

					if (throwable instanceof Exception) {
						exception = (Exception)throwable;
					}
				}

				if (backgroundTaskExecutor != null) {
					statusMessage = backgroundTaskExecutor.handleException(
						new BackgroundTaskImpl(backgroundTask), exception);
				}

				if (_log.isInfoEnabled()) {
					if (statusMessage != null) {
						statusMessage = statusMessage.concat(
							StackTraceUtil.getStackTrace(exception));
					}
					else {
						statusMessage = StackTraceUtil.getStackTrace(exception);
					}
				}

				_log.error("Unable to execute background task", exception);
			}
			finally {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Completing background task ", backgroundTaskId,
							" with status: ", status));
				}

				_backgroundTaskLocalService.amendBackgroundTask(
					backgroundTaskId, null, status, statusMessage,
					serviceContext);

				_backgroundTaskStatusRegistry.unregisterBackgroundTaskStatus(
					backgroundTaskId);

				Message responseMessage = new Message();

				responseMessage.put(
					BackgroundTaskConstants.MESSAGE_KEY_BACKGROUND_TASK_ID,
					backgroundTask.getBackgroundTaskId());
				responseMessage.put("companyId", backgroundTask.getCompanyId());
				responseMessage.put("name", backgroundTask.getName());
				responseMessage.put("status", status);
				responseMessage.put(
					"taskExecutorClassName",
					backgroundTask.getTaskExecutorClassName());

				_messageBus.sendMessage(
					DestinationNames.BACKGROUND_TASK_STATUS, responseMessage);
			}
		}
	}

	private ClassLoader _getAggregatePluginsClassLoader(
		String servletContextNamesString) {

		String[] servletContextNames = StringUtil.split(
			servletContextNamesString);

		List<ClassLoader> classLoaders = new ArrayList<>(
			servletContextNames.length);

		for (String servletContextName : servletContextNames) {
			ClassLoader classLoader =
				ServletContextClassLoaderPool.getClassLoader(
					servletContextName);

			if (classLoader == null) {
				_log.error(
					"Unable to find class loader for servlet context " +
						servletContextName);
			}
			else {
				classLoaders.add(classLoader);
			}
		}

		return AggregateClassLoader.getAggregateClassLoader(
			classLoaders.toArray(new ClassLoader[0]));
	}

	private BackgroundTaskExecutor _getBackgroundTaskExecutor(
		BackgroundTask backgroundTask) {

		BackgroundTaskExecutor backgroundTaskExecutor = null;

		String servletContextNames = backgroundTask.getServletContextNames();

		if (Validator.isNull(servletContextNames)) {
			backgroundTaskExecutor =
				_backgroundTaskExecutorRegistry.getBackgroundTaskExecutor(
					backgroundTask.getTaskExecutorClassName());

			if (backgroundTaskExecutor == null) {
				throw new IllegalStateException(
					"Unknown background task executor " +
						backgroundTask.getTaskExecutorClassName());
			}

			backgroundTaskExecutor = backgroundTaskExecutor.clone();
		}
		else {
			ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

			if (Validator.isNotNull(servletContextNames)) {
				classLoader = _getAggregatePluginsClassLoader(
					servletContextNames);
			}

			try {
				backgroundTaskExecutor =
					(BackgroundTaskExecutor)InstanceFactory.newInstance(
						classLoader, backgroundTask.getTaskExecutorClassName());
			}
			catch (Exception exception) {
				throw new IllegalStateException(
					"Cannot instantiate BackgroundTaskExecutor: " +
						backgroundTask.getTaskExecutorClassName(),
					exception);
			}
		}

		return backgroundTaskExecutor;
	}

	private ClassLoader _getBackgroundTaskExecutorClassLoader(
		BackgroundTask backgroundTask) {

		if (Validator.isNull(backgroundTask.getServletContextNames())) {
			return null;
		}

		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		String servletContextNames = backgroundTask.getServletContextNames();

		if (Validator.isNotNull(servletContextNames)) {
			classLoader = _getAggregatePluginsClassLoader(servletContextNames);
		}

		return classLoader;
	}

	private BackgroundTaskExecutor _wrapBackgroundTaskExecutor(
		BackgroundTask backgroundTask, ClassLoader classLoader) {

		BackgroundTaskExecutor backgroundTaskExecutor =
			_getBackgroundTaskExecutor(backgroundTask);

		if (classLoader != null) {
			backgroundTaskExecutor = new ClassLoaderAwareBackgroundTaskExecutor(
				backgroundTaskExecutor, classLoader);
		}

		if (backgroundTaskExecutor.isSerial()) {
			backgroundTaskExecutor = new SerialBackgroundTaskExecutor(
				backgroundTaskExecutor, _lockManager);
		}

		return new ThreadLocalAwareBackgroundTaskExecutor(
			backgroundTaskExecutor, _backgroundTaskThreadLocalManager);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BackgroundTaskMessageListener.class);

	private final BackgroundTaskExecutorRegistry
		_backgroundTaskExecutorRegistry;
	private final BackgroundTaskLocalService _backgroundTaskLocalService;
	private final BackgroundTaskStatusRegistry _backgroundTaskStatusRegistry;
	private final BackgroundTaskThreadLocalManager
		_backgroundTaskThreadLocalManager;
	private final LockManager _lockManager;
	private final MessageBus _messageBus;

}