/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.client.dto.v1_0.PermissionObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class PermissionObjectBulkSelectionActionSerDes {

	public static PermissionObjectBulkSelectionAction toDTO(String json) {
		PermissionObjectBulkSelectionActionJSONParser
			permissionObjectBulkSelectionActionJSONParser =
				new PermissionObjectBulkSelectionActionJSONParser();

		return permissionObjectBulkSelectionActionJSONParser.parseToDTO(json);
	}

	public static PermissionObjectBulkSelectionAction[] toDTOs(String json) {
		PermissionObjectBulkSelectionActionJSONParser
			permissionObjectBulkSelectionActionJSONParser =
				new PermissionObjectBulkSelectionActionJSONParser();

		return permissionObjectBulkSelectionActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PermissionObjectBulkSelectionAction
			permissionObjectBulkSelectionAction) {

		if (permissionObjectBulkSelectionAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (permissionObjectBulkSelectionAction.getConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configuration\": ");

			sb.append("\"");

			sb.append(
				_escape(
					permissionObjectBulkSelectionAction.getConfiguration()));

			sb.append("\"");
		}

		if (permissionObjectBulkSelectionAction.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 permissionObjectBulkSelectionAction.
						 getPermissions().length;
				 i++) {

				sb.append(
					permissionObjectBulkSelectionAction.getPermissions()[i]);

				if ((i + 1) < permissionObjectBulkSelectionAction.
						getPermissions().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (permissionObjectBulkSelectionAction.getRoleKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleKey\": ");

			sb.append("\"");

			sb.append(
				_escape(permissionObjectBulkSelectionAction.getRoleKey()));

			sb.append("\"");
		}

		if (permissionObjectBulkSelectionAction.getBulkActionItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0;
				 i < permissionObjectBulkSelectionAction.
					 getBulkActionItems().length;
				 i++) {

				sb.append(
					String.valueOf(
						permissionObjectBulkSelectionAction.getBulkActionItems()
							[i]));

				if ((i + 1) < permissionObjectBulkSelectionAction.
						getBulkActionItems().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (permissionObjectBulkSelectionAction.getSelectionScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectionScope\": ");

			sb.append(
				String.valueOf(
					permissionObjectBulkSelectionAction.getSelectionScope()));
		}

		if (permissionObjectBulkSelectionAction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(permissionObjectBulkSelectionAction.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PermissionObjectBulkSelectionActionJSONParser
			permissionObjectBulkSelectionActionJSONParser =
				new PermissionObjectBulkSelectionActionJSONParser();

		return permissionObjectBulkSelectionActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PermissionObjectBulkSelectionAction
			permissionObjectBulkSelectionAction) {

		if (permissionObjectBulkSelectionAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (permissionObjectBulkSelectionAction.getConfiguration() == null) {
			map.put("configuration", null);
		}
		else {
			map.put(
				"configuration",
				String.valueOf(
					permissionObjectBulkSelectionAction.getConfiguration()));
		}

		if (permissionObjectBulkSelectionAction.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(
					permissionObjectBulkSelectionAction.getPermissions()));
		}

		if (permissionObjectBulkSelectionAction.getRoleKey() == null) {
			map.put("roleKey", null);
		}
		else {
			map.put(
				"roleKey",
				String.valueOf(
					permissionObjectBulkSelectionAction.getRoleKey()));
		}

		if (permissionObjectBulkSelectionAction.getBulkActionItems() == null) {
			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(
					permissionObjectBulkSelectionAction.getBulkActionItems()));
		}

		if (permissionObjectBulkSelectionAction.getSelectionScope() == null) {
			map.put("selectionScope", null);
		}
		else {
			map.put(
				"selectionScope",
				String.valueOf(
					permissionObjectBulkSelectionAction.getSelectionScope()));
		}

		if (permissionObjectBulkSelectionAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(permissionObjectBulkSelectionAction.getType()));
		}

		return map;
	}

	public static class PermissionObjectBulkSelectionActionJSONParser
		extends BaseJSONParser<PermissionObjectBulkSelectionAction> {

		@Override
		protected PermissionObjectBulkSelectionAction createDTO() {
			return new PermissionObjectBulkSelectionAction();
		}

		@Override
		protected PermissionObjectBulkSelectionAction[] createDTOArray(
			int size) {

			return new PermissionObjectBulkSelectionAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "configuration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "bulkActionItems")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selectionScope")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PermissionObjectBulkSelectionAction
				permissionObjectBulkSelectionAction,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "configuration")) {
				if (jsonParserFieldValue != null) {
					permissionObjectBulkSelectionAction.setConfiguration(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.bulk.rest.client.permission.Permission[]
						permissionsArray =
							new
							com.liferay.bulk.rest.client.permission.Permission
								[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.bulk.rest.client.permission.Permission.
								toDTO((String)jsonParserFieldValues[i]);
					}

					permissionObjectBulkSelectionAction.setPermissions(
						permissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleKey")) {
				if (jsonParserFieldValue != null) {
					permissionObjectBulkSelectionAction.setRoleKey(
						(String)jsonParserFieldValue);
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

					permissionObjectBulkSelectionAction.setBulkActionItems(
						bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectionScope")) {
				if (jsonParserFieldValue != null) {
					permissionObjectBulkSelectionAction.setSelectionScope(
						SelectionScopeSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					permissionObjectBulkSelectionAction.setType(
						PermissionObjectBulkSelectionAction.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-1289597897