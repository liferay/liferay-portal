/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.PagePermission;
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
public class PagePermissionSerDes {

	public static PagePermission toDTO(String json) {
		PagePermissionJSONParser pagePermissionJSONParser =
			new PagePermissionJSONParser();

		return pagePermissionJSONParser.parseToDTO(json);
	}

	public static PagePermission[] toDTOs(String json) {
		PagePermissionJSONParser pagePermissionJSONParser =
			new PagePermissionJSONParser();

		return pagePermissionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PagePermission pagePermission) {
		if (pagePermission == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pagePermission.getActionKeys() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionKeys\": ");

			sb.append("[");

			for (int i = 0; i < pagePermission.getActionKeys().length; i++) {
				sb.append(_toJSON(pagePermission.getActionKeys()[i]));

				if ((i + 1) < pagePermission.getActionKeys().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pagePermission.getRoleKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleKey\": ");

			sb.append("\"");

			sb.append(_escape(pagePermission.getRoleKey()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PagePermissionJSONParser pagePermissionJSONParser =
			new PagePermissionJSONParser();

		return pagePermissionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PagePermission pagePermission) {
		if (pagePermission == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pagePermission.getActionKeys() == null) {
			map.put("actionKeys", null);
		}
		else {
			map.put(
				"actionKeys", String.valueOf(pagePermission.getActionKeys()));
		}

		if (pagePermission.getRoleKey() == null) {
			map.put("roleKey", null);
		}
		else {
			map.put("roleKey", String.valueOf(pagePermission.getRoleKey()));
		}

		return map;
	}

	public static class PagePermissionJSONParser
		extends BaseJSONParser<PagePermission> {

		@Override
		protected PagePermission createDTO() {
			return new PagePermission();
		}

		@Override
		protected PagePermission[] createDTOArray(int size) {
			return new PagePermission[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actionKeys")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleKey")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PagePermission pagePermission, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actionKeys")) {
				if (jsonParserFieldValue != null) {
					pagePermission.setActionKeys(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleKey")) {
				if (jsonParserFieldValue != null) {
					pagePermission.setRoleKey((String)jsonParserFieldValue);
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