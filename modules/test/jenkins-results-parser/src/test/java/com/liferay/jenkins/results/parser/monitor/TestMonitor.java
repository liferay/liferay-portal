/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.RandomTestUtil;

/**
 * @author Brittney Nguyen
 */
public class TestMonitor implements Monitor {

	public TestMonitor(MonitorConfig monitorConfig) {
		_monitorConfig = monitorConfig;
	}

	@Override
	public MonitorResult execute() {
		return new MonitorResult(
			RandomTestUtil.randomString(), null, MonitorResult.Status.OK,
			System.currentTimeMillis());
	}

	@Override
	public String getId() {
		return _monitorConfig.getId();
	}

	@Override
	public MonitorConfig getMonitorConfig() {
		return _monitorConfig;
	}

	@Override
	public void prepareCycle() {
	}

	private final MonitorConfig _monitorConfig;

}