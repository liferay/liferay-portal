/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
 * @author Calum Ragan
 */
public class StaleBuildReaperTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Environment.setInstance(Mockito.mock(Environment.class));

		JenkinsMasterTestUtil.getJenkinsCohortProperties("test-9", 2);

		_urlReader = mockUrlReader();

		_setUpJenkinsMasterUrlReaderOutputs(
			_getStaleBuildsComputerAPIJSONObject(), "test-9-1");
		_setUpJenkinsMasterUrlReaderOutputs(
			_getJenkinsSlaveOfflineComputerAPIJSONObject(), "test-9-2");

		_jenkinsCohort = JenkinsCohort.getInstance("test-9");
	}

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		JenkinsMasterTestUtil.resetCaches();

		JenkinsResultsParserUtil.setBuildProperties(new Properties());
	}

	@Test
	public void testGetSummary() {
		StaleBuildReaper staleBuildReaper = _reap(
			true, null, new ArrayList<String>());

		String summary = staleBuildReaper.getSummary();

		Assert.assertEquals(3, staleBuildReaper.getStaleBuildsCount());

		Assert.assertTrue(summary, summary.contains(_BUILD_URL_LIKELY_STUCK));
		Assert.assertTrue(summary, summary.contains(_BUILD_URL_NODE_REMOVED));
		Assert.assertTrue(summary, summary.contains(_BUILD_URL_OFFLINE));

		Assert.assertFalse(summary, summary.contains(_BUILD_URL_HEALTHY));
		Assert.assertFalse(summary, summary.contains(_BUILD_URL_RECONNECTING));
		Assert.assertFalse(
			summary, summary.contains(_BUILD_URL_TEMPORARILY_OFFLINE));

		Assert.assertFalse(
			summary, summary.contains(_BUILD_URL_FLYWEIGHT_STUCK));

		Assert.assertTrue(
			summary, summary.contains("its executor reports likelyStuck"));
		Assert.assertTrue(
			summary, summary.contains("its node is being removed"));
		Assert.assertTrue(
			summary, summary.contains("its node has been offline"));
	}

	@Test
	public void testReap() {
		List<String> stoppedBuildURLs = new ArrayList<>();

		StaleBuildReaper staleBuildReaper = _reap(
			false, null, stoppedBuildURLs);

		Assert.assertEquals(3, staleBuildReaper.getReapedBuildsCount());

		Assert.assertEquals(3, staleBuildReaper.getStaleBuildsCount());

		List<String> expectedBuildURLs = new ArrayList<>();

		expectedBuildURLs.add(_BUILD_URL_LIKELY_STUCK);
		expectedBuildURLs.add(_BUILD_URL_NODE_REMOVED);
		expectedBuildURLs.add(_BUILD_URL_OFFLINE);

		Collections.sort(expectedBuildURLs);

		Collections.sort(stoppedBuildURLs);

		Assert.assertEquals(expectedBuildURLs, stoppedBuildURLs);
	}

	@Test
	public void testReapAbortFailed() {
		List<String> stoppedBuildURLs = new ArrayList<>();

		StaleBuildReaper staleBuildReaper = _reap(
			false, _BUILD_URL_LIKELY_STUCK, stoppedBuildURLs);

		Assert.assertEquals(2, staleBuildReaper.getReapedBuildsCount());

		Assert.assertEquals(3, staleBuildReaper.getStaleBuildsCount());

		Assert.assertFalse(
			stoppedBuildURLs.toString(),
			stoppedBuildURLs.contains(_BUILD_URL_LIKELY_STUCK));

		String summary = staleBuildReaper.getSummary();

		Assert.assertTrue(summary, summary.contains("Reaped 2 of 3"));

		Assert.assertTrue(summary, summary.contains("Abort failed."));
	}

	@Test
	public void testReapBlacklistedJenkinsMaster() {
		ReflectionTestUtil.setFieldValue(
			JenkinsMaster.getInstance("test-9-2"), "_blacklisted", true);

		List<JenkinsMaster> availableJenkinsMasters =
			_jenkinsCohort.getAvailableJenkinsMasters();

		Assert.assertEquals(
			availableJenkinsMasters.toString(), 1,
			availableJenkinsMasters.size());

		List<JenkinsMaster> blacklistedJenkinsMasters =
			_jenkinsCohort.getBlacklistedJenkinsMasters();

		Assert.assertEquals(
			blacklistedJenkinsMasters.toString(), 1,
			blacklistedJenkinsMasters.size());

		List<String> stoppedBuildURLs = new ArrayList<>();

		StaleBuildReaper staleBuildReaper = _reap(
			false, null, stoppedBuildURLs);

		Assert.assertEquals(3, staleBuildReaper.getReapedBuildsCount());

		Assert.assertTrue(
			stoppedBuildURLs.toString(),
			stoppedBuildURLs.contains(_BUILD_URL_OFFLINE));
	}

	@Test
	public void testReapDryRun() {
		List<String> stoppedBuildURLs = new ArrayList<>();

		StaleBuildReaper staleBuildReaper = _reap(true, null, stoppedBuildURLs);

		Assert.assertEquals(0, staleBuildReaper.getReapedBuildsCount());

		Assert.assertTrue(
			stoppedBuildURLs.toString(), stoppedBuildURLs.isEmpty());
	}

	private JSONObject _getJenkinsSlaveOfflineComputerAPIJSONObject() {
		return JenkinsMasterTestUtil.getComputerAPIJSONObject(
			2,
			JenkinsMasterTestUtil.getOfflineComputerJSONObject(
				"test-9-2-1", "Connection was broken",
				_getStartTime(30 * _MINUTE), false,
				JenkinsMasterTestUtil.getExecutorJSONObject(
					_BUILD_URL_OFFLINE, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), false,
					_getStartTime(46 * _HOUR))),
			JenkinsMasterTestUtil.getOfflineComputerJSONObject(
				"test-9-2-2", "Connection was broken",
				_getStartTime(2 * _MINUTE), false,
				JenkinsMasterTestUtil.getExecutorJSONObject(
					_BUILD_URL_RECONNECTING, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), false,
					_getStartTime(46 * _HOUR))),
			JenkinsMasterTestUtil.getOfflineComputerJSONObject(
				"test-9-2-3", "Disk is full", _getStartTime(30 * _HOUR), true,
				JenkinsMasterTestUtil.getExecutorJSONObject(
					_BUILD_URL_TEMPORARILY_OFFLINE, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), false,
					_getStartTime(46 * _HOUR))));
	}

	private JSONObject _getStaleBuildsComputerAPIJSONObject() {
		return JenkinsMasterTestUtil.getComputerAPIJSONObject(
			7,
			JenkinsMasterTestUtil.getBuiltInComputerJSONObject(
				JenkinsMasterTestUtil.getExecutorJSONObject(
					_BUILD_URL_HEALTHY, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), false,
					_getStartTime(20 * _HOUR)),
				JenkinsMasterTestUtil.getExecutorJSONObject(
					_BUILD_URL_FLYWEIGHT_STUCK, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), true,
					_getStartTime(10 * _MINUTE))),
			JenkinsMasterTestUtil.getComputerJSONObject(
				"test-9-1-1",
				JenkinsMasterTestUtil.getExecutorJSONObject(
					_BUILD_URL_LIKELY_STUCK, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), true,
					_getStartTime(20 * _HOUR))),
			JenkinsMasterTestUtil.getOfflineComputerJSONObject(
				"test-9-1-2", "Node is being removed",
				_getStartTime(12 * 24 * _HOUR), false,
				JenkinsMasterTestUtil.getExecutorJSONObject(
					_BUILD_URL_NODE_REMOVED, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), false,
					_getStartTime(12 * 24 * _HOUR))));
	}

	private long _getStartTime(long duration) {
		return JenkinsResultsParserUtil.getCurrentTimeMillis() - duration;
	}

	private StaleBuildReaper _reap(
		boolean dryRun, String failingBuildURL, List<String> stoppedBuildURLs) {

		try (MockedStatic<JenkinsStopBuildUtil> stopBuildMockedStatic =
				Mockito.mockStatic(JenkinsStopBuildUtil.class);
			MockedStatic<NotificationUtil> notificationMockedStatic =
				Mockito.mockStatic(NotificationUtil.class)) {

			stopBuildMockedStatic.when(
				() -> JenkinsStopBuildUtil.abortBuild(Mockito.anyString())
			).thenAnswer(
				invocation -> {
					String buildURL = invocation.getArgument(0);

					if (buildURL.equals(failingBuildURL)) {
						throw new RuntimeException(
							"Unable to stop " + buildURL);
					}

					stoppedBuildURLs.add(buildURL);

					return JenkinsStopBuildUtil.AbortResult.STOPPED;
				}
			);

			StaleBuildReaper staleBuildReaper = new StaleBuildReaper(
				dryRun, _jenkinsCohort);

			staleBuildReaper.reap();

			return staleBuildReaper;
		}
	}

	private void _setUpJenkinsMasterUrlReaderOutputs(
			JSONObject computerAPIJSONObject, String masterName)
		throws Exception {

		String masterURL = "http://" + masterName;

		JSONObject queueJSONObject = new JSONObject();

		queueJSONObject.put("items", new JSONArray());

		setUrlReaderOutput(
			queueJSONObject.toString(), masterURL + "/queue/api/json",
			_urlReader);

		JSONObject modeJSONObject = new JSONObject();

		modeJSONObject.put("mode", "NORMAL");

		setUrlReaderOutput(
			modeJSONObject.toString(), masterURL + "/api/json?tree=mode",
			_urlReader);

		setUrlReaderOutput(
			computerAPIJSONObject.toString(), masterURL + "/computer/api/json",
			_urlReader);
	}

	private static final String _BUILD_URL_FLYWEIGHT_STUCK =
		"http://test-9-1/job/publish-testray-report/7/";

	private static final String _BUILD_URL_HEALTHY =
		"http://test-9-1/job/test-portal-release-downstream/1/";

	private static final String _BUILD_URL_LIKELY_STUCK =
		"http://test-9-1/job/test-portal-acceptance-pullrequest(master)/1580/";

	private static final String _BUILD_URL_NODE_REMOVED =
		"http://test-9-1/job/test-portal-release-downstream/22649/";

	private static final String _BUILD_URL_OFFLINE =
		"http://test-9-2/job/test-portal-testsuite-downstream/166636/";

	private static final String _BUILD_URL_RECONNECTING =
		"http://test-9-2/job/test-portal-source-format(master)/99/";

	private static final String _BUILD_URL_TEMPORARILY_OFFLINE =
		"http://test-9-2/job/test-portal-acceptance-pullrequest(master)/42/";

	private static final long _HOUR = 60 * 60 * 1000L;

	private static final long _MINUTE = 60 * 1000L;

	private JenkinsCohort _jenkinsCohort;
	private UrlReader _urlReader;

}