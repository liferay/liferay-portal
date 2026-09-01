/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.UrlReader;

import java.io.IOException;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class MasterResourceReaderTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testClearInstances() {
		String masterName = MonitorTestUtil.newJenkinsMasterName();

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(masterName);

		MasterResourceReader.clearInstances();

		Assert.assertNotSame(
			masterResourceReader, MasterResourceReader.getInstance(masterName));
	}

	@Test
	public void testGetInstance() {
		String masterName = MonitorTestUtil.newJenkinsMasterName();

		testSame(
			MasterResourceReader.getInstance(masterName),
			MasterResourceReader.getInstance(masterName));

		Assert.assertNotSame(
			MasterResourceReader.getInstance(masterName),
			MasterResourceReader.getInstance(
				MonitorTestUtil.newJenkinsMasterName()));
	}

	@Test
	public void testGetJobJSONObjects() throws Exception {
		String jobName = RandomTestUtil.randomString();

		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			_newJobsContent(jobName), "/api/json?tree=jobs", urlReader);

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(
				MonitorTestUtil.newJenkinsMasterName());

		Map<String, JSONObject> jobJSONObjects =
			masterResourceReader.getJobJSONObjects(_MILLIS_TIMEOUT);

		JSONObject jobJSONObject = jobJSONObjects.get(jobName);

		testEquals(jobName, jobJSONObject.optString("name"));

		testEquals(null, jobJSONObjects.get(RandomTestUtil.randomString()));

		testSame(
			jobJSONObjects,
			masterResourceReader.getJobJSONObjects(_MILLIS_TIMEOUT));
	}

	@Test
	public void testGetJobJSONObjectsIsUnmodifiable() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			_newJobsContent(RandomTestUtil.randomString()),
			"/api/json?tree=jobs", urlReader);

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(
				MonitorTestUtil.newJenkinsMasterName());

		Map<String, JSONObject> jobJSONObjects =
			masterResourceReader.getJobJSONObjects(_MILLIS_TIMEOUT);

		try {
			jobJSONObjects.put(RandomTestUtil.randomString(), new JSONObject());

			Assert.fail("Expected UnsupportedOperationException");
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}
	}

	@Test
	public void testGetJobJSONObjectsWithReadFailure() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderException(
			new IOException(RandomTestUtil.randomString()),
			"/api/json?tree=jobs", urlReader);

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(
				MonitorTestUtil.newJenkinsMasterName());

		try {
			masterResourceReader.getJobJSONObjects(_MILLIS_TIMEOUT);

			Assert.fail("Expected the read to fail");
		}
		catch (Exception exception) {
		}

		String jobName = RandomTestUtil.randomString();

		setUrlReaderOutput(
			_newJobsContent(jobName), "/api/json?tree=jobs", urlReader);

		Map<String, JSONObject> jobJSONObjects =
			masterResourceReader.getJobJSONObjects(_MILLIS_TIMEOUT);

		Assert.assertNotNull(jobJSONObjects.get(jobName));
	}

	@Test
	public void testGetMemoryInfo() throws Exception {
		setShellCommandOutput(
			"cat /proc/meminfo", mockShell(),
			MonitorTestUtil.newMemoryInfo(23791372L, 32249488L));

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(masterName);

		testSame(
			masterResourceReader.getMemoryInfo(),
			masterResourceReader.getMemoryInfo());
	}

	@Test
	public void testGetMemoryInfoWithoutPrometheusScrape() throws Exception {
		mockUrlReader();

		String memoryInfo = MonitorTestUtil.newMemoryInfo(23791372L, 32249488L);

		setShellCommandOutput("cat /proc/meminfo", mockShell(), memoryInfo);

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(masterName);

		testEquals(memoryInfo, masterResourceReader.getMemoryInfo());
	}

	@Test
	public void testGetPrometheusScrape() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			MonitorTestUtil.newSample(
				"label", RandomTestUtil.randomString(),
				MonitorTestUtil.newMetricName(), "1.0"),
			"/prometheus", urlReader);

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(masterName);

		testSame(
			masterResourceReader.getPrometheusScrape(_MILLIS_TIMEOUT),
			masterResourceReader.getPrometheusScrape(_MILLIS_TIMEOUT));
	}

	@Test
	public void testGetPrometheusScrapeWithEmptyContent() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput("", "/prometheus", urlReader);

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(masterName);

		PrometheusScrape prometheusScrape =
			masterResourceReader.getPrometheusScrape(_MILLIS_TIMEOUT);

		String labelValue = RandomTestUtil.randomString();
		String name = MonitorTestUtil.newMetricName();

		setUrlReaderOutput(
			MonitorTestUtil.newSample("label", labelValue, name, "1.0"),
			"/prometheus", urlReader);

		testSame(
			prometheusScrape,
			masterResourceReader.getPrometheusScrape(_MILLIS_TIMEOUT));
		testEquals(null, prometheusScrape.getValue("label", labelValue, name));

		MasterResourceReader.clearInstances();

		masterResourceReader = MasterResourceReader.getInstance(masterName);

		prometheusScrape = masterResourceReader.getPrometheusScrape(
			_MILLIS_TIMEOUT);

		testEquals(1.0D, prometheusScrape.getValue("label", labelValue, name));
	}

	@Test
	public void testGetPrometheusScrapeWithoutMemoryInfo() throws Exception {
		mockShell();

		String labelValue = RandomTestUtil.randomString();
		String name = MonitorTestUtil.newMetricName();
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			MonitorTestUtil.newSample("label", labelValue, name, "1.0"),
			"/prometheus", urlReader);

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(masterName);

		PrometheusScrape prometheusScrape =
			masterResourceReader.getPrometheusScrape(_MILLIS_TIMEOUT);

		testEquals(1.0D, prometheusScrape.getValue("label", labelValue, name));
	}

	@Test
	public void testGetPrometheusScrapeWithReadFailure() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderException(
			new IOException(RandomTestUtil.randomString()), "/prometheus",
			urlReader);

		String masterName = MonitorTestUtil.newJenkinsMasterName();

		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(masterName);

		try {
			masterResourceReader.getPrometheusScrape(_MILLIS_TIMEOUT);

			Assert.fail("Expected IOException");
		}
		catch (IOException ioException) {
		}

		String labelValue = RandomTestUtil.randomString();
		String name = MonitorTestUtil.newMetricName();

		setUrlReaderOutput(
			MonitorTestUtil.newSample("label", labelValue, name, "1.0"),
			"/prometheus", urlReader);

		PrometheusScrape prometheusScrape =
			masterResourceReader.getPrometheusScrape(_MILLIS_TIMEOUT);

		testEquals(1.0D, prometheusScrape.getValue("label", labelValue, name));
	}

	private String _newJobsContent(String jobName) {
		JSONObject jobsJSONObject = new JSONObject(
		).put(
			"jobs",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"buildable", true
				).put(
					"disabled", false
				).put(
					"name", jobName
				)
			)
		);

		return jobsJSONObject.toString();
	}

	private static final int _MILLIS_TIMEOUT = 1000;

}