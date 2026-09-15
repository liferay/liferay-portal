/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.HistogramBucket;
import com.liferay.osb.faro.rest.client.dto.v1_0.Metric;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class MetricSerDes {

	public static Metric toDTO(String json) {
		MetricJSONParser metricJSONParser = new MetricJSONParser();

		return metricJSONParser.parseToDTO(json);
	}

	public static Metric[] toDTOs(String json) {
		MetricJSONParser metricJSONParser = new MetricJSONParser();

		return metricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Metric metric) {
		if (metric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (metric.getHistogramBuckets() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"histogramBuckets\": ");

			sb.append("[");

			for (int i = 0; i < metric.getHistogramBuckets().length; i++) {
				sb.append(String.valueOf(metric.getHistogramBuckets()[i]));

				if ((i + 1) < metric.getHistogramBuckets().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (metric.getPreviousValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previousValue\": ");

			sb.append(metric.getPreviousValue());
		}

		if (metric.getTrendClassification() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trendClassification\": ");

			sb.append("\"");

			sb.append(_escape(metric.getTrendClassification()));

			sb.append("\"");
		}

		if (metric.getTrendPercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trendPercentage\": ");

			sb.append(metric.getTrendPercentage());
		}

		if (metric.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append(metric.getValue());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MetricJSONParser metricJSONParser = new MetricJSONParser();

		return metricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Metric metric) {
		if (metric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (metric.getHistogramBuckets() == null) {
			map.put("histogramBuckets", null);
		}
		else {
			map.put(
				"histogramBuckets",
				String.valueOf(metric.getHistogramBuckets()));
		}

		if (metric.getPreviousValue() == null) {
			map.put("previousValue", null);
		}
		else {
			map.put("previousValue", String.valueOf(metric.getPreviousValue()));
		}

		if (metric.getTrendClassification() == null) {
			map.put("trendClassification", null);
		}
		else {
			map.put(
				"trendClassification",
				String.valueOf(metric.getTrendClassification()));
		}

		if (metric.getTrendPercentage() == null) {
			map.put("trendPercentage", null);
		}
		else {
			map.put(
				"trendPercentage", String.valueOf(metric.getTrendPercentage()));
		}

		if (metric.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(metric.getValue()));
		}

		return map;
	}

	public static class MetricJSONParser extends BaseJSONParser<Metric> {

		@Override
		protected Metric createDTO() {
			return new Metric();
		}

		@Override
		protected Metric[] createDTOArray(int size) {
			return new Metric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "histogramBuckets")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "previousValue")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "trendClassification")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "trendPercentage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Metric metric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "histogramBuckets")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					HistogramBucket[] histogramBucketsArray =
						new HistogramBucket[jsonParserFieldValues.length];

					for (int i = 0; i < histogramBucketsArray.length; i++) {
						histogramBucketsArray[i] = HistogramBucketSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					metric.setHistogramBuckets(histogramBucketsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "previousValue")) {
				if (jsonParserFieldValue != null) {
					metric.setPreviousValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "trendClassification")) {

				if (jsonParserFieldValue != null) {
					metric.setTrendClassification((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "trendPercentage")) {
				if (jsonParserFieldValue != null) {
					metric.setTrendPercentage(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					metric.setValue(
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
// LIFERAY-REST-BUILDER-HASH:-1524259695