/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.EventDefinition;
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
public class EventDefinitionSerDes {

	public static EventDefinition toDTO(String json) {
		EventDefinitionJSONParser eventDefinitionJSONParser =
			new EventDefinitionJSONParser();

		return eventDefinitionJSONParser.parseToDTO(json);
	}

	public static EventDefinition[] toDTOs(String json) {
		EventDefinitionJSONParser eventDefinitionJSONParser =
			new EventDefinitionJSONParser();

		return eventDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(EventDefinition eventDefinition) {
		if (eventDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (eventDefinition.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(eventDefinition.getDescription()));

			sb.append("\"");
		}

		if (eventDefinition.getDisplayName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayName\": ");

			sb.append("\"");

			sb.append(_escape(eventDefinition.getDisplayName()));

			sb.append("\"");
		}

		if (eventDefinition.getHidden() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hidden\": ");

			sb.append(eventDefinition.getHidden());
		}

		if (eventDefinition.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(eventDefinition.getId()));

			sb.append("\"");
		}

		if (eventDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(eventDefinition.getName()));

			sb.append("\"");
		}

		if (eventDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(eventDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EventDefinitionJSONParser eventDefinitionJSONParser =
			new EventDefinitionJSONParser();

		return eventDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(EventDefinition eventDefinition) {
		if (eventDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (eventDefinition.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(eventDefinition.getDescription()));
		}

		if (eventDefinition.getDisplayName() == null) {
			map.put("displayName", null);
		}
		else {
			map.put(
				"displayName",
				String.valueOf(eventDefinition.getDisplayName()));
		}

		if (eventDefinition.getHidden() == null) {
			map.put("hidden", null);
		}
		else {
			map.put("hidden", String.valueOf(eventDefinition.getHidden()));
		}

		if (eventDefinition.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(eventDefinition.getId()));
		}

		if (eventDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(eventDefinition.getName()));
		}

		if (eventDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(eventDefinition.getType()));
		}

		return map;
	}

	public static class EventDefinitionJSONParser
		extends BaseJSONParser<EventDefinition> {

		@Override
		protected EventDefinition createDTO() {
			return new EventDefinition();
		}

		@Override
		protected EventDefinition[] createDTOArray(int size) {
			return new EventDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "displayName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "hidden")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			EventDefinition eventDefinition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					eventDefinition.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayName")) {
				if (jsonParserFieldValue != null) {
					eventDefinition.setDisplayName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "hidden")) {
				if (jsonParserFieldValue != null) {
					eventDefinition.setHidden((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					eventDefinition.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					eventDefinition.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					eventDefinition.setType(
						EventDefinition.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:1960071113