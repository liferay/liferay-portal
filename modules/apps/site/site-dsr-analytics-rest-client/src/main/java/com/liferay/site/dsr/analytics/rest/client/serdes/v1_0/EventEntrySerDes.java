/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.serdes.v1_0;

import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.EventEntry;
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
public class EventEntrySerDes {

	public static EventEntry toDTO(String json) {
		EventEntryJSONParser eventEntryJSONParser = new EventEntryJSONParser();

		return eventEntryJSONParser.parseToDTO(json);
	}

	public static EventEntry[] toDTOs(String json) {
		EventEntryJSONParser eventEntryJSONParser = new EventEntryJSONParser();

		return eventEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(EventEntry eventEntry) {
		if (eventEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (eventEntry.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(_escape(eventEntry.getCreateDate()));

			sb.append("\"");
		}

		if (eventEntry.getEmailAddressHashed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddressHashed\": ");

			sb.append("\"");

			sb.append(_escape(eventEntry.getEmailAddressHashed()));

			sb.append("\"");
		}

		if (eventEntry.getIndividualName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualName\": ");

			sb.append("\"");

			sb.append(_escape(eventEntry.getIndividualName()));

			sb.append("\"");
		}

		if (eventEntry.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(eventEntry.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EventEntryJSONParser eventEntryJSONParser = new EventEntryJSONParser();

		return eventEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(EventEntry eventEntry) {
		if (eventEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (eventEntry.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put("createDate", String.valueOf(eventEntry.getCreateDate()));
		}

		if (eventEntry.getEmailAddressHashed() == null) {
			map.put("emailAddressHashed", null);
		}
		else {
			map.put(
				"emailAddressHashed",
				String.valueOf(eventEntry.getEmailAddressHashed()));
		}

		if (eventEntry.getIndividualName() == null) {
			map.put("individualName", null);
		}
		else {
			map.put(
				"individualName",
				String.valueOf(eventEntry.getIndividualName()));
		}

		if (eventEntry.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(eventEntry.getName()));
		}

		return map;
	}

	public static class EventEntryJSONParser
		extends BaseJSONParser<EventEntry> {

		@Override
		protected EventEntry createDTO() {
			return new EventEntry();
		}

		@Override
		protected EventEntry[] createDTOArray(int size) {
			return new EventEntry[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "emailAddressHashed")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "individualName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			EventEntry eventEntry, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					eventEntry.setCreateDate((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "emailAddressHashed")) {

				if (jsonParserFieldValue != null) {
					eventEntry.setEmailAddressHashed(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "individualName")) {
				if (jsonParserFieldValue != null) {
					eventEntry.setIndividualName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					eventEntry.setName((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-1927326672