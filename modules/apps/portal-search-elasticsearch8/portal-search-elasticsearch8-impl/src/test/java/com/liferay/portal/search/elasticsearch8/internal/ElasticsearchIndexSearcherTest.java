/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.HitsImpl;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.elasticsearch8.constants.ElasticsearchSearchContextAttributes;
import com.liferay.portal.search.elasticsearch8.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.searcher.SearchResponseBuilderFactoryImpl;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Michael C. Han
 */
public class ElasticsearchIndexSearcherTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_documentFixture.setUp();

		SearchRequestBuilderFactory searchRequestBuilderFactory =
			new SearchRequestBuilderFactoryImpl();

		_indexNameBuilder = _createIndexNameBuilder();

		_elasticsearchIndexSearcher = _createElasticsearchIndexSearcher(
			_indexNameBuilder, searchRequestBuilderFactory);

		_searchRequestBuilderFactory = searchRequestBuilderFactory;
	}

	@After
	public void tearDown() {
		_documentFixture.tearDown();
	}

	@Test
	public void testSearchContextAttributes() throws SearchException {
		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(
			ElasticsearchSearchContextAttributes.
				ATTRIBUTE_KEY_SEARCH_REQUEST_PREFERENCE,
			"testValue");
		searchContext.setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_BASIC_FACET_SELECTION,
			Boolean.TRUE);
		searchContext.setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_LUCENE_SYNTAX, Boolean.TRUE);

		SearchRequest searchRequest = _searchRequestBuilderFactory.builder(
			searchContext
		).build();

		Query query = Mockito.mock(Query.class);

		SearchSearchRequest searchSearchRequest =
			_elasticsearchIndexSearcher.createSearchSearchRequest(
				searchRequest, searchContext, query);

		searchSearchRequest.setSize(0);
		searchSearchRequest.setSorts(searchContext.getSorts());
		searchSearchRequest.setSorts(searchRequest.getSorts());
		searchSearchRequest.setStart(0);
		searchSearchRequest.setStats(searchContext.getStats());

		Assert.assertTrue(searchSearchRequest.isBasicFacetSelection());
		Assert.assertTrue(searchSearchRequest.isLuceneSyntax());

		Assert.assertEquals("testValue", searchSearchRequest.getPreference());
	}

	@Test
	public void testSearchPastLastPage() {
		_testSearchPastLastPage();
		_testSearchPastLastPageWithoutFallback();
	}

	@Test
	public void testSearchPastMaxResultWindow() {
		int maxResultWindow = 10000;

		Mockito.when(
			_elasticsearchConfigurationWrapper.indexMaxResultWindow()
		).thenReturn(
			maxResultWindow
		);

		Mockito.when(
			_searchEngineAdapter.execute(Mockito.any(SearchSearchRequest.class))
		).thenThrow(
			new RuntimeException("Search of size 0 attempted")
		);

		SearchContext searchContext = new SearchContext();

		searchContext.setEnd(maxResultWindow + 20);
		searchContext.setStart(maxResultWindow);

		_elasticsearchIndexSearcher.search(
			searchContext, Mockito.mock(Query.class));
	}

	@Test
	public void testTrackTotalHitsLimit() {
		_assertTrackTotalHitsLimit(null, 11000);
	}

	@Test
	public void testTrackTotalHitsLimitAboveConnectorLimit() {
		_assertTrackTotalHitsLimit(50000, 11000);
	}

	@Test
	public void testTrackTotalHitsLimitBelowConnectorLimit() {
		_assertTrackTotalHitsLimit(1000, 1000);
	}

	@Test
	public void testTrackTotalHitsLimitZero() {
		_assertTrackTotalHitsLimit(0, 0);
	}

	private void _assertSearchPastLastPage(
		Boolean fallbackToLastPage, int expectedDocumentsLength,
		int expectedSearchCount, int expectedStart) {

		Mockito.clearInvocations(_searchEngineAdapter);

		Mockito.when(
			_elasticsearchConfigurationWrapper.indexMaxResultWindow()
		).thenReturn(
			10000
		);

		Mockito.when(
			_searchEngineAdapter.execute(Mockito.any(SearchSearchRequest.class))
		).thenReturn(
			_createSearchSearchResponse(),
			_createSearchSearchResponse(new DocumentImpl())
		);

		SearchContext searchContext = new SearchContext();

		if (fallbackToLastPage != null) {
			searchContext.setAttribute(
				SearchContextAttributes.ATTRIBUTE_KEY_FALLBACK_TO_LAST_PAGE,
				fallbackToLastPage);
		}

		searchContext.setEnd(80);
		searchContext.setStart(60);

		Hits hits = _elasticsearchIndexSearcher.search(
			searchContext, Mockito.mock(Query.class));

		Document[] documents = hits.getDocs();

		Assert.assertEquals(
			Arrays.toString(documents), expectedDocumentsLength,
			documents.length);

		ArgumentCaptor<SearchSearchRequest> argumentCaptor =
			ArgumentCaptor.forClass(SearchSearchRequest.class);

		Mockito.verify(
			_searchEngineAdapter, Mockito.times(expectedSearchCount)
		).execute(
			argumentCaptor.capture()
		);

		SearchSearchRequest searchSearchRequest = argumentCaptor.getValue();

		Assert.assertEquals(
			Integer.valueOf(expectedStart), searchSearchRequest.getStart());
	}

	private void _assertTrackTotalHitsLimit(
		Integer trackTotalHitsLimit, int expectedTrackTotalHitsLimit) {

		Mockito.when(
			_elasticsearchConfigurationWrapper.indexMaxResultWindow()
		).thenReturn(
			10000
		);

		Mockito.when(
			_elasticsearchConfigurationWrapper.trackTotalHitsLimit()
		).thenReturn(
			11000
		);

		SearchContext searchContext = new SearchContext();

		SearchRequest searchRequest = _searchRequestBuilderFactory.builder(
			searchContext
		).trackTotalHitsLimit(
			trackTotalHitsLimit
		).build();

		SearchSearchRequest searchSearchRequest =
			_elasticsearchIndexSearcher.createSearchSearchRequest(
				searchRequest, searchContext, Mockito.mock(Query.class));

		Assert.assertEquals(
			Integer.valueOf(expectedTrackTotalHitsLimit),
			searchSearchRequest.getTrackTotalHitsLimit());
	}

	private ElasticsearchIndexSearcher _createElasticsearchIndexSearcher(
		IndexNameBuilder indexNameBuilder,
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		ElasticsearchIndexSearcher elasticsearchIndexSearcher =
			new ElasticsearchIndexSearcher();

		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexSearcher, "_elasticsearchConfigurationWrapper",
			_elasticsearchConfigurationWrapper);
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexSearcher, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexSearcher, "_searchEngineAdapter",
			_searchEngineAdapter);
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexSearcher, "_searchResponseBuilderFactory",
			new SearchResponseBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexSearcher, "_searchRequestBuilderFactory",
			searchRequestBuilderFactory);

		return elasticsearchIndexSearcher;
	}

	private IndexNameBuilder _createIndexNameBuilder() {
		IndexNameBuilder indexNameBuilder = Mockito.mock(
			IndexNameBuilder.class);

		Mockito.when(
			indexNameBuilder.getIndexName(Mockito.anyLong())
		).then(
			invocation -> String.valueOf(invocation.getArgument(0, Long.class))
		);

		return indexNameBuilder;
	}

	private SearchSearchResponse _createSearchSearchResponse(
		Document... documents) {

		SearchSearchResponse searchSearchResponse = new SearchSearchResponse();

		Hits hits = new HitsImpl();

		hits.setDocs(documents);
		hits.setLength(50);

		searchSearchResponse.setHits(hits);

		return searchSearchResponse;
	}

	private void _testSearchPastLastPage() {
		_assertSearchPastLastPage(null, 1, 2, 40);
	}

	private void _testSearchPastLastPageWithoutFallback() {
		_assertSearchPastLastPage(Boolean.FALSE, 0, 1, 60);
	}

	private final DocumentFixture _documentFixture = new DocumentFixture();
	private final ElasticsearchConfigurationWrapper
		_elasticsearchConfigurationWrapper = Mockito.mock(
			ElasticsearchConfigurationWrapper.class);
	private ElasticsearchIndexSearcher _elasticsearchIndexSearcher;
	private IndexNameBuilder _indexNameBuilder;
	private final SearchEngineAdapter _searchEngineAdapter = Mockito.mock(
		SearchEngineAdapter.class);
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}