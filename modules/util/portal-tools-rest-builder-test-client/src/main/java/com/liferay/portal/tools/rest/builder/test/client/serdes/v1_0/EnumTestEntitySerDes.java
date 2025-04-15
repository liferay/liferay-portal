/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.EnumTestEntity;
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
public class EnumTestEntitySerDes {

	public static EnumTestEntity toDTO(String json) {
		EnumTestEntityJSONParser enumTestEntityJSONParser =
			new EnumTestEntityJSONParser();

		return enumTestEntityJSONParser.parseToDTO(json);
	}

	public static EnumTestEntity[] toDTOs(String json) {
		EnumTestEntityJSONParser enumTestEntityJSONParser =
			new EnumTestEntityJSONParser();

		return enumTestEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(EnumTestEntity enumTestEntity) {
		if (enumTestEntity == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (enumTestEntity.getTestEnum() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"testEnum\": ");

			sb.append("\"");

			sb.append(enumTestEntity.getTestEnum());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EnumTestEntityJSONParser enumTestEntityJSONParser =
			new EnumTestEntityJSONParser();

		return enumTestEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(EnumTestEntity enumTestEntity) {
		if (enumTestEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (enumTestEntity.getTestEnum() == null) {
			map.put("testEnum", null);
		}
		else {
			map.put("testEnum", String.valueOf(enumTestEntity.getTestEnum()));
		}

		return map;
	}

	public static class EnumTestEntityJSONParser
		extends BaseJSONParser<EnumTestEntity> {

		@Override
		protected EnumTestEntity createDTO() {
			return new EnumTestEntity();
		}

		@Override
		protected EnumTestEntity[] createDTOArray(int size) {
			return new EnumTestEntity[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "testEnum")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			EnumTestEntity enumTestEntity, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "testEnum")) {
				if (jsonParserFieldValue != null) {
					enumTestEntity.setTestEnum(
						EnumTestEntity.TestEnum.create(
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