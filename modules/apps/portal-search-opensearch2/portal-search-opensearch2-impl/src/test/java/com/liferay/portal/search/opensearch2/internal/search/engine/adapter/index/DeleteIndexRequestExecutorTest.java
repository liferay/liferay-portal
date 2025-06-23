/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.index;

import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.search.opensearch2.internal.BaseOpenSearchTestCase;
import com.liferay.portal.search.opensearch2.internal.OpenSearchTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public class DeleteIndexRequestExecutorTest extends BaseOpenSearchTestCase {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Test
	public void testIndexRequestTranslation() {
		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(
			_INDEX_NAME_1, _INDEX_NAME_2);

		IndicesOptions indicesOptions = new IndicesOptions();

		indicesOptions.setAllowNoIndices(true);
		indicesOptions.setExpandToClosedIndices(false);
		indicesOptions.setExpandToOpenIndices(false);
		indicesOptions.setIgnoreUnavailable(true);

		deleteIndexRequest.setIndicesOptions(indicesOptions);

		DeleteIndexRequestExecutor deleteIndexRequestExecutor =
			new DeleteIndexRequestExecutor(openSearchConnectionManager);

		org.opensearch.client.opensearch.indices.DeleteIndexRequest
			openSearchDeleteIndexRequest =
				deleteIndexRequestExecutor.createDeleteIndexRequest(
					deleteIndexRequest);

		List<String> indices = openSearchDeleteIndexRequest.index();

		Assert.assertEquals(String.join(", ", indices), 2, indices.size());
		Assert.assertEquals(_INDEX_NAME_1, indices.get(0));
		Assert.assertEquals(_INDEX_NAME_2, indices.get(1));

		Assert.assertEquals(
			indicesOptions.isAllowNoIndices(),
			openSearchDeleteIndexRequest.allowNoIndices());

		Assert.assertEquals(
			indicesOptions.isIgnoreUnavailable(),
			openSearchDeleteIndexRequest.ignoreUnavailable());
	}

	private static final String _INDEX_NAME_1 = "test_index1";

	private static final String _INDEX_NAME_2 = "test_index2";

}