/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.internal;

import com.liferay.portal.background.task.internal.lock.helper.BackgroundTaskLockHelper;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutorRegistry;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.DelegatingBackgroundTaskExecutor;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManager;

/**
 * @author Michael C. Han
 */
public class SerialBackgroundTaskExecutor
	extends DelegatingBackgroundTaskExecutor {

	public SerialBackgroundTaskExecutor(
		BackgroundTaskExecutor backgroundTaskExecutor,
		BackgroundTaskExecutorRegistry backgroundTaskExecutorRegistry,
		LockManager lockManager) {

		super(backgroundTaskExecutor);

		_backgroundTaskExecutorRegistry = backgroundTaskExecutorRegistry;
		_lockManager = lockManager;

		_backgroundTaskLockHelper = new BackgroundTaskLockHelper(
			backgroundTaskExecutorRegistry, lockManager);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return new SerialBackgroundTaskExecutor(
			getBackgroundTaskExecutor(), _backgroundTaskExecutorRegistry,
			_lockManager);
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		Lock lock = null;

		try {
			if (isSerial()) {
				lock = _acquireLock(backgroundTask);
			}

			BackgroundTaskExecutor backgroundTaskExecutor =
				getBackgroundTaskExecutor();

			return backgroundTaskExecutor.execute(backgroundTask);
		}
		finally {
			if (lock != null) {
				_backgroundTaskLockHelper.unlockBackgroundTask(backgroundTask);
			}
		}
	}

	private Lock _acquireLock(BackgroundTask backgroundTask)
		throws DuplicateLockException {

		Lock lock = _backgroundTaskLockHelper.lockBackgroundTask(
			backgroundTask);

		if (!lock.isNew()) {
			throw new DuplicateLockException(lock);
		}

		return lock;
	}

	private final BackgroundTaskExecutorRegistry
		_backgroundTaskExecutorRegistry;
	private final BackgroundTaskLockHelper _backgroundTaskLockHelper;
	private final LockManager _lockManager;

}