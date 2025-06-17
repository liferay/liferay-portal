/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.groupby;

import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.groupby.GroupByRequest;
import com.liferay.portal.search.opensearch2.internal.highlight.HighlightTranslator;
import com.liferay.portal.search.opensearch2.internal.legacy.sort.SortTranslator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.aggregations.Aggregation;
import org.opensearch.client.opensearch._types.aggregations.AggregationBuilders;
import org.opensearch.client.opensearch._types.aggregations.BucketSortAggregation;
import org.opensearch.client.opensearch._types.aggregations.TermsAggregation;
import org.opensearch.client.opensearch._types.aggregations.TopHitsAggregation;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.search.SourceConfig;
import org.opensearch.client.opensearch.core.search.SourceFilter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 * @author Tibor Lipusz
 * @author Petteri Karttunen
 */
@Component(service = GroupByTranslator.class)
public class GroupByTranslatorImpl implements GroupByTranslator {

	@Override
	public void translate(
		GroupByRequest groupByRequest, boolean highlightEnabled,
		String[] highlightFieldNames, int highlightFragmentSize,
		boolean highlightRequireFieldMatch, int highlightSnippetSize,
		Locale locale, SearchRequest.Builder searchRequestBuilder,
		String[] selectedFieldNames) {

		Aggregation.Builder aggregationBuilder = new Aggregation.Builder();

		TermsAggregation.Builder termsAggregationBuilder =
			AggregationBuilders.terms();

		termsAggregationBuilder = termsAggregationBuilder.field(
			groupByRequest.getField());

		int termsSize = GetterUtil.getInteger(groupByRequest.getTermsSize());

		if (termsSize > 0) {
			termsAggregationBuilder.size(termsSize);
		}

		_addTermsSorts(termsAggregationBuilder, groupByRequest);

		Aggregation.Builder.ContainerBuilder containerBuilder =
			aggregationBuilder.terms(termsAggregationBuilder.build());

		int termsStart = GetterUtil.getInteger(groupByRequest.getTermsStart());

		if ((termsSize > 0) || (termsStart > 0)) {
			containerBuilder.aggregations(
				BUCKET_SORT_AGGREGATION_NAME,
				_getBucketSortPipelineAggregation(termsSize, termsStart));
		}

		containerBuilder.aggregations(
			TOP_HITS_AGGREGATION_NAME,
			_getTopHitsAggregation(
				groupByRequest, selectedFieldNames, highlightEnabled,
				highlightFieldNames, highlightFragmentSize,
				highlightRequireFieldMatch, highlightSnippetSize));

		searchRequestBuilder.aggregations(
			GROUP_BY_AGGREGATION_PREFIX + groupByRequest.getField(),
			containerBuilder.build());
	}

	private void _addTermsSorts(
		TermsAggregation.Builder builder, GroupByRequest groupByRequest) {

		Sort[] sorts = groupByRequest.getTermsSorts();

		if (ArrayUtil.isEmpty(sorts)) {
			return;
		}

		Set<String> sortFieldNames = new HashSet<>();

		List<Map<String, SortOrder>> sortOrdersList = new ArrayList<>();

		for (Sort sort : sorts) {
			if (sort == null) {
				continue;
			}

			String sortFieldName = sort.getFieldName();

			if (sortFieldNames.contains(sortFieldName)) {
				continue;
			}

			sortFieldNames.add(sortFieldName);

			if (sortFieldName.equals("_count") ||
				sortFieldName.equals("_key")) {

				sortOrdersList.add(
					HashMapBuilder.<String, SortOrder>put(
						sortFieldName, _getSortOrder(sort)
					).build());
			}
		}

		if (!sortOrdersList.isEmpty()) {
			builder.order(sortOrdersList);
		}
	}

	private Aggregation _getBucketSortPipelineAggregation(int size, int start) {
		BucketSortAggregation.Builder builder =
			AggregationBuilders.bucketSort();

		if (start > 0) {
			builder.from(start);
		}

		if (size > 0) {
			builder.size(size);
		}

		return new Aggregation(builder.build());
	}

	private SortOrder _getSortOrder(Sort sort) {
		if (sort.isReverse()) {
			return SortOrder.Desc;
		}

		return SortOrder.Asc;
	}

	private Aggregation _getTopHitsAggregation(
		GroupByRequest groupByRequest, String[] selectedFieldNames,
		boolean highlightEnabled, String[] highlightFieldNames,
		int highlightFragmentSize, boolean highlightRequireFieldMatch,
		int highlightSnippetSize) {

		TopHitsAggregation.Builder builder = AggregationBuilders.topHits();

		int docsStart = GetterUtil.getInteger(groupByRequest.getDocsStart());

		if (docsStart > 0) {
			builder.from(docsStart);
		}

		int docsSize = GetterUtil.getInteger(groupByRequest.getDocsSize());

		if (docsSize > 0) {
			builder.size(docsSize);
		}

		if (ArrayUtil.isNotEmpty(groupByRequest.getDocsSorts())) {
			builder.sort(
				_sortTranslator.translateSorts(groupByRequest.getDocsSorts()));
		}

		if (highlightEnabled) {
			builder.highlight(
				_highlightTranslator.translate(
					highlightFieldNames, highlightFragmentSize,
					highlightRequireFieldMatch, false, highlightSnippetSize));
		}

		if (ArrayUtil.isEmpty(selectedFieldNames)) {
			builder.source(
				SourceConfig.of(sourceConfig -> sourceConfig.fetch(true)));
		}
		else {
			builder.source(
				SourceConfig.of(
					sourceConfig -> sourceConfig.filter(
						SourceFilter.of(
							sourceFilter -> sourceFilter.includes(
								ListUtil.fromArray(selectedFieldNames))))));
		}

		return new Aggregation(builder.build());
	}

	private final HighlightTranslator _highlightTranslator =
		new HighlightTranslator();
	private final SortTranslator _sortTranslator = new SortTranslator();

}