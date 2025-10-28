/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal;

import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.opensearch2.constants.OpenSearchSearchContextAttributes;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapper;
import com.liferay.portal.search.opensearch2.internal.configuration.OpenSearchConfigurationWrapperImpl;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael C. Han
 */
public class OpenSearchIndexSearcherTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@ClassRule
	public static final OpenSearchTestRule openSearchTestRule =
		OpenSearchTestRule.INSTANCE;

	@Before
	public void setUp() {
		_documentFixture.setUp();

		SearchRequestBuilderFactory searchRequestBuilderFactory =
			new SearchRequestBuilderFactoryImpl();

		_indexNameBuilder = _createIndexNameBuilder();

		_openSearchIndexSearcher = _createOpenSearchIndexSearcher(
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
			OpenSearchSearchContextAttributes.
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
			_openSearchIndexSearcher.createSearchSearchRequest(
				query, searchContext, searchRequest);

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
	public void testTrackTotalHitsLimit() {
		Mockito.when(
			_openSearchConfigurationWrapper.indexMaxResultWindow()
		).thenReturn(
			10000
		);

		Mockito.when(
			_openSearchConfigurationWrapper.trackTotalHits()
		).thenReturn(
			true
		);

		Mockito.when(
			_openSearchConfigurationWrapper.trackTotalHitsLimit()
		).thenReturn(
			11000
		);

		SearchContext searchContext = new SearchContext();

		SearchRequest searchRequest = _searchRequestBuilderFactory.builder(
			searchContext
		).build();

		SearchSearchRequest searchSearchRequest =
			_openSearchIndexSearcher.createSearchSearchRequest(
				Mockito.mock(Query.class), searchContext, searchRequest);

		Assert.assertEquals(
			Integer.valueOf(11000),
			searchSearchRequest.getTrackTotalHitsLimit());
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

	private OpenSearchIndexSearcher _createOpenSearchIndexSearcher(
		IndexNameBuilder indexNameBuilder,
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		OpenSearchIndexSearcher openSearchIndexSearcher =
			new OpenSearchIndexSearcher();

		ReflectionTestUtil.setFieldValue(
			openSearchIndexSearcher, "_indexNameBuilder", indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			openSearchIndexSearcher, "_openSearchConfigurationWrapper",
			_openSearchConfigurationWrapper);
		ReflectionTestUtil.setFieldValue(
			openSearchIndexSearcher, "_searchRequestBuilderFactory",
			searchRequestBuilderFactory);

		return openSearchIndexSearcher;
	}

	private final DocumentFixture _documentFixture = new DocumentFixture();
	private IndexNameBuilder _indexNameBuilder;
	private final OpenSearchConfigurationWrapper
		_openSearchConfigurationWrapper = Mockito.mock(
			OpenSearchConfigurationWrapperImpl.class);
	private OpenSearchIndexSearcher _openSearchIndexSearcher;
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}