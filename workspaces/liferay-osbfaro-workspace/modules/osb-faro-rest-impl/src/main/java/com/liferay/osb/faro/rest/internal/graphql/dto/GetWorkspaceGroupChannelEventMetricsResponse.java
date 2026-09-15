/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author Leslie Wong
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetWorkspaceGroupChannelEventMetricsResponse {

	public EventMetric getEventMetric() {
		return _eventMetric;
	}

	public void setEventMetric(EventMetric eventMetric) {
		_eventMetric = eventMetric;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EventMetric {

		public Metric getTotalEventsMetric() {
			return _totalEventsMetric;
		}

		public Metric getTotalSessionsMetric() {
			return _totalSessionsMetric;
		}

		public void setTotalEventsMetric(Metric totalEventsMetric) {
			_totalEventsMetric = totalEventsMetric;
		}

		public void setTotalSessionsMetric(Metric totalSessionsMetric) {
			_totalSessionsMetric = totalSessionsMetric;
		}

		private Metric _totalEventsMetric;
		private Metric _totalSessionsMetric;

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Histogram {

		public List<HistogramMetric> getMetrics() {
			return _metrics;
		}

		public Integer getTotal() {
			return _total;
		}

		public void setMetrics(List<HistogramMetric> metrics) {
			_metrics = metrics;
		}

		public void setTotal(Integer total) {
			_total = total;
		}

		private List<HistogramMetric> _metrics;
		private Integer _total;

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class HistogramMetric {

		public String getKey() {
			return _key;
		}

		public Double getValue() {
			return _value;
		}

		public String getValueKey() {
			return _valueKey;
		}

		public void setKey(String key) {
			_key = key;
		}

		public void setValue(Double value) {
			_value = value;
		}

		public void setValueKey(String valueKey) {
			_valueKey = valueKey;
		}

		private String _key;
		private Double _value;
		private String _valueKey;

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Metric {

		public Histogram getHistogram() {
			return _histogram;
		}

		public Double getPreviousValue() {
			return _previousValue;
		}

		public Trend getTrend() {
			return _trend;
		}

		public Double getValue() {
			return _value;
		}

		public void setHistogram(Histogram histogram) {
			_histogram = histogram;
		}

		public void setPreviousValue(Double previousValue) {
			_previousValue = previousValue;
		}

		public void setTrend(Trend trend) {
			_trend = trend;
		}

		public void setValue(Double value) {
			_value = value;
		}

		private Histogram _histogram;
		private Double _previousValue;
		private Trend _trend;
		private Double _value;

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Trend {

		public Double getPercentage() {
			return _percentage;
		}

		public String getTrendClassification() {
			return _trendClassification;
		}

		public void setPercentage(Double percentage) {
			_percentage = percentage;
		}

		public void setTrendClassification(String trendClassification) {
			_trendClassification = trendClassification;
		}

		private Double _percentage;
		private String _trendClassification;

	}

	private EventMetric _eventMetric;

}