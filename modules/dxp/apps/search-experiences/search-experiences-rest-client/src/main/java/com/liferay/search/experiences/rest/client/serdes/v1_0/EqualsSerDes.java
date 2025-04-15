/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.Equals;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class EqualsSerDes {

	public static Equals toDTO(String json) {
		EqualsJSONParser equalsJSONParser = new EqualsJSONParser();

		return equalsJSONParser.parseToDTO(json);
	}

	public static Equals[] toDTOs(String json) {
		EqualsJSONParser equalsJSONParser = new EqualsJSONParser();

		return equalsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Equals equals) {
		if (equals == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (equals.getFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"format\": ");

			sb.append("\"");

			sb.append(_escape(equals.getFormat()));

			sb.append("\"");
		}

		if (equals.getParameterName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameterName\": ");

			sb.append("\"");

			sb.append(_escape(equals.getParameterName()));

			sb.append("\"");
		}

		if (equals.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			if (equals.getValue() instanceof String) {
				sb.append("\"");
				sb.append((String)equals.getValue());
				sb.append("\"");
			}
			else {
				sb.append(equals.getValue());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EqualsJSONParser equalsJSONParser = new EqualsJSONParser();

		return equalsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Equals equals) {
		if (equals == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (equals.getFormat() == null) {
			map.put("format", null);
		}
		else {
			map.put("format", String.valueOf(equals.getFormat()));
		}

		if (equals.getParameterName() == null) {
			map.put("parameterName", null);
		}
		else {
			map.put("parameterName", String.valueOf(equals.getParameterName()));
		}

		if (equals.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(equals.getValue()));
		}

		return map;
	}

	public static class EqualsJSONParser extends BaseJSONParser<Equals> {

		@Override
		protected Equals createDTO() {
			return new Equals();
		}

		@Override
		protected Equals[] createDTOArray(int size) {
			return new Equals[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "format")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parameterName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Equals equals, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "format")) {
				if (jsonParserFieldValue != null) {
					equals.setFormat((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parameterName")) {
				if (jsonParserFieldValue != null) {
					equals.setParameterName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					equals.setValue((Object)jsonParserFieldValue);
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