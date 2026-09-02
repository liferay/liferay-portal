/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsMasterTestUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.UrlReader;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class JobHealthMonitorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockEnvironment(Collections.<String, String>emptyMap());

		MasterResourceReader.clearInstances();

		JenkinsMasterTestUtil.getJenkinsMaster(
			_MASTER_NAME, "http://" + _MASTER_NAME);
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
	public void testExecuteCadenceOverridesCron() throws Exception {
		long virtualCurrentTime = _newTimestamp(10, 0);

		_setJobJSONObject(
			_newConfigXML("0 6 * * 1-5"),
			_newJobJSONObject(
				42, "SUCCESS", virtualCurrentTime - (3600 * 1000)));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].parameter[cadence]", "900");

		MonitorResult monitorResult = _executeAtTime(
			monitorProperties, virtualCurrentTime);

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller last ran 1 hour ago, exceeding " +
				"its cadence of 15 minutes plus a grace period of 30 minutes",
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals(
			String.valueOf(virtualCurrentTime - (2700 * 1000)),
			metrics.get("overdue.deadline.timestamp"));
	}

	@Test
	public void testExecuteCronGraceScalesToPeriod() throws Exception {
		Calendar calendar = _newCalendar(3, 5);

		calendar.add(Calendar.DAY_OF_MONTH, -1);

		_setJobJSONObject(
			_newConfigXML("0 3 * * *"),
			_newJobJSONObject(42, "SUCCESS", calendar.getTimeInMillis()));

		MonitorResult monitorResult = _executeAtTime(
			_newMonitorProperties(), _newTimestamp(4, 0));

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		Calendar deadlineCalendar = _newCalendar(3, 0);

		deadlineCalendar.add(Calendar.DAY_OF_MONTH, -1);

		testEquals(
			String.valueOf(deadlineCalendar.getTimeInMillis()),
			metrics.get("overdue.deadline.timestamp"));

		monitorResult = _executeAtTime(
			_newMonitorProperties(), _newTimestamp(10, 5));

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());

		metrics = monitorResult.getMetrics();

		testEquals(
			String.valueOf(_newTimestamp(3, 0)),
			metrics.get("overdue.deadline.timestamp"));
	}

	@Test
	public void testExecuteCronHashedWithinSpan() throws Exception {
		Calendar calendar = _newCalendar(3, 40);

		long virtualCurrentTime = calendar.getTimeInMillis();

		calendar.add(Calendar.DAY_OF_MONTH, -1);

		calendar.set(Calendar.MINUTE, 47);

		_setJobJSONObject(
			_newConfigXML("H 3 * * *"),
			_newJobJSONObject(42, "SUCCESS", calendar.getTimeInMillis()));

		MonitorResult monitorResult = _executeAtTime(
			_newMonitorProperties(), virtualCurrentTime);

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test
	public void testExecuteCronInvalidSpec() throws Exception {
		_setJobJSONObject(
			_newConfigXML("@daily"),
			_newJobJSONObject(
				42, "SUCCESS",
				JenkinsResultsParserUtil.getCurrentTimeMillis()));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
		testEquals(
			"Unable to read the schedule for job " +
				"generate-reports-controller: Invalid cron spec: @daily",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteCronOverdue() throws Exception {
		_setJobJSONObject(
			_newConfigXML("*/15 * * * *"),
			_newJobJSONObject(
				42, "SUCCESS",
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(4 * 3600 * 1000)));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.WARN, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller last ran 4 hours ago, exceeding " +
				"its schedule of */15 * * * * plus a grace period of 30 " +
					"minutes",
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		Assert.assertNotNull(metrics.get("overdue.deadline.timestamp"));
	}

	@Test
	public void testExecuteCronUnreachableSchedule() throws Exception {
		_setJobJSONObject(
			_newConfigXML("0 0 31 2 *"),
			_newJobJSONObject(
				42, "SUCCESS",
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(4 * 3600 * 1000)));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller has the schedule 0 0 31 2 *, " +
				"which never comes round",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteCronWithinGracePeriod() throws Exception {
		_setJobJSONObject(
			_newConfigXML("*/15 * * * *"),
			_newJobJSONObject(
				42, "SUCCESS",
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(1200 * 1000)));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());
	}

	@Test
	public void testExecuteDisabled() throws Exception {
		JSONObject jobJSONObject = _newJobJSONObject(
			42, "SUCCESS", JenkinsResultsParserUtil.getCurrentTimeMillis());

		jobJSONObject.put("disabled", true);

		_setJobJSONObject(jobJSONObject);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller is disabled",
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("true", metrics.get("job.disabled"));
	}

	@Test
	public void testExecuteJobMissingFromMaster() throws Exception {
		UrlReader urlReader = mockUrlReader();

		JSONObject jobsJSONObject = new JSONObject(
		).put(
			"jobs",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"name", "some-other-job"
				)
			)
		);

		setUrlReaderOutput(
			jobsJSONObject.toString(), _MASTER_API_URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller was not found on http://test-9-1",
			monitorResult.getMessage());
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
					"number", RandomTestUtil.randomInt()
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
	public void testExecuteMissingSchedule() throws Exception {
		_setJobJSONObject(
			"<project><triggers/></project>",
			_newJobJSONObject(
				42, "SUCCESS",
				JenkinsResultsParserUtil.getCurrentTimeMillis() -
					(30L * 24 * 3600 * 1000)));

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller has no cadence and no schedule, " +
				"so it is not checked for being on time",
			monitorResult.getMessage());
	}

	@Test
	public void testExecuteNeverRun() throws Exception {
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
	public void testExecuteNotBuildable() throws Exception {
		JSONObject jobJSONObject = _newJobJSONObject(
			42, "SUCCESS", JenkinsResultsParserUtil.getCurrentTimeMillis());

		jobJSONObject.put("buildable", false);

		_setJobJSONObject(jobJSONObject);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Job generate-reports-controller is not buildable",
			monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("false", metrics.get("job.buildable"));
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
					"number", RandomTestUtil.randomInt()
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
			RandomTestUtil.randomString(), _MASTER_API_URL, urlReader);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
		testEquals(
			"Unable to read http://test-9-1: Unable to create JSON object",
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

	@Test
	public void testPrepareCycle() throws Exception {
		MasterResourceReader masterResourceReader =
			MasterResourceReader.getInstance(_MASTER_NAME);

		JobHealthMonitor jobHealthMonitor = _newJobHealthMonitor(
			_newMonitorProperties());

		jobHealthMonitor.prepareCycle();

		Assert.assertNotSame(
			masterResourceReader,
			MasterResourceReader.getInstance(_MASTER_NAME));
	}

	private MonitorResult _execute(Properties monitorProperties) {
		JobHealthMonitor jobHealthMonitor = _newJobHealthMonitor(
			monitorProperties);

		return jobHealthMonitor.execute();
	}

	private MonitorResult _executeAtTime(
		Properties monitorProperties, long virtualCurrentTime) {

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				virtualCurrentTime
			);

			return _execute(monitorProperties);
		}
	}

	private Calendar _newCalendar(int hourOfDay, int minute) {
		Calendar calendar = Calendar.getInstance();

		calendar.clear();

		calendar.set(2026, Calendar.AUGUST, 27, hourOfDay, minute, 0);

		return calendar;
	}

	private String _newConfigXML(String spec) {
		return JenkinsResultsParserUtil.combine(
			"<project><triggers><hudson.triggers.TimerTrigger><spec>", spec,
			"</spec></hudson.triggers.TimerTrigger></triggers></project>");
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

	private long _newTimestamp(int hourOfDay, int minute) {
		Calendar calendar = _newCalendar(hourOfDay, minute);

		return calendar.getTimeInMillis();
	}

	private void _setJobJSONObject(JSONObject jobJSONObject) throws Exception {
		_setJobJSONObject(_newConfigXML("0 3 * * *"), jobJSONObject);
	}

	private void _setJobJSONObject(String configXML, JSONObject jobJSONObject)
		throws Exception {

		UrlReader urlReader = mockUrlReader();

		String jobConfigURL =
			"http://test-9-1/job/generate-reports-controller/config.xml";

		setUrlReaderOutput(configXML, jobConfigURL, urlReader);

		JSONObject jobsJSONObject = new JSONObject(
		).put(
			"jobs",
			new JSONArray(
			).put(
				jobJSONObject.put("name", _JOB_NAME)
			)
		);

		setUrlReaderOutput(
			jobsJSONObject.toString(), _MASTER_API_URL, urlReader);
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

	private static final String _JOB_NAME = "generate-reports-controller";

	private static final String _MASTER_API_URL =
		"http://test-9-1/api/json?tree=jobs";

	private static final String _MASTER_NAME = "test-9-1";

}