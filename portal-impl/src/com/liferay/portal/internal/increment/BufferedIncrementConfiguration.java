/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.internal.increment;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

/**
 * @author Shuyang Zhou
 */
public class BufferedIncrementConfiguration {

	public BufferedIncrementConfiguration(String configuration) {
		Filter filter = new Filter(configuration);

		_standbyQueueThreshold = GetterUtil.getInteger(
			PropsUtil.get(
				PropsKeys.BUFFERED_INCREMENT_STANDBY_QUEUE_THRESHOLD, filter));
		_standbyTimeUpperLimit = GetterUtil.getLong(
			PropsUtil.get(
				PropsKeys.BUFFERED_INCREMENT_STANDBY_TIME_UPPER_LIMIT, filter));

		long threadpoolKeepAliveTime = GetterUtil.getLong(
			PropsUtil.get(
				PropsKeys.BUFFERED_INCREMENT_THREADPOOL_KEEP_ALIVE_TIME,
				filter));

		if (threadpoolKeepAliveTime < 0) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						PropsKeys.BUFFERED_INCREMENT_THREADPOOL_KEEP_ALIVE_TIME,
						"[", configuration, "]=", threadpoolKeepAliveTime,
						". Auto reset to 0."));
			}

			threadpoolKeepAliveTime = 0;
		}

		_threadpoolKeepAliveTime = threadpoolKeepAliveTime;

		int threadpoolMaxSize = GetterUtil.getInteger(
			PropsUtil.get(
				PropsKeys.BUFFERED_INCREMENT_THREADPOOL_MAX_SIZE, filter));

		if (threadpoolMaxSize < 1) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						PropsKeys.BUFFERED_INCREMENT_THREADPOOL_MAX_SIZE, "[",
						configuration, "]=", threadpoolMaxSize,
						". Auto reset to 1."));
			}

			threadpoolMaxSize = 1;
		}

		_threadpoolMaxSize = threadpoolMaxSize;

		if ((_standbyQueueThreshold > 0) && (_standbyTimeUpperLimit > 0)) {
			_standbyEnabled = true;
		}
		else {
			_standbyEnabled = false;
		}
	}

	public long calculateStandbyTime(int queueLength) {
		if (queueLength < 0) {
			throw new IllegalArgumentException(
				"Negative queue length " + queueLength);
		}

		if (!_standbyEnabled) {
			throw new IllegalStateException("Standby is disabled");
		}

		if (queueLength > _standbyQueueThreshold) {
			return 0;
		}

		return (_standbyQueueThreshold - queueLength) * _standbyTimeUpperLimit *
			1000 / _standbyQueueThreshold;
	}

	public int getStandbyQueueThreshold() {
		return _standbyQueueThreshold;
	}

	public long getStandbyTimeUpperLimit() {
		return _standbyTimeUpperLimit;
	}

	public long getThreadpoolKeepAliveTime() {
		return _threadpoolKeepAliveTime;
	}

	public int getThreadpoolMaxSize() {
		return _threadpoolMaxSize;
	}

	public boolean isStandbyEnabled() {
		return _standbyEnabled;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BufferedIncrementConfiguration.class);

	private final boolean _standbyEnabled;
	private final int _standbyQueueThreshold;
	private final long _standbyTimeUpperLimit;
	private final long _threadpoolKeepAliveTime;
	private final int _threadpoolMaxSize;

}