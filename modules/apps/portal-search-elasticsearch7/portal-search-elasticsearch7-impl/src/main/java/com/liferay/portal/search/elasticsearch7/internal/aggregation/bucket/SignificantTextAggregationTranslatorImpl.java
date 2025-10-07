/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.bucket.SignificantTextAggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.BaseAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.significance.SignificanceHeuristicTranslator;
import com.liferay.portal.search.query.QueryTranslator;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.SignificantTextAggregationBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = SignificantTextAggregationTranslator.class)
public class SignificantTextAggregationTranslatorImpl
	implements SignificantTextAggregationTranslator {

	@Override
	public SignificantTextAggregationBuilder translate(
		SignificantTextAggregation significantTextAggregation,
		AggregationTranslator<AggregationBuilder> aggregationTranslator,
		PipelineAggregationTranslator<PipelineAggregationBuilder>
			pipelineAggregationTranslator) {

		SignificantTextAggregationBuilder significantTextAggregationBuilder =
			AggregationBuilders.significantText(
				significantTextAggregation.getName(),
				significantTextAggregation.getField());

		if (significantTextAggregation.getBucketCountThresholds() != null) {
			significantTextAggregationBuilder.bucketCountThresholds(
				_bucketCountThresholdsTranslator.translate(
					significantTextAggregation.getBucketCountThresholds()));
		}

		significantTextAggregationBuilder.bucketCountThresholds();

		if (significantTextAggregation.getBackgroundFilterQuery() != null) {
			significantTextAggregationBuilder.backgroundFilter(
				_queryTranslator.translate(
					significantTextAggregation.getBackgroundFilterQuery()));
		}

		if (significantTextAggregation.getFilterDuplicateText() != null) {
			significantTextAggregationBuilder.filterDuplicateText(
				significantTextAggregation.getFilterDuplicateText());
		}

		if (significantTextAggregation.getIncludeExcludeClause() != null) {
			significantTextAggregationBuilder.includeExclude(
				_includeExcludeTranslator.translate(
					significantTextAggregation.getIncludeExcludeClause()));
		}

		if (significantTextAggregation.getMinDocCount() != null) {
			significantTextAggregationBuilder.minDocCount(
				significantTextAggregation.getMinDocCount());
		}

		if (significantTextAggregation.getShardMinDocCount() != null) {
			significantTextAggregationBuilder.shardMinDocCount(
				significantTextAggregation.getShardMinDocCount());
		}

		if (significantTextAggregation.getShardSize() != null) {
			significantTextAggregationBuilder.shardSize(
				significantTextAggregation.getShardSize());
		}

		if (significantTextAggregation.getSignificanceHeuristic() != null) {
			significantTextAggregationBuilder.significanceHeuristic(
				_significanceHeuristicTranslator.translate(
					significantTextAggregation.getSignificanceHeuristic()));
		}

		if (ListUtil.isNotEmpty(significantTextAggregation.getSourceFields())) {
			significantTextAggregationBuilder.sourceFieldNames(
				significantTextAggregation.getSourceFields());
		}

		_baseAggregationTranslator.translate(
			significantTextAggregationBuilder, significantTextAggregation,
			aggregationTranslator, pipelineAggregationTranslator);

		return significantTextAggregationBuilder;
	}

	private final BaseAggregationTranslator _baseAggregationTranslator =
		new BaseAggregationTranslator();
	private final BucketCountThresholdsTranslator
		_bucketCountThresholdsTranslator =
			new BucketCountThresholdsTranslator();
	private final IncludeExcludeTranslator _includeExcludeTranslator =
		new IncludeExcludeTranslator();
	private final QueryTranslator<QueryBuilder> _queryTranslator =
		new ElasticsearchQueryTranslator();
	private final SignificanceHeuristicTranslator
		_significanceHeuristicTranslator =
			new SignificanceHeuristicTranslator();

}