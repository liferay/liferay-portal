/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.WidgetInstance;
import com.liferay.headless.delivery.client.dto.v1_0.WidgetPermission;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WidgetInstanceSerDes {

	public static WidgetInstance toDTO(String json) {
		WidgetInstanceJSONParser widgetInstanceJSONParser =
			new WidgetInstanceJSONParser();

		return widgetInstanceJSONParser.parseToDTO(json);
	}

	public static WidgetInstance[] toDTOs(String json) {
		WidgetInstanceJSONParser widgetInstanceJSONParser =
			new WidgetInstanceJSONParser();

		return widgetInstanceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WidgetInstance widgetInstance) {
		if (widgetInstance == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (widgetInstance.getWidgetConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetConfig\": ");

			sb.append(_toJSON(widgetInstance.getWidgetConfig()));
		}

		if (widgetInstance.getWidgetInstanceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstanceId\": ");

			sb.append("\"");

			sb.append(_escape(widgetInstance.getWidgetInstanceId()));

			sb.append("\"");
		}

		if (widgetInstance.getWidgetName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetName\": ");

			sb.append("\"");

			sb.append(_escape(widgetInstance.getWidgetName()));

			sb.append("\"");
		}

		if (widgetInstance.getWidgetPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetPermissions\": ");

			sb.append("[");

			for (int i = 0; i < widgetInstance.getWidgetPermissions().length;
				 i++) {

				sb.append(
					String.valueOf(widgetInstance.getWidgetPermissions()[i]));

				if ((i + 1) < widgetInstance.getWidgetPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WidgetInstanceJSONParser widgetInstanceJSONParser =
			new WidgetInstanceJSONParser();

		return widgetInstanceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(WidgetInstance widgetInstance) {
		if (widgetInstance == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (widgetInstance.getWidgetConfig() == null) {
			map.put("widgetConfig", null);
		}
		else {
			map.put(
				"widgetConfig",
				String.valueOf(widgetInstance.getWidgetConfig()));
		}

		if (widgetInstance.getWidgetInstanceId() == null) {
			map.put("widgetInstanceId", null);
		}
		else {
			map.put(
				"widgetInstanceId",
				String.valueOf(widgetInstance.getWidgetInstanceId()));
		}

		if (widgetInstance.getWidgetName() == null) {
			map.put("widgetName", null);
		}
		else {
			map.put(
				"widgetName", String.valueOf(widgetInstance.getWidgetName()));
		}

		if (widgetInstance.getWidgetPermissions() == null) {
			map.put("widgetPermissions", null);
		}
		else {
			map.put(
				"widgetPermissions",
				String.valueOf(widgetInstance.getWidgetPermissions()));
		}

		return map;
	}

	public static class WidgetInstanceJSONParser
		extends BaseJSONParser<WidgetInstance> {

		@Override
		protected WidgetInstance createDTO() {
			return new WidgetInstance();
		}

		@Override
		protected WidgetInstance[] createDTOArray(int size) {
			return new WidgetInstance[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "widgetConfig")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstanceId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "widgetName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "widgetPermissions")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WidgetInstance widgetInstance, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "widgetConfig")) {
				if (jsonParserFieldValue != null) {
					widgetInstance.setWidgetConfig(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstanceId")) {
				if (jsonParserFieldValue != null) {
					widgetInstance.setWidgetInstanceId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetName")) {
				if (jsonParserFieldValue != null) {
					widgetInstance.setWidgetName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetPermissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					WidgetPermission[] widgetPermissionsArray =
						new WidgetPermission[jsonParserFieldValues.length];

					for (int i = 0; i < widgetPermissionsArray.length; i++) {
						widgetPermissionsArray[i] =
							WidgetPermissionSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					widgetInstance.setWidgetPermissions(widgetPermissionsArray);
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