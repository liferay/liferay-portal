/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexSearcher;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcher;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.suggest.QuerySuggester;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.BaseSearchRequest;
import com.liferay.portal.search.engine.adapter.search.BaseSearchResponse;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.legacy.searcher.SearchResponseBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchResponseBuilder;
import com.liferay.portal.search.solr8.configuration.SolrConfiguration;
import com.liferay.portal.search.solr8.internal.search.response.HitsImpl;

import java.util.Map;

import org.apache.commons.lang.time.StopWatch;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Zsolt Berentey
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.portal.search.solr8.configuration.SolrConfiguration",
	property = "search.engine.impl=Solr", service = IndexSearcher.class
)
public class SolrIndexSearcher extends BaseIndexSearcher {

	@Override
	public String getQueryString(SearchContext searchContext, Query query) {
		return _searchEngineAdapter.getQueryString(query);
	}

	@Override
	public Hits search(SearchContext searchContext, Query query) {
		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			int end = searchContext.getEnd();
			int start = searchContext.getStart();

			SearchRequest searchRequest = _getSearchRequest(searchContext);

			Integer from = searchRequest.getFrom();
			Integer size = searchRequest.getSize();

			if ((from == null) && (size != null)) {
				end = size;
				start = 0;
			}
			else if ((from != null) && (size != null)) {
				end = from + size;
				start = from;
			}

			if (start == QueryUtil.ALL_POS) {
				start = 0;
			}
			else if (start < 0) {
				throw new IllegalArgumentException("Invalid start " + start);
			}

			if (end == QueryUtil.ALL_POS) {
				end = GetterUtil.getInteger(
					PropsUtil.get(PropsKeys.INDEX_SEARCH_LIMIT));
			}
			else if (end < 0) {
				throw new IllegalArgumentException("Invalid end " + end);
			}

			SearchResponseBuilder searchResponseBuilder =
				_getSearchResponseBuilder(searchContext);

			Hits hits = null;

			while (true) {
				SearchSearchRequest searchSearchRequest =
					createSearchSearchRequest(
						end, query, searchContext, searchRequest, start);

				SearchSearchResponse searchSearchResponse =
					_searchEngineAdapter.execute(searchSearchRequest);

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"The search engine processed ",
							searchSearchResponse.getSearchRequestString(),
							" in ", searchSearchResponse.getExecutionTime(),
							" ms"));
				}

				populateResponse(searchSearchResponse, searchResponseBuilder);

				searchResponseBuilder.searchHits(
					searchSearchResponse.getSearchHits());

				hits = searchSearchResponse.getHits();

				Document[] documents = hits.getDocs();

				if ((documents.length != 0) || (start == 0)) {
					break;
				}

				int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
					start, end, hits.getLength());

				start = startAndEnd[0];
				end = startAndEnd[1];
			}

			hits.setStart(stopWatch.getStartTime());

			return hits;
		}
		catch (Exception exception) {
			if (_logExceptionsOnly) {
				_log.error(exception);
			}
			else {
				if (exception instanceof RuntimeException) {
					throw (RuntimeException)exception;
				}

				throw new SystemException(exception.getMessage(), exception);
			}

			return new HitsImpl();
		}
		finally {
			if (_log.isInfoEnabled()) {
				stopWatch.stop();

				_log.info(
					StringBundler.concat(
						"Searching took ", stopWatch.getTime(), " ms"));
			}
		}
	}

	@Override
	public long searchCount(SearchContext searchContext, Query query) {
		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		try {
			CountSearchRequest countSearchRequest = createCountSearchRequest(
				searchContext, query);

			CountSearchResponse countSearchResponse =
				_searchEngineAdapter.execute(countSearchRequest);

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"The search engine processed ",
						countSearchResponse.getSearchRequestString(), " in ",
						countSearchResponse.getExecutionTime(), " ms"));
			}

			populateResponse(
				countSearchResponse, _getSearchResponseBuilder(searchContext));

			return countSearchResponse.getCount();
		}
		catch (Exception exception) {
			if (_logExceptionsOnly) {
				_log.error(exception);
			}
			else {
				if (exception instanceof RuntimeException) {
					throw (RuntimeException)exception;
				}

				throw new SystemException(exception.getMessage(), exception);
			}

			return 0;
		}
		finally {
			if (_log.isInfoEnabled()) {
				stopWatch.stop();

				_log.info(
					StringBundler.concat(
						"Searching took ", stopWatch.getTime(), " ms"));
			}
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_solrConfiguration = ConfigurableUtil.createConfigurable(
			SolrConfiguration.class, properties);

		_defaultCollection = _solrConfiguration.defaultCollection();
		_logExceptionsOnly = _solrConfiguration.logExceptionsOnly();
	}

	protected CountSearchRequest createCountSearchRequest(
		SearchContext searchContext, Query query) {

		CountSearchRequest countSearchRequest = new CountSearchRequest();

		populateBaseSearchRequest(
			countSearchRequest, _getSearchRequest(searchContext), searchContext,
			query);

		return countSearchRequest;
	}

	protected SearchSearchRequest createSearchSearchRequest(
		int end, Query query, SearchContext searchContext,
		SearchRequest searchRequest, int start) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		populateBaseSearchRequest(
			searchSearchRequest, searchRequest, searchContext, query);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		searchSearchRequest.setAllFieldsSelected(
			queryConfig.isAllFieldsSelected());
		searchSearchRequest.setAlternateUidFieldName(
			queryConfig.getAlternateUidFieldName());

		searchSearchRequest.setGroupBy(searchContext.getGroupBy());
		searchSearchRequest.setGroupByRequests(
			searchRequest.getGroupByRequests());
		searchSearchRequest.setHighlightEnabled(
			queryConfig.isHighlightEnabled());
		searchSearchRequest.setHighlightFieldNames(
			queryConfig.getHighlightFieldNames());
		searchSearchRequest.setHighlightFragmentSize(
			queryConfig.getHighlightFragmentSize());
		searchSearchRequest.setHighlightRequireFieldMatch(
			queryConfig.isHighlightRequireFieldMatch());
		searchSearchRequest.setHighlightSnippetSize(
			queryConfig.getHighlightSnippetSize());
		searchSearchRequest.setIncludeResponseString(
			searchRequest.isIncludeResponseString());
		searchSearchRequest.setLocale(queryConfig.getLocale());
		searchSearchRequest.setLuceneSyntax(
			GetterUtil.getBoolean(
				searchContext.getAttribute(
					SearchContextAttributes.ATTRIBUTE_KEY_LUCENE_SYNTAX)));

		if (query != null) {
			searchSearchRequest.setPostFilter(query.getPostFilter());
		}

		searchSearchRequest.setScoreEnabled(queryConfig.isScoreEnabled());
		searchSearchRequest.setSelectedFieldNames(
			queryConfig.getSelectedFieldNames());
		searchSearchRequest.setSize(end - start);
		searchSearchRequest.setSorts(searchContext.getSorts());
		searchSearchRequest.setSorts(searchRequest.getSorts());
		searchSearchRequest.setStart(start);
		searchSearchRequest.setStats(searchContext.getStats());

		return searchSearchRequest;
	}

	@Override
	protected QuerySuggester getQuerySuggester() {
		return _querySuggester;
	}

	protected void populateBaseSearchRequest(
		BaseSearchRequest baseSearchRequest, SearchRequest searchRequest,
		SearchContext searchContext, Query query) {

		baseSearchRequest.putAllFacets(searchContext.getFacets());
		baseSearchRequest.setBasicFacetSelection(
			GetterUtil.getBoolean(
				searchContext.getAttribute(
					SearchContextAttributes.
						ATTRIBUTE_KEY_BASIC_FACET_SELECTION)));
		baseSearchRequest.setExplain(searchRequest.isExplain());
		baseSearchRequest.setIncludeResponseString(
			searchRequest.isIncludeResponseString());
		baseSearchRequest.setIndexNames(new String[] {_defaultCollection});

		long companyId = searchContext.getCompanyId();

		if ((companyId >= 0) && (query != null)) {
			BooleanFilter preBooleanFilter = query.getPreBooleanFilter();

			if (preBooleanFilter == null) {
				preBooleanFilter = new BooleanFilter();
			}

			preBooleanFilter.addRequiredTerm(
				Field.COMPANY_ID, searchContext.getCompanyId());

			query.setPreBooleanFilter(preBooleanFilter);
		}

		baseSearchRequest.setStatsRequests(searchRequest.getStatsRequests());

		setLegacyQuery(baseSearchRequest, query);
		setQuery(baseSearchRequest, searchRequest);
	}

	protected void populateResponse(
		BaseSearchResponse baseSearchResponse,
		SearchResponseBuilder searchResponseBuilder) {

		searchResponseBuilder.count(
			baseSearchResponse.getCount()
		).requestString(
			baseSearchResponse.getSearchRequestString()
		).responseString(
			baseSearchResponse.getSearchResponseString()
		).searchTimeValue(
			baseSearchResponse.getSearchTimeValue()
		).statsResponseMap(
			baseSearchResponse.getStatsResponseMap()
		);
	}

	protected void populateResponse(
		SearchSearchResponse searchSearchResponse,
		SearchResponseBuilder searchResponseBuilder) {

		populateResponse(
			(BaseSearchResponse)searchSearchResponse, searchResponseBuilder);

		searchResponseBuilder.groupByResponses(
			searchSearchResponse.getGroupByResponses());
	}

	protected void setLegacyQuery(
		BaseSearchRequest baseSearchRequest, Query query) {

		baseSearchRequest.setQuery(query);
	}

	protected void setQuery(
		BaseSearchRequest baseSearchRequest, SearchRequest searchRequest) {

		baseSearchRequest.setQuery(searchRequest.getQuery());
	}

	private SearchRequest _getSearchRequest(SearchContext searchContext) {
		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(searchContext);

		return searchRequestBuilder.build();
	}

	private SearchResponseBuilder _getSearchResponseBuilder(
		SearchContext searchContext) {

		return _searchResponseBuilderFactory.builder(searchContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SolrIndexSearcher.class);

	private volatile String _defaultCollection;
	private volatile boolean _logExceptionsOnly;

	@Reference(target = "(search.engine.impl=Solr)")
	private QuerySuggester _querySuggester;

	@Reference(target = "(search.engine.impl=Solr)")
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private SearchResponseBuilderFactory _searchResponseBuilderFactory;

	private volatile SolrConfiguration _solrConfiguration;

}