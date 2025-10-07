/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.solr8.internal.search.engine.adapter.search;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.GroupBy;
import com.liferay.portal.kernel.search.Stats;
import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.groupby.GroupByRequest;
import com.liferay.portal.search.legacy.groupby.GroupByRequestFactory;
import com.liferay.portal.search.legacy.stats.StatsRequestBuilderFactory;
import com.liferay.portal.search.solr8.internal.groupby.GroupByTranslator;
import com.liferay.portal.search.solr8.internal.sort.SolrSortFieldTranslator;
import com.liferay.portal.search.solr8.internal.stats.StatsTranslator;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.stats.StatsRequestBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(service = SearchSolrQueryAssembler.class)
public class SearchSolrQueryAssemblerImpl implements SearchSolrQueryAssembler {

	@Override
	public void assemble(
		SolrQuery solrQuery, SearchSearchRequest searchSearchRequest) {

		_baseSolrQueryAssembler.assemble(solrQuery, searchSearchRequest);

		setGroupBy(solrQuery, searchSearchRequest);
		setGroupByRequests(solrQuery, searchSearchRequest);
		setHighlights(solrQuery, searchSearchRequest);
		setIncludeScore(solrQuery, searchSearchRequest);
		setPagination(solrQuery, searchSearchRequest);
		setSelectedFields(solrQuery, searchSearchRequest);
		setSorts(solrQuery, searchSearchRequest);
		setStats(solrQuery, searchSearchRequest);
	}

	protected void addHighlightedField(
		SolrQuery solrQuery, Locale locale, String fieldName) {

		solrQuery.addHighlightField(fieldName);

		String localizedFieldName = Field.getLocalizedName(locale, fieldName);

		solrQuery.addHighlightField(localizedFieldName);
	}

	protected SolrQuery.SortClause getFieldSortClause(
		com.liferay.portal.kernel.search.Sort sort, String fieldName) {

		if (sort.isReverse()) {
			return SolrQuery.SortClause.desc(fieldName);
		}

		return SolrQuery.SortClause.asc(fieldName);
	}

	protected SolrQuery.SortClause getScoreSortClause(
		com.liferay.portal.kernel.search.Sort sort) {

		if (sort.isReverse()) {
			return SolrQuery.SortClause.asc("score");
		}

		return SolrQuery.SortClause.desc("score");
	}

	protected SolrQuery.SortClause getSortClause(
		com.liferay.portal.kernel.search.Sort sort, String fieldName) {

		if (fieldName.equals("score")) {
			return getScoreSortClause(sort);
		}

		return getFieldSortClause(sort, fieldName);
	}

	protected String getSortFieldName(
		com.liferay.portal.kernel.search.Sort sort, String scoreFieldName) {

		String sortFieldName = sort.getFieldName();

		if (Objects.equals(sortFieldName, Field.ENTRY_CLASS_NAME) ||
			Objects.equals(sortFieldName, Field.PRIORITY) ||
			Objects.equals(sortFieldName, "score")) {

			return sortFieldName;
		}

		return Field.getSortFieldName(sort, scoreFieldName);
	}

	protected void setGroupBy(
		SolrQuery solrQuery, SearchSearchRequest searchSearchRequest) {

		GroupBy groupBy = searchSearchRequest.getGroupBy();

		if (groupBy != null) {
			_groupByTranslator.translate(
				solrQuery, _groupByRequestFactory.getGroupByRequest(groupBy),
				searchSearchRequest.getLocale(),
				searchSearchRequest.getHighlightFieldNames(),
				searchSearchRequest.isHighlightEnabled(),
				searchSearchRequest.isHighlightRequireFieldMatch(),
				searchSearchRequest.getHighlightFragmentSize(),
				searchSearchRequest.getHighlightSnippetSize());
		}
	}

	protected void setGroupByRequests(
		SolrQuery solrQuery, SearchSearchRequest searchSearchRequest) {

		List<GroupByRequest> groupByRequests =
			searchSearchRequest.getGroupByRequests();

		if (ListUtil.isNotEmpty(groupByRequests)) {
			_groupByTranslator.translate(
				solrQuery, groupByRequests.get(0),
				searchSearchRequest.getLocale(),
				searchSearchRequest.getHighlightFieldNames(),
				searchSearchRequest.isHighlightEnabled(),
				searchSearchRequest.isHighlightRequireFieldMatch(),
				searchSearchRequest.getHighlightFragmentSize(),
				searchSearchRequest.getHighlightSnippetSize());
		}
	}

	protected void setHighlights(
		SolrQuery solrQuery, SearchSearchRequest searchSearchRequest) {

		if (!searchSearchRequest.isHighlightEnabled()) {
			return;
		}

		solrQuery.setHighlight(true);
		solrQuery.setHighlightFragsize(
			searchSearchRequest.getHighlightFragmentSize());
		solrQuery.setHighlightSimplePost(HighlightUtil.HIGHLIGHT_TAG_CLOSE);
		solrQuery.setHighlightSimplePre(HighlightUtil.HIGHLIGHT_TAG_OPEN);
		solrQuery.setHighlightSnippets(
			searchSearchRequest.getHighlightSnippetSize());

		for (String highlightFieldName :
				searchSearchRequest.getHighlightFieldNames()) {

			addHighlightedField(
				solrQuery, searchSearchRequest.getLocale(), highlightFieldName);
		}

		if (!searchSearchRequest.isLuceneSyntax()) {
			solrQuery.setHighlightRequireFieldMatch(
				searchSearchRequest.isHighlightRequireFieldMatch());
		}
	}

	protected void setIncludeScore(
		SolrQuery solrQuery, SearchSearchRequest searchSearchRequest) {

		solrQuery.setIncludeScore(searchSearchRequest.isScoreEnabled());
	}

	protected void setPagination(
		SolrQuery solrQuery, SearchSearchRequest searchSearchRequest) {

		GroupBy groupBy = searchSearchRequest.getGroupBy();

		if ((groupBy != null) ||
			ListUtil.isNotEmpty(searchSearchRequest.getGroupByRequests())) {

			return;
		}

		solrQuery.setRows(searchSearchRequest.getSize());
		solrQuery.setStart(searchSearchRequest.getStart());
	}

	protected void setSelectedFields(
		SolrQuery solrQuery, SearchSearchRequest searchSearchRequest) {

		if (searchSearchRequest.isAllFieldsSelected()) {
			return;
		}

		Set<String> selectedFieldNames = SetUtil.fromArray(
			searchSearchRequest.getSelectedFieldNames());

		if (!selectedFieldNames.contains(Field.UID)) {
			selectedFieldNames.add(Field.UID);
		}

		solrQuery.setFields(selectedFieldNames.toArray(new String[0]));
	}

	protected void setSorts(
		SolrQuery solrQuery, SearchSearchRequest searchSearchRequest) {

		List<Sort> sorts = searchSearchRequest.getSorts();

		for (Sort sort : sorts) {
			solrQuery.addSort(_sortFieldTranslator.translate(sort));
		}

		com.liferay.portal.kernel.search.Sort[] sorts71 =
			searchSearchRequest.getSorts71();

		if (ArrayUtil.isEmpty(sorts71)) {
			return;
		}

		Set<String> sortFieldNames = new HashSet<>();

		for (com.liferay.portal.kernel.search.Sort sort : sorts71) {
			if (sort == null) {
				continue;
			}

			String sortFieldName = getSortFieldName(sort, "score");

			if (sortFieldNames.contains(sortFieldName)) {
				continue;
			}

			sortFieldNames.add(sortFieldName);

			solrQuery.addSort(getSortClause(sort, sortFieldName));
		}
	}

	protected void setStats(
		SolrQuery solrQuery, SearchSearchRequest searchSearchRequest) {

		Map<String, Stats> statsMap = searchSearchRequest.getStats();

		if (MapUtil.isNotEmpty(statsMap)) {
			for (Stats stats : statsMap.values()) {
				StatsRequestBuilder statsRequestBuilder =
					_statsRequestBuilderFactory.getStatsRequestBuilder(stats);

				_statsTranslator.populateRequest(
					solrQuery, statsRequestBuilder.build());
			}
		}
	}

	@Reference
	private BaseSolrQueryAssembler _baseSolrQueryAssembler;

	@Reference
	private GroupByRequestFactory _groupByRequestFactory;

	private final GroupByTranslator _groupByTranslator =
		new GroupByTranslator();
	private final SolrSortFieldTranslator _sortFieldTranslator =
		new SolrSortFieldTranslator();

	@Reference
	private StatsRequestBuilderFactory _statsRequestBuilderFactory;

	@Reference
	private StatsTranslator _statsTranslator;

}