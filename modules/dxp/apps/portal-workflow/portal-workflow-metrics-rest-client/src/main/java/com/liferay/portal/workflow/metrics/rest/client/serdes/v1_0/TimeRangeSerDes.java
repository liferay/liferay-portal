/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.TimeRange;
import com.liferay.portal.workflow.metrics.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class TimeRangeSerDes {

	public static TimeRange toDTO(String json) {
		TimeRangeJSONParser timeRangeJSONParser = new TimeRangeJSONParser();

		return timeRangeJSONParser.parseToDTO(json);
	}

	public static TimeRange[] toDTOs(String json) {
		TimeRangeJSONParser timeRangeJSONParser = new TimeRangeJSONParser();

		return timeRangeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TimeRange timeRange) {
		if (timeRange == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (timeRange.getDateEnd() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateEnd\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(timeRange.getDateEnd()));

			sb.append("\"");
		}

		if (timeRange.getDateStart() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateStart\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(timeRange.getDateStart()));

			sb.append("\"");
		}

		if (timeRange.getDefaultTimeRange() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultTimeRange\": ");

			sb.append(timeRange.getDefaultTimeRange());
		}

		if (timeRange.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(timeRange.getId());
		}

		if (timeRange.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(timeRange.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TimeRangeJSONParser timeRangeJSONParser = new TimeRangeJSONParser();

		return timeRangeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TimeRange timeRange) {
		if (timeRange == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (timeRange.getDateEnd() == null) {
			map.put("dateEnd", null);
		}
		else {
			map.put(
				"dateEnd",
				liferayToJSONDateFormat.format(timeRange.getDateEnd()));
		}

		if (timeRange.getDateStart() == null) {
			map.put("dateStart", null);
		}
		else {
			map.put(
				"dateStart",
				liferayToJSONDateFormat.format(timeRange.getDateStart()));
		}

		if (timeRange.getDefaultTimeRange() == null) {
			map.put("defaultTimeRange", null);
		}
		else {
			map.put(
				"defaultTimeRange",
				String.valueOf(timeRange.getDefaultTimeRange()));
		}

		if (timeRange.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(timeRange.getId()));
		}

		if (timeRange.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(timeRange.getName()));
		}

		return map;
	}

	public static class TimeRangeJSONParser extends BaseJSONParser<TimeRange> {

		@Override
		protected TimeRange createDTO() {
			return new TimeRange();
		}

		@Override
		protected TimeRange[] createDTOArray(int size) {
			return new TimeRange[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dateEnd")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateStart")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultTimeRange")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TimeRange timeRange, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dateEnd")) {
				if (jsonParserFieldValue != null) {
					timeRange.setDateEnd(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateStart")) {
				if (jsonParserFieldValue != null) {
					timeRange.setDateStart(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultTimeRange")) {
				if (jsonParserFieldValue != null) {
					timeRange.setDefaultTimeRange(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					timeRange.setId(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					timeRange.setName((String)jsonParserFieldValue);
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