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

import org.json.JSONArray;
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
public class QueuedBuildDurationMonitorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockEnvironment(Collections.emptyMap());

		JenkinsMasterTestUtil.getJenkinsCohortProperties(_COHORT_NAME, 2);

		mockURLReaders();

		_currentTimeMillis = JenkinsResultsParserUtil.getCurrentTimeMillis();

		_setUpMaster(_MASTER_NAME_1);
		_setUpMaster(_MASTER_NAME_2);
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
		_testExecute(_getOKMessage(), 0, MonitorResult.Status.OK, 0);

		String taskName = RandomTestUtil.randomString();

		_setQueueAPIJSONObject(
			_MASTER_NAME_1,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", _SECONDS_STUCK, taskName));

		_testExecute(
			_getStuckMessage(
				1, _getStuckQueueItemMessage(_MASTER_NAME_1, taskName)),
			1, MonitorResult.Status.CRITICAL, 1);

		_setComputerAPIJSONObject(
			_MASTER_NAME_1,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(59 * 60)));

		_testExecute(_getOKMessage(), 1, MonitorResult.Status.OK, 0);

		long queueDurationMaximumSeconds = 3 * _SECONDS_HOUR;

		_setQueueAPIJSONObject(
			_MASTER_NAME_1,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", queueDurationMaximumSeconds,
				RandomTestUtil.randomString()),
			_newQueueItemJSONObject(
				"hudson.model.Queue$BlockedItem", 10 * _SECONDS_HOUR,
				RandomTestUtil.randomString()));

		_setComputerAPIJSONObject(_MASTER_NAME_1);

		_testExecute(_getOKMessage(), 1, MonitorResult.Status.OK, 0);

		taskName = RandomTestUtil.randomString();

		_setQueueAPIJSONObject(
			_MASTER_NAME_1,
			_newQueueItemJSONObject(
				"hudson.model.Queue$WaitingItem",
				queueDurationMaximumSeconds + 1, taskName));

		_testExecute(
			_getStuckMessage(
				1,
				JenkinsResultsParserUtil.combine(
					taskName, " on ", _getRemoteURL(_MASTER_NAME_1), " (",
					JenkinsResultsParserUtil.toDurationString(
						(queueDurationMaximumSeconds + 1) * 1000L),
					")")),
			1, MonitorResult.Status.CRITICAL, 1);
	}

	@Test
	public void testExecuteBuildStartGap() throws Exception {
		String taskName = RandomTestUtil.randomString();

		_setQueueAPIJSONObject(
			_MASTER_NAME_1,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", _SECONDS_STUCK, taskName));

		String expectedMessage = _getStuckMessage(
			1, _getStuckQueueItemMessage(_MASTER_NAME_1, taskName));

		_setComputerAPIJSONObject(
			_MASTER_NAME_1,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(_SECONDS_HOUR)));

		_testExecute(_getOKMessage(), 1, MonitorResult.Status.OK, 0);

		_setComputerAPIJSONObject(
			_MASTER_NAME_1,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(),
				_newExecutorJSONObject(_SECONDS_HOUR + 1)));

		_testExecute(expectedMessage, 1, MonitorResult.Status.CRITICAL, 1);

		JSONObject executorJSONObject = _newExecutorJSONObject(60);

		JSONObject currentExecutableJSONObject =
			executorJSONObject.getJSONObject("currentExecutable");

		currentExecutableJSONObject.put("building", false);

		_setComputerAPIJSONObject(
			_MASTER_NAME_1,
			JenkinsMasterTestUtil.getBuiltInComputerJSONObject(
				_newExecutorJSONObject(60)),
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(), executorJSONObject));

		_testExecute(expectedMessage, 1, MonitorResult.Status.CRITICAL, 1);

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].threshold[build.start.gap.maximum]", "0");

		_setComputerAPIJSONObject(
			_MASTER_NAME_1,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(), _newExecutorJSONObject(1)));

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(expectedMessage, monitorResult.getMessage());
	}

	@Test
	public void testExecuteExcludedJenkinsMasters() throws Exception {
		String taskName = RandomTestUtil.randomString();

		_setQueueAPIJSONObject(
			_MASTER_NAME_1,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", _SECONDS_STUCK, taskName));

		_setQueueAPIJSONObject(
			_MASTER_NAME_2,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", _SECONDS_STUCK,
				RandomTestUtil.randomString()));

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].parameter[excluded.master.names]",
			JenkinsResultsParserUtil.combine(
				" ", _MASTER_NAME_2, " , ", RandomTestUtil.randomString()));

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(
			_getStuckMessage(
				1, _getStuckQueueItemMessage(_MASTER_NAME_1, taskName)),
			monitorResult.getMessage());

		verifyURLReaderAttemptsCount(
			0,
			JenkinsResultsParserUtil.combine(
				"http://", _MASTER_NAME_2, "/queue/api/json"));

		monitorProperties.setProperty(
			"monitor[a].parameter[excluded.master.names]",
			JenkinsResultsParserUtil.combine(
				_MASTER_NAME_1, ",", _MASTER_NAME_2));

		monitorResult = _execute(monitorProperties);

		testEquals(
			"Unable to find a master in cohort " + _COHORT_NAME,
			monitorResult.getMessage());
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
	}

	@Test
	public void testExecuteJenkinsMasters() throws Exception {
		String taskName = RandomTestUtil.randomString();

		_setQueueAPIJSONObject(
			_MASTER_NAME_2,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", _SECONDS_STUCK, taskName));

		ReflectionTestUtil.setFieldValue(
			JenkinsMaster.getInstance(_MASTER_NAME_2), "_blacklisted", true);

		JenkinsCohort jenkinsCohort = JenkinsCohort.getInstance(_COHORT_NAME);

		List<JenkinsMaster> jenkinsMasters = jenkinsCohort.getJenkinsMasters();

		Assert.assertFalse(
			jenkinsMasters.contains(JenkinsMaster.getInstance(_MASTER_NAME_2)));

		String expectedMessage = _getStuckMessage(
			1, _getStuckQueueItemMessage(_MASTER_NAME_2, taskName));

		_testExecute(expectedMessage, 1, MonitorResult.Status.CRITICAL, 1);

		verifyURLReaderAttemptsCount(
			1,
			JenkinsResultsParserUtil.combine(
				"http://", _MASTER_NAME_2, "/queue/api/json"));

		JenkinsMasterTestUtil.getJenkinsCohortProperties(_COHORT_NAME, 2);

		mockURLReaders();

		_setUpMaster(_MASTER_NAME_1);
		_setUpMaster(_MASTER_NAME_2);

		_setQueueAPIJSONObject(
			_MASTER_NAME_2,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", _SECONDS_STUCK, taskName));

		jenkinsCohort = JenkinsCohort.getInstance(_COHORT_NAME);

		jenkinsMasters = jenkinsCohort.getJenkinsMasters();

		Assert.assertTrue(
			jenkinsMasters.contains(JenkinsMaster.getInstance(_MASTER_NAME_2)));

		ReflectionTestUtil.setFieldValue(
			JenkinsMaster.getInstance(_MASTER_NAME_2), "_blacklisted", true);

		_testExecute(expectedMessage, 1, MonitorResult.Status.CRITICAL, 1);

		verifyURLReaderAttemptsCount(
			1,
			JenkinsResultsParserUtil.combine(
				"http://", _MASTER_NAME_2, "/queue/api/json"));
	}

	@Test
	public void testExecuteMessage() throws Exception {
		String taskName1 = RandomTestUtil.randomString();
		String taskName2 = RandomTestUtil.randomString();

		_setQueueAPIJSONObject(
			_MASTER_NAME_1,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", (3 * _SECONDS_HOUR) + 600,
				taskName1),
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", (3 * _SECONDS_HOUR) + 1800,
				taskName2));

		String taskName3 = RandomTestUtil.randomString();

		_setQueueAPIJSONObject(
			_MASTER_NAME_2,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", 4 * _SECONDS_HOUR,
				taskName3));

		_testExecute(
			_getStuckMessage(
				3,
				JenkinsResultsParserUtil.combine(
					taskName3, " on ", _getRemoteURL(_MASTER_NAME_2),
					" (4 hours), ", taskName2, " on ",
					_getRemoteURL(_MASTER_NAME_1), " (3 hours 30 minutes), ",
					taskName1, " on ", _getRemoteURL(_MASTER_NAME_1),
					" (3 hours 10 minutes)")),
			3, MonitorResult.Status.CRITICAL, 3);

		String taskName4 = RandomTestUtil.randomString();
		String taskName5 = RandomTestUtil.randomString();
		String taskName6 = RandomTestUtil.randomString();

		_setQueueAPIJSONObject(
			_MASTER_NAME_1,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", (3 * _SECONDS_HOUR) + 600,
				taskName4),
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", (3 * _SECONDS_HOUR) + 1800,
				taskName5),
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", (3 * _SECONDS_HOUR) + 1200,
				taskName6));

		_setQueueAPIJSONObject(
			_MASTER_NAME_2,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", 5 * _SECONDS_HOUR,
				RandomTestUtil.randomString()));

		_testExecute(
			_getStuckMessage(
				4,
				JenkinsResultsParserUtil.combine(
					"1 on ", _getRemoteURL(_MASTER_NAME_2),
					" (oldest 5 hours), 3 on ", _getRemoteURL(_MASTER_NAME_1),
					" (oldest 3 hours 30 minutes)")),
			4, MonitorResult.Status.CRITICAL, 4);

		_setComputerAPIJSONObject(
			_MASTER_NAME_2,
			JenkinsMasterTestUtil.getComputerJSONObject(
				RandomTestUtil.randomString(), _newExecutorJSONObject(600)));

		_testExecute(
			_getStuckMessage(
				3,
				JenkinsResultsParserUtil.combine(
					taskName5, " on ", _getRemoteURL(_MASTER_NAME_1),
					" (3 hours 30 minutes), ", taskName6, " on ",
					_getRemoteURL(_MASTER_NAME_1), " (3 hours 20 minutes), ",
					taskName4, " on ", _getRemoteURL(_MASTER_NAME_1),
					" (3 hours 10 minutes)")),
			4, MonitorResult.Status.CRITICAL, 3);
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
		testEquals(MonitorResult.Status.CRITICAL, monitorResult.getStatus());
	}

	@Test
	public void testExecuteThreshold() throws Exception {
		_setQueueAPIJSONObject(
			_MASTER_NAME_1,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", 7200,
				RandomTestUtil.randomString()));

		_testExecuteThreshold(MonitorResult.Status.CRITICAL, 0);
		_testExecuteThreshold(MonitorResult.Status.CRITICAL, 7199);
		_testExecuteThreshold(MonitorResult.Status.OK, 7200);
	}

	@Test
	public void testExecuteUnreadableBlacklistedJenkinsMaster()
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			JenkinsMaster.getInstance(_MASTER_NAME_2), "_blacklisted", true);

		setURLReaderException(
			new IOException(RandomTestUtil.randomString()),
			JenkinsResultsParserUtil.combine(
				"http://", _MASTER_NAME_2, "/queue/api/json"));

		_testExecute(_getOKMessage(), 0, MonitorResult.Status.OK, 0);

		_setQueueAPIJSONObject(_MASTER_NAME_2);

		setURLReaderException(
			new IOException(RandomTestUtil.randomString()),
			JenkinsResultsParserUtil.combine(
				"http://", _MASTER_NAME_2, "/computer/api/json"));

		_testExecute(_getOKMessage(), 0, MonitorResult.Status.OK, 0);
	}

	@Test
	public void testExecuteUnreadableJenkinsMaster() throws Exception {
		String exceptionMessage = RandomTestUtil.randomString();

		setURLReaderException(
			new IOException(exceptionMessage),
			JenkinsResultsParserUtil.combine(
				"http://", _MASTER_NAME_2, "/queue/api/json"));

		String queueMessage = JenkinsResultsParserUtil.combine(
			"Unable to read the queue on ", _getRemoteURL(_MASTER_NAME_2), ": ",
			exceptionMessage);

		_testExecute(queueMessage, 0, MonitorResult.Status.CRITICAL, 0);

		setURLReaderException(
			new IOException(exceptionMessage),
			JenkinsResultsParserUtil.combine(
				"http://", _MASTER_NAME_2, "/computer/api/json"));

		String runningBuildsMessage = JenkinsResultsParserUtil.combine(
			"Unable to read running builds on ", _getRemoteURL(_MASTER_NAME_2),
			": ", exceptionMessage);

		_testExecute(
			queueMessage + ". " + runningBuildsMessage, 0,
			MonitorResult.Status.CRITICAL, 0);

		_setQueueAPIJSONObject(
			_MASTER_NAME_2,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", _SECONDS_STUCK,
				RandomTestUtil.randomString()));

		String taskName = RandomTestUtil.randomString();

		_setQueueAPIJSONObject(
			_MASTER_NAME_1,
			_newQueueItemJSONObject(
				"hudson.model.Queue$BuildableItem", _SECONDS_STUCK, taskName));

		_testExecute(
			JenkinsResultsParserUtil.combine(
				_getStuckMessage(
					1, _getStuckQueueItemMessage(_MASTER_NAME_1, taskName)),
				". ", runningBuildsMessage),
			1, MonitorResult.Status.CRITICAL, 1);
	}

	@Test
	public void testGetReadsCount() throws Exception {
		JenkinsMasterTestUtil.getJenkinsCohortProperties(_COHORT_NAME, 1);

		mockURLReaders();

		_setUpMaster(_MASTER_NAME_1);

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty("monitor[a].timeout", "40");

		QueuedBuildDurationMonitor queuedBuildDurationMonitor =
			_newQueuedBuildDurationMonitor(monitorProperties);

		_execute(queuedBuildDurationMonitor);

		testEquals(2, queuedBuildDurationMonitor.getReadsCount());

		verifyURLReaderRead(false, 2, 1, 9000);
	}

	@Test
	public void testQueuedBuildDurationMonitor() {
		_testQueuedBuildDurationMonitorInvalidProperty(
			"monitor[a].threshold[build.start.gap.maximum]", "-1");
		_testQueuedBuildDurationMonitorInvalidProperty(
			"monitor[a].threshold[build.start.gap.maximum]",
			RandomTestUtil.randomString());
		_testQueuedBuildDurationMonitorInvalidProperty(
			"monitor[a].threshold[queue.duration.maximum]", "-1");
		_testQueuedBuildDurationMonitorInvalidProperty(
			"monitor[a].threshold[queue.duration.maximum]",
			RandomTestUtil.randomString());
		_testQueuedBuildDurationMonitorMissingProperty(
			"monitor[a].parameter[cohort.name]");
	}

	private MonitorResult _execute(Properties monitorProperties) {
		return _execute(_newQueuedBuildDurationMonitor(monitorProperties));
	}

	private MonitorResult _execute(
		QueuedBuildDurationMonitor queuedBuildDurationMonitor) {

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				_currentTimeMillis
			);

			return queuedBuildDurationMonitor.execute();
		}
	}

	private String _getOKMessage() {
		return JenkinsResultsParserUtil.combine(
			"No master in cohort ", _COHORT_NAME,
			" has queued builds stuck longer than 3 hours");
	}

	private String _getRemoteURL(String masterName) {
		return JenkinsResultsParserUtil.combine(
			"http://", masterName, ".liferay.com");
	}

	private String _getStuckMessage(int count, String stuckQueueItemsMessage) {
		String noun = "builds";
		String verb = "were";

		if (count == 1) {
			noun = "build";
			verb = "was";
		}

		return JenkinsResultsParserUtil.combine(
			String.valueOf(count), " ", noun, " in cohort ", _COHORT_NAME, " ",
			verb, " queued over 3 hours: ", stuckQueueItemsMessage);
	}

	private String _getStuckQueueItemMessage(
		String masterName, String taskName) {

		return JenkinsResultsParserUtil.combine(
			taskName, " on ", _getRemoteURL(masterName),
			" (3 hours 12 minutes)");
	}

	private JSONObject _newExecutorJSONObject(long seconds) {
		return JenkinsMasterTestUtil.getExecutorJSONObject(
			JenkinsResultsParserUtil.combine(
				"http://", _MASTER_NAME_1, "/job/",
				RandomTestUtil.randomString(), "/",
				String.valueOf(RandomTestUtil.randomInt()), "/"),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(), false,
			_currentTimeMillis - (seconds * 1000));
	}

	private Properties _newMonitorProperties() {
		Properties monitorProperties = new Properties();

		monitorProperties.setProperty(
			"monitor[a].parameter[cohort.name]", _COHORT_NAME);
		monitorProperties.setProperty(
			"monitor[a].type", "queued-build-duration");

		return monitorProperties;
	}

	private JSONObject _newQueueItemJSONObject(
		String className, long seconds, String taskName) {

		return new JSONObject(
		).put(
			"_class", className
		).put(
			"inQueueSince", _currentTimeMillis - (seconds * 1000)
		).put(
			"task",
			new JSONObject(
			).put(
				"name", taskName
			)
		);
	}

	private QueuedBuildDurationMonitor _newQueuedBuildDurationMonitor(
		Properties monitorProperties) {

		List<MonitorConfig> monitorConfigs =
			MonitorConfigLoader.getMonitorConfigs(monitorProperties);

		return new QueuedBuildDurationMonitor(monitorConfigs.get(0));
	}

	private void _setComputerAPIJSONObject(
			String masterName, JSONObject... computerJSONObjects)
		throws Exception {

		setURLReaderOutput(
			String.valueOf(
				JenkinsMasterTestUtil.getComputerAPIJSONObject(
					RandomTestUtil.randomInt(), computerJSONObjects)),
			JenkinsResultsParserUtil.combine(
				"http://", masterName, "/computer/api/json"));
	}

	private void _setQueueAPIJSONObject(
			String masterName, JSONObject... queueItemJSONObjects)
		throws Exception {

		setURLReaderOutput(
			new JSONObject(
			).put(
				"items", new JSONArray(queueItemJSONObjects)
			).toString(),
			JenkinsResultsParserUtil.combine(
				"http://", masterName, "/queue/api/json"));
	}

	private void _setUpMaster(String masterName) throws Exception {
		_setComputerAPIJSONObject(masterName);
		_setQueueAPIJSONObject(masterName);
	}

	private void _testExecute(
		String expectedMessage, int expectedQueuedBuildsCount,
		MonitorResult.Status expectedStatus,
		int expectedStuckQueuedBuildsCount) {

		MonitorResult monitorResult = _execute(_newMonitorProperties());

		Assert.assertNull(monitorResult.getLinkURLs());

		testEquals(expectedMessage, monitorResult.getMessage());

		Map<String, String> metrics = monitorResult.getMetrics();

		testEquals(
			String.valueOf(expectedQueuedBuildsCount),
			metrics.get("queued.builds.count"));
		testEquals(
			String.valueOf(expectedStuckQueuedBuildsCount),
			metrics.get("stuck.queued.builds.count"));

		testEquals(expectedStatus, monitorResult.getStatus());
	}

	private void _testExecuteThreshold(
		MonitorResult.Status expectedStatus, long threshold) {

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(
			"monitor[a].threshold[queue.duration.maximum]",
			String.valueOf(threshold));

		MonitorResult monitorResult = _execute(monitorProperties);

		testEquals(expectedStatus, monitorResult.getStatus());
	}

	private void _testQueuedBuildDurationMonitorInvalidProperty(
		String name, String value) {

		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.setProperty(name, value);

		try {
			_newQueuedBuildDurationMonitor(monitorProperties);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private void _testQueuedBuildDurationMonitorMissingProperty(String name) {
		Properties monitorProperties = _newMonitorProperties();

		monitorProperties.remove(name);

		try {
			_newQueuedBuildDurationMonitor(monitorProperties);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private static final String _COHORT_NAME = "test-9";

	private static final String _MASTER_NAME_1 = _COHORT_NAME + "-1";

	private static final String _MASTER_NAME_2 = _COHORT_NAME + "-2";

	private static final long _SECONDS_HOUR = 60 * 60;

	private static final long _SECONDS_STUCK = (3 * _SECONDS_HOUR) + (12 * 60);

	private long _currentTimeMillis;

}