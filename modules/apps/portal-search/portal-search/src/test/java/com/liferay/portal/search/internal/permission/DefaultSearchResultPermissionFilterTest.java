/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.permission;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.HitsImpl;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.RelatedEntryIndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.FacetPostProcessor;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.search.configuration.DefaultSearchResultPermissionFilterConfiguration;
import com.liferay.portal.search.hits.SearchHitBuilder;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.hits.SearchHitsBuilder;
import com.liferay.portal.search.hits.TotalHitsRelation;
import com.liferay.portal.search.internal.searcher.SearchResponseImpl;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Gustavo Lima
 */
public class DefaultSearchResultPermissionFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSearchGroupAdmin() {
		_groupAdmin = true;
		_permissionFilteredSearchResultAccurateCountThreshold = 0;

		DefaultSearchResultPermissionFilter
			defaultSearchResultPermissionFilter =
				_getDefaultSearchResultPermissionFilter();

		SearchContext searchContext = _getSearchContext(0, 4);

		_assertPagination(
			searchContext, defaultSearchResultPermissionFilter, 4, 10);

		searchContext = _getSearchContext(0, 8);

		_assertPagination(
			searchContext, defaultSearchResultPermissionFilter, 8, 10);

		searchContext = _getSearchContext(0, 10);

		_assertPagination(
			searchContext, defaultSearchResultPermissionFilter, 10, 10);
	}

	@Test
	public void testSearchGuestWithThreshold() {
		_groupAdmin = false;
		_permissionFilteredSearchResultAccurateCountThreshold = 20;

		DefaultSearchResultPermissionFilter
			defaultSearchResultPermissionFilter =
				_getDefaultSearchResultPermissionFilter();

		SearchContext searchContext = _getSearchContext(0, 4);

		_assertPagination(
			searchContext, defaultSearchResultPermissionFilter, 4, 9);

		searchContext = _getSearchContext(0, 8);

		_assertPagination(
			searchContext, defaultSearchResultPermissionFilter, 8, 9);

		searchContext = _getSearchContext(0, 10);

		_assertPagination(
			searchContext, defaultSearchResultPermissionFilter, 9, 9);
	}

	@Test
	public void testSearchGuestWithoutThreshold() {
		_groupAdmin = false;
		_permissionFilteredSearchResultAccurateCountThreshold = 0;

		DefaultSearchResultPermissionFilter
			defaultSearchResultPermissionFilter =
				_getDefaultSearchResultPermissionFilter();

		SearchContext searchContext = _getSearchContext(0, 4);

		_assertPagination(
			searchContext, defaultSearchResultPermissionFilter, 4, 10);

		searchContext = _getSearchContext(0, 8);

		_assertPagination(
			searchContext, defaultSearchResultPermissionFilter, 8, 10);

		searchContext = _getSearchContext(0, 10);

		_assertPagination(
			searchContext, defaultSearchResultPermissionFilter, 9, 9);
	}

	@Test
	public void testSearchPreservesTotalHitsRelation() {
		_groupAdmin = false;
		_permissionFilteredSearchResultAccurateCountThreshold = 0;

		SearchContext searchContext = _getSearchContext(0, 10);

		searchContext.setEnd(QueryUtil.ALL_POS);
		searchContext.setStart(QueryUtil.ALL_POS);

		_mockSearchHits(searchContext, TotalHitsRelation.GTE);

		_assertTotalHitsRelation(
			searchContext, _getDefaultSearchResultPermissionFilter(),
			TotalHitsRelation.GTE);
	}

	@Test
	public void testSearchWithDynamicInheritanceEnabled() throws Exception {
		_groupAdmin = false;
		_permissionFilteredSearchResultAccurateCountThreshold = 0;

		Mockito.when(
			_indexer.hasPermission(
				Mockito.any(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.eq(ActionKeys.VIEW))
		).thenReturn(
			false
		);

		Mockito.when(
			_indexer.isFilterSearch()
		).thenReturn(
			true
		);

		Mockito.when(
			_indexerRegistry.getIndexer(Mockito.anyString())
		).thenReturn(
			(Indexer)_indexer
		);

		ResourcePermissionLocalService resourcePermissionLocalService =
			Mockito.mock(ResourcePermissionLocalService.class);

		ResourcePermissionLocalServiceUtil.setService(
			resourcePermissionLocalService);

		Mockito.when(
			resourcePermissionLocalService.hasResourcePermission(
				Mockito.anyLong(), Mockito.anyString(),
				Mockito.eq(ResourceConstants.SCOPE_COMPANY),
				Mockito.anyString(), Mockito.any(), Mockito.eq(ActionKeys.VIEW))
		).thenReturn(
			true
		);

		DefaultSearchResultPermissionFilter
			defaultSearchResultPermissionFilter =
				_getDefaultSearchResultPermissionFilter();

		SearchContext searchContext = _getSearchContext(0, 10);

		_assertResultsCount(
			searchContext, defaultSearchResultPermissionFilter, 9, false);
		_assertResultsCount(
			searchContext, defaultSearchResultPermissionFilter, 0, true);

		ResourcePermissionLocalServiceUtil.setService(null);
	}

	@Test
	public void testSearchWithEndExceedingIndexSearchLimit() {
		_groupAdmin = true;
		_permissionFilteredSearchResultAccurateCountThreshold = 0;

		DefaultSearchResultPermissionFilter
			defaultSearchResultPermissionFilter =
				_getDefaultSearchResultPermissionFilter();

		SearchContext searchContext = _getSearchContext(
			PropsValues.INDEX_SEARCH_LIMIT - 1,
			PropsValues.INDEX_SEARCH_LIMIT + 1);

		defaultSearchResultPermissionFilter.search(searchContext);

		Assert.assertEquals(
			PropsValues.INDEX_SEARCH_LIMIT, searchContext.getEnd());

		_groupAdmin = false;

		defaultSearchResultPermissionFilter.search(
			_getSearchContext(
				PropsValues.INDEX_SEARCH_LIMIT - 1,
				PropsValues.INDEX_SEARCH_LIMIT + 1));

		Mockito.verify(
			_searchRequestBuilder, Mockito.times(2)
		).from(
			PropsValues.INDEX_SEARCH_LIMIT - 1
		);

		Mockito.verify(
			_searchRequestBuilder, Mockito.times(2)
		).size(
			1
		);
	}

	@Test
	public void testSearchWithLargestAllowedStartAndEnd() {
		_groupAdmin = true;
		_permissionFilteredSearchResultAccurateCountThreshold = 0;

		DefaultSearchResultPermissionFilter
			defaultSearchResultPermissionFilter =
				_getDefaultSearchResultPermissionFilter();

		defaultSearchResultPermissionFilter.search(
			_getSearchContext(
				PropsValues.INDEX_SEARCH_LIMIT - 1,
				PropsValues.INDEX_SEARCH_LIMIT));

		_groupAdmin = false;

		defaultSearchResultPermissionFilter.search(
			_getSearchContext(
				PropsValues.INDEX_SEARCH_LIMIT - 1,
				PropsValues.INDEX_SEARCH_LIMIT));

		Mockito.verify(
			_searchFunction, Mockito.times(2)
		).apply(
			Mockito.any()
		);

		Mockito.verify(
			_searchRequestBuilder, Mockito.never()
		).from(
			PropsValues.INDEX_SEARCH_LIMIT - 1
		);
	}

	@Test
	public void testSearchWithSizeZero() {
		_groupAdmin = false;
		_permissionFilteredSearchResultAccurateCountThreshold = 0;

		_assertPagination(
			_getSearchContext(0, 0), _getDefaultSearchResultPermissionFilter(),
			0, _PRIVATE_DOCUMENTS + _PUBLIC_DOCUMENTS);
	}

	@Test
	public void testSearchWithStartExceedingIndexSearchLimit() {
		_groupAdmin = true;
		_permissionFilteredSearchResultAccurateCountThreshold = 0;

		DefaultSearchResultPermissionFilter
			defaultSearchResultPermissionFilter =
				_getDefaultSearchResultPermissionFilter();

		defaultSearchResultPermissionFilter.search(
			_getSearchContext(
				PropsValues.INDEX_SEARCH_LIMIT,
				PropsValues.INDEX_SEARCH_LIMIT + 1));

		_groupAdmin = false;

		defaultSearchResultPermissionFilter.search(
			_getSearchContext(
				PropsValues.INDEX_SEARCH_LIMIT,
				PropsValues.INDEX_SEARCH_LIMIT + 1));

		Mockito.verifyNoInteractions(_searchFunction);
	}

	private void _assertPagination(
		SearchContext searchContext,
		DefaultSearchResultPermissionFilter defaultSearchResultPermissionFilter,
		int pageCount, int totalCount) {

		Hits hits = defaultSearchResultPermissionFilter.search(searchContext);

		Document[] docs = hits.getDocs();

		Assert.assertEquals(hits.toString(), totalCount, hits.getLength());

		Assert.assertEquals(Arrays.toString(docs), pageCount, docs.length);
	}

	private void _assertResultsCount(
		SearchContext searchContext,
		DefaultSearchResultPermissionFilter defaultSearchResultPermissionFilter,
		int expectedResultsCount, boolean serviceTrackerContainsKey) {

		Mockito.when(
			_serviceTrackerMap.containsKey(Mockito.anyString())
		).thenReturn(
			serviceTrackerContainsKey
		);

		Assert.assertEquals(
			expectedResultsCount,
			defaultSearchResultPermissionFilter.search(
				searchContext
			).getLength());
	}

	private void _assertTotalHitsRelation(
		SearchContext searchContext,
		DefaultSearchResultPermissionFilter defaultSearchResultPermissionFilter,
		TotalHitsRelation expectedTotalHitsRelation) {

		defaultSearchResultPermissionFilter.search(searchContext);

		ArgumentCaptor<SearchHits> argumentCaptor = ArgumentCaptor.forClass(
			SearchHits.class);

		Mockito.verify(
			(SearchResponseImpl)searchContext.getAttribute("search.response")
		).setSearchHits(
			argumentCaptor.capture()
		);

		SearchHits searchHits = argumentCaptor.getValue();

		Assert.assertEquals(
			expectedTotalHitsRelation, searchHits.getTotalHitsRelation());
	}

	private DefaultSearchResultPermissionFilter
		_getDefaultSearchResultPermissionFilter() {

		_mockSearchResultPermissionFilterConfiguration();

		SearchRequestBuilderFactory searchRequestBuilderFactory =
			_getSearchRequestBuilderFactory();

		return new DefaultSearchResultPermissionFilter(
			_defaultSearchResultPermissionFilterConfiguration,
			Mockito.mock(FacetPostProcessor.class), _indexerRegistry,
			_permissionChecker, Mockito.mock(RelatedEntryIndexerRegistry.class),
			_searchFunction, searchRequestBuilderFactory, _serviceTrackerMap);
	}

	private Document _getDocument(String companyId, int index) {
		Document document = Mockito.mock(Document.class);

		Mockito.when(
			document.get(Field.COMPANY_ID)
		).thenReturn(
			companyId
		);

		Mockito.when(
			document.get(Field.ENTRY_CLASS_NAME)
		).thenReturn(
			"com.liferay.journal.model.JournalArticle"
		);

		Mockito.when(
			document.get(Field.ENTRY_CLASS_PK)
		).thenReturn(
			String.valueOf(index)
		);

		return document;
	}

	private Hits _getHits(int size) {
		Hits hits = new HitsImpl();

		size = Math.min(
			Math.max(
				size, _permissionFilteredSearchResultAccurateCountThreshold),
			_PRIVATE_DOCUMENTS + _PUBLIC_DOCUMENTS);

		Document[] documents = Arrays.copyOf(_documents, size);

		float[] scores = new float[size];

		for (int i = 0; i < size; i++) {
			scores[i] = i;
		}

		hits.setDocs(documents);
		hits.setLength(_documents.length);
		hits.setScores(scores);

		return Mockito.spy(hits);
	}

	private SearchContext _getSearchContext(int start, int end) {
		SearchContext searchContext = new SearchContext();

		_mockPermission(searchContext);
		_mockSearchResponse(searchContext);

		_setUpDocuments();

		searchContext.setEnd(end);
		searchContext.setStart(start);

		Mockito.when(
			_searchFunction.apply(searchContext)
		).thenReturn(
			_getHits(end)
		);

		return searchContext;
	}

	private SearchRequestBuilderFactory _getSearchRequestBuilderFactory() {
		SearchRequestBuilderFactory searchRequestBuilderFactory = Mockito.mock(
			SearchRequestBuilderFactory.class);

		Mockito.when(
			searchRequestBuilderFactory.builder(Mockito.any())
		).thenReturn(
			_searchRequestBuilder
		);

		Mockito.when(
			_searchRequestBuilder.build()
		).thenReturn(
			_searchRequest
		);

		return searchRequestBuilderFactory;
	}

	private void _mockPermission(SearchContext searchContext) {
		searchContext.setAttribute(Field.GROUP_ID, _USER_GROUP_ID);
		searchContext.setAttribute(Field.STATUS, 1);

		Mockito.when(
			_permissionChecker.getCompanyId()
		).thenReturn(
			_USER_GROUP_ID
		);

		Mockito.when(
			_permissionChecker.isGroupAdmin(_USER_GROUP_ID)
		).thenReturn(
			_groupAdmin
		);
	}

	private void _mockSearchHits(
		SearchContext searchContext, TotalHitsRelation totalHitsRelation) {

		SearchHitBuilder searchHitBuilder = new SearchHitBuilder();
		SearchHitsBuilder searchHitsBuilder = new SearchHitsBuilder();

		SearchResponseImpl searchResponseImpl =
			(SearchResponseImpl)searchContext.getAttribute("search.response");

		Mockito.when(
			searchResponseImpl.getSearchHits()
		).thenReturn(
			searchHitsBuilder.addSearchHit(
				searchHitBuilder.build()
			).totalHitsRelation(
				totalHitsRelation
			).build()
		);
	}

	private void _mockSearchResponse(SearchContext searchContext) {
		SearchResponseImpl searchResponseImpl = Mockito.mock(
			SearchResponseImpl.class);

		SearchHitsBuilder searchHitsBuilder = new SearchHitsBuilder();

		Mockito.when(
			searchResponseImpl.getSearchHits()
		).thenReturn(
			searchHitsBuilder.build()
		);

		searchContext.setAttribute("search.response", searchResponseImpl);
	}

	private void _mockSearchResultPermissionFilterConfiguration() {
		Mockito.when(
			_defaultSearchResultPermissionFilterConfiguration.
				permissionFilteredSearchResultAccurateCountThreshold()
		).thenReturn(
			_permissionFilteredSearchResultAccurateCountThreshold
		);

		Mockito.when(
			_defaultSearchResultPermissionFilterConfiguration.
				searchQueryResultWindowLimit()
		).thenReturn(
			100
		);
	}

	private void _setUpDocuments() {
		_documents = new Document[_PRIVATE_DOCUMENTS + _PUBLIC_DOCUMENTS];

		for (int i = 0; i < _PUBLIC_DOCUMENTS; i++) {
			Document document = _getDocument("1", i);

			_documents[i] = document;
		}

		for (int i = _PUBLIC_DOCUMENTS; i < _documents.length; i++) {
			Document document = _getDocument("0", i);

			_documents[i] = document;
		}
	}

	private static final int _PRIVATE_DOCUMENTS = 1;

	private static final int _PUBLIC_DOCUMENTS = 9;

	private static final long _USER_GROUP_ID = 1L;

	private static final SearchRequest _searchRequest = Mockito.mock(
		SearchRequest.class);

	private final DefaultSearchResultPermissionFilterConfiguration
		_defaultSearchResultPermissionFilterConfiguration = Mockito.mock(
			DefaultSearchResultPermissionFilterConfiguration.class);
	private Document[] _documents;
	private boolean _groupAdmin;
	private final Indexer<?> _indexer = Mockito.mock(Indexer.class);
	private final IndexerRegistry _indexerRegistry = Mockito.mock(
		IndexerRegistry.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private int _permissionFilteredSearchResultAccurateCountThreshold;
	private final Function<SearchContext, Hits> _searchFunction = Mockito.mock(
		Function.class);
	private final SearchRequestBuilder _searchRequestBuilder = Mockito.mock(
		SearchRequestBuilder.class);
	private final ServiceTrackerMap<String, ModelResourcePermission<?>>
		_serviceTrackerMap = Mockito.mock(ServiceTrackerMap.class);

}