/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.search.index.test;

import com.liferay.analytics.machine.learning.content.MostViewedContentRecommendation;
import com.liferay.analytics.machine.learning.content.MostViewedContentRecommendationManager;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@FeatureFlag("LRAC-14771")
@RunWith(Arquillian.class)
public class MostViewedContentRecommendationIndexReindexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testReindex() throws Exception {
		_addMostViewedContentRecommendation();

		_indexReindexer.reindex(
			TestPropsValues.getCompanyId(), IndexReindexer.ExecutionMode.FULL);

		Assert.assertEquals(
			0,
			_mostViewedContentRecommendationManager.
				getMostViewedContentRecommendationsCount(
					null, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId()));
	}

	@Test
	public void testReindexWithMissingIndex() throws Exception {
		String indexName =
			_indexNameBuilder.getIndexName(TestPropsValues.getCompanyId()) +
				"-most-viewed-content-recommendation";

		_searchEngineAdapter.execute(new DeleteIndexRequest(indexName));

		_indexReindexer.reindex(
			TestPropsValues.getCompanyId(), IndexReindexer.ExecutionMode.SYNC);

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(
				new IndicesExistsIndexRequest(indexName));

		Assert.assertTrue(indicesExistsIndexResponse.isExists());
	}

	@Test
	public void testReindexWithSyncExecutionMode() throws Exception {
		_addMostViewedContentRecommendation();

		long count =
			_mostViewedContentRecommendationManager.
				getMostViewedContentRecommendationsCount(
					null, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId());

		_indexReindexer.reindex(
			TestPropsValues.getCompanyId(), IndexReindexer.ExecutionMode.SYNC);

		Assert.assertEquals(
			count,
			_mostViewedContentRecommendationManager.
				getMostViewedContentRecommendationsCount(
					null, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId()));
	}

	private void _addMostViewedContentRecommendation() throws Exception {
		long count =
			_mostViewedContentRecommendationManager.
				getMostViewedContentRecommendationsCount(
					null, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId());

		MostViewedContentRecommendation mostViewedContentRecommendation =
			new MostViewedContentRecommendation();

		mostViewedContentRecommendation.setCompanyId(
			TestPropsValues.getCompanyId());
		mostViewedContentRecommendation.setRecommendedEntryClassPK(
			RandomTestUtil.randomLong());
		mostViewedContentRecommendation.setScore(RandomTestUtil.randomFloat());

		_mostViewedContentRecommendationManager.
			addMostViewedContentRecommendation(mostViewedContentRecommendation);

		IdempotentRetryAssert.retryAssert(
			3, TimeUnit.SECONDS,
			() -> {
				Assert.assertEquals(
					count + 1,
					_mostViewedContentRecommendationManager.
						getMostViewedContentRecommendationsCount(
							null, TestPropsValues.getCompanyId(),
							TestPropsValues.getUserId()));

				return null;
			});
	}

	@Inject
	private IndexNameBuilder _indexNameBuilder;

	@Inject(
		filter = "component.name=com.liferay.analytics.machine.learning.internal.search.index.MostViewedContentRecommendationIndexReindexer"
	)
	private IndexReindexer _indexReindexer;

	@Inject
	private MostViewedContentRecommendationManager
		_mostViewedContentRecommendationManager;

	@Inject(
		filter = "|(search.engine.impl=Elasticsearch)(search.engine.impl=OpenSearch)"
	)
	private SearchEngineAdapter _searchEngineAdapter;

}