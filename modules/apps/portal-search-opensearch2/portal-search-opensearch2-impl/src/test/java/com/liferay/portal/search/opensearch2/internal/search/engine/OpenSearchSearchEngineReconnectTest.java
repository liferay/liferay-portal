/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.opensearch2.internal.OpenSearchSearchEngine;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnection;
import com.liferay.portal.search.opensearch2.internal.connection.OpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.connection.TestOpenSearchConnectionManager;
import com.liferay.portal.search.opensearch2.internal.index.util.IndexFactoryCompanyIdRegistryUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author André de Oliveira
 * @author Petteri Karttunen
 */
public class OpenSearchSearchEngineReconnectTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static final OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		OpenSearchSearchEngineFixture openSearchSearchEngineFixture =
			new OpenSearchSearchEngineFixture(
				new TestOpenSearchConnectionManager());

		openSearchSearchEngineFixture.setUp();

		_openSearchSearchEngineFixture = openSearchSearchEngineFixture;
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_openSearchSearchEngineFixture.tearDown();

		for (long companyId :
				IndexFactoryCompanyIdRegistryUtil.getCompanyIds()) {

			IndexFactoryCompanyIdRegistryUtil.unregisterCompanyId(companyId);
		}
	}

	@Test
	public void testInitializeAfterReconnect() {
		OpenSearchSearchEngine openSearchSearchEngine =
			_openSearchSearchEngineFixture.getOpenSearchSearchEngine();

		long companyId = RandomTestUtil.randomLong();

		openSearchSearchEngine.initialize(companyId);

		_reconnect(
			_openSearchSearchEngineFixture.getOpenSearchConnectionManager());

		openSearchSearchEngine.initialize(companyId);
	}

	private void _reconnect(
		OpenSearchConnectionManager openSearchConnectionManager) {

		OpenSearchConnection openSearchConnection =
			openSearchConnectionManager.getOpenSearchConnection(
				TestOpenSearchConnectionManager.REMOTE_TEST_CONNECTION);

		openSearchConnection.close();

		openSearchConnection.connect();
	}

	private static OpenSearchSearchEngineFixture _openSearchSearchEngineFixture;

}