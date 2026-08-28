/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.searcher;

import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.collapse.Collapse;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.groupby.GroupByRequest;
import com.liferay.portal.search.highlight.Highlight;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.rescore.Rescore;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.stats.StatsRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author André de Oliveira
 */
public class SearchRequestImpl implements SearchRequest, Serializable {

	public static boolean isBasicFacetSelection(SearchContext searchContext) {
		return GetterUtil.getBoolean(
			searchContext.getAttribute(
				SearchContextAttributes.ATTRIBUTE_KEY_BASIC_FACET_SELECTION));
	}

	public SearchRequestImpl(SearchContext searchContext) {
		_searchContext = searchContext;
	}

	public SearchRequestImpl(SearchRequestImpl searchRequestImpl) {
		_aggregationsMap.putAll(searchRequestImpl._aggregationsMap);
		_basicFacetSelection = searchRequestImpl._basicFacetSelection;
		_collapse = searchRequestImpl._collapse;
		_complexQueryParts.addAll(searchRequestImpl._complexQueryParts);
		_connectionId = searchRequestImpl._connectionId;
		_emptySearchEnabled = searchRequestImpl._emptySearchEnabled;
		_excludeContributors.addAll(searchRequestImpl._excludeContributors);
		_explain = searchRequestImpl._explain;
		_federatedSearchKey = searchRequestImpl._federatedSearchKey;
		_federatedSearchRequestsMap.putAll(
			searchRequestImpl._federatedSearchRequestsMap);
		_from = searchRequestImpl._from;
		_groupByRequests.addAll(searchRequestImpl._groupByRequests);
		_highlight = searchRequestImpl._highlight;
		_includeContributors.addAll(searchRequestImpl._includeContributors);
		_includeResponseString = searchRequestImpl._includeResponseString;
		_modelIndexerClassNames.addAll(
			searchRequestImpl._modelIndexerClassNames);
		_pipelineAggregationsMap.putAll(
			searchRequestImpl._pipelineAggregationsMap);
		_postFilterComplexQueryParts.addAll(
			searchRequestImpl._postFilterComplexQueryParts);
		_postFilterQuery = searchRequestImpl._postFilterQuery;
		_query = searchRequestImpl._query;
		_rescoreQuery = searchRequestImpl._rescoreQuery;
		_rescores.addAll(searchRequestImpl._rescores);
		_retainFacetSelections = searchRequestImpl._retainFacetSelections;
		_searchContext = searchRequestImpl._searchContext;
		_size = searchRequestImpl._size;
		_sorts.addAll(searchRequestImpl._sorts);
		_statsRequests.addAll(searchRequestImpl._statsRequests);
		_trackTotalHitsLimit = searchRequestImpl._trackTotalHitsLimit;
	}

	public void addAggregation(Aggregation aggregation) {
		_aggregationsMap.put(aggregation.getName(), aggregation);
	}

	public void addComplexQueryPart(ComplexQueryPart complexQueryPart) {
		_complexQueryParts.add(complexQueryPart);
	}

	public void addEntryClassNames(String... entryClassNames) {
		_searchContext.setEntryClassNames(entryClassNames);
	}

	public void addExcludeContributors(String... ids) {
		Collections.addAll(_excludeContributors, ids);
	}

	public void addFederatedSearchRequest(SearchRequest searchRequest) {
		_federatedSearchRequestsMap.put(
			searchRequest.getFederatedSearchKey(), searchRequest);
	}

	public void addIncludeContributors(String... ids) {
		Collections.addAll(_includeContributors, ids);
	}

	public void addIndex(String index) {
		QueryConfig queryConfig = _searchContext.getQueryConfig();

		queryConfig.setSelectedIndexNames(
			ArrayUtil.append(queryConfig.getSelectedIndexNames(), index));
	}

	public void addPipelineAggregation(
		PipelineAggregation pipelineAggregation) {

		_pipelineAggregationsMap.put(
			pipelineAggregation.getName(), pipelineAggregation);
	}

	public void addPostFilterQueryPart(ComplexQueryPart complexQueryPart) {
		_postFilterComplexQueryParts.add(complexQueryPart);
	}

	public void addRescore(Rescore rescore) {
		_rescores.add(rescore);
	}

	public void addSelectedFieldNames(String... selectedFieldNames) {
		QueryConfig queryConfig = _searchContext.getQueryConfig();

		queryConfig.addSelectedFieldNames(selectedFieldNames);
	}

	public void addSort(Sort sort) {
		_sorts.add(sort);
	}

	@Override
	public Map<String, Aggregation> getAggregationsMap() {
		return Collections.unmodifiableMap(_aggregationsMap);
	}

	@Override
	public Collapse getCollapse() {
		return _collapse;
	}

	@Override
	public List<ComplexQueryPart> getComplexQueryParts() {
		return Collections.unmodifiableList(_complexQueryParts);
	}

	@Override
	public String getConnectionId() {
		return _connectionId;
	}

	@Override
	public List<String> getEntryClassNames() {
		return Collections.unmodifiableList(
			Arrays.asList(_searchContext.getEntryClassNames()));
	}

	@Override
	public List<String> getExcludeContributors() {
		return Collections.unmodifiableList(_excludeContributors);
	}

	@Override
	public String getFederatedSearchKey() {
		return _federatedSearchKey;
	}

	@Override
	public List<SearchRequest> getFederatedSearchRequests() {
		return new ArrayList<>(_federatedSearchRequestsMap.values());
	}

	@Override
	public Boolean getFetchSource() {
		return _fetchSource;
	}

	@Override
	public String[] getFetchSourceExcludes() {
		return _fetchSourceExcludes;
	}

	@Override
	public String[] getFetchSourceIncludes() {
		return _fetchSourceIncludes;
	}

	@Override
	public Integer getFrom() {
		return _from;
	}

	@Override
	public List<GroupByRequest> getGroupByRequests() {
		return Collections.unmodifiableList(_groupByRequests);
	}

	@Override
	public Highlight getHighlight() {
		return _highlight;
	}

	@Override
	public List<String> getIncludeContributors() {
		return Collections.unmodifiableList(_includeContributors);
	}

	@Override
	public List<String> getIndexes() {
		QueryConfig queryConfig = _searchContext.getQueryConfig();

		return Collections.unmodifiableList(
			Arrays.asList(queryConfig.getSelectedIndexNames()));
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getModelIndexerClassNames()}
	 */
	@Deprecated
	@Override
	public List<Class<?>> getModelIndexerClasses() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getModelIndexerClassNames() {
		return _modelIndexerClassNames;
	}

	@Override
	public String getPaginationStartParameterName() {
		return _paginationStartParameterName;
	}

	@Override
	public Map<String, PipelineAggregation> getPipelineAggregationsMap() {
		return Collections.unmodifiableMap(_pipelineAggregationsMap);
	}

	@Override
	public List<ComplexQueryPart> getPostFilterComplexQueryParts() {
		return Collections.unmodifiableList(_postFilterComplexQueryParts);
	}

	@Override
	public Query getPostFilterQuery() {
		return _postFilterQuery;
	}

	@Override
	public Query getQuery() {
		return _query;
	}

	@Override
	public String getQueryString() {
		return _searchContext.getKeywords();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getRescores()}
	 */
	@Deprecated
	@Override
	public Query getRescoreQuery() {
		return _rescoreQuery;
	}

	@Override
	public List<Rescore> getRescores() {
		return _rescores;
	}

	public SearchContext getSearchContext() {
		return _searchContext;
	}

	@Override
	public Integer getSize() {
		return _size;
	}

	@Override
	public List<Sort> getSorts() {
		return Collections.unmodifiableList(_sorts);
	}

	@Override
	public List<StatsRequest> getStatsRequests() {
		return Collections.unmodifiableList(_statsRequests);
	}

	@Override
	public String[] getStoredFields() {
		return _storedFields;
	}

	@Override
	public Integer getTrackTotalHitsLimit() {
		return _trackTotalHitsLimit;
	}

	@Override
	public boolean isBasicFacetSelection() {
		return _basicFacetSelection;
	}

	@Override
	public boolean isEmptySearchEnabled() {
		return _emptySearchEnabled;
	}

	@Override
	public boolean isExplain() {
		return _explain;
	}

	@Override
	public boolean isIncludeResponseString() {
		return _includeResponseString;
	}

	@Override
	public boolean isRetainFacetSelections() {
		return _retainFacetSelections;
	}

	public void setBasicFacetSelection(boolean basicFacetSelection) {
		_basicFacetSelection = basicFacetSelection;

		_searchContext.setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_BASIC_FACET_SELECTION,
			Boolean.valueOf(basicFacetSelection));
	}

	public void setCollapse(Collapse collapse) {
		_collapse = collapse;
	}

	public void setCompanyId(Long companyId) {
		_searchContext.setCompanyId(GetterUtil.getLong(companyId));
	}

	public void setConnectionId(String connectionId) {
		_connectionId = connectionId;
	}

	public void setEmptySearchEnabled(boolean emptySearchEnabled) {
		_emptySearchEnabled = emptySearchEnabled;

		_searchContext.setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH,
			Boolean.valueOf(emptySearchEnabled));
	}

	public void setExplain(boolean explain) {
		_explain = explain;
	}

	public void setFederatedSearchKey(String federatedSearchKey) {
		_federatedSearchKey = federatedSearchKey;
	}

	public void setFetchSource(boolean fetchSource) {
		_fetchSource = fetchSource;
	}

	public void setFetchSourceExcludes(String[] fetchSourceExcludes) {
		_fetchSourceExcludes = fetchSourceExcludes;
	}

	public void setFetchSourceIncludes(String[] fetchSourceIncludes) {
		_fetchSourceIncludes = fetchSourceIncludes;
	}

	public void setFrom(Integer from) {
		_from = from;
	}

	public void setGroupByRequests(GroupByRequest... groupByRequests) {
		_groupByRequests.clear();

		Collections.addAll(_groupByRequests, groupByRequests);
	}

	public void setGroupIds(long... groupIds) {
		_searchContext.setGroupIds(groupIds);
	}

	public void setHighlight(Highlight highlight) {
		_highlight = highlight;
	}

	public void setHighlightEnabled(boolean highlightEnabled) {
		QueryConfig queryConfig = _searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(highlightEnabled);
	}

	public void setHighlightFields(String... highlightFields) {
		QueryConfig queryConfig = _searchContext.getQueryConfig();

		queryConfig.setHighlightFieldNames(highlightFields);
	}

	public void setIncludeResponseString(boolean includeResponseString) {
		_includeResponseString = includeResponseString;
	}

	public void setIndexes(String... indexes) {
		QueryConfig queryConfig = _searchContext.getQueryConfig();

		queryConfig.setSelectedIndexNames(indexes);
	}

	public void setLocale(Locale locale) {
		_searchContext.setLocale(locale);
	}

	public void setModelIndexerClassNames(String... classNames) {
		_modelIndexerClassNames.clear();

		Collections.addAll(_modelIndexerClassNames, classNames);
	}

	public void setOwnerUserId(Long userId) {
		_searchContext.setOwnerUserId(GetterUtil.getLong(userId));
	}

	public void setPaginationStartParameterName(
		String paginationStartParameterName) {

		_paginationStartParameterName = paginationStartParameterName;
	}

	public void setPostFilterQuery(Query query) {
		_postFilterQuery = query;
	}

	public void setQuery(Query query) {
		_query = query;
	}

	public void setQueryString(String queryString) {
		_searchContext.setKeywords(queryString);
	}

	public void setRescores(List<Rescore> rescores) {
		_rescores = rescores;
	}

	public void setRetainFacetSelections(boolean retainFacetSelections) {
		_retainFacetSelections = retainFacetSelections;

		_searchContext.setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_RETAIN_FACET_SELECTIONS,
			Boolean.valueOf(retainFacetSelections));
	}

	public void setSelectedFieldNames(String... selectedFieldNames) {
		QueryConfig queryConfig = _searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(selectedFieldNames);
	}

	public void setSize(Integer size) {
		_size = size;
	}

	public void setSorts(Sort... sorts) {
		_sorts.clear();

		Collections.addAll(_sorts, sorts);
	}

	public void setStatsRequests(StatsRequest... statsRequests) {
		_statsRequests.clear();

		Collections.addAll(_statsRequests, statsRequests);
	}

	public void setStoredFields(String... storedFields) {
		_storedFields = storedFields;
	}

	public void setTrackTotalHitsLimit(Integer trackTotalHitsLimit) {
		_trackTotalHitsLimit = trackTotalHitsLimit;
	}

	private final Map<String, Aggregation> _aggregationsMap =
		new LinkedHashMap<>();
	private boolean _basicFacetSelection;
	private Collapse _collapse;
	private final List<ComplexQueryPart> _complexQueryParts = new ArrayList<>();
	private String _connectionId;
	private boolean _emptySearchEnabled;
	private final List<String> _excludeContributors = new ArrayList<>();
	private boolean _explain;
	private String _federatedSearchKey;
	private final Map<String, SearchRequest> _federatedSearchRequestsMap =
		new LinkedHashMap<>();
	private Boolean _fetchSource;
	private String[] _fetchSourceExcludes;
	private String[] _fetchSourceIncludes;
	private Integer _from;
	private final List<GroupByRequest> _groupByRequests = new ArrayList<>();
	private Highlight _highlight;
	private final List<String> _includeContributors = new ArrayList<>();
	private boolean _includeResponseString;
	private final List<String> _modelIndexerClassNames = new ArrayList<>();
	private String _paginationStartParameterName;
	private final Map<String, PipelineAggregation> _pipelineAggregationsMap =
		new LinkedHashMap<>();
	private final List<ComplexQueryPart> _postFilterComplexQueryParts =
		new ArrayList<>();
	private Query _postFilterQuery;
	private Query _query;
	private Query _rescoreQuery;
	private List<Rescore> _rescores = new ArrayList<>();
	private boolean _retainFacetSelections;
	private final SearchContext _searchContext;
	private Integer _size;
	private final List<Sort> _sorts = new ArrayList<>();
	private final List<StatsRequest> _statsRequests = new ArrayList<>();
	private String[] _storedFields;
	private Integer _trackTotalHitsLimit;

}