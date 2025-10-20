/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.indexer;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.RelatedEntryIndexer;
import com.liferay.portal.kernel.search.RelatedEntryIndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.indexer.IndexerQueryBuilder;
import com.liferay.portal.search.internal.expando.helper.ExpandoQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.helper.AddSearchKeywordsQueryContributorHelper;
import com.liferay.portal.search.internal.indexer.helper.PreFilterContributorHelper;
import com.liferay.portal.search.internal.util.SearchStringUtil;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.SearchContextContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;
import com.liferay.portal.search.spi.model.query.contributor.helper.SearchContextContributorHelper;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class IndexerQueryBuilderImpl<T extends BaseModel<?>>
	implements IndexerQueryBuilder {

	public IndexerQueryBuilderImpl(
		AddSearchKeywordsQueryContributorHelper
			addSearchKeywordsQueryContributorHelper,
		ExpandoQueryContributorHelper expandoQueryContributorHelper,
		IndexerRegistry indexerRegistry,
		ModelSearchSettings modelSearchSettings,
		ModelKeywordQueryContributorsRegistry
			modelKeywordQueryContributorsRegistry,
		Iterable<SearchContextContributor> modelSearchContextContributors,
		PreFilterContributorHelper preFilterContributorHelper,
		Iterable<SearchContextContributor> searchContextContributors,
		String className,
		RelatedEntryIndexerRegistry relatedEntryIndexerRegistry) {

		_addSearchKeywordsQueryContributorHelper =
			addSearchKeywordsQueryContributorHelper;
		_expandoQueryContributorHelper = expandoQueryContributorHelper;
		_indexerRegistry = indexerRegistry;
		_modelSearchSettings = modelSearchSettings;
		_modelKeywordQueryContributorsRegistry =
			modelKeywordQueryContributorsRegistry;
		_modelSearchContextContributors = modelSearchContextContributors;
		_preFilterContributorHelper = preFilterContributorHelper;
		_searchContextContributors = searchContextContributors;
		_className = className;
		_relatedEntryIndexerRegistry = relatedEntryIndexerRegistry;
	}

	@Override
	public BooleanQuery getQuery(SearchContext searchContext) {
		_resetFullQuery(searchContext);

		String[] fullQueryEntryClassNames =
			searchContext.getFullQueryEntryClassNames();

		if (ArrayUtil.isNotEmpty(fullQueryEntryClassNames)) {
			searchContext.setAttribute(
				"relatedEntryClassNames",
				_modelSearchSettings.getSearchClassNames());
		}

		String[] entryClassNames = ArrayUtil.append(
			_modelSearchSettings.getSearchClassNames(),
			fullQueryEntryClassNames);

		searchContext.setEntryClassNames(entryClassNames);

		_contributeSearchContext(searchContext);

		Map<String, Indexer<?>> entryClassNameIndexerMap =
			_getEntryClassNameIndexerMap(entryClassNames);

		BooleanFilter booleanFilter = new BooleanFilter();

		_addPreFilters(booleanFilter, entryClassNameIndexerMap, searchContext);

		BooleanQuery fullQuery = _createFullQuery(booleanFilter, searchContext);

		fullQuery.setQueryConfig(searchContext.getQueryConfig());

		return fullQuery;
	}

	protected void addPreFiltersFromModel(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		_preFilterContributorHelper.contribute(
			booleanFilter, modelSearchSettings, searchContext);
	}

	protected void addSearchTermsFromModel(
		BooleanQuery booleanQuery, SearchContext searchContext) {

		if (IndexerProvidedClausesUtil.shouldSuppress(searchContext)) {
			return;
		}

		if (searchContext.isIncludeAttachments() ||
			searchContext.isIncludeDiscussions()) {

			_contributeClauses(booleanQuery, searchContext);

			return;
		}

		BooleanQuery keywordsBooleanQuery = new BooleanQueryImpl();

		_contributeClauses(keywordsBooleanQuery, searchContext);

		if (!keywordsBooleanQuery.hasClauses()) {
			return;
		}

		try {
			BooleanQuery modelBooleanQuery = new BooleanQueryImpl();

			modelBooleanQuery.add(
				new TermQueryImpl(
					"entryClassName", _modelSearchSettings.getClassName()),
				BooleanClauseOccur.MUST);
			modelBooleanQuery.add(
				keywordsBooleanQuery, BooleanClauseOccur.MUST);

			booleanQuery.add(modelBooleanQuery, BooleanClauseOccur.SHOULD);
		}
		catch (ParseException parseException) {
			throw new SystemException(parseException);
		}
	}

	protected void contribute(
		List<KeywordQueryContributor> keywordQueryContributors,
		BooleanQuery booleanQuery, SearchContext searchContext) {

		for (KeywordQueryContributor keywordQueryContributor :
				keywordQueryContributors) {

			keywordQueryContributor.contribute(
				searchContext.getKeywords(), booleanQuery,
				new KeywordQueryContributorHelper() {

					@Override
					public String getClassName() {
						return _modelSearchSettings.getClassName();
					}

					@Override
					public String[] getSearchClassNames() {
						return _modelSearchSettings.getSearchClassNames();
					}

					@Override
					public SearchContext getSearchContext() {
						return searchContext;
					}

				});
		}
	}

	private void _add(
		BooleanQuery booleanQuery, Query query,
		BooleanClauseOccur booleanClauseOccur) {

		try {
			booleanQuery.add(query, booleanClauseOccur);
		}
		catch (ParseException parseException) {
			throw new SystemException(parseException);
		}
	}

	private void _addPreFilters(
		BooleanFilter queryBooleanFilter,
		Map<String, Indexer<?>> entryClassNameIndexerMap,
		SearchContext searchContext) {

		_preFilterContributorHelper.contribute(
			queryBooleanFilter, entryClassNameIndexerMap, searchContext);
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

	private void _addSearchTermsFromIndexerPostProcessors(
		BooleanQuery booleanQuery, BooleanFilter booleanFilter,
		SearchContext searchContext) {

		List<IndexerPostProcessor> indexerPostProcessors =
			IndexerRegistryUtil.getIndexerPostProcessors(_className);

		indexerPostProcessors.forEach(
			indexerPostProcessor -> {
				try {
					indexerPostProcessor.postProcessSearchQuery(
						booleanQuery, booleanFilter, searchContext);
				}
				catch (RuntimeException runtimeException) {
					throw runtimeException;
				}
				catch (Exception exception) {
					throw new SystemException(exception);
				}
			});
	}

	private void _contributeClauses(
		BooleanQuery booleanQuery, SearchContext searchContext) {

		contribute(
			_modelKeywordQueryContributorsRegistry.
				filterKeywordQueryContributors(
					_getStrings(
						"search.full.query.clause.contributors.excludes",
						searchContext),
					_getStrings(
						"search.full.query.clause.contributors.includes",
						searchContext)),
			booleanQuery, searchContext);
	}

	private void _contributeSearchContext(SearchContext searchContext) {
		SearchContextContributorHelper searchContextContributorHelper =
			_modelSearchSettings::getSearchClassNames;

		_searchContextContributors.forEach(
			searchContextContributor -> searchContextContributor.contribute(
				searchContext, searchContextContributorHelper));

		_modelSearchContextContributors.forEach(
			modelSearchContextContributor ->
				modelSearchContextContributor.contribute(
					searchContext, searchContextContributorHelper));
	}

	private BooleanQuery _createFullQuery(
		BooleanFilter fullQueryBooleanFilter, SearchContext searchContext) {

		BooleanQuery booleanQuery = new BooleanQueryImpl();

		if (fullQueryBooleanFilter.hasClauses()) {
			booleanQuery.setPreBooleanFilter(fullQueryBooleanFilter);
		}

		BooleanQuery keywordBooleanQuery = _createKeywordQuery(
			fullQueryBooleanFilter, searchContext);

		if (keywordBooleanQuery.hasClauses()) {
			_add(booleanQuery, keywordBooleanQuery, BooleanClauseOccur.MUST);
		}

		BooleanClause<Query>[] booleanClauses =
			searchContext.getBooleanClauses();

		if (booleanClauses != null) {
			for (BooleanClause<Query> booleanClause : booleanClauses) {
				_add(
					booleanQuery, booleanClause.getClause(),
					booleanClause.getBooleanClauseOccur());
			}
		}

		_postProcessFullQuery(booleanQuery, searchContext);

		return booleanQuery;
	}

	private BooleanQuery _createKeywordQuery(
		BooleanFilter fullQueryBooleanFilter, SearchContext searchContext) {

		BooleanQuery booleanQuery = new BooleanQueryImpl();

		_addSearchKeywords(
			booleanQuery,
			Arrays.asList(_modelSearchSettings.getSearchClassNames()),
			searchContext);

		addSearchTermsFromModel(booleanQuery, searchContext);

		_addSearchTermsFromIndexerPostProcessors(
			booleanQuery, fullQueryBooleanFilter, searchContext);

		return booleanQuery;
	}

	private Map<String, Indexer<?>> _getEntryClassNameIndexerMap(
		String[] entryClassNames) {

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

	private Collection<String> _getStrings(
		String string, SearchContext searchContext) {

		return Arrays.asList(
			SearchStringUtil.splitAndUnquote(
				(String)searchContext.getAttribute(string)));
	}

	private void _postProcessFullQuery(
		BooleanQuery booleanQuery, SearchContext searchContext) {

		List<IndexerPostProcessor> indexerPostProcessors =
			IndexerRegistryUtil.getIndexerPostProcessors(_className);

		indexerPostProcessors.forEach(
			indexerPostProcessor -> {
				try {
					indexerPostProcessor.postProcessFullQuery(
						booleanQuery, searchContext);
				}
				catch (RuntimeException runtimeException) {
					throw runtimeException;
				}
				catch (Exception exception) {
					throw new SystemException(exception);
				}
			});
	}

	private void _resetFullQuery(SearchContext searchContext) {
		searchContext.clearFullQueryEntryClassNames();

		for (RelatedEntryIndexer relatedEntryIndexer :
				_relatedEntryIndexerRegistry.getRelatedEntryIndexers()) {

			relatedEntryIndexer.updateFullQuery(searchContext);
		}
	}

	private final AddSearchKeywordsQueryContributorHelper
		_addSearchKeywordsQueryContributorHelper;
	private final String _className;
	private final ExpandoQueryContributorHelper _expandoQueryContributorHelper;
	private final IndexerRegistry _indexerRegistry;
	private final ModelKeywordQueryContributorsRegistry
		_modelKeywordQueryContributorsRegistry;
	private final Iterable<SearchContextContributor>
		_modelSearchContextContributors;
	private final ModelSearchSettings _modelSearchSettings;
	private final PreFilterContributorHelper _preFilterContributorHelper;
	private final RelatedEntryIndexerRegistry _relatedEntryIndexerRegistry;
	private final Iterable<SearchContextContributor> _searchContextContributors;

}