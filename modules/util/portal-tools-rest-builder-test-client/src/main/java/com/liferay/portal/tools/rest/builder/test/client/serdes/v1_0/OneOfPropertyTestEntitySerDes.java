/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.OneOfPropertyTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class OneOfPropertyTestEntitySerDes {

	public static OneOfPropertyTestEntity toDTO(String json) {
		OneOfPropertyTestEntityJSONParser oneOfPropertyTestEntityJSONParser =
			new OneOfPropertyTestEntityJSONParser();

		return oneOfPropertyTestEntityJSONParser.parseToDTO(json);
	}

	public static OneOfPropertyTestEntity[] toDTOs(String json) {
		OneOfPropertyTestEntityJSONParser oneOfPropertyTestEntityJSONParser =
			new OneOfPropertyTestEntityJSONParser();

		return oneOfPropertyTestEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		OneOfPropertyTestEntity oneOfPropertyTestEntity) {

		if (oneOfPropertyTestEntity == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (oneOfPropertyTestEntity.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(oneOfPropertyTestEntity.getName()));

			sb.append("\"");
		}

		if (oneOfPropertyTestEntity.getOneOfProperty() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"oneOfProperty\": ");

			sb.append(_toJSON(oneOfPropertyTestEntity.getOneOfProperty()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OneOfPropertyTestEntityJSONParser oneOfPropertyTestEntityJSONParser =
			new OneOfPropertyTestEntityJSONParser();

		return oneOfPropertyTestEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		OneOfPropertyTestEntity oneOfPropertyTestEntity) {

		if (oneOfPropertyTestEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (oneOfPropertyTestEntity.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(oneOfPropertyTestEntity.getName()));
		}

		if (oneOfPropertyTestEntity.getOneOfProperty() == null) {
			map.put("oneOfProperty", null);
		}
		else {
			map.put(
				"oneOfProperty",
				String.valueOf(oneOfPropertyTestEntity.getOneOfProperty()));
		}

		return map;
	}

	public static class OneOfPropertyTestEntityJSONParser
		extends BaseJSONParser<OneOfPropertyTestEntity> {

		@Override
		protected OneOfPropertyTestEntity createDTO() {
			return new OneOfPropertyTestEntity();
		}

		@Override
		protected OneOfPropertyTestEntity[] createDTOArray(int size) {
			return new OneOfPropertyTestEntity[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "oneOfProperty")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OneOfPropertyTestEntity oneOfPropertyTestEntity,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					oneOfPropertyTestEntity.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "oneOfProperty")) {
				if (jsonParserFieldValue != null) {
					oneOfPropertyTestEntity.setOneOfProperty(
						(Object)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:234960116