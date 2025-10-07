/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.serdes.v1_0;

import com.liferay.headless.cms.client.dto.v1_0.BulkActionItem;
import com.liferay.headless.cms.client.dto.v1_0.PermissionBulkAction;
import com.liferay.headless.cms.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
public class PermissionBulkActionSerDes {

	public static PermissionBulkAction toDTO(String json) {
		PermissionBulkActionJSONParser permissionBulkActionJSONParser =
			new PermissionBulkActionJSONParser();

		return permissionBulkActionJSONParser.parseToDTO(json);
	}

	public static PermissionBulkAction[] toDTOs(String json) {
		PermissionBulkActionJSONParser permissionBulkActionJSONParser =
			new PermissionBulkActionJSONParser();

		return permissionBulkActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PermissionBulkAction permissionBulkAction) {
		if (permissionBulkAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (permissionBulkAction.getConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configuration\": ");

			sb.append("\"");

			sb.append(_escape(permissionBulkAction.getConfiguration()));

			sb.append("\"");
		}

		if (permissionBulkAction.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < permissionBulkAction.getPermissions().length;
				 i++) {

				sb.append(permissionBulkAction.getPermissions()[i]);

				if ((i + 1) < permissionBulkAction.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (permissionBulkAction.getBulkActionItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0;
				 i < permissionBulkAction.getBulkActionItems().length; i++) {

				sb.append(
					String.valueOf(
						permissionBulkAction.getBulkActionItems()[i]));

				if ((i + 1) <
						permissionBulkAction.getBulkActionItems().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (permissionBulkAction.getSelectAll() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectAll\": ");

			sb.append(permissionBulkAction.getSelectAll());
		}

		if (permissionBulkAction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(permissionBulkAction.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PermissionBulkActionJSONParser permissionBulkActionJSONParser =
			new PermissionBulkActionJSONParser();

		return permissionBulkActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PermissionBulkAction permissionBulkAction) {

		if (permissionBulkAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (permissionBulkAction.getConfiguration() == null) {
			map.put("configuration", null);
		}
		else {
			map.put(
				"configuration",
				String.valueOf(permissionBulkAction.getConfiguration()));
		}

		if (permissionBulkAction.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(permissionBulkAction.getPermissions()));
		}

		if (permissionBulkAction.getBulkActionItems() == null) {
			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(permissionBulkAction.getBulkActionItems()));
		}

		if (permissionBulkAction.getSelectAll() == null) {
			map.put("selectAll", null);
		}
		else {
			map.put(
				"selectAll",
				String.valueOf(permissionBulkAction.getSelectAll()));
		}

		if (permissionBulkAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(permissionBulkAction.getType()));
		}

		return map;
	}

	public static class PermissionBulkActionJSONParser
		extends BaseJSONParser<PermissionBulkAction> {

		@Override
		protected PermissionBulkAction createDTO() {
			return new PermissionBulkAction();
		}

		@Override
		protected PermissionBulkAction[] createDTOArray(int size) {
			return new PermissionBulkAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "configuration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "bulkActionItems")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selectAll")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PermissionBulkAction permissionBulkAction,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "configuration")) {
				if (jsonParserFieldValue != null) {
					permissionBulkAction.setConfiguration(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.cms.client.permission.Permission[]
						permissionsArray = new
						com.liferay.headless.cms.client.permission.Permission
							[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.cms.client.permission.
								Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					permissionBulkAction.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "bulkActionItems")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					BulkActionItem[] bulkActionItemsArray =
						new BulkActionItem[jsonParserFieldValues.length];

					for (int i = 0; i < bulkActionItemsArray.length; i++) {
						bulkActionItemsArray[i] = BulkActionItemSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					permissionBulkAction.setBulkActionItems(
						bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectAll")) {
				if (jsonParserFieldValue != null) {
					permissionBulkAction.setSelectAll(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					permissionBulkAction.setType(
						PermissionBulkAction.Type.create(
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