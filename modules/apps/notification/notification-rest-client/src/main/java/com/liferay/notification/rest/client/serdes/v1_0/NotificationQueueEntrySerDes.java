/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.client.serdes.v1_0;

import com.liferay.notification.rest.client.dto.v1_0.NotificationQueueEntry;
import com.liferay.notification.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
public class NotificationQueueEntrySerDes {

	public static NotificationQueueEntry toDTO(String json) {
		NotificationQueueEntryJSONParser notificationQueueEntryJSONParser =
			new NotificationQueueEntryJSONParser();

		return notificationQueueEntryJSONParser.parseToDTO(json);
	}

	public static NotificationQueueEntry[] toDTOs(String json) {
		NotificationQueueEntryJSONParser notificationQueueEntryJSONParser =
			new NotificationQueueEntryJSONParser();

		return notificationQueueEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(NotificationQueueEntry notificationQueueEntry) {
		if (notificationQueueEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (notificationQueueEntry.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(notificationQueueEntry.getActions()));
		}

		if (notificationQueueEntry.getBody() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"body\": ");

			sb.append("\"");

			sb.append(_escape(notificationQueueEntry.getBody()));

			sb.append("\"");
		}

		if (notificationQueueEntry.getFromName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fromName\": ");

			sb.append("\"");

			sb.append(_escape(notificationQueueEntry.getFromName()));

			sb.append("\"");
		}

		if (notificationQueueEntry.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(notificationQueueEntry.getId());
		}

		if (notificationQueueEntry.getRecipients() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"recipients\": ");

			sb.append("[");

			for (int i = 0; i < notificationQueueEntry.getRecipients().length;
				 i++) {

				sb.append(_toJSON(notificationQueueEntry.getRecipients()[i]));

				if ((i + 1) < notificationQueueEntry.getRecipients().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (notificationQueueEntry.getRecipientsSummary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"recipientsSummary\": ");

			sb.append("\"");

			sb.append(_escape(notificationQueueEntry.getRecipientsSummary()));

			sb.append("\"");
		}

		if (notificationQueueEntry.getSentDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sentDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					notificationQueueEntry.getSentDate()));

			sb.append("\"");
		}

		if (notificationQueueEntry.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(notificationQueueEntry.getStatus());
		}

		if (notificationQueueEntry.getSubject() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subject\": ");

			sb.append("\"");

			sb.append(_escape(notificationQueueEntry.getSubject()));

			sb.append("\"");
		}

		if (notificationQueueEntry.getTriggerBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"triggerBy\": ");

			sb.append("\"");

			sb.append(_escape(notificationQueueEntry.getTriggerBy()));

			sb.append("\"");
		}

		if (notificationQueueEntry.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(notificationQueueEntry.getType()));

			sb.append("\"");
		}

		if (notificationQueueEntry.getTypeLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeLabel\": ");

			sb.append("\"");

			sb.append(_escape(notificationQueueEntry.getTypeLabel()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		NotificationQueueEntryJSONParser notificationQueueEntryJSONParser =
			new NotificationQueueEntryJSONParser();

		return notificationQueueEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		NotificationQueueEntry notificationQueueEntry) {

		if (notificationQueueEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (notificationQueueEntry.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(notificationQueueEntry.getActions()));
		}

		if (notificationQueueEntry.getBody() == null) {
			map.put("body", null);
		}
		else {
			map.put("body", String.valueOf(notificationQueueEntry.getBody()));
		}

		if (notificationQueueEntry.getFromName() == null) {
			map.put("fromName", null);
		}
		else {
			map.put(
				"fromName",
				String.valueOf(notificationQueueEntry.getFromName()));
		}

		if (notificationQueueEntry.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(notificationQueueEntry.getId()));
		}

		if (notificationQueueEntry.getRecipients() == null) {
			map.put("recipients", null);
		}
		else {
			map.put(
				"recipients",
				String.valueOf(notificationQueueEntry.getRecipients()));
		}

		if (notificationQueueEntry.getRecipientsSummary() == null) {
			map.put("recipientsSummary", null);
		}
		else {
			map.put(
				"recipientsSummary",
				String.valueOf(notificationQueueEntry.getRecipientsSummary()));
		}

		if (notificationQueueEntry.getSentDate() == null) {
			map.put("sentDate", null);
		}
		else {
			map.put(
				"sentDate",
				liferayToJSONDateFormat.format(
					notificationQueueEntry.getSentDate()));
		}

		if (notificationQueueEntry.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put(
				"status", String.valueOf(notificationQueueEntry.getStatus()));
		}

		if (notificationQueueEntry.getSubject() == null) {
			map.put("subject", null);
		}
		else {
			map.put(
				"subject", String.valueOf(notificationQueueEntry.getSubject()));
		}

		if (notificationQueueEntry.getTriggerBy() == null) {
			map.put("triggerBy", null);
		}
		else {
			map.put(
				"triggerBy",
				String.valueOf(notificationQueueEntry.getTriggerBy()));
		}

		if (notificationQueueEntry.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(notificationQueueEntry.getType()));
		}

		if (notificationQueueEntry.getTypeLabel() == null) {
			map.put("typeLabel", null);
		}
		else {
			map.put(
				"typeLabel",
				String.valueOf(notificationQueueEntry.getTypeLabel()));
		}

		return map;
	}

	public static class NotificationQueueEntryJSONParser
		extends BaseJSONParser<NotificationQueueEntry> {

		@Override
		protected NotificationQueueEntry createDTO() {
			return new NotificationQueueEntry();
		}

		@Override
		protected NotificationQueueEntry[] createDTOArray(int size) {
			return new NotificationQueueEntry[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "body")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fromName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "recipients")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "recipientsSummary")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sentDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subject")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "triggerBy")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "typeLabel")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			NotificationQueueEntry notificationQueueEntry,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "body")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setBody(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fromName")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setFromName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "recipients")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setRecipients(
						(Object[])jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "recipientsSummary")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setRecipientsSummary(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sentDate")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setSentDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subject")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setSubject(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "triggerBy")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setTriggerBy(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setType(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "typeLabel")) {
				if (jsonParserFieldValue != null) {
					notificationQueueEntry.setTypeLabel(
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