/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.engine.adapter.search;

import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.engine.adapter.ccr.CrossClusterRequest;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.highlight.Highlight;
import com.liferay.portal.search.pit.PointInTime;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.rescore.Rescore;
import com.liferay.portal.search.stats.StatsRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Michael C. Han
 */
@ProviderType
public abstract class BaseSearchRequest extends CrossClusterRequest {

	public void addAggregation(Aggregation aggregation) {
		_aggregationsMap.put(aggregation.getName(), aggregation);
	}

	public void addComplexQueryParts(
		Collection<ComplexQueryPart> complexQueryParts) {

		_complexQueryParts.addAll(complexQueryParts);
	}

	public void addIndexBoost(String index, float boost) {
		_indexBoosts.put(index, boost);
	}

	public void addPipelineAggregation(
		PipelineAggregation pipelineAggregation) {

		_pipelineAggregationsMap.put(
			pipelineAggregation.getName(), pipelineAggregation);
	}

	public void addPostFilterComplexQueryParts(
		Collection<ComplexQueryPart> complexQueryParts) {

		_postFilterComplexQueryParts.addAll(complexQueryParts);
	}

	public Map<String, Aggregation> getAggregationsMap() {
		return Collections.unmodifiableMap(_aggregationsMap);
	}

	public List<ComplexQueryPart> getComplexQueryParts() {
		return Collections.unmodifiableList(_complexQueryParts);
	}

	public Boolean getExplain() {
		return _explain;
	}

	public Map<String, Facet> getFacets() {
		return _facets;
	}

	public Highlight getHighlight() {
		return _highlight;
	}

	public Map<String, Float> getIndexBoosts() {
		return Collections.unmodifiableMap(_indexBoosts);
	}

	public String[] getIndexNames() {
		return _indexNames;
	}

	public Float getMinimumScore() {
		return _minimumScore;
	}

	public Map<String, PipelineAggregation> getPipelineAggregationsMap() {
		return Collections.unmodifiableMap(_pipelineAggregationsMap);
	}

	public PointInTime getPointInTime() {
		return _pointInTime;
	}

	public Filter getPostFilter() {
		return _postFilter;
	}

	public List<ComplexQueryPart> getPostFilterComplexQueryParts() {
		return Collections.unmodifiableList(_postFilterComplexQueryParts);
	}

	public Query getPostFilterQuery() {
		return _postFilterQuery;
	}

	public Query getQuery() {
		return _query;
	}

	public com.liferay.portal.kernel.search.Query getQuery71() {
		return _legacyQuery;
	}

	public Boolean getRequestCache() {
		return _requestCache;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getRescores()}
	 */
	@Deprecated
	public Query getRescoreQuery() {
		return _rescoreQuery;
	}

	public List<Rescore> getRescores() {
		return _rescores;
	}

	public List<StatsRequest> getStatsRequests() {
		return Collections.unmodifiableList(_statsRequests);
	}

	public Long getTimeoutInMilliseconds() {
		return _timeoutInMilliseconds;
	}

	public Boolean getTrackTotalHits() {
		return _trackTotalHits;
	}

	public Integer getTrackTotalHitsLimit() {
		return _trackTotalHitsLimit;
	}

	public String[] getTypes() {
		return _types;
	}

	public boolean isBasicFacetSelection() {
		return _basicFacetSelection;
	}

	public boolean isExplain() {
		if (_explain != null) {
			return _explain;
		}

		return false;
	}

	public boolean isIncludeResponseString() {
		return _includeResponseString;
	}

	public boolean isRequestCache() {
		if (_requestCache != null) {
			return _requestCache;
		}

		return false;
	}

	public boolean isTrackTotalHits() {
		if (_trackTotalHits != null) {
			return _trackTotalHits;
		}

		return false;
	}

	public void putAllFacets(Map<String, Facet> facets) {
		_facets.putAll(facets);
	}

	public void putFacet(String fieldName, Facet facet) {
		_facets.put(fieldName, facet);
	}

	public void setBasicFacetSelection(boolean basicFacetSelection) {
		_basicFacetSelection = basicFacetSelection;
	}

	public void setExplain(Boolean explain) {
		_explain = explain;
	}

	public void setHighlight(Highlight highlight) {
		_highlight = highlight;
	}

	public void setIncludeResponseString(boolean includeResponseString) {
		_includeResponseString = includeResponseString;
	}

	public void setIndexNames(String... indexNames) {
		_indexNames = indexNames;
	}

	public void setMinimumScore(Float minimumScore) {
		_minimumScore = minimumScore;
	}

	public void setPointInTime(PointInTime pointInTime) {
		_pointInTime = pointInTime;
	}

	public void setPostFilter(Filter postFilter) {
		_postFilter = postFilter;
	}

	public void setPostFilterQuery(Query postFilterQuery) {
		_postFilterQuery = postFilterQuery;
	}

	public void setQuery(com.liferay.portal.kernel.search.Query legacyQuery) {
		_legacyQuery = legacyQuery;
	}

	public void setQuery(Query query) {
		_query = query;
	}

	public void setRequestCache(Boolean requestCache) {
		_requestCache = requestCache;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #setRescores(List)}
	 */
	@Deprecated
	public void setRescoreQuery(Query rescoreQuery) {
		_rescoreQuery = rescoreQuery;
	}

	public void setRescores(List<Rescore> rescores) {
		_rescores = rescores;
	}

	public void setStatsRequests(Collection<StatsRequest> statsRequests) {
		_statsRequests = new ArrayList<>(statsRequests);
	}

	public void setTimeoutInMilliseconds(Long timeoutInMilliseconds) {
		_timeoutInMilliseconds = timeoutInMilliseconds;
	}

	public void setTrackTotalHits(Boolean trackTotalHits) {
		_trackTotalHits = trackTotalHits;
	}

	public void setTrackTotalHitsLimit(Integer trackTotalHitsLimit) {
		_trackTotalHitsLimit = trackTotalHitsLimit;
	}

	public void setTypes(String... types) {
		_types = types;
	}

	private final Map<String, Aggregation> _aggregationsMap =
		new LinkedHashMap<>();
	private boolean _basicFacetSelection;
	private final List<ComplexQueryPart> _complexQueryParts = new ArrayList<>();
	private Boolean _explain;
	private final Map<String, Facet> _facets = new LinkedHashMap<>();
	private Highlight _highlight;
	private boolean _includeResponseString;
	private final Map<String, Float> _indexBoosts = new LinkedHashMap<>();
	private String[] _indexNames;
	private com.liferay.portal.kernel.search.Query _legacyQuery;
	private Float _minimumScore;
	private final Map<String, PipelineAggregation> _pipelineAggregationsMap =
		new LinkedHashMap<>();
	private PointInTime _pointInTime;
	private Filter _postFilter;
	private final List<ComplexQueryPart> _postFilterComplexQueryParts =
		new ArrayList<>();
	private Query _postFilterQuery;
	private Query _query;
	private Boolean _requestCache;
	private Query _rescoreQuery;
	private List<Rescore> _rescores;
	private List<StatsRequest> _statsRequests = Collections.emptyList();
	private Long _timeoutInMilliseconds;
	private Boolean _trackTotalHits;
	private Integer _trackTotalHitsLimit;
	private String[] _types;

}