/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.VisitorMetric;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class VisitorMetricSerDes {

	public static VisitorMetric toDTO(String json) {
		VisitorMetricJSONParser visitorMetricJSONParser =
			new VisitorMetricJSONParser();

		return visitorMetricJSONParser.parseToDTO(json);
	}

	public static VisitorMetric[] toDTOs(String json) {
		VisitorMetricJSONParser visitorMetricJSONParser =
			new VisitorMetricJSONParser();

		return visitorMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(VisitorMetric visitorMetric) {
		if (visitorMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (visitorMetric.getAverageSessionDuration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"averageSessionDuration\": ");

			sb.append(visitorMetric.getAverageSessionDuration());
		}

		if (visitorMetric.getKnownVisitors() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"knownVisitors\": ");

			sb.append(visitorMetric.getKnownVisitors());
		}

		if (visitorMetric.getTotalSessionDuration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalSessionDuration\": ");

			sb.append(visitorMetric.getTotalSessionDuration());
		}

		if (visitorMetric.getVisitors() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visitors\": ");

			sb.append(visitorMetric.getVisitors());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		VisitorMetricJSONParser visitorMetricJSONParser =
			new VisitorMetricJSONParser();

		return visitorMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(VisitorMetric visitorMetric) {
		if (visitorMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (visitorMetric.getAverageSessionDuration() == null) {
			map.put("averageSessionDuration", null);
		}
		else {
			map.put(
				"averageSessionDuration",
				String.valueOf(visitorMetric.getAverageSessionDuration()));
		}

		if (visitorMetric.getKnownVisitors() == null) {
			map.put("knownVisitors", null);
		}
		else {
			map.put(
				"knownVisitors",
				String.valueOf(visitorMetric.getKnownVisitors()));
		}

		if (visitorMetric.getTotalSessionDuration() == null) {
			map.put("totalSessionDuration", null);
		}
		else {
			map.put(
				"totalSessionDuration",
				String.valueOf(visitorMetric.getTotalSessionDuration()));
		}

		if (visitorMetric.getVisitors() == null) {
			map.put("visitors", null);
		}
		else {
			map.put("visitors", String.valueOf(visitorMetric.getVisitors()));
		}

		return map;
	}

	public static class VisitorMetricJSONParser
		extends BaseJSONParser<VisitorMetric> {

		@Override
		protected VisitorMetric createDTO() {
			return new VisitorMetric();
		}

		@Override
		protected VisitorMetric[] createDTOArray(int size) {
			return new VisitorMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "averageSessionDuration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "knownVisitors")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalSessionDuration")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "visitors")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			VisitorMetric visitorMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "averageSessionDuration")) {
				if (jsonParserFieldValue != null) {
					visitorMetric.setAverageSessionDuration(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "knownVisitors")) {
				if (jsonParserFieldValue != null) {
					visitorMetric.setKnownVisitors(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "totalSessionDuration")) {

				if (jsonParserFieldValue != null) {
					visitorMetric.setTotalSessionDuration(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "visitors")) {
				if (jsonParserFieldValue != null) {
					visitorMetric.setVisitors(
						Long.valueOf((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1381382714