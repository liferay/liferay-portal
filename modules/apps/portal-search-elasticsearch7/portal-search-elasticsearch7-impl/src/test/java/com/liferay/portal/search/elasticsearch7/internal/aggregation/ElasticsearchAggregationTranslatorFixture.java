/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.aggregation;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.DateHistogramAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.DateRangeAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.FilterAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.FilterAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.FiltersAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.FiltersAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.GeoDistanceAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.HistogramAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.RangeAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.SignificantTermsAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.SignificantTermsAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.SignificantTextAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.SignificantTextAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket.TermsAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.metrics.ScriptedMetricAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.metrics.TopHitsAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.metrics.TopHitsAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.metrics.WeightedAvgAggregationTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.pipeline.ElasticsearchPipelineAggregationTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.sort.ElasticsearchSortFieldTranslatorFixture;

import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;

/**
 * @author Michael C. Han
 */
public class ElasticsearchAggregationTranslatorFixture {

	public ElasticsearchAggregationTranslatorFixture() {
		ElasticsearchPipelineAggregationTranslatorFixture
			pipelineAggregationTranslatorFixture =
				new ElasticsearchPipelineAggregationTranslatorFixture();

		ElasticsearchQueryTranslator elasticsearchQueryTranslator =
			new ElasticsearchQueryTranslator();

		PipelineAggregationTranslator<PipelineAggregationBuilder>
			pipelineAggregationTranslator =
				pipelineAggregationTranslatorFixture.
					getElasticsearchPipelineAggregationTranslator();

		AggregationBuilderAssemblerFactory aggregationBuilderAssemblerFactory =
			new AggregationBuilderAssemblerFactoryImpl();

		ReflectionTestUtil.setFieldValue(
			aggregationBuilderAssemblerFactory,
			"_pipelineAggregationTranslator", pipelineAggregationTranslator);

		ElasticsearchAggregationTranslator elasticsearchAggregationTranslator =
			new ElasticsearchAggregationTranslator();

		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator,
			"_aggregationBuilderAssemblerFactory",
			aggregationBuilderAssemblerFactory);
		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator,
			"_dateHistogramAggregationTranslator",
			new DateHistogramAggregationTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator,
			"_dateRangeAggregationTranslator",
			new DateRangeAggregationTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator,
			"_histogramAggregationTranslator",
			new HistogramAggregationTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator,
			"_pipelineAggregationTranslator", pipelineAggregationTranslator);
		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator, "_rangeAggregationTranslator",
			new RangeAggregationTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator, "_termsAggregationTranslator",
			new TermsAggregationTranslatorImpl());

		_injectGeoAggregationTranslators(elasticsearchAggregationTranslator);
		_injectQueryAggregationTranslators(
			elasticsearchAggregationTranslator, elasticsearchQueryTranslator);
		_injectScriptAggregationTranslators(elasticsearchAggregationTranslator);
		_injectTopHitsAggregationTranslators(
			elasticsearchAggregationTranslator, elasticsearchQueryTranslator);

		_elasticsearchAggregationTranslator =
			elasticsearchAggregationTranslator;
	}

	public ElasticsearchAggregationTranslator
		getElasticsearchAggregationTranslator() {

		return _elasticsearchAggregationTranslator;
	}

	private void _injectGeoAggregationTranslators(
		ElasticsearchAggregationTranslator elasticsearchAggregationTranslator) {

		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator,
			"_geoDistanceAggregationTranslator",
			new GeoDistanceAggregationTranslatorImpl());
	}

	private void _injectQueryAggregationTranslators(
		ElasticsearchAggregationTranslator elasticsearchAggregationTranslator,
		ElasticsearchQueryTranslator elasticsearchQueryTranslator) {

		FilterAggregationTranslator filterAggregationTranslator =
			new FilterAggregationTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			filterAggregationTranslator, "_queryTranslator",
			elasticsearchQueryTranslator);

		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator, "_filterAggregationTranslator",
			filterAggregationTranslator);

		FiltersAggregationTranslator filtersAggregationTranslator =
			new FiltersAggregationTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			filtersAggregationTranslator, "_queryTranslator",
			elasticsearchQueryTranslator);

		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator, "_filtersAggregationTranslator",
			filtersAggregationTranslator);

		SignificantTermsAggregationTranslator
			significantTermsAggregationTranslator =
				new SignificantTermsAggregationTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			significantTermsAggregationTranslator, "_queryTranslator",
			elasticsearchQueryTranslator);

		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator,
			"_significantTermsAggregationTranslator",
			significantTermsAggregationTranslator);

		SignificantTextAggregationTranslator
			significantTextAggregationTranslator =
				new SignificantTextAggregationTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			significantTextAggregationTranslator, "_queryTranslator",
			elasticsearchQueryTranslator);

		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator,
			"_significantTextAggregationTranslator",
			significantTextAggregationTranslator);
	}

	private void _injectScriptAggregationTranslators(
		ElasticsearchAggregationTranslator elasticsearchAggregationTranslator) {

		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator,
			"_scriptedMetricAggregationTranslator",
			new ScriptedMetricAggregationTranslatorImpl());
		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator,
			"_weightedAvgAggregationTranslator",
			new WeightedAvgAggregationTranslatorImpl());
	}

	private void _injectTopHitsAggregationTranslators(
		ElasticsearchAggregationTranslator elasticsearchAggregationTranslator,
		ElasticsearchQueryTranslator elasticsearchQueryTranslator) {

		ElasticsearchSortFieldTranslatorFixture
			elasticsearchSortFieldTranslatorFixture =
				new ElasticsearchSortFieldTranslatorFixture(
					elasticsearchQueryTranslator);

		TopHitsAggregationTranslator topHitsAggregationTranslator =
			new TopHitsAggregationTranslatorImpl();

		ReflectionTestUtil.setFieldValue(
			topHitsAggregationTranslator, "_queryTranslator",
			elasticsearchQueryTranslator);
		ReflectionTestUtil.setFieldValue(
			topHitsAggregationTranslator, "_sortFieldTranslator",
			elasticsearchSortFieldTranslatorFixture.
				getElasticsearchSortFieldTranslator());

		ReflectionTestUtil.setFieldValue(
			elasticsearchAggregationTranslator, "_topHitsAggregationTranslator",
			topHitsAggregationTranslator);
	}

	private final ElasticsearchAggregationTranslator
		_elasticsearchAggregationTranslator;

}