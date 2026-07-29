/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.index.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Felipe Lorenz
 */
@RunWith(Arquillian.class)
public class RankingIndexReindexerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testReindex() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		RankingIndexName rankingIndexName =
			_rankingIndexNameBuilder.getRankingIndexName(companyId);

		_searchEngineAdapter.execute(
			new DeleteIndexRequest(rankingIndexName.getIndexName()));

		_indexReindexer.reindex(companyId, IndexReindexer.ExecutionMode.SYNC);

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(
				new IndicesExistsIndexRequest(rankingIndexName.getIndexName()));

		Assert.assertTrue(indicesExistsIndexResponse.isExists());
	}

	@Inject(
		filter = "(component.name=com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexReindexer)"
	)
	private IndexReindexer _indexReindexer;

	@Inject
	private RankingIndexNameBuilder _rankingIndexNameBuilder;

	@Inject(
		filter = "|(search.engine.impl=Elasticsearch)(search.engine.impl=OpenSearch)"
	)
	private SearchEngineAdapter _searchEngineAdapter;

}