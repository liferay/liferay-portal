/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket;

import com.liferay.portal.search.aggregation.bucket.SignificantTermsAggregation;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.significance.SignificanceHeuristicTranslator;
import com.liferay.portal.search.query.QueryTranslator;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.SignificantTermsAggregationBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = SignificantTermsAggregationTranslator.class)
public class SignificantTermsAggregationTranslatorImpl
	implements SignificantTermsAggregationTranslator {

	@Override
	public SignificantTermsAggregationBuilder translate(
		SignificantTermsAggregation significantTermsAggregation) {

		SignificantTermsAggregationBuilder significantTermsAggregationBuilder =
			AggregationBuilders.significantTerms(
				significantTermsAggregation.getName());

		significantTermsAggregationBuilder.field(
			significantTermsAggregation.getField());

		if (significantTermsAggregation.getBucketCountThresholds() != null) {
			significantTermsAggregationBuilder.bucketCountThresholds(
				_bucketCountThresholdsTranslator.translate(
					significantTermsAggregation.getBucketCountThresholds()));
		}

		if (significantTermsAggregation.getBackgroundFilterQuery() != null) {
			significantTermsAggregationBuilder.backgroundFilter(
				_queryTranslator.translate(
					significantTermsAggregation.getBackgroundFilterQuery()));
		}

		if (significantTermsAggregation.getExecutionHint() != null) {
			significantTermsAggregationBuilder.executionHint(
				significantTermsAggregation.getExecutionHint());
		}

		if (significantTermsAggregation.getIncludeExcludeClause() != null) {
			significantTermsAggregationBuilder.includeExclude(
				_includeExcludeTranslator.translate(
					significantTermsAggregation.getIncludeExcludeClause()));
		}

		if (significantTermsAggregation.getMinDocCount() != null) {
			significantTermsAggregationBuilder.minDocCount(
				significantTermsAggregation.getMinDocCount());
		}

		if (significantTermsAggregation.getShardMinDocCount() != null) {
			significantTermsAggregationBuilder.shardMinDocCount(
				significantTermsAggregation.getShardMinDocCount());
		}

		if (significantTermsAggregation.getShardSize() != null) {
			significantTermsAggregationBuilder.shardSize(
				significantTermsAggregation.getShardSize());
		}

		if (significantTermsAggregation.getSignificanceHeuristic() != null) {
			significantTermsAggregationBuilder.significanceHeuristic(
				_significanceHeuristicTranslator.translate(
					significantTermsAggregation.getSignificanceHeuristic()));
		}

		return significantTermsAggregationBuilder;
	}

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