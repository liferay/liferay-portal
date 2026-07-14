/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brittney Nguyen
 */
public class MonitorResultStore {

	public MonitorResultStore() {
		this(100);
	}

	public MonitorResultStore(int maxMonitorResultCount) {
		if (maxMonitorResultCount < 1) {
			throw new IllegalArgumentException(
				"Invalid max monitor result count: " + maxMonitorResultCount);
		}

		_maxMonitorResultCount = maxMonitorResultCount;
	}

	public MonitorResult getLatestMonitorResult(String id) {
		List<MonitorResult> monitorResults = _monitorResultsMap.get(id);

		if (monitorResults == null) {
			return null;
		}

		return monitorResults.get(monitorResults.size() - 1);
	}

	public List<MonitorResult> getMonitorResults(String id) {
		List<MonitorResult> monitorResults = _monitorResultsMap.get(id);

		if (monitorResults == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(new ArrayList<>(monitorResults));
	}

	public void store(String id, MonitorResult monitorResult) {
		List<MonitorResult> monitorResults = _monitorResultsMap.get(id);

		if (monitorResults == null) {
			monitorResults = new ArrayList<>();

			_monitorResultsMap.put(id, monitorResults);
		}

		monitorResults.add(monitorResult);

		while (monitorResults.size() > _maxMonitorResultCount) {
			monitorResults.remove(0);
		}
	}

	private final int _maxMonitorResultCount;
	private final Map<String, List<MonitorResult>> _monitorResultsMap =
		new HashMap<>();

}