/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brittney Nguyen
 */
public class MonitorScheduler {

	public MonitorScheduler(MonitorResultStore monitorResultStore) {
		_monitorResultStore = monitorResultStore;
	}

	public List<Monitor> getDueMonitors(List<Monitor> monitors) {
		List<Monitor> dueMonitors = new ArrayList<>();

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		for (Monitor monitor : monitors) {
			if (_isDue(currentTimeMillis, monitor)) {
				dueMonitors.add(monitor);
			}
		}

		return dueMonitors;
	}

	private boolean _isDue(long currentTimeMillis, Monitor monitor) {
		MonitorResult latestMonitorResult =
			_monitorResultStore.getLatestMonitorResult(monitor.getId());

		if (latestMonitorResult == null) {
			return true;
		}

		MonitorConfig monitorConfig = monitor.getMonitorConfig();

		if ((currentTimeMillis - latestMonitorResult.getTimestamp()) >=
				(monitorConfig.getInterval() * 1000)) {

			return true;
		}

		return false;
	}

	private final MonitorResultStore _monitorResultStore;

}