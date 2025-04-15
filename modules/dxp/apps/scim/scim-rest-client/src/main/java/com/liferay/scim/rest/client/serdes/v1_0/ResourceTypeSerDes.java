/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.ResourceType;
import com.liferay.scim.rest.client.dto.v1_0.Schema;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class ResourceTypeSerDes {

	public static ResourceType toDTO(String json) {
		ResourceTypeJSONParser resourceTypeJSONParser =
			new ResourceTypeJSONParser();

		return resourceTypeJSONParser.parseToDTO(json);
	}

	public static ResourceType[] toDTOs(String json) {
		ResourceTypeJSONParser resourceTypeJSONParser =
			new ResourceTypeJSONParser();

		return resourceTypeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ResourceType resourceType) {
		if (resourceType == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (resourceType.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(resourceType.getDescription()));

			sb.append("\"");
		}

		if (resourceType.getEndpoint() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endpoint\": ");

			sb.append("\"");

			sb.append(_escape(resourceType.getEndpoint()));

			sb.append("\"");
		}

		if (resourceType.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(resourceType.getId()));

			sb.append("\"");
		}

		if (resourceType.getMeta() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"meta\": ");

			sb.append(String.valueOf(resourceType.getMeta()));
		}

		if (resourceType.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(resourceType.getName()));

			sb.append("\"");
		}

		if (resourceType.getSchema() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schema\": ");

			sb.append("\"");

			sb.append(_escape(resourceType.getSchema()));

			sb.append("\"");
		}

		if (resourceType.getSchemas() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < resourceType.getSchemas().length; i++) {
				sb.append(String.valueOf(resourceType.getSchemas()[i]));

				if ((i + 1) < resourceType.getSchemas().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ResourceTypeJSONParser resourceTypeJSONParser =
			new ResourceTypeJSONParser();

		return resourceTypeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ResourceType resourceType) {
		if (resourceType == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (resourceType.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(resourceType.getDescription()));
		}

		if (resourceType.getEndpoint() == null) {
			map.put("endpoint", null);
		}
		else {
			map.put("endpoint", String.valueOf(resourceType.getEndpoint()));
		}

		if (resourceType.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(resourceType.getId()));
		}

		if (resourceType.getMeta() == null) {
			map.put("meta", null);
		}
		else {
			map.put("meta", String.valueOf(resourceType.getMeta()));
		}

		if (resourceType.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(resourceType.getName()));
		}

		if (resourceType.getSchema() == null) {
			map.put("schema", null);
		}
		else {
			map.put("schema", String.valueOf(resourceType.getSchema()));
		}

		if (resourceType.getSchemas() == null) {
			map.put("schemas", null);
		}
		else {
			map.put("schemas", String.valueOf(resourceType.getSchemas()));
		}

		return map;
	}

	public static class ResourceTypeJSONParser
		extends BaseJSONParser<ResourceType> {

		@Override
		protected ResourceType createDTO() {
			return new ResourceType();
		}

		@Override
		protected ResourceType[] createDTOArray(int size) {
			return new ResourceType[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "endpoint")) {
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
			else if (Objects.equals(jsonParserFieldName, "schema")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ResourceType resourceType, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					resourceType.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "endpoint")) {
				if (jsonParserFieldValue != null) {
					resourceType.setEndpoint((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					resourceType.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				if (jsonParserFieldValue != null) {
					resourceType.setMeta(
						MetaSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					resourceType.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schema")) {
				if (jsonParserFieldValue != null) {
					resourceType.setSchema((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Schema[] schemasArray =
						new Schema[jsonParserFieldValues.length];

					for (int i = 0; i < schemasArray.length; i++) {
						schemasArray[i] = SchemaSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					resourceType.setSchemas(schemasArray);
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