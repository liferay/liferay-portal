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
import java.util.List;

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
		GetWorkspaceGroupChannelEventMetricsResponse.EventMetric
			responseEventMetric =
				new GetWorkspaceGroupChannelEventMetricsResponse.EventMetric();

		GetWorkspaceGroupChannelEventMetricsResponse.Metric
			responseTotalEventsMetric = _createMetric(
				RandomTestUtil.randomDouble(), RandomTestUtil.randomDouble(),
				RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
				RandomTestUtil.randomDouble(), RandomTestUtil.randomDouble());
		GetWorkspaceGroupChannelEventMetricsResponse.Metric
			responseTotalSessionsMetric = _createMetric(
				RandomTestUtil.randomDouble(), RandomTestUtil.randomDouble(),
				RandomTestUtil.randomString(), RandomTestUtil.randomDouble());

		responseEventMetric.setTotalEventsMetric(responseTotalEventsMetric);
		responseEventMetric.setTotalSessionsMetric(responseTotalSessionsMetric);

		EventMetric eventMetric = _toDTO(responseEventMetric);

		Metric totalEventsMetric = eventMetric.getTotalEvents();

		GetWorkspaceGroupChannelEventMetricsResponse.Trend responseTrend =
			responseTotalEventsMetric.getTrend();

		Assert.assertEquals(
			responseTotalEventsMetric.getPreviousValue(),
			totalEventsMetric.getPreviousValue());
		Assert.assertEquals(
			responseTrend.getTrendClassification(),
			totalEventsMetric.getTrendClassification());
		Assert.assertEquals(
			responseTrend.getPercentage(),
			totalEventsMetric.getTrendPercentage());
		Assert.assertEquals(
			responseTotalEventsMetric.getValue(), totalEventsMetric.getValue());

		HistogramBucket[] histogramBuckets =
			totalEventsMetric.getHistogramBuckets();

		GetWorkspaceGroupChannelEventMetricsResponse.Histogram
			responseHistogram = responseTotalEventsMetric.getHistogram();

		List<GetWorkspaceGroupChannelEventMetricsResponse.HistogramMetric>
			responseHistogramMetrics = responseHistogram.getMetrics();

		Assert.assertEquals(
			Arrays.toString(histogramBuckets), responseHistogramMetrics.size(),
			histogramBuckets.length);

		for (int i = 0; i < histogramBuckets.length; i++) {
			GetWorkspaceGroupChannelEventMetricsResponse.HistogramMetric
				responseHistogramMetric = responseHistogramMetrics.get(i);

			Assert.assertEquals(
				responseHistogramMetric.getKey(), histogramBuckets[i].getKey());
			Assert.assertEquals(
				responseHistogramMetric.getValue(),
				histogramBuckets[i].getValue());
		}

		Metric totalSessionsMetric = eventMetric.getTotalSessions();

		responseTrend = responseTotalSessionsMetric.getTrend();

		Assert.assertNull(totalSessionsMetric.getHistogramBuckets());
		Assert.assertEquals(
			responseTrend.getTrendClassification(),
			totalSessionsMetric.getTrendClassification());
		Assert.assertEquals(
			responseTotalSessionsMetric.getValue(),
			totalSessionsMetric.getValue());

		responseEventMetric =
			new GetWorkspaceGroupChannelEventMetricsResponse.EventMetric();

		GetWorkspaceGroupChannelEventMetricsResponse.Metric responseMetric =
			new GetWorkspaceGroupChannelEventMetricsResponse.Metric();

		responseMetric.setValue(RandomTestUtil.randomDouble());

		responseEventMetric.setTotalEventsMetric(responseMetric);

		eventMetric = _toDTO(responseEventMetric);

		Assert.assertNull(eventMetric.getTotalSessions());

		totalEventsMetric = eventMetric.getTotalEvents();

		Assert.assertNull(totalEventsMetric.getHistogramBuckets());
		Assert.assertNull(totalEventsMetric.getPreviousValue());
		Assert.assertNull(totalEventsMetric.getTrendClassification());
		Assert.assertNull(totalEventsMetric.getTrendPercentage());
		Assert.assertEquals(
			responseMetric.getValue(), totalEventsMetric.getValue());

		Assert.assertNull(
			_eventMetricDTOConverter.toDTO(
				new FaroDTOConverterContext(false, null, null), null));
	}

	private GetWorkspaceGroupChannelEventMetricsResponse.Metric _createMetric(
		Double percentage, Double previousValue, String trendClassification,
		Double value, Double... bucketValues) {

		GetWorkspaceGroupChannelEventMetricsResponse.Metric metric =
			new GetWorkspaceGroupChannelEventMetricsResponse.Metric();

		if (bucketValues.length > 0) {
			GetWorkspaceGroupChannelEventMetricsResponse.Histogram histogram =
				new GetWorkspaceGroupChannelEventMetricsResponse.Histogram();

			GetWorkspaceGroupChannelEventMetricsResponse.HistogramMetric[]
				histogramMetrics =
					new
					GetWorkspaceGroupChannelEventMetricsResponse.HistogramMetric
						[bucketValues.length];

			for (int i = 0; i < bucketValues.length; i++) {
				histogramMetrics[i] =
					new GetWorkspaceGroupChannelEventMetricsResponse.
						HistogramMetric();

				histogramMetrics[i].setKey(RandomTestUtil.randomString());
				histogramMetrics[i].setValue(bucketValues[i]);
				histogramMetrics[i].setValueKey(RandomTestUtil.randomString());
			}

			histogram.setMetrics(Arrays.asList(histogramMetrics));
			histogram.setTotal(bucketValues.length);

			metric.setHistogram(histogram);
		}

		metric.setPreviousValue(previousValue);

		GetWorkspaceGroupChannelEventMetricsResponse.Trend trend =
			new GetWorkspaceGroupChannelEventMetricsResponse.Trend();

		trend.setPercentage(percentage);
		trend.setTrendClassification(trendClassification);

		metric.setTrend(trend);

		metric.setValue(value);

		return metric;
	}

	private EventMetric _toDTO(
		GetWorkspaceGroupChannelEventMetricsResponse.EventMetric
			responseEventMetric) {

		return _eventMetricDTOConverter.toDTO(
			new FaroDTOConverterContext(
				false, RandomTestUtil.randomString(), null),
			responseEventMetric);
	}

	private final EventMetricDTOConverter _eventMetricDTOConverter =
		new EventMetricDTOConverter();

}