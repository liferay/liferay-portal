/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.internal.increment;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.cache.thread.local.Lifecycle;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCacheManager;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.increment.Increment;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.Serializable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Shuyang Zhou
 */
public class BufferedIncrementRunnable implements Runnable {

	public BufferedIncrementRunnable(
		BufferedIncrementConfiguration bufferedIncrementConfiguration,
		BatchablePipe<Serializable, Increment<?>> batchablePipe,
		AtomicInteger queueLengthTracker, Thread dispatchThread) {

		_bufferedIncrementConfiguration = bufferedIncrementConfiguration;
		_batchablePipe = batchablePipe;
		_queueLengthTracker = queueLengthTracker;
		_dispatchThread = dispatchThread;

		if (bufferedIncrementConfiguration.isStandbyEnabled()) {
			_queueLengthTracker.incrementAndGet();
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void run() {
		while (true) {
			BufferedIncreasableEntry bufferedIncreasableEntry =
				(BufferedIncreasableEntry)_batchablePipe.take();

			if (bufferedIncreasableEntry == null) {
				break;
			}

			try {
				bufferedIncreasableEntry.proceed();
			}
			catch (Throwable throwable) {
				_log.error(
					"Unable to persist buffered increment value: " +
						bufferedIncreasableEntry,
					throwable);
			}

			if (!_bufferedIncrementConfiguration.isStandbyEnabled() ||
				!CTCollectionThreadLocal.isProductionMode()) {

				continue;
			}

			int queueLength = _queueLengthTracker.decrementAndGet();

			long standbyTime =
				_bufferedIncrementConfiguration.calculateStandbyTime(
					queueLength);

			try {
				Thread.sleep(standbyTime);
			}
			catch (InterruptedException interruptedException) {
				if (_log.isDebugEnabled()) {
					_log.debug(interruptedException);
				}

				break;
			}
		}

		if (_dispatchThread != Thread.currentThread()) {
			ThreadLocalCacheManager.clearAll(Lifecycle.REQUEST);

			CentralizedThreadLocal.clearShortLivedCentralizedThreadLocals();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BufferedIncrementRunnable.class);

	private final BatchablePipe<Serializable, Increment<?>> _batchablePipe;
	private final BufferedIncrementConfiguration
		_bufferedIncrementConfiguration;
	private final Thread _dispatchThread;
	private final AtomicInteger _queueLengthTracker;

}