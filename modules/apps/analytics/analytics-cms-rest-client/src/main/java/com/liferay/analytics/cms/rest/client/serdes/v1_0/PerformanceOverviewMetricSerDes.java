/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.serdes.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceOverviewMetric;
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
public class PerformanceOverviewMetricSerDes {

	public static PerformanceOverviewMetric toDTO(String json) {
		PerformanceOverviewMetricJSONParser
			performanceOverviewMetricJSONParser =
				new PerformanceOverviewMetricJSONParser();

		return performanceOverviewMetricJSONParser.parseToDTO(json);
	}

	public static PerformanceOverviewMetric[] toDTOs(String json) {
		PerformanceOverviewMetricJSONParser
			performanceOverviewMetricJSONParser =
				new PerformanceOverviewMetricJSONParser();

		return performanceOverviewMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PerformanceOverviewMetric performanceOverviewMetric) {

		if (performanceOverviewMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (performanceOverviewMetric.getDownloadsMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"downloadsMetric\": ");

			sb.append(
				String.valueOf(performanceOverviewMetric.getDownloadsMetric()));
		}

		if (performanceOverviewMetric.getImpressionsMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"impressionsMetric\": ");

			sb.append(
				String.valueOf(
					performanceOverviewMetric.getImpressionsMetric()));
		}

		if (performanceOverviewMetric.getReadsMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readsMetric\": ");

			sb.append(
				String.valueOf(performanceOverviewMetric.getReadsMetric()));
		}

		if (performanceOverviewMetric.getViewsMetric() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewsMetric\": ");

			sb.append(
				String.valueOf(performanceOverviewMetric.getViewsMetric()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PerformanceOverviewMetricJSONParser
			performanceOverviewMetricJSONParser =
				new PerformanceOverviewMetricJSONParser();

		return performanceOverviewMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PerformanceOverviewMetric performanceOverviewMetric) {

		if (performanceOverviewMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (performanceOverviewMetric.getDownloadsMetric() == null) {
			map.put("downloadsMetric", null);
		}
		else {
			map.put(
				"downloadsMetric",
				String.valueOf(performanceOverviewMetric.getDownloadsMetric()));
		}

		if (performanceOverviewMetric.getImpressionsMetric() == null) {
			map.put("impressionsMetric", null);
		}
		else {
			map.put(
				"impressionsMetric",
				String.valueOf(
					performanceOverviewMetric.getImpressionsMetric()));
		}

		if (performanceOverviewMetric.getReadsMetric() == null) {
			map.put("readsMetric", null);
		}
		else {
			map.put(
				"readsMetric",
				String.valueOf(performanceOverviewMetric.getReadsMetric()));
		}

		if (performanceOverviewMetric.getViewsMetric() == null) {
			map.put("viewsMetric", null);
		}
		else {
			map.put(
				"viewsMetric",
				String.valueOf(performanceOverviewMetric.getViewsMetric()));
		}

		return map;
	}

	public static class PerformanceOverviewMetricJSONParser
		extends BaseJSONParser<PerformanceOverviewMetric> {

		@Override
		protected PerformanceOverviewMetric createDTO() {
			return new PerformanceOverviewMetric();
		}

		@Override
		protected PerformanceOverviewMetric[] createDTOArray(int size) {
			return new PerformanceOverviewMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "downloadsMetric")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "impressionsMetric")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "readsMetric")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "viewsMetric")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PerformanceOverviewMetric performanceOverviewMetric,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "downloadsMetric")) {
				if (jsonParserFieldValue != null) {
					performanceOverviewMetric.setDownloadsMetric(
						MetricSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "impressionsMetric")) {
				if (jsonParserFieldValue != null) {
					performanceOverviewMetric.setImpressionsMetric(
						MetricSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "readsMetric")) {
				if (jsonParserFieldValue != null) {
					performanceOverviewMetric.setReadsMetric(
						MetricSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewsMetric")) {
				if (jsonParserFieldValue != null) {
					performanceOverviewMetric.setViewsMetric(
						MetricSerDes.toDTO((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1692100147