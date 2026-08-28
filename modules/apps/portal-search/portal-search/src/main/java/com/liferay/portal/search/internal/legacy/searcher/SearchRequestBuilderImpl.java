/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.legacy.searcher;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.collapse.Collapse;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.groupby.GroupByRequest;
import com.liferay.portal.search.highlight.Highlight;
import com.liferay.portal.search.internal.searcher.SearchRequestImpl;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.rescore.Rescore;
import com.liferay.portal.search.searcher.FacetContext;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.stats.StatsRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author André de Oliveira
 */
public class SearchRequestBuilderImpl implements SearchRequestBuilder {

	public SearchRequestBuilderImpl(
		SearchRequestBuilderFactory searchRequestBuilderFactory) {

		this(searchRequestBuilderFactory, new SearchContext());
	}

	public SearchRequestBuilderImpl(
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		SearchContext searchContext) {

		_searchRequestBuilderFactory = searchRequestBuilderFactory;
		_searchContext = searchContext;

		_facetContext = new FacetContextImpl(searchContext);
	}

	public SearchRequestBuilderImpl(
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		SearchRequest searchRequest) {

		this(
			searchRequestBuilderFactory,
			new SearchRequestImpl(
				(SearchRequestImpl)searchRequest
			).getSearchContext());
	}

	@Override
	public SearchRequestBuilder addAggregation(Aggregation aggregation) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.addAggregation(aggregation));

		return this;
	}

	@Override
	public SearchRequestBuilder addComplexQueryPart(
		ComplexQueryPart complexQueryPart) {

		if (complexQueryPart != null) {
			_withSearchRequestImpl(
				searchRequestImpl -> searchRequestImpl.addComplexQueryPart(
					complexQueryPart));
		}

		return this;
	}

	@Override
	public SearchRequestBuilder addFederatedSearchRequest(
		SearchRequest searchRequest) {

		_addFederatedSearchRequests(Arrays.asList(searchRequest));

		return this;
	}

	@Override
	public SearchRequestBuilder addIndex(String index) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.addIndex(index));

		return this;
	}

	@Override
	public SearchRequestBuilder addPipelineAggregation(
		PipelineAggregation pipelineAggregation) {

		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.addPipelineAggregation(
				pipelineAggregation));

		return this;
	}

	@Override
	public SearchRequestBuilder addPostFilterQueryPart(
		ComplexQueryPart complexQueryPart) {

		if (complexQueryPart != null) {
			_withSearchRequestImpl(
				searchRequestImpl -> searchRequestImpl.addPostFilterQueryPart(
					complexQueryPart));
		}

		return this;
	}

	@Override
	public SearchRequestBuilder addRescore(Rescore rescore) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.addRescore(rescore));

		return this;
	}

	@Override
	public SearchRequestBuilder addSelectedFieldNames(
		String... selectedFieldNames) {

		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.addSelectedFieldNames(
				selectedFieldNames));

		return this;
	}

	@Override
	public SearchRequestBuilder addSort(Sort sort) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.addSort(sort));

		return this;
	}

	@Override
	public SearchRequestBuilder basicFacetSelection(
		boolean basicFacetSelection) {

		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setBasicFacetSelection(
				basicFacetSelection));

		return this;
	}

	@Override
	public SearchRequest build() {
		basicFacetSelection(
			SearchRequestImpl.isBasicFacetSelection(_searchContext));

		_addFederatedSearchRequests(_buildFederatedSearchRequests());

		return _withSearchRequestGet(Function.identity());
	}

	@Override
	public SearchRequestBuilder collapse(Collapse collapse) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setCollapse(collapse));

		return this;
	}

	@Override
	public SearchRequestBuilder companyId(Long companyId) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setCompanyId(companyId));

		return this;
	}

	@Override
	public SearchRequestBuilder connectionId(String connectionId) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setConnectionId(
				connectionId));

		return this;
	}

	@Override
	public SearchRequestBuilder emptySearchEnabled(boolean allowEmptySearches) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setEmptySearchEnabled(
				allowEmptySearches));

		return this;
	}

	@Override
	public SearchRequestBuilder entryClassNames(String... entryClassNames) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.addEntryClassNames(
				entryClassNames));

		return this;
	}

	@Override
	public SearchRequestBuilder excludeContributors(String... ids) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.addExcludeContributors(ids));

		return this;
	}

	@Override
	public SearchRequestBuilder explain(boolean explain) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setExplain(explain));

		return this;
	}

	@Override
	public SearchRequestBuilder federatedSearchKey(String federatedSearchKey) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setFederatedSearchKey(
				federatedSearchKey));

		return this;
	}

	@Override
	public SearchRequestBuilder fetchSource(boolean fetchSource) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setFetchSource(fetchSource));

		return this;
	}

	@Override
	public SearchRequestBuilder fetchSourceExcludes(
		String[] fetchSourceExcludes) {

		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setFetchSourceExcludes(
				fetchSourceExcludes));

		return this;
	}

	@Override
	public SearchRequestBuilder fetchSourceIncludes(
		String[] fetchSourceIncludes) {

		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setFetchSourceIncludes(
				fetchSourceIncludes));

		return this;
	}

	@Override
	public SearchRequestBuilder fields(String... fields) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setSelectedFieldNames(
				fields));

		return this;
	}

	@Override
	public SearchRequestBuilder from(Integer from) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setFrom(from));

		return this;
	}

	@Override
	public SearchRequestBuilder getFederatedSearchRequestBuilder(
		String federatedSearchKey) {

		if (Validator.isBlank(federatedSearchKey)) {
			return this;
		}

		return _federatedSearchRequestBuildersMap.computeIfAbsent(
			federatedSearchKey, this::_newFederatedSearchRequestBuilder);
	}

	@Override
	public SearchRequestBuilder groupByRequests(
		GroupByRequest... groupByRequests) {

		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setGroupByRequests(
				groupByRequests));

		return this;
	}

	@Override
	public SearchRequestBuilder groupIds(long... groupIds) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setGroupIds(groupIds));

		return this;
	}

	@Override
	public SearchRequestBuilder highlight(Highlight highlight) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setHighlight(highlight));

		return this;
	}

	@Override
	public SearchRequestBuilder highlightEnabled(boolean highlightEnabled) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setHighlightEnabled(
				highlightEnabled));

		return this;
	}

	@Override
	public SearchRequestBuilder highlightFields(String... highlightFields) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setHighlightFields(
				highlightFields));

		return this;
	}

	@Override
	public SearchRequestBuilder includeContributors(String... ids) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.addIncludeContributors(ids));

		return this;
	}

	@Override
	public SearchRequestBuilder includeResponseString(
		boolean includeResponseString) {

		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setIncludeResponseString(
				includeResponseString));

		return this;
	}

	@Override
	public SearchRequestBuilder indexes(String... indexes) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setIndexes(indexes));

		return this;
	}

	@Override
	public SearchRequestBuilder locale(Locale locale) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setLocale(locale));

		return this;
	}

	@Override
	public SearchRequestBuilder modelIndexerClasses(Class<?>... classes) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setModelIndexerClassNames(
				TransformUtil.transform(
					classes, Class::getCanonicalName, String.class)));

		return this;
	}

	@Override
	public SearchRequestBuilder modelIndexerClassNames(String... classNames) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setModelIndexerClassNames(
				classNames));

		return this;
	}

	@Override
	public SearchRequestBuilder ownerUserId(Long userId) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setOwnerUserId(userId));

		return this;
	}

	@Override
	public void paginationStartParameterName(
		String paginationStartParameterName) {

		_withSearchRequestImpl(
			searchRequestImpl ->
				searchRequestImpl.setPaginationStartParameterName(
					paginationStartParameterName));
	}

	@Override
	public SearchRequestBuilder postFilterQuery(Query query) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setPostFilterQuery(query));

		return this;
	}

	@Override
	public SearchRequestBuilder query(Query query) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setQuery(query));

		return this;
	}

	@Override
	public SearchRequestBuilder queryString(String queryString) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setQueryString(queryString));

		return this;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #rescores(List)}
	 */
	@Deprecated
	@Override
	public SearchRequestBuilder rescoreQuery(Query query) {
		return this;
	}

	@Override
	public SearchRequestBuilder rescores(List<Rescore> rescores) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setRescores(rescores));

		return this;
	}

	@Override
	public SearchRequestBuilder retainFacetSelections(
		boolean retainFacetSelections) {

		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setRetainFacetSelections(
				retainFacetSelections));

		return this;
	}

	@Override
	public SearchRequestBuilder size(Integer size) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setSize(size));

		return this;
	}

	@Override
	public SearchRequestBuilder sorts(Sort... sorts) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setSorts(sorts));

		return this;
	}

	@Override
	public SearchRequestBuilder statsRequests(StatsRequest... statsRequests) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setStatsRequests(
				statsRequests));

		return this;
	}

	@Override
	public SearchRequestBuilder storedFields(String... storedFields) {
		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setStoredFields(
				storedFields));

		return this;
	}

	@Override
	public SearchRequestBuilder trackTotalHitsLimit(
		Integer trackTotalHitsLimit) {

		_withSearchRequestImpl(
			searchRequestImpl -> searchRequestImpl.setTrackTotalHitsLimit(
				trackTotalHitsLimit));

		return this;
	}

	@Override
	public SearchRequestBuilder withFacetContext(
		Consumer<FacetContext> facetContextConsumer) {

		facetContextConsumer.accept(_facetContext);

		return this;
	}

	@Override
	public <T> T withFacetContextGet(
		Function<FacetContext, T> facetContextFunction) {

		return facetContextFunction.apply(_facetContext);
	}

	@Override
	public SearchRequestBuilder withSearchContext(
		Consumer<SearchContext> searchContextConsumer) {

		searchContextConsumer.accept(_searchContext);

		return this;
	}

	@Override
	public <T> T withSearchContextGet(
		Function<SearchContext, T> searchContextFunction) {

		return searchContextFunction.apply(_searchContext);
	}

	@Override
	public SearchRequestBuilder withSearchRequestBuilder(
		Consumer<SearchRequestBuilder>... searchRequestBuilderConsumers) {

		for (Consumer<SearchRequestBuilder> searchRequestBuilderConsumer :
				searchRequestBuilderConsumers) {

			searchRequestBuilderConsumer.accept(this);
		}

		return this;
	}

	protected static <T extends Serializable> T setAttribute(
		SearchContext searchContext, String key, T value) {

		searchContext.setAttribute(key, value);

		return value;
	}

	private void _addFederatedSearchRequests(
		List<SearchRequest> searchRequests) {

		_withSearchRequestImpl(
			searchRequestImpl -> searchRequests.forEach(
				searchRequestImpl::addFederatedSearchRequest));
	}

	private List<SearchRequest> _buildFederatedSearchRequests() {
		Collection<SearchRequestBuilder> searchRequestBuilders =
			_federatedSearchRequestBuildersMap.values();

		List<SearchRequest> searchRequests = new ArrayList<>(
			searchRequestBuilders.size());

		for (SearchRequestBuilder searchRequestBuilder :
				searchRequestBuilders) {

			searchRequests.add(searchRequestBuilder.build());
		}

		return searchRequests;
	}

	private SearchRequestImpl _getSearchRequestImpl(
		SearchContext searchContext) {

		SearchRequestImpl searchRequestImpl =
			(SearchRequestImpl)searchContext.getAttribute(
				_SEARCH_CONTEXT_KEY_SEARCH_REQUEST);

		if (searchRequestImpl != null) {
			return searchRequestImpl;
		}

		return setAttribute(
			searchContext, _SEARCH_CONTEXT_KEY_SEARCH_REQUEST,
			new SearchRequestImpl(searchContext));
	}

	private SearchRequestBuilder _newFederatedSearchRequestBuilder(
		String federatedSearchKey) {

		return _searchRequestBuilderFactory.builder(
		).federatedSearchKey(
			federatedSearchKey
		).withSearchContext(
			searchContext -> {
				searchContext.setCompanyId(_searchContext.getCompanyId());
				searchContext.setLayout(_searchContext.getLayout());
				searchContext.setTimeZone(_searchContext.getTimeZone());
				searchContext.setUserId(_searchContext.getUserId());
			}
		);
	}

	private <T> T _withSearchRequestGet(
		Function<SearchRequest, T> searchRequestFunction) {

		synchronized (_searchContext) {
			return searchRequestFunction.apply(
				_getSearchRequestImpl(_searchContext));
		}
	}

	private void _withSearchRequestImpl(
		Consumer<SearchRequestImpl> searchRequestImplConsumer) {

		synchronized (_searchContext) {
			searchRequestImplConsumer.accept(
				_getSearchRequestImpl(_searchContext));
		}
	}

	private static final String _SEARCH_CONTEXT_KEY_SEARCH_REQUEST =
		"search.request";

	private final FacetContext _facetContext;
	private final Map<String, SearchRequestBuilder>
		_federatedSearchRequestBuildersMap = new LinkedHashMap<>();
	private final SearchContext _searchContext;
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory;

}