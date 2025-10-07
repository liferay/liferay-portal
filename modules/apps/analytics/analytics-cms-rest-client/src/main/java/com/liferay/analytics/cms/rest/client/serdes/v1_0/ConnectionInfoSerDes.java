/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.serdes.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.ConnectionInfo;
import com.liferay.analytics.cms.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class ConnectionInfoSerDes {

	public static ConnectionInfo toDTO(String json) {
		ConnectionInfoJSONParser connectionInfoJSONParser =
			new ConnectionInfoJSONParser();

		return connectionInfoJSONParser.parseToDTO(json);
	}

	public static ConnectionInfo[] toDTOs(String json) {
		ConnectionInfoJSONParser connectionInfoJSONParser =
			new ConnectionInfoJSONParser();

		return connectionInfoJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ConnectionInfo connectionInfo) {
		if (connectionInfo == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (connectionInfo.getAdmin() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"admin\": ");

			sb.append(connectionInfo.getAdmin());
		}

		if (connectionInfo.getConnectedToAnalyticsCloud() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"connectedToAnalyticsCloud\": ");

			sb.append(connectionInfo.getConnectedToAnalyticsCloud());
		}

		if (connectionInfo.getConnectedToSpace() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"connectedToSpace\": ");

			sb.append(connectionInfo.getConnectedToSpace());
		}

		if (connectionInfo.getSiteSyncedToAnalyticsCloud() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteSyncedToAnalyticsCloud\": ");

			sb.append(connectionInfo.getSiteSyncedToAnalyticsCloud());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ConnectionInfoJSONParser connectionInfoJSONParser =
			new ConnectionInfoJSONParser();

		return connectionInfoJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ConnectionInfo connectionInfo) {
		if (connectionInfo == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (connectionInfo.getAdmin() == null) {
			map.put("admin", null);
		}
		else {
			map.put("admin", String.valueOf(connectionInfo.getAdmin()));
		}

		if (connectionInfo.getConnectedToAnalyticsCloud() == null) {
			map.put("connectedToAnalyticsCloud", null);
		}
		else {
			map.put(
				"connectedToAnalyticsCloud",
				String.valueOf(connectionInfo.getConnectedToAnalyticsCloud()));
		}

		if (connectionInfo.getConnectedToSpace() == null) {
			map.put("connectedToSpace", null);
		}
		else {
			map.put(
				"connectedToSpace",
				String.valueOf(connectionInfo.getConnectedToSpace()));
		}

		if (connectionInfo.getSiteSyncedToAnalyticsCloud() == null) {
			map.put("siteSyncedToAnalyticsCloud", null);
		}
		else {
			map.put(
				"siteSyncedToAnalyticsCloud",
				String.valueOf(connectionInfo.getSiteSyncedToAnalyticsCloud()));
		}

		return map;
	}

	public static class ConnectionInfoJSONParser
		extends BaseJSONParser<ConnectionInfo> {

		@Override
		protected ConnectionInfo createDTO() {
			return new ConnectionInfo();
		}

		@Override
		protected ConnectionInfo[] createDTOArray(int size) {
			return new ConnectionInfo[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "admin")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "connectedToAnalyticsCloud")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "connectedToSpace")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteSyncedToAnalyticsCloud")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ConnectionInfo connectionInfo, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "admin")) {
				if (jsonParserFieldValue != null) {
					connectionInfo.setAdmin((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "connectedToAnalyticsCloud")) {

				if (jsonParserFieldValue != null) {
					connectionInfo.setConnectedToAnalyticsCloud(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "connectedToSpace")) {
				if (jsonParserFieldValue != null) {
					connectionInfo.setConnectedToSpace(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteSyncedToAnalyticsCloud")) {

				if (jsonParserFieldValue != null) {
					connectionInfo.setSiteSyncedToAnalyticsCloud(
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