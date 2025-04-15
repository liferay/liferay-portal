/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.Schema;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class SchemaSerDes {

	public static Schema toDTO(String json) {
		SchemaJSONParser schemaJSONParser = new SchemaJSONParser();

		return schemaJSONParser.parseToDTO(json);
	}

	public static Schema[] toDTOs(String json) {
		SchemaJSONParser schemaJSONParser = new SchemaJSONParser();

		return schemaJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Schema schema) {
		if (schema == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (schema.getProperty1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"property1\": ");

			sb.append("\"");

			sb.append(_escape(schema.getProperty1()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SchemaJSONParser schemaJSONParser = new SchemaJSONParser();

		return schemaJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Schema schema) {
		if (schema == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (schema.getProperty1() == null) {
			map.put("property1", null);
		}
		else {
			map.put("property1", String.valueOf(schema.getProperty1()));
		}

		return map;
	}

	public static class SchemaJSONParser extends BaseJSONParser<Schema> {

		@Override
		protected Schema createDTO() {
			return new Schema();
		}

		@Override
		protected Schema[] createDTOArray(int size) {
			return new Schema[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "property1")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Schema schema, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "property1")) {
				if (jsonParserFieldValue != null) {
					schema.setProperty1((String)jsonParserFieldValue);
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