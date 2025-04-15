/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.In;
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
public class InSerDes {

	public static In toDTO(String json) {
		InJSONParser inJSONParser = new InJSONParser();

		return inJSONParser.parseToDTO(json);
	}

	public static In[] toDTOs(String json) {
		InJSONParser inJSONParser = new InJSONParser();

		return inJSONParser.parseToDTOs(json);
	}

	public static String toJSON(In in) {
		if (in == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (in.getParameterName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameterName\": ");

			sb.append("\"");

			sb.append(_escape(in.getParameterName()));

			sb.append("\"");
		}

		if (in.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			if (in.getValue() instanceof String) {
				sb.append("\"");
				sb.append((String)in.getValue());
				sb.append("\"");
			}
			else {
				sb.append(in.getValue());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		InJSONParser inJSONParser = new InJSONParser();

		return inJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(In in) {
		if (in == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (in.getParameterName() == null) {
			map.put("parameterName", null);
		}
		else {
			map.put("parameterName", String.valueOf(in.getParameterName()));
		}

		if (in.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(in.getValue()));
		}

		return map;
	}

	public static class InJSONParser extends BaseJSONParser<In> {

		@Override
		protected In createDTO() {
			return new In();
		}

		@Override
		protected In[] createDTOArray(int size) {
			return new In[size];
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
			In in, String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "parameterName")) {
				if (jsonParserFieldValue != null) {
					in.setParameterName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					in.setValue((Object)jsonParserFieldValue);
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