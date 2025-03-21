/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.MessageFormSubmissionResult;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class MessageFormSubmissionResultSerDes {

	public static MessageFormSubmissionResult toDTO(String json) {
		MessageFormSubmissionResultJSONParser
			messageFormSubmissionResultJSONParser =
				new MessageFormSubmissionResultJSONParser();

		return messageFormSubmissionResultJSONParser.parseToDTO(json);
	}

	public static MessageFormSubmissionResult[] toDTOs(String json) {
		MessageFormSubmissionResultJSONParser
			messageFormSubmissionResultJSONParser =
				new MessageFormSubmissionResultJSONParser();

		return messageFormSubmissionResultJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		MessageFormSubmissionResult messageFormSubmissionResult) {

		if (messageFormSubmissionResult == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (messageFormSubmissionResult.getMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"message\": ");

			sb.append(String.valueOf(messageFormSubmissionResult.getMessage()));
		}

		if (messageFormSubmissionResult.getMessageType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"messageType\": ");

			sb.append("\"");

			sb.append(messageFormSubmissionResult.getMessageType());

			sb.append("\"");
		}

		if (messageFormSubmissionResult.getShowNotification() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showNotification\": ");

			sb.append(messageFormSubmissionResult.getShowNotification());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MessageFormSubmissionResultJSONParser
			messageFormSubmissionResultJSONParser =
				new MessageFormSubmissionResultJSONParser();

		return messageFormSubmissionResultJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		MessageFormSubmissionResult messageFormSubmissionResult) {

		if (messageFormSubmissionResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (messageFormSubmissionResult.getMessage() == null) {
			map.put("message", null);
		}
		else {
			map.put(
				"message",
				String.valueOf(messageFormSubmissionResult.getMessage()));
		}

		if (messageFormSubmissionResult.getMessageType() == null) {
			map.put("messageType", null);
		}
		else {
			map.put(
				"messageType",
				String.valueOf(messageFormSubmissionResult.getMessageType()));
		}

		if (messageFormSubmissionResult.getShowNotification() == null) {
			map.put("showNotification", null);
		}
		else {
			map.put(
				"showNotification",
				String.valueOf(
					messageFormSubmissionResult.getShowNotification()));
		}

		return map;
	}

	public static class MessageFormSubmissionResultJSONParser
		extends BaseJSONParser<MessageFormSubmissionResult> {

		@Override
		protected MessageFormSubmissionResult createDTO() {
			return new MessageFormSubmissionResult();
		}

		@Override
		protected MessageFormSubmissionResult[] createDTOArray(int size) {
			return new MessageFormSubmissionResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "message")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "messageType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "showNotification")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			MessageFormSubmissionResult messageFormSubmissionResult,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "message")) {
				if (jsonParserFieldValue != null) {
					messageFormSubmissionResult.setMessage(
						FragmentInlineValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "messageType")) {
				if (jsonParserFieldValue != null) {
					messageFormSubmissionResult.setMessageType(
						MessageFormSubmissionResult.MessageType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "showNotification")) {
				if (jsonParserFieldValue != null) {
					messageFormSubmissionResult.setShowNotification(
						(Boolean)jsonParserFieldValue);
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