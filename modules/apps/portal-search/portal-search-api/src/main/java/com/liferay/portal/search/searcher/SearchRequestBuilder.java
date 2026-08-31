/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.searcher;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.collapse.Collapse;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.groupby.GroupByRequest;
import com.liferay.portal.search.highlight.Highlight;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.rescore.Rescore;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.stats.StatsRequest;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Builds a search request that can be used for executing a search.
 *
 * @author André de Oliveira
 */
@ProviderType
public interface SearchRequestBuilder {

	public SearchRequestBuilder addAggregation(Aggregation aggregation);

	public SearchRequestBuilder addComplexQueryPart(
		ComplexQueryPart complexQueryPart);

	public SearchRequestBuilder addFederatedSearchRequest(
		SearchRequest searchRequest);

	public SearchRequestBuilder addIndex(String index);

	public SearchRequestBuilder addPipelineAggregation(
		PipelineAggregation pipelineAggregation);

	public SearchRequestBuilder addPostFilterQueryPart(
		ComplexQueryPart complexQueryPart);

	public SearchRequestBuilder addRescore(Rescore rescore);

	/**
	 * Adds fields to include in the search results as a map of keys and values.
	 *
	 * @param  selectedFieldNames the names of the fields to return
	 * @return the search request builder
	 */
	public SearchRequestBuilder addSelectedFieldNames(
		String... selectedFieldNames);

	public SearchRequestBuilder addSort(Sort sort);

	public SearchRequestBuilder basicFacetSelection(
		boolean basicFacetSelection);

	/**
	 * Builds the search request.
	 *
	 * @return the search request
	 */
	public SearchRequest build();

	public SearchRequestBuilder collapse(Collapse collapse);

	public SearchRequestBuilder companyId(Long companyId);

	public SearchRequestBuilder connectionId(String connectionId);

	public SearchRequestBuilder emptySearchEnabled(boolean emptySearchEnabled);

	public SearchRequestBuilder entryClassNames(String... entryClassNames);

	public SearchRequestBuilder excludeContributors(String... ids);

	/**
	 * Enables explanation of how each hit's score was computed.
	 *
	 * @param  explain whether to explain scores
	 * @return the search request builder
	 */
	public SearchRequestBuilder explain(boolean explain);

	public SearchRequestBuilder federatedSearchKey(String federatedSearchKey);

	public SearchRequestBuilder fetchSource(boolean fetchSource);

	public SearchRequestBuilder fetchSourceExcludes(
		String[] fetchSourceExcludes);

	public SearchRequestBuilder fetchSourceIncludes(
		String[] fetchSourceIncludes);

	public SearchRequestBuilder fields(String... fields);

	public SearchRequestBuilder from(Integer from);

	public SearchRequestBuilder getFederatedSearchRequestBuilder(
		String federatedSearchKey);

	/**
	 * Provides a top hits aggregations for each of the specified fields.
	 *
	 * @param  groupByRequests the grouping that is enabled for each field
	 * @return the search request builder
	 * @review
	 */
	public SearchRequestBuilder groupByRequests(
		GroupByRequest... groupByRequests);

	public SearchRequestBuilder groupIds(long... groupIds);

	public SearchRequestBuilder highlight(Highlight highlight);

	public SearchRequestBuilder highlightEnabled(boolean highlightEnabled);

	public SearchRequestBuilder highlightFields(String... highlightFields);

	public SearchRequestBuilder includeContributors(String... ids);

	/**
	 * Enables inclusion of the search engine's response string with the
	 * returned results.
	 *
	 * @param  includeResponseString whether to include the response string
	 * @return the search request builder
	 */
	public SearchRequestBuilder includeResponseString(
		boolean includeResponseString);

	public SearchRequestBuilder indexes(String... indexes);

	public SearchRequestBuilder locale(Locale locale);

	public SearchRequestBuilder modelIndexerClasses(Class<?>... classes);

	public SearchRequestBuilder modelIndexerClassNames(String... classNames);

	public SearchRequestBuilder ownerUserId(Long userId);

	public void paginationStartParameterName(
		String paginationStartParameterName);

	public SearchRequestBuilder postFilterQuery(Query query);

	public SearchRequestBuilder query(Query query);

	public SearchRequestBuilder queryString(String queryString);

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #rescores(List)}
	 */
	@Deprecated
	public SearchRequestBuilder rescoreQuery(Query rescoreQuery);

	/**
	 * Provides secondary queries to reorder the top documents returned.
	 *
	 * @param  rescores the rescore queries
	 * @return the search request builder
	 */
	public SearchRequestBuilder rescores(List<Rescore> rescores);

	public SearchRequestBuilder retainFacetSelections(
		boolean retainFacetSelections);

	public SearchRequestBuilder size(Integer size);

	public SearchRequestBuilder sorts(Sort... sorts);

	/**
	 * Provides the metric aggregations to be computed for each field.
	 *
	 * @param  statsRequests the stats that are enabled for each field
	 * @return the search request builder
	 */
	public SearchRequestBuilder statsRequests(StatsRequest... statsRequests);

	public SearchRequestBuilder storedFields(String... storedFields);

	public SearchRequestBuilder trackTotalHitsLimit(
		Integer trackTotalHitsLimit);

	public SearchRequestBuilder withFacetContext(
		Consumer<FacetContext> facetContextConsumer);

	public <T> T withFacetContextGet(
		Function<FacetContext, T> facetContextFunction);

	public SearchRequestBuilder withSearchContext(
		Consumer<SearchContext> searchContextConsumer);

	public <T> T withSearchContextGet(
		Function<SearchContext, T> searchContextFunction);

	public SearchRequestBuilder withSearchRequestBuilder(
		Consumer<SearchRequestBuilder>... searchRequestBuilderConsumers);

}