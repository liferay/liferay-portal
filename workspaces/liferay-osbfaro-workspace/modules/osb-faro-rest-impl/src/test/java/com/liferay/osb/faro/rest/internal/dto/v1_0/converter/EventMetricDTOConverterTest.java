/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.EventMetric;
import com.liferay.osb.faro.rest.dto.v1_0.HistogramBucket;
import com.liferay.osb.faro.rest.dto.v1_0.Metric;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventMetricsResponse;

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
	public void testToDTOFlattensTrendAndMapsHistogram() {
		GetWorkspaceGroupChannelEventMetricsResponse.EventMetric eventMetric =
			new GetWorkspaceGroupChannelEventMetricsResponse.EventMetric();

		eventMetric.setTotalEventsMetric(
			_createMetric(36D, 48D, -25D, "NEGATIVE", 12D, 24D));
		eventMetric.setTotalSessionsMetric(
			_createMetric(4D, 2D, 100D, "POSITIVE"));

		EventMetric eventMetricDTO = _eventMetricDTOConverter.toDTO(
			new FaroDTOConverterContext(false, "account-1", null), eventMetric);

		Metric totalEvents = eventMetricDTO.getTotalEvents();

		Assert.assertEquals(
			Double.valueOf(48D), totalEvents.getPreviousValue());
		Assert.assertEquals("NEGATIVE", totalEvents.getTrendClassification());
		Assert.assertEquals(
			Double.valueOf(-25D), totalEvents.getTrendPercentage());
		Assert.assertEquals(Double.valueOf(36D), totalEvents.getValue());

		HistogramBucket[] histogramBuckets = totalEvents.getHistogramBuckets();

		Assert.assertEquals(
			Arrays.toString(histogramBuckets), 2, histogramBuckets.length);
		Assert.assertEquals("2026-07-01T00:00", histogramBuckets[0].getKey());
		Assert.assertEquals(
			Double.valueOf(12D), histogramBuckets[0].getValue());
		Assert.assertEquals("2026-08-01T00:00", histogramBuckets[1].getKey());
		Assert.assertEquals(
			Double.valueOf(24D), histogramBuckets[1].getValue());

		Metric totalSessions = eventMetricDTO.getTotalSessions();

		Assert.assertNull(totalSessions.getHistogramBuckets());
		Assert.assertEquals("POSITIVE", totalSessions.getTrendClassification());
		Assert.assertEquals(Double.valueOf(4D), totalSessions.getValue());
	}

	@Test
	public void testToDTOLeavesTrendNullWhenAbsent() {
		GetWorkspaceGroupChannelEventMetricsResponse.EventMetric eventMetric =
			new GetWorkspaceGroupChannelEventMetricsResponse.EventMetric();

		GetWorkspaceGroupChannelEventMetricsResponse.Metric metric =
			new GetWorkspaceGroupChannelEventMetricsResponse.Metric();

		metric.setValue(7D);

		eventMetric.setTotalEventsMetric(metric);

		EventMetric eventMetricDTO = _eventMetricDTOConverter.toDTO(
			new FaroDTOConverterContext(false, "account-1", null), eventMetric);

		Metric totalEvents = eventMetricDTO.getTotalEvents();

		Assert.assertNull(totalEvents.getHistogramBuckets());
		Assert.assertNull(totalEvents.getPreviousValue());
		Assert.assertNull(totalEvents.getTrendClassification());
		Assert.assertNull(totalEvents.getTrendPercentage());
		Assert.assertEquals(Double.valueOf(7D), totalEvents.getValue());

		Assert.assertNull(eventMetricDTO.getTotalSessions());
	}

	@Test
	public void testToDTOReturnsNullForNullEventMetric() {
		Assert.assertNull(
			_eventMetricDTOConverter.toDTO(
				new FaroDTOConverterContext(false, null, null), null));
	}

	private GetWorkspaceGroupChannelEventMetricsResponse.Metric _createMetric(
		Double value, Double previousValue, Double percentage,
		String trendClassification, Double... bucketValues) {

		GetWorkspaceGroupChannelEventMetricsResponse.Metric metric =
			new GetWorkspaceGroupChannelEventMetricsResponse.Metric();

		if (bucketValues.length > 0) {
			GetWorkspaceGroupChannelEventMetricsResponse.Histogram histogram =
				new GetWorkspaceGroupChannelEventMetricsResponse.Histogram();

			String[] keys = {"2026-07-01T00:00", "2026-08-01T00:00"};

			GetWorkspaceGroupChannelEventMetricsResponse.HistogramMetric[]
				histogramMetrics =
					new
					GetWorkspaceGroupChannelEventMetricsResponse.HistogramMetric
						[bucketValues.length];

			for (int i = 0; i < bucketValues.length; i++) {
				histogramMetrics[i] =
					new GetWorkspaceGroupChannelEventMetricsResponse.
						HistogramMetric();

				histogramMetrics[i].setKey(keys[i]);
				histogramMetrics[i].setValue(bucketValues[i]);
				histogramMetrics[i].setValueKey(keys[i]);
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

	private final EventMetricDTOConverter _eventMetricDTOConverter =
		new EventMetricDTOConverter();

}