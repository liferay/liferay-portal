/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.aggregation.bucket;

import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.bucket.FilterAggregation;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.BaseAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.query.QueryTranslator;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = FilterAggregationTranslator.class)
public class FilterAggregationTranslatorImpl
	implements FilterAggregationTranslator {

	@Override
	public FilterAggregationBuilder translate(
		FilterAggregation filterAggregation,
		AggregationTranslator<AggregationBuilder> aggregationTranslator,
		PipelineAggregationTranslator<PipelineAggregationBuilder>
			pipelineAggregationTranslator) {

		QueryBuilder filterQueryBuilder = _queryTranslator.translate(
			filterAggregation.getFilterQuery());

		FilterAggregationBuilder filterAggregationBuilder =
			AggregationBuilders.filter(
				filterAggregation.getName(), filterQueryBuilder);

		_baseAggregationTranslator.translate(
			filterAggregationBuilder, filterAggregation, aggregationTranslator,
			pipelineAggregationTranslator);

		return filterAggregationBuilder;
	}

	private final BaseAggregationTranslator _baseAggregationTranslator =
		new BaseAggregationTranslator();
	private final QueryTranslator<QueryBuilder> _queryTranslator =
		new ElasticsearchQueryTranslator();

}