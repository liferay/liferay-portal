/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.Contains;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class ContainsSerDes {

	public static Contains toDTO(String json) {
		ContainsJSONParser containsJSONParser = new ContainsJSONParser();

		return containsJSONParser.parseToDTO(json);
	}

	public static Contains[] toDTOs(String json) {
		ContainsJSONParser containsJSONParser = new ContainsJSONParser();

		return containsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Contains contains) {
		if (contains == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contains.getParameterName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameterName\": ");

			sb.append("\"");

			sb.append(_escape(contains.getParameterName()));

			sb.append("\"");
		}

		if (contains.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			if (contains.getValue() instanceof String) {
				sb.append("\"");
				sb.append((String)contains.getValue());
				sb.append("\"");
			}
			else {
				sb.append(contains.getValue());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContainsJSONParser containsJSONParser = new ContainsJSONParser();

		return containsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Contains contains) {
		if (contains == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contains.getParameterName() == null) {
			map.put("parameterName", null);
		}
		else {
			map.put(
				"parameterName", String.valueOf(contains.getParameterName()));
		}

		if (contains.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(contains.getValue()));
		}

		return map;
	}

	public static class ContainsJSONParser extends BaseJSONParser<Contains> {

		@Override
		protected Contains createDTO() {
			return new Contains();
		}

		@Override
		protected Contains[] createDTOArray(int size) {
			return new Contains[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "parameterName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Contains contains, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "parameterName")) {
				if (jsonParserFieldValue != null) {
					contains.setParameterName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					contains.setValue((Object)jsonParserFieldValue);
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