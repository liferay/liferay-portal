/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.search.engine.adapter.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.GroupBy;
import com.liferay.portal.kernel.search.Stats;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.groupby.GroupByRequest;
import com.liferay.portal.search.legacy.groupby.GroupByRequestFactory;
import com.liferay.portal.search.legacy.stats.StatsRequestBuilderFactory;
import com.liferay.portal.search.opensearch2.internal.groupby.GroupByTranslator;
import com.liferay.portal.search.opensearch2.internal.highlight.HighlightTranslator;
import com.liferay.portal.search.opensearch2.internal.legacy.sort.SortTranslator;
import com.liferay.portal.search.opensearch2.internal.stats.StatsTranslator;
import com.liferay.portal.search.opensearch2.internal.util.SetterUtil;
import com.liferay.portal.search.query.QueryTranslator;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortFieldTranslator;
import com.liferay.portal.search.stats.StatsRequest;
import com.liferay.portal.search.stats.StatsRequestBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.Time;
import org.opensearch.client.opensearch._types.TimeUnit;
import org.opensearch.client.opensearch._types.query_dsl.FieldAndFormat;
import org.opensearch.client.opensearch._types.query_dsl.QueryVariant;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.search.SourceConfig;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
@Component(service = SearchSearchRequestAssembler.class)
public class SearchSearchRequestAssemblerImpl
	implements SearchSearchRequestAssembler {

	@Override
	public void assemble(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		_commonSearchRequestBuilderAssembler.assemble(
			searchSearchRequest, searchRequestBuilder);

		_setFetchSource(searchRequestBuilder, searchSearchRequest);
		_setFields(searchRequestBuilder, searchSearchRequest);
		_setGroupBy(searchRequestBuilder, searchSearchRequest);
		_setGroupByRequests(searchRequestBuilder, searchSearchRequest);
		_setHighlighter(searchRequestBuilder, searchSearchRequest);
		_setPagination(searchRequestBuilder, searchSearchRequest);
		_setPreference(searchRequestBuilder, searchSearchRequest);
		_setScroll(searchRequestBuilder, searchSearchRequest);
		_setSearchAfter(searchRequestBuilder, searchSearchRequest);
		_setSorts(searchRequestBuilder, searchSearchRequest);
		_setStats(searchRequestBuilder, searchSearchRequest);
		_setStoredFields(searchRequestBuilder, searchSearchRequest);
		_setTrackScores(searchRequestBuilder, searchSearchRequest);
		_setVersion(searchRequestBuilder, searchSearchRequest);
	}

	protected GroupByRequest translate(GroupBy groupBy) {
		return _groupByRequestFactory.getGroupByRequest(groupBy);
	}

	protected StatsRequest translate(Stats stats) {
		StatsRequestBuilder statsRequestBuilder =
			_statsRequestBuilderFactory.getStatsRequestBuilder(stats);

		return statsRequestBuilder.build();
	}

	private void _setFetchSource(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		if ((searchSearchRequest.getFetchSource() == null) &&
			(searchSearchRequest.getFetchSourceExcludes() == null) &&
			(searchSearchRequest.getFetchSourceIncludes() == null)) {

			searchRequestBuilder.source(
				SourceConfig.of(sourceConfig -> sourceConfig.fetch(false)));

			return;
		}

		SourceConfig.Builder sourceConfigBuilder = new SourceConfig.Builder();

		SetterUtil.setNotNullBoolean(
			sourceConfigBuilder::fetch, searchSearchRequest.getFetchSource());

		sourceConfigBuilder.filter(
			sourceFilter -> sourceFilter.excludes(
				ListUtil.fromArray(searchSearchRequest.getFetchSourceExcludes())
			).includes(
				ListUtil.fromArray(searchSearchRequest.getFetchSourceIncludes())
			));

		searchRequestBuilder.source(sourceConfigBuilder.build());
	}

	private void _setFields(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		String[] selectedFieldNames =
			searchSearchRequest.getSelectedFieldNames();

		if (ArrayUtil.isNotEmpty(selectedFieldNames)) {
			searchRequestBuilder.fields(_toFieldAndFormats(selectedFieldNames));
		}
		else {
			searchRequestBuilder.fields(
				_toFieldAndFormats(new String[] {StringPool.STAR}));
		}
	}

	private void _setGroupBy(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		if (searchSearchRequest.getGroupBy() != null) {
			_groupByTranslator.translate(
				translate(searchSearchRequest.getGroupBy()),
				searchSearchRequest.isHighlightEnabled(),
				searchSearchRequest.getHighlightFieldNames(),
				searchSearchRequest.getHighlightFragmentSize(),
				searchSearchRequest.isHighlightRequireFieldMatch(),
				searchSearchRequest.getHighlightSnippetSize(),
				searchSearchRequest.getLocale(), searchRequestBuilder,
				searchSearchRequest.getSelectedFieldNames());
		}
	}

	private void _setGroupByRequests(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		List<GroupByRequest> groupByRequests =
			searchSearchRequest.getGroupByRequests();

		if (ListUtil.isNotEmpty(groupByRequests)) {
			groupByRequests.forEach(
				groupByRequest -> _groupByTranslator.translate(
					groupByRequest, searchSearchRequest.isHighlightEnabled(),
					searchSearchRequest.getHighlightFieldNames(),
					searchSearchRequest.getHighlightFragmentSize(),
					searchSearchRequest.isHighlightRequireFieldMatch(),
					searchSearchRequest.getHighlightSnippetSize(),
					searchSearchRequest.getLocale(), searchRequestBuilder,
					searchSearchRequest.getSelectedFieldNames()));
		}
	}

	private void _setHighlighter(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		if (searchSearchRequest.getHighlight() != null) {
			searchRequestBuilder.highlight(
				_highlightTranslator.translate(
					searchSearchRequest.getHighlight(), _queryTranslator));
		}
		else if (searchSearchRequest.isHighlightEnabled()) {
			searchRequestBuilder.highlight(
				_highlightTranslator.translate(
					searchSearchRequest.getHighlightFieldNames(),
					searchSearchRequest.getHighlightFragmentSize(),
					searchSearchRequest.isHighlightRequireFieldMatch(),
					searchSearchRequest.isLuceneSyntax(),
					searchSearchRequest.getHighlightSnippetSize()));
		}
	}

	private void _setPagination(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		SetterUtil.setNotNullInteger(
			searchRequestBuilder::from, searchSearchRequest.getStart());
		SetterUtil.setNotNullInteger(
			searchRequestBuilder::size, searchSearchRequest.getSize());
	}

	private void _setPreference(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		SetterUtil.setNotBlankString(
			searchRequestBuilder::preference,
			searchSearchRequest.getPreference());
	}

	private void _setScroll(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		long scrollKeepAliveMinutes =
			searchSearchRequest.getScrollKeepAliveMinutes();

		if (scrollKeepAliveMinutes > 0) {
			searchRequestBuilder.scroll(
				Time.of(
					time -> time.time(
						scrollKeepAliveMinutes +
							TimeUnit.Minutes.jsonValue())));
		}
	}

	private void _setSearchAfter(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		if (ArrayUtil.isNotEmpty(searchSearchRequest.getSearchAfter())) {
			searchRequestBuilder.searchAfter(
				Arrays.asList(
					ArrayUtil.toStringArray(
						searchSearchRequest.getSearchAfter())));
		}
	}

	private void _setSorts(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		for (Sort sort : searchSearchRequest.getSorts()) {
			searchRequestBuilder.sort(_sortFieldTranslator.translate(sort));
		}

		searchRequestBuilder.sort(
			_sortTranslator.translateSorts(searchSearchRequest.getSorts71()));
	}

	private void _setStats(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		Map<String, Stats> statsMap = searchSearchRequest.getStats();

		if (MapUtil.isNotEmpty(statsMap)) {
			statsMap.forEach(
				(key, stats) -> _statsTranslator.populateRequest(
					searchRequestBuilder, translate(stats)));
		}
	}

	private void _setStoredFields(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		String[] storedFields = searchSearchRequest.getStoredFields();

		if (ArrayUtil.isEmpty(storedFields)) {
			return;
		}

		searchRequestBuilder.storedFields(ListUtil.fromArray(storedFields));
	}

	private void _setTrackScores(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		SetterUtil.setNotNullBoolean(
			searchRequestBuilder::trackScores,
			searchSearchRequest.getScoreEnabled());
	}

	private void _setVersion(
		SearchRequest.Builder searchRequestBuilder,
		SearchSearchRequest searchSearchRequest) {

		SetterUtil.setNotNullBoolean(
			searchRequestBuilder::version, searchSearchRequest.getVersion());
	}

	private List<FieldAndFormat> _toFieldAndFormats(String[] fieldNames) {
		List<FieldAndFormat> fieldAndFormats = new ArrayList<>();

		for (String fieldName : fieldNames) {
			fieldAndFormats.add(
				FieldAndFormat.of(
					fieldAndFormat -> fieldAndFormat.field(fieldName)));
		}

		return fieldAndFormats;
	}

	@Reference
	private CommonSearchRequestBuilderAssembler
		_commonSearchRequestBuilderAssembler;

	@Reference
	private GroupByRequestFactory _groupByRequestFactory;

	@Reference
	private GroupByTranslator _groupByTranslator;

	private final HighlightTranslator _highlightTranslator =
		new HighlightTranslator();

	@Reference(target = "(search.engine.impl=OpenSearch)")
	private QueryTranslator<QueryVariant> _queryTranslator;

	@Reference
	private SortFieldTranslator<SortOptions> _sortFieldTranslator;

	private final SortTranslator _sortTranslator = new SortTranslator();

	@Reference
	private StatsRequestBuilderFactory _statsRequestBuilderFactory;

	@Reference
	private StatsTranslator _statsTranslator;

}