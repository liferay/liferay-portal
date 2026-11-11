/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.facet;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.DateRangeAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.MultiBucketAggregateBase;
import co.elastic.clients.elasticsearch._types.aggregations.RangeAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.SingleBucketAggregateBase;
import co.elastic.clients.util.TaggedUnionUtils;

import com.liferay.portal.kernel.search.facet.collector.FacetCollector;

import java.util.Map;

/**
 * @author André de Oliveira
 */
public class FacetCollectorFactory {

	public FacetCollector getFacetCollector(Aggregate aggregate, String name) {
		Object object = TaggedUnionUtils.get(aggregate, aggregate._kind());

		if (object instanceof DateRangeAggregate) {
			DateRangeAggregate dateRangeAggregate = aggregate.dateRange();

			return new RangeFacetCollector(dateRangeAggregate.buckets(), name);
		}

		// Order matters

		if (object instanceof RangeAggregate) {
			RangeAggregate rangeAggregate = aggregate.range();

			return new RangeFacetCollector(rangeAggregate.buckets(), name);
		}

		if (object instanceof MultiBucketAggregateBase) {
			MultiBucketAggregateBase multiBucketAggregateBase =
				(MultiBucketAggregateBase)object;

			return new MultiBucketsAggregationFacetCollector(
				name, multiBucketAggregateBase);
		}

		if (object instanceof SingleBucketAggregateBase) {
			SingleBucketAggregateBase singleBucketAggregateBase =
				(SingleBucketAggregateBase)object;

			Map<String, Aggregate> aggregations =
				singleBucketAggregateBase.aggregations();

			return getFacetCollector(aggregations.get(name), name);
		}

		return new ElasticsearchFacetFieldCollector(name);
	}

}