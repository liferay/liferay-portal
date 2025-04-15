/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.serdes.v1_0;

import com.liferay.analytics.reports.rest.client.dto.v1_0.AppearsOnHistogram;
import com.liferay.analytics.reports.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.reports.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
public class AppearsOnHistogramSerDes {

	public static AppearsOnHistogram toDTO(String json) {
		AppearsOnHistogramJSONParser appearsOnHistogramJSONParser =
			new AppearsOnHistogramJSONParser();

		return appearsOnHistogramJSONParser.parseToDTO(json);
	}

	public static AppearsOnHistogram[] toDTOs(String json) {
		AppearsOnHistogramJSONParser appearsOnHistogramJSONParser =
			new AppearsOnHistogramJSONParser();

		return appearsOnHistogramJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AppearsOnHistogram appearsOnHistogram) {
		if (appearsOnHistogram == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (appearsOnHistogram.getCanonicalUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"canonicalUrl\": ");

			sb.append("\"");

			sb.append(_escape(appearsOnHistogram.getCanonicalUrl()));

			sb.append("\"");
		}

		if (appearsOnHistogram.getMetrics() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metrics\": ");

			sb.append("[");

			for (int i = 0; i < appearsOnHistogram.getMetrics().length; i++) {
				sb.append(String.valueOf(appearsOnHistogram.getMetrics()[i]));

				if ((i + 1) < appearsOnHistogram.getMetrics().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (appearsOnHistogram.getPageTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTitle\": ");

			sb.append("\"");

			sb.append(_escape(appearsOnHistogram.getPageTitle()));

			sb.append("\"");
		}

		if (appearsOnHistogram.getTotal() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(appearsOnHistogram.getTotal());
		}

		if (appearsOnHistogram.getTotalValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalValue\": ");

			sb.append(appearsOnHistogram.getTotalValue());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AppearsOnHistogramJSONParser appearsOnHistogramJSONParser =
			new AppearsOnHistogramJSONParser();

		return appearsOnHistogramJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AppearsOnHistogram appearsOnHistogram) {

		if (appearsOnHistogram == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (appearsOnHistogram.getCanonicalUrl() == null) {
			map.put("canonicalUrl", null);
		}
		else {
			map.put(
				"canonicalUrl",
				String.valueOf(appearsOnHistogram.getCanonicalUrl()));
		}

		if (appearsOnHistogram.getMetrics() == null) {
			map.put("metrics", null);
		}
		else {
			map.put("metrics", String.valueOf(appearsOnHistogram.getMetrics()));
		}

		if (appearsOnHistogram.getPageTitle() == null) {
			map.put("pageTitle", null);
		}
		else {
			map.put(
				"pageTitle", String.valueOf(appearsOnHistogram.getPageTitle()));
		}

		if (appearsOnHistogram.getTotal() == null) {
			map.put("total", null);
		}
		else {
			map.put("total", String.valueOf(appearsOnHistogram.getTotal()));
		}

		if (appearsOnHistogram.getTotalValue() == null) {
			map.put("totalValue", null);
		}
		else {
			map.put(
				"totalValue",
				String.valueOf(appearsOnHistogram.getTotalValue()));
		}

		return map;
	}

	public static class AppearsOnHistogramJSONParser
		extends BaseJSONParser<AppearsOnHistogram> {

		@Override
		protected AppearsOnHistogram createDTO() {
			return new AppearsOnHistogram();
		}

		@Override
		protected AppearsOnHistogram[] createDTOArray(int size) {
			return new AppearsOnHistogram[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "canonicalUrl")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "metrics")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageTitle")) {
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
			AppearsOnHistogram appearsOnHistogram, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "canonicalUrl")) {
				if (jsonParserFieldValue != null) {
					appearsOnHistogram.setCanonicalUrl(
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

					appearsOnHistogram.setMetrics(metricsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageTitle")) {
				if (jsonParserFieldValue != null) {
					appearsOnHistogram.setPageTitle(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				if (jsonParserFieldValue != null) {
					appearsOnHistogram.setTotal(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalValue")) {
				if (jsonParserFieldValue != null) {
					appearsOnHistogram.setTotalValue(
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