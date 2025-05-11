/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.MultipartTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class MultipartTestEntitySerDes {

	public static MultipartTestEntity toDTO(String json) {
		MultipartTestEntityJSONParser multipartTestEntityJSONParser =
			new MultipartTestEntityJSONParser();

		return multipartTestEntityJSONParser.parseToDTO(json);
	}

	public static MultipartTestEntity[] toDTOs(String json) {
		MultipartTestEntityJSONParser multipartTestEntityJSONParser =
			new MultipartTestEntityJSONParser();

		return multipartTestEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MultipartTestEntity multipartTestEntity) {
		if (multipartTestEntity == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (multipartTestEntity.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(multipartTestEntity.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MultipartTestEntityJSONParser multipartTestEntityJSONParser =
			new MultipartTestEntityJSONParser();

		return multipartTestEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		MultipartTestEntity multipartTestEntity) {

		if (multipartTestEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (multipartTestEntity.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(multipartTestEntity.getName()));
		}

		return map;
	}

	public static class MultipartTestEntityJSONParser
		extends BaseJSONParser<MultipartTestEntity> {

		@Override
		protected MultipartTestEntity createDTO() {
			return new MultipartTestEntity();
		}

		@Override
		protected MultipartTestEntity[] createDTOArray(int size) {
			return new MultipartTestEntity[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			MultipartTestEntity multipartTestEntity, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					multipartTestEntity.setName((String)jsonParserFieldValue);
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