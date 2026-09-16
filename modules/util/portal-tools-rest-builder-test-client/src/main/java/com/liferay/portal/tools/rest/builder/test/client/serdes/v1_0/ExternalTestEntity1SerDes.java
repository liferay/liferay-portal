/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ExternalChildTestEntity1;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ExternalChildTestEntity2;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ExternalTestEntity1;
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
public class ExternalTestEntity1SerDes {

	public static ExternalTestEntity1 toDTO(String json) {
		ExternalTestEntity1JSONParser externalTestEntity1JSONParser =
			new ExternalTestEntity1JSONParser();

		return externalTestEntity1JSONParser.parseToDTO(json);
	}

	public static ExternalTestEntity1[] toDTOs(String json) {
		ExternalTestEntity1JSONParser externalTestEntity1JSONParser =
			new ExternalTestEntity1JSONParser();

		return externalTestEntity1JSONParser.parseToDTOs(json);
	}

	public static String toJSON(ExternalTestEntity1 externalTestEntity1) {
		if (externalTestEntity1 == null) {
			return "null";
		}

		ExternalTestEntity1.Type type = externalTestEntity1.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("ExternalChildTestEntity1")) {
				return ExternalChildTestEntity1SerDes.toJSON(
					(ExternalChildTestEntity1)externalTestEntity1);
			}

			if (typeString.equals("ExternalChildTestEntity2")) {
				return ExternalChildTestEntity2SerDes.toJSON(
					(ExternalChildTestEntity2)externalTestEntity1);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		ExternalTestEntity1JSONParser externalTestEntity1JSONParser =
			new ExternalTestEntity1JSONParser();

		return externalTestEntity1JSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ExternalTestEntity1 externalTestEntity1) {

		if (externalTestEntity1 == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (externalTestEntity1.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(externalTestEntity1.getType()));
		}

		return map;
	}

	public static class ExternalTestEntity1JSONParser
		extends BaseJSONParser<ExternalTestEntity1> {

		@Override
		protected ExternalTestEntity1 createDTO() {
			return null;
		}

		@Override
		protected ExternalTestEntity1[] createDTOArray(int size) {
			return new ExternalTestEntity1[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public ExternalTestEntity1 parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("ExternalChildTestEntity1")) {
					return ExternalChildTestEntity1.toDTO(json);
				}

				if (typeString.equals("ExternalChildTestEntity2")) {
					return ExternalChildTestEntity2.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			ExternalTestEntity1 externalTestEntity1, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					externalTestEntity1.setType(
						ExternalTestEntity1.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:1874277060