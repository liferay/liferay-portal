/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.RolePermission;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

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
public class RolePermissionSerDes {

	public static RolePermission toDTO(String json) {
		RolePermissionJSONParser rolePermissionJSONParser =
			new RolePermissionJSONParser();

		return rolePermissionJSONParser.parseToDTO(json);
	}

	public static RolePermission[] toDTOs(String json) {
		RolePermissionJSONParser rolePermissionJSONParser =
			new RolePermissionJSONParser();

		return rolePermissionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(RolePermission rolePermission) {
		if (rolePermission == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (rolePermission.getActionIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionIds\": ");

			sb.append("[");

			for (int i = 0; i < rolePermission.getActionIds().length; i++) {
				sb.append(_toJSON(rolePermission.getActionIds()[i]));

				if ((i + 1) < rolePermission.getActionIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (rolePermission.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(rolePermission.getId());
		}

		if (rolePermission.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(rolePermission.getLabel()));

			sb.append("\"");
		}

		if (rolePermission.getPrimaryKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primaryKey\": ");

			sb.append("\"");

			sb.append(_escape(rolePermission.getPrimaryKey()));

			sb.append("\"");
		}

		if (rolePermission.getResourceName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resourceName\": ");

			sb.append("\"");

			sb.append(_escape(rolePermission.getResourceName()));

			sb.append("\"");
		}

		if (rolePermission.getRoleId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleId\": ");

			sb.append(rolePermission.getRoleId());
		}

		if (rolePermission.getScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append(rolePermission.getScope());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RolePermissionJSONParser rolePermissionJSONParser =
			new RolePermissionJSONParser();

		return rolePermissionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(RolePermission rolePermission) {
		if (rolePermission == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (rolePermission.getActionIds() == null) {
			map.put("actionIds", null);
		}
		else {
			map.put("actionIds", String.valueOf(rolePermission.getActionIds()));
		}

		if (rolePermission.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(rolePermission.getId()));
		}

		if (rolePermission.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(rolePermission.getLabel()));
		}

		if (rolePermission.getPrimaryKey() == null) {
			map.put("primaryKey", null);
		}
		else {
			map.put(
				"primaryKey", String.valueOf(rolePermission.getPrimaryKey()));
		}

		if (rolePermission.getResourceName() == null) {
			map.put("resourceName", null);
		}
		else {
			map.put(
				"resourceName",
				String.valueOf(rolePermission.getResourceName()));
		}

		if (rolePermission.getRoleId() == null) {
			map.put("roleId", null);
		}
		else {
			map.put("roleId", String.valueOf(rolePermission.getRoleId()));
		}

		if (rolePermission.getScope() == null) {
			map.put("scope", null);
		}
		else {
			map.put("scope", String.valueOf(rolePermission.getScope()));
		}

		return map;
	}

	public static class RolePermissionJSONParser
		extends BaseJSONParser<RolePermission> {

		@Override
		protected RolePermission createDTO() {
			return new RolePermission();
		}

		@Override
		protected RolePermission[] createDTOArray(int size) {
			return new RolePermission[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "primaryKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "resourceName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			RolePermission rolePermission, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				if (jsonParserFieldValue != null) {
					rolePermission.setActionIds(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					rolePermission.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					rolePermission.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "primaryKey")) {
				if (jsonParserFieldValue != null) {
					rolePermission.setPrimaryKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "resourceName")) {
				if (jsonParserFieldValue != null) {
					rolePermission.setResourceName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleId")) {
				if (jsonParserFieldValue != null) {
					rolePermission.setRoleId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				if (jsonParserFieldValue != null) {
					rolePermission.setScope(
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