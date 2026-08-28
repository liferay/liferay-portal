/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class PrometheusScrapeTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetValue() {
		String label = RandomTestUtil.randomString();
		String name = MonitorTestUtil.newMetricName();

		_testGetValue(
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample(
					"file_store", MonitorTestUtil.FILE_STORE, name, "3.0")),
			3.0D, "file_store", MonitorTestUtil.FILE_STORE, name);
		_testGetValue(
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample("label", label, name, "0.0")),
			0.0D, "label", label, name);
		_testGetValue(
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample("label", label, name, "1.0"),
				MonitorTestUtil.newSample("label", label + "x", name, "2.0")),
			2.0D, "label", label + "x", name);
		_testGetValue(
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample(
					"label", label, name, "5.0 1699999999000")),
			5.0D, "label", label, name);
		_testGetValue(
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample(
					"label", label, name, "7.7849452544E10")),
			7.7849452544E10, "label", label, name);
		_testGetValue(
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample(
					"label", label, name, "9.223372036853727E18")),
			9.223372036853727E18, "label", label, name);
		_testGetValue(
			MonitorTestUtil.newScrape(name + "{path=\"a\\\"b\",} 7.0"), 7.0D,
			"path", "a\"b", name);
	}

	@Test
	public void testGetValueAbsent() {
		String commentedName = MonitorTestUtil.newMetricName();
		String garbageName = MonitorTestUtil.newMetricName();
		String label = RandomTestUtil.randomString();
		String name = MonitorTestUtil.newMetricName();

		String scrape = MonitorTestUtil.newScrape(
			"# HELP " + commentedName + " help text",
			"# TYPE " + commentedName + " gauge",
			MonitorTestUtil.newSample("label", label, name, "1.0"),
			garbageName + " line that is not a sample @@@@");

		_testGetValue(scrape, null, "label", label + "x", name);
		_testGetValue(
			scrape, null, "label", label, MonitorTestUtil.newMetricName());
		_testGetValue(scrape, null, "label", label, commentedName);
		_testGetValue(scrape, null, "label", label, garbageName);
	}

	@Test
	public void testGetValueAmbiguous() {
		String label = RandomTestUtil.randomString();
		String name = MonitorTestUtil.newMetricName();

		_testGetValue(
			MonitorTestUtil.newScrape(
				JenkinsResultsParserUtil.combine(
					name, "{file_store=\"", MonitorTestUtil.FILE_STORE,
					"\",directory=\"jobs\",} 1.0"),
				JenkinsResultsParserUtil.combine(
					name, "{file_store=\"", MonitorTestUtil.FILE_STORE,
					"\",directory=\"caches\",} 2.0")),
			null, "file_store", MonitorTestUtil.FILE_STORE, name);
		_testGetValue(
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample("label", label, name, "1.0"),
				MonitorTestUtil.newSample("label", label, name, "2.0")),
			null, "label", label, name);
	}

	@Test
	public void testGetValueCallerMetrics() {
		PrometheusScrape prometheusScrape = new PrometheusScrape(
			MonitorTestUtil.newScrape(
				"default_jenkins_executors_busy{label=\"master\",} 2.0",
				"default_jenkins_executors_defined{label=\"master\",} 4.0",
				"default_jenkins_executors_queue_length" +
					"{label=\"slave-mem\",} 0.0",
				MonitorTestUtil.newSample(
					"file_store", MonitorTestUtil.FILE_STORE,
					"default_jenkins_file_store_available_bytes",
					"3.7194141696E10"),
				MonitorTestUtil.newSample(
					"file_store", MonitorTestUtil.FILE_STORE,
					"default_jenkins_file_store_capacity_bytes",
					"7.7849452544E10")));

		testEquals(
			2.0D,
			prometheusScrape.getValue(
				"label", "master", "default_jenkins_executors_busy"));
		testEquals(
			4.0D,
			prometheusScrape.getValue(
				"label", "master", "default_jenkins_executors_defined"));
		testEquals(
			0.0D,
			prometheusScrape.getValue(
				"label", "slave-mem",
				"default_jenkins_executors_queue_length"));
	}

	@Test
	public void testGetValueEmptyContent() {
		String label = RandomTestUtil.randomString();
		String name = MonitorTestUtil.newMetricName();

		_testGetValue("", null, "label", label, name);
		_testGetValue(null, null, "label", label, name);
	}

	@Test
	public void testGetValueLineEndings() {
		String label = RandomTestUtil.randomString();
		String name = MonitorTestUtil.newMetricName();

		PrometheusScrape prometheusScrape = new PrometheusScrape(
			MonitorTestUtil.newSample("label", label, name, "3.0\r\n"));

		testEquals(3.0D, prometheusScrape.getValue("label", label, name));
	}

	@Test
	public void testGetValueNonFinite() {
		String label = RandomTestUtil.randomString();

		_testGetValueNonFinite(label, "+Inf");
		_testGetValueNonFinite(label, "-Inf");
		_testGetValueNonFinite(label, "Infinity");
		_testGetValueNonFinite(label, "NaN");
	}

	@Test
	public void testGetValueNullLabel() {
		String label = RandomTestUtil.randomString();
		String name = MonitorTestUtil.newMetricName();
		String unlabeledName = MonitorTestUtil.newMetricName();

		String scrape = MonitorTestUtil.newScrape(
			MonitorTestUtil.newSample("label", label, name, "1.0"),
			unlabeledName + " 2.0");

		_testGetValue(scrape, null, "label", null, name);
		_testGetValue(scrape, null, null, label, name);
		_testGetValue(scrape, null, null, null, unlabeledName);
	}

	private void _testGetValue(
		String content, Double expectedValue, String labelName,
		String labelValue, String name) {

		PrometheusScrape prometheusScrape = new PrometheusScrape(content);

		testEquals(
			expectedValue,
			prometheusScrape.getValue(labelName, labelValue, name));
	}

	private void _testGetValueNonFinite(
		String labelValue, String nonFiniteValue) {

		String name = MonitorTestUtil.newMetricName();

		_testGetValue(
			MonitorTestUtil.newScrape(
				MonitorTestUtil.newSample(
					"label", labelValue, name, nonFiniteValue)),
			null, "label", labelValue, name);
	}

}