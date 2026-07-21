/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.engine;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch8.internal.ElasticsearchSearchEngine;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnection;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnectionFixture;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch8.internal.index.util.IndexFactoryCompanyIdRegistryUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author André de Oliveira
 */
public class ElasticsearchSearchEngineReconnectTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		ElasticsearchConnectionFixture elasticsearchConnectionFixture =
			ElasticsearchConnectionFixture.builder(
			).clusterName(
				ElasticsearchSearchEngineReconnectTest.class.getSimpleName()
			).build();

		ElasticsearchSearchEngineFixture elasticsearchSearchEngineFixture =
			new ElasticsearchSearchEngineFixture(
				elasticsearchConnectionFixture);

		elasticsearchSearchEngineFixture.setUp();

		_elasticsearchSearchEngineFixture = elasticsearchSearchEngineFixture;
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_elasticsearchSearchEngineFixture.tearDown();

		for (long companyId :
				IndexFactoryCompanyIdRegistryUtil.getCompanyIds()) {

			IndexFactoryCompanyIdRegistryUtil.unregisterCompanyId(companyId);
		}
	}

	@Test
	public void testInitializeAfterReconnect() {
		ElasticsearchSearchEngine elasticsearchSearchEngine =
			_elasticsearchSearchEngineFixture.getElasticsearchSearchEngine();

		long companyId = RandomTestUtil.randomLong();

		elasticsearchSearchEngine.initialize(companyId);

		_reconnect(
			_elasticsearchSearchEngineFixture.
				getElasticsearchConnectionManager());

		elasticsearchSearchEngine.initialize(companyId);
	}

	private void _reconnect(
		ElasticsearchConnectionManager elasticsearchConnectionManager) {

		ElasticsearchConnection elasticsearchConnection =
			elasticsearchConnectionManager.getElasticsearchConnection();

		elasticsearchConnection.close();

		elasticsearchConnection.connect();
	}

	private static ElasticsearchSearchEngineFixture
		_elasticsearchSearchEngineFixture;

}