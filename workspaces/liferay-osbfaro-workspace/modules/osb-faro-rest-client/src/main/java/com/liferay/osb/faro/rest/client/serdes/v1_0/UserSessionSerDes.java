/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.Event;
import com.liferay.osb.faro.rest.client.dto.v1_0.UserSession;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Leslie Wong
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

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (userSession.getBecameKnown() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"becameKnown\": ");

			sb.append(userSession.getBecameKnown());
		}

		if (userSession.getBrowserName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"browserName\": ");

			sb.append("\"");

			sb.append(_escape(userSession.getBrowserName()));

			sb.append("\"");
		}

		if (userSession.getCompleteDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completeDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(userSession.getCompleteDate()));

			sb.append("\"");
		}

		if (userSession.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(userSession.getCreateDate()));

			sb.append("\"");
		}

		if (userSession.getDeviceType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deviceType\": ");

			sb.append("\"");

			sb.append(_escape(userSession.getDeviceType()));

			sb.append("\"");
		}

		if (userSession.getEvents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"events\": ");

			sb.append("[");

			for (int i = 0; i < userSession.getEvents().length; i++) {
				sb.append(String.valueOf(userSession.getEvents()[i]));

				if ((i + 1) < userSession.getEvents().length) {
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

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (userSession.getBecameKnown() == null) {
			map.put("becameKnown", null);
		}
		else {
			map.put(
				"becameKnown", String.valueOf(userSession.getBecameKnown()));
		}

		if (userSession.getBrowserName() == null) {
			map.put("browserName", null);
		}
		else {
			map.put(
				"browserName", String.valueOf(userSession.getBrowserName()));
		}

		if (userSession.getCompleteDate() == null) {
			map.put("completeDate", null);
		}
		else {
			map.put(
				"completeDate",
				liferayToJSONDateFormat.format(userSession.getCompleteDate()));
		}

		if (userSession.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(userSession.getCreateDate()));
		}

		if (userSession.getDeviceType() == null) {
			map.put("deviceType", null);
		}
		else {
			map.put("deviceType", String.valueOf(userSession.getDeviceType()));
		}

		if (userSession.getEvents() == null) {
			map.put("events", null);
		}
		else {
			map.put("events", String.valueOf(userSession.getEvents()));
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
			if (Objects.equals(jsonParserFieldName, "becameKnown")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "browserName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "completeDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deviceType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "events")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			UserSession userSession, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "becameKnown")) {
				if (jsonParserFieldValue != null) {
					userSession.setBecameKnown((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "browserName")) {
				if (jsonParserFieldValue != null) {
					userSession.setBrowserName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "completeDate")) {
				if (jsonParserFieldValue != null) {
					userSession.setCompleteDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					userSession.setCreateDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deviceType")) {
				if (jsonParserFieldValue != null) {
					userSession.setDeviceType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "events")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Event[] eventsArray =
						new Event[jsonParserFieldValues.length];

					for (int i = 0; i < eventsArray.length; i++) {
						eventsArray[i] = EventSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					userSession.setEvents(eventsArray);
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
// LIFERAY-REST-BUILDER-HASH:861380507