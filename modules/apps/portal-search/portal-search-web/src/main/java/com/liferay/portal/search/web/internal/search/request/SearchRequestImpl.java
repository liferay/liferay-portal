/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.request;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.web.search.request.SearchSettingsContributor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author André de Oliveira
 */
public class SearchRequestImpl {

	public SearchRequestImpl(
		SearchContextBuilder searchContextBuilder,
		SearchContainerBuilder searchContainerBuilder, Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		_searchContextBuilder = searchContextBuilder;
		_searchContainerBuilder = searchContainerBuilder;
		_searcher = searcher;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
	}

	public void addSearchSettingsContributor(
		SearchSettingsContributor searchSettingsContributor) {

		_searchSettingsContributors.add(searchSettingsContributor);
	}

	public void removeSearchSettingsContributor(
		SearchSettingsContributor searchSettingsContributor) {

		_searchSettingsContributors.remove(searchSettingsContributor);
	}

	public SearchResponseImpl search() {
		SearchContext searchContext = _buildSearchContext();

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(searchContext);

		searchRequestBuilder.fetchSource(true);

		SearchSettingsImpl searchSettingsImpl = _buildSettings(
			searchRequestBuilder, searchContext);

		SearchContainer<Document> searchContainer = _buildSearchContainer(
			searchSettingsImpl);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		_populateSearchContainer(searchContainer, searchResponse);

		SearchResponseImpl searchResponseImpl = new SearchResponseImpl();

		searchResponse.withHits(
			hits -> {
				searchResponseImpl.setDocuments(hits.toList());
				searchResponseImpl.setHits(hits);
				searchResponseImpl.setTotalHits(hits.getLength());
			});

		searchResponseImpl.setKeywords(searchContext.getKeywords());
		searchResponseImpl.setPaginationDelta(searchContainer.getDelta());
		searchResponseImpl.setPaginationStart(searchContainer.getCur());
		searchResponseImpl.setSearchContainer(searchContainer);
		searchResponseImpl.setSearchResponse(searchResponse);
		searchResponseImpl.setSearchSettings(searchSettingsImpl);

		return searchResponseImpl;
	}

	private SearchContainer<Document> _buildSearchContainer(
		SearchSettingsImpl searchSettingsImpl) {

		return _searchContainerBuilder.getSearchContainer(searchSettingsImpl);
	}

	private SearchContext _buildSearchContext() {
		SearchContext searchContext = _searchContextBuilder.getSearchContext();

		searchContext.setAttribute("filterExpired", Boolean.TRUE);
		searchContext.setAttribute("paginationType", "more");

		return searchContext;
	}

	private SearchSettingsImpl _buildSettings(
		SearchRequestBuilder searchRequestBuilder,
		SearchContext searchContext) {

		SearchSettingsImpl searchSettingsImpl = new SearchSettingsImpl(
			searchRequestBuilder, searchContext);

		_searchSettingsContributors.forEach(
			searchSettingsContributor -> searchSettingsContributor.contribute(
				searchSettingsImpl));

		return searchSettingsImpl;
	}

	private void _populateSearchContainer(
		SearchContainer<Document> searchContainer,
		SearchResponse searchResponse) {

		searchContainer.setSearch(true);

		searchResponse.withHits(
			hits -> searchContainer.setResultsAndTotal(
				hits::toList, hits.getLength()));
	}

	private final SearchContainerBuilder _searchContainerBuilder;
	private final SearchContextBuilder _searchContextBuilder;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private final Set<SearchSettingsContributor> _searchSettingsContributors =
		new HashSet<>();
	private final Searcher _searcher;

}