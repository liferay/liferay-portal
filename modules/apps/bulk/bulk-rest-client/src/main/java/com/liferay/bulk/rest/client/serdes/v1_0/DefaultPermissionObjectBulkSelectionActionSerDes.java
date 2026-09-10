/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.client.dto.v1_0.DefaultPermissionObjectBulkSelectionAction;
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
public class DefaultPermissionObjectBulkSelectionActionSerDes {

	public static DefaultPermissionObjectBulkSelectionAction toDTO(
		String json) {

		DefaultPermissionObjectBulkSelectionActionJSONParser
			defaultPermissionObjectBulkSelectionActionJSONParser =
				new DefaultPermissionObjectBulkSelectionActionJSONParser();

		return defaultPermissionObjectBulkSelectionActionJSONParser.parseToDTO(
			json);
	}

	public static DefaultPermissionObjectBulkSelectionAction[] toDTOs(
		String json) {

		DefaultPermissionObjectBulkSelectionActionJSONParser
			defaultPermissionObjectBulkSelectionActionJSONParser =
				new DefaultPermissionObjectBulkSelectionActionJSONParser();

		return defaultPermissionObjectBulkSelectionActionJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		DefaultPermissionObjectBulkSelectionAction
			defaultPermissionObjectBulkSelectionAction) {

		if (defaultPermissionObjectBulkSelectionAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (defaultPermissionObjectBulkSelectionAction.
				getDefaultPermissions() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultPermissions\": ");

			sb.append("\"");

			sb.append(
				_escape(
					defaultPermissionObjectBulkSelectionAction.
						getDefaultPermissions()));

			sb.append("\"");
		}

		if (defaultPermissionObjectBulkSelectionAction.getDepotGroupId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"depotGroupId\": ");

			sb.append(
				defaultPermissionObjectBulkSelectionAction.getDepotGroupId());
		}

		if (defaultPermissionObjectBulkSelectionAction.getRoleKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleKey\": ");

			sb.append("\"");

			sb.append(
				_escape(
					defaultPermissionObjectBulkSelectionAction.getRoleKey()));

			sb.append("\"");
		}

		if (defaultPermissionObjectBulkSelectionAction.getTreePath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"treePath\": ");

			sb.append("\"");

			sb.append(
				_escape(
					defaultPermissionObjectBulkSelectionAction.getTreePath()));

			sb.append("\"");
		}

		if (defaultPermissionObjectBulkSelectionAction.getBulkActionItems() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0;
				 i < defaultPermissionObjectBulkSelectionAction.
					 getBulkActionItems().length;
				 i++) {

				sb.append(
					String.valueOf(
						defaultPermissionObjectBulkSelectionAction.
							getBulkActionItems()[i]));

				if ((i + 1) < defaultPermissionObjectBulkSelectionAction.
						getBulkActionItems().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (defaultPermissionObjectBulkSelectionAction.getSelectionScope() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectionScope\": ");

			sb.append(
				String.valueOf(
					defaultPermissionObjectBulkSelectionAction.
						getSelectionScope()));
		}

		if (defaultPermissionObjectBulkSelectionAction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(defaultPermissionObjectBulkSelectionAction.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DefaultPermissionObjectBulkSelectionActionJSONParser
			defaultPermissionObjectBulkSelectionActionJSONParser =
				new DefaultPermissionObjectBulkSelectionActionJSONParser();

		return defaultPermissionObjectBulkSelectionActionJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		DefaultPermissionObjectBulkSelectionAction
			defaultPermissionObjectBulkSelectionAction) {

		if (defaultPermissionObjectBulkSelectionAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (defaultPermissionObjectBulkSelectionAction.
				getDefaultPermissions() == null) {

			map.put("defaultPermissions", null);
		}
		else {
			map.put(
				"defaultPermissions",
				String.valueOf(
					defaultPermissionObjectBulkSelectionAction.
						getDefaultPermissions()));
		}

		if (defaultPermissionObjectBulkSelectionAction.getDepotGroupId() ==
				null) {

			map.put("depotGroupId", null);
		}
		else {
			map.put(
				"depotGroupId",
				String.valueOf(
					defaultPermissionObjectBulkSelectionAction.
						getDepotGroupId()));
		}

		if (defaultPermissionObjectBulkSelectionAction.getRoleKey() == null) {
			map.put("roleKey", null);
		}
		else {
			map.put(
				"roleKey",
				String.valueOf(
					defaultPermissionObjectBulkSelectionAction.getRoleKey()));
		}

		if (defaultPermissionObjectBulkSelectionAction.getTreePath() == null) {
			map.put("treePath", null);
		}
		else {
			map.put(
				"treePath",
				String.valueOf(
					defaultPermissionObjectBulkSelectionAction.getTreePath()));
		}

		if (defaultPermissionObjectBulkSelectionAction.getBulkActionItems() ==
				null) {

			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(
					defaultPermissionObjectBulkSelectionAction.
						getBulkActionItems()));
		}

		if (defaultPermissionObjectBulkSelectionAction.getSelectionScope() ==
				null) {

			map.put("selectionScope", null);
		}
		else {
			map.put(
				"selectionScope",
				String.valueOf(
					defaultPermissionObjectBulkSelectionAction.
						getSelectionScope()));
		}

		if (defaultPermissionObjectBulkSelectionAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					defaultPermissionObjectBulkSelectionAction.getType()));
		}

		return map;
	}

	public static class DefaultPermissionObjectBulkSelectionActionJSONParser
		extends BaseJSONParser<DefaultPermissionObjectBulkSelectionAction> {

		@Override
		protected DefaultPermissionObjectBulkSelectionAction createDTO() {
			return new DefaultPermissionObjectBulkSelectionAction();
		}

		@Override
		protected DefaultPermissionObjectBulkSelectionAction[] createDTOArray(
			int size) {

			return new DefaultPermissionObjectBulkSelectionAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "defaultPermissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "depotGroupId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "treePath")) {
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
			DefaultPermissionObjectBulkSelectionAction
				defaultPermissionObjectBulkSelectionAction,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultPermissions")) {
				if (jsonParserFieldValue != null) {
					defaultPermissionObjectBulkSelectionAction.
						setDefaultPermissions((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "depotGroupId")) {
				if (jsonParserFieldValue != null) {
					defaultPermissionObjectBulkSelectionAction.setDepotGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleKey")) {
				if (jsonParserFieldValue != null) {
					defaultPermissionObjectBulkSelectionAction.setRoleKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "treePath")) {
				if (jsonParserFieldValue != null) {
					defaultPermissionObjectBulkSelectionAction.setTreePath(
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

					defaultPermissionObjectBulkSelectionAction.
						setBulkActionItems(bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectionScope")) {
				if (jsonParserFieldValue != null) {
					defaultPermissionObjectBulkSelectionAction.
						setSelectionScope(
							SelectionScopeSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					defaultPermissionObjectBulkSelectionAction.setType(
						DefaultPermissionObjectBulkSelectionAction.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-841466035