/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.client.serdes.v1_0;

import com.liferay.analytics.reports.rest.client.dto.v1_0.Trend;
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
public class TrendSerDes {

	public static Trend toDTO(String json) {
		TrendJSONParser trendJSONParser = new TrendJSONParser();

		return trendJSONParser.parseToDTO(json);
	}

	public static Trend[] toDTOs(String json) {
		TrendJSONParser trendJSONParser = new TrendJSONParser();

		return trendJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Trend trend) {
		if (trend == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (trend.getPercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentage\": ");

			sb.append(trend.getPercentage());
		}

		if (trend.getTrendClassification() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trendClassification\": ");

			sb.append("\"");

			sb.append(trend.getTrendClassification());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TrendJSONParser trendJSONParser = new TrendJSONParser();

		return trendJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Trend trend) {
		if (trend == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (trend.getPercentage() == null) {
			map.put("percentage", null);
		}
		else {
			map.put("percentage", String.valueOf(trend.getPercentage()));
		}

		if (trend.getTrendClassification() == null) {
			map.put("trendClassification", null);
		}
		else {
			map.put(
				"trendClassification",
				String.valueOf(trend.getTrendClassification()));
		}

		return map;
	}

	public static class TrendJSONParser extends BaseJSONParser<Trend> {

		@Override
		protected Trend createDTO() {
			return new Trend();
		}

		@Override
		protected Trend[] createDTOArray(int size) {
			return new Trend[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "percentage")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "trendClassification")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Trend trend, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "percentage")) {
				if (jsonParserFieldValue != null) {
					trend.setPercentage(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "trendClassification")) {

				if (jsonParserFieldValue != null) {
					trend.setTrendClassification(
						Trend.TrendClassification.create(
							(String)jsonParserFieldValue));
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