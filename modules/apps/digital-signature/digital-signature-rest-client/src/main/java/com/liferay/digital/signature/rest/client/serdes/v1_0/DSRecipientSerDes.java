/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.client.serdes.v1_0;

import com.liferay.digital.signature.rest.client.dto.v1_0.DSRecipient;
import com.liferay.digital.signature.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class DSRecipientSerDes {

	public static DSRecipient toDTO(String json) {
		DSRecipientJSONParser dsRecipientJSONParser =
			new DSRecipientJSONParser();

		return dsRecipientJSONParser.parseToDTO(json);
	}

	public static DSRecipient[] toDTOs(String json) {
		DSRecipientJSONParser dsRecipientJSONParser =
			new DSRecipientJSONParser();

		return dsRecipientJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DSRecipient dsRecipient) {
		if (dsRecipient == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dsRecipient.getDsClientUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dsClientUserId\": ");

			sb.append("\"");

			sb.append(_escape(dsRecipient.getDsClientUserId()));

			sb.append("\"");
		}

		if (dsRecipient.getEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(dsRecipient.getEmailAddress()));

			sb.append("\"");
		}

		if (dsRecipient.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(dsRecipient.getId()));

			sb.append("\"");
		}

		if (dsRecipient.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(dsRecipient.getName()));

			sb.append("\"");
		}

		if (dsRecipient.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(dsRecipient.getStatus()));

			sb.append("\"");
		}

		if (dsRecipient.getTabs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tabs\": ");

			if (dsRecipient.getTabs() instanceof String) {
				sb.append("\"");
				sb.append((String)dsRecipient.getTabs());
				sb.append("\"");
			}
			else {
				sb.append(dsRecipient.getTabs());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DSRecipientJSONParser dsRecipientJSONParser =
			new DSRecipientJSONParser();

		return dsRecipientJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DSRecipient dsRecipient) {
		if (dsRecipient == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dsRecipient.getDsClientUserId() == null) {
			map.put("dsClientUserId", null);
		}
		else {
			map.put(
				"dsClientUserId",
				String.valueOf(dsRecipient.getDsClientUserId()));
		}

		if (dsRecipient.getEmailAddress() == null) {
			map.put("emailAddress", null);
		}
		else {
			map.put(
				"emailAddress", String.valueOf(dsRecipient.getEmailAddress()));
		}

		if (dsRecipient.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(dsRecipient.getId()));
		}

		if (dsRecipient.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(dsRecipient.getName()));
		}

		if (dsRecipient.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(dsRecipient.getStatus()));
		}

		if (dsRecipient.getTabs() == null) {
			map.put("tabs", null);
		}
		else {
			map.put("tabs", String.valueOf(dsRecipient.getTabs()));
		}

		return map;
	}

	public static class DSRecipientJSONParser
		extends BaseJSONParser<DSRecipient> {

		@Override
		protected DSRecipient createDTO() {
			return new DSRecipient();
		}

		@Override
		protected DSRecipient[] createDTOArray(int size) {
			return new DSRecipient[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dsClientUserId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tabs")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DSRecipient dsRecipient, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dsClientUserId")) {
				if (jsonParserFieldValue != null) {
					dsRecipient.setDsClientUserId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				if (jsonParserFieldValue != null) {
					dsRecipient.setEmailAddress((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					dsRecipient.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					dsRecipient.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					dsRecipient.setStatus((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tabs")) {
				if (jsonParserFieldValue != null) {
					dsRecipient.setTabs((Object)jsonParserFieldValue);
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