/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.EventMetric;
import com.liferay.osb.faro.rest.dto.v1_0.HistogramBucket;
import com.liferay.osb.faro.rest.dto.v1_0.Metric;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventMetricsResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.lang.reflect.Field;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class EventMetricDTOConverterTest {

	@Before
	public void setUp() throws Exception {
		Field field = EventMetricDTOConverter.class.getDeclaredField(
			"_metricDTOConverter");

		field.setAccessible(true);

		field.set(_eventMetricDTOConverter, new MetricDTOConverter());
	}

	@Test
	public void testToDTO() {
		Assert.assertNull(
			_eventMetricDTOConverter.toDTO(
				new FaroDTOConverterContext(false, null, null), null));

		_testToDTOWithOptionalValues();
		_testToDTOWithoutOptionalValues();
	}

	private GetWorkspaceGroupChannelEventMetricsResponse.Metric _createMetric(
		Double value) {

		GetWorkspaceGroupChannelEventMetricsResponse.Metric metric =
			new GetWorkspaceGroupChannelEventMetricsResponse.Metric();

		metric.setValue(value);

		return metric;
	}

	private GetWorkspaceGroupChannelEventMetricsResponse.Metric _createMetric(
		String[] bucketKeys, Double[] bucketValues, Double percentage,
		Double previousValue, String trendClassification, Double value) {

		GetWorkspaceGroupChannelEventMetricsResponse.Metric metric =
			_createMetric(value);

		if ((bucketKeys != null) && (bucketValues != null)) {
			GetWorkspaceGroupChannelEventMetricsResponse.Histogram histogram =
				new GetWorkspaceGroupChannelEventMetricsResponse.Histogram();

			GetWorkspaceGroupChannelEventMetricsResponse.HistogramMetric[]
				histogramMetrics =
					new
					GetWorkspaceGroupChannelEventMetricsResponse.HistogramMetric
						[bucketKeys.length];

			for (int i = 0; i < bucketKeys.length; i++) {
				histogramMetrics[i] =
					new GetWorkspaceGroupChannelEventMetricsResponse.
						HistogramMetric();

				histogramMetrics[i].setKey(bucketKeys[i]);
				histogramMetrics[i].setValue(bucketValues[i]);
				histogramMetrics[i].setValueKey(RandomTestUtil.randomString());
			}

			histogram.setMetrics(Arrays.asList(histogramMetrics));
			histogram.setTotal(bucketKeys.length);

			metric.setHistogram(histogram);
		}

		metric.setPreviousValue(previousValue);

		GetWorkspaceGroupChannelEventMetricsResponse.Trend trend =
			new GetWorkspaceGroupChannelEventMetricsResponse.Trend();

		trend.setPercentage(percentage);
		trend.setTrendClassification(trendClassification);

		metric.setTrend(trend);

		return metric;
	}

	private void _testToDTOWithOptionalValues() {
		String[] bucketKeys = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		};
		Double[] bucketValues = {
			RandomTestUtil.randomDouble(), RandomTestUtil.randomDouble()
		};
		Double totalEventsPreviousValue = RandomTestUtil.randomDouble();
		String totalEventsTrendClassification = RandomTestUtil.randomString();
		Double totalEventsTrendPercentage = RandomTestUtil.randomDouble();
		Double totalEventsValue = RandomTestUtil.randomDouble();
		String totalSessionsTrendClassification = RandomTestUtil.randomString();
		Double totalSessionsValue = RandomTestUtil.randomDouble();

		EventMetric eventMetric = _toDTO(
			_createMetric(
				bucketKeys, bucketValues, totalEventsTrendPercentage,
				totalEventsPreviousValue, totalEventsTrendClassification,
				totalEventsValue),
			_createMetric(
				null, null, RandomTestUtil.randomDouble(),
				RandomTestUtil.randomDouble(), totalSessionsTrendClassification,
				totalSessionsValue));

		Metric totalEventsMetric = eventMetric.getTotalEvents();

		Assert.assertEquals(
			totalEventsPreviousValue, totalEventsMetric.getPreviousValue());
		Assert.assertEquals(
			totalEventsTrendClassification,
			totalEventsMetric.getTrendClassification());
		Assert.assertEquals(
			totalEventsTrendPercentage, totalEventsMetric.getTrendPercentage());
		Assert.assertEquals(totalEventsValue, totalEventsMetric.getValue());

		HistogramBucket[] histogramBuckets =
			totalEventsMetric.getHistogramBuckets();

		Assert.assertEquals(
			Arrays.toString(histogramBuckets), bucketKeys.length,
			histogramBuckets.length);

		HistogramBucket histogramBucket = histogramBuckets[0];

		Assert.assertEquals(bucketKeys[0], histogramBucket.getKey());
		Assert.assertEquals(bucketValues[0], histogramBucket.getValue());

		histogramBucket = histogramBuckets[1];

		Assert.assertEquals(bucketKeys[1], histogramBucket.getKey());
		Assert.assertEquals(bucketValues[1], histogramBucket.getValue());

		Metric totalSessionsMetric = eventMetric.getTotalSessions();

		Assert.assertNull(totalSessionsMetric.getHistogramBuckets());
		Assert.assertEquals(
			totalSessionsTrendClassification,
			totalSessionsMetric.getTrendClassification());
		Assert.assertEquals(totalSessionsValue, totalSessionsMetric.getValue());
	}

	private void _testToDTOWithoutOptionalValues() {
		Double totalEventsValue = RandomTestUtil.randomDouble();

		EventMetric eventMetric = _toDTO(_createMetric(totalEventsValue), null);

		Assert.assertNull(eventMetric.getTotalSessions());

		Metric totalEventsMetric = eventMetric.getTotalEvents();

		Assert.assertNull(totalEventsMetric.getHistogramBuckets());
		Assert.assertNull(totalEventsMetric.getPreviousValue());
		Assert.assertNull(totalEventsMetric.getTrendClassification());
		Assert.assertNull(totalEventsMetric.getTrendPercentage());
		Assert.assertEquals(totalEventsValue, totalEventsMetric.getValue());
	}

	private EventMetric _toDTO(
		GetWorkspaceGroupChannelEventMetricsResponse.Metric totalEventsMetric,
		GetWorkspaceGroupChannelEventMetricsResponse.Metric
			totalSessionsMetric) {

		GetWorkspaceGroupChannelEventMetricsResponse.EventMetric eventMetric =
			new GetWorkspaceGroupChannelEventMetricsResponse.EventMetric();

		eventMetric.setTotalEventsMetric(totalEventsMetric);
		eventMetric.setTotalSessionsMetric(totalSessionsMetric);

		return _eventMetricDTOConverter.toDTO(
			new FaroDTOConverterContext(
				false, RandomTestUtil.randomString(), null),
			eventMetric);
	}

	private final EventMetricDTOConverter _eventMetricDTOConverter =
		new EventMetricDTOConverter();

}