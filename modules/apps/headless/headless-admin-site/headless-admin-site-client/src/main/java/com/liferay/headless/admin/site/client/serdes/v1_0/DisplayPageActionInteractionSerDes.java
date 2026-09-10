/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageActionInteraction;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class DisplayPageActionInteractionSerDes {

	public static DisplayPageActionInteraction toDTO(String json) {
		DisplayPageActionInteractionJSONParser
			displayPageActionInteractionJSONParser =
				new DisplayPageActionInteractionJSONParser();

		return displayPageActionInteractionJSONParser.parseToDTO(json);
	}

	public static DisplayPageActionInteraction[] toDTOs(String json) {
		DisplayPageActionInteractionJSONParser
			displayPageActionInteractionJSONParser =
				new DisplayPageActionInteractionJSONParser();

		return displayPageActionInteractionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DisplayPageActionInteraction displayPageActionInteraction) {

		if (displayPageActionInteraction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (displayPageActionInteraction.getMappingFieldKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mappingFieldKey\": ");

			sb.append("\"");

			sb.append(
				_escape(displayPageActionInteraction.getMappingFieldKey()));

			sb.append("\"");
		}

		if (displayPageActionInteraction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(displayPageActionInteraction.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DisplayPageActionInteractionJSONParser
			displayPageActionInteractionJSONParser =
				new DisplayPageActionInteractionJSONParser();

		return displayPageActionInteractionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DisplayPageActionInteraction displayPageActionInteraction) {

		if (displayPageActionInteraction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (displayPageActionInteraction.getMappingFieldKey() == null) {
			map.put("mappingFieldKey", null);
		}
		else {
			map.put(
				"mappingFieldKey",
				String.valueOf(
					displayPageActionInteraction.getMappingFieldKey()));
		}

		if (displayPageActionInteraction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(displayPageActionInteraction.getType()));
		}

		return map;
	}

	public static class DisplayPageActionInteractionJSONParser
		extends BaseJSONParser<DisplayPageActionInteraction> {

		@Override
		protected DisplayPageActionInteraction createDTO() {
			return new DisplayPageActionInteraction();
		}

		@Override
		protected DisplayPageActionInteraction[] createDTOArray(int size) {
			return new DisplayPageActionInteraction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "mappingFieldKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DisplayPageActionInteraction displayPageActionInteraction,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "mappingFieldKey")) {
				if (jsonParserFieldValue != null) {
					displayPageActionInteraction.setMappingFieldKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					displayPageActionInteraction.setType(
						DisplayPageActionInteraction.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-95708795