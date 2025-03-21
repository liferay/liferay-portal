/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.FragmentField;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FragmentFieldSerDes {

	public static FragmentField toDTO(String json) {
		FragmentFieldJSONParser fragmentFieldJSONParser =
			new FragmentFieldJSONParser();

		return fragmentFieldJSONParser.parseToDTO(json);
	}

	public static FragmentField[] toDTOs(String json) {
		FragmentFieldJSONParser fragmentFieldJSONParser =
			new FragmentFieldJSONParser();

		return fragmentFieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FragmentField fragmentField) {
		if (fragmentField == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentField.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(fragmentField.getId()));

			sb.append("\"");
		}

		if (fragmentField.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			if (fragmentField.getValue() instanceof String) {
				sb.append("\"");
				sb.append((String)fragmentField.getValue());
				sb.append("\"");
			}
			else {
				sb.append(fragmentField.getValue());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentFieldJSONParser fragmentFieldJSONParser =
			new FragmentFieldJSONParser();

		return fragmentFieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FragmentField fragmentField) {
		if (fragmentField == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentField.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(fragmentField.getId()));
		}

		if (fragmentField.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(fragmentField.getValue()));
		}

		return map;
	}

	public static class FragmentFieldJSONParser
		extends BaseJSONParser<FragmentField> {

		@Override
		protected FragmentField createDTO() {
			return new FragmentField();
		}

		@Override
		protected FragmentField[] createDTOArray(int size) {
			return new FragmentField[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentField fragmentField, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					fragmentField.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					fragmentField.setValue((Object)jsonParserFieldValue);
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