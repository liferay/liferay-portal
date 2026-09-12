/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsMasterTestUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.UrlReader;

import java.io.IOException;

import java.time.Instant;

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

		mockEnvironment(Collections.emptyMap());

		JenkinsMasterTestUtil.getJenkinsMaster(
			_MASTER_NAME, "http://" + _MASTER_NAME);
	}

	@Test
	public void testExecuteControllerNeverRan() throws Exception {
		_setURLReaderOutput(_newHeadCommitJSONObject(0, _newSHA()));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(
			"Controller job " + _CONTROLLER_JOB_NAME + " has never run",
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
	}

	@Test
	public void testExecuteControllerStale() throws Exception {
		String sha = _newSHA();

		_setURLReaderOutput(
			_newHeadCommitJSONObject(0, sha),
			_newBuildJSONObject(28800, _newInvocationDescription(sha)));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());

		String message = monitorResult.getMessage();

		Assert.assertTrue(
			message.contains(
				"leaving the upstream testsuite for branch master " +
					"unevaluated"));
	}

	@Test
	public void testExecuteHeadIsUnreadable() throws Exception {
		String sha = _newSHA();

		UrlReader urlReader = _setURLReaderOutput(
			null, _newBuildJSONObject(0, _newInvocationDescription(sha)));

		setUrlReaderException(
			new IOException("Unable to read"), _HEAD_COMMIT_API_URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());

		String message = monitorResult.getMessage();

		Assert.assertTrue(
			message.contains("Unable to read the head of branch master"));
	}

	@Test
	public void testExecuteMergeWithoutSubsequentRun() throws Exception {
		_setURLReaderOutput(
			_newHeadCommitJSONObject(21600, _newSHA()),
			_newBuildJSONObject(0, _newInvocationDescription(_newSHA())));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());

		String message = monitorResult.getMessage();

		Assert.assertTrue(message.contains("Branch master was merged"));
		Assert.assertTrue(
			message.contains("but its upstream testsuite last ran against"));
	}

	@Test
	public void testExecuteNoInvocationInWindow() throws Exception {
		_setURLReaderOutput(
			_newHeadCommitJSONObject(0, _newSHA()), _newBuildJSONObject(0, ""));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());

		String message = monitorResult.getMessage();

		Assert.assertTrue(
			message.contains(
				"Unable to determine the last upstream testsuite run"));
	}

	@Test
	public void testExecuteQuietBranchDoesNotAlert() throws Exception {
		String sha = _newSHA();

		_setURLReaderOutput(
			_newHeadCommitJSONObject(432000, sha), _newBuildJSONObject(0, ""),
			_newBuildJSONObject(432000, _newInvocationDescription(sha)));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(
			"The upstream testsuite for branch master is OK",
			monitorResult.getMessage());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals(sha, metrics.get("branch.head.sha"));
		testEquals(sha, metrics.get("last.invocation.sha"));
	}

	@Test
	public void testExecuteRecentMergeIsNotOverdue() throws Exception {
		_setURLReaderOutput(
			_newHeadCommitJSONObject(1800, _newSHA()),
			_newBuildJSONObject(3600, _newInvocationDescription(_newSHA())));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test
	public void testExecuteSingleRunnerDescription() throws Exception {
		String sha = _newSHA();

		_setURLReaderOutput(
			_newHeadCommitJSONObject(0, sha),
			_newBuildJSONObject(
				0,
				JenkinsResultsParserUtil.combine(
					"<strong>UNSTABLE</strong> - <a href=\"", _BUILD_URL,
					"\">Build URL</a><ul><li><strong>Git ID:</strong> ",
					"<a href=\"https://github.com/liferay/liferay-portal",
					"/commit/", sha, "\">", sha.substring(0, 7),
					"</a></li></ul>")));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals(sha, metrics.get("last.invocation.sha"));
	}

	@Test
	public void testUpstreamJobHealthMonitor() {
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

	private JSONObject _newHeadCommitJSONObject(long ageSeconds, String sha) {
		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		Instant instant = Instant.ofEpochMilli(
			currentTimeMillis - (ageSeconds * 1000));

		return new JSONObject(
		).put(
			"commit",
			new JSONObject(
			).put(
				"committer",
				new JSONObject(
				).put(
					"date", instant.toString()
				)
			)
		).put(
			"sha", sha
		);
	}

	private String _newInvocationDescription(String sha) {
		return JenkinsResultsParserUtil.combine(
			"object, <strong>GIT ID</strong> - <a href=\"",
			"https://github.com/liferay/liferay-portal/commit/", sha, "\">",
			sha.substring(0, 7), "</a>");
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

	private String _newSHA() {
		return RandomTestUtil.randomSHA();
	}

	private UpstreamJobHealthMonitor _newUpstreamJobHealthMonitor(
		Properties monitorProperties) {

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		return new UpstreamJobHealthMonitor(monitorConfigs.get(0));
	}

	private UrlReader _setURLReaderOutput(
			JSONObject headCommitJSONObject, JSONObject... buildJSONObjects)
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

		if (headCommitJSONObject != null) {
			setUrlReaderOutput(
				headCommitJSONObject.toString(), _HEAD_COMMIT_API_URL,
				urlReader);
		}

		return urlReader;
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

	private static final String _BUILD_URL =
		"https://test-1-41.liferay.com/job/test-portal-testsuite-upstream" +
			"(master)/1234/";

	private static final String _CONTROLLER_JOB_NAME =
		"test-portal-testsuite-upstream-controller(master)";

	private static final String _HEAD_COMMIT_API_URL =
		"https://api.github.com/repos/liferay/liferay-portal/commits/master";

	private static final String _JOB_API_URL =
		"http://test-9-1/job/test-portal-testsuite-upstream-controller" +
			"(master)/api/json";

	private static final String _MASTER_NAME = "test-9-1";

}