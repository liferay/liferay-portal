/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.NestedArrayItemsTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class NestedArrayItemsTestEntitySerDes {

	public static NestedArrayItemsTestEntity toDTO(String json) {
		NestedArrayItemsTestEntityJSONParser
			nestedArrayItemsTestEntityJSONParser =
				new NestedArrayItemsTestEntityJSONParser();

		return nestedArrayItemsTestEntityJSONParser.parseToDTO(json);
	}

	public static NestedArrayItemsTestEntity[] toDTOs(String json) {
		NestedArrayItemsTestEntityJSONParser
			nestedArrayItemsTestEntityJSONParser =
				new NestedArrayItemsTestEntityJSONParser();

		return nestedArrayItemsTestEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		NestedArrayItemsTestEntity nestedArrayItemsTestEntity) {

		if (nestedArrayItemsTestEntity == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (nestedArrayItemsTestEntity.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(nestedArrayItemsTestEntity.getName()));

			sb.append("\"");
		}

		if (nestedArrayItemsTestEntity.getValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"values\": ");

			sb.append("[");

			for (int i = 0; i < nestedArrayItemsTestEntity.getValues().length;
				 i++) {

				sb.append(nestedArrayItemsTestEntity.getValues()[i]);

				if ((i + 1) < nestedArrayItemsTestEntity.getValues().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		NestedArrayItemsTestEntityJSONParser
			nestedArrayItemsTestEntityJSONParser =
				new NestedArrayItemsTestEntityJSONParser();

		return nestedArrayItemsTestEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		NestedArrayItemsTestEntity nestedArrayItemsTestEntity) {

		if (nestedArrayItemsTestEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (nestedArrayItemsTestEntity.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(nestedArrayItemsTestEntity.getName()));
		}

		if (nestedArrayItemsTestEntity.getValues() == null) {
			map.put("values", null);
		}
		else {
			map.put(
				"values",
				String.valueOf(nestedArrayItemsTestEntity.getValues()));
		}

		return map;
	}

	public static class NestedArrayItemsTestEntityJSONParser
		extends BaseJSONParser<NestedArrayItemsTestEntity> {

		@Override
		protected NestedArrayItemsTestEntity createDTO() {
			return new NestedArrayItemsTestEntity();
		}

		@Override
		protected NestedArrayItemsTestEntity[] createDTOArray(int size) {
			return new NestedArrayItemsTestEntity[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "values")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			NestedArrayItemsTestEntity nestedArrayItemsTestEntity,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					nestedArrayItemsTestEntity.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "values")) {
				if (jsonParserFieldValue != null) {
					nestedArrayItemsTestEntity.setValues(
						(String[][])jsonParserFieldValue);
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