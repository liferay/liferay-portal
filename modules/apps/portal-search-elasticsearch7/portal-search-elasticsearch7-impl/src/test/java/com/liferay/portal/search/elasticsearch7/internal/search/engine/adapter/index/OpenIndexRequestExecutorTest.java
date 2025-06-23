/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.engine.adapter.index.OpenIndexRequest;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.core.TimeValue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public class OpenIndexRequestExecutorTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			CreateIndexRequestExecutorTest.class.getSimpleName());

		_elasticsearchFixture.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Test
	public void testIndexRequestTranslation() {
		OpenIndexRequest openIndexRequest = new OpenIndexRequest(_INDEX_NAME);

		IndicesOptions indicesOptions = new IndicesOptions();

		indicesOptions.setIgnoreUnavailable(true);

		openIndexRequest.setIndicesOptions(indicesOptions);

		openIndexRequest.setTimeout(100);
		openIndexRequest.setWaitForActiveShards(200);

		OpenIndexRequestExecutor openIndexRequestExecutor =
			new OpenIndexRequestExecutor(_elasticsearchFixture);

		org.elasticsearch.action.admin.indices.open.OpenIndexRequest
			elastichsearchOpenIndexRequest =
				openIndexRequestExecutor.createOpenIndexRequest(
					openIndexRequest);

		Assert.assertArrayEquals(
			openIndexRequest.getIndexNames(),
			elastichsearchOpenIndexRequest.indices());

		Assert.assertEquals(
			IndicesOptionsTranslatorUtil.translate(
				openIndexRequest.getIndicesOptions()),
			elastichsearchOpenIndexRequest.indicesOptions());

		Assert.assertEquals(
			TimeValue.timeValueMillis(openIndexRequest.getTimeout()),
			elastichsearchOpenIndexRequest.masterNodeTimeout());

		Assert.assertEquals(
			TimeValue.timeValueMillis(openIndexRequest.getTimeout()),
			elastichsearchOpenIndexRequest.timeout());

		Assert.assertEquals(
			ActiveShardCount.from(openIndexRequest.getWaitForActiveShards()),
			elastichsearchOpenIndexRequest.waitForActiveShards());
	}

	private static final String _INDEX_NAME = "test_request_index";

	private ElasticsearchFixture _elasticsearchFixture;

}