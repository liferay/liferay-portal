/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.searcher;

import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.asset.SearchableAssetClassNamesProvider;
import com.liferay.portal.search.internal.expando.helper.ExpandoQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.helper.AddSearchKeywordsQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.helper.PostProcessSearchQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.helper.PreFilterContributorHelper;
import com.liferay.portal.search.internal.searcher.helper.IndexSearcherHelper;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequest;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class FacetedSearcherImpl
	extends BaseSearcher implements FacetedSearcher {

	public FacetedSearcherImpl(
		AddSearchKeywordsQueryContributorHelper
			addSearchKeywordsQueryContributorHelper,
		ExpandoQueryContributorHelper expandoQueryContributorHelper,
		IndexerRegistry indexerRegistry,
		IndexSearcherHelper indexSearcherHelper,
		PostProcessSearchQueryContributorHelper
			postProcessSearchQueryContributorHelper,
		PreFilterContributorHelper preFilterContributorHelper,
		SearchableAssetClassNamesProvider searchableAssetClassNamesProvider,
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		_addSearchKeywordsQueryContributorHelper =
			addSearchKeywordsQueryContributorHelper;
		_expandoQueryContributorHelper = expandoQueryContributorHelper;
		_indexerRegistry = indexerRegistry;
		_indexSearcherHelper = indexSearcherHelper;
		_postProcessSearchQueryContributorHelper =
			postProcessSearchQueryContributorHelper;
		_preFilterContributorHelper = preFilterContributorHelper;
		_searchableAssetClassNamesProvider = searchableAssetClassNamesProvider;
		_searchRequestBuilderFactory = searchRequestBuilderFactory;
	}

	@Override
	protected BooleanQuery createFullQuery(
			BooleanFilter fullQueryBooleanFilter, SearchContext searchContext)
		throws Exception {

		BooleanQuery searchQuery = new BooleanQuery();

		Map<String, Indexer<?>> entryClassNameIndexerMap =
			_getEntryClassNameIndexerMap(
				_getEntryClassNames(
					_getSearchRequest(searchContext),
					searchContext.getCompanyId()));

		_addSearchKeywords(
			searchQuery, entryClassNameIndexerMap.keySet(), searchContext);

		_postProcessSearchQuery(
			searchQuery, fullQueryBooleanFilter,
			entryClassNameIndexerMap.values(), searchContext);

		_addPreFilters(
			fullQueryBooleanFilter, entryClassNameIndexerMap, searchContext);

		BooleanQuery fullQuery = new BooleanQuery();

		if (fullQueryBooleanFilter.hasClauses()) {
			fullQuery.setPreBooleanFilter(fullQueryBooleanFilter);
		}

		if (searchQuery.hasClauses()) {
			fullQuery.add(searchQuery, BooleanClauseOccur.MUST);
		}

		BooleanClause<Query>[] booleanClauses =
			searchContext.getBooleanClauses();

		if (booleanClauses != null) {
			for (BooleanClause<Query> booleanClause : booleanClauses) {
				fullQuery.add(
					booleanClause.getClause(),
					booleanClause.getBooleanClauseOccur());
			}
		}

		_postProcessFullQuery(
			entryClassNameIndexerMap, fullQuery, searchContext);

		return fullQuery;
	}

	@Override
	protected Hits doSearch(SearchContext searchContext)
		throws SearchException {

		try {
			BooleanFilter booleanFilter = new BooleanFilter();

			booleanFilter.addRequiredTerm(
				Field.COMPANY_ID, searchContext.getCompanyId());

			Query query = createFullQuery(booleanFilter, searchContext);

			query.setQueryConfig(searchContext.getQueryConfig());

			return _indexSearcherHelper.search(searchContext, query);
		}
		catch (Exception exception) {
			throw new SearchException(exception);
		}
	}

	@Override
	protected boolean isUseSearchResultPermissionFilter(
		SearchContext searchContext) {

		List<String> entryClassNames = _getEntryClassNames(
			_getSearchRequest(searchContext), searchContext.getCompanyId());

		if (ListUtil.isEmpty(entryClassNames)) {
			return super.isFilterSearch();
		}

		for (String entryClassName : entryClassNames) {
			Indexer<?> indexer = _indexerRegistry.getIndexer(entryClassName);

			if (indexer == null) {
				continue;
			}

			if (indexer.isFilterSearch()) {
				return true;
			}
		}

		return super.isFilterSearch();
	}

	private void _addPreFilters(
		BooleanFilter booleanFilter,
		Map<String, Indexer<?>> entryClassNameIndexerMap,
		SearchContext searchContext) {

		_preFilterContributorHelper.contribute(
			booleanFilter, entryClassNameIndexerMap, searchContext);
	}

	private void _addSearchExpando(
		BooleanQuery booleanQuery, Collection<String> searchClassNames,
		SearchContext searchContext) {

		_expandoQueryContributorHelper.contribute(
			StringUtil.trim(searchContext.getKeywords()), booleanQuery,
			searchClassNames, searchContext);
	}

	private void _addSearchKeywords(
		BooleanQuery booleanQuery, Collection<String> searchClassNames,
		SearchContext searchContext) {

		_addSearchKeywordsQueryContributorHelper.contribute(
			booleanQuery, searchContext);

		_addSearchExpando(booleanQuery, searchClassNames, searchContext);
	}

	private Map<String, Indexer<?>> _getEntryClassNameIndexerMap(
		List<String> entryClassNames) {

		Map<String, Indexer<?>> entryClassNameIndexerMap =
			new LinkedHashMap<>();

		for (String entryClassName : entryClassNames) {
			Indexer<?> indexer = _indexerRegistry.getIndexer(entryClassName);

			if (indexer == null) {
				continue;
			}

			entryClassNameIndexerMap.put(entryClassName, indexer);
		}

		return entryClassNameIndexerMap;
	}

	private List<String> _getEntryClassNames(
		SearchRequest searchRequest, long companyId) {

		List<String> entryClassNames = searchRequest.getEntryClassNames();

		if (ListUtil.isNotEmpty(entryClassNames)) {
			return entryClassNames;
		}

		List<String> modelIndexerClassNames =
			searchRequest.getModelIndexerClassNames();

		if (ListUtil.isNotEmpty(modelIndexerClassNames)) {
			return modelIndexerClassNames;
		}

		return ListUtil.fromArray(
			_searchableAssetClassNamesProvider.getClassNames(companyId));
	}

	private SearchRequest _getSearchRequest(SearchContext searchContext) {
		return _searchRequestBuilderFactory.builder(
			searchContext
		).build();
	}

	private void _postProcessFullQuery(
			Map<String, Indexer<?>> entryClassNameIndexerMap,
			BooleanQuery fullQuery, SearchContext searchContext)
		throws Exception {

		for (Indexer<?> indexer : entryClassNameIndexerMap.values()) {
			for (IndexerPostProcessor indexerPostProcessor :
					indexer.getIndexerPostProcessors()) {

				indexerPostProcessor.postProcessFullQuery(
					fullQuery, searchContext);
			}
		}
	}

	private void _postProcessSearchQuery(
			BooleanQuery booleanQuery, BooleanFilter booleanFilter,
			Collection<Indexer<?>> indexers, SearchContext searchContext)
		throws Exception {

		_postProcessSearchQueryContributorHelper.contribute(
			booleanQuery, booleanFilter, indexers, searchContext);
	}

	private final AddSearchKeywordsQueryContributorHelper
		_addSearchKeywordsQueryContributorHelper;
	private final ExpandoQueryContributorHelper _expandoQueryContributorHelper;
	private final IndexSearcherHelper _indexSearcherHelper;
	private final IndexerRegistry _indexerRegistry;
	private final PostProcessSearchQueryContributorHelper
		_postProcessSearchQueryContributorHelper;
	private final PreFilterContributorHelper _preFilterContributorHelper;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;
	private final SearchableAssetClassNamesProvider
		_searchableAssetClassNamesProvider;

}