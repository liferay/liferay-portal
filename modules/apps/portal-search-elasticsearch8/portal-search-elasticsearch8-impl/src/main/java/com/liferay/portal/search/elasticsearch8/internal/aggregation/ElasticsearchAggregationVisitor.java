/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.aggregation;

import co.elastic.clients.elasticsearch._types.GeoHashPrecision;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.TimeUnit;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder.ContainerBuilder;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.aggregations.AverageAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.ChiSquareHeuristic;
import co.elastic.clients.elasticsearch._types.aggregations.DateRangeExpression;
import co.elastic.clients.elasticsearch._types.aggregations.ExtendedBounds;
import co.elastic.clients.elasticsearch._types.aggregations.GoogleNormalizedDistanceHeuristic;
import co.elastic.clients.elasticsearch._types.aggregations.HdrMethod;
import co.elastic.clients.elasticsearch._types.aggregations.MutualInformationHeuristic;
import co.elastic.clients.elasticsearch._types.aggregations.PercentageScoreHeuristic;
import co.elastic.clients.elasticsearch._types.aggregations.SamplerAggregationExecutionHint;
import co.elastic.clients.elasticsearch._types.aggregations.ScriptedHeuristic;
import co.elastic.clients.elasticsearch._types.aggregations.TDigest;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregationCollectMode;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregationExecutionHint;
import co.elastic.clients.elasticsearch._types.aggregations.TermsExclude;
import co.elastic.clients.elasticsearch._types.aggregations.TermsInclude;
import co.elastic.clients.elasticsearch._types.aggregations.WeightedAverageAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.WeightedAverageValue;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldAndFormat;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SourceConfigBuilders;
import co.elastic.clients.elasticsearch.core.search.SourceFilter;
import co.elastic.clients.util.NamedValue;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationVisitor;
import com.liferay.portal.search.aggregation.ValueType;
import com.liferay.portal.search.aggregation.bucket.ChildrenAggregation;
import com.liferay.portal.search.aggregation.bucket.CollectionMode;
import com.liferay.portal.search.aggregation.bucket.DateHistogramAggregation;
import com.liferay.portal.search.aggregation.bucket.DateRangeAggregation;
import com.liferay.portal.search.aggregation.bucket.DiversifiedSamplerAggregation;
import com.liferay.portal.search.aggregation.bucket.FilterAggregation;
import com.liferay.portal.search.aggregation.bucket.FiltersAggregation;
import com.liferay.portal.search.aggregation.bucket.GeoDistanceAggregation;
import com.liferay.portal.search.aggregation.bucket.GeoHashGridAggregation;
import com.liferay.portal.search.aggregation.bucket.GlobalAggregation;
import com.liferay.portal.search.aggregation.bucket.HistogramAggregation;
import com.liferay.portal.search.aggregation.bucket.IncludeExcludeClause;
import com.liferay.portal.search.aggregation.bucket.MissingAggregation;
import com.liferay.portal.search.aggregation.bucket.NestedAggregation;
import com.liferay.portal.search.aggregation.bucket.Order;
import com.liferay.portal.search.aggregation.bucket.Range;
import com.liferay.portal.search.aggregation.bucket.RangeAggregation;
import com.liferay.portal.search.aggregation.bucket.ReverseNestedAggregation;
import com.liferay.portal.search.aggregation.bucket.SamplerAggregation;
import com.liferay.portal.search.aggregation.bucket.SignificantTermsAggregation;
import com.liferay.portal.search.aggregation.bucket.SignificantTextAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.metrics.AvgAggregation;
import com.liferay.portal.search.aggregation.metrics.CardinalityAggregation;
import com.liferay.portal.search.aggregation.metrics.ExtendedStatsAggregation;
import com.liferay.portal.search.aggregation.metrics.GeoBoundsAggregation;
import com.liferay.portal.search.aggregation.metrics.GeoCentroidAggregation;
import com.liferay.portal.search.aggregation.metrics.MaxAggregation;
import com.liferay.portal.search.aggregation.metrics.MinAggregation;
import com.liferay.portal.search.aggregation.metrics.PercentileRanksAggregation;
import com.liferay.portal.search.aggregation.metrics.PercentilesAggregation;
import com.liferay.portal.search.aggregation.metrics.PercentilesMethod;
import com.liferay.portal.search.aggregation.metrics.ScriptedMetricAggregation;
import com.liferay.portal.search.aggregation.metrics.StatsAggregation;
import com.liferay.portal.search.aggregation.metrics.SumAggregation;
import com.liferay.portal.search.aggregation.metrics.TopHitsAggregation;
import com.liferay.portal.search.aggregation.metrics.ValueCountAggregation;
import com.liferay.portal.search.aggregation.metrics.WeightedAvgAggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregation;
import com.liferay.portal.search.elasticsearch8.internal.geolocation.GeoTranslator;
import com.liferay.portal.search.elasticsearch8.internal.highlight.HighlightTranslator;
import com.liferay.portal.search.elasticsearch8.internal.query.ElasticsearchQueryVisitor;
import com.liferay.portal.search.elasticsearch8.internal.script.ScriptTranslator;
import com.liferay.portal.search.elasticsearch8.internal.sort.ElasticsearchSortFieldTranslator;
import com.liferay.portal.search.elasticsearch8.internal.util.ConversionUtil;
import com.liferay.portal.search.elasticsearch8.internal.util.ElasticsearchStringUtil;
import com.liferay.portal.search.elasticsearch8.internal.util.SetterUtil;
import com.liferay.portal.search.script.Script;
import com.liferay.portal.search.significance.ChiSquareSignificanceHeuristic;
import com.liferay.portal.search.significance.GNDSignificanceHeuristic;
import com.liferay.portal.search.significance.MutualInformationSignificanceHeuristic;
import com.liferay.portal.search.significance.PercentageScoreSignificanceHeuristic;
import com.liferay.portal.search.significance.ScriptSignificanceHeuristic;
import com.liferay.portal.search.significance.SignificanceHeuristic;
import com.liferay.portal.search.sort.SortFieldTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Michael C. Han
 */
public class ElasticsearchAggregationVisitor
	implements AggregationVisitor
		<co.elastic.clients.elasticsearch._types.aggregations.Aggregation> {

	public static final ElasticsearchAggregationVisitor INSTANCE =
		new ElasticsearchAggregationVisitor();

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(AvgAggregation avgAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		AverageAggregation.Builder averageAggregationBuilder =
			AggregationBuilders.avg();

		averageAggregationBuilder.field(avgAggregation.getField());

		SetterUtil.setNotNullFieldValue(
			averageAggregationBuilder::missing, avgAggregation.getMissing());
		SetterUtil.setNotNullScript(
			averageAggregationBuilder::script, avgAggregation.getScript());

		return _translateChildAggregations(
			avgAggregation,
			aggregationBuilder.avg(averageAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(CardinalityAggregation cardinalityAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			CardinalityAggregation.Builder cardinalityAggregationBuilder =
				AggregationBuilders.cardinality();

		cardinalityAggregationBuilder.field(cardinalityAggregation.getField());

		SetterUtil.setNotNullFieldValue(
			cardinalityAggregationBuilder::missing,
			cardinalityAggregation.getMissing());
		SetterUtil.setNotNullInteger(
			cardinalityAggregationBuilder::precisionThreshold,
			cardinalityAggregation.getPrecisionThreshold());
		SetterUtil.setNotNullScript(
			cardinalityAggregationBuilder::script,
			cardinalityAggregation.getScript());

		return _translateChildAggregations(
			cardinalityAggregation,
			aggregationBuilder.cardinality(
				cardinalityAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(ChildrenAggregation childrenAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			ChildrenAggregation.Builder childrenAggregationBuilder =
				AggregationBuilders.children();

		SetterUtil.setNotBlankString(
			childrenAggregationBuilder::type,
			childrenAggregation.getChildType());

		return _translateChildAggregations(
			childrenAggregation,
			aggregationBuilder.children(childrenAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(DateHistogramAggregation dateHistogramAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			DateHistogramAggregation.Builder dateHistogramAggregationBuilder =
				AggregationBuilders.dateHistogram();

		if (dateHistogramAggregation.getDateHistogramInterval() != null) {
			dateHistogramAggregationBuilder.fixedInterval(
				Time.of(
					time -> time.time(
						dateHistogramAggregation.getDateHistogramInterval())));
		}

		dateHistogramAggregationBuilder.field(
			dateHistogramAggregation.getField());

		if ((dateHistogramAggregation.getMaxBound() != null) &&
			(dateHistogramAggregation.getMinBound() != null)) {

			dateHistogramAggregationBuilder.extendedBounds(
				ExtendedBounds.of(
					elasticsearchExtendedBounds ->
						elasticsearchExtendedBounds.max(
							ConversionUtil.toFieldDateMath(
								null,
								ConversionUtil.toDouble(
									dateHistogramAggregation.getMaxBound()))
						).min(
							ConversionUtil.toFieldDateMath(
								null,
								ConversionUtil.toDouble(
									dateHistogramAggregation.getMinBound()))
						)));
		}

		if (dateHistogramAggregation.getMinDocCount() != null) {
			dateHistogramAggregationBuilder.minDocCount(
				Math.toIntExact(dateHistogramAggregation.getMinDocCount()));
		}

		if (dateHistogramAggregation.getOffset() != null) {
			dateHistogramAggregationBuilder.offset(
				Time.of(
					time -> time.time(
						dateHistogramAggregation.getOffset() +
							TimeUnit.Milliseconds.jsonValue())));
		}

		if (ListUtil.isNotEmpty(dateHistogramAggregation.getOrders())) {
			dateHistogramAggregationBuilder.order(
				_translateOrders(dateHistogramAggregation.getOrders()));
		}

		return _translateChildAggregations(
			dateHistogramAggregation,
			aggregationBuilder.dateHistogram(
				dateHistogramAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(DateRangeAggregation dateRangeAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			DateRangeAggregation.Builder dateRangeAggregationBuilder =
				AggregationBuilders.dateRange();

		dateRangeAggregationBuilder.field(dateRangeAggregation.getField());

		SetterUtil.setNotBlankString(
			dateRangeAggregationBuilder::format,
			dateRangeAggregation.getFormat());
		SetterUtil.setNotNullBoolean(
			dateRangeAggregationBuilder::keyed,
			dateRangeAggregation.getKeyed());
		SetterUtil.setNotNullFieldValue(
			dateRangeAggregationBuilder::missing,
			dateRangeAggregation.getMissing());
		setRanges(dateRangeAggregationBuilder, dateRangeAggregation);

		return _translateChildAggregations(
			dateRangeAggregation,
			aggregationBuilder.dateRange(dateRangeAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(DiversifiedSamplerAggregation diversifiedSamplerAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			DiversifiedSamplerAggregation.Builder
				diversifiedSamplerAggregationBuilder =
					AggregationBuilders.diversifiedSampler();

		if (diversifiedSamplerAggregation.getExecutionHint() != null) {
			diversifiedSamplerAggregationBuilder.executionHint(
				SamplerAggregationExecutionHint.valueOf(
					diversifiedSamplerAggregation.getExecutionHint()));
		}

		diversifiedSamplerAggregationBuilder.field(
			diversifiedSamplerAggregation.getField());

		SetterUtil.setNotNullInteger(
			diversifiedSamplerAggregationBuilder::maxDocsPerValue,
			diversifiedSamplerAggregation.getMaxDocsPerValue());
		SetterUtil.setNotNullScript(
			diversifiedSamplerAggregationBuilder::script,
			diversifiedSamplerAggregation.getScript());
		SetterUtil.setNotNullInteger(
			diversifiedSamplerAggregationBuilder::shardSize,
			diversifiedSamplerAggregation.getShardSize());

		return _translateChildAggregations(
			diversifiedSamplerAggregation,
			aggregationBuilder.diversifiedSampler(
				diversifiedSamplerAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(ExtendedStatsAggregation extendedStatsAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			ExtendedStatsAggregation.Builder extendedStatsAggregationBuilder =
				AggregationBuilders.extendedStats();

		extendedStatsAggregationBuilder.field(
			extendedStatsAggregation.getField());

		SetterUtil.setNotNullFieldValue(
			extendedStatsAggregationBuilder::missing,
			extendedStatsAggregation.getMissing());
		SetterUtil.setNotNullScript(
			extendedStatsAggregationBuilder::script,
			extendedStatsAggregation.getScript());

		Integer sigma = extendedStatsAggregation.getSigma();

		if (sigma != null) {
			extendedStatsAggregationBuilder.sigma(sigma.doubleValue());
		}

		return _translateChildAggregations(
			extendedStatsAggregation,
			aggregationBuilder.extendedStats(
				extendedStatsAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(FilterAggregation filterAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			builder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		return _translateChildAggregations(
			filterAggregation,
			builder.filter(
				new Query(
					ElasticsearchQueryVisitor.INSTANCE.translate(
						filterAggregation.getFilterQuery()))));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(FiltersAggregation filtersAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.FiltersAggregation.
			Builder filtersAggregationBuilder = AggregationBuilders.filters();

		List<FiltersAggregation.KeyedQuery> keyedQueries =
			filtersAggregation.getKeyedQueries();

		Map<String, Query> keyedFilters = new HashMap<>();

		keyedQueries.forEach(
			keyedQuery -> keyedFilters.put(
				keyedQuery.getKey(),
				new Query(
					ElasticsearchQueryVisitor.INSTANCE.translate(
						keyedQuery.getQuery()))));

		Buckets.Builder<Query> bucketsBuilder = new Buckets.Builder<>();

		filtersAggregationBuilder.filters(
			bucketsBuilder.keyed(
				keyedFilters
			).build());

		SetterUtil.setNotNullBoolean(
			filtersAggregationBuilder::otherBucket,
			filtersAggregation.getOtherBucket());
		SetterUtil.setNotBlankString(
			filtersAggregationBuilder::otherBucketKey,
			filtersAggregation.getOtherBucketKey());

		return _translateChildAggregations(
			filtersAggregation,
			aggregationBuilder.filters(filtersAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(GeoBoundsAggregation geoBoundsAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			GeoBoundsAggregation.Builder geoBoundsAggregationBuilder =
				AggregationBuilders.geoBounds();

		geoBoundsAggregationBuilder.field(geoBoundsAggregation.getField());

		SetterUtil.setNotNullFieldValue(
			geoBoundsAggregationBuilder::missing,
			geoBoundsAggregation.getMissing());
		SetterUtil.setNotNullScript(
			geoBoundsAggregationBuilder::script,
			geoBoundsAggregation.getScript());
		SetterUtil.setNotNullBoolean(
			geoBoundsAggregationBuilder::wrapLongitude,
			geoBoundsAggregation.getWrapLongitude());

		return _translateChildAggregations(
			geoBoundsAggregation,
			aggregationBuilder.geoBounds(geoBoundsAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(GeoCentroidAggregation geoCentroidAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			GeoCentroidAggregation.Builder geoCentroidAggregationBuilder =
				AggregationBuilders.geoCentroid();

		geoCentroidAggregationBuilder.field(geoCentroidAggregation.getField());

		SetterUtil.setNotNullFieldValue(
			geoCentroidAggregationBuilder::missing,
			geoCentroidAggregation.getMissing());
		SetterUtil.setNotNullScript(
			geoCentroidAggregationBuilder::script,
			geoCentroidAggregation.getScript());

		return _translateChildAggregations(
			geoCentroidAggregation,
			aggregationBuilder.geoCentroid(
				geoCentroidAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(GeoDistanceAggregation geoDistanceAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			GeoDistanceAggregation.Builder geoDistanceAggregationBuilder =
				AggregationBuilders.geoDistance();

		if (geoDistanceAggregation.getGeoDistanceType() != null) {
			geoDistanceAggregationBuilder.distanceType(
				_geoTranslator.translateGeoDistanceType(
					geoDistanceAggregation.getGeoDistanceType()));
		}

		geoDistanceAggregationBuilder.field(geoDistanceAggregation.getField());

		geoDistanceAggregationBuilder.origin(
			_geoTranslator.translateGeoLocationPoint(
				geoDistanceAggregation.getGeoLocationPoint()));

		if (geoDistanceAggregation.getDistanceUnit() != null) {
			geoDistanceAggregationBuilder.unit(
				_geoTranslator.translateDistanceUnit(
					geoDistanceAggregation.getDistanceUnit()));
		}

		List<Range> rangeAggregationRanges = geoDistanceAggregation.getRanges();

		rangeAggregationRanges.forEach(
			rangeAggregationRange -> geoDistanceAggregationBuilder.ranges(
				_createAggregationRange(
					ElasticsearchStringUtil.getFirstDoubleValue(
						rangeAggregationRange::getFrom,
						rangeAggregationRange::getFromAsString),
					rangeAggregationRange.getKey(),
					ElasticsearchStringUtil.getFirstDoubleValue(
						rangeAggregationRange::getTo,
						rangeAggregationRange::getToAsString))));

		return _translateChildAggregations(
			geoDistanceAggregation,
			aggregationBuilder.geoDistance(
				geoDistanceAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(GeoHashGridAggregation geoHashGridAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			GeoHashGridAggregation.Builder geoHashGridAggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					GeoHashGridAggregation.Builder();

		geoHashGridAggregationBuilder.field(geoHashGridAggregation.getField());

		if (geoHashGridAggregation.getPrecision() != null) {
			geoHashGridAggregationBuilder.precision(
				GeoHashPrecision.of(
					geoHashPrecision -> geoHashPrecision.geohashLength(
						geoHashGridAggregation.getPrecision())));
		}

		SetterUtil.setNotNullInteger(
			geoHashGridAggregationBuilder::shardSize,
			geoHashGridAggregation.getShardSize());
		SetterUtil.setNotNullInteger(
			geoHashGridAggregationBuilder::size,
			geoHashGridAggregation.getSize());

		return _translateChildAggregations(
			geoHashGridAggregation,
			aggregationBuilder.geohashGrid(
				geoHashGridAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(GlobalAggregation globalAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.GlobalAggregation.
			Builder globalAggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					GlobalAggregation.Builder();

		return _translateChildAggregations(
			globalAggregation,
			aggregationBuilder.global(globalAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(HistogramAggregation histogramAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			HistogramAggregation.Builder histogramAggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					HistogramAggregation.Builder();

		histogramAggregationBuilder.field(histogramAggregation.getField());

		if ((histogramAggregation.getMaxBound() != null) &&
			(histogramAggregation.getMinBound() != null)) {

			histogramAggregationBuilder.extendedBounds(
				ExtendedBounds.of(
					elasticsearchExtendedBounds ->
						elasticsearchExtendedBounds.max(
							histogramAggregation.getMaxBound()
						).min(
							histogramAggregation.getMinBound()
						)));
		}

		SetterUtil.setNotNullDouble(
			histogramAggregationBuilder::interval,
			histogramAggregation.getInterval());

		if (histogramAggregation.getMinDocCount() != null) {
			histogramAggregationBuilder.minDocCount(
				Math.toIntExact(histogramAggregation.getMinDocCount()));
		}

		SetterUtil.setNotNullDouble(
			histogramAggregationBuilder::missing,
			GetterUtil.getDouble(histogramAggregation.getMissing()));
		SetterUtil.setNotNullDouble(
			histogramAggregationBuilder::offset,
			histogramAggregation.getOffset());

		if (ListUtil.isNotEmpty(histogramAggregation.getOrders())) {
			histogramAggregationBuilder.order(
				_translateOrders(histogramAggregation.getOrders()));
		}

		SetterUtil.setNotNullScript(
			histogramAggregationBuilder::script,
			histogramAggregation.getScript());

		return _translateChildAggregations(
			histogramAggregation,
			aggregationBuilder.histogram(histogramAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(MaxAggregation maxAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.MaxAggregation.
			Builder maxAggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					MaxAggregation.Builder();

		maxAggregationBuilder.field(maxAggregation.getField());

		SetterUtil.setNotNullFieldValue(
			maxAggregationBuilder::missing, maxAggregation.getMissing());
		SetterUtil.setNotNullScript(
			maxAggregationBuilder::script, maxAggregation.getScript());

		return _translateChildAggregations(
			maxAggregation,
			aggregationBuilder.max(maxAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(MinAggregation minAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.MinAggregation.
			Builder minAggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					MinAggregation.Builder();

		minAggregationBuilder.field(minAggregation.getField());

		SetterUtil.setNotNullFieldValue(
			minAggregationBuilder::missing, minAggregation.getMissing());
		SetterUtil.setNotNullScript(
			minAggregationBuilder::script, minAggregation.getScript());

		return _translateChildAggregations(
			minAggregation,
			aggregationBuilder.min(minAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(MissingAggregation missingAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.MissingAggregation.
			Builder missingAggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					MissingAggregation.Builder();

		missingAggregationBuilder.field(missingAggregation.getField());

		SetterUtil.setNotNullFieldValue(
			missingAggregationBuilder::missing,
			missingAggregation.getMissing());

		return _translateChildAggregations(
			missingAggregation,
			aggregationBuilder.missing(missingAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(NestedAggregation nestedAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.NestedAggregation.
			Builder nestedAggregationBuilder = AggregationBuilders.nested();

		nestedAggregationBuilder.path(nestedAggregation.getPath());

		return _translateChildAggregations(
			nestedAggregation,
			aggregationBuilder.nested(nestedAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(PercentileRanksAggregation percentileRanksAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			PercentileRanksAggregation.Builder
				percentileRanksAggregationBuilder =
					AggregationBuilders.percentileRanks();

		percentileRanksAggregationBuilder.field(
			percentileRanksAggregation.getField());

		SetterUtil.setNotNullBoolean(
			percentileRanksAggregationBuilder::keyed,
			percentileRanksAggregation.getKeyed());
		SetterUtil.setNotNullFieldValue(
			percentileRanksAggregationBuilder::missing,
			percentileRanksAggregation.getMissing());
		SetterUtil.setNotNullScript(
			percentileRanksAggregationBuilder::script,
			percentileRanksAggregation.getScript());

		if (percentileRanksAggregation.getPercentilesMethod() != null) {
			PercentilesMethod percentilesMethod =
				percentileRanksAggregation.getPercentilesMethod();

			if (percentilesMethod.equals(PercentilesMethod.HDR)) {
				percentileRanksAggregationBuilder.hdr(
					HdrMethod.of(
						hdrMethor -> hdrMethor.numberOfSignificantValueDigits(
							percentileRanksAggregation.
								getHdrSignificantValueDigits())));
			}
			else if (percentilesMethod.equals(PercentilesMethod.TDIGEST)) {
				percentileRanksAggregationBuilder.tdigest(
					TDigest.of(
						tdigest -> tdigest.compression(
							percentileRanksAggregation.getCompression())));
			}
		}

		percentileRanksAggregationBuilder.values(
			ConversionUtil.toDoubleList(
				percentileRanksAggregation.getValues()));

		return _translateChildAggregations(
			percentileRanksAggregation,
			aggregationBuilder.percentileRanks(
				percentileRanksAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(PercentilesAggregation percentilesAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			PercentilesAggregation.Builder percentilesAggregationBuilder =
				AggregationBuilders.percentiles();

		percentilesAggregationBuilder.field(percentilesAggregation.getField());

		SetterUtil.setNotNullBoolean(
			percentilesAggregationBuilder::keyed,
			percentilesAggregation.getKeyed());
		SetterUtil.setNotNullFieldValue(
			percentilesAggregationBuilder::missing,
			percentilesAggregation.getMissing());
		SetterUtil.setNotNullScript(
			percentilesAggregationBuilder::script,
			percentilesAggregation.getScript());

		if (percentilesAggregation.getPercents() != null) {
			percentilesAggregationBuilder.percents(
				ConversionUtil.toDoubleList(
					percentilesAggregation.getPercents()));
		}

		if (percentilesAggregation.getPercentilesMethod() != null) {
			PercentilesMethod percentilesMethod =
				percentilesAggregation.getPercentilesMethod();

			if (percentilesMethod.equals(PercentilesMethod.HDR)) {
				percentilesAggregationBuilder.hdr(
					HdrMethod.of(
						hdrMethor -> hdrMethor.numberOfSignificantValueDigits(
							percentilesAggregation.
								getHdrSignificantValueDigits())));
			}
			else if (percentilesMethod.equals(PercentilesMethod.TDIGEST)) {
				percentilesAggregationBuilder.tdigest(
					TDigest.of(
						tdigest -> tdigest.compression(
							percentilesAggregation.getCompression())));
			}
		}

		return _translateChildAggregations(
			percentilesAggregation,
			aggregationBuilder.percentiles(
				percentilesAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(RangeAggregation rangeAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.RangeAggregation.
			Builder rangeAggregationBuilder = AggregationBuilders.range();

		rangeAggregationBuilder.field(rangeAggregation.getField());

		SetterUtil.setNotNullBoolean(
			rangeAggregationBuilder::keyed, rangeAggregation.getKeyed());
		SetterUtil.setNotNullInteger(
			rangeAggregationBuilder::missing,
			GetterUtil.getInteger(rangeAggregation.getMissing()));
		setRanges(rangeAggregationBuilder, rangeAggregation);
		SetterUtil.setNotNullScript(
			rangeAggregationBuilder::script, rangeAggregation.getScript());

		return _translateChildAggregations(
			rangeAggregation,
			aggregationBuilder.range(rangeAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(ReverseNestedAggregation reverseNestedAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			ReverseNestedAggregation.Builder reverseNestedAggregationBuilder =
				AggregationBuilders.reverseNested();

		reverseNestedAggregationBuilder.path(
			reverseNestedAggregation.getPath());

		return _translateChildAggregations(
			reverseNestedAggregation,
			aggregationBuilder.reverseNested(
				reverseNestedAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(SamplerAggregation samplerAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.SamplerAggregation.
			Builder samplerAggregationBuilder = AggregationBuilders.sampler();

		SetterUtil.setNotNullInteger(
			samplerAggregationBuilder::shardSize,
			samplerAggregation.getShardSize());

		return _translateChildAggregations(
			samplerAggregation,
			aggregationBuilder.sampler(samplerAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(ScriptedMetricAggregation scriptedMetricAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			ScriptedMetricAggregation.Builder scriptedMetricAggregationBuilder =
				AggregationBuilders.scriptedMetric();

		SetterUtil.setNotNullScript(
			scriptedMetricAggregationBuilder::combineScript,
			scriptedMetricAggregation.getCombineScript());
		SetterUtil.setNotNullScript(
			scriptedMetricAggregationBuilder::initScript,
			scriptedMetricAggregation.getInitScript());
		SetterUtil.setNotNullScript(
			scriptedMetricAggregationBuilder::mapScript,
			scriptedMetricAggregation.getMapScript());

		scriptedMetricAggregationBuilder.params(
			ConversionUtil.toJsonDataMap(
				scriptedMetricAggregation.getParameters()));

		SetterUtil.setNotNullScript(
			scriptedMetricAggregationBuilder::reduceScript,
			scriptedMetricAggregation.getReduceScript());

		return _translateChildAggregations(
			scriptedMetricAggregation,
			aggregationBuilder.scriptedMetric(
				scriptedMetricAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(SignificantTermsAggregation significantTermsAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			SignificantTermsAggregation.Builder
				significantTermsAggregationBuilder =
					AggregationBuilders.significantTerms();

		setNotNullQuery(
			significantTermsAggregationBuilder::backgroundFilter,
			significantTermsAggregation.getBackgroundFilterQuery());

		if (significantTermsAggregation.getExecutionHint() != null) {
			significantTermsAggregationBuilder.executionHint(
				_translateExecutionHint(
					significantTermsAggregation.getExecutionHint()));
		}

		significantTermsAggregationBuilder.field(
			significantTermsAggregation.getField());

		if (significantTermsAggregation.getIncludeExcludeClause() != null) {
			IncludeExcludeClause includeExcludeClause =
				significantTermsAggregation.getIncludeExcludeClause();

			if (includeExcludeClause.getExcludeRegex() != null) {
				significantTermsAggregationBuilder.exclude(
					TermsExclude.of(
						termsExclude -> termsExclude.regexp(
							includeExcludeClause.getExcludeRegex())));
			}
			else if (includeExcludeClause.getExcludedValues() != null) {
				significantTermsAggregationBuilder.exclude(
					TermsExclude.of(
						termsExclude -> termsExclude.terms(
							Arrays.asList(
								includeExcludeClause.getExcludedValues()))));
			}

			if (includeExcludeClause.getIncludedValues() != null) {
				significantTermsAggregationBuilder.include(
					TermsInclude.of(
						termsInclude -> termsInclude.terms(
							Arrays.asList(
								includeExcludeClause.getIncludedValues()))));
			}
		}

		SetterUtil.setNotNullLong(
			significantTermsAggregationBuilder::minDocCount,
			significantTermsAggregation.getMinDocCount());
		SetterUtil.setNotNullLong(
			significantTermsAggregationBuilder::shardMinDocCount,
			significantTermsAggregation.getShardMinDocCount());
		SetterUtil.setNotNullInteger(
			significantTermsAggregationBuilder::shardSize,
			significantTermsAggregation.getShardSize());

		if (significantTermsAggregation.getSignificanceHeuristic() != null) {
			SignificanceHeuristic significanceHeuristic =
				significantTermsAggregation.getSignificanceHeuristic();

			if (significanceHeuristic instanceof
					ChiSquareSignificanceHeuristic) {

				significantTermsAggregationBuilder.chiSquare(
					_translateChiSquareHeuristic(
						(ChiSquareSignificanceHeuristic)significanceHeuristic));
			}
			else if (significanceHeuristic instanceof
						GNDSignificanceHeuristic) {

				significantTermsAggregationBuilder.gnd(
					_translateGNDSignificanceHeuristic(
						(GNDSignificanceHeuristic)significanceHeuristic));
			}
			else if (significanceHeuristic instanceof
						MutualInformationSignificanceHeuristic) {

				significantTermsAggregationBuilder.mutualInformation(
					_translateMutualInformationSignificanceHeuristic(
						(MutualInformationSignificanceHeuristic)
							significanceHeuristic));
			}
			else if (significanceHeuristic instanceof
						PercentageScoreSignificanceHeuristic) {

				significantTermsAggregationBuilder.percentage(
					new PercentageScoreHeuristic());
			}
			else if (significanceHeuristic instanceof
						ScriptSignificanceHeuristic) {

				significantTermsAggregationBuilder.scriptHeuristic(
					_translateScriptSignificanceHeuristic(
						(ScriptSignificanceHeuristic)significanceHeuristic));
			}
		}

		SetterUtil.setNotNullInteger(
			significantTermsAggregationBuilder::size,
			significantTermsAggregation.getSize());

		return _translateChildAggregations(
			significantTermsAggregation,
			aggregationBuilder.significantTerms(
				significantTermsAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(SignificantTextAggregation significantTextAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			SignificantTextAggregation.Builder
				significantTextAggregationBuilder =
					AggregationBuilders.significantText();

		setNotNullQuery(
			significantTextAggregationBuilder::backgroundFilter,
			significantTextAggregation.getBackgroundFilterQuery());

		if (significantTextAggregation.getExecutionHint() != null) {
			significantTextAggregationBuilder.executionHint(
				_translateExecutionHint(
					significantTextAggregation.getExecutionHint()));
		}

		significantTextAggregationBuilder.field(
			significantTextAggregation.getField());

		SetterUtil.setNotNullBoolean(
			significantTextAggregationBuilder::filterDuplicateText,
			significantTextAggregation.getFilterDuplicateText());

		if (significantTextAggregation.getIncludeExcludeClause() != null) {
			IncludeExcludeClause includeExcludeClause =
				significantTextAggregation.getIncludeExcludeClause();

			if (includeExcludeClause.getExcludeRegex() != null) {
				significantTextAggregationBuilder.exclude(
					TermsExclude.of(
						termsExclude -> termsExclude.regexp(
							includeExcludeClause.getExcludeRegex())));
			}
			else if (includeExcludeClause.getExcludedValues() != null) {
				significantTextAggregationBuilder.exclude(
					TermsExclude.of(
						termsExclude -> termsExclude.terms(
							Arrays.asList(
								includeExcludeClause.getExcludedValues()))));
			}

			if (includeExcludeClause.getIncludedValues() != null) {
				significantTextAggregationBuilder.include(
					TermsInclude.of(
						termsInclude -> termsInclude.terms(
							Arrays.asList(
								includeExcludeClause.getIncludedValues()))));
			}
		}

		SetterUtil.setNotNullLong(
			significantTextAggregationBuilder::minDocCount,
			significantTextAggregation.getMinDocCount());
		SetterUtil.setNotNullLong(
			significantTextAggregationBuilder::shardMinDocCount,
			significantTextAggregation.getShardMinDocCount());
		SetterUtil.setNotNullInteger(
			significantTextAggregationBuilder::shardSize,
			significantTextAggregation.getShardSize());

		if (significantTextAggregation.getSignificanceHeuristic() != null) {
			SignificanceHeuristic significanceHeuristic =
				significantTextAggregation.getSignificanceHeuristic();

			if (significanceHeuristic instanceof
					ChiSquareSignificanceHeuristic) {

				significantTextAggregationBuilder.chiSquare(
					_translateChiSquareHeuristic(
						(ChiSquareSignificanceHeuristic)significanceHeuristic));
			}
			else if (significanceHeuristic instanceof
						GNDSignificanceHeuristic) {

				significantTextAggregationBuilder.gnd(
					_translateGNDSignificanceHeuristic(
						(GNDSignificanceHeuristic)significanceHeuristic));
			}
			else if (significanceHeuristic instanceof
						MutualInformationSignificanceHeuristic) {

				significantTextAggregationBuilder.mutualInformation(
					_translateMutualInformationSignificanceHeuristic(
						(MutualInformationSignificanceHeuristic)
							significanceHeuristic));
			}
			else if (significanceHeuristic instanceof
						PercentageScoreSignificanceHeuristic) {

				significantTextAggregationBuilder.percentage(
					new PercentageScoreHeuristic());
			}
			else if (significanceHeuristic instanceof
						ScriptSignificanceHeuristic) {

				significantTextAggregationBuilder.scriptHeuristic(
					_translateScriptSignificanceHeuristic(
						(ScriptSignificanceHeuristic)significanceHeuristic));
			}
		}

		SetterUtil.setNotNullInteger(
			significantTextAggregationBuilder::size,
			significantTextAggregation.getSize());
		SetterUtil.setNotEmptyStringList(
			significantTextAggregationBuilder::sourceFields,
			significantTextAggregation.getSourceFields());

		return _translateChildAggregations(
			significantTextAggregation,
			aggregationBuilder.significantText(
				significantTextAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(StatsAggregation statsAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.StatsAggregation.
			Builder statsAggregationBuilder = AggregationBuilders.stats();

		statsAggregationBuilder.field(statsAggregation.getField());

		SetterUtil.setNotNullFieldValue(
			statsAggregationBuilder::missing, statsAggregation.getMissing());
		SetterUtil.setNotNullScript(
			statsAggregationBuilder::script, statsAggregation.getScript());

		return _translateChildAggregations(
			statsAggregation,
			aggregationBuilder.stats(statsAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(SumAggregation sumAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.SumAggregation.
			Builder sumAggregationBuilder = AggregationBuilders.sum();

		sumAggregationBuilder.field(sumAggregation.getField());

		SetterUtil.setNotNullFieldValue(
			sumAggregationBuilder::missing, sumAggregation.getMissing());
		SetterUtil.setNotNullScript(
			sumAggregationBuilder::script, sumAggregation.getScript());

		return _translateChildAggregations(
			sumAggregation,
			aggregationBuilder.sum(sumAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(TermsAggregation termsAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation.
			Builder termsAggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					TermsAggregation.Builder();

		if (termsAggregation.getCollectionMode() != null) {
			termsAggregationBuilder.collectMode(
				_translateCollectMode(termsAggregation.getCollectionMode()));
		}

		if (termsAggregation.getExecutionHint() != null) {
			termsAggregationBuilder.executionHint(
				_translateExecutionHint(termsAggregation.getExecutionHint()));
		}

		termsAggregationBuilder.field(termsAggregation.getField());

		SetterUtil.setNotNullInteger(
			termsAggregationBuilder::minDocCount,
			termsAggregation.getMinDocCount());
		SetterUtil.setNotNullFieldValue(
			termsAggregationBuilder::missing, termsAggregation.getMissing());

		if (termsAggregation.getIncludeExcludeClause() != null) {
			IncludeExcludeClause includeExcludeClause =
				termsAggregation.getIncludeExcludeClause();

			if (includeExcludeClause.getExcludeRegex() != null) {
				termsAggregationBuilder.exclude(
					TermsExclude.of(
						termsExclude -> termsExclude.regexp(
							includeExcludeClause.getExcludeRegex())));
			}
			else if (includeExcludeClause.getExcludedValues() != null) {
				termsAggregationBuilder.exclude(
					TermsExclude.of(
						termsInclude -> termsInclude.terms(
							Arrays.asList(
								includeExcludeClause.getExcludedValues()))));
			}

			if (includeExcludeClause.getIncludeRegex() != null) {
				termsAggregationBuilder.include(
					TermsInclude.of(
						termsInclude -> termsInclude.regexp(
							includeExcludeClause.getIncludeRegex())));
			}
			else if (includeExcludeClause.getIncludedValues() != null) {
				termsAggregationBuilder.include(
					TermsInclude.of(
						termsInclude -> termsInclude.terms(
							Arrays.asList(
								includeExcludeClause.getIncludedValues()))));
			}
		}

		if (ListUtil.isNotEmpty(termsAggregation.getOrders())) {
			termsAggregationBuilder.order(
				_translateOrders(termsAggregation.getOrders()));
		}

		SetterUtil.setNotNullScript(
			termsAggregationBuilder::script, termsAggregation.getScript());
		SetterUtil.setNotNullInteger(
			termsAggregationBuilder::shardSize,
			termsAggregation.getShardSize());
		SetterUtil.setNotNullBoolean(
			termsAggregationBuilder::showTermDocCountError,
			termsAggregation.getShowTermDocCountError());
		SetterUtil.setNotNullInteger(
			termsAggregationBuilder::size, termsAggregation.getSize());

		return _translateChildAggregations(
			termsAggregation,
			aggregationBuilder.terms(termsAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(TopHitsAggregation topHitsAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.TopHitsAggregation.
			Builder topHitsAggregationBuilder = AggregationBuilders.topHits();

		if (ListUtil.isNotEmpty(topHitsAggregation.getSelectedFields())) {
			topHitsAggregationBuilder.docvalueFields(
				_toFieldAndFormats(topHitsAggregation.getSelectedFields()));
		}

		SetterUtil.setNotNullBoolean(
			topHitsAggregationBuilder::explain,
			topHitsAggregation.getExplain());
		SetterUtil.setNotNullInteger(
			topHitsAggregationBuilder::from, topHitsAggregation.getFrom());

		if (topHitsAggregation.getHighlight() != null) {
			topHitsAggregationBuilder.highlight(
				_highlightTranslator.translate(
					topHitsAggregation.getHighlight()));
		}

		ListUtil.isNotEmptyForEach(
			topHitsAggregation.getScriptFields(),
			scriptField -> topHitsAggregationBuilder.scriptFields(
				scriptField.getField(),
				co.elastic.clients.elasticsearch._types.ScriptField.of(
					elasticsearchScriptField ->
						elasticsearchScriptField.ignoreFailure(
							scriptField.isIgnoreFailure()
						).script(
							scriptTranslator.translate(scriptField.getScript())
						))));

		SetterUtil.setNotNullInteger(
			topHitsAggregationBuilder::size, topHitsAggregation.getSize());

		if (topHitsAggregation.getFetchSource() != null) {
			SourceConfig.Builder sourceConfigBuilder =
				new SourceConfig.Builder();

			sourceConfigBuilder.fetch(topHitsAggregation.getFetchSource());

			SourceFilter.Builder sourceFilterbuilder =
				SourceConfigBuilders.filter();

			if (topHitsAggregation.getFetchSourceInclude() != null) {
				sourceFilterbuilder.includes(
					Arrays.asList(topHitsAggregation.getFetchSourceInclude()));
			}

			if (topHitsAggregation.getFetchSourceExclude() != null) {
				sourceFilterbuilder.includes(
					Arrays.asList(topHitsAggregation.getFetchSourceExclude()));
			}

			sourceConfigBuilder.filter(sourceFilterbuilder.build());

			topHitsAggregationBuilder.source(sourceConfigBuilder.build());
		}

		ListUtil.isNotEmptyForEach(
			topHitsAggregation.getSortFields(),
			sortField -> topHitsAggregationBuilder.sort(
				_sortFieldTranslator.translate(sortField)));

		SetterUtil.setNotNullBoolean(
			topHitsAggregationBuilder::trackScores,
			topHitsAggregation.getTrackScores());
		SetterUtil.setNotNullBoolean(
			topHitsAggregationBuilder::version,
			topHitsAggregation.getVersion());

		return _translateChildAggregations(
			topHitsAggregation,
			aggregationBuilder.topHits(topHitsAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(ValueCountAggregation valueCountAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		co.elastic.clients.elasticsearch._types.aggregations.
			ValueCountAggregation.Builder valueCountAggregationBuilder =
				AggregationBuilders.valueCount();

		valueCountAggregationBuilder.field(valueCountAggregation.getField());

		SetterUtil.setNotNullFieldValue(
			valueCountAggregationBuilder::missing,
			valueCountAggregation.getMissing());
		SetterUtil.setNotNullScript(
			valueCountAggregationBuilder::script,
			valueCountAggregation.getScript());

		return _translateChildAggregations(
			valueCountAggregation,
			aggregationBuilder.valueCount(
				valueCountAggregationBuilder.build()));
	}

	@Override
	public co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		visit(WeightedAvgAggregation weightedAvgAggregation) {

		co.elastic.clients.elasticsearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new co.elastic.clients.elasticsearch._types.aggregations.
					Aggregation.Builder();

		WeightedAverageAggregation.Builder weightedAverageAggregationBuilder =
			AggregationBuilders.weightedAvg();

		SetterUtil.setNotBlankString(
			weightedAverageAggregationBuilder::format,
			weightedAvgAggregation.getFormat());

		weightedAverageAggregationBuilder.value(
			_getWeightedAverageValue(
				weightedAvgAggregation.getValueField(),
				weightedAvgAggregation.getValueMissing(),
				weightedAvgAggregation.getValueScript()));

		if (weightedAvgAggregation.getValueType() != null) {
			weightedAverageAggregationBuilder.valueType(
				translateValueType(weightedAvgAggregation.getValueType()));
		}

		weightedAverageAggregationBuilder.weight(
			_getWeightedAverageValue(
				weightedAvgAggregation.getWeightField(),
				weightedAvgAggregation.getWeightMissing(),
				weightedAvgAggregation.getWeightScript()));

		return _translateChildAggregations(
			weightedAvgAggregation,
			aggregationBuilder.weightedAvg(
				weightedAverageAggregationBuilder.build()));
	}

	protected void setNotNullQuery(
		Consumer<Query> consumer, com.liferay.portal.search.query.Query query) {

		if (query != null) {
			consumer.accept(
				new Query(ElasticsearchQueryVisitor.INSTANCE.translate(query)));
		}
	}

	protected void setRanges(
		co.elastic.clients.elasticsearch._types.aggregations.
			DateRangeAggregation.Builder builder,
		DateRangeAggregation dateRangeAggregation) {

		List<Range> ranges = dateRangeAggregation.getRanges();

		ranges.forEach(
			range -> builder.ranges(
				DateRangeExpression.of(
					dateRangeExpression -> dateRangeExpression.from(
						ConversionUtil.toFieldDateMath(
							range.getFromAsString(), range.getFrom())
					).key(
						range.getKey()
					).to(
						ConversionUtil.toFieldDateMath(
							range.getToAsString(), range.getTo())
					))));
	}

	protected void setRanges(
		co.elastic.clients.elasticsearch._types.aggregations.RangeAggregation.
			Builder builder,
		RangeAggregation rangeAggregation) {

		List<Range> ranges = rangeAggregation.getRanges();

		ranges.forEach(
			range -> builder.ranges(
				_createAggregationRange(
					ElasticsearchStringUtil.getFirstDoubleValue(
						range::getFrom, range::getFromAsString),
					range.getKey(),
					ElasticsearchStringUtil.getFirstDoubleValue(
						range::getTo, range::getToAsString))));
	}

	protected co.elastic.clients.elasticsearch._types.aggregations.ValueType
		translateValueType(ValueType valueType) {

		if (valueType == ValueType.BOOLEAN) {
			return co.elastic.clients.elasticsearch._types.aggregations.
				ValueType.Boolean;
		}
		else if (valueType == ValueType.DATE) {
			return co.elastic.clients.elasticsearch._types.aggregations.
				ValueType.Date;
		}
		else if (valueType == ValueType.DOUBLE) {
			return co.elastic.clients.elasticsearch._types.aggregations.
				ValueType.Double;
		}
		else if (valueType == ValueType.GEOPOINT) {
			return co.elastic.clients.elasticsearch._types.aggregations.
				ValueType.GeoPoint;
		}
		else if (valueType == ValueType.IP) {
			return co.elastic.clients.elasticsearch._types.aggregations.
				ValueType.Ip;
		}
		else if (valueType == ValueType.LONG) {
			return co.elastic.clients.elasticsearch._types.aggregations.
				ValueType.Long;
		}
		else if (valueType == ValueType.NUMBER) {
			return co.elastic.clients.elasticsearch._types.aggregations.
				ValueType.Number;
		}
		else if (valueType == ValueType.NUMERIC) {
			return co.elastic.clients.elasticsearch._types.aggregations.
				ValueType.Numeric;
		}
		else if (valueType == ValueType.STRING) {
			return co.elastic.clients.elasticsearch._types.aggregations.
				ValueType.String;
		}

		throw new IllegalArgumentException("Invalid value type " + valueType);
	}

	protected final ScriptTranslator scriptTranslator = new ScriptTranslator();

	private ElasticsearchAggregationVisitor() {
	}

	private AggregationRange _createAggregationRange(
		Double from, String key, Double to) {

		AggregationRange.Builder builder = new AggregationRange.Builder();

		if (from != null) {
			builder.from(from);
		}

		if (!Validator.isBlank(key)) {
			builder.key(key);
		}

		if (to != null) {
			builder.to(to);
		}

		return builder.build();
	}

	private WeightedAverageValue _getWeightedAverageValue(
		String field, Object missing, Script script) {

		WeightedAverageValue.Builder builder =
			new WeightedAverageValue.Builder();

		builder.field(field);

		if (missing != null) {
			builder.missing(ConversionUtil.toDouble(missing));
		}

		if (script != null) {
			builder.script(scriptTranslator.translate(script));
		}

		return builder.build();
	}

	private List<FieldAndFormat> _toFieldAndFormats(List<String> fieldNames) {
		List<FieldAndFormat> fieldAndFormats = new ArrayList<>();

		for (String fieldName : fieldNames) {
			fieldAndFormats.add(
				FieldAndFormat.of(
					fieldAndFormat -> fieldAndFormat.field(fieldName)));
		}

		return fieldAndFormats;
	}

	private ChiSquareHeuristic _translateChiSquareHeuristic(
		ChiSquareSignificanceHeuristic chiSquareSignificanceHeuristic) {

		return ChiSquareHeuristic.of(
			chiSquareHeuristic -> chiSquareHeuristic.backgroundIsSuperset(
				chiSquareSignificanceHeuristic.isBackgroundIsSuperset()
			).includeNegatives(
				chiSquareSignificanceHeuristic.isIncludeNegatives()
			));
	}

	private co.elastic.clients.elasticsearch._types.aggregations.Aggregation
		_translateChildAggregations(
			Aggregation aggregation, ContainerBuilder containerBuilder) {

		for (Aggregation childAggregation :
				aggregation.getChildrenAggregations()) {

			containerBuilder.aggregations(
				childAggregation.getName(), childAggregation.accept(this));
		}

		for (PipelineAggregation pipelineAggregation :
				aggregation.getPipelineAggregations()) {

			containerBuilder.aggregations(
				pipelineAggregation.getName(),
				pipelineAggregation.accept(
					ElasticsearchPipelineAggregationVisitor.INSTANCE));
		}

		return containerBuilder.build();
	}

	private TermsAggregationCollectMode _translateCollectMode(
		CollectionMode collectionMode) {

		if (collectionMode == CollectionMode.BREADTH_FIRST) {
			return TermsAggregationCollectMode.BreadthFirst;
		}
		else if (collectionMode == CollectionMode.DEPTH_FIRST) {
			return TermsAggregationCollectMode.DepthFirst;
		}

		throw new IllegalArgumentException(
			"Invalid collection mode " + collectionMode);
	}

	private TermsAggregationExecutionHint _translateExecutionHint(
		String executionHint) {

		if (executionHint.equals("global_ordinals")) {
			return TermsAggregationExecutionHint.GlobalOrdinals;
		}
		else if (executionHint.equals("global_ordinals_hash")) {
			return TermsAggregationExecutionHint.GlobalOrdinalsHash;
		}
		else if (executionHint.equals("global_ordinals_low_cardinality")) {
			return TermsAggregationExecutionHint.GlobalOrdinalsLowCardinality;
		}

		throw new IllegalArgumentException(
			"Invalid execution hint " + executionHint);
	}

	private GoogleNormalizedDistanceHeuristic
		_translateGNDSignificanceHeuristic(
			GNDSignificanceHeuristic gndSignificanceHeuristic) {

		return GoogleNormalizedDistanceHeuristic.of(
			googleNormalizedDistanceHeuristic ->
				googleNormalizedDistanceHeuristic.backgroundIsSuperset(
					gndSignificanceHeuristic.isBackgroundIsSuperset()));
	}

	private MutualInformationHeuristic
		_translateMutualInformationSignificanceHeuristic(
			MutualInformationSignificanceHeuristic
				mutualInformationSignificanceHeuristic) {

		return MutualInformationHeuristic.of(
			mutualInformationHeuristic ->
				mutualInformationHeuristic.backgroundIsSuperset(
					mutualInformationSignificanceHeuristic.
						isBackgroundIsSuperset()
				).includeNegatives(
					mutualInformationSignificanceHeuristic.isIncludeNegatives()
				));
	}

	private NamedValue<SortOrder> _translateOrder(Order order) {
		SortOrder sortOrder;

		if (order.isAscending()) {
			sortOrder = SortOrder.Asc;
		}
		else {
			sortOrder = SortOrder.Desc;
		}

		if (Order.COUNT_METRIC_NAME.equals(order.getMetricName())) {
			return NamedValue.of(Order.COUNT_METRIC_NAME, sortOrder);
		}
		else if (Order.KEY_METRIC_NAME.equals(order.getMetricName())) {
			return NamedValue.of(Order.KEY_METRIC_NAME, sortOrder);
		}
		else if (order.getMetricName() == null) {
			return NamedValue.of(order.getPath(), sortOrder);
		}

		return NamedValue.of(
			order.getPath() + StringPool.PERIOD + order.getMetricName(),
			sortOrder);
	}

	private List<NamedValue<SortOrder>> _translateOrders(List<Order> orders) {
		List<NamedValue<SortOrder>> sortOrders = new ArrayList<>(orders.size());

		orders.forEach(order -> sortOrders.add(_translateOrder(order)));

		return sortOrders;
	}

	private ScriptedHeuristic _translateScriptSignificanceHeuristic(
		ScriptSignificanceHeuristic scriptSignificanceHeuristic) {

		return ScriptedHeuristic.of(
			scriptedHeuristic -> scriptedHeuristic.script(
				scriptTranslator.translate(
					scriptSignificanceHeuristic.getScript())));
	}

	private final GeoTranslator _geoTranslator = new GeoTranslator();
	private final HighlightTranslator _highlightTranslator =
		new HighlightTranslator();
	private final SortFieldTranslator<SortOptions> _sortFieldTranslator =
		new ElasticsearchSortFieldTranslator();

}