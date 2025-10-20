/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.logging;

import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.search.elasticsearch7.internal.ElasticsearchIndexSearcher;
import com.liferay.portal.search.elasticsearch7.internal.indexing.LiferayElasticsearchIndexingFixtureFactory;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search.CountSearchRequestExecutorImpl;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search.SearchSearchRequestExecutorImpl;
import com.liferay.portal.search.test.rule.logging.ExpectedLogMethodTestRule;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.search.test.util.logging.ExpectedLog;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Bryan Engler
 * @author André de Oliveira
 */
public class ElasticsearchIndexSearcherLoggingTest
	extends BaseIndexingTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			ExpectedLogMethodTestRule.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@ExpectedLog(
		expectedClass = CountSearchRequestExecutorImpl.class,
		expectedLevel = ExpectedLog.Level.FINE,
		expectedLog = "The search engine processed"
	)
	@Test
	public void testCountSearchRequestExecutorLogsViaIndexer() {
		searchCount(createSearchContext(), new MatchAllQuery());
	}

	@ExpectedLog(
		expectedClass = ElasticsearchIndexSearcher.class,
		expectedLevel = ExpectedLog.Level.INFO,
		expectedLog = "The search engine processed"
	)
	@Test
	public void testIndexerSearchCountLogs() {
		searchCount(createSearchContext(), new MatchAllQuery());
	}

	@ExpectedLog(
		expectedClass = ElasticsearchIndexSearcher.class,
		expectedLevel = ExpectedLog.Level.INFO,
		expectedLog = "The search engine processed"
	)
	@Test
	public void testIndexerSearchLogs() {
		search(createSearchContext());
	}

	@ExpectedLog(
		expectedClass = SearchSearchRequestExecutorImpl.class,
		expectedLevel = ExpectedLog.Level.FINE,
		expectedLog = "The search engine processed the request in"
	)
	@Test
	public void testSearchSearchRequestExecutorLogsExecutionTime() {
		search(createSearchContext());
	}

	@ExpectedLog(
		expectedClass = SearchSearchRequestExecutorImpl.class,
		expectedLevel = ExpectedLog.Level.FINE,
		expectedLog = "Search request string for"
	)
	@Test
	public void testSearchSearchRequestExecutorLogsRequestString() {
		search(createSearchContext());
	}

	@ExpectedLog(
		expectedClass = SearchSearchRequestExecutorImpl.class,
		expectedLevel = ExpectedLog.Level.INFO, expectedLog = "Stack trace for"
	)
	@Test
	public void testSearchSearchRequestExecutorLogsStackTraceInfo() {
		search(createSearchContext());
	}

	@Override
	protected IndexingFixture createIndexingFixture() {
		return LiferayElasticsearchIndexingFixtureFactory.getInstance();
	}

}