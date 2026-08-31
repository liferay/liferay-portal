/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.searcher;

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
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Holds the parameters used when performing a search. Build the search request
 * with the {@link SearchRequestBuilder}.
 *
 * @author André de Oliveira
 */
@ProviderType
public interface SearchRequest {

	public Map<String, Aggregation> getAggregationsMap();

	public Collapse getCollapse();

	public List<ComplexQueryPart> getComplexQueryParts();

	public String getConnectionId();

	public List<String> getEntryClassNames();

	public List<String> getExcludeContributors();

	public String getFederatedSearchKey();

	public List<SearchRequest> getFederatedSearchRequests();

	public Boolean getFetchSource();

	public String[] getFetchSourceExcludes();

	public String[] getFetchSourceIncludes();

	public Integer getFrom();

	/**
	 * Provides the top hits aggregations used for grouping results by the
	 * specified fields.
	 *
	 * @return the GroupByRequests that are enabled for the search.
	 * @review
	 */
	public List<GroupByRequest> getGroupByRequests();

	public Highlight getHighlight();

	public List<String> getIncludeContributors();

	public List<String> getIndexes();

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getModelIndexerClassNames()}
	 */
	@Deprecated
	public List<Class<?>> getModelIndexerClasses();

	public List<String> getModelIndexerClassNames();

	public String getPaginationStartParameterName();

	public Map<String, PipelineAggregation> getPipelineAggregationsMap();

	public List<ComplexQueryPart> getPostFilterComplexQueryParts();

	public Query getPostFilterQuery();

	public Query getQuery();

	public String getQueryString();

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #getRescores()}
	 */
	@Deprecated
	public Query getRescoreQuery();

	/**
	 * Provides secondary queries to reorder the top documents returned.
	 *
	 * @return the rescore queries
	 */
	public List<Rescore> getRescores();

	public Integer getSize();

	public List<Sort> getSorts();

	/**
	 * Provides the metric aggregations to be computed for each field.
	 *
	 * @return the stats that are enabled for each field
	 */
	public List<StatsRequest> getStatsRequests();

	public String[] getStoredFields();

	public Integer getTrackTotalHitsLimit();

	public boolean isBasicFacetSelection();

	public boolean isEmptySearchEnabled();

	/**
	 * Returns <code>true</code> if the explanation for how each hit's score is
	 * computed.
	 *
	 * @return <code>true</code> if the scores are explained; <code>false</code>
	 *         otherwise
	 */
	public boolean isExplain();

	/**
	 * Returns <code>true</code> if the search engine's response string is
	 * included with the returned results.
	 *
	 * @return <code>true</code> if the response string is included;
	 *         <code>false</code> otherwise
	 */
	public boolean isIncludeResponseString();

	public boolean isRetainFacetSelections();

}