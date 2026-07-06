/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.client.serdes.v1_0;

import com.liferay.ai.hub.rest.client.dto.v1_0.Chatbot;
import com.liferay.ai.hub.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class ChatbotSerDes {

	public static Chatbot toDTO(String json) {
		ChatbotJSONParser chatbotJSONParser = new ChatbotJSONParser();

		return chatbotJSONParser.parseToDTO(json);
	}

	public static Chatbot[] toDTOs(String json) {
		ChatbotJSONParser chatbotJSONParser = new ChatbotJSONParser();

		return chatbotJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Chatbot chatbot) {
		if (chatbot == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (chatbot.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(chatbot.getActive());
		}

		if (chatbot.getAvatar() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"avatar\": ");

			sb.append(_toJSON(chatbot.getAvatar()));
		}

		if (chatbot.getDisclaimerMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"disclaimerMessage\": ");

			sb.append("\"");

			sb.append(_escape(chatbot.getDisclaimerMessage()));

			sb.append("\"");
		}

		if (chatbot.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(chatbot.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (chatbot.getIntroMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"introMessage\": ");

			sb.append("\"");

			sb.append(_escape(chatbot.getIntroMessage()));

			sb.append("\"");
		}

		if (chatbot.getNotificationMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"notificationMessage\": ");

			sb.append("\"");

			sb.append(_escape(chatbot.getNotificationMessage()));

			sb.append("\"");
		}

		if (chatbot.getPlaceholderMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placeholderMessage\": ");

			sb.append("\"");

			sb.append(_escape(chatbot.getPlaceholderMessage()));

			sb.append("\"");
		}

		if (chatbot.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(chatbot.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ChatbotJSONParser chatbotJSONParser = new ChatbotJSONParser();

		return chatbotJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Chatbot chatbot) {
		if (chatbot == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (chatbot.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(chatbot.getActive()));
		}

		if (chatbot.getAvatar() == null) {
			map.put("avatar", null);
		}
		else {
			map.put("avatar", String.valueOf(chatbot.getAvatar()));
		}

		if (chatbot.getDisclaimerMessage() == null) {
			map.put("disclaimerMessage", null);
		}
		else {
			map.put(
				"disclaimerMessage",
				String.valueOf(chatbot.getDisclaimerMessage()));
		}

		if (chatbot.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(chatbot.getExternalReferenceCode()));
		}

		if (chatbot.getIntroMessage() == null) {
			map.put("introMessage", null);
		}
		else {
			map.put("introMessage", String.valueOf(chatbot.getIntroMessage()));
		}

		if (chatbot.getNotificationMessage() == null) {
			map.put("notificationMessage", null);
		}
		else {
			map.put(
				"notificationMessage",
				String.valueOf(chatbot.getNotificationMessage()));
		}

		if (chatbot.getPlaceholderMessage() == null) {
			map.put("placeholderMessage", null);
		}
		else {
			map.put(
				"placeholderMessage",
				String.valueOf(chatbot.getPlaceholderMessage()));
		}

		if (chatbot.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(chatbot.getTitle()));
		}

		return map;
	}

	public static class ChatbotJSONParser extends BaseJSONParser<Chatbot> {

		@Override
		protected Chatbot createDTO() {
			return new Chatbot();
		}

		@Override
		protected Chatbot[] createDTOArray(int size) {
			return new Chatbot[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "avatar")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "disclaimerMessage")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "introMessage")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "notificationMessage")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "placeholderMessage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Chatbot chatbot, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					chatbot.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "avatar")) {
				if (jsonParserFieldValue != null) {
					chatbot.setAvatar((Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "disclaimerMessage")) {
				if (jsonParserFieldValue != null) {
					chatbot.setDisclaimerMessage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					chatbot.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "introMessage")) {
				if (jsonParserFieldValue != null) {
					chatbot.setIntroMessage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "notificationMessage")) {

				if (jsonParserFieldValue != null) {
					chatbot.setNotificationMessage(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "placeholderMessage")) {

				if (jsonParserFieldValue != null) {
					chatbot.setPlaceholderMessage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					chatbot.setTitle((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-1593085211