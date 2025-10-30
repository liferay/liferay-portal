/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.serdes.v1_0;

import com.liferay.headless.cms.client.dto.v1_0.BulkActionItem;
import com.liferay.headless.cms.client.dto.v1_0.DefaultPermissionBulkAction;
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
public class DefaultPermissionBulkActionSerDes {

	public static DefaultPermissionBulkAction toDTO(String json) {
		DefaultPermissionBulkActionJSONParser
			defaultPermissionBulkActionJSONParser =
				new DefaultPermissionBulkActionJSONParser();

		return defaultPermissionBulkActionJSONParser.parseToDTO(json);
	}

	public static DefaultPermissionBulkAction[] toDTOs(String json) {
		DefaultPermissionBulkActionJSONParser
			defaultPermissionBulkActionJSONParser =
				new DefaultPermissionBulkActionJSONParser();

		return defaultPermissionBulkActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DefaultPermissionBulkAction defaultPermissionBulkAction) {

		if (defaultPermissionBulkAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (defaultPermissionBulkAction.getDefaultPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultPermissions\": ");

			sb.append("\"");

			sb.append(
				_escape(defaultPermissionBulkAction.getDefaultPermissions()));

			sb.append("\"");
		}

		if (defaultPermissionBulkAction.getDepotGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"depotGroupId\": ");

			sb.append(defaultPermissionBulkAction.getDepotGroupId());
		}

		if (defaultPermissionBulkAction.getTreePath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"treePath\": ");

			sb.append("\"");

			sb.append(_escape(defaultPermissionBulkAction.getTreePath()));

			sb.append("\"");
		}

		if (defaultPermissionBulkAction.getBulkActionItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0;
				 i < defaultPermissionBulkAction.getBulkActionItems().length;
				 i++) {

				sb.append(
					String.valueOf(
						defaultPermissionBulkAction.getBulkActionItems()[i]));

				if ((i + 1) <
						defaultPermissionBulkAction.
							getBulkActionItems().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (defaultPermissionBulkAction.getSelectAll() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectAll\": ");

			sb.append(defaultPermissionBulkAction.getSelectAll());
		}

		if (defaultPermissionBulkAction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(defaultPermissionBulkAction.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DefaultPermissionBulkActionJSONParser
			defaultPermissionBulkActionJSONParser =
				new DefaultPermissionBulkActionJSONParser();

		return defaultPermissionBulkActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DefaultPermissionBulkAction defaultPermissionBulkAction) {

		if (defaultPermissionBulkAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (defaultPermissionBulkAction.getDefaultPermissions() == null) {
			map.put("defaultPermissions", null);
		}
		else {
			map.put(
				"defaultPermissions",
				String.valueOf(
					defaultPermissionBulkAction.getDefaultPermissions()));
		}

		if (defaultPermissionBulkAction.getDepotGroupId() == null) {
			map.put("depotGroupId", null);
		}
		else {
			map.put(
				"depotGroupId",
				String.valueOf(defaultPermissionBulkAction.getDepotGroupId()));
		}

		if (defaultPermissionBulkAction.getTreePath() == null) {
			map.put("treePath", null);
		}
		else {
			map.put(
				"treePath",
				String.valueOf(defaultPermissionBulkAction.getTreePath()));
		}

		if (defaultPermissionBulkAction.getBulkActionItems() == null) {
			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(
					defaultPermissionBulkAction.getBulkActionItems()));
		}

		if (defaultPermissionBulkAction.getSelectAll() == null) {
			map.put("selectAll", null);
		}
		else {
			map.put(
				"selectAll",
				String.valueOf(defaultPermissionBulkAction.getSelectAll()));
		}

		if (defaultPermissionBulkAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(defaultPermissionBulkAction.getType()));
		}

		return map;
	}

	public static class DefaultPermissionBulkActionJSONParser
		extends BaseJSONParser<DefaultPermissionBulkAction> {

		@Override
		protected DefaultPermissionBulkAction createDTO() {
			return new DefaultPermissionBulkAction();
		}

		@Override
		protected DefaultPermissionBulkAction[] createDTOArray(int size) {
			return new DefaultPermissionBulkAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "defaultPermissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "depotGroupId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "treePath")) {
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
			DefaultPermissionBulkAction defaultPermissionBulkAction,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultPermissions")) {
				if (jsonParserFieldValue != null) {
					defaultPermissionBulkAction.setDefaultPermissions(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "depotGroupId")) {
				if (jsonParserFieldValue != null) {
					defaultPermissionBulkAction.setDepotGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "treePath")) {
				if (jsonParserFieldValue != null) {
					defaultPermissionBulkAction.setTreePath(
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

					defaultPermissionBulkAction.setBulkActionItems(
						bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectAll")) {
				if (jsonParserFieldValue != null) {
					defaultPermissionBulkAction.setSelectAll(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					defaultPermissionBulkAction.setType(
						DefaultPermissionBulkAction.Type.create(
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