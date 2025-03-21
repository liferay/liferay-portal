/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.account.client.dto.v1_0.Body_1;
import com.liferay.headless.commerce.admin.account.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class Body_1SerDes {

	public static Body_1 toDTO(String json) {
		Body_1JSONParser body_1JSONParser = new Body_1JSONParser();

		return body_1JSONParser.parseToDTO(json);
	}

	public static Body_1[] toDTOs(String json) {
		Body_1JSONParser body_1JSONParser = new Body_1JSONParser();

		return body_1JSONParser.parseToDTOs(json);
	}

	public static String toJSON(Body_1 body_1) {
		if (body_1 == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (body_1.getLogo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logo\": ");

			sb.append("\"");

			sb.append(_escape(body_1.getLogo()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		Body_1JSONParser body_1JSONParser = new Body_1JSONParser();

		return body_1JSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Body_1 body_1) {
		if (body_1 == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (body_1.getLogo() == null) {
			map.put("logo", null);
		}
		else {
			map.put("logo", String.valueOf(body_1.getLogo()));
		}

		return map;
	}

	public static class Body_1JSONParser extends BaseJSONParser<Body_1> {

		@Override
		protected Body_1 createDTO() {
			return new Body_1();
		}

		@Override
		protected Body_1[] createDTOArray(int size) {
			return new Body_1[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "logo")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Body_1 body_1, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "logo")) {
				if (jsonParserFieldValue != null) {
					body_1.setLogo((String)jsonParserFieldValue);
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