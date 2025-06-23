/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.opensearch.client.opensearch.indices.ExistsRequest;

/**
 * @author Michael C. Han
 */
public class IndicesExistsIndexRequestExecutorTest
	extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testIndexRequestTranslation() {
		IndicesExistsIndexRequest indicesExistsIndexRequest =
			new IndicesExistsIndexRequest(_INDEX_NAME_1, _INDEX_NAME_2);

		IndicesExistsIndexRequestExecutor indicesExistsIndexRequestExecutor =
			new IndicesExistsIndexRequestExecutor(openSearchConnectionManager);

		ExistsRequest existsRequest =
			indicesExistsIndexRequestExecutor.createExistsRequest(
				indicesExistsIndexRequest);

		List<String> indices = existsRequest.index();

		Assert.assertEquals(String.join(", ", indices), 2, indices.size());
		Assert.assertEquals(_INDEX_NAME_1, indices.get(0));
		Assert.assertEquals(_INDEX_NAME_2, indices.get(1));
	}

	private static final String _INDEX_NAME_1 = "test_index1";

	private static final String _INDEX_NAME_2 = "test_index2";

}