/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.GenericError;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

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
public class GenericErrorSerDes {

	public static GenericError toDTO(String json) {
		GenericErrorJSONParser genericErrorJSONParser =
			new GenericErrorJSONParser();

		return genericErrorJSONParser.parseToDTO(json);
	}

	public static GenericError[] toDTOs(String json) {
		GenericErrorJSONParser genericErrorJSONParser =
			new GenericErrorJSONParser();

		return genericErrorJSONParser.parseToDTOs(json);
	}

	public static String toJSON(GenericError genericError) {
		if (genericError == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (genericError.getMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"message\": ");

			sb.append("\"");

			sb.append(_escape(genericError.getMessage()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		GenericErrorJSONParser genericErrorJSONParser =
			new GenericErrorJSONParser();

		return genericErrorJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(GenericError genericError) {
		if (genericError == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (genericError.getMessage() == null) {
			map.put("message", null);
		}
		else {
			map.put("message", String.valueOf(genericError.getMessage()));
		}

		return map;
	}

	public static class GenericErrorJSONParser
		extends BaseJSONParser<GenericError> {

		@Override
		protected GenericError createDTO() {
			return new GenericError();
		}

		@Override
		protected GenericError[] createDTOArray(int size) {
			return new GenericError[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "message")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			GenericError genericError, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "message")) {
				if (jsonParserFieldValue != null) {
					genericError.setMessage((String)jsonParserFieldValue);
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