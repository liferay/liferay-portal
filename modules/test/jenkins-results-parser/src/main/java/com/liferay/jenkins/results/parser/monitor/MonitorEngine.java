/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Brittney Nguyen
 */
public class MonitorEngine {

	public MonitorEngine(
		MonitorResultStore monitorResultStore, List<Monitor> monitors) {

		MonitorIdValidator.validate(monitors);

		_monitorResultStore = monitorResultStore;
		_monitors = Collections.unmodifiableList(new ArrayList<>(monitors));

		_monitorScheduler = new MonitorScheduler(monitorResultStore);
	}

	public Map<Monitor, MonitorResult> runCycle() {
		List<Monitor> dueMonitors = _monitorScheduler.getDueMonitors(_monitors);

		for (Monitor monitor : dueMonitors) {
			try {
				monitor.prepareCycle();
			}
			catch (RuntimeException runtimeException) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"WARNING: Unable to prepare monitor ", monitor.getId(),
						": ", runtimeException.getMessage()));
			}
		}

		Map<Monitor, MonitorResult> monitorResultsMap = _monitorRunner.run(
			dueMonitors);

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		for (Map.Entry<Monitor, MonitorResult> entry :
				monitorResultsMap.entrySet()) {

			Monitor monitor = entry.getKey();

			MonitorResult monitorResult = entry.getValue();

			monitorResult = new MonitorResult(
				monitorResult.getMessage(), monitorResult.getMetrics(),
				monitorResult.getStatus(), currentTimeMillis);

			entry.setValue(monitorResult);

			_monitorResultStore.store(monitor.getId(), monitorResult);
		}

		return monitorResultsMap;
	}

	private final MonitorResultStore _monitorResultStore;
	private final MonitorRunner _monitorRunner = new MonitorRunner();
	private final List<Monitor> _monitors;
	private final MonitorScheduler _monitorScheduler;

}