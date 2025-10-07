/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.aggregation;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationTranslator;
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
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationTranslator;
import com.liferay.portal.search.opensearch2.internal.geolocation.GeoTranslator;
import com.liferay.portal.search.opensearch2.internal.highlight.HighlightTranslator;
import com.liferay.portal.search.opensearch2.internal.query.OpenSearchQueryTranslator;
import com.liferay.portal.search.opensearch2.internal.script.ScriptTranslator;
import com.liferay.portal.search.opensearch2.internal.sort.OpenSearchSortFieldTranslator;
import com.liferay.portal.search.opensearch2.internal.util.ConversionUtil;
import com.liferay.portal.search.opensearch2.internal.util.OpenSearchStringUtil;
import com.liferay.portal.search.opensearch2.internal.util.SetterUtil;
import com.liferay.portal.search.query.QueryTranslator;
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

import org.opensearch.client.opensearch._types.GeoHashPrecision;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.Time;
import org.opensearch.client.opensearch._types.TimeUnit;
import org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder.ContainerBuilder;
import org.opensearch.client.opensearch._types.aggregations.AggregationBuilders;
import org.opensearch.client.opensearch._types.aggregations.AggregationRange;
import org.opensearch.client.opensearch._types.aggregations.AverageAggregation;
import org.opensearch.client.opensearch._types.aggregations.Buckets;
import org.opensearch.client.opensearch._types.aggregations.ChiSquareHeuristic;
import org.opensearch.client.opensearch._types.aggregations.DateRangeExpression;
import org.opensearch.client.opensearch._types.aggregations.ExtendedBounds;
import org.opensearch.client.opensearch._types.aggregations.GoogleNormalizedDistanceHeuristic;
import org.opensearch.client.opensearch._types.aggregations.HdrMethod;
import org.opensearch.client.opensearch._types.aggregations.HistogramOrder;
import org.opensearch.client.opensearch._types.aggregations.MutualInformationHeuristic;
import org.opensearch.client.opensearch._types.aggregations.PercentageScoreHeuristic;
import org.opensearch.client.opensearch._types.aggregations.SamplerAggregationExecutionHint;
import org.opensearch.client.opensearch._types.aggregations.ScriptedHeuristic;
import org.opensearch.client.opensearch._types.aggregations.TDigest;
import org.opensearch.client.opensearch._types.aggregations.TermsAggregationCollectMode;
import org.opensearch.client.opensearch._types.aggregations.TermsAggregationExecutionHint;
import org.opensearch.client.opensearch._types.aggregations.TermsExclude;
import org.opensearch.client.opensearch._types.aggregations.TermsInclude;
import org.opensearch.client.opensearch._types.aggregations.WeightedAverageValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.QueryVariant;
import org.opensearch.client.opensearch.core.search.SourceConfig;
import org.opensearch.client.opensearch.core.search.SourceConfigBuilders;
import org.opensearch.client.opensearch.core.search.SourceFilter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Petteri Karttunen
 */
@Component(
	property = "search.engine.impl=OpenSearch",
	service = AggregationTranslator.class
)
public class OpenSearchAggregationTranslator
	implements AggregationTranslator
		<org.opensearch.client.opensearch._types.aggregations.Aggregation>,
			   AggregationVisitor
				   <org.opensearch.client.opensearch._types.aggregations.
					   Aggregation> {

	@Override
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		translate(Aggregation aggregation) {

		return aggregation.accept(this);
	}

	@Override
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(AvgAggregation avgAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(CardinalityAggregation cardinalityAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(ChildrenAggregation childrenAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(DateHistogramAggregation dateHistogramAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
			DateHistogramAggregation.Builder dateHistogramAggregationBuilder =
				AggregationBuilders.dateHistogram();

		if (dateHistogramAggregation.getDateHistogramInterval() != null) {
			dateHistogramAggregationBuilder.interval(
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
					openSearchExtendedBounds -> openSearchExtendedBounds.max(
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
			List<Order> orders = dateHistogramAggregation.getOrders();

			dateHistogramAggregationBuilder.order(
				_toOpenSearchHistogramOrder(orders.get(0)));
		}

		return _translateChildAggregations(
			dateHistogramAggregation,
			aggregationBuilder.dateHistogram(
				dateHistogramAggregationBuilder.build()));
	}

	@Override
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(DateRangeAggregation dateRangeAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(DiversifiedSamplerAggregation diversifiedSamplerAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(ExtendedStatsAggregation extendedStatsAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(FilterAggregation filterAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			builder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		return _translateChildAggregations(
			filterAggregation,
			builder.filter(
				new Query(
					_queryTranslator.translate(
						filterAggregation.getFilterQuery()))));
	}

	@Override
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(FiltersAggregation filtersAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.FiltersAggregation.
			Builder filtersAggregationBuilder = AggregationBuilders.filters();

		List<FiltersAggregation.KeyedQuery> keyedQueries =
			filtersAggregation.getKeyedQueries();

		Map<String, Query> keyedFilters = new HashMap<>();

		keyedQueries.forEach(
			keyedQuery -> keyedFilters.put(
				keyedQuery.getKey(),
				new Query(_queryTranslator.translate(keyedQuery.getQuery()))));

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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(GeoBoundsAggregation geoBoundsAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(GeoCentroidAggregation geoCentroidAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(GeoDistanceAggregation geoDistanceAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
					OpenSearchStringUtil.getFirstStringValue(
						rangeAggregationRange::getFromAsString,
						rangeAggregationRange::getFrom),
					rangeAggregationRange.getKey(),
					OpenSearchStringUtil.getFirstStringValue(
						rangeAggregationRange::getToAsString,
						rangeAggregationRange::getTo))));

		return _translateChildAggregations(
			geoDistanceAggregation,
			aggregationBuilder.geoDistance(
				geoDistanceAggregationBuilder.build()));
	}

	@Override
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(GeoHashGridAggregation geoHashGridAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
			GeoHashGridAggregation.Builder geoHashGridAggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(GlobalAggregation globalAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.GlobalAggregation.
			Builder globalAggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					GlobalAggregation.Builder();

		return _translateChildAggregations(
			globalAggregation,
			aggregationBuilder.global(globalAggregationBuilder.build()));
	}

	@Override
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(HistogramAggregation histogramAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
			HistogramAggregation.Builder histogramAggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					HistogramAggregation.Builder();

		histogramAggregationBuilder.field(histogramAggregation.getField());

		if ((histogramAggregation.getMaxBound() != null) &&
			(histogramAggregation.getMinBound() != null)) {

			histogramAggregationBuilder.extendedBounds(
				ExtendedBounds.of(
					openSearchExtendedBounds -> openSearchExtendedBounds.max(
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
			List<Order> orders = histogramAggregation.getOrders();

			histogramAggregationBuilder.order(
				_toOpenSearchHistogramOrder(orders.get(0)));
		}

		SetterUtil.setNotNullScript(
			histogramAggregationBuilder::script,
			histogramAggregation.getScript());

		return _translateChildAggregations(
			histogramAggregation,
			aggregationBuilder.histogram(histogramAggregationBuilder.build()));
	}

	@Override
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(MaxAggregation maxAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.MaxAggregation.
			Builder maxAggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(MinAggregation minAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.MinAggregation.
			Builder minAggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(MissingAggregation missingAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.MissingAggregation.
			Builder missingAggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(NestedAggregation nestedAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.NestedAggregation.
			Builder nestedAggregationBuilder = AggregationBuilders.nested();

		nestedAggregationBuilder.path(nestedAggregation.getPath());

		return _translateChildAggregations(
			nestedAggregation,
			aggregationBuilder.nested(nestedAggregationBuilder.build()));
	}

	@Override
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(PercentileRanksAggregation percentileRanksAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(PercentilesAggregation percentilesAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(RangeAggregation rangeAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.RangeAggregation.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(ReverseNestedAggregation reverseNestedAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(SamplerAggregation samplerAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.SamplerAggregation.
			Builder samplerAggregationBuilder = AggregationBuilders.sampler();

		SetterUtil.setNotNullInteger(
			samplerAggregationBuilder::shardSize,
			samplerAggregation.getShardSize());

		return _translateChildAggregations(
			samplerAggregation,
			aggregationBuilder.sampler(samplerAggregationBuilder.build()));
	}

	@Override
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(ScriptedMetricAggregation scriptedMetricAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(SignificantTermsAggregation significantTermsAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
					Arrays.asList(includeExcludeClause.getIncludedValues()));
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(SignificantTextAggregation significantTextAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
					Arrays.asList(includeExcludeClause.getIncludedValues()));
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(StatsAggregation statsAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.StatsAggregation.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(SumAggregation sumAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.SumAggregation.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(TermsAggregation termsAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.TermsAggregation.
			Builder termsAggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(TopHitsAggregation topHitsAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.TopHitsAggregation.
			Builder topHitsAggregationBuilder = AggregationBuilders.topHits();

		SetterUtil.setNotEmptyStringList(
			topHitsAggregationBuilder::docvalueFields,
			topHitsAggregation.getSelectedFields());
		SetterUtil.setNotNullBoolean(
			topHitsAggregationBuilder::explain,
			topHitsAggregation.getExplain());
		SetterUtil.setNotNullInteger(
			topHitsAggregationBuilder::from, topHitsAggregation.getFrom());

		if (topHitsAggregation.getHighlight() != null) {
			topHitsAggregationBuilder.highlight(
				_highlightTranslator.translate(
					topHitsAggregation.getHighlight(), _queryTranslator));
		}

		ListUtil.isNotEmptyForEach(
			topHitsAggregation.getScriptFields(),
			scriptField -> topHitsAggregationBuilder.scriptFields(
				scriptField.getField(),
				org.opensearch.client.opensearch._types.ScriptField.of(
					openSearchScriptField ->
						openSearchScriptField.ignoreFailure(
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(ValueCountAggregation valueCountAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
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
	public org.opensearch.client.opensearch._types.aggregations.Aggregation
		visit(WeightedAvgAggregation weightedAvgAggregation) {

		org.opensearch.client.opensearch._types.aggregations.Aggregation.Builder
			aggregationBuilder =
				new org.opensearch.client.opensearch._types.aggregations.
					Aggregation.Builder();

		org.opensearch.client.opensearch._types.aggregations.
			WeightedAverageAggregation.Builder
				weightedAverageAggregationBuilder =
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
			consumer.accept(new Query(_queryTranslator.translate(query)));
		}
	}

	protected void setRanges(
		org.opensearch.client.opensearch._types.aggregations.
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
		org.opensearch.client.opensearch._types.aggregations.RangeAggregation.
			Builder builder,
		RangeAggregation rangeAggregation) {

		List<Range> ranges = rangeAggregation.getRanges();

		ranges.forEach(
			range -> builder.ranges(
				_createAggregationRange(
					OpenSearchStringUtil.getFirstStringValue(
						range::getFromAsString, range::getFrom),
					range.getKey(),
					OpenSearchStringUtil.getFirstStringValue(
						range::getToAsString, range::getTo))));
	}

	protected org.opensearch.client.opensearch._types.aggregations.ValueType
		translateValueType(ValueType valueType) {

		if (valueType == ValueType.BOOLEAN) {
			return org.opensearch.client.opensearch._types.aggregations.
				ValueType.Boolean;
		}
		else if (valueType == ValueType.DATE) {
			return org.opensearch.client.opensearch._types.aggregations.
				ValueType.Date;
		}
		else if (valueType == ValueType.DOUBLE) {
			return org.opensearch.client.opensearch._types.aggregations.
				ValueType.Double;
		}
		else if (valueType == ValueType.GEOPOINT) {
			return org.opensearch.client.opensearch._types.aggregations.
				ValueType.GeoPoint;
		}
		else if (valueType == ValueType.IP) {
			return org.opensearch.client.opensearch._types.aggregations.
				ValueType.Ip;
		}
		else if (valueType == ValueType.LONG) {
			return org.opensearch.client.opensearch._types.aggregations.
				ValueType.Long;
		}
		else if (valueType == ValueType.NUMBER) {
			return org.opensearch.client.opensearch._types.aggregations.
				ValueType.Number;
		}
		else if (valueType == ValueType.NUMERIC) {
			return org.opensearch.client.opensearch._types.aggregations.
				ValueType.Numeric;
		}
		else if (valueType == ValueType.STRING) {
			return org.opensearch.client.opensearch._types.aggregations.
				ValueType.String;
		}

		throw new IllegalArgumentException("Invalid value type " + valueType);
	}

	protected final ScriptTranslator scriptTranslator = new ScriptTranslator();

	private AggregationRange _createAggregationRange(
		String from, String key, String to) {

		AggregationRange.Builder builder = new AggregationRange.Builder();

		if (!Validator.isBlank(from)) {
			builder.from(from);
		}

		if (!Validator.isBlank(key)) {
			builder.key(key);
		}

		if (!Validator.isBlank(to)) {
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

	private HistogramOrder _toOpenSearchHistogramOrder(Order order) {
		SortOrder sortOrder;

		if (order.isAscending()) {
			sortOrder = SortOrder.Asc;
		}
		else {
			sortOrder = SortOrder.Desc;
		}

		if (Order.COUNT_METRIC_NAME.equals(order.getMetricName())) {
			return HistogramOrder.of(
				histogramOrder -> histogramOrder.count(sortOrder));
		}
		else if (Order.KEY_METRIC_NAME.equals(order.getMetricName())) {
			return HistogramOrder.of(
				histogramOrder -> histogramOrder.key(sortOrder));
		}

		throw new IllegalArgumentException("Invalid order " + order);
	}

	private org.opensearch.client.opensearch._types.aggregations.Aggregation
		_translateChildAggregations(
			Aggregation aggregation, ContainerBuilder containerBuilder) {

		for (Aggregation childAggregation :
				aggregation.getChildrenAggregations()) {

			containerBuilder.aggregations(
				childAggregation.getName(), translate(childAggregation));
		}

		for (PipelineAggregation pipelineAggregation :
				aggregation.getPipelineAggregations()) {

			containerBuilder.aggregations(
				pipelineAggregation.getName(),
				_pipelineAggregationTranslator.translate(pipelineAggregation));
		}

		return containerBuilder.build();
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

	private List<Map<String, SortOrder>> _translateOrders(List<Order> orders) {
		List<Map<String, SortOrder>> sortOrdersList = new ArrayList<>();

		orders.forEach(
			order -> {
				Map<String, SortOrder> sortOrders = new HashMap<>();

				SortOrder sortOrder;

				if (order.isAscending()) {
					sortOrder = SortOrder.Asc;
				}
				else {
					sortOrder = SortOrder.Desc;
				}

				if (Order.COUNT_METRIC_NAME.equals(order.getMetricName())) {
					sortOrders.put(Order.COUNT_METRIC_NAME, sortOrder);
				}
				else if (Order.KEY_METRIC_NAME.equals(order.getMetricName())) {
					sortOrders.put(Order.KEY_METRIC_NAME, sortOrder);
				}

				sortOrdersList.add(sortOrders);
			});

		return sortOrdersList;
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

	@Reference(target = "(search.engine.impl=OpenSearch)")
	private PipelineAggregationTranslator
		<org.opensearch.client.opensearch._types.aggregations.Aggregation>
			_pipelineAggregationTranslator;

	private final QueryTranslator<QueryVariant> _queryTranslator =
		new OpenSearchQueryTranslator();
	private final SortFieldTranslator<SortOptions> _sortFieldTranslator =
		new OpenSearchSortFieldTranslator();

}