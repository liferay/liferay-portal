/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

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

		setUrlReaderOutput(
			new JSONObject(
			).put(
				"items", new JSONArray()
			).toString(),
			"http://test-9-1/queue/api/json", urlReader);
		setUrlReaderOutput(
			new JSONObject(
			).put(
				"mode", "NORMAL"
			).toString(),
			"http://test-9-1/api/json?tree=mode", urlReader);
		setUrlReaderOutput(
			read(new File(dependenciesDirs.get(0), "computer-api.json")),
			"http://test-9-1/computer/api/json", urlReader);

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

	private JenkinsMaster _jenkinsMaster;

}