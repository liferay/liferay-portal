/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.serdes.v1_0;

import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.UserSession;
import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.UserSessionEvent;
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
public class UserSessionSerDes {

	public static UserSession toDTO(String json) {
		UserSessionJSONParser userSessionJSONParser =
			new UserSessionJSONParser();

		return userSessionJSONParser.parseToDTO(json);
	}

	public static UserSession[] toDTOs(String json) {
		UserSessionJSONParser userSessionJSONParser =
			new UserSessionJSONParser();

		return userSessionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(UserSession userSession) {
		if (userSession == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (userSession.getUserName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userName\": ");

			sb.append("\"");

			sb.append(_escape(userSession.getUserName()));

			sb.append("\"");
		}

		if (userSession.getUserSessionEvents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userSessionEvents\": ");

			sb.append("[");

			for (int i = 0; i < userSession.getUserSessionEvents().length;
				 i++) {

				sb.append(
					String.valueOf(userSession.getUserSessionEvents()[i]));

				if ((i + 1) < userSession.getUserSessionEvents().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UserSessionJSONParser userSessionJSONParser =
			new UserSessionJSONParser();

		return userSessionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(UserSession userSession) {
		if (userSession == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (userSession.getUserName() == null) {
			map.put("userName", null);
		}
		else {
			map.put("userName", String.valueOf(userSession.getUserName()));
		}

		if (userSession.getUserSessionEvents() == null) {
			map.put("userSessionEvents", null);
		}
		else {
			map.put(
				"userSessionEvents",
				String.valueOf(userSession.getUserSessionEvents()));
		}

		return map;
	}

	public static class UserSessionJSONParser
		extends BaseJSONParser<UserSession> {

		@Override
		protected UserSession createDTO() {
			return new UserSession();
		}

		@Override
		protected UserSession[] createDTOArray(int size) {
			return new UserSession[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "userName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userSessionEvents")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			UserSession userSession, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "userName")) {
				if (jsonParserFieldValue != null) {
					userSession.setUserName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userSessionEvents")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					UserSessionEvent[] userSessionEventsArray =
						new UserSessionEvent[jsonParserFieldValues.length];

					for (int i = 0; i < userSessionEventsArray.length; i++) {
						userSessionEventsArray[i] =
							UserSessionEventSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					userSession.setUserSessionEvents(userSessionEventsArray);
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
// LIFERAY-REST-BUILDER-HASH:-349725533