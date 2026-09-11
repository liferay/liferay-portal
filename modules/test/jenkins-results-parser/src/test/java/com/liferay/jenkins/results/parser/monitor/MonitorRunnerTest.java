/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class MonitorRunnerTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testMonitorRunnerDefaultTimeoutMillis() {
		new MonitorRunner(1);

		try {
			new MonitorRunner(0);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	@Test
	public void testMonitorRunnerThreadCount() {
		new MonitorRunner(1, 1);

		try {
			new MonitorRunner(1, 0);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	@Test(timeout = 5000)
	public void testRun() {
		MonitorRunner monitorRunner = new MonitorRunner();

		TestMonitor testMonitor1 = new TestMonitor(_newMonitorConfig("a"));
		TestMonitor testMonitor2 = new TestMonitor(_newMonitorConfig("b"));

		List<Monitor> monitors = Arrays.<Monitor>asList(
			testMonitor1, testMonitor2);

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			monitors);

		testEquals(monitors, new ArrayList<>(monitorResultsMap.keySet()));

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor1);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		monitorResult = monitorResultsMap.get(testMonitor2);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test(timeout = 5000)
	public void testRunConcurrentMonitors() {
		MonitorRunner monitorRunner = new MonitorRunner(60 * 1000);

		CountDownLatch countDownLatch = new CountDownLatch(2);

		TestMonitor testMonitor1 = _newConcurrentTestMonitor(
			countDownLatch, _newMonitorConfig("a"));
		TestMonitor testMonitor2 = _newConcurrentTestMonitor(
			countDownLatch, _newMonitorConfig("b"));

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Arrays.<Monitor>asList(testMonitor1, testMonitor2));

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor1);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		monitorResult = monitorResultsMap.get(testMonitor2);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test(timeout = 5000)
	public void testRunDuplicateIds() {
		MonitorRunner monitorRunner = new MonitorRunner();

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Arrays.<Monitor>asList(
				new TestMonitor(_newMonitorConfig("a")),
				new TestMonitor(_newMonitorConfig("a"))));

		testEquals(2, monitorResultsMap.size());
	}

	@Test(timeout = 5000)
	public void testRunDurationMillis() {
		MonitorRunner monitorRunner = new MonitorRunner(5000);

		TestMonitor testMonitor = _newSleepingTestMonitor(
			200, _newMonitorConfig(RandomTestUtil.randomString()));

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Collections.<Monitor>singletonList(testMonitor));

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor);

		Assert.assertTrue(
			String.valueOf(monitorResult.getDurationMillis()),
			monitorResult.getDurationMillis() >= 200);
		Assert.assertTrue(
			String.valueOf(monitorResult.getDurationMillis()),
			monitorResult.getDurationMillis() < 5000);
	}

	@Test(timeout = 5000)
	public void testRunDurationMillisBehindSlowMonitor() {
		MonitorRunner monitorRunner = new MonitorRunner(5000);

		TestMonitor slowTestMonitor = _newSleepingTestMonitor(
			600, _newMonitorConfig(RandomTestUtil.randomString()));
		TestMonitor fastTestMonitor = new TestMonitor(
			_newMonitorConfig(RandomTestUtil.randomString()));

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Arrays.<Monitor>asList(slowTestMonitor, fastTestMonitor));

		MonitorResult monitorResult = monitorResultsMap.get(fastTestMonitor);

		Assert.assertTrue(
			String.valueOf(monitorResult.getDurationMillis()),
			monitorResult.getDurationMillis() < 300);

		monitorResult = monitorResultsMap.get(slowTestMonitor);

		Assert.assertTrue(
			String.valueOf(monitorResult.getDurationMillis()),
			monitorResult.getDurationMillis() >= 600);
	}

	@Test(timeout = 5000)
	public void testRunDurationMillisOnTimeout() {
		MonitorRunner monitorRunner = new MonitorRunner(300);

		TestMonitor shortTimeoutTestMonitor = new HangingTestMonitor(
			null, _newMonitorConfig(RandomTestUtil.randomString()));
		TestMonitor longTimeoutTestMonitor = new HangingTestMonitor(
			null, _newMonitorConfig(RandomTestUtil.randomString(), 1));

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Arrays.<Monitor>asList(
				longTimeoutTestMonitor, shortTimeoutTestMonitor));

		MonitorResult monitorResult = monitorResultsMap.get(
			shortTimeoutTestMonitor);

		Assert.assertTrue(
			String.valueOf(monitorResult.getDurationMillis()),
			monitorResult.getDurationMillis() >= 600);
	}

	@Test(timeout = 5000)
	public void testRunEmpty() {
		MonitorRunner monitorRunner = new MonitorRunner();

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Collections.<Monitor>emptyList());

		Assert.assertTrue(monitorResultsMap.isEmpty());

		monitorResultsMap = monitorRunner.run(null);

		Assert.assertTrue(monitorResultsMap.isEmpty());
	}

	@Test(timeout = 5000)
	public void testRunHangingMonitor() {
		MonitorRunner monitorRunner = new MonitorRunner(100);

		TestMonitor testMonitor = new HangingTestMonitor(
			null, _newMonitorConfig("a"));

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Collections.<Monitor>singletonList(testMonitor));

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor);

		testEquals(
			"Monitor a timed out after 100 ms", monitorResult.getMessage());
		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
	}

	@Test(timeout = 5000)
	public void testRunHangingMonitorCancellation() throws Exception {
		MonitorRunner monitorRunner = new MonitorRunner(100);

		CountDownLatch countDownLatch = new CountDownLatch(1);

		monitorRunner.run(
			Collections.<Monitor>singletonList(
				new HangingTestMonitor(
					countDownLatch, _newMonitorConfig("a"))));

		Assert.assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
	}

	@Test(timeout = 5000)
	public void testRunHangingMonitorWithPassingMonitors() {
		MonitorRunner monitorRunner = new MonitorRunner(100);

		TestMonitor testMonitor1 = new HangingTestMonitor(
			null, _newMonitorConfig("a"));
		TestMonitor testMonitor2 = new TestMonitor(_newMonitorConfig("b"));
		TestMonitor testMonitor3 = new TestMonitor(_newMonitorConfig("c"));

		long startTimestamp = System.currentTimeMillis();

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Arrays.<Monitor>asList(testMonitor1, testMonitor2, testMonitor3));

		Assert.assertTrue((System.currentTimeMillis() - startTimestamp) < 2000);

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor1);

		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());

		monitorResult = monitorResultsMap.get(testMonitor2);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		monitorResult = monitorResultsMap.get(testMonitor3);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test(timeout = 5000)
	public void testRunInterrupted() {
		MonitorRunner monitorRunner = new MonitorRunner();

		TestMonitor testMonitor = new TestMonitor(_newMonitorConfig("a"));

		Thread thread = Thread.currentThread();

		thread.interrupt();

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Collections.<Monitor>singletonList(testMonitor));

		Assert.assertTrue(Thread.interrupted());

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor);

		testEquals("Monitor a was interrupted", monitorResult.getMessage());
		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
	}

	@Test(timeout = 5000)
	public void testRunInterruptedWhileQueued() {
		MonitorRunner monitorRunner = new MonitorRunner(1000, 2);

		List<Monitor> monitors = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			monitors.add(
				new TestMonitor(
					_newMonitorConfig(RandomTestUtil.randomString())));
		}

		Thread thread = Thread.currentThread();

		thread.interrupt();

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			monitors);

		Assert.assertTrue(Thread.interrupted());

		for (Map.Entry<Monitor, MonitorResult> entry :
				monitorResultsMap.entrySet()) {

			Monitor monitor = entry.getKey();

			MonitorResult monitorResult = entry.getValue();

			testEquals(
				"Monitor " + monitor.getId() + " was interrupted",
				monitorResult.getMessage());
			testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
		}
	}

	@Test(timeout = 5000)
	public void testRunMonitorConfigTimeout() {
		MonitorRunner monitorRunner = new MonitorRunner();

		TestMonitor testMonitor = new HangingTestMonitor(
			null, _newMonitorConfig("a", 1));

		long startTimestamp = System.currentTimeMillis();

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Collections.<Monitor>singletonList(testMonitor));

		Assert.assertTrue((System.currentTimeMillis() - startTimestamp) < 3000);

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor);

		testEquals(
			"Monitor a timed out after 1000 ms", monitorResult.getMessage());
		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
	}

	@Test(timeout = 5000)
	public void testRunMonitorConfigTimeoutMaximum() {
		MonitorRunner monitorRunner = new MonitorRunner();

		TestMonitor testMonitor = new TestMonitor(
			_newMonitorConfig("a", Long.MAX_VALUE)) {

			@Override
			public MonitorResult execute() {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException interruptedException) {
					throw new RuntimeException(interruptedException);
				}

				return super.execute();
			}

		};

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Collections.<Monitor>singletonList(testMonitor));

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test(timeout = 5000)
	public void testRunMonitorWithoutAvailableThread() {
		MonitorRunner monitorRunner = new MonitorRunner(300);

		CountDownLatch releaseCountDownLatch = new CountDownLatch(1);
		CountDownLatch startCountDownLatch = new CountDownLatch(
			MonitorRunner.THREADS_MAXIMUM);

		List<Monitor> monitors = new ArrayList<>();

		for (int i = 0; i < MonitorRunner.THREADS_MAXIMUM; i++) {
			monitors.add(
				new BlockingTestMonitor(
					releaseCountDownLatch, startCountDownLatch,
					_newMonitorConfig(RandomTestUtil.randomString())));
		}

		TestMonitor testMonitor = new TestMonitor(_newMonitorConfig("a"));

		monitors.add(testMonitor);

		try {
			Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
				monitors);

			testEquals(0L, startCountDownLatch.getCount());

			Monitor blockedMonitor = monitors.get(0);

			MonitorResult monitorResult = monitorResultsMap.get(blockedMonitor);

			testEquals(
				"Monitor " + blockedMonitor.getId() + " timed out after 300 ms",
				monitorResult.getMessage());

			monitorResult = monitorResultsMap.get(testMonitor);

			testEquals(
				"Monitor a did not start within 600 ms",
				monitorResult.getMessage());
			testEquals(
				MonitorResult.DURATION_MILLIS_UNMEASURED,
				monitorResult.getDurationMillis());
			testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
		}
		finally {
			releaseCountDownLatch.countDown();
		}
	}

	@Test(timeout = 5000)
	public void testRunMonitorWithoutAvailableThreadMultipleBatches() {
		MonitorRunner monitorRunner = new MonitorRunner(300, 1);

		CountDownLatch releaseCountDownLatch = new CountDownLatch(1);
		CountDownLatch startCountDownLatch = new CountDownLatch(1);

		List<Monitor> monitors = new ArrayList<>();

		monitors.add(
			new BlockingTestMonitor(
				releaseCountDownLatch, startCountDownLatch,
				_newMonitorConfig(RandomTestUtil.randomString())));

		TestMonitor testMonitor1 = new TestMonitor(_newMonitorConfig("a"));
		TestMonitor testMonitor2 = new TestMonitor(_newMonitorConfig("b"));

		monitors.add(testMonitor1);
		monitors.add(testMonitor2);

		try {
			Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
				monitors);

			testEquals(0L, startCountDownLatch.getCount());

			MonitorResult monitorResult = monitorResultsMap.get(testMonitor1);

			testEquals(
				"Monitor a did not start within 900 ms",
				monitorResult.getMessage());

			monitorResult = monitorResultsMap.get(testMonitor2);

			testEquals(
				"Monitor b did not start within 900 ms",
				monitorResult.getMessage());
		}
		finally {
			releaseCountDownLatch.countDown();
		}
	}

	@Test(timeout = 5000)
	public void testRunMoreMonitorsThanThreads() {
		MonitorRunner monitorRunner = new MonitorRunner(1000, 2);

		TestMonitor testMonitor1 = new HangingTestMonitor(
			null, _newMonitorConfig("a"));
		TestMonitor testMonitor2 = new HangingTestMonitor(
			null, _newMonitorConfig("b"));
		TestMonitor testMonitor3 = _newSleepingTestMonitor(
			300, _newMonitorConfig(RandomTestUtil.randomString()));

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Arrays.<Monitor>asList(testMonitor1, testMonitor2, testMonitor3));

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor1);

		testEquals(
			"Monitor a timed out after 1000 ms", monitorResult.getMessage());

		monitorResult = monitorResultsMap.get(testMonitor2);

		testEquals(
			"Monitor b timed out after 1000 ms", monitorResult.getMessage());

		monitorResult = monitorResultsMap.get(testMonitor3);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test(timeout = 5000)
	public void testRunMultipleHangingMonitors() {
		MonitorRunner monitorRunner = new MonitorRunner(200);

		List<Monitor> monitors = Arrays.<Monitor>asList(
			new HangingTestMonitor(null, _newMonitorConfig("a")),
			new HangingTestMonitor(null, _newMonitorConfig("b")),
			new HangingTestMonitor(null, _newMonitorConfig("c")),
			new HangingTestMonitor(null, _newMonitorConfig("d")));

		long startTimestamp = System.currentTimeMillis();

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			monitors);

		Assert.assertTrue((System.currentTimeMillis() - startTimestamp) < 600);

		for (MonitorResult monitorResult : monitorResultsMap.values()) {
			testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
		}
	}

	@Test(timeout = 5000)
	public void testRunNullResultMonitor() {
		MonitorRunner monitorRunner = new MonitorRunner();

		TestMonitor testMonitor = new TestMonitor(_newMonitorConfig("a")) {

			@Override
			public MonitorResult execute() {
				return null;
			}

		};

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Collections.<Monitor>singletonList(testMonitor));

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor);

		testEquals("Monitor a returned no result", monitorResult.getMessage());
		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
	}

	@Test(timeout = 5000)
	public void testRunThreadCountMaximum() {
		MonitorRunner monitorRunner = new MonitorRunner(60 * 1000);

		List<Monitor> monitors = new ArrayList<>();

		AtomicInteger activeCount = new AtomicInteger();
		AtomicInteger maximumActiveCount = new AtomicInteger();

		for (int i = 0; i < (MonitorRunner.THREADS_MAXIMUM + 5); i++) {
			monitors.add(
				_newGaugeTestMonitor(
					activeCount, maximumActiveCount,
					_newMonitorConfig(RandomTestUtil.randomString())));
		}

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			monitors);

		testEquals(monitors.size(), monitorResultsMap.size());

		for (MonitorResult monitorResult : monitorResultsMap.values()) {
			testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
		}

		Assert.assertTrue(
			maximumActiveCount.get() <= MonitorRunner.THREADS_MAXIMUM);
		Assert.assertTrue(maximumActiveCount.get() > 1);
	}

	@Test(timeout = 5000)
	public void testRunThrowingMonitor() {
		MonitorRunner monitorRunner = new MonitorRunner();

		TestMonitor testMonitor1 = new TestMonitor(_newMonitorConfig("a")) {

			@Override
			public MonitorResult execute() {
				throw new RuntimeException("Unable to execute the monitor");
			}

		};

		TestMonitor testMonitor2 = new TestMonitor(_newMonitorConfig("b"));

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Arrays.<Monitor>asList(testMonitor1, testMonitor2));

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor1);

		testEquals(
			"Monitor a failed: Unable to execute the monitor",
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());

		monitorResult = monitorResultsMap.get(testMonitor2);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test(timeout = 5000)
	public void testRunThrowingMonitorWithoutMessage() {
		MonitorRunner monitorRunner = new MonitorRunner();

		TestMonitor testMonitor = new TestMonitor(_newMonitorConfig("a")) {

			@Override
			public MonitorResult execute() {
				throw new RuntimeException();
			}

		};

		Map<Monitor, MonitorResult> monitorResultsMap = monitorRunner.run(
			Collections.<Monitor>singletonList(testMonitor));

		MonitorResult monitorResult = monitorResultsMap.get(testMonitor);

		testEquals(
			"Monitor a failed: java.lang.RuntimeException",
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
	}

	private TestMonitor _newConcurrentTestMonitor(
		CountDownLatch countDownLatch, MonitorConfig monitorConfig) {

		return new TestMonitor(monitorConfig) {

			@Override
			public MonitorResult execute() {
				countDownLatch.countDown();

				try {
					if (!countDownLatch.await(2, TimeUnit.SECONDS)) {
						throw new IllegalStateException(
							"Monitors did not run concurrently");
					}
				}
				catch (InterruptedException interruptedException) {
					throw new RuntimeException(interruptedException);
				}

				return super.execute();
			}

		};
	}

	private TestMonitor _newGaugeTestMonitor(
		AtomicInteger activeCount, AtomicInteger maximumActiveCount,
		MonitorConfig monitorConfig) {

		return new TestMonitor(monitorConfig) {

			@Override
			public MonitorResult execute() {
				int currentActiveCount = activeCount.incrementAndGet();

				while (true) {
					int previousActiveCount = maximumActiveCount.get();

					if ((currentActiveCount <= previousActiveCount) ||
						maximumActiveCount.compareAndSet(
							previousActiveCount, currentActiveCount)) {

						break;
					}
				}

				try {
					Thread.sleep(50);
				}
				catch (InterruptedException interruptedException) {
					throw new RuntimeException(interruptedException);
				}
				finally {
					activeCount.decrementAndGet();
				}

				return super.execute();
			}

		};
	}

	private MonitorConfig _newMonitorConfig(String id) {
		return _newMonitorConfig(id, 0);
	}

	private MonitorConfig _newMonitorConfig(String id, long timeoutSeconds) {
		return new MonitorConfig(
			id, RandomTestUtil.randomLong(), null,
			MonitorConfig.Severity.MEDIUM, null, timeoutSeconds,
			RandomTestUtil.randomString());
	}

	private TestMonitor _newSleepingTestMonitor(
		long sleepMillis, MonitorConfig monitorConfig) {

		return new TestMonitor(monitorConfig) {

			@Override
			public MonitorResult execute() {
				try {
					Thread.sleep(sleepMillis);
				}
				catch (InterruptedException interruptedException) {
					throw new RuntimeException(interruptedException);
				}

				return super.execute();
			}

		};
	}

	private static class BlockingTestMonitor extends TestMonitor {

		public BlockingTestMonitor(
			CountDownLatch releaseCountDownLatch,
			CountDownLatch startCountDownLatch, MonitorConfig monitorConfig) {

			super(monitorConfig);

			_releaseCountDownLatch = releaseCountDownLatch;
			_startCountDownLatch = startCountDownLatch;
		}

		@Override
		public MonitorResult execute() {
			_startCountDownLatch.countDown();

			while (true) {
				try {
					_releaseCountDownLatch.await();

					break;
				}
				catch (InterruptedException interruptedException) {
				}
			}

			return null;
		}

		private final CountDownLatch _releaseCountDownLatch;
		private final CountDownLatch _startCountDownLatch;

	}

	private static class HangingTestMonitor extends TestMonitor {

		public HangingTestMonitor(
			CountDownLatch countDownLatch, MonitorConfig monitorConfig) {

			super(monitorConfig);

			_countDownLatch = countDownLatch;
		}

		@Override
		public MonitorResult execute() {
			try {
				Thread.sleep(10000);
			}
			catch (InterruptedException interruptedException) {
				if (_countDownLatch != null) {
					_countDownLatch.countDown();
				}

				Thread thread = Thread.currentThread();

				thread.interrupt();
			}

			return null;
		}

		private final CountDownLatch _countDownLatch;

	}

}