/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsMasterTestUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.io.IOException;

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
public class ControllerResultMonitorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockEnvironment(Collections.emptyMap());

		JenkinsMasterTestUtil.getJenkinsMaster(
			_MASTER_NAME, "http://" + _MASTER_NAME);

		mockURLReaders();

		setURLReaderOutput(_newConfigXML("0 3 * * *"), _JOB_CONFIG_URL);

		_currentTimeMillis = JenkinsResultsParserUtil.getCurrentTimeMillis();
	}

	@Test
	public void testControllerResultMonitor() {
		_testControllerResultMonitorInvalidProperty(
			"monitor[a].threshold[builds.maximum]", "-1");
		_testControllerResultMonitorInvalidProperty(
			"monitor[a].threshold[builds.maximum]",
			RandomTestUtil.randomString());
		_testControllerResultMonitorInvalidProperty(
			"monitor[a].threshold[pending.maximum]", "-1");
		_testControllerResultMonitorInvalidProperty(
			"monitor[a].threshold[pending.maximum]",
			RandomTestUtil.randomString());

		_testControllerResultMonitorMissingProperty(
			"monitor[a].parameter[job.name]");
		_testControllerResultMonitorMissingProperty(
			"monitor[a].parameter[master.name]");
	}

	@Test
	public void testExecuteBranchUnchanged() throws Exception {
		_testExecute(
			"Job " + _JOB_NAME + " is OK", MonitorResult.Status.OK,
			_newBuildJSONObject(0, "", RandomTestUtil.randomInt(), null),
			_newBuildJSONObject(
				86400, _newAlreadyRanDescription(), RandomTestUtil.randomInt(),
				"SUCCESS"));
		_testExecute(
			"Job " + _JOB_NAME + " is OK", MonitorResult.Status.OK,
			_newBuildJSONObject(
				600, _newAlreadyRanDescription(), RandomTestUtil.randomInt(),
				"SUCCESS"));
		_testExecute(
			"Unable to find a result for job " + _JOB_NAME +
				" in its last 1 builds",
			MonitorResult.Status.UNKNOWN,
			_newBuildJSONObject(0, "", RandomTestUtil.randomInt(), null));
		_testExecute(
			"Unable to find a result for job " + _JOB_NAME +
				" in its last 1 builds",
			MonitorResult.Status.UNKNOWN,
			_newBuildJSONObject(
				600, "<strong>SKIPPED</strong> - Job is already running",
				RandomTestUtil.randomInt(), "SUCCESS"));
		_testExecute(
			"Unable to find a result for job " + _JOB_NAME +
				" in its last 1 builds",
			MonitorResult.Status.UNKNOWN,
			_newBuildJSONObject(
				600, _newExpireDescription(), RandomTestUtil.randomInt(),
				"SUCCESS"));
	}

	@Test
	public void testExecuteDescription() throws Exception {
		_testExecute(
			"Job " + _JOB_NAME + " build 1001 reported FAILURE for " +
				_getBuildURL(2001),
			MonitorResult.Status.CRITICAL,
			_newBuildJSONObject(
				600, _newResultDescription("", 2001, "FAILURE"), 1001,
				"SUCCESS"));
		_testExecute(
			"Job " + _JOB_NAME + " build 1002 reported FAILURE for " +
				_getBuildURL(2002),
			MonitorResult.Status.CRITICAL,
			_newBuildJSONObject(
				600, _newRetroactiveFailureDescription(2002), 1002, "SUCCESS"));
		_testExecute(
			"Job " + _JOB_NAME + " is OK", MonitorResult.Status.OK,
			_newBuildJSONObject(
				600,
				_newResultDescription(
					"green", RandomTestUtil.randomInt(), "SUCCESS"),
				RandomTestUtil.randomInt(), "SUCCESS"));
	}

	@Test
	public void testExecuteInProgress() throws Exception {
		_setDownstreamBuildJSONObject(true, 2001, null);

		_testExecute(
			"Job " + _JOB_NAME + " is OK", MonitorResult.Status.OK,
			_newBuildJSONObject(
				600, "<strong>SKIPPED</strong> - Job is already running",
				RandomTestUtil.randomInt(), "SUCCESS"),
			_newBuildJSONObject(
				86400, _newInProgressDescription(2001),
				RandomTestUtil.randomInt(), "SUCCESS"));

		_setDownstreamBuildJSONObject(true, 2002, null);

		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " build 1002 has been in progress for ",
				JenkinsResultsParserUtil.toDurationString(86401 * 1000), " at ",
				_getBuildURL(2002)),
			MonitorResult.Status.WARN,
			_newBuildJSONObject(
				600, "<strong>SKIPPED</strong> - Job is already running",
				RandomTestUtil.randomInt(), "SUCCESS"),
			_newBuildJSONObject(
				86401, _newInProgressDescription(2002), 1002, "SUCCESS"));

		_setDownstreamBuildJSONObject(false, 2003, "ABORTED");

		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " build 1003 invoked ", _getBuildURL(2003),
				", which finished with the result ABORTED without reporting ",
				"back"),
			MonitorResult.Status.CRITICAL,
			_newBuildJSONObject(
				600, _newInProgressDescription(2003), 1003, "SUCCESS"));

		_setDownstreamBuildJSONObject(false, 2004, "SUCCESS");

		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " build 1004 invoked ", _getBuildURL(2004),
				", which finished with the result SUCCESS without reporting ",
				"back"),
			MonitorResult.Status.CRITICAL,
			_newBuildJSONObject(
				600, _newInProgressDescription(2004), 1004, "SUCCESS"));

		setURLReaderException(
			new IOException(RandomTestUtil.randomString()),
			_getDownstreamBuildAPIURL(2005));

		_testExecute(
			"Unable to read " + _getBuildURL(2005),
			MonitorResult.Status.UNKNOWN,
			_newBuildJSONObject(
				600, _newInProgressDescription(2005), 1005, "SUCCESS"));
		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Unable to determine the downstream build of job ", _JOB_NAME,
				" build 1006"),
			MonitorResult.Status.UNKNOWN,
			_newBuildJSONObject(
				600, "<strong>IN PROGRESS</strong>", 1006, "SUCCESS"));
	}

	@Test
	public void testExecuteInQueue() throws Exception {
		_testExecute(
			"Job " + _JOB_NAME + " is OK", MonitorResult.Status.OK,
			_newBuildJSONObject(
				600, "<strong>SKIPPED</strong> - Job was already invoked",
				RandomTestUtil.randomInt(), "SUCCESS"),
			_newBuildJSONObject(
				86400, _newInQueueDescription(), RandomTestUtil.randomInt(),
				"SUCCESS"));
		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " build 1002 invoked its downstream build ",
				JenkinsResultsParserUtil.toDurationString(86401 * 1000),
				" ago and it has not started"),
			MonitorResult.Status.WARN,
			_newBuildJSONObject(
				600, "<strong>SKIPPED</strong> - Job was already invoked",
				RandomTestUtil.randomInt(), "SUCCESS"),
			_newBuildJSONObject(
				86401, _newInQueueDescription(), 1002, "SUCCESS"));
	}

	@Test
	public void testExecuteMissingTimestamp() throws Exception {
		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Unable to determine the last build timestamp for job ",
				_JOB_NAME, ". Job ", _JOB_NAME, " build 1001 reported FAILURE ",
				"for ", _getBuildURL(2001)),
			MonitorResult.Status.CRITICAL,
			new JSONObject(
			).put(
				"description", _newResultDescription("", 2001, "FAILURE")
			).put(
				"number", 1001
			).put(
				"result", "SUCCESS"
			).put(
				"timestamp", 0
			));
	}

	@Test
	public void testExecuteNeverRan() throws Exception {
		_testExecute(
			"Job " + _JOB_NAME + " has never run",
			MonitorResult.Status.CRITICAL);
	}

	@Test
	public void testExecuteNoDescription() throws Exception {
		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " build 1001 completed with the result ",
				"ABORTED without invoking a downstream build"),
			MonitorResult.Status.WARN,
			_newBuildJSONObject(600, "", 1001, "ABORTED"));
		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " build 1002 completed with the result ",
				"FAILURE without invoking a downstream build"),
			MonitorResult.Status.CRITICAL,
			_newBuildJSONObject(600, "", 1002, "FAILURE"));
		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " build 1003 completed with the result ",
				"UNSTABLE without invoking a downstream build"),
			MonitorResult.Status.CRITICAL,
			_newBuildJSONObject(600, "", 1003, "UNSTABLE"));
	}

	@Test
	public void testExecuteOK() throws Exception {
		setURLReaderOutput(
			new JSONObject(
			).put(
				"builds",
				new JSONArray(
				).put(
					_newBuildJSONObject(
						600,
						_newResultDescription(
							"green", RandomTestUtil.randomInt(), "SUCCESS"),
						1001, "SUCCESS")
				)
			).toString(),
			_JOB_API_URL + "?tree=builds%5Bdescription%2Cnumber%2Cresult%2C" +
				"timestamp%5D%7B0%2C30%7D");

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals("Job " + _JOB_NAME + " is OK", monitorResult.getMessage());
		testEquals(MonitorResult.Status.OK, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("600", metrics.get("last.build.age.seconds"));
		testEquals("SUCCESS", metrics.get("last.result"));
		testEquals("1001", metrics.get("last.result.build.number"));
		testEquals("0", metrics.get("skipped.builds.count"));
	}

	@Test
	public void testExecuteOverdue() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.clear();

		calendar.set(2026, Calendar.AUGUST, 27, 12, 0, 0);

		_currentTimeMillis = calendar.getTimeInMillis();

		_testExecute(
			"Job " + _JOB_NAME + " is OK", MonitorResult.Status.OK,
			_newBuildJSONObject(
				32400,
				_newResultDescription(
					"green", RandomTestUtil.randomInt(), "SUCCESS"),
				RandomTestUtil.randomInt(), "SUCCESS"));
		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " last ran ",
				JenkinsResultsParserUtil.toDurationString(32401L * 1000),
				" ago, exceeding its schedule of 0 3 * * * plus a grace ",
				"period of ",
				JenkinsResultsParserUtil.toDurationString(21600L * 1000)),
			MonitorResult.Status.WARN,
			_newBuildJSONObject(
				32401,
				_newResultDescription(
					"green", RandomTestUtil.randomInt(), "SUCCESS"),
				RandomTestUtil.randomInt(), "SUCCESS"));
	}

	@Test
	public void testExecuteReadFailure() throws Exception {
		setURLReaderException(
			new IOException(RandomTestUtil.randomString()), _JOB_API_URL);

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());

		String message = monitorResult.getMessage();

		Assert.assertTrue(
			message.startsWith(
				"Unable to read http://" + _MASTER_NAME + "/job/" + _JOB_NAME));

		setURLReaderOutput("{}", _JOB_API_URL);

		monitorResult = _execute(_newMonitorProperties());

		testEquals(
			JenkinsResultsParserUtil.combine(
				"Unable to read http://", _MASTER_NAME, "/job/", _JOB_NAME),
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
	}

	@Test
	public void testExecuteSchedule() throws Exception {
		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " last ran ",
				JenkinsResultsParserUtil.toDurationString(259200L * 1000),
				" ago, exceeding its schedule of 0 3 * * * plus a grace ",
				"period of ",
				JenkinsResultsParserUtil.toDurationString(21600L * 1000)),
			MonitorResult.Status.WARN,
			_newBuildJSONObject(
				259200,
				_newResultDescription(
					"green", RandomTestUtil.randomInt(), "SUCCESS"),
				RandomTestUtil.randomInt(), "SUCCESS"));
		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " last ran ",
				JenkinsResultsParserUtil.toDurationString(259200L * 1000),
				" ago, exceeding its schedule of 0 3 * * * plus a grace ",
				"period of ",
				JenkinsResultsParserUtil.toDurationString(21600L * 1000),
				". Job ", _JOB_NAME, " build 1002 reported FAILURE for ",
				_getBuildURL(2002)),
			MonitorResult.Status.CRITICAL,
			_newBuildJSONObject(
				259200, _newResultDescription("", 2002, "FAILURE"), 1002,
				"SUCCESS"));

		setURLReaderOutput(_newConfigXML("0 0 31 2 *"), _JOB_CONFIG_URL);

		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " has the schedule 0 0 31 2 *, which ",
				"never comes round"),
			MonitorResult.Status.UNKNOWN,
			_newBuildJSONObject(
				600,
				_newResultDescription(
					"green", RandomTestUtil.randomInt(), "SUCCESS"),
				RandomTestUtil.randomInt(), "SUCCESS"));

		setURLReaderOutput("<project />", _JOB_CONFIG_URL);

		_testExecute(
			JenkinsResultsParserUtil.combine(
				"Job ", _JOB_NAME, " has no schedule, so it is not checked ",
				"for being on time"),
			MonitorResult.Status.UNKNOWN,
			_newBuildJSONObject(
				600,
				_newResultDescription(
					"green", RandomTestUtil.randomInt(), "SUCCESS"),
				RandomTestUtil.randomInt(), "SUCCESS"));

		String exceptionMessage = RandomTestUtil.randomString();

		setURLReaderException(
			new IOException(exceptionMessage), _JOB_CONFIG_URL);

		MonitorResult monitorResult = _executeBuilds(
			_newBuildJSONObject(
				600,
				_newResultDescription(
					"green", RandomTestUtil.randomInt(), "SUCCESS"),
				RandomTestUtil.randomInt(), "SUCCESS"));

		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());

		String message = monitorResult.getMessage();

		Assert.assertTrue(message.contains(exceptionMessage));
		Assert.assertTrue(
			message.startsWith(
				"Unable to read the schedule for job " + _JOB_NAME + ": "));
	}

	@Test
	public void testExecuteSkipped() throws Exception {
		_testExecuteSkipped(
			"Job " + _JOB_NAME + " build 1001 reported FAILURE for " +
				_getBuildURL(2001),
			MonitorResult.Status.CRITICAL,
			_newBuildJSONObject(
				864000, _newResultDescription("", 2001, "FAILURE"), 1001,
				"SUCCESS"));
		_testExecuteSkipped(
			"Job " + _JOB_NAME + " is OK", MonitorResult.Status.OK,
			_newBuildJSONObject(
				864000,
				_newResultDescription(
					"green", RandomTestUtil.randomInt(), "SUCCESS"),
				RandomTestUtil.randomInt(), "SUCCESS"));
		_testExecuteSkipped(
			"Unable to find a result for job " + _JOB_NAME +
				" in its last 6 builds",
			MonitorResult.Status.UNKNOWN, null);

		_testExecute(
			"Job " + _JOB_NAME + " is OK", MonitorResult.Status.OK,
			_newBuildJSONObject(
				600,
				_newResultDescription(
					"green", RandomTestUtil.randomInt(), "SUCCESS"),
				RandomTestUtil.randomInt(), "SUCCESS"),
			_newBuildJSONObject(
				87000,
				_newResultDescription(
					"", RandomTestUtil.randomInt(), "FAILURE"),
				RandomTestUtil.randomInt(), "SUCCESS"));
	}

	@Test
	public void testGetReadsCount() {
		ControllerResultMonitor controllerResultMonitor =
			_newControllerResultMonitor(_newMonitorProperties());

		testEquals(3, controllerResultMonitor.getReadsCount());
	}

	private MonitorResult _execute(Properties monitorProperties) {
		ControllerResultMonitor controllerResultMonitor =
			_newControllerResultMonitor(monitorProperties);

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				_currentTimeMillis
			);

			return controllerResultMonitor.execute();
		}
	}

	private MonitorResult _executeBuilds(JSONObject... buildJSONObjects)
		throws Exception {

		JSONArray buildsJSONArray = new JSONArray();

		for (JSONObject buildJSONObject : buildJSONObjects) {
			buildsJSONArray.put(buildJSONObject);
		}

		setURLReaderOutput(
			new JSONObject(
			).put(
				"builds", buildsJSONArray
			).toString(),
			_JOB_API_URL);

		return _execute(_newMonitorProperties());
	}

	private String _getBuildURL(int number) {
		return _DOWNSTREAM_JOB_URL + "/" + number;
	}

	private String _getDownstreamBuildAPIURL(int number) {
		return "/job/archive-binaries-cache/" + number + "/api/json";
	}

	private String _newAlreadyRanDescription() {
		String sha = RandomTestUtil.randomSHA();

		return JenkinsResultsParserUtil.combine(
			"<strong>SKIPPED</strong> - <a href=\"https://github.com",
			"/liferay/liferay-portal/commit/", sha, "\">", sha.substring(0, 7),
			"</a> was already ran");
	}

	private JSONObject _newBuildJSONObject(
		long ageSeconds, String description, int number, String result) {

		return new JSONObject(
		).put(
			"description", description
		).put(
			"number", number
		).put(
			"result", (result == null) ? JSONObject.NULL : result
		).put(
			"timestamp", _currentTimeMillis - (ageSeconds * 1000)
		);
	}

	private String _newConfigXML(String spec) {
		return JenkinsResultsParserUtil.combine(
			"<project><triggers><hudson.triggers.TimerTrigger><spec>", spec,
			"</spec></hudson.triggers.TimerTrigger></triggers></project>");
	}

	private ControllerResultMonitor _newControllerResultMonitor(
		Properties monitorProperties) {

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		return new ControllerResultMonitor(monitorConfigs.get(0));
	}

	private String _newExpireDescription() {
		String inProgressDescription = _newInProgressDescription(
			RandomTestUtil.randomInt());

		return inProgressDescription.replace("IN PROGRESS", "EXPIRE");
	}

	private String _newInProgressDescription(int number) {
		return JenkinsResultsParserUtil.combine(
			"<strong>IN PROGRESS</strong> - <a href=\"", _getBuildURL(number),
			"/\">Build URL</a>");
	}

	private String _newInQueueDescription() {
		String sha = RandomTestUtil.randomSHA();

		return JenkinsResultsParserUtil.combine(
			"<a href=\"", _DOWNSTREAM_JOB_URL,
			"\"><strong>IN QUEUE</strong></a><ul><li><strong>Git ID:</strong> ",
			"<a href=\"https://github.com/liferay/liferay-portal/commit/", sha,
			"\">", sha.substring(0, 7), "</a></li></ul>");
	}

	private Properties _newMonitorProperties() {
		Properties monitorProperties = new Properties();

		monitorProperties.setProperty(
			"monitor[a].parameter[job.name]", _JOB_NAME);
		monitorProperties.setProperty(
			"monitor[a].parameter[master.name]", _MASTER_NAME);
		monitorProperties.setProperty("monitor[a].type", "controller-result");

		return monitorProperties;
	}

	private String _newResultDescription(
		String color, int number, String status) {

		String userContentURL = "https://" + RandomTestUtil.randomString();

		return JenkinsResultsParserUtil.combine(
			"<strong style=\"color: ", color, "\">", status,
			"</strong> - <a href=\"", _getBuildURL(number),
			"/\">Build URL</a> - <a href=\"", userContentURL,
			"/jenkins-report.html\">Jenkins Report</a> - <a href=\"",
			userContentURL, "/job-summary/index.html\">Job Summary</a>");
	}

	private String _newRetroactiveFailureDescription(int number) {
		String sha = RandomTestUtil.randomSHA();

		return JenkinsResultsParserUtil.combine(
			"<strong style=\"color: red\">FAILURE</strong> - ",
			_getBuildURL(number), "/<ul><li><strong>Git ID:</strong> ",
			"<a href=\"https://github.com/liferay/liferay-portal/commit/", sha,
			"\">", sha.substring(0, 7), "</a></li></ul>");
	}

	private void _setDownstreamBuildJSONObject(
			boolean building, int number, String result)
		throws Exception {

		setURLReaderOutput(
			new JSONObject(
			).put(
				"building", building
			).put(
				"result", (result == null) ? JSONObject.NULL : result
			).toString(),
			_getDownstreamBuildAPIURL(number));
	}

	private void _testControllerResultMonitorInvalidProperty(
		String name, String value) {

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(name, value);

		try {
			_newControllerResultMonitor(monitorProperties);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testControllerResultMonitorMissingProperty(String name) {
		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.remove(name);

		try {
			_newControllerResultMonitor(monitorProperties);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testExecute(
			String expectedMessage, MonitorResult.Status expectedStatus,
			JSONObject... buildJSONObjects)
		throws Exception {

		MonitorResult monitorResult = _executeBuilds(buildJSONObjects);

		testEquals(expectedMessage, monitorResult.getMessage());
		testEquals(expectedStatus, monitorResult.getStatus());
	}

	private void _testExecuteSkipped(
			String expectedMessage, MonitorResult.Status expectedStatus,
			JSONObject resultBuildJSONObject)
		throws Exception {

		JSONObject[] buildJSONObjects = {
			_newBuildJSONObject(0, "", RandomTestUtil.randomInt(), null),
			_newBuildJSONObject(
				86400, "<strong>SKIPPED</strong> - Job was already invoked",
				RandomTestUtil.randomInt(), "SUCCESS"),
			_newBuildJSONObject(
				172800, "<strong>SKIPPED</strong> - Job is already running",
				RandomTestUtil.randomInt(), "SUCCESS"),
			_newBuildJSONObject(
				259200, _newAlreadyRanDescription(), RandomTestUtil.randomInt(),
				"SUCCESS"),
			_newBuildJSONObject(
				345600, _newExpireDescription(), RandomTestUtil.randomInt(),
				"SUCCESS"),
			_newBuildJSONObject(
				432000, "", RandomTestUtil.randomInt(), "SUCCESS")
		};

		if (resultBuildJSONObject != null) {
			JSONObject[] withResultBuildJSONObjects =
				new JSONObject[buildJSONObjects.length + 1];

			System.arraycopy(
				buildJSONObjects, 0, withResultBuildJSONObjects, 0,
				buildJSONObjects.length);

			withResultBuildJSONObjects[buildJSONObjects.length] =
				resultBuildJSONObject;

			buildJSONObjects = withResultBuildJSONObjects;
		}

		MonitorResult monitorResult = _executeBuilds(buildJSONObjects);

		testEquals(expectedMessage, monitorResult.getMessage());
		testEquals(expectedStatus, monitorResult.getStatus());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals("6", metrics.get("skipped.builds.count"));
	}

	private static final String _DOWNSTREAM_JOB_URL =
		"https://test-1-1.liferay.com/job/archive-binaries-cache";

	private static final String _JOB_API_URL =
		"http://test-9-1/job/archive-binaries-cache-controller" +
			"(master)/api/json";

	private static final String _JOB_CONFIG_URL =
		"http://test-9-1/job/archive-binaries-cache-controller" +
			"(master)/config.xml";

	private static final String _JOB_NAME =
		"archive-binaries-cache-controller(master)";

	private static final String _MASTER_NAME = "test-9-1";

	private long _currentTimeMillis;

}