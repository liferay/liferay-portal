/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket;

import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.bucket.FiltersAggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.BaseAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.query.QueryTranslator;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = FiltersAggregationTranslator.class)
public class FiltersAggregationTranslatorImpl
	implements FiltersAggregationTranslator {

	@Override
	public FiltersAggregationBuilder translate(
		FiltersAggregation filtersAggregation,
		AggregationTranslator<AggregationBuilder> aggregationTranslator,
		PipelineAggregationTranslator<PipelineAggregationBuilder>
			pipelineAggregationTranslator) {

		List<FiltersAggregation.KeyedQuery> keyedQueries =
			filtersAggregation.getKeyedQueries();

		List<FiltersAggregator.KeyedFilter> keyedFilters = new ArrayList<>(
			keyedQueries.size());

		keyedQueries.forEach(
			keyedQuery -> {
				QueryBuilder filterQueryBuilder = _queryTranslator.translate(
					keyedQuery.getQuery());

				keyedFilters.add(
					new FiltersAggregator.KeyedFilter(
						keyedQuery.getKey(), filterQueryBuilder));
			});

		FiltersAggregationBuilder filtersAggregationBuilder =
			AggregationBuilders.filters(
				filtersAggregation.getName(),
				keyedFilters.toArray(
					new FiltersAggregator.KeyedFilter[keyedQueries.size()]));

		if (filtersAggregation.getOtherBucket() != null) {
			filtersAggregationBuilder.otherBucket(
				filtersAggregation.getOtherBucket());
		}

		if (filtersAggregation.getOtherBucketKey() != null) {
			filtersAggregationBuilder.otherBucketKey(
				filtersAggregation.getOtherBucketKey());
		}

		_baseAggregationTranslator.translate(
			filtersAggregationBuilder, filtersAggregation,
			aggregationTranslator, pipelineAggregationTranslator);

		return filtersAggregationBuilder;
	}

	private final BaseAggregationTranslator _baseAggregationTranslator =
		new BaseAggregationTranslator();
	private final QueryTranslator<QueryBuilder> _queryTranslator =
		new ElasticsearchQueryTranslator();

}