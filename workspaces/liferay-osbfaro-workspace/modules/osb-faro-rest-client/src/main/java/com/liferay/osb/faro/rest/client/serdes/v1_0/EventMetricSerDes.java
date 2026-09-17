/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.EventMetric;
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
public class EventMetricSerDes {

	public static EventMetric toDTO(String json) {
		EventMetricJSONParser eventMetricJSONParser =
			new EventMetricJSONParser();

		return eventMetricJSONParser.parseToDTO(json);
	}

	public static EventMetric[] toDTOs(String json) {
		EventMetricJSONParser eventMetricJSONParser =
			new EventMetricJSONParser();

		return eventMetricJSONParser.parseToDTOs(json);
	}

	public static String toJSON(EventMetric eventMetric) {
		if (eventMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (eventMetric.getTotalEvents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalEvents\": ");

			sb.append(String.valueOf(eventMetric.getTotalEvents()));
		}

		if (eventMetric.getTotalSessions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalSessions\": ");

			sb.append(String.valueOf(eventMetric.getTotalSessions()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EventMetricJSONParser eventMetricJSONParser =
			new EventMetricJSONParser();

		return eventMetricJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(EventMetric eventMetric) {
		if (eventMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (eventMetric.getTotalEvents() == null) {
			map.put("totalEvents", null);
		}
		else {
			map.put(
				"totalEvents", String.valueOf(eventMetric.getTotalEvents()));
		}

		if (eventMetric.getTotalSessions() == null) {
			map.put("totalSessions", null);
		}
		else {
			map.put(
				"totalSessions",
				String.valueOf(eventMetric.getTotalSessions()));
		}

		return map;
	}

	public static class EventMetricJSONParser
		extends BaseJSONParser<EventMetric> {

		@Override
		protected EventMetric createDTO() {
			return new EventMetric();
		}

		@Override
		protected EventMetric[] createDTOArray(int size) {
			return new EventMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "totalEvents")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalSessions")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			EventMetric eventMetric, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "totalEvents")) {
				if (jsonParserFieldValue != null) {
					eventMetric.setTotalEvents(
						MetricSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalSessions")) {
				if (jsonParserFieldValue != null) {
					eventMetric.setTotalSessions(
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
// LIFERAY-REST-BUILDER-HASH:1158085770