/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsMasterTestUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.UrlReader;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Brittney Nguyen
 */
public class JobHealthMonitorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		JenkinsMasterTestUtil.getJenkinsMaster(_MASTER_NAME, _MASTER_URL);
	}

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		JenkinsMasterTestUtil.resetCaches();
	}

	@Test
	public void testExecuteAborted() throws Exception {
		_setJobJSONObject(
			_newJobJSONObject(
				42, "ABORTED",
				JenkinsResultsParserUtil.getCurrentTimeMillis()));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller completed build 42 with the " +
				"result ABORTED",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteBuildDurationMaximum() throws Exception {
		_setJobJSONObject(
			_newRunningJobJSONObject(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - (1800 * 1000),
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(7200 * 1000)));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].threshold[build.duration.maximum]", "900");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller has been running for 30 " +
				"minutes, exceeding its maximum duration of 15 minutes",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteBuildDurationMaximumWithCadence() throws Exception {
		_setJobJSONObject(
			_newRunningJobJSONObject(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - (7200 * 1000),
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(4 * 3600 * 1000)));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "900");
		monitorProperties.setProperty(
			"monitor[a].threshold[build.duration.maximum]", "3600");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller has been running for 2 hours, " +
				"exceeding its maximum duration of 1 hour",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteBuildDurationMaximumWithinBound() throws Exception {
		_setJobJSONObject(
			_newRunningJobJSONObject(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - (300 * 1000),
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(7200 * 1000)));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].threshold[build.duration.maximum]", "900");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test
	public void testExecuteBuildDurationMaximumWithoutThreshold()
		throws Exception {

		_setJobJSONObject(
			_newRunningJobJSONObject(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - (7200 * 1000),
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(4 * 3600 * 1000)));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test
	public void testExecuteMissingLastBuildTimestamp() throws Exception {
		_setJobJSONObject(
			new JSONObject(
			).put(
				"lastBuild",
				new JSONObject(
				).put(
					"building", false
				).put(
					"number", 42
				)
			));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "900");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
		testEquals(
			"Unable to determine the last build timestamp for job " +
				"generate-reports-controller",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteNotGreen() throws Exception {
		_setJobJSONObject(
			_newJobJSONObject(
				42, "FAILURE",
				JenkinsResultsParserUtil.getCurrentTimeMillis()));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller completed build 42 with the " +
				"result FAILURE",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteNotGreenAndOverdue() throws Exception {
		_setJobJSONObject(
			_newJobJSONObject(
				42, "FAILURE",
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(3600 * 1000)));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "900");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller completed build 42 with the " +
				"result FAILURE. Job generate-reports-controller last ran 1 " +
					"hour ago, exceeding its cadence of 15 minutes plus a " +
						"grace period of 30 minutes",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteNotGreenMissingLastBuildTimestamp()
		throws Exception {

		_setJobJSONObject(
			new JSONObject(
			).put(
				"lastBuild",
				new JSONObject(
				).put(
					"building", false
				).put(
					"number", 42
				)
			).put(
				"lastCompletedBuild",
				new JSONObject(
				).put(
					"number", 42
				).put(
					"result", "FAILURE"
				).put(
					"timestamp", JenkinsResultsParserUtil.getCurrentTimeMillis()
				)
			));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller completed build 42 with the " +
				"result FAILURE. Unable to determine the last build " +
					"timestamp for job generate-reports-controller",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteNotGreenNotExpectedGreen() throws Exception {
		_setJobJSONObject(
			_newJobJSONObject(
				42, "FAILURE",
				JenkinsResultsParserUtil.getCurrentTimeMillis()));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].parameter[expected.green]", "false");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller is OK",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteNotTriggered() throws Exception {
		_setJobJSONObject(
			new JSONObject(
			).put(
				"lastBuild", JSONObject.NULL
			).put(
				"lastCompletedBuild", JSONObject.NULL
			));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "900");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller has never run",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteOK() throws Exception {
		_setJobJSONObject(
			_newJobJSONObject(
				42, "SUCCESS",
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(600 * 1000)));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "900");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller is OK",
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("42", metrics.get("last.completed.build.number"));
		testEquals("600", metrics.get("last.build.age.seconds"));
		testEquals("SUCCESS", metrics.get("last.completed.build.result"));
		testEquals("false", metrics.get("last.build.running"));
	}

	@Test
	public void testExecuteOverdue() throws Exception {
		_setJobJSONObject(
			_newJobJSONObject(
				42, "SUCCESS",
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(3600 * 1000)));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "900");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller last ran 1 hour ago, exceeding " +
				"its cadence of 15 minutes plus a grace period of 30 minutes",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteOverdueGraceThreshold() throws Exception {
		_setJobJSONObject(
			_newJobJSONObject(
				42, "SUCCESS",
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(3600 * 1000)));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "900");
		monitorProperties.setProperty(
			"monitor[a].threshold[overdue.grace]", "7200");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test
	public void testExecuteOverdueWithoutCadence() throws Exception {
		_setJobJSONObject(
			_newJobJSONObject(
				42, "SUCCESS",
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(30L * 24 * 3600 * 1000)));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test
	public void testExecuteRunningPastCadence() throws Exception {
		_setJobJSONObject(
			_newRunningJobJSONObject(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - (3600 * 1000),
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(7200 * 1000)));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "900");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller has been running for 1 hour, " +
				"exceeding its cadence of 15 minutes plus a grace period of " +
					"30 minutes",
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("true", metrics.get("last.build.running"));
	}

	@Test
	public void testExecuteRunningWithinBuildDurationMaximum()
		throws Exception {

		_setJobJSONObject(
			_newRunningJobJSONObject(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - (3600 * 1000),
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(7200 * 1000)));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "900");
		monitorProperties.setProperty(
			"monitor[a].threshold[build.duration.maximum]", "86400");

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test
	public void testExecuteRunningWithoutCompletedBuild() throws Exception {
		_setJobJSONObject(
			new JSONObject(
			).put(
				"lastBuild",
				new JSONObject(
				).put(
					"building", true
				).put(
					"number", 43
				).put(
					"timestamp",
					JenkinsResultsParserUtil.getCurrentTimeMillis() -
						(7200 * 1000)
				)
			));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test
	public void testExecuteUnreadableResponse() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			RandomTestUtil.randomString(), _JOB_API_URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Unable to read http://test-9-1/job/generate-reports-controller: " +
				"Unable to create JSON object",
			monitorResult.getMessage());
	}

	@Test
	public void testJobHealthMonitor() {
		_testJobHealthMonitorInvalidProperty(
			"monitor[a].parameter[cadence]", "-1");
		_testJobHealthMonitorInvalidProperty(
			"monitor[a].parameter[cadence]", "not-a-number");
		_testJobHealthMonitorInvalidProperty(
			"monitor[a].parameter[expected.green]", "yes");

		_testJobHealthMonitorMissingProperty("monitor[a].parameter[job.name]");
		_testJobHealthMonitorMissingProperty(
			"monitor[a].parameter[master.name]");
	}

	private MonitorResult _execute(Properties monitorProperties) {
		JobHealthMonitor jobHealthMonitor = _newJobHealthMonitor(
			monitorProperties);

		return jobHealthMonitor.execute();
	}

	private JobHealthMonitor _newJobHealthMonitor(
		Properties monitorProperties) {

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		return new JobHealthMonitor(monitorConfigs.get(0));
	}

	private JSONObject _newJobJSONObject(
		int number, String result, long timestamp) {

		return new JSONObject(
		).put(
			"lastBuild",
			new JSONObject(
			).put(
				"building", false
			).put(
				"number", number
			).put(
				"timestamp", timestamp
			)
		).put(
			"lastCompletedBuild",
			new JSONObject(
			).put(
				"number", number
			).put(
				"result", result
			).put(
				"timestamp", timestamp
			)
		);
	}

	private Properties _newMonitorProperties() {
		Properties monitorProperties = new Properties();

		monitorProperties.setProperty(
			"monitor[a].parameter[job.name]", _JOB_NAME);
		monitorProperties.setProperty(
			"monitor[a].parameter[master.name]", _MASTER_NAME);
		monitorProperties.setProperty("monitor[a].type", "job-health");

		return monitorProperties;
	}

	private JSONObject _newRunningJobJSONObject(
		long lastBuildTimestamp, long lastCompletedBuildTimestamp) {

		return new JSONObject(
		).put(
			"lastBuild",
			new JSONObject(
			).put(
				"building", true
			).put(
				"number", 43
			).put(
				"timestamp", lastBuildTimestamp
			)
		).put(
			"lastCompletedBuild",
			new JSONObject(
			).put(
				"number", 42
			).put(
				"result", "SUCCESS"
			).put(
				"timestamp", lastCompletedBuildTimestamp
			)
		);
	}

	private void _setJobJSONObject(JSONObject jobJSONObject) throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(jobJSONObject.toString(), _JOB_API_URL, urlReader);
	}

	private void _testJobHealthMonitorExpectedIllegalArgumentException(
		Properties monitorProperties) {

		try {
			_newJobHealthMonitor(monitorProperties);

			Assert.fail("Expected IllegalArgumentException");
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testJobHealthMonitorInvalidProperty(
		String name, String value) {

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(name, value);

		_testJobHealthMonitorExpectedIllegalArgumentException(
			monitorProperties);
	}

	private void _testJobHealthMonitorMissingProperty(String name) {
		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.remove(name);

		_testJobHealthMonitorExpectedIllegalArgumentException(
			monitorProperties);
	}

	private static final String _JOB_API_URL =
		"http://test-9-1/job/generate-reports-controller/api/json";

	private static final String _JOB_NAME = "generate-reports-controller";

	private static final String _MASTER_NAME = "test-9-1";

	private static final String _MASTER_URL = "http://test-9-1";

}