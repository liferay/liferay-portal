/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.util;

import com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChangeAggregation;
import com.liferay.osb.faro.rest.dto.v1_0.HistogramBucket;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembershipChangeMetric;
import com.liferay.osb.faro.rest.dto.v1_0.Metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class IndividualSegmentMembershipChangeMetricUtilTest {

	@Test
	public void testToIndividualSegmentMembershipChangeMetric() {
		IndividualSegmentMembershipChangeMetric
			individualSegmentMembershipChangeMetric =
				IndividualSegmentMembershipChangeMetricUtil.
					toIndividualSegmentMembershipChangeMetric(
						3, _aggregations());

		Metric individualsMetric =
			individualSegmentMembershipChangeMetric.getIndividuals();

		Assert.assertEquals(Double.valueOf(13), individualsMetric.getValue());
		Assert.assertEquals(
			Double.valueOf(11), individualsMetric.getPreviousValue());
		Assert.assertEquals(
			"POSITIVE", individualsMetric.getTrendClassification());

		Metric knownIndividualsMetric =
			individualSegmentMembershipChangeMetric.getKnownIndividuals();

		Assert.assertEquals(
			Double.valueOf(10), knownIndividualsMetric.getValue());
		Assert.assertEquals(
			Double.valueOf(9), knownIndividualsMetric.getPreviousValue());

		HistogramBucket[] histogramBuckets =
			individualsMetric.getHistogramBuckets();

		Assert.assertEquals(
			Arrays.toString(histogramBuckets), 3, histogramBuckets.length);

		Assert.assertEquals(
			"2026-09-14T00:00:00Z", histogramBuckets[0].getKey());
		Assert.assertEquals(Double.valueOf(15), histogramBuckets[0].getValue());
		Assert.assertEquals(
			"2026-09-16T00:00:00Z", histogramBuckets[2].getKey());
		Assert.assertEquals(Double.valueOf(13), histogramBuckets[2].getValue());

		individualSegmentMembershipChangeMetric =
			IndividualSegmentMembershipChangeMetricUtil.
				toIndividualSegmentMembershipChangeMetric(30, _aggregations());

		individualsMetric =
			individualSegmentMembershipChangeMetric.getIndividuals();

		Assert.assertEquals(Double.valueOf(13), individualsMetric.getValue());
		Assert.assertNull(individualsMetric.getPreviousValue());
		Assert.assertNull(individualsMetric.getTrendClassification());
		Assert.assertNull(individualsMetric.getTrendPercentage());
		histogramBuckets = individualsMetric.getHistogramBuckets();

		Assert.assertEquals(
			Arrays.toString(histogramBuckets), 6, histogramBuckets.length);

		individualSegmentMembershipChangeMetric =
			IndividualSegmentMembershipChangeMetricUtil.
				toIndividualSegmentMembershipChangeMetric(
					30, Collections.emptyList());

		individualsMetric =
			individualSegmentMembershipChangeMetric.getIndividuals();

		Assert.assertNull(individualsMetric.getValue());
		Assert.assertNull(individualsMetric.getPreviousValue());
		Assert.assertNull(individualsMetric.getTrendClassification());
		Assert.assertNull(individualsMetric.getTrendPercentage());
		histogramBuckets = individualsMetric.getHistogramBuckets();

		Assert.assertEquals(
			Arrays.toString(histogramBuckets), 0, histogramBuckets.length);
	}

	private IndividualSegmentMembershipChangeAggregation _aggregation(
		long individualsCount, long intervalInitDate,
		long knownIndividualsCount) {

		IndividualSegmentMembershipChangeAggregation
			individualSegmentMembershipChangeAggregation =
				new IndividualSegmentMembershipChangeAggregation();

		individualSegmentMembershipChangeAggregation.setIndividualsCount(
			individualsCount);
		individualSegmentMembershipChangeAggregation.setIntervalInitDate(
			new Date(intervalInitDate));
		individualSegmentMembershipChangeAggregation.setKnownIndividualsCount(
			knownIndividualsCount);

		return individualSegmentMembershipChangeAggregation;
	}

	private List<IndividualSegmentMembershipChangeAggregation> _aggregations() {
		return Arrays.asList(
			_aggregation(10, 1789084800000L, 8),
			_aggregation(12, 1789171200000L, 9),
			_aggregation(11, 1789257600000L, 9),
			_aggregation(15, 1789344000000L, 12),
			_aggregation(15, 1789430400000L, 12),
			_aggregation(13, 1789516800000L, 10));
	}

}