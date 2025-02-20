/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.messaging.internal;

import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.petra.concurrent.NoticeableThreadPoolExecutor;
import com.liferay.petra.concurrent.ThreadPoolHandlerAdapter;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseDestination;
import com.liferay.portal.kernel.messaging.DestinationStatistics;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageRunnable;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.NamedThreadFactory;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Michael C. Han
 * @author Shuyang Zhou
 */
public abstract class BaseAsyncDestination extends BaseDestination {

	@Override
	public void close(boolean force) {
		if ((_noticeableThreadPoolExecutor == null) ||
			_noticeableThreadPoolExecutor.isShutdown()) {

			return;
		}

		if (force) {
			_noticeableThreadPoolExecutor.shutdownNow();
		}
		else {
			_noticeableThreadPoolExecutor.shutdown();
		}
	}

	@Override
	public DestinationStatistics getDestinationStatistics() {
		DestinationStatistics destinationStatistics =
			new DestinationStatistics();

		destinationStatistics.setActiveThreadCount(
			_noticeableThreadPoolExecutor.getActiveCount());
		destinationStatistics.setCurrentThreadCount(
			_noticeableThreadPoolExecutor.getPoolSize());
		destinationStatistics.setLargestThreadCount(
			_noticeableThreadPoolExecutor.getLargestPoolSize());
		destinationStatistics.setMaxThreadPoolSize(
			_noticeableThreadPoolExecutor.getMaximumPoolSize());
		destinationStatistics.setMinThreadPoolSize(
			_noticeableThreadPoolExecutor.getCorePoolSize());
		destinationStatistics.setPendingMessageCount(
			_noticeableThreadPoolExecutor.getPendingTaskCount());
		destinationStatistics.setRejectedMessageCount(
			_rejectedTaskCounter.get());
		destinationStatistics.setSentMessageCount(
			_noticeableThreadPoolExecutor.getCompletedTaskCount());

		return destinationStatistics;
	}

	public int getMaximumQueueSize() {
		return _maximumQueueSize;
	}

	public int getWorkersCoreSize() {
		return _workersCoreSize;
	}

	public int getWorkersMaxSize() {
		return _workersMaxSize;
	}

	@Override
	public void open() {
		if ((_noticeableThreadPoolExecutor != null) &&
			!_noticeableThreadPoolExecutor.isShutdown()) {

			return;
		}

		if (_rejectedExecutionHandler == null) {
			_rejectedExecutionHandler = _createRejectionExecutionHandler();
		}
		else {
			_rejectedTaskCounter.set(0);
		}

		NoticeableThreadPoolExecutor noticeableThreadPoolExecutor =
			new NoticeableThreadPoolExecutor(
				_workersCoreSize, _workersMaxSize, 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(_maximumQueueSize),
				new NamedThreadFactory(
					getName(), Thread.NORM_PRIORITY,
					PortalClassLoaderUtil.getClassLoader()),
				_rejectedExecutionHandler, new ThreadPoolHandlerAdapter());

		NoticeableExecutorService oldNoticeableExecutorService =
			_portalExecutorManager.registerPortalExecutor(
				getName(), noticeableThreadPoolExecutor);

		if (oldNoticeableExecutorService != null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Abort creating a new thread pool for destination " +
						getName() + " and reuse previous one");
			}

			noticeableThreadPoolExecutor.shutdownNow();

			noticeableThreadPoolExecutor =
				(NoticeableThreadPoolExecutor)oldNoticeableExecutorService;
		}

		_noticeableThreadPoolExecutor = noticeableThreadPoolExecutor;
	}

	@Override
	public void send(Message message) {
		List<MessageListener> messageListeners =
			messageListenerRegistry.getMessageListeners(name);

		if (messageListeners.isEmpty()) {
			if (_log.isDebugEnabled()) {
				_log.debug("No message listeners for destination " + getName());
			}

			return;
		}

		NoticeableThreadPoolExecutor noticeableThreadPoolExecutor =
			_noticeableThreadPoolExecutor;

		if (noticeableThreadPoolExecutor.isShutdown()) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Destination ", getName(), " is shutdown and cannot ",
					"receive more messages"));
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Sending message ", message, " from destination ",
					getName(), " to message listeners ", messageListeners));
		}

		dispatch(messageListeners, message);
	}

	public void setMaximumQueueSize(int maximumQueueSize) {
		_maximumQueueSize = maximumQueueSize;
	}

	public void setPermissionCheckerFactory(
		PermissionCheckerFactory permissionCheckerFactory) {

		this.permissionCheckerFactory = permissionCheckerFactory;
	}

	public void setPortalExecutorManager(
		PortalExecutorManager portalExecutorManager) {

		_portalExecutorManager = portalExecutorManager;
	}

	public void setRejectedExecutionHandler(
		RejectedExecutionHandler rejectedExecutionHandler) {

		_rejectedExecutionHandler = (runnable, threadPoolExecutor) -> {
			_rejectedTaskCounter.incrementAndGet();

			rejectedExecutionHandler.rejectedExecution(
				runnable, threadPoolExecutor);
		};
	}

	public void setUserLocalService(UserLocalService userLocalService) {
		this.userLocalService = userLocalService;
	}

	/**
	 *   @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *          #setWorkersSize(int, int)}
	 */
	@Deprecated
	public void setWorkersCoreSize(int workersCoreSize) {
		_workersCoreSize = workersCoreSize;

		if (_noticeableThreadPoolExecutor != null) {
			_noticeableThreadPoolExecutor.setCorePoolSize(workersCoreSize);
		}
	}

	/**
	 *   @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *          #setWorkersSize(int, int)}
	 */
	@Deprecated
	public void setWorkersMaxSize(int workersMaxSize) {
		_workersMaxSize = workersMaxSize;

		if (_noticeableThreadPoolExecutor != null) {
			_noticeableThreadPoolExecutor.setMaximumPoolSize(workersMaxSize);
		}
	}

	public void setWorkersSize(int workersCoreSize, int workersMaxSize) {
		if (workersCoreSize < 1) {
			throw new IllegalArgumentException(
				"To ensure FIFO, core pool size must be 1 or greater");
		}
		else if ((workersMaxSize <= 0) || (workersMaxSize < workersCoreSize)) {
			throw new IllegalArgumentException(
				"Maximum pool size must be greater than 0 and core pool size");
		}

		_workersCoreSize = workersCoreSize;
		_workersMaxSize = workersMaxSize;

		if (_noticeableThreadPoolExecutor != null) {
			_noticeableThreadPoolExecutor.setCorePoolSize(workersCoreSize);

			// Invoke setMaximumPoolSize after setCorePoolSize. See LPS-124209.

			_noticeableThreadPoolExecutor.setMaximumPoolSize(workersMaxSize);
		}
	}

	protected abstract void dispatch(
		List<MessageListener> messageListeners, Message message);

	protected void execute(Runnable runnable) {
		_noticeableThreadPoolExecutor.execute(runnable);
	}

	protected PermissionCheckerFactory permissionCheckerFactory;
	protected UserLocalService userLocalService;

	private RejectedExecutionHandler _createRejectionExecutionHandler() {
		return new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(
				Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

				_rejectedTaskCounter.incrementAndGet();

				if (!_log.isWarnEnabled()) {
					return;
				}

				MessageRunnable messageRunnable = (MessageRunnable)runnable;

				_log.warn(
					StringBundler.concat(
						"Discarding message ", messageRunnable.getMessage(),
						" because it exceeds the maximum queue size of ",
						_maximumQueueSize));
			}

		};
	}

	private static final int _WORKERS_CORE_SIZE = 2;

	private static final int _WORKERS_MAX_SIZE = 5;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAsyncDestination.class);

	private int _maximumQueueSize = Integer.MAX_VALUE;
	private NoticeableThreadPoolExecutor _noticeableThreadPoolExecutor;
	private PortalExecutorManager _portalExecutorManager;
	private RejectedExecutionHandler _rejectedExecutionHandler;
	private final AtomicLong _rejectedTaskCounter = new AtomicLong();
	private int _workersCoreSize = _WORKERS_CORE_SIZE;
	private int _workersMaxSize = _WORKERS_MAX_SIZE;

}