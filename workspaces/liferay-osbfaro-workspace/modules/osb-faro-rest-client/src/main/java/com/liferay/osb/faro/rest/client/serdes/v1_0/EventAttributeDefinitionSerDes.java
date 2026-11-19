/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.EventAttributeDefinition;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class EventAttributeDefinitionSerDes {

	public static EventAttributeDefinition toDTO(String json) {
		EventAttributeDefinitionJSONParser eventAttributeDefinitionJSONParser =
			new EventAttributeDefinitionJSONParser();

		return eventAttributeDefinitionJSONParser.parseToDTO(json);
	}

	public static EventAttributeDefinition[] toDTOs(String json) {
		EventAttributeDefinitionJSONParser eventAttributeDefinitionJSONParser =
			new EventAttributeDefinitionJSONParser();

		return eventAttributeDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		EventAttributeDefinition eventAttributeDefinition) {

		if (eventAttributeDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (eventAttributeDefinition.getDataType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataType\": ");

			sb.append("\"");
			sb.append(eventAttributeDefinition.getDataType());
			sb.append("\"");
		}

		if (eventAttributeDefinition.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(eventAttributeDefinition.getDescription()));

			sb.append("\"");
		}

		if (eventAttributeDefinition.getDisplayName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayName\": ");

			sb.append("\"");

			sb.append(_escape(eventAttributeDefinition.getDisplayName()));

			sb.append("\"");
		}

		if (eventAttributeDefinition.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(eventAttributeDefinition.getId()));

			sb.append("\"");
		}

		if (eventAttributeDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(eventAttributeDefinition.getName()));

			sb.append("\"");
		}

		if (eventAttributeDefinition.getSampleValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sampleValue\": ");

			sb.append("\"");

			sb.append(_escape(eventAttributeDefinition.getSampleValue()));

			sb.append("\"");
		}

		if (eventAttributeDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(eventAttributeDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EventAttributeDefinitionJSONParser eventAttributeDefinitionJSONParser =
			new EventAttributeDefinitionJSONParser();

		return eventAttributeDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		EventAttributeDefinition eventAttributeDefinition) {

		if (eventAttributeDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (eventAttributeDefinition.getDataType() == null) {
			map.put("dataType", null);
		}
		else {
			map.put(
				"dataType",
				String.valueOf(eventAttributeDefinition.getDataType()));
		}

		if (eventAttributeDefinition.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(eventAttributeDefinition.getDescription()));
		}

		if (eventAttributeDefinition.getDisplayName() == null) {
			map.put("displayName", null);
		}
		else {
			map.put(
				"displayName",
				String.valueOf(eventAttributeDefinition.getDisplayName()));
		}

		if (eventAttributeDefinition.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(eventAttributeDefinition.getId()));
		}

		if (eventAttributeDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(eventAttributeDefinition.getName()));
		}

		if (eventAttributeDefinition.getSampleValue() == null) {
			map.put("sampleValue", null);
		}
		else {
			map.put(
				"sampleValue",
				String.valueOf(eventAttributeDefinition.getSampleValue()));
		}

		if (eventAttributeDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(eventAttributeDefinition.getType()));
		}

		return map;
	}

	public static class EventAttributeDefinitionJSONParser
		extends BaseJSONParser<EventAttributeDefinition> {

		@Override
		protected EventAttributeDefinition createDTO() {
			return new EventAttributeDefinition();
		}

		@Override
		protected EventAttributeDefinition[] createDTOArray(int size) {
			return new EventAttributeDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dataType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "displayName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sampleValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			EventAttributeDefinition eventAttributeDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dataType")) {
				if (jsonParserFieldValue != null) {
					eventAttributeDefinition.setDataType(
						EventAttributeDefinition.DataType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					eventAttributeDefinition.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayName")) {
				if (jsonParserFieldValue != null) {
					eventAttributeDefinition.setDisplayName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					eventAttributeDefinition.setId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					eventAttributeDefinition.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sampleValue")) {
				if (jsonParserFieldValue != null) {
					eventAttributeDefinition.setSampleValue(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					eventAttributeDefinition.setType(
						EventAttributeDefinition.Type.create(
							(String)jsonParserFieldValue));
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:30300939