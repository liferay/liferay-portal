/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.rest.client.serdes.v1_0;

import com.liferay.ai.hub.cell.rest.client.dto.v1_0.AuthorizationToken;
import com.liferay.ai.hub.cell.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public class AuthorizationTokenSerDes {

	public static AuthorizationToken toDTO(String json) {
		AuthorizationTokenJSONParser authorizationTokenJSONParser =
			new AuthorizationTokenJSONParser();

		return authorizationTokenJSONParser.parseToDTO(json);
	}

	public static AuthorizationToken[] toDTOs(String json) {
		AuthorizationTokenJSONParser authorizationTokenJSONParser =
			new AuthorizationTokenJSONParser();

		return authorizationTokenJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AuthorizationToken authorizationToken) {
		if (authorizationToken == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (authorizationToken.getAccessToken() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accessToken\": ");

			sb.append("\"");

			sb.append(_escape(authorizationToken.getAccessToken()));

			sb.append("\"");
		}

		if (authorizationToken.getScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append("\"");

			sb.append(_escape(authorizationToken.getScope()));

			sb.append("\"");
		}

		if (authorizationToken.getServiceURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"serviceURL\": ");

			sb.append("\"");

			sb.append(_escape(authorizationToken.getServiceURL()));

			sb.append("\"");
		}

		if (authorizationToken.getUserToken() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userToken\": ");

			sb.append("\"");

			sb.append(_escape(authorizationToken.getUserToken()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AuthorizationTokenJSONParser authorizationTokenJSONParser =
			new AuthorizationTokenJSONParser();

		return authorizationTokenJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AuthorizationToken authorizationToken) {

		if (authorizationToken == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (authorizationToken.getAccessToken() == null) {
			map.put("accessToken", null);
		}
		else {
			map.put(
				"accessToken",
				String.valueOf(authorizationToken.getAccessToken()));
		}

		if (authorizationToken.getScope() == null) {
			map.put("scope", null);
		}
		else {
			map.put("scope", String.valueOf(authorizationToken.getScope()));
		}

		if (authorizationToken.getServiceURL() == null) {
			map.put("serviceURL", null);
		}
		else {
			map.put(
				"serviceURL",
				String.valueOf(authorizationToken.getServiceURL()));
		}

		if (authorizationToken.getUserToken() == null) {
			map.put("userToken", null);
		}
		else {
			map.put(
				"userToken", String.valueOf(authorizationToken.getUserToken()));
		}

		return map;
	}

	public static class AuthorizationTokenJSONParser
		extends BaseJSONParser<AuthorizationToken> {

		@Override
		protected AuthorizationToken createDTO() {
			return new AuthorizationToken();
		}

		@Override
		protected AuthorizationToken[] createDTOArray(int size) {
			return new AuthorizationToken[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accessToken")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "serviceURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userToken")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AuthorizationToken authorizationToken, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accessToken")) {
				if (jsonParserFieldValue != null) {
					authorizationToken.setAccessToken(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				if (jsonParserFieldValue != null) {
					authorizationToken.setScope((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "serviceURL")) {
				if (jsonParserFieldValue != null) {
					authorizationToken.setServiceURL(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userToken")) {
				if (jsonParserFieldValue != null) {
					authorizationToken.setUserToken(
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
// LIFERAY-REST-BUILDER-HASH:-360317627