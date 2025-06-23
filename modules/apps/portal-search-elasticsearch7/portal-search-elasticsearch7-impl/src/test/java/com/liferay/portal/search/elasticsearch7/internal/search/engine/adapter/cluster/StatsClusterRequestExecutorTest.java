/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.cluster;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.engine.adapter.cluster.StatsClusterRequest;
import com.liferay.portal.search.engine.adapter.cluster.StatsClusterResponse;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Dylan Rebelak
 */
public class StatsClusterRequestExecutorTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			ElasticsearchConnectionFixture.builder(
			).clusterName(
				StatsClusterRequestExecutorTest.class.getSimpleName()
			).build();

		elasticsearchConnectionFixture.createNode();

		_elasticsearchConnectionFixture = elasticsearchConnectionFixture;
	}

	@After
	public void tearDown() throws Exception {
		_elasticsearchConnectionFixture.destroyNode();
	}

	@Test
	public void testClusterRequestExecution() {
		StatsClusterRequest statsClusterRequest = new StatsClusterRequest(
			new String[] {_NODE_ID});

		StatsClusterRequestExecutor statsClusterRequestExecutor =
			new StatsClusterRequestExecutor(
				_elasticsearchConnectionFixture, new JSONFactoryImpl());

		StatsClusterResponse statsClusterResponse =
			statsClusterRequestExecutor.execute(statsClusterRequest);

		Assert.assertNotNull(statsClusterResponse);
		Assert.assertNotEquals(
			0, statsClusterResponse.getAvailableSpaceInBytes());
		Assert.assertNotNull(statsClusterResponse.getClusterHealthStatus());
		Assert.assertNotEquals(0, statsClusterResponse.getUsedSpaceInBytes());
	}

	private static final String _NODE_ID = "liferay_sidecar";

	private ElasticsearchConnectionFixture _elasticsearchConnectionFixture;

}