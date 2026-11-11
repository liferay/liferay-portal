/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.facet;

import com.liferay.portal.kernel.search.facet.collector.FacetCollector;

import java.util.Map;

import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.DateRangeAggregate;
import org.opensearch.client.opensearch._types.aggregations.MultiBucketAggregateBase;
import org.opensearch.client.opensearch._types.aggregations.RangeAggregate;
import org.opensearch.client.opensearch._types.aggregations.SingleBucketAggregateBase;
import org.opensearch.client.util.TaggedUnionUtils;

/**
 * @author André de Oliveira
 * @author Petteri Karttunen
 */
public class FacetCollectorFactory {

	public FacetCollector getFacetCollector(Aggregate aggregate, String name) {
		Object object = TaggedUnionUtils.get(aggregate, aggregate._kind());

		if (object instanceof DateRangeAggregate) {
			DateRangeAggregate dateRangeAggregate = aggregate.dateRange();

			return new RangeFacetCollector(name, dateRangeAggregate.buckets());
		}

		// Order matters

		if (object instanceof RangeAggregate) {
			RangeAggregate rangeAggregate = aggregate.range();

			return new RangeFacetCollector(name, rangeAggregate.buckets());
		}

		if (object instanceof MultiBucketAggregateBase) {
			MultiBucketAggregateBase multiBucketAggregateBase =
				(MultiBucketAggregateBase)object;

			return new MultiBucketsAggregationFacetCollector(
				multiBucketAggregateBase, name);
		}

		if (object instanceof SingleBucketAggregateBase) {
			SingleBucketAggregateBase singleBucketAggregateBase =
				(SingleBucketAggregateBase)object;

			Map<String, Aggregate> aggregations =
				singleBucketAggregateBase.aggregations();

			return getFacetCollector(aggregations.get(name), name);
		}

		return new OpenSearchFacetFieldCollector(name);
	}

}