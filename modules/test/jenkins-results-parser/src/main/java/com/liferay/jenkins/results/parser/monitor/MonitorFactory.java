/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

/**
 * @author Brittney Nguyen
 */
public class MonitorFactory {

	public static Monitor newMonitor(MonitorConfig monitorConfig) {
		String type = monitorConfig.getType();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(type)) {
			if (type.equals("http-endpoint")) {
				return new HTTPEndpointMonitor(monitorConfig);
			}

			if (type.equals("job-health")) {
				return new JobHealthMonitor(monitorConfig);
			}

			if (type.equals("report-freshness")) {
				return new ReportFreshnessMonitor(monitorConfig);
			}

			if (type.equals("resource-threshold")) {
				return new ResourceThresholdMonitor(monitorConfig);
			}
		}

		throw new IllegalArgumentException("Unknown monitor type: " + type);
	}

}