/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.AuthenticationScheme;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class AuthenticationSchemeSerDes {

	public static AuthenticationScheme toDTO(String json) {
		AuthenticationSchemeJSONParser authenticationSchemeJSONParser =
			new AuthenticationSchemeJSONParser();

		return authenticationSchemeJSONParser.parseToDTO(json);
	}

	public static AuthenticationScheme[] toDTOs(String json) {
		AuthenticationSchemeJSONParser authenticationSchemeJSONParser =
			new AuthenticationSchemeJSONParser();

		return authenticationSchemeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AuthenticationScheme authenticationScheme) {
		if (authenticationScheme == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (authenticationScheme.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(authenticationScheme.getDescription()));

			sb.append("\"");
		}

		if (authenticationScheme.getDocumentationUri() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentationUri\": ");

			sb.append("\"");

			sb.append(_escape(authenticationScheme.getDocumentationUri()));

			sb.append("\"");
		}

		if (authenticationScheme.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(authenticationScheme.getName()));

			sb.append("\"");
		}

		if (authenticationScheme.getPrimary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(authenticationScheme.getPrimary());
		}

		if (authenticationScheme.getSpecUri() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"specUri\": ");

			sb.append("\"");

			sb.append(_escape(authenticationScheme.getSpecUri()));

			sb.append("\"");
		}

		if (authenticationScheme.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(authenticationScheme.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AuthenticationSchemeJSONParser authenticationSchemeJSONParser =
			new AuthenticationSchemeJSONParser();

		return authenticationSchemeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AuthenticationScheme authenticationScheme) {

		if (authenticationScheme == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (authenticationScheme.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(authenticationScheme.getDescription()));
		}

		if (authenticationScheme.getDocumentationUri() == null) {
			map.put("documentationUri", null);
		}
		else {
			map.put(
				"documentationUri",
				String.valueOf(authenticationScheme.getDocumentationUri()));
		}

		if (authenticationScheme.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(authenticationScheme.getName()));
		}

		if (authenticationScheme.getPrimary() == null) {
			map.put("primary", null);
		}
		else {
			map.put(
				"primary", String.valueOf(authenticationScheme.getPrimary()));
		}

		if (authenticationScheme.getSpecUri() == null) {
			map.put("specUri", null);
		}
		else {
			map.put(
				"specUri", String.valueOf(authenticationScheme.getSpecUri()));
		}

		if (authenticationScheme.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(authenticationScheme.getType()));
		}

		return map;
	}

	public static class AuthenticationSchemeJSONParser
		extends BaseJSONParser<AuthenticationScheme> {

		@Override
		protected AuthenticationScheme createDTO() {
			return new AuthenticationScheme();
		}

		@Override
		protected AuthenticationScheme[] createDTOArray(int size) {
			return new AuthenticationScheme[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "documentationUri")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "specUri")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AuthenticationScheme authenticationScheme,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					authenticationScheme.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "documentationUri")) {
				if (jsonParserFieldValue != null) {
					authenticationScheme.setDocumentationUri(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					authenticationScheme.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				if (jsonParserFieldValue != null) {
					authenticationScheme.setPrimary(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "specUri")) {
				if (jsonParserFieldValue != null) {
					authenticationScheme.setSpecUri(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					authenticationScheme.setType((String)jsonParserFieldValue);
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