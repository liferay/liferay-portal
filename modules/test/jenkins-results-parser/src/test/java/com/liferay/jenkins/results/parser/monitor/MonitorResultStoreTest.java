/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class MonitorResultStoreTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetLatestMonitorResult() {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		testSame(null, monitorResultStore.getLatestMonitorResult("a"));

		MonitorResult monitorResult1 = _newMonitorResult();
		MonitorResult monitorResult2 = _newMonitorResult();

		monitorResultStore.store("a", monitorResult1);
		monitorResultStore.store("a", monitorResult2);

		testSame(
			monitorResult2, monitorResultStore.getLatestMonitorResult("a"));
	}

	@Test
	public void testGetMonitorResults() {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		List<MonitorResult> monitorResults =
			monitorResultStore.getMonitorResults("a");

		Assert.assertTrue(monitorResults.isEmpty());

		MonitorResult monitorResult1 = _newMonitorResult();
		MonitorResult monitorResult2 = _newMonitorResult();

		monitorResultStore.store("a", monitorResult1);
		monitorResultStore.store("a", monitorResult2);

		monitorResults = monitorResultStore.getMonitorResults("a");

		testEquals(2, monitorResults.size());
		testSame(monitorResult1, monitorResults.get(0));
		testSame(monitorResult2, monitorResults.get(1));
	}

	@Test
	public void testGetMonitorResultsIsUnmodifiable() {
		MonitorResultStore monitorResultStore = new MonitorResultStore();

		monitorResultStore.store("a", _newMonitorResult());

		List<MonitorResult> monitorResults =
			monitorResultStore.getMonitorResults("a");

		try {
			monitorResults.add(_newMonitorResult());

			Assert.fail("Expected UnsupportedOperationException");
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}
	}

	@Test
	public void testMonitorResultStoreMaxMonitorResultCount() {
		new MonitorResultStore(1);

		try {
			new MonitorResultStore(0);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	@Test
	public void testStoreMaxMonitorResultCount() {
		MonitorResultStore monitorResultStore = new MonitorResultStore(2);

		MonitorResult monitorResult1 = _newMonitorResult();
		MonitorResult monitorResult2 = _newMonitorResult();
		MonitorResult monitorResult3 = _newMonitorResult();

		monitorResultStore.store("a", monitorResult1);
		monitorResultStore.store("a", monitorResult2);
		monitorResultStore.store("a", monitorResult3);

		List<MonitorResult> monitorResults =
			monitorResultStore.getMonitorResults("a");

		testEquals(2, monitorResults.size());
		testSame(monitorResult2, monitorResults.get(0));
		testSame(monitorResult3, monitorResults.get(1));
	}

	private MonitorResult _newMonitorResult() {
		return new MonitorResult(
			RandomTestUtil.randomString(), null, MonitorResult.Status.OK,
			RandomTestUtil.randomLong());
	}

}