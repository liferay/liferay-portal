/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.index.StatsIndexRequest;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.elasticsearch.client.Request;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Felipe Lorenz
 */
public class StatsIndexRequestExecutorTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			StatsIndexRequestExecutorTest.class.getSimpleName());

		_elasticsearchFixture.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Test
	public void testStatsIndexRequestTranslationWithMoreThanOneIndex() {
		StatsIndexRequest statsIndexRequest = new StatsIndexRequest(
			"liferay-1", "liferay-2", "liferay-3");

		StatsIndexRequestExecutor statsIndexRequestExecutor =
			new StatsIndexRequestExecutor(null, null);

		Request request =
			statsIndexRequestExecutor.getElasticsearchIndexRequest(
				statsIndexRequest);

		Assert.assertEquals(
			"/liferay-1,liferay-2,liferay-3/_stats", request.getEndpoint());
	}

	@Test
	public void testStatsIndexRequestTranslationWithOneIndex() {
		StatsIndexRequest statsIndexRequest = new StatsIndexRequest(
			"liferay-1");

		StatsIndexRequestExecutor statsIndexRequestExecutor =
			new StatsIndexRequestExecutor(null, null);

		Request request =
			statsIndexRequestExecutor.getElasticsearchIndexRequest(
				statsIndexRequest);

		Assert.assertEquals("/liferay-1/_stats", request.getEndpoint());
	}

	@Test
	public void testStatsIndexRequestTranslationWithoutIndex() {
		StatsIndexRequest statsIndexRequest = new StatsIndexRequest();

		StatsIndexRequestExecutor statsIndexRequestExecutor =
			new StatsIndexRequestExecutor(null, null);

		Request request =
			statsIndexRequestExecutor.getElasticsearchIndexRequest(
				statsIndexRequest);

		Assert.assertEquals("/_all/_stats", request.getEndpoint());
	}

	private ElasticsearchFixture _elasticsearchFixture;

}