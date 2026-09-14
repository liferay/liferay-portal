/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Brittney Nguyen
 */
public class MonitorRunner {

	public static final int THREADS_MAXIMUM = 20;

	public MonitorRunner() {
		this(MonitorConfig.SECONDS_TIMEOUT_DEFAULT * 1000, THREADS_MAXIMUM);
	}

	public MonitorRunner(long defaultTimeoutMillis) {
		this(defaultTimeoutMillis, THREADS_MAXIMUM);
	}

	public MonitorRunner(long defaultTimeoutMillis, int threadCount) {
		if (defaultTimeoutMillis < 1) {
			throw new IllegalArgumentException(
				"Invalid default timeout: " + defaultTimeoutMillis);
		}

		if (threadCount < 1) {
			throw new IllegalArgumentException(
				"Invalid thread count: " + threadCount);
		}

		_defaultTimeoutMillis = defaultTimeoutMillis;
		_threadCount = threadCount;
	}

	public Map<Monitor, MonitorResult> run(Collection<Monitor> monitors) {
		Map<Monitor, MonitorResult> monitorResultsMap = new LinkedHashMap<>();

		if ((monitors == null) || monitors.isEmpty()) {
			return monitorResultsMap;
		}

		ExecutorService executorService = _newExecutorService(_threadCount);

		try {
			long startTimeoutMillis = _getStartTimeoutMillis(monitors);

			long submitTimestamp = System.currentTimeMillis();

			Map<MonitorTask, Future<MonitorResult>> futuresMap =
				new LinkedHashMap<>();

			for (Monitor monitor : monitors) {
				MonitorTask monitorTask = new MonitorTask(monitor);

				futuresMap.put(
					monitorTask, executorService.submit(monitorTask));
			}

			for (Map.Entry<MonitorTask, Future<MonitorResult>> entry :
					futuresMap.entrySet()) {

				MonitorTask monitorTask = entry.getKey();

				monitorResultsMap.put(
					monitorTask.getMonitor(),
					_resolveMonitorResult(
						entry.getValue(), monitorTask, startTimeoutMillis,
						submitTimestamp));
			}
		}
		finally {
			executorService.shutdownNow();
		}

		return monitorResultsMap;
	}

	private long _getStartTimeoutMillis(Collection<Monitor> monitors) {
		int monitorsCount = monitors.size();

		// No monitor waits for a thread at or below the cap, so submission time
		// is start time and the submit baseline is already exact.

		if (monitorsCount <= _threadCount) {
			return 0;
		}

		long maximumTimeoutMillis = 0;

		for (Monitor monitor : monitors) {
			long timeoutMillis = _getTimeoutMillis(monitor);

			if (timeoutMillis > maximumTimeoutMillis) {
				maximumTimeoutMillis = timeoutMillis;
			}
		}

		int batchesCount = ((monitorsCount + _threadCount) - 1) / _threadCount;

		// Every task releases its thread within its own timeout, so the last
		// monitor starts no later than one timeout short of this. The extra
		// batch is slack for the hand-off, since a bound equal to the last
		// legal start races it and reports a healthy monitor as never started.

		long startTimeoutMillis = batchesCount * maximumTimeoutMillis;

		if ((startTimeoutMillis / batchesCount) != maximumTimeoutMillis) {
			return Long.MAX_VALUE;
		}

		return startTimeoutMillis;
	}

	private long _getTimeoutMillis(Monitor monitor) {
		MonitorConfig monitorConfig = monitor.getMonitorConfig();

		long timeoutSeconds = monitorConfig.getTimeoutSeconds();

		if (timeoutSeconds <= 0) {
			return _defaultTimeoutMillis;
		}

		return timeoutSeconds * 1000;
	}

	private ExecutorService _newExecutorService(int threadCount) {
		return Executors.newFixedThreadPool(
			threadCount,
			new ThreadFactory() {

				@Override
				public Thread newThread(Runnable runnable) {
					Thread thread = new Thread(
						runnable,
						"monitor-runner-" + _threadNumber.getAndIncrement());

					thread.setDaemon(true);

					return thread;
				}

				private final AtomicInteger _threadNumber = new AtomicInteger(
					1);

			});
	}

	private MonitorResult _newUnknownMonitorResult(
		long durationMillis, String message) {

		return new MonitorResult(
			durationMillis, message, null, MonitorResult.Status.UNKNOWN,
			JenkinsResultsParserUtil.getCurrentTimeMillis());
	}

	private MonitorResult _resolveMonitorResult(
		Future<MonitorResult> future, MonitorTask monitorTask,
		long startTimeoutMillis, long submitTimestamp) {

		Monitor monitor = monitorTask.getMonitor();

		long timeoutMillis = _getTimeoutMillis(monitor);

		try {
			long baselineTimestamp = submitTimestamp;

			if (startTimeoutMillis > 0) {
				long elapsedMillis =
					System.currentTimeMillis() - submitTimestamp;

				if (!monitorTask.awaitStart(
						startTimeoutMillis - elapsedMillis)) {

					future.cancel(true);

					return _newUnknownMonitorResult(
						MonitorResult.DURATION_MILLIS_UNMEASURED,
						JenkinsResultsParserUtil.combine(
							"Monitor ", monitor.getId(),
							" did not start within ",
							String.valueOf(startTimeoutMillis), " ms"));
				}

				baselineTimestamp = monitorTask.getStartTimestamp();
			}

			long remainingMillis =
				(baselineTimestamp + timeoutMillis) -
					System.currentTimeMillis();

			if (remainingMillis < 0) {
				remainingMillis = 0;
			}

			MonitorResult monitorResult = future.get(
				remainingMillis, TimeUnit.MILLISECONDS);

			long durationMillis = monitorTask.getDurationMillis();

			if (monitorResult == null) {
				return _newUnknownMonitorResult(
					durationMillis,
					JenkinsResultsParserUtil.combine(
						"Monitor ", monitor.getId(), " returned no result"));
			}

			return new MonitorResult(
				durationMillis, monitorResult.getMessage(),
				monitorResult.getMetrics(), monitorResult.getStatus(),
				monitorResult.getTimestamp());
		}
		catch (ExecutionException executionException) {
			Throwable throwable = executionException.getCause();

			return _newUnknownMonitorResult(
				monitorTask.getDurationMillis(),
				JenkinsResultsParserUtil.combine(
					"Monitor ", monitor.getId(), " failed: ",
					JenkinsResultsParserUtil.getMessage(throwable)));
		}
		catch (InterruptedException interruptedException) {
			Thread thread = Thread.currentThread();

			thread.interrupt();

			future.cancel(true);

			return _newUnknownMonitorResult(
				MonitorResult.DURATION_MILLIS_UNMEASURED,
				JenkinsResultsParserUtil.combine(
					"Monitor ", monitor.getId(), " was interrupted"));
		}
		catch (TimeoutException timeoutException) {
			future.cancel(true);

			return _newUnknownMonitorResult(
				monitorTask.getElapsedMillis(),
				JenkinsResultsParserUtil.combine(
					"Monitor ", monitor.getId(), " timed out after ",
					String.valueOf(timeoutMillis), " ms"));
		}
	}

	private final long _defaultTimeoutMillis;
	private final int _threadCount;

	private static class MonitorTask implements Callable<MonitorResult> {

		public MonitorTask(Monitor monitor) {
			_monitor = monitor;
		}

		public boolean awaitStart(long timeoutMillis)
			throws InterruptedException {

			return _startCountDownLatch.await(
				timeoutMillis, TimeUnit.MILLISECONDS);
		}

		@Override
		public MonitorResult call() {
			_startNanoTime = System.nanoTime();
			_startTimestamp = System.currentTimeMillis();

			_startCountDownLatch.countDown();

			try {
				return _monitor.execute();
			}
			finally {
				_endNanoTime = System.nanoTime();

				_completed = true;
			}
		}

		public long getDurationMillis() {
			if (!_completed) {
				return MonitorResult.DURATION_MILLIS_UNMEASURED;
			}

			return TimeUnit.NANOSECONDS.toMillis(_endNanoTime - _startNanoTime);
		}

		public long getElapsedMillis() {
			if (_startTimestamp == 0) {
				return MonitorResult.DURATION_MILLIS_UNMEASURED;
			}

			return TimeUnit.NANOSECONDS.toMillis(
				System.nanoTime() - _startNanoTime);
		}

		public Monitor getMonitor() {
			return _monitor;
		}

		public long getStartTimestamp() {
			return _startTimestamp;
		}

		private volatile boolean _completed;
		private volatile long _endNanoTime;
		private final Monitor _monitor;
		private final CountDownLatch _startCountDownLatch = new CountDownLatch(
			1);
		private volatile long _startNanoTime;
		private volatile long _startTimestamp;

	}

}