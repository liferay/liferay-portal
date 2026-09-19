/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util.aggregation;

import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.BucketAggregationResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.junit.Assert;

/**
 * @author André de Oliveira
 */
public class AggregationAssert {

	public static void assertBucketValues(
		String expected, Function<Bucket, Double> function,
		BucketAggregationResult bucketAggregationResult) {

		Assert.assertEquals(
			expected,
			StringUtil.merge(
				bucketAggregationResult.getBuckets(),
				bucket -> String.valueOf(function.apply(bucket)),
				StringPool.COMMA_AND_SPACE));
	}

	public static void assertBuckets(
		String expected, BucketAggregationResult bucketAggregationResult) {

		Assert.assertEquals(
			expected,
			String.valueOf(
				new ArrayList<>(bucketAggregationResult.getBuckets())));
	}

	public static void assertKeyedBuckets(
		BucketAggregationResult bucketAggregationResult, Tuple... tuples) {

		Collection<Bucket> buckets = bucketAggregationResult.getBuckets();

		Assert.assertEquals(buckets.toString(), tuples.length, buckets.size());

		for (Tuple tuple : tuples) {
			Bucket bucket = bucketAggregationResult.getBucket(
				String.valueOf(tuple.getObject(0)));

			Assert.assertEquals(
				bucket.toString(), String.valueOf(tuple.getObject(1)),
				String.valueOf(bucket.getDocCount()));
		}
	}

}