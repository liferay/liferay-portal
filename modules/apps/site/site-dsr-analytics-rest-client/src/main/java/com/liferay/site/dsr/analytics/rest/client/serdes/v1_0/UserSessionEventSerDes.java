/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.serdes.v1_0;

import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.Property;
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
public class UserSessionEventSerDes {

	public static UserSessionEvent toDTO(String json) {
		UserSessionEventJSONParser userSessionEventJSONParser =
			new UserSessionEventJSONParser();

		return userSessionEventJSONParser.parseToDTO(json);
	}

	public static UserSessionEvent[] toDTOs(String json) {
		UserSessionEventJSONParser userSessionEventJSONParser =
			new UserSessionEventJSONParser();

		return userSessionEventJSONParser.parseToDTOs(json);
	}

	public static String toJSON(UserSessionEvent userSessionEvent) {
		if (userSessionEvent == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (userSessionEvent.getAssetTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(userSessionEvent.getAssetTitle()));

			sb.append("\"");
		}

		if (userSessionEvent.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(_escape(userSessionEvent.getCreateDate()));

			sb.append("\"");
		}

		if (userSessionEvent.getEmailAddressHashed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddressHashed\": ");

			sb.append("\"");

			sb.append(_escape(userSessionEvent.getEmailAddressHashed()));

			sb.append("\"");
		}

		if (userSessionEvent.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(userSessionEvent.getName()));

			sb.append("\"");
		}

		if (userSessionEvent.getProperties() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"properties\": ");

			sb.append("[");

			for (int i = 0; i < userSessionEvent.getProperties().length; i++) {
				sb.append(String.valueOf(userSessionEvent.getProperties()[i]));

				if ((i + 1) < userSessionEvent.getProperties().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UserSessionEventJSONParser userSessionEventJSONParser =
			new UserSessionEventJSONParser();

		return userSessionEventJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(UserSessionEvent userSessionEvent) {
		if (userSessionEvent == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (userSessionEvent.getAssetTitle() == null) {
			map.put("assetTitle", null);
		}
		else {
			map.put(
				"assetTitle", String.valueOf(userSessionEvent.getAssetTitle()));
		}

		if (userSessionEvent.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate", String.valueOf(userSessionEvent.getCreateDate()));
		}

		if (userSessionEvent.getEmailAddressHashed() == null) {
			map.put("emailAddressHashed", null);
		}
		else {
			map.put(
				"emailAddressHashed",
				String.valueOf(userSessionEvent.getEmailAddressHashed()));
		}

		if (userSessionEvent.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(userSessionEvent.getName()));
		}

		if (userSessionEvent.getProperties() == null) {
			map.put("properties", null);
		}
		else {
			map.put(
				"properties", String.valueOf(userSessionEvent.getProperties()));
		}

		return map;
	}

	public static class UserSessionEventJSONParser
		extends BaseJSONParser<UserSessionEvent> {

		@Override
		protected UserSessionEvent createDTO() {
			return new UserSessionEvent();
		}

		@Override
		protected UserSessionEvent[] createDTOArray(int size) {
			return new UserSessionEvent[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "emailAddressHashed")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "properties")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			UserSessionEvent userSessionEvent, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				if (jsonParserFieldValue != null) {
					userSessionEvent.setAssetTitle(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					userSessionEvent.setCreateDate(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "emailAddressHashed")) {

				if (jsonParserFieldValue != null) {
					userSessionEvent.setEmailAddressHashed(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					userSessionEvent.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "properties")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Property[] propertiesArray =
						new Property[jsonParserFieldValues.length];

					for (int i = 0; i < propertiesArray.length; i++) {
						propertiesArray[i] = PropertySerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					userSessionEvent.setProperties(propertiesArray);
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
// LIFERAY-REST-BUILDER-HASH:1658347318