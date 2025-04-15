/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.EntityModelResourceTestEntity2;
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
public class EntityModelResourceTestEntity2SerDes {

	public static EntityModelResourceTestEntity2 toDTO(String json) {
		EntityModelResourceTestEntity2JSONParser
			entityModelResourceTestEntity2JSONParser =
				new EntityModelResourceTestEntity2JSONParser();

		return entityModelResourceTestEntity2JSONParser.parseToDTO(json);
	}

	public static EntityModelResourceTestEntity2[] toDTOs(String json) {
		EntityModelResourceTestEntity2JSONParser
			entityModelResourceTestEntity2JSONParser =
				new EntityModelResourceTestEntity2JSONParser();

		return entityModelResourceTestEntity2JSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		EntityModelResourceTestEntity2 entityModelResourceTestEntity2) {

		if (entityModelResourceTestEntity2 == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (entityModelResourceTestEntity2.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(entityModelResourceTestEntity2.getId());
		}

		if (entityModelResourceTestEntity2.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(entityModelResourceTestEntity2.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EntityModelResourceTestEntity2JSONParser
			entityModelResourceTestEntity2JSONParser =
				new EntityModelResourceTestEntity2JSONParser();

		return entityModelResourceTestEntity2JSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		EntityModelResourceTestEntity2 entityModelResourceTestEntity2) {

		if (entityModelResourceTestEntity2 == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (entityModelResourceTestEntity2.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put(
				"id", String.valueOf(entityModelResourceTestEntity2.getId()));
		}

		if (entityModelResourceTestEntity2.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(entityModelResourceTestEntity2.getName()));
		}

		return map;
	}

	public static class EntityModelResourceTestEntity2JSONParser
		extends BaseJSONParser<EntityModelResourceTestEntity2> {

		@Override
		protected EntityModelResourceTestEntity2 createDTO() {
			return new EntityModelResourceTestEntity2();
		}

		@Override
		protected EntityModelResourceTestEntity2[] createDTOArray(int size) {
			return new EntityModelResourceTestEntity2[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			EntityModelResourceTestEntity2 entityModelResourceTestEntity2,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					entityModelResourceTestEntity2.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					entityModelResourceTestEntity2.setName(
						(String)jsonParserFieldValue);
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