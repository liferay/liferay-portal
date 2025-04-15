/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.UnreferencedTestEntity;
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
public class UnreferencedTestEntitySerDes {

	public static UnreferencedTestEntity toDTO(String json) {
		UnreferencedTestEntityJSONParser unreferencedTestEntityJSONParser =
			new UnreferencedTestEntityJSONParser();

		return unreferencedTestEntityJSONParser.parseToDTO(json);
	}

	public static UnreferencedTestEntity[] toDTOs(String json) {
		UnreferencedTestEntityJSONParser unreferencedTestEntityJSONParser =
			new UnreferencedTestEntityJSONParser();

		return unreferencedTestEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(UnreferencedTestEntity unreferencedTestEntity) {
		if (unreferencedTestEntity == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (unreferencedTestEntity.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(unreferencedTestEntity.getDescription()));

			sb.append("\"");
		}

		if (unreferencedTestEntity.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(unreferencedTestEntity.getId());
		}

		if (unreferencedTestEntity.getPropertyWithHyphens() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"property-with-hyphens\": ");

			sb.append("\"");

			sb.append(_escape(unreferencedTestEntity.getPropertyWithHyphens()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UnreferencedTestEntityJSONParser unreferencedTestEntityJSONParser =
			new UnreferencedTestEntityJSONParser();

		return unreferencedTestEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		UnreferencedTestEntity unreferencedTestEntity) {

		if (unreferencedTestEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (unreferencedTestEntity.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(unreferencedTestEntity.getDescription()));
		}

		if (unreferencedTestEntity.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(unreferencedTestEntity.getId()));
		}

		if (unreferencedTestEntity.getPropertyWithHyphens() == null) {
			map.put("property-with-hyphens", null);
		}
		else {
			map.put(
				"property-with-hyphens",
				String.valueOf(
					unreferencedTestEntity.getPropertyWithHyphens()));
		}

		return map;
	}

	public static class UnreferencedTestEntityJSONParser
		extends BaseJSONParser<UnreferencedTestEntity> {

		@Override
		protected UnreferencedTestEntity createDTO() {
			return new UnreferencedTestEntity();
		}

		@Override
		protected UnreferencedTestEntity[] createDTOArray(int size) {
			return new UnreferencedTestEntity[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "property-with-hyphens")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			UnreferencedTestEntity unreferencedTestEntity,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					unreferencedTestEntity.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					unreferencedTestEntity.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "property-with-hyphens")) {

				if (jsonParserFieldValue != null) {
					unreferencedTestEntity.setPropertyWithHyphens(
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