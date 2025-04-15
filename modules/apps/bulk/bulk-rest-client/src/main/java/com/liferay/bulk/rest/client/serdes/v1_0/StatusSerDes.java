/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.Status;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class StatusSerDes {

	public static Status toDTO(String json) {
		StatusJSONParser statusJSONParser = new StatusJSONParser();

		return statusJSONParser.parseToDTO(json);
	}

	public static Status[] toDTOs(String json) {
		StatusJSONParser statusJSONParser = new StatusJSONParser();

		return statusJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Status status) {
		if (status == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (status.getActionInProgress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionInProgress\": ");

			sb.append(status.getActionInProgress());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		StatusJSONParser statusJSONParser = new StatusJSONParser();

		return statusJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Status status) {
		if (status == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (status.getActionInProgress() == null) {
			map.put("actionInProgress", null);
		}
		else {
			map.put(
				"actionInProgress",
				String.valueOf(status.getActionInProgress()));
		}

		return map;
	}

	public static class StatusJSONParser extends BaseJSONParser<Status> {

		@Override
		protected Status createDTO() {
			return new Status();
		}

		@Override
		protected Status[] createDTOArray(int size) {
			return new Status[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actionInProgress")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Status status, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actionInProgress")) {
				if (jsonParserFieldValue != null) {
					status.setActionInProgress((Boolean)jsonParserFieldValue);
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