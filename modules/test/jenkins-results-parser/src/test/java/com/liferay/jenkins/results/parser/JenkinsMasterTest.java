/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class JenkinsMasterTest extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Environment.setInstance(Mockito.mock(Environment.class));

		UrlReader urlReader = mockUrlReader();

		_setUpMaster(
			"test-9-1",
			read(new File(dependenciesDirs.get(0), "computer-api.json")),
			urlReader);
		_setUpMaster(
			"test-9-2", _getRunningBuildsComputerAPIJSONObject().toString(),
			urlReader);

		_jenkinsMaster = JenkinsMasterTestUtil.getJenkinsMaster(
			"test-9-1", "http://test-9-1");
	}

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		JenkinsMaster.maxRecentBatchAge = 120 * 1000;
	}

	@Test
	public void testGetAvailableSlavesCount() {
		int availableSlavesCount = _jenkinsMaster.getAvailableSlavesCount(null);

		JenkinsMaster.maxRecentBatchAge = -1;

		String label = RandomTestUtil.randomString();

		_jenkinsMaster.addRecentBatch(7, label);

		Map<String, Map<Long, Integer>> labelBatchSizes =
			JenkinsMasterTestUtil.getLabelBatchSizes(_jenkinsMaster);

		Map<Long, Integer> batchSizes = labelBatchSizes.get(label);

		Assert.assertEquals(batchSizes.toString(), 1, batchSizes.size());

		String otherLabel = RandomTestUtil.randomString();

		_jenkinsMaster.getAvailableSlavesCount(otherLabel);

		Assert.assertTrue(batchSizes.isEmpty());

		Assert.assertEquals(
			availableSlavesCount, _jenkinsMaster.getAvailableSlavesCount(null));
	}

	@Test
	public void testGetQueueItem() throws Exception {
		UrlReader urlReader = mockUrlReader();

		setUrlReaderOutput(
			new JSONObject(
			).put(
				"id", 7800
			).toString(),
			"http://test-9-1/queue/item/7800/api/json", urlReader);

		JenkinsMaster.QueueItem queueItem = _jenkinsMaster.getQueueItem(7800);

		Assert.assertEquals(7800, queueItem.getId());
	}

	@Test
	public void testGetQueueItemNotFound() throws Exception {
		UrlReader urlReader = mockUrlReader();

		String queueItemAPIURL = "http://test-9-1/queue/item/7800/api/json";

		setUrlReaderException(
			new FileNotFoundException(queueItemAPIURL), queueItemAPIURL,
			urlReader);

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();
		PrintStream printStream = System.out;

		System.setOut(new PrintStream(byteArrayOutputStream, true));

		try {
			Assert.assertNull(_jenkinsMaster.getQueueItem(7800));
		}
		finally {
			System.setOut(printStream);
		}

		Assert.assertEquals("", byteArrayOutputStream.toString());
	}

	@Test
	public void testGetRunningBuilds() {
		JenkinsMaster jenkinsMaster = JenkinsMasterTestUtil.getJenkinsMaster(
			"test-9-2", "http://test-9-2");

		jenkinsMaster.update(false);

		List<JenkinsMaster.RunningBuild> runningBuilds =
			jenkinsMaster.getRunningBuilds();

		Assert.assertEquals(runningBuilds.toString(), 3, runningBuilds.size());

		Assert.assertEquals(7, jenkinsMaster.getMaxRunningBuildsCount());

		List<String> buildURLs = jenkinsMaster.getBuildURLs();

		Assert.assertEquals(buildURLs.toString(), 2, buildURLs.size());

		Assert.assertFalse(
			buildURLs.toString(), buildURLs.contains(_BUILD_URL_FLYWEIGHT));

		JenkinsMaster.RunningBuild flyweightRunningBuild = _getRunningBuild(
			_BUILD_URL_FLYWEIGHT, runningBuilds);

		Assert.assertEquals(
			"Built-In Node", flyweightRunningBuild.getJenkinsSlaveName());
		Assert.assertFalse(flyweightRunningBuild.isLikelyStuck());
		Assert.assertFalse(
			flyweightRunningBuild.isJenkinsSlaveOfflineUnexpectedly());

		JenkinsMaster.RunningBuild likelyStuckRunningBuild = _getRunningBuild(
			_BUILD_URL_LIKELY_STUCK, runningBuilds);

		Assert.assertTrue(likelyStuckRunningBuild.isLikelyStuck());
		Assert.assertFalse(
			likelyStuckRunningBuild.isJenkinsSlaveOfflineUnexpectedly());
		Assert.assertEquals(
			_LIKELY_STUCK_ESTIMATED_DURATION,
			likelyStuckRunningBuild.getEstimatedDuration());

		JenkinsMaster.RunningBuild offlineRunningBuild = _getRunningBuild(
			_BUILD_URL_OFFLINE_NODE, runningBuilds);

		Assert.assertTrue(
			offlineRunningBuild.isJenkinsSlaveOfflineUnexpectedly());
		Assert.assertTrue(offlineRunningBuild.isJenkinsSlaveBeingRemoved());
	}

	@Test
	public void testUpdate() {
		_jenkinsMaster.update();

		int availableSlavesCount = _jenkinsMaster.getAvailableSlavesCount(null);

		_jenkinsMaster.addRecentBatch(5, null);

		Assert.assertEquals(
			availableSlavesCount - 5,
			_jenkinsMaster.getAvailableSlavesCount(null));

		ReflectionTestUtil.setFieldValue(
			_jenkinsMaster, "_updateTimestamp", -1L);

		_jenkinsMaster.update();

		Assert.assertEquals(
			availableSlavesCount - 5,
			_jenkinsMaster.getAvailableSlavesCount(null));

		Map<String, Map<Long, Integer>> labelBatchSizes =
			JenkinsMasterTestUtil.getLabelBatchSizes(_jenkinsMaster);

		Assert.assertFalse(labelBatchSizes.isEmpty());
	}

	@Test
	public void testUpdateFullAfterMinimal() {
		JenkinsMaster jenkinsMaster = JenkinsMasterTestUtil.getJenkinsMaster(
			"test-9-2", "http://test-9-2");

		jenkinsMaster.update(true);

		List<JenkinsMaster.RunningBuild> runningBuilds =
			jenkinsMaster.getRunningBuilds();

		Assert.assertTrue(runningBuilds.toString(), runningBuilds.isEmpty());

		jenkinsMaster.update(false);

		runningBuilds = jenkinsMaster.getRunningBuilds();

		Assert.assertEquals(runningBuilds.toString(), 3, runningBuilds.size());

		Assert.assertEquals(7, jenkinsMaster.getMaxRunningBuildsCount());

		jenkinsMaster.update(true);

		runningBuilds = jenkinsMaster.getRunningBuilds();

		Assert.assertEquals(runningBuilds.toString(), 3, runningBuilds.size());
	}

	private JenkinsMaster.RunningBuild _getRunningBuild(
		String buildURL, List<JenkinsMaster.RunningBuild> runningBuilds) {

		for (JenkinsMaster.RunningBuild runningBuild : runningBuilds) {
			String runningBuildURL = runningBuild.getURL();

			if (runningBuildURL.equals(buildURL)) {
				return runningBuild;
			}
		}

		throw new AssertionError(
			JenkinsResultsParserUtil.combine(
				"Unable to find ", buildURL, " in ",
				String.valueOf(runningBuilds)));
	}

	private JSONObject _getRunningBuildsComputerAPIJSONObject() {
		return JenkinsMasterTestUtil.getComputerAPIJSONObject(
			7,
			JenkinsMasterTestUtil.getBuiltInComputerJSONObject(
				JenkinsMasterTestUtil.getExecutorJSONObject(
					_BUILD_URL_FLYWEIGHT, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), false,
					RandomTestUtil.randomLong())),
			JenkinsMasterTestUtil.getComputerJSONObject(
				"test-9-2-1",
				JenkinsMasterTestUtil.getExecutorJSONObject(
					_BUILD_URL_LIKELY_STUCK, _LIKELY_STUCK_ESTIMATED_DURATION,
					RandomTestUtil.randomString(), true,
					RandomTestUtil.randomLong())),
			JenkinsMasterTestUtil.getOfflineComputerJSONObject(
				"test-9-2-2", "Node is being removed",
				RandomTestUtil.randomLong(), false,
				JenkinsMasterTestUtil.getExecutorJSONObject(
					_BUILD_URL_OFFLINE_NODE, RandomTestUtil.randomLong(),
					RandomTestUtil.randomString(), false,
					RandomTestUtil.randomLong())));
	}

	private void _setUpMaster(
			String masterName, String computerAPIJSON, UrlReader urlReader)
		throws Exception {

		String masterURL = "http://" + masterName;

		setUrlReaderOutput(
			new JSONObject(
			).put(
				"items", new JSONArray()
			).toString(),
			masterURL + "/queue/api/json", urlReader);
		setUrlReaderOutput(
			new JSONObject(
			).put(
				"mode", "NORMAL"
			).toString(),
			masterURL + "/api/json?tree=mode", urlReader);
		setUrlReaderOutput(
			computerAPIJSON, masterURL + "/computer/api/json", urlReader);
	}

	private static final String _BUILD_URL_FLYWEIGHT =
		"http://test-9-2/job/publish-testray-report/7/";

	private static final String _BUILD_URL_LIKELY_STUCK =
		"http://test-9-2/job/test-portal-acceptance-pullrequest(master)/1580/";

	private static final String _BUILD_URL_OFFLINE_NODE =
		"http://test-9-2/job/test-portal-release-downstream/22649/";

	private static final long _LIKELY_STUCK_ESTIMATED_DURATION =
		RandomTestUtil.randomLong();

	private JenkinsMaster _jenkinsMaster;

}