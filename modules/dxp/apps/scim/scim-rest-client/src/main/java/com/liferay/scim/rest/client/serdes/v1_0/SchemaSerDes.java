/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.Attribute;
import com.liferay.scim.rest.client.dto.v1_0.Schema;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
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

		if (schema.getAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			sb.append("[");

			for (int i = 0; i < schema.getAttributes().length; i++) {
				sb.append(String.valueOf(schema.getAttributes()[i]));

				if ((i + 1) < schema.getAttributes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (schema.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(schema.getDescription()));

			sb.append("\"");
		}

		if (schema.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(schema.getId()));

			sb.append("\"");
		}

		if (schema.getMeta() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"meta\": ");

			sb.append(String.valueOf(schema.getMeta()));
		}

		if (schema.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(schema.getName()));

			sb.append("\"");
		}

		if (schema.getSchemas() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < schema.getSchemas().length; i++) {
				sb.append(_toJSON(schema.getSchemas()[i]));

				if ((i + 1) < schema.getSchemas().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		if (schema.getAttributes() == null) {
			map.put("attributes", null);
		}
		else {
			map.put("attributes", String.valueOf(schema.getAttributes()));
		}

		if (schema.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(schema.getDescription()));
		}

		if (schema.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(schema.getId()));
		}

		if (schema.getMeta() == null) {
			map.put("meta", null);
		}
		else {
			map.put("meta", String.valueOf(schema.getMeta()));
		}

		if (schema.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(schema.getName()));
		}

		if (schema.getSchemas() == null) {
			map.put("schemas", null);
		}
		else {
			map.put("schemas", String.valueOf(schema.getSchemas()));
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
			if (Objects.equals(jsonParserFieldName, "attributes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Schema schema, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "attributes")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Attribute[] attributesArray =
						new Attribute[jsonParserFieldValues.length];

					for (int i = 0; i < attributesArray.length; i++) {
						attributesArray[i] = AttributeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					schema.setAttributes(attributesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					schema.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					schema.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				if (jsonParserFieldValue != null) {
					schema.setMeta(
						MetaSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					schema.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				if (jsonParserFieldValue != null) {
					schema.setSchemas(
						toStrings((Object[])jsonParserFieldValue));
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