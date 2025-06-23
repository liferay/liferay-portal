/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.groupby;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.GeoDistanceSort;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.groupby.GroupByRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.BucketSortPipelineAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author Michael C. Han
 */
public class GroupByTranslator {

	public static final String BUCKET_SORT_AGGREGATION_NAME = "_bucketSort";

	public static final String GROUP_BY_AGGREGATION_PREFIX = "GroupBy_";

	public static final String TOP_HITS_AGGREGATION_NAME = "_topHits";

	public void translate(
		SearchSourceBuilder searchSourceBuilder, GroupByRequest groupByRequest,
		Locale locale, String[] selectedFieldNames,
		String[] highlightFieldNames, boolean highlightEnabled,
		boolean highlightRequireFieldMatch, int highlightFragmentSize,
		int highlightSnippetSize) {

		TermsAggregationBuilder termsAggregationBuilder =
			AggregationBuilders.terms(
				GROUP_BY_AGGREGATION_PREFIX + groupByRequest.getField());

		termsAggregationBuilder = termsAggregationBuilder.field(
			groupByRequest.getField());

		int termsSize = GetterUtil.getInteger(groupByRequest.getTermsSize());

		if (termsSize > 0) {
			termsAggregationBuilder.size(termsSize);
		}

		_addTermsSorts(termsAggregationBuilder, groupByRequest);

		int termsStart = GetterUtil.getInteger(groupByRequest.getTermsStart());

		if ((termsSize > 0) || (termsStart > 0)) {
			termsAggregationBuilder.subAggregation(
				_getBucketSortPipelineBuilder(termsStart, termsSize));
		}

		TopHitsAggregationBuilder topHitsAggregationBuilder =
			_getTopHitsBuilder(
				groupByRequest, selectedFieldNames, locale, highlightFieldNames,
				highlightEnabled, highlightRequireFieldMatch,
				highlightFragmentSize, highlightSnippetSize);

		termsAggregationBuilder.subAggregation(topHitsAggregationBuilder);

		searchSourceBuilder.aggregation(termsAggregationBuilder);
	}

	private void _addDocsSorts(
		TopHitsAggregationBuilder topHitsAggregationBuilder, Sort[] sorts) {

		if (ArrayUtil.isEmpty(sorts)) {
			return;
		}

		Set<String> sortFieldNames = new HashSet<>();

		for (Sort sort : sorts) {
			if (sort == null) {
				continue;
			}

			String sortFieldName = Field.getSortFieldName(
				sort, _ELASTICSEARCH_SCORE_FIELD);

			if (sortFieldNames.contains(sortFieldName)) {
				continue;
			}

			sortFieldNames.add(sortFieldName);

			SortOrder sortOrder = SortOrder.ASC;

			if (sort.isReverse() ||
				sortFieldName.equals(_ELASTICSEARCH_SCORE_FIELD)) {

				sortOrder = SortOrder.DESC;
			}

			SortBuilder<?> sortBuilder = null;

			if (sortFieldName.equals(_ELASTICSEARCH_SCORE_FIELD)) {
				sortBuilder = SortBuilders.scoreSort();
			}
			else if (sort.getType() == Sort.GEO_DISTANCE_TYPE) {
				GeoDistanceSort geoDistanceSort = (GeoDistanceSort)sort;

				List<GeoPoint> geoPoints = new ArrayList<>();

				for (GeoLocationPoint geoLocationPoint :
						geoDistanceSort.getGeoLocationPoints()) {

					geoPoints.add(
						new GeoPoint(
							geoLocationPoint.getLatitude(),
							geoLocationPoint.getLongitude()));
				}

				GeoDistanceSortBuilder geoDistanceSortBuilder =
					SortBuilders.geoDistanceSort(
						sortFieldName, geoPoints.toArray(new GeoPoint[0]));

				geoDistanceSortBuilder.geoDistance(GeoDistance.ARC);

				Collection<String> geoHashes = geoDistanceSort.getGeoHashes();

				if (!geoHashes.isEmpty()) {
					geoDistanceSort.addGeoHash(
						geoHashes.toArray(new String[0]));
				}

				sortBuilder = geoDistanceSortBuilder;
			}
			else {
				FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort(
					sortFieldName);

				fieldSortBuilder.unmappedType("keyword");

				sortBuilder = fieldSortBuilder;
			}

			sortBuilder.order(sortOrder);

			topHitsAggregationBuilder.sort(sortBuilder);
		}
	}

	private void _addHighlightedField(
		TopHitsAggregationBuilder topHitsAggregationBuilder,
		HighlightBuilder highlightBuilder, Locale locale, String fieldName,
		int highlightFragmentSize, int highlightSnippetSize) {

		highlightBuilder.field(
			fieldName, highlightFragmentSize, highlightSnippetSize);

		String localizedFieldName = Field.getLocalizedName(locale, fieldName);

		highlightBuilder.field(
			localizedFieldName, highlightFragmentSize, highlightSnippetSize);

		topHitsAggregationBuilder.highlighter(highlightBuilder);
	}

	private void _addHighlights(
		TopHitsAggregationBuilder topHitsAggregationBuilder, Locale locale,
		String[] highlightFieldNames, int highlightFragmentSize,
		int highlightSnippetSize, boolean highlightRequireFieldMatch) {

		HighlightBuilder highlightBuilder = new HighlightBuilder();

		for (String highlightFieldName : highlightFieldNames) {
			_addHighlightedField(
				topHitsAggregationBuilder, highlightBuilder, locale,
				highlightFieldName, highlightFragmentSize,
				highlightSnippetSize);
		}

		highlightBuilder.postTags(HighlightUtil.HIGHLIGHT_TAG_CLOSE);
		highlightBuilder.preTags(HighlightUtil.HIGHLIGHT_TAG_OPEN);
		highlightBuilder.requireFieldMatch(highlightRequireFieldMatch);

		topHitsAggregationBuilder.highlighter(highlightBuilder);
	}

	private void _addSelectedFields(
		TopHitsAggregationBuilder topHitsAggregationBuilder,
		String[] selectedFieldNames) {

		if (ArrayUtil.isEmpty(selectedFieldNames)) {
			topHitsAggregationBuilder.fetchSource(true);
		}
		else {
			topHitsAggregationBuilder.fetchSource(
				selectedFieldNames, new String[0]);
		}
	}

	private void _addTermsSorts(
		TermsAggregationBuilder termsAggregationBuilder,
		GroupByRequest groupByRequest) {

		Sort[] sorts = groupByRequest.getTermsSorts();

		if (ArrayUtil.isEmpty(sorts)) {
			return;
		}

		Set<String> sortFieldNames = new HashSet<>();

		List<BucketOrder> bucketOrders = new ArrayList<>(sorts.length);

		for (Sort sort : sorts) {
			if (sort == null) {
				continue;
			}

			String sortFieldName = sort.getFieldName();

			if (sortFieldNames.contains(sortFieldName)) {
				continue;
			}

			sortFieldNames.add(sortFieldName);

			if (sortFieldName.equals("_count")) {
				bucketOrders.add(BucketOrder.count(!sort.isReverse()));
			}
			else if (sortFieldName.equals("_key")) {
				bucketOrders.add(BucketOrder.key(!sort.isReverse()));
			}
		}

		if (!bucketOrders.isEmpty()) {
			termsAggregationBuilder.order(bucketOrders);
		}
	}

	private BucketSortPipelineAggregationBuilder _getBucketSortPipelineBuilder(
		int start, int size) {

		BucketSortPipelineAggregationBuilder
			bucketSortPipelineAggregationBuilder =
				new BucketSortPipelineAggregationBuilder(
					BUCKET_SORT_AGGREGATION_NAME, Collections.emptyList());

		if (start > 0) {
			bucketSortPipelineAggregationBuilder.from(start);
		}

		if (size > 0) {
			bucketSortPipelineAggregationBuilder.size(size);
		}

		return bucketSortPipelineAggregationBuilder;
	}

	private TopHitsAggregationBuilder _getTopHitsBuilder(
		GroupByRequest groupByRequest, String[] selectedFieldNames,
		Locale locale, String[] highlightFieldNames, boolean highlightEnabled,
		boolean highlightRequireFieldMatch, int highlightFragmentSize,
		int highlightSnippetSize) {

		TopHitsAggregationBuilder topHitsAggregationBuilder =
			AggregationBuilders.topHits(TOP_HITS_AGGREGATION_NAME);

		int docsStart = GetterUtil.getInteger(groupByRequest.getDocsStart());

		if (docsStart > 0) {
			topHitsAggregationBuilder.from(docsStart);
		}

		int docsSize = GetterUtil.getInteger(groupByRequest.getDocsSize());

		if (docsSize > 0) {
			topHitsAggregationBuilder.size(docsSize);
		}

		_addDocsSorts(topHitsAggregationBuilder, groupByRequest.getDocsSorts());

		if (highlightEnabled) {
			_addHighlights(
				topHitsAggregationBuilder, locale, highlightFieldNames,
				highlightFragmentSize, highlightSnippetSize,
				highlightRequireFieldMatch);
		}

		_addSelectedFields(topHitsAggregationBuilder, selectedFieldNames);

		return topHitsAggregationBuilder;
	}

	private static final String _ELASTICSEARCH_SCORE_FIELD = "_score";

}