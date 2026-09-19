/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.benchmarks;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ObjectValuePair;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;

/**
 * @author Dante Wang
 */
public class Statistics {

	public Statistics(int runCount) {
		_runCount = runCount;
	}

	public void addResults(List<ObjectValuePair<String, Long>> results) {
		for (ObjectValuePair<String, Long> result : results) {
			Queue<Long> durations = _durationsMap.computeIfAbsent(
				result.getKey(), testStepName -> new ConcurrentLinkedQueue<>());

			durations.offer(result.getValue());

			AtomicLong durationSum = _durationSumsMap.computeIfAbsent(
				result.getKey(),
				testStepName -> {
					if (!_testStepNames.contains(testStepName)) {
						_testStepNames.offer(testStepName);
					}

					return new AtomicLong();
				});

			durationSum.addAndGet(result.getValue());
		}
	}

	public void printStatistics() {
		long totalTime = System.currentTimeMillis() - _START_TIME;

		System.out.println(
			StringBundler.concat(
				"\nTests took ", totalTime, " ms. Each session took ",
				String.format("%.2f", (double)totalTime / _runCount), " ms."));

		for (String testStepName : _testStepNames) {
			Queue<Long> durations = _durationsMap.get(testStepName);

			Assert.assertEquals(
				"The size of collected durations does not match run count",
				_runCount, durations.size());

			AtomicLong durationSum = _durationSumsMap.get(testStepName);

			double average = (double)durationSum.get() / _runCount;

			double variance = 0;

			for (Long duration : durations) {
				variance += (duration - average) * (duration - average);
			}

			variance = variance / _runCount;

			System.out.println(
				StringBundler.concat(
					"\nTest step: ", testStepName, "\n\n\tAverage time: ",
					String.format("%.2f", average),
					" ms\n\tStandard deviation: ",
					String.format("%.2f", Math.sqrt(variance)), 2,
					" ms\n\tTPS: ",
					String.format(
						"%.2f", _runCount * 1000 / (double)durationSum.get())));
		}
	}

	private static final long _START_TIME = System.currentTimeMillis();

	private final Map<String, AtomicLong> _durationSumsMap =
		new ConcurrentHashMap<>();
	private final Map<String, Queue<Long>> _durationsMap =
		new ConcurrentHashMap<>();
	private final int _runCount;
	private final Queue<String> _testStepNames = new ConcurrentLinkedQueue<>();

}