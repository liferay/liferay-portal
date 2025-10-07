/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.GroupBy;
import com.liferay.portal.kernel.search.Stats;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.collapse.Collapse;
import com.liferay.portal.search.collapse.InnerCollapse;
import com.liferay.portal.search.elasticsearch7.internal.groupby.GroupByTranslator;
import com.liferay.portal.search.elasticsearch7.internal.highlight.HighlightTranslator;
import com.liferay.portal.search.elasticsearch7.internal.highlight.HighlighterTranslator;
import com.liferay.portal.search.elasticsearch7.internal.legacy.sort.SortTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.sort.ElasticsearchSortFieldTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.StatsTranslator;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.groupby.GroupByRequest;
import com.liferay.portal.search.legacy.groupby.GroupByRequestFactory;
import com.liferay.portal.search.legacy.stats.StatsRequestBuilderFactory;
import com.liferay.portal.search.query.QueryTranslator;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortFieldTranslator;
import com.liferay.portal.search.stats.StatsRequest;
import com.liferay.portal.search.stats.StatsRequestBuilder;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = SearchSearchRequestAssembler.class)
public class SearchSearchRequestAssemblerImpl
	implements SearchSearchRequestAssembler {

	@Override
	public void assemble(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest, SearchRequest searchRequest) {

		_commonSearchSourceBuilderAssembler.assemble(
			searchSourceBuilder, searchSearchRequest, searchRequest);

		_setCollapse(searchSourceBuilder, searchSearchRequest);
		_setFetchFields(searchSourceBuilder, searchSearchRequest);
		_setFetchSource(searchSourceBuilder, searchSearchRequest);
		_setGroupBy(searchSourceBuilder, searchSearchRequest);
		_setGroupByRequests(searchSourceBuilder, searchSearchRequest);
		_setHighlighter(searchSourceBuilder, searchSearchRequest);
		_setPagination(searchSourceBuilder, searchSearchRequest);
		_setPreference(searchRequest, searchSearchRequest);
		_setScroll(searchRequest, searchSearchRequest);
		_setSearchAfter(searchSourceBuilder, searchSearchRequest);
		_setSorts(searchSourceBuilder, searchSearchRequest);
		_setStats(searchSourceBuilder, searchSearchRequest);
		_setStoredFields(searchSourceBuilder, searchSearchRequest);
		_setTrackScores(searchSourceBuilder, searchSearchRequest);
		_setVersion(searchSourceBuilder, searchSearchRequest);

		searchRequest.source(searchSourceBuilder);
	}

	protected GroupByRequest translate(GroupBy groupBy) {
		return _groupByRequestFactory.getGroupByRequest(groupBy);
	}

	protected StatsRequest translate(Stats stats) {
		StatsRequestBuilder statsRequestBuilder =
			_statsRequestBuilderFactory.getStatsRequestBuilder(stats);

		return statsRequestBuilder.build();
	}

	private void _setCollapse(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		Collapse collapse = searchSearchRequest.getCollapse();

		if ((collapse == null) || (collapse.getField() == null)) {
			return;
		}

		CollapseBuilder collapseBuilder = new CollapseBuilder(
			collapse.getField());

		ListUtil.isNotEmptyForEach(
			collapse.getInnerHits(),
			innerHit -> {
				InnerHitBuilder innerHitBuilder = new InnerHitBuilder(
					innerHit.getName());

				InnerCollapse innerCollapse = innerHit.getInnerCollapse();

				if (innerCollapse != null) {
					innerHitBuilder.setInnerCollapse(
						new CollapseBuilder(innerCollapse.getField()));
				}

				innerHitBuilder.setSize(innerHit.getSize());

				if (ListUtil.isNotEmpty(innerHit.getSorts())) {
					for (Sort sort : innerHit.getSorts()) {
						innerHitBuilder.addSort(
							_sortFieldTranslator.translate(sort));
					}
				}

				collapseBuilder.setInnerHits(innerHitBuilder);
			});

		if (collapse.getMaxConcurrentGroupRequests() != null) {
			collapseBuilder.setMaxConcurrentGroupRequests(
				collapse.getMaxConcurrentGroupRequests());
		}

		searchSourceBuilder.collapse(collapseBuilder);
	}

	private void _setFetchFields(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		String[] selectedFieldNames =
			searchSearchRequest.getSelectedFieldNames();

		if (ArrayUtil.isNotEmpty(selectedFieldNames)) {
			for (String selectedFieldName : selectedFieldNames) {
				searchSourceBuilder.fetchField(selectedFieldName);
			}
		}
		else {
			searchSourceBuilder.fetchField(StringPool.STAR);
		}
	}

	private void _setFetchSource(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		if ((searchSearchRequest.getFetchSource() != null) ||
			(searchSearchRequest.getFetchSourceExcludes() != null) ||
			(searchSearchRequest.getFetchSourceIncludes() != null)) {

			if (searchSearchRequest.getFetchSource() == null) {
				searchSourceBuilder.fetchSource(true);
			}
			else {
				searchSourceBuilder.fetchSource(
					searchSearchRequest.getFetchSource());
			}

			searchSourceBuilder.fetchSource(
				searchSearchRequest.getFetchSourceIncludes(),
				searchSearchRequest.getFetchSourceExcludes());
		}
		else {
			searchSourceBuilder.fetchSource(false);
		}
	}

	private void _setGroupBy(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		if (searchSearchRequest.getGroupBy() != null) {
			_groupByTranslator.translate(
				searchSourceBuilder,
				translate(searchSearchRequest.getGroupBy()),
				searchSearchRequest.getLocale(),
				searchSearchRequest.getSelectedFieldNames(),
				searchSearchRequest.getHighlightFieldNames(),
				searchSearchRequest.isHighlightEnabled(),
				searchSearchRequest.isHighlightRequireFieldMatch(),
				searchSearchRequest.getHighlightFragmentSize(),
				searchSearchRequest.getHighlightSnippetSize());
		}
	}

	private void _setGroupByRequests(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		List<GroupByRequest> groupByRequests =
			searchSearchRequest.getGroupByRequests();

		if (ListUtil.isNotEmpty(groupByRequests)) {
			groupByRequests.forEach(
				groupByRequest -> _groupByTranslator.translate(
					searchSourceBuilder, groupByRequest,
					searchSearchRequest.getLocale(),
					searchSearchRequest.getSelectedFieldNames(),
					searchSearchRequest.getHighlightFieldNames(),
					searchSearchRequest.isHighlightEnabled(),
					searchSearchRequest.isHighlightRequireFieldMatch(),
					searchSearchRequest.getHighlightFragmentSize(),
					searchSearchRequest.getHighlightSnippetSize()));
		}
	}

	private void _setHighlighter(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		if (searchSearchRequest.getHighlight() != null) {
			searchSourceBuilder.highlighter(
				_highlightTranslator.translate(
					searchSearchRequest.getHighlight(), _queryTranslator));
		}
		else if (searchSearchRequest.isHighlightEnabled()) {
			_highlighterTranslator.translate(
				searchSourceBuilder,
				searchSearchRequest.getHighlightFieldNames(),
				searchSearchRequest.isHighlightRequireFieldMatch(),
				searchSearchRequest.getHighlightFragmentSize(),
				searchSearchRequest.getHighlightSnippetSize(),
				searchSearchRequest.isLuceneSyntax());
		}
	}

	private void _setPagination(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		if (searchSearchRequest.getStart() != null) {
			searchSourceBuilder.from(searchSearchRequest.getStart());
		}

		if (searchSearchRequest.getSize() != null) {
			searchSourceBuilder.size(searchSearchRequest.getSize());
		}
	}

	private void _setPreference(
		SearchRequest searchRequest, SearchSearchRequest searchSearchRequest) {

		String preference = searchSearchRequest.getPreference();

		if (!Validator.isBlank(preference)) {
			searchRequest.preference(preference);
		}
	}

	private void _setScroll(
		SearchRequest searchRequest, SearchSearchRequest searchSearchRequest) {

		long scrollKeepAliveMinutes =
			searchSearchRequest.getScrollKeepAliveMinutes();

		if (scrollKeepAliveMinutes > 0) {
			searchRequest.scroll(
				TimeValue.timeValueMinutes(scrollKeepAliveMinutes));
		}
	}

	private void _setSearchAfter(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		if (ArrayUtil.isNotEmpty(searchSearchRequest.getSearchAfter())) {
			searchSourceBuilder.searchAfter(
				searchSearchRequest.getSearchAfter());
		}
	}

	private void _setSorts(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		for (Sort sort : searchSearchRequest.getSorts()) {
			searchSourceBuilder.sort(_sortFieldTranslator.translate(sort));
		}

		_sortTranslator.translate(
			searchSourceBuilder, searchSearchRequest.getSorts71());
	}

	private void _setStats(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		Map<String, Stats> statsMap = searchSearchRequest.getStats();

		if (MapUtil.isNotEmpty(statsMap)) {
			statsMap.forEach(
				(key, stats) -> _statsTranslator.populateRequest(
					searchSourceBuilder, translate(stats)));
		}
	}

	private void _setStoredFields(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		String[] storedFields = searchSearchRequest.getStoredFields();

		if (ArrayUtil.isEmpty(storedFields)) {
			return;
		}

		searchSourceBuilder.storedFields(ListUtil.fromArray(storedFields));
	}

	private void _setTrackScores(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		if (searchSearchRequest.getScoreEnabled() != null) {
			searchSourceBuilder.trackScores(
				searchSearchRequest.getScoreEnabled());
		}
	}

	private void _setVersion(
		SearchSourceBuilder searchSourceBuilder,
		SearchSearchRequest searchSearchRequest) {

		if (searchSearchRequest.getVersion() != null) {
			searchSourceBuilder.version(searchSearchRequest.getVersion());
		}
	}

	@Reference
	private CommonSearchSourceBuilderAssembler
		_commonSearchSourceBuilderAssembler;

	@Reference
	private GroupByRequestFactory _groupByRequestFactory;

	private final GroupByTranslator _groupByTranslator =
		new GroupByTranslator();
	private final HighlighterTranslator _highlighterTranslator =
		new HighlighterTranslator();
	private final HighlightTranslator _highlightTranslator =
		new HighlightTranslator();
	private final QueryTranslator<QueryBuilder> _queryTranslator =
		new ElasticsearchQueryTranslator();
	private final SortFieldTranslator<SortBuilder<?>> _sortFieldTranslator =
		new ElasticsearchSortFieldTranslator();
	private final SortTranslator _sortTranslator = new SortTranslator();

	@Reference
	private StatsRequestBuilderFactory _statsRequestBuilderFactory;

	@Reference
	private StatsTranslator _statsTranslator;

}