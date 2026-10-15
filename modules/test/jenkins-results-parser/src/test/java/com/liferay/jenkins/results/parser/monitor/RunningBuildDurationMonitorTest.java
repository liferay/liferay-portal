/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsCohort;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsMasterTestUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class RunningBuildDurationMonitorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockEnvironment(Collections.emptyMap());

		JenkinsMasterTestUtil.getJenkinsCohortProperties(_COHORT_NAME, 2);

		mockURLReaders();

		_currentTimeMillis = JenkinsResultsParserUtil.getCurrentTimeMillis();
	}

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		JenkinsMasterTestUtil.resetCaches();

		JenkinsResultsParserUtil.setBuildProperties(new Properties());
	}

	@Test
	public void testExecute() throws Exception {
		_setComputerAPIJSONObject(_MASTER_NAME_1);
		_setComputerAPIJSONObject(_MASTER_NAME_2);

		_testExecute(
			0, _getOKMessage(_SECONDS_DAY), 0, MonitorResult.Status.OK);

		_setComputerAPIJSONObject(
			_MASTER_NAME_1,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(
					_newBuildURL(_MASTER_NAME_1), RandomTestUtil.randomString(),
					_SECONDS_DAY)));
		_setComputerAPIJSONObject(
			_MASTER_NAME_2,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(
					_newBuildURL(_MASTER_NAME_2), RandomTestUtil.randomString(),
					3600)));

		_testExecute(
			0, _getOKMessage(_SECONDS_DAY), 2, MonitorResult.Status.OK);

		String buildURL = _newBuildURL(_MASTER_NAME_1);
		String fullDisplayName = RandomTestUtil.randomString();

		_setComputerAPIJSONObject(
			_MASTER_NAME_1,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(
					buildURL, fullDisplayName, _SECONDS_DAY + 1)));

		_testExecute(
			1,
			JenkinsResultsParserUtil.combine(
				_getCountMessage("1 build", _SECONDS_DAY), ". ",
				_getBuildMessage(
					buildURL, fullDisplayName, _MASTER_NAME_1,
					_SECONDS_DAY + 1)),
			2, MonitorResult.Status.CRITICAL);

		String agentBuildURL = _newBuildURL(_MASTER_NAME_1);
		String agentFullDisplayName = RandomTestUtil.randomString();
		String flyweightBuildURL = _newBuildURL(_MASTER_NAME_1);
		String flyweightFullDisplayName = RandomTestUtil.randomString();

		_setComputerAPIJSONObject(
			_MASTER_NAME_1,
			JenkinsMasterTestUtil.getBuiltInComputerJSONObject(
				_newExecutorJSONObject(
					flyweightBuildURL, flyweightFullDisplayName,
					3 * _SECONDS_DAY)),
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(
					agentBuildURL, agentFullDisplayName, 2 * _SECONDS_DAY),
				_newExecutorJSONObject(
					_newBuildURL(_MASTER_NAME_1), RandomTestUtil.randomString(),
					3600)));

		String secondMasterBuildURL = _newBuildURL(_MASTER_NAME_2);
		String secondMasterFullDisplayName = RandomTestUtil.randomString();

		_setComputerAPIJSONObject(
			_MASTER_NAME_2,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(
					secondMasterBuildURL, secondMasterFullDisplayName,
					_SECONDS_DAY + 60)));

		_testExecute(
			3,
			JenkinsResultsParserUtil.join(
				". ", _getCountMessage("3 builds", _SECONDS_DAY),
				_getBuildMessage(
					flyweightBuildURL, flyweightFullDisplayName, _MASTER_NAME_1,
					3 * _SECONDS_DAY),
				_getBuildMessage(
					agentBuildURL, agentFullDisplayName, _MASTER_NAME_1,
					2 * _SECONDS_DAY),
				_getBuildMessage(
					secondMasterBuildURL, secondMasterFullDisplayName,
					_MASTER_NAME_2, _SECONDS_DAY + 60)),
			4, MonitorResult.Status.CRITICAL);

		JSONObject executorJSONObject = _newExecutorJSONObject(
			_newBuildURL(_MASTER_NAME_1), RandomTestUtil.randomString(),
			2 * _SECONDS_DAY);

		JSONObject currentExecutableJSONObject =
			executorJSONObject.getJSONObject("currentExecutable");

		currentExecutableJSONObject.put("building", false);

		_setComputerAPIJSONObject(
			_MASTER_NAME_1,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(), executorJSONObject));
		_setComputerAPIJSONObject(_MASTER_NAME_2);

		_testExecute(
			0, _getOKMessage(_SECONDS_DAY), 0, MonitorResult.Status.OK);
	}

	@Test
	public void testExecuteJenkinsMasters() throws Exception {
		String buildURL = _newBuildURL(_MASTER_NAME_2);
		String fullDisplayName = RandomTestUtil.randomString();

		_setComputerAPIJSONObject(_MASTER_NAME_1);
		_setComputerAPIJSONObject(
			_MASTER_NAME_2,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(
					buildURL, fullDisplayName, _SECONDS_DAY + 1)));

		ReflectionTestUtil.setFieldValue(
			JenkinsMaster.getInstance(_MASTER_NAME_2), "_blacklisted", true);

		JenkinsCohort jenkinsCohort = JenkinsCohort.getInstance(_COHORT_NAME);

		List<JenkinsMaster> jenkinsMasters = jenkinsCohort.getJenkinsMasters();

		Assert.assertFalse(
			jenkinsMasters.contains(JenkinsMaster.getInstance(_MASTER_NAME_2)));

		String expectedMessage = JenkinsResultsParserUtil.combine(
			_getCountMessage("1 build", _SECONDS_DAY), ". ",
			_getBuildMessage(
				buildURL, fullDisplayName, _MASTER_NAME_2, _SECONDS_DAY + 1));

		_testExecute(1, expectedMessage, 1, MonitorResult.Status.CRITICAL);

		verifyURLReaderAttemptsCount(
			1, "http://" + _MASTER_NAME_2 + "/computer/api/json");

		JenkinsMasterTestUtil.getJenkinsCohortProperties(_COHORT_NAME, 2);

		mockURLReaders();

		_setComputerAPIJSONObject(_MASTER_NAME_1);
		_setComputerAPIJSONObject(
			_MASTER_NAME_2,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(
					buildURL, fullDisplayName, _SECONDS_DAY + 1)));

		jenkinsCohort = JenkinsCohort.getInstance(_COHORT_NAME);

		jenkinsMasters = jenkinsCohort.getJenkinsMasters();

		Assert.assertTrue(
			jenkinsMasters.contains(JenkinsMaster.getInstance(_MASTER_NAME_2)));

		ReflectionTestUtil.setFieldValue(
			JenkinsMaster.getInstance(_MASTER_NAME_2), "_blacklisted", true);

		_testExecute(1, expectedMessage, 1, MonitorResult.Status.CRITICAL);

		verifyURLReaderAttemptsCount(
			1, "http://" + _MASTER_NAME_2 + "/computer/api/json");
	}

	@Test
	public void testExecuteNoJenkinsMasters() {
		Properties monitorProperties = _newMonitorProperties();

		String cohortName = RandomTestUtil.randomString();

		monitorProperties.setProperty(
			"monitor[a].parameter[cohort.name]", cohortName);

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(
			"Unable to find a master in cohort " + cohortName,
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());
	}

	@Test
	public void testExecuteThreshold() throws Exception {
		String buildURL = _newBuildURL(_MASTER_NAME_1);
		String fullDisplayName = RandomTestUtil.randomString();

		_setComputerAPIJSONObject(
			_MASTER_NAME_1,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(buildURL, fullDisplayName, 7200)));

		_setComputerAPIJSONObject(_MASTER_NAME_2);

		_testExecuteThreshold(
			JenkinsResultsParserUtil.combine(
				_getCountMessage("1 build", 0), ". ",
				_getBuildMessage(
					buildURL, fullDisplayName, _MASTER_NAME_1, 7200)),
			MonitorResult.Status.CRITICAL, 0);
		_testExecuteThreshold(
			JenkinsResultsParserUtil.combine(
				_getCountMessage("1 build", 7199), ". ",
				_getBuildMessage(
					buildURL, fullDisplayName, _MASTER_NAME_1, 7200)),
			MonitorResult.Status.CRITICAL, 7199);
		_testExecuteThreshold(
			_getOKMessage(7200), MonitorResult.Status.OK, 7200);
	}

	@Test
	public void testExecuteUnreadableJenkinsMaster() throws Exception {
		String exceptionMessage = RandomTestUtil.randomString();

		_setComputerAPIJSONObject(_MASTER_NAME_1);

		setURLReaderException(
			new IOException(exceptionMessage),
			"http://" + _MASTER_NAME_2 + "/computer/api/json");

		String unreadableMessage = JenkinsResultsParserUtil.combine(
			"Unable to read running builds on ", _MASTER_NAME_2, ": ",
			exceptionMessage);

		_testExecute(0, unreadableMessage, 0, MonitorResult.Status.UNKNOWN);

		setURLReaderOutput(
			"{}", "http://" + _MASTER_NAME_2 + "/computer/api/json");

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		String message = monitorResult.getMessage();

		Assert.assertTrue(
			message,
			message.startsWith(
				"Unable to read running builds on " + _MASTER_NAME_2 + ": "));

		testEquals(MonitorResult.Status.UNKNOWN, monitorResult.getStatus());

		String buildURL = _newBuildURL(_MASTER_NAME_1);
		String fullDisplayName = RandomTestUtil.randomString();

		_setComputerAPIJSONObject(
			_MASTER_NAME_1,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(
					buildURL, fullDisplayName, _SECONDS_DAY + 1)));

		setURLReaderException(
			new IOException(exceptionMessage),
			"http://" + _MASTER_NAME_2 + "/computer/api/json");

		_testExecute(
			1,
			JenkinsResultsParserUtil.join(
				". ", _getCountMessage("1 build", _SECONDS_DAY),
				_getBuildMessage(
					buildURL, fullDisplayName, _MASTER_NAME_1,
					_SECONDS_DAY + 1),
				unreadableMessage),
			1, MonitorResult.Status.CRITICAL);
	}

	@Test
	public void testGetReadsCount() throws Exception {
		JenkinsMasterTestUtil.getJenkinsCohortProperties(_COHORT_NAME, 1);

		mockURLReaders();

		_setComputerAPIJSONObject(_MASTER_NAME_1);

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].timeout", "40");

		_execute(monitorProperties);

		verifyURLReaderRead(false, 1, 18000);

		JenkinsMasterTestUtil.getJenkinsCohortProperties(_COHORT_NAME, 3);

		mockURLReaders();

		_setComputerAPIJSONObject(_COHORT_NAME + "-3");
		_setComputerAPIJSONObject(_MASTER_NAME_1);
		_setComputerAPIJSONObject(_MASTER_NAME_2);

		RunningBuildDurationMonitor runningBuildDurationMonitor =
			_newRunningBuildDurationMonitor(_newMonitorProperties());

		_execute(runningBuildDurationMonitor);

		testEquals(3, runningBuildDurationMonitor.getReadsCount());
	}

	@Test
	public void testRunningBuildDurationMonitor() {
		_testRunningBuildDurationMonitorInvalidProperty(
			"monitor[a].threshold[build.duration.maximum]", "-1");
		_testRunningBuildDurationMonitorInvalidProperty(
			"monitor[a].threshold[build.duration.maximum]",
			RandomTestUtil.randomString());

		_testRunningBuildDurationMonitorMissingProperty(
			"monitor[a].parameter[cohort.name]");
	}

	private MonitorResult _execute(Properties monitorProperties) {
		return _execute(_newRunningBuildDurationMonitor(monitorProperties));
	}

	private MonitorResult _execute(
		RunningBuildDurationMonitor runningBuildDurationMonitor) {

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				_currentTimeMillis
			);

			return runningBuildDurationMonitor.execute();
		}
	}

	private String _getBuildMessage(
		String buildURL, String fullDisplayName, String masterName,
		long seconds) {

		return JenkinsResultsParserUtil.combine(
			masterName, " ", fullDisplayName, " has been running for ",
			JenkinsResultsParserUtil.toDurationString(seconds * 1000), " at ",
			buildURL);
	}

	private String _getCountMessage(String builds, long seconds) {
		return JenkinsResultsParserUtil.combine(
			builds, " in cohort ", _COHORT_NAME, " over the ceiling of ",
			JenkinsResultsParserUtil.toDurationString(seconds * 1000));
	}

	private String _getOKMessage(long seconds) {
		return JenkinsResultsParserUtil.combine(
			"No build in cohort ", _COHORT_NAME, " has run longer than ",
			JenkinsResultsParserUtil.toDurationString(seconds * 1000));
	}

	private String _newBuildURL(String masterName) {
		return JenkinsResultsParserUtil.combine(
			"http://", masterName, "/job/", RandomTestUtil.randomString(), "/",
			String.valueOf(RandomTestUtil.randomInt()), "/");
	}

	private JSONObject _newExecutorJSONObject(
		String buildURL, String fullDisplayName, long seconds) {

		return JenkinsMasterTestUtil.getExecutorJSONObject(
			buildURL, RandomTestUtil.randomLong(), fullDisplayName, false,
			_currentTimeMillis - (seconds * 1000));
	}

	private Properties _newMonitorProperties() {
		Properties monitorProperties = new Properties();

		monitorProperties.setProperty(
			"monitor[a].parameter[cohort.name]", _COHORT_NAME);
		monitorProperties.setProperty(
			"monitor[a].type", "running-build-duration");

		return monitorProperties;
	}

	private RunningBuildDurationMonitor _newRunningBuildDurationMonitor(
		Properties monitorProperties) {

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		return new RunningBuildDurationMonitor(monitorConfigs.get(0));
	}

	private void _setComputerAPIJSONObject(
			String masterName, JSONObject... computerJSONObjects)
		throws Exception {

		setURLReaderOutput(
			String.valueOf(
				JenkinsMasterTestUtil.getComputerAPIJSONObject(
					RandomTestUtil.randomInt(), computerJSONObjects)),
			"http://" + masterName + "/computer/api/json");
	}

	private void _testExecute(
		int expectedLongRunningBuildsCount, String expectedMessage,
		int expectedRunningBuildsCount, MonitorResult.Status expectedStatus) {

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		testEquals(expectedMessage, monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals(
			String.valueOf(expectedLongRunningBuildsCount),
			metrics.get("long.running.builds.count"));
		testEquals(
			String.valueOf(expectedRunningBuildsCount),
			metrics.get("running.builds.count"));

		testEquals(expectedStatus, monitorResult.getStatus());
	}

	private void _testExecuteThreshold(
		String expectedMessage, MonitorResult.Status expectedStatus,
		long threshold) {

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].threshold[build.duration.maximum]",
			String.valueOf(threshold));

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(expectedMessage, monitorResult.getMessage());
		testEquals(expectedStatus, monitorResult.getStatus());
	}

	private void _testRunningBuildDurationMonitorInvalidProperty(
		String name, String value) {

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(name, value);

		try {
			_newRunningBuildDurationMonitor(monitorProperties);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testRunningBuildDurationMonitorMissingProperty(String name) {
		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.remove(name);

		try {
			_newRunningBuildDurationMonitor(monitorProperties);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private static final String _COHORT_NAME = "test-9";

	private static final String _MASTER_NAME_1 = _COHORT_NAME + "-1";

	private static final String _MASTER_NAME_2 = _COHORT_NAME + "-2";

	private static final int _SECONDS_DAY = 24 * 60 * 60;

	private long _currentTimeMillis;

}