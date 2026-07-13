/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.information;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.elasticsearch8.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.internal.engine.ConnectionInformationBuilderFactoryImpl;
import com.liferay.portal.search.internal.engine.NodeInformationBuilderFactoryImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Selena Aungst
 */
public class ElasticsearchSearchEngineInformationTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			ElasticsearchConnectionFixture.builder(
			).clusterName(
				_CLUSTER_NAME
			).build();

		elasticsearchConnectionFixture.createNode();

		_elasticsearchConnectionFixture = elasticsearchConnectionFixture;
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_elasticsearchConnectionFixture.destroyNode();
	}

	@Before
	public void setUp() {
		ElasticsearchConnectionManager elasticsearchConnectionManager =
			Mockito.mock(ElasticsearchConnectionManager.class);

		Mockito.when(
			elasticsearchConnectionManager.getElasticsearchClient()
		).thenReturn(
			_elasticsearchConnectionFixture.getElasticsearchClient()
		);

		ReflectionTestUtil.setFieldValue(
			_elasticsearchSearchEngineInformation,
			"connectionInformationBuilderFactory",
			new ConnectionInformationBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_elasticsearchSearchEngineInformation,
			"elasticsearchConfigurationWrapper",
			Mockito.mock(ElasticsearchConfigurationWrapper.class));
		ReflectionTestUtil.setFieldValue(
			_elasticsearchSearchEngineInformation,
			"elasticsearchConnectionManager", elasticsearchConnectionManager);
		ReflectionTestUtil.setFieldValue(
			_elasticsearchSearchEngineInformation,
			"nodeInformationBuilderFactory",
			new NodeInformationBuilderFactoryImpl());
	}

	@Test
	public void testGetNodesString() {
		String nodesString =
			_elasticsearchSearchEngineInformation.getNodesString();

		Assert.assertTrue(
			nodesString, nodesString.startsWith(_CLUSTER_NAME + ": ["));
		Assert.assertTrue(nodesString, nodesString.endsWith(")]"));
	}

	private static final String _CLUSTER_NAME =
		"ElasticsearchSearchEngineInformationTest";

	private static ElasticsearchConnectionFixture
		_elasticsearchConnectionFixture;

	private final ElasticsearchSearchEngineInformation
		_elasticsearchSearchEngineInformation =
			new ElasticsearchSearchEngineInformation();

}