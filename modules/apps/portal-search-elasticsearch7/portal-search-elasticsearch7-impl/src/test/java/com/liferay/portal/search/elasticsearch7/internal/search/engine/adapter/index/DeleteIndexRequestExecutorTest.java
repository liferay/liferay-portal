/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index;

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesOptions;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public class DeleteIndexRequestExecutorTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			DeleteIndexRequestExecutorTest.class.getSimpleName());

		_elasticsearchFixture.setUp();
	}

	@After
	public void tearDown() throws Exception {
		_elasticsearchFixture.tearDown();
	}

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
			new DeleteIndexRequestExecutor(_elasticsearchFixture);

		org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
			elasticsearchDeleteIndexRequest =
				deleteIndexRequestExecutor.createDeleteIndexRequest(
					deleteIndexRequest);

		String[] indices = elasticsearchDeleteIndexRequest.indices();

		Assert.assertEquals(Arrays.toString(indices), 2, indices.length);
		Assert.assertEquals(_INDEX_NAME_1, indices[0]);
		Assert.assertEquals(_INDEX_NAME_2, indices[1]);

		org.elasticsearch.action.support.IndicesOptions
			elasticsearchIndicesOptions =
				elasticsearchDeleteIndexRequest.indicesOptions();

		Assert.assertEquals(
			indicesOptions.isAllowNoIndices(),
			elasticsearchIndicesOptions.allowNoIndices());
		Assert.assertEquals(
			indicesOptions.isExpandToClosedIndices(),
			elasticsearchIndicesOptions.expandWildcardsClosed());
		Assert.assertEquals(
			indicesOptions.isIgnoreUnavailable(),
			elasticsearchIndicesOptions.ignoreUnavailable());
		Assert.assertEquals(
			indicesOptions.isExpandToOpenIndices(),
			elasticsearchIndicesOptions.expandWildcardsOpen());
		Assert.assertTrue(
			elasticsearchIndicesOptions.allowAliasesToMultipleIndices());
		Assert.assertFalse(elasticsearchIndicesOptions.forbidClosedIndices());
		Assert.assertFalse(elasticsearchIndicesOptions.ignoreAliases());
	}

	private static final String _INDEX_NAME_1 = "test_request_index1";

	private static final String _INDEX_NAME_2 = "test_request_index2";

	private ElasticsearchFixture _elasticsearchFixture;

}