/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.serdes.v1_0;

import com.liferay.analytics.reports.rest.client.dto.v1_0.Histogram;
import com.liferay.analytics.reports.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.reports.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class HistogramSerDes {

	public static Histogram toDTO(String json) {
		HistogramJSONParser histogramJSONParser = new HistogramJSONParser();

		return histogramJSONParser.parseToDTO(json);
	}

	public static Histogram[] toDTOs(String json) {
		HistogramJSONParser histogramJSONParser = new HistogramJSONParser();

		return histogramJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Histogram histogram) {
		if (histogram == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (histogram.getMetricName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metricName\": ");

			sb.append("\"");

			sb.append(_escape(histogram.getMetricName()));

			sb.append("\"");
		}

		if (histogram.getMetrics() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metrics\": ");

			sb.append("[");

			for (int i = 0; i < histogram.getMetrics().length; i++) {
				sb.append(String.valueOf(histogram.getMetrics()[i]));

				if ((i + 1) < histogram.getMetrics().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (histogram.getTotal() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(histogram.getTotal());
		}

		if (histogram.getTotalValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalValue\": ");

			sb.append(histogram.getTotalValue());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		HistogramJSONParser histogramJSONParser = new HistogramJSONParser();

		return histogramJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Histogram histogram) {
		if (histogram == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (histogram.getMetricName() == null) {
			map.put("metricName", null);
		}
		else {
			map.put("metricName", String.valueOf(histogram.getMetricName()));
		}

		if (histogram.getMetrics() == null) {
			map.put("metrics", null);
		}
		else {
			map.put("metrics", String.valueOf(histogram.getMetrics()));
		}

		if (histogram.getTotal() == null) {
			map.put("total", null);
		}
		else {
			map.put("total", String.valueOf(histogram.getTotal()));
		}

		if (histogram.getTotalValue() == null) {
			map.put("totalValue", null);
		}
		else {
			map.put("totalValue", String.valueOf(histogram.getTotalValue()));
		}

		return map;
	}

	public static class HistogramJSONParser extends BaseJSONParser<Histogram> {

		@Override
		protected Histogram createDTO() {
			return new Histogram();
		}

		@Override
		protected Histogram[] createDTOArray(int size) {
			return new Histogram[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "metricName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "metrics")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalValue")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Histogram histogram, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "metricName")) {
				if (jsonParserFieldValue != null) {
					histogram.setMetricName((String)jsonParserFieldValue);
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

					histogram.setMetrics(metricsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				if (jsonParserFieldValue != null) {
					histogram.setTotal(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalValue")) {
				if (jsonParserFieldValue != null) {
					histogram.setTotalValue(
						Double.valueOf((String)jsonParserFieldValue));
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