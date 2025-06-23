/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.cluster;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.cluster.ClusterHealthStatus;
import com.liferay.portal.search.engine.adapter.cluster.HealthClusterRequest;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.core.TimeValue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Dylan Rebelak
 */
public class HealthClusterRequestExecutorTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			HealthClusterRequestExecutorTest.class.getSimpleName());

		_elasticsearchFixture.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Test
	public void testClusterRequestTranslation() {
		HealthClusterRequest healthClusterRequest = new HealthClusterRequest(
			_INDEX_NAME);

		healthClusterRequest.setTimeout(1000);
		healthClusterRequest.setWaitForClusterHealthStatus(
			ClusterHealthStatus.GREEN);

		HealthClusterRequestExecutor healthClusterRequestExecutor =
			new HealthClusterRequestExecutor(_elasticsearchFixture);

		ClusterHealthRequest clusterHealthRequest =
			healthClusterRequestExecutor.createClusterHealthRequest(
				healthClusterRequest);

		String[] indices = clusterHealthRequest.indices();

		Assert.assertArrayEquals(new String[] {_INDEX_NAME}, indices);

		Assert.assertEquals(
			healthClusterRequest.getWaitForClusterHealthStatus(),
			ClusterHealthStatusTranslatorUtil.translate(
				clusterHealthRequest.waitForStatus()));

		Assert.assertEquals(
			TimeValue.timeValueMillis(1000), clusterHealthRequest.timeout());

		Assert.assertEquals(
			TimeValue.timeValueMillis(1000),
			clusterHealthRequest.masterNodeTimeout());
	}

	private static final String _INDEX_NAME = "test_request_index";

	private ElasticsearchFixture _elasticsearchFixture;

}