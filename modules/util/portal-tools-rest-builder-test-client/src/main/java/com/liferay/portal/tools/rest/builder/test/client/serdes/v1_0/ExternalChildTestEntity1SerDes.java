/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ExternalChildTestEntity1;
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
public class ExternalChildTestEntity1SerDes {

	public static ExternalChildTestEntity1 toDTO(String json) {
		ExternalChildTestEntity1JSONParser externalChildTestEntity1JSONParser =
			new ExternalChildTestEntity1JSONParser();

		return externalChildTestEntity1JSONParser.parseToDTO(json);
	}

	public static ExternalChildTestEntity1[] toDTOs(String json) {
		ExternalChildTestEntity1JSONParser externalChildTestEntity1JSONParser =
			new ExternalChildTestEntity1JSONParser();

		return externalChildTestEntity1JSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ExternalChildTestEntity1 externalChildTestEntity1) {

		if (externalChildTestEntity1 == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (externalChildTestEntity1.getExternalProperty() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalProperty\": ");

			sb.append("\"");

			sb.append(_escape(externalChildTestEntity1.getExternalProperty()));

			sb.append("\"");
		}

		if (externalChildTestEntity1.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(externalChildTestEntity1.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ExternalChildTestEntity1JSONParser externalChildTestEntity1JSONParser =
			new ExternalChildTestEntity1JSONParser();

		return externalChildTestEntity1JSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ExternalChildTestEntity1 externalChildTestEntity1) {

		if (externalChildTestEntity1 == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (externalChildTestEntity1.getExternalProperty() == null) {
			map.put("externalProperty", null);
		}
		else {
			map.put(
				"externalProperty",
				String.valueOf(externalChildTestEntity1.getExternalProperty()));
		}

		if (externalChildTestEntity1.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(externalChildTestEntity1.getType()));
		}

		return map;
	}

	public static class ExternalChildTestEntity1JSONParser
		extends BaseJSONParser<ExternalChildTestEntity1> {

		@Override
		protected ExternalChildTestEntity1 createDTO() {
			return new ExternalChildTestEntity1();
		}

		@Override
		protected ExternalChildTestEntity1[] createDTOArray(int size) {
			return new ExternalChildTestEntity1[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalProperty")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ExternalChildTestEntity1 externalChildTestEntity1,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalProperty")) {
				if (jsonParserFieldValue != null) {
					externalChildTestEntity1.setExternalProperty(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					externalChildTestEntity1.setType(
						ExternalChildTestEntity1.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:408086694