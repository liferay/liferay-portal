/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.serdes.v1_0;

import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.UserSession;
import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.UserSessions;
import com.liferay.site.dsr.analytics.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class UserSessionsSerDes {

	public static UserSessions toDTO(String json) {
		UserSessionsJSONParser userSessionsJSONParser =
			new UserSessionsJSONParser();

		return userSessionsJSONParser.parseToDTO(json);
	}

	public static UserSessions[] toDTOs(String json) {
		UserSessionsJSONParser userSessionsJSONParser =
			new UserSessionsJSONParser();

		return userSessionsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(UserSessions userSessions) {
		if (userSessions == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (userSessions.getTotalEvents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalEvents\": ");

			sb.append(userSessions.getTotalEvents());
		}

		if (userSessions.getUserSessions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userSessions\": ");

			sb.append("[");

			for (int i = 0; i < userSessions.getUserSessions().length; i++) {
				sb.append(String.valueOf(userSessions.getUserSessions()[i]));

				if ((i + 1) < userSessions.getUserSessions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UserSessionsJSONParser userSessionsJSONParser =
			new UserSessionsJSONParser();

		return userSessionsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(UserSessions userSessions) {
		if (userSessions == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (userSessions.getTotalEvents() == null) {
			map.put("totalEvents", null);
		}
		else {
			map.put(
				"totalEvents", String.valueOf(userSessions.getTotalEvents()));
		}

		if (userSessions.getUserSessions() == null) {
			map.put("userSessions", null);
		}
		else {
			map.put(
				"userSessions", String.valueOf(userSessions.getUserSessions()));
		}

		return map;
	}

	public static class UserSessionsJSONParser
		extends BaseJSONParser<UserSessions> {

		@Override
		protected UserSessions createDTO() {
			return new UserSessions();
		}

		@Override
		protected UserSessions[] createDTOArray(int size) {
			return new UserSessions[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "totalEvents")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userSessions")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			UserSessions userSessions, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "totalEvents")) {
				if (jsonParserFieldValue != null) {
					userSessions.setTotalEvents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userSessions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					UserSession[] userSessionsArray =
						new UserSession[jsonParserFieldValues.length];

					for (int i = 0; i < userSessionsArray.length; i++) {
						userSessionsArray[i] = UserSessionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					userSessions.setUserSessions(userSessionsArray);
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
// LIFERAY-REST-BUILDER-HASH:-1179704338