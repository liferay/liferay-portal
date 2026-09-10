/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.serdes.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceMetric;
import com.liferay.analytics.cms.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class PerformanceMetricSerDes {

	public static PerformanceMetric toDTO(String json) {
		PerformanceMetricJSONParser performanceMetricJSONParser =
			new PerformanceMetricJSONParser();

		return performanceMetricJSONParser.parseToDTO(json);
	}

	public static PerformanceMetric[] toDTOs(String json) {
		PerformanceMetricJSONParser performanceMetricJSONParser =
			new PerformanceMetricJSONParser();

		return performanceMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PerformanceMetric performanceMetric) {
		if (performanceMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (performanceMetric.getMetricType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metricType\": ");

			sb.append("\"");

			sb.append(_escape(performanceMetric.getMetricType()));

			sb.append("\"");
		}

		if (performanceMetric.getMetrics() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metrics\": ");

			sb.append("[");

			for (int i = 0; i < performanceMetric.getMetrics().length; i++) {
				sb.append(String.valueOf(performanceMetric.getMetrics()[i]));

				if ((i + 1) < performanceMetric.getMetrics().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PerformanceMetricJSONParser performanceMetricJSONParser =
			new PerformanceMetricJSONParser();

		return performanceMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PerformanceMetric performanceMetric) {

		if (performanceMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (performanceMetric.getMetricType() == null) {
			map.put("metricType", null);
		}
		else {
			map.put(
				"metricType",
				String.valueOf(performanceMetric.getMetricType()));
		}

		if (performanceMetric.getMetrics() == null) {
			map.put("metrics", null);
		}
		else {
			map.put("metrics", String.valueOf(performanceMetric.getMetrics()));
		}

		return map;
	}

	public static class PerformanceMetricJSONParser
		extends BaseJSONParser<PerformanceMetric> {

		@Override
		protected PerformanceMetric createDTO() {
			return new PerformanceMetric();
		}

		@Override
		protected PerformanceMetric[] createDTOArray(int size) {
			return new PerformanceMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "metricType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "metrics")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PerformanceMetric performanceMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "metricType")) {
				if (jsonParserFieldValue != null) {
					performanceMetric.setMetricType(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metrics")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Metric[] metricsArray =
						new Metric[jsonParserFieldValues.length];

					for (int i = 0; i < metricsArray.length; i++) {
						metricsArray[i] = MetricSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					performanceMetric.setMetrics(metricsArray);
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}
// LIFERAY-REST-BUILDER-HASH:741616935