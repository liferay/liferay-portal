/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Calendar;
import com.liferay.portal.workflow.metrics.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class CalendarSerDes {

	public static Calendar toDTO(String json) {
		CalendarJSONParser calendarJSONParser = new CalendarJSONParser();

		return calendarJSONParser.parseToDTO(json);
	}

	public static Calendar[] toDTOs(String json) {
		CalendarJSONParser calendarJSONParser = new CalendarJSONParser();

		return calendarJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Calendar calendar) {
		if (calendar == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (calendar.getDefaultCalendar() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultCalendar\": ");

			sb.append(calendar.getDefaultCalendar());
		}

		if (calendar.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(calendar.getKey()));

			sb.append("\"");
		}

		if (calendar.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(calendar.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CalendarJSONParser calendarJSONParser = new CalendarJSONParser();

		return calendarJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Calendar calendar) {
		if (calendar == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (calendar.getDefaultCalendar() == null) {
			map.put("defaultCalendar", null);
		}
		else {
			map.put(
				"defaultCalendar",
				String.valueOf(calendar.getDefaultCalendar()));
		}

		if (calendar.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(calendar.getKey()));
		}

		if (calendar.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(calendar.getTitle()));
		}

		return map;
	}

	public static class CalendarJSONParser extends BaseJSONParser<Calendar> {

		@Override
		protected Calendar createDTO() {
			return new Calendar();
		}

		@Override
		protected Calendar[] createDTOArray(int size) {
			return new Calendar[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "defaultCalendar")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Calendar calendar, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultCalendar")) {
				if (jsonParserFieldValue != null) {
					calendar.setDefaultCalendar((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					calendar.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					calendar.setTitle((String)jsonParserFieldValue);
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