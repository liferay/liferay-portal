/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Hashtable;
import java.util.Map;

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

		setUrlReaderOutput(
			"{\"mode\": \"NORMAL\"}", "http://test-9-1/api/json?tree=mode",
			urlReader);
		setUrlReaderOutput(
			read(new File(dependenciesDirs.get(0), "computer-api.json")),
			"http://test-9-1/computer/api/json", urlReader);
		setUrlReaderOutput(
			"{\"items\": []}", "http://test-9-1/queue/api/json", urlReader);

		_jenkinsMaster = JenkinsMasterTestUtil.stageMaster(
			"test-9-1", "http://test-9-1");
	}

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		JenkinsMaster.maxRecentBatchAge = 120 * 1000;

		JenkinsMasterTestUtil.resetCaches();

		JenkinsResultsParserUtil.setBuildProperties(
			(Hashtable<Object, Object>)null);
	}

	@Test
	public void testGetAvailableSlavesCount() {
		int availableSlavesCount = _jenkinsMaster.getAvailableSlavesCount(null);

		JenkinsMaster.maxRecentBatchAge = -1;

		_jenkinsMaster.addRecentBatch(7, "label-a");

		Map<String, Map<Long, Integer>> labelBatchSizes =
			JenkinsMasterTestUtil.getLabelBatchSizes(_jenkinsMaster);

		Map<Long, Integer> batchSizes = labelBatchSizes.get("label-a");

		Assert.assertEquals(batchSizes.toString(), 1, batchSizes.size());

		_jenkinsMaster.getAvailableSlavesCount("label-b");

		Assert.assertTrue(batchSizes.isEmpty());

		Assert.assertEquals(
			availableSlavesCount, _jenkinsMaster.getAvailableSlavesCount(null));
	}

	@Test
	public void testUpdate() {
		_jenkinsMaster.update();

		int availableSlavesCount = _jenkinsMaster.getAvailableSlavesCount(null);

		_jenkinsMaster.addRecentBatch(5, null);

		Assert.assertEquals(
			availableSlavesCount - 5,
			_jenkinsMaster.getAvailableSlavesCount(null));

		setDeclaredFieldValue(
			JenkinsMaster.class, _jenkinsMaster, "_updateTimestamp", -1L);

		_jenkinsMaster.update();

		Assert.assertEquals(
			availableSlavesCount - 5,
			_jenkinsMaster.getAvailableSlavesCount(null));

		Map<String, Map<Long, Integer>> labelBatchSizes =
			JenkinsMasterTestUtil.getLabelBatchSizes(_jenkinsMaster);

		Assert.assertFalse(labelBatchSizes.isEmpty());
	}

	private JenkinsMaster _jenkinsMaster;

}