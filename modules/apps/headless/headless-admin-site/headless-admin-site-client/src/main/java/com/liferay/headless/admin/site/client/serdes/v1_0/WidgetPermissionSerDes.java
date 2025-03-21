/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPermission;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class WidgetPermissionSerDes {

	public static WidgetPermission toDTO(String json) {
		WidgetPermissionJSONParser widgetPermissionJSONParser =
			new WidgetPermissionJSONParser();

		return widgetPermissionJSONParser.parseToDTO(json);
	}

	public static WidgetPermission[] toDTOs(String json) {
		WidgetPermissionJSONParser widgetPermissionJSONParser =
			new WidgetPermissionJSONParser();

		return widgetPermissionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WidgetPermission widgetPermission) {
		if (widgetPermission == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (widgetPermission.getActionIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionIds\": ");

			sb.append("[");

			for (int i = 0; i < widgetPermission.getActionIds().length; i++) {
				sb.append(_toJSON(widgetPermission.getActionIds()[i]));

				if ((i + 1) < widgetPermission.getActionIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (widgetPermission.getRoleName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleName\": ");

			sb.append("\"");

			sb.append(_escape(widgetPermission.getRoleName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WidgetPermissionJSONParser widgetPermissionJSONParser =
			new WidgetPermissionJSONParser();

		return widgetPermissionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(WidgetPermission widgetPermission) {
		if (widgetPermission == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (widgetPermission.getActionIds() == null) {
			map.put("actionIds", null);
		}
		else {
			map.put(
				"actionIds", String.valueOf(widgetPermission.getActionIds()));
		}

		if (widgetPermission.getRoleName() == null) {
			map.put("roleName", null);
		}
		else {
			map.put("roleName", String.valueOf(widgetPermission.getRoleName()));
		}

		return map;
	}

	public static class WidgetPermissionJSONParser
		extends BaseJSONParser<WidgetPermission> {

		@Override
		protected WidgetPermission createDTO() {
			return new WidgetPermission();
		}

		@Override
		protected WidgetPermission[] createDTOArray(int size) {
			return new WidgetPermission[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WidgetPermission widgetPermission, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				if (jsonParserFieldValue != null) {
					widgetPermission.setActionIds(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleName")) {
				if (jsonParserFieldValue != null) {
					widgetPermission.setRoleName((String)jsonParserFieldValue);
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