/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.searcher;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.groupby.GroupByResponse;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.hits.TotalHitsRelation;
import com.liferay.portal.search.stats.StatsResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Contains the full response of an executed search, as returned from the search
 * engine. The exact response format and the contents of the response depends on
 * the search engine and the search request that was executed.
 *
 * @author André de Oliveira
 */
@ProviderType
public interface SearchResponse {

	public AggregationResult getAggregationResult(String name);

	public Map<String, AggregationResult> getAggregationResultsMap();

	public long getCount();

	public List<Document> getDocuments();

	public List<com.liferay.portal.kernel.search.Document> getDocuments71();

	public String getFederatedSearchKey();

	public SearchResponse getFederatedSearchResponse(String key);

	public Collection<SearchResponse> getFederatedSearchResponses();

	/**
	 * Returns the map containing the top hits aggregations for each field.
	 *
	 * @return the map containing the top hits aggregations
	 * @review
	 */
	public List<GroupByResponse> getGroupByResponses();

	public SearchRequest getRequest();

	/**
	 * Returns the request string that was submitted to the search engine. The
	 * format of the string is dependent on the search engine.
	 *
	 * @return the full request string, as returned by the search engine
	 */
	public String getRequestString();

	/**
	 * Returns the response string as returned by the search engine. This can be
	 * large depending on the number of results; it must be enabled with {@link
	 * SearchRequest#isIncludeResponseString()}.
	 *
	 * @return the response string as returned by the search engine, or blank if
	 *         disabled
	 */
	public String getResponseString();

	public SearchHits getSearchHits();

	public SearchTimeValue getSearchTimeValue();

	/**
	 * Returns the map containing the metrics aggregations computed by the
	 * search engine.
	 *
	 * @return the map containing the metrics aggregations per field
	 */
	public Map<String, StatsResponse> getStatsResponseMap();

	public int getTotalHits();

	/**
	 * Returns how {@link #getTotalHits()} relates to the actual number of
	 * matching documents. {@link TotalHitsRelation#GTE} means the search
	 * engine stopped counting at a limit, so the total is a lower bound.
	 *
	 * @return the total hits relation
	 */
	public TotalHitsRelation getTotalHitsRelation();

	public void withFacetContext(Consumer<FacetContext> facetContextConsumer);

	public <T> T withFacetContextGet(
		Function<FacetContext, T> facetContextFunction);

	public void withHits(Consumer<Hits> hitsConsumer);

	public <T> T withHitsGet(Function<Hits, T> hitsFunction);

	public void withSearchContext(
		Consumer<SearchContext> searchContextConsumer);

	public <T> T withSearchContextGet(
		Function<SearchContext, T> searchContextFunction);

}