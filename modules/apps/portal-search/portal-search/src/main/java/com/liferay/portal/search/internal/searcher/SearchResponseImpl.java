/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.searcher;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.groupby.GroupByResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.hits.SearchHitsBuilder;
import com.liferay.portal.search.hits.TotalHitsRelation;
import com.liferay.portal.search.internal.legacy.searcher.FacetContextImpl;
import com.liferay.portal.search.searcher.FacetContext;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.SearchTimeValue;
import com.liferay.portal.search.stats.StatsResponse;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author André de Oliveira
 */
public class SearchResponseImpl implements SearchResponse, Serializable {

	public SearchResponseImpl(SearchContext searchContext) {
		_searchContext = searchContext;

		_facetContextImpl = new FacetContextImpl(searchContext);
	}

	public void addFederatedSearchResponse(SearchResponse searchResponse) {
		_federatedSearchResponsesMap.put(
			searchResponse.getFederatedSearchKey(), searchResponse);
	}

	@Override
	public AggregationResult getAggregationResult(String name) {
		return _aggregationResultsMap.get(name);
	}

	@Override
	public Map<String, AggregationResult> getAggregationResultsMap() {
		return Collections.unmodifiableMap(_aggregationResultsMap);
	}

	@Override
	public long getCount() {
		return _count;
	}

	@Override
	public List<Document> getDocuments() {
		if (_searchHits == null) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			_searchHits.getSearchHits(), SearchHit::getDocument);
	}

	@Override
	public List<com.liferay.portal.kernel.search.Document> getDocuments71() {
		if (_hits == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(_hits.getDocs());
	}

	@Override
	public String getFederatedSearchKey() {
		return _federatedSearchKey;
	}

	@Override
	public SearchResponse getFederatedSearchResponse(String key) {
		if (Validator.isBlank(key)) {
			return this;
		}

		return _federatedSearchResponsesMap.get(key);
	}

	@Override
	public Collection<SearchResponse> getFederatedSearchResponses() {
		return _federatedSearchResponsesMap.values();
	}

	@Override
	public List<GroupByResponse> getGroupByResponses() {
		return Collections.unmodifiableList(_groupByResponses);
	}

	@Override
	public SearchRequest getRequest() {
		return _searchRequest;
	}

	@Override
	public String getRequestString() {
		return _requestString;
	}

	@Override
	public String getResponseString() {
		return _responseString;
	}

	@Override
	public SearchHits getSearchHits() {
		if (_searchHits == null) {
			SearchHitsBuilder searchHitsBuilder = new SearchHitsBuilder();

			return searchHitsBuilder.build();
		}

		return _searchHits;
	}

	@Override
	public SearchTimeValue getSearchTimeValue() {
		return _searchTimeValue;
	}

	@Override
	public Map<String, StatsResponse> getStatsResponseMap() {
		return Collections.unmodifiableMap(_statsResponseMap);
	}

	@Override
	public int getTotalHits() {
		if (_hits == null) {
			return 0;
		}

		return _hits.getLength();
	}

	@Override
	public TotalHitsRelation getTotalHitsRelation() {
		if (_searchHits == null) {
			return TotalHitsRelation.EQ;
		}

		return _searchHits.getTotalHitsRelation();
	}

	public void setAggregationResultsMap(
		Map<String, AggregationResult> aggregationResultsMap) {

		_aggregationResultsMap.clear();

		_aggregationResultsMap.putAll(aggregationResultsMap);
	}

	public void setCount(long count) {
		_count = count;
	}

	public void setFederatedSearchKey(String federatedSearchKey) {
		_federatedSearchKey = federatedSearchKey;
	}

	public void setGroupByResponses(List<GroupByResponse> groupByResponses) {
		_groupByResponses.clear();

		_groupByResponses.addAll(groupByResponses);
	}

	public void setHits(Hits hits) {
		_hits = hits;
	}

	public void setRequest(SearchRequest searchRequest) {
		_searchRequest = searchRequest;
	}

	public void setRequestString(String requestString) {
		_requestString = GetterUtil.getString(requestString);
	}

	public void setResponseString(String responseString) {
		_responseString = GetterUtil.getString(responseString);
	}

	public void setSearchHits(SearchHits searchHits) {
		_searchHits = searchHits;
	}

	public void setSearchTimeValue(SearchTimeValue searchTimeValue) {
		_searchTimeValue = searchTimeValue;
	}

	public void setStatsResponseMap(Map<String, StatsResponse> map) {
		_statsResponseMap.clear();

		_statsResponseMap.putAll(map);
	}

	@Override
	public void withFacetContext(Consumer<FacetContext> facetContextConsumer) {
		facetContextConsumer.accept(_facetContextImpl);
	}

	@Override
	public <T> T withFacetContextGet(
		Function<FacetContext, T> facetContextFunction) {

		return facetContextFunction.apply(_facetContextImpl);
	}

	@Override
	public void withHits(Consumer<Hits> hitsConsumer) {
		hitsConsumer.accept(_hits);
	}

	@Override
	public <T> T withHitsGet(Function<Hits, T> hitsFunction) {
		return hitsFunction.apply(_hits);
	}

	@Override
	public void withSearchContext(
		Consumer<SearchContext> searchContextConsumer) {

		searchContextConsumer.accept(_searchContext);
	}

	@Override
	public <T> T withSearchContextGet(Function<SearchContext, T> function) {
		return function.apply(_searchContext);
	}

	private final Map<String, AggregationResult> _aggregationResultsMap =
		new LinkedHashMap<>();
	private long _count;
	private final FacetContextImpl _facetContextImpl;
	private String _federatedSearchKey = StringPool.BLANK;
	private final Map<String, SearchResponse> _federatedSearchResponsesMap =
		new LinkedHashMap<>();
	private final List<GroupByResponse> _groupByResponses = new ArrayList<>();
	private Hits _hits;
	private String _requestString = StringPool.BLANK;
	private String _responseString = StringPool.BLANK;
	private final SearchContext _searchContext;
	private SearchHits _searchHits;
	private SearchRequest _searchRequest;
	private SearchTimeValue _searchTimeValue;
	private final Map<String, StatsResponse> _statsResponseMap =
		new LinkedHashMap<>();

}