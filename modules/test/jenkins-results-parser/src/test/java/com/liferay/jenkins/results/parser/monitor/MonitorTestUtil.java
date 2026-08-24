/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsMasterTestUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

/**
 * @author Brittney Nguyen
 */
public class MonitorTestUtil {

	public static final String FILE_STORE = "/opt/java/jenkins (/dev/root)";

	public static String newJenkinsMasterName() {
		String masterName = RandomTestUtil.randomString();

		JenkinsMasterTestUtil.getJenkinsMaster(
			masterName, "http://" + masterName);

		return masterName;
	}

	public static String newMemoryInfo(long memoryAvailable, long memoryTotal) {
		return newScrape(
			"MemTotal:       " + memoryTotal + " kB",
			"MemFree:         2430036 kB",
			"MemAvailable:   " + memoryAvailable + " kB",
			"Buffers:         1914852 kB", "Cached:         16684940 kB",
			"SwapTotal:             0 kB");
	}

	public static String newMetricName() {
		return "metric" + RandomTestUtil.randomSHA();
	}

	public static String newSample(
		String labelName, String labelValue, String name, String value) {

		return JenkinsResultsParserUtil.combine(
			name, "{", labelName, "=\"", labelValue, "\",} ", value);
	}

	public static String newScrape(String... lines) {
		return JenkinsResultsParserUtil.join("\n", lines);
	}

}