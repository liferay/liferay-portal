/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.NotificationActionExecutionResult;
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
public class NotificationActionExecutionResultSerDes {

	public static NotificationActionExecutionResult toDTO(String json) {
		NotificationActionExecutionResultJSONParser
			notificationActionExecutionResultJSONParser =
				new NotificationActionExecutionResultJSONParser();

		return notificationActionExecutionResultJSONParser.parseToDTO(json);
	}

	public static NotificationActionExecutionResult[] toDTOs(String json) {
		NotificationActionExecutionResultJSONParser
			notificationActionExecutionResultJSONParser =
				new NotificationActionExecutionResultJSONParser();

		return notificationActionExecutionResultJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		NotificationActionExecutionResult notificationActionExecutionResult) {

		if (notificationActionExecutionResult == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (notificationActionExecutionResult.getReload() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reload\": ");

			sb.append(notificationActionExecutionResult.getReload());
		}

		if (notificationActionExecutionResult.getText() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"text\": ");

			sb.append(
				String.valueOf(notificationActionExecutionResult.getText()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		NotificationActionExecutionResultJSONParser
			notificationActionExecutionResultJSONParser =
				new NotificationActionExecutionResultJSONParser();

		return notificationActionExecutionResultJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		NotificationActionExecutionResult notificationActionExecutionResult) {

		if (notificationActionExecutionResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (notificationActionExecutionResult.getReload() == null) {
			map.put("reload", null);
		}
		else {
			map.put(
				"reload",
				String.valueOf(notificationActionExecutionResult.getReload()));
		}

		if (notificationActionExecutionResult.getText() == null) {
			map.put("text", null);
		}
		else {
			map.put(
				"text",
				String.valueOf(notificationActionExecutionResult.getText()));
		}

		return map;
	}

	public static class NotificationActionExecutionResultJSONParser
		extends BaseJSONParser<NotificationActionExecutionResult> {

		@Override
		protected NotificationActionExecutionResult createDTO() {
			return new NotificationActionExecutionResult();
		}

		@Override
		protected NotificationActionExecutionResult[] createDTOArray(int size) {
			return new NotificationActionExecutionResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "reload")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "text")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			NotificationActionExecutionResult notificationActionExecutionResult,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "reload")) {
				if (jsonParserFieldValue != null) {
					notificationActionExecutionResult.setReload(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "text")) {
				if (jsonParserFieldValue != null) {
					notificationActionExecutionResult.setText(
						FragmentInlineValueSerDes.toDTO(
							(String)jsonParserFieldValue));
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