/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.client.serdes.v1_0;

import com.liferay.change.tracking.rest.client.dto.v1_0.CTRemote;
import com.liferay.change.tracking.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author David Truong
 * @generated
 */
@Generated("")
public class CTRemoteSerDes {

	public static CTRemote toDTO(String json) {
		CTRemoteJSONParser ctRemoteJSONParser = new CTRemoteJSONParser();

		return ctRemoteJSONParser.parseToDTO(json);
	}

	public static CTRemote[] toDTOs(String json) {
		CTRemoteJSONParser ctRemoteJSONParser = new CTRemoteJSONParser();

		return ctRemoteJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CTRemote ctRemote) {
		if (ctRemote == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ctRemote.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(ctRemote.getActions()));
		}

		if (ctRemote.getClientId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clientId\": ");

			sb.append("\"");

			sb.append(_escape(ctRemote.getClientId()));

			sb.append("\"");
		}

		if (ctRemote.getClientSecret() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clientSecret\": ");

			sb.append("\"");

			sb.append(_escape(ctRemote.getClientSecret()));

			sb.append("\"");
		}

		if (ctRemote.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(ctRemote.getDateCreated()));

			sb.append("\"");
		}

		if (ctRemote.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(ctRemote.getDateModified()));

			sb.append("\"");
		}

		if (ctRemote.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(ctRemote.getDescription()));

			sb.append("\"");
		}

		if (ctRemote.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(ctRemote.getId());
		}

		if (ctRemote.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(ctRemote.getName()));

			sb.append("\"");
		}

		if (ctRemote.getOwnerName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ownerName\": ");

			sb.append("\"");

			sb.append(_escape(ctRemote.getOwnerName()));

			sb.append("\"");
		}

		if (ctRemote.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(ctRemote.getUrl()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CTRemoteJSONParser ctRemoteJSONParser = new CTRemoteJSONParser();

		return ctRemoteJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CTRemote ctRemote) {
		if (ctRemote == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ctRemote.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(ctRemote.getActions()));
		}

		if (ctRemote.getClientId() == null) {
			map.put("clientId", null);
		}
		else {
			map.put("clientId", String.valueOf(ctRemote.getClientId()));
		}

		if (ctRemote.getClientSecret() == null) {
			map.put("clientSecret", null);
		}
		else {
			map.put("clientSecret", String.valueOf(ctRemote.getClientSecret()));
		}

		if (ctRemote.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(ctRemote.getDateCreated()));
		}

		if (ctRemote.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(ctRemote.getDateModified()));
		}

		if (ctRemote.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(ctRemote.getDescription()));
		}

		if (ctRemote.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(ctRemote.getId()));
		}

		if (ctRemote.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(ctRemote.getName()));
		}

		if (ctRemote.getOwnerName() == null) {
			map.put("ownerName", null);
		}
		else {
			map.put("ownerName", String.valueOf(ctRemote.getOwnerName()));
		}

		if (ctRemote.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put("url", String.valueOf(ctRemote.getUrl()));
		}

		return map;
	}

	public static class CTRemoteJSONParser extends BaseJSONParser<CTRemote> {

		@Override
		protected CTRemote createDTO() {
			return new CTRemote();
		}

		@Override
		protected CTRemote[] createDTOArray(int size) {
			return new CTRemote[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "clientId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "clientSecret")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ownerName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CTRemote ctRemote, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					ctRemote.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "clientId")) {
				if (jsonParserFieldValue != null) {
					ctRemote.setClientId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "clientSecret")) {
				if (jsonParserFieldValue != null) {
					ctRemote.setClientSecret((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					ctRemote.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					ctRemote.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					ctRemote.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					ctRemote.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					ctRemote.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ownerName")) {
				if (jsonParserFieldValue != null) {
					ctRemote.setOwnerName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					ctRemote.setUrl((String)jsonParserFieldValue);
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