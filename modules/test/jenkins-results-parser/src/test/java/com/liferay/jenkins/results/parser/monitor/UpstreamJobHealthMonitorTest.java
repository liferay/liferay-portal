/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsMasterTestUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.UrlReader;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Calum Ragan
 */
public class UpstreamJobHealthMonitorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockEnvironment(Collections.<String, String>emptyMap());

		JenkinsMasterTestUtil.getJenkinsMaster(
			_MASTER_NAME, "http://" + _MASTER_NAME);
	}

	@Test
	public void testExecuteControllerNeverRan() throws Exception {
		_setBuildsJSONObject();

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Controller job " + _CONTROLLER_JOB_NAME + " has never run",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteControllerStale() throws Exception {
		_setBuildsJSONObject(
			_newBuildJSONObject(28800, _newSkippedAlreadyRanDescription()));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());

		String message = monitorResult.getMessage();

		Assert.assertTrue(
			message.contains(
				"leaving the upstream testsuite for branch master " +
					"unevaluated"));
	}

	@Test
	public void testExecuteMergeWithoutSubsequentRun() throws Exception {
		_setBuildsJSONObject(
			_newBuildJSONObject(0, _SKIPPED_RUNNING_DESCRIPTION),
			_newBuildJSONObject(21600, _newInProgressDescription()));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("PENDING", metrics.get("last.invocation.result"));
		testEquals(
			_INVOCATION_BUILD_URL, metrics.get("last.invocation.build.url"));

		String message = monitorResult.getMessage();

		Assert.assertTrue(message.contains("Branch master was merged"));
	}

	@Test
	public void testExecuteNoMergeAndNoRun() throws Exception {
		_setBuildsJSONObject(
			_newBuildJSONObject(0, _newSkippedAlreadyRanDescription()),
			_newBuildJSONObject(3600, _newSkippedAlreadyRanDescription()),
			_newBuildJSONObject(7200, _newSkippedAlreadyRanDescription()));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
		testEquals(
			"The upstream testsuite for branch master is OK",
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("3", metrics.get("controller.skipped.streak"));
	}

	@Test
	public void testExecuteNotGreen() throws Exception {
		_setBuildsJSONObject(_newBuildJSONObject(0, _newFailureDescription()));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"The upstream testsuite for branch master completed with the " +
				"result \"FAILURE\"",
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("FAILURE", metrics.get("last.invocation.result"));
	}

	@Test
	public void testExecuteNotGreenWhenExpectedGreenIsFalse() throws Exception {
		_setBuildsJSONObject(_newBuildJSONObject(0, _newFailureDescription()));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].parameter[expected.green]", "false");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test
	public void testExecuteRecentMergeIsNotOverdue() throws Exception {
		_setBuildsJSONObject(
			_newBuildJSONObject(1800, _newInProgressDescription()));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test
	public void testUpstreamJobHealthMonitor() {
		_testUpstreamJobHealthMonitorInvalidProperty(
			"monitor[a].parameter[expected.green]", "yes");
		_testUpstreamJobHealthMonitorInvalidProperty(
			"monitor[a].threshold[trigger.latency]", "-1");

		_testUpstreamJobHealthMonitorMissingProperty(
			"monitor[a].parameter[branch]");
		_testUpstreamJobHealthMonitorMissingProperty(
			"monitor[a].parameter[controller.job.name]");
		_testUpstreamJobHealthMonitorMissingProperty(
			"monitor[a].parameter[master.name]");
	}

	private MonitorResult _execute(Properties monitorProperties) {
		UpstreamJobHealthMonitor upstreamJobHealthMonitor =
			_newUpstreamJobHealthMonitor(monitorProperties);

		return upstreamJobHealthMonitor.execute();
	}

	private JSONObject _newBuildJSONObject(
		long ageSeconds, String description) {

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		return new JSONObject(
		).put(
			"description", description
		).put(
			"timestamp", currentTimeMillis - (ageSeconds * 1000)
		);
	}

	private String _newFailureDescription() {
		return JenkinsResultsParserUtil.combine(
			"<strong style=\"color: red\">FAILURE</strong> - ",
			_INVOCATION_BUILD_URL);
	}

	private String _newInProgressDescription() {
		return JenkinsResultsParserUtil.combine(
			"<strong>IN PROGRESS</strong> - ", _INVOCATION_BUILD_URL);
	}

	private Properties _newMonitorProperties() {
		Properties monitorProperties = new Properties();

		monitorProperties.setProperty("monitor[a].parameter[branch]", _BRANCH);
		monitorProperties.setProperty(
			"monitor[a].parameter[controller.job.name]", _CONTROLLER_JOB_NAME);
		monitorProperties.setProperty(
			"monitor[a].parameter[master.name]", _MASTER_NAME);
		monitorProperties.setProperty("monitor[a].type", "upstream-job-health");

		return monitorProperties;
	}

	private String _newSkippedAlreadyRanDescription() {
		String sha = RandomTestUtil.randomSHA();

		return JenkinsResultsParserUtil.combine(
			"<strong>SKIPPED</strong> - <a href=\"https://github.com/liferay",
			"/liferay-portal/commit/", sha, "\">", sha.substring(0, 7),
			"</a> was already ran");
	}

	private UpstreamJobHealthMonitor _newUpstreamJobHealthMonitor(
		Properties monitorProperties) {

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		return new UpstreamJobHealthMonitor(monitorConfigs.get(0));
	}

	private void _setBuildsJSONObject(JSONObject... buildJSONObjects)
		throws Exception {

		JSONArray buildsJSONArray = new JSONArray();

		for (JSONObject buildJSONObject : buildJSONObjects) {
			buildsJSONArray.put(buildJSONObject);
		}

		UrlReader urlReader = mockUrlReader();

		JSONObject jobJSONObject = new JSONObject(
		).put(
			"builds", buildsJSONArray
		);

		setUrlReaderOutput(jobJSONObject.toString(), _JOB_API_URL, urlReader);
	}

	private void _testUpstreamJobHealthMonitorInvalidProperty(
		String name, String value) {

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(name, value);

		try {
			_newUpstreamJobHealthMonitor(monitorProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testUpstreamJobHealthMonitorMissingProperty(String name) {
		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.remove(name);

		try {
			_newUpstreamJobHealthMonitor(monitorProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private static final String _BRANCH = "master";

	private static final String _CONTROLLER_JOB_NAME =
		"test-portal-testsuite-upstream-controller(master)";

	private static final String _INVOCATION_BUILD_URL =
		"https://test-1-41.liferay.com/job/test-portal-testsuite-upstream" +
			"(master)/1234/";

	private static final String _JOB_API_URL =
		"http://test-9-1/job/test-portal-testsuite-upstream-controller" +
			"(master)/api/json";

	private static final String _MASTER_NAME = "test-9-1";

	private static final String _SKIPPED_RUNNING_DESCRIPTION =
		"<strong>SKIPPED</strong> - Job is already running";

}