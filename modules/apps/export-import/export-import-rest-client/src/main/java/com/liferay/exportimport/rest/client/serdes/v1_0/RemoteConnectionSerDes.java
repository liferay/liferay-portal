/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.RemoteConnection;
import com.liferay.exportimport.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class RemoteConnectionSerDes {

	public static RemoteConnection toDTO(String json) {
		RemoteConnectionJSONParser remoteConnectionJSONParser =
			new RemoteConnectionJSONParser();

		return remoteConnectionJSONParser.parseToDTO(json);
	}

	public static RemoteConnection[] toDTOs(String json) {
		RemoteConnectionJSONParser remoteConnectionJSONParser =
			new RemoteConnectionJSONParser();

		return remoteConnectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(RemoteConnection remoteConnection) {
		if (remoteConnection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (remoteConnection.getAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"address\": ");

			sb.append("\"");

			sb.append(_escape(remoteConnection.getAddress()));

			sb.append("\"");
		}

		if (remoteConnection.getPathContext() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pathContext\": ");

			sb.append("\"");

			sb.append(_escape(remoteConnection.getPathContext()));

			sb.append("\"");
		}

		if (remoteConnection.getPort() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"port\": ");

			sb.append(remoteConnection.getPort());
		}

		if (remoteConnection.getSecureConnection() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"secureConnection\": ");

			sb.append(remoteConnection.getSecureConnection());
		}

		if (remoteConnection.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(remoteConnection.getSiteId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RemoteConnectionJSONParser remoteConnectionJSONParser =
			new RemoteConnectionJSONParser();

		return remoteConnectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(RemoteConnection remoteConnection) {
		if (remoteConnection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (remoteConnection.getAddress() == null) {
			map.put("address", null);
		}
		else {
			map.put("address", String.valueOf(remoteConnection.getAddress()));
		}

		if (remoteConnection.getPathContext() == null) {
			map.put("pathContext", null);
		}
		else {
			map.put(
				"pathContext",
				String.valueOf(remoteConnection.getPathContext()));
		}

		if (remoteConnection.getPort() == null) {
			map.put("port", null);
		}
		else {
			map.put("port", String.valueOf(remoteConnection.getPort()));
		}

		if (remoteConnection.getSecureConnection() == null) {
			map.put("secureConnection", null);
		}
		else {
			map.put(
				"secureConnection",
				String.valueOf(remoteConnection.getSecureConnection()));
		}

		if (remoteConnection.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(remoteConnection.getSiteId()));
		}

		return map;
	}

	public static class RemoteConnectionJSONParser
		extends BaseJSONParser<RemoteConnection> {

		@Override
		protected RemoteConnection createDTO() {
			return new RemoteConnection();
		}

		@Override
		protected RemoteConnection[] createDTOArray(int size) {
			return new RemoteConnection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "address")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pathContext")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "port")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "secureConnection")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			RemoteConnection remoteConnection, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "address")) {
				if (jsonParserFieldValue != null) {
					remoteConnection.setAddress((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pathContext")) {
				if (jsonParserFieldValue != null) {
					remoteConnection.setPathContext(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "port")) {
				if (jsonParserFieldValue != null) {
					remoteConnection.setPort(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "secureConnection")) {
				if (jsonParserFieldValue != null) {
					remoteConnection.setSecureConnection(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					remoteConnection.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:-1505641460