/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.AssignToObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.BulkActionItem;
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
public class AssignToObjectBulkSelectionActionSerDes {

	public static AssignToObjectBulkSelectionAction toDTO(String json) {
		AssignToObjectBulkSelectionActionJSONParser
			assignToObjectBulkSelectionActionJSONParser =
				new AssignToObjectBulkSelectionActionJSONParser();

		return assignToObjectBulkSelectionActionJSONParser.parseToDTO(json);
	}

	public static AssignToObjectBulkSelectionAction[] toDTOs(String json) {
		AssignToObjectBulkSelectionActionJSONParser
			assignToObjectBulkSelectionActionJSONParser =
				new AssignToObjectBulkSelectionActionJSONParser();

		return assignToObjectBulkSelectionActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		AssignToObjectBulkSelectionAction assignToObjectBulkSelectionAction) {

		if (assignToObjectBulkSelectionAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assignToObjectBulkSelectionAction.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(
				_escape(assignToObjectBulkSelectionAction.getClassName()));

			sb.append("\"");
		}

		if (assignToObjectBulkSelectionAction.getExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					assignToObjectBulkSelectionAction.
						getExternalReferenceCode()));

			sb.append("\"");
		}

		if (assignToObjectBulkSelectionAction.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(assignToObjectBulkSelectionAction.getName()));

			sb.append("\"");
		}

		if (assignToObjectBulkSelectionAction.getBulkActionItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 assignToObjectBulkSelectionAction.
						 getBulkActionItems().length;
				 i++) {

				sb.append(
					String.valueOf(
						assignToObjectBulkSelectionAction.getBulkActionItems()
							[i]));

				if ((i + 1) < assignToObjectBulkSelectionAction.
						getBulkActionItems().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (assignToObjectBulkSelectionAction.getSelectionScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectionScope\": ");

			sb.append(
				String.valueOf(
					assignToObjectBulkSelectionAction.getSelectionScope()));
		}

		if (assignToObjectBulkSelectionAction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(assignToObjectBulkSelectionAction.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssignToObjectBulkSelectionActionJSONParser
			assignToObjectBulkSelectionActionJSONParser =
				new AssignToObjectBulkSelectionActionJSONParser();

		return assignToObjectBulkSelectionActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AssignToObjectBulkSelectionAction assignToObjectBulkSelectionAction) {

		if (assignToObjectBulkSelectionAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assignToObjectBulkSelectionAction.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(
					assignToObjectBulkSelectionAction.getClassName()));
		}

		if (assignToObjectBulkSelectionAction.getExternalReferenceCode() ==
				null) {

			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					assignToObjectBulkSelectionAction.
						getExternalReferenceCode()));
		}

		if (assignToObjectBulkSelectionAction.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(assignToObjectBulkSelectionAction.getName()));
		}

		if (assignToObjectBulkSelectionAction.getBulkActionItems() == null) {
			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(
					assignToObjectBulkSelectionAction.getBulkActionItems()));
		}

		if (assignToObjectBulkSelectionAction.getSelectionScope() == null) {
			map.put("selectionScope", null);
		}
		else {
			map.put(
				"selectionScope",
				String.valueOf(
					assignToObjectBulkSelectionAction.getSelectionScope()));
		}

		if (assignToObjectBulkSelectionAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(assignToObjectBulkSelectionAction.getType()));
		}

		return map;
	}

	public static class AssignToObjectBulkSelectionActionJSONParser
		extends BaseJSONParser<AssignToObjectBulkSelectionAction> {

		@Override
		protected AssignToObjectBulkSelectionAction createDTO() {
			return new AssignToObjectBulkSelectionAction();
		}

		@Override
		protected AssignToObjectBulkSelectionAction[] createDTOArray(int size) {
			return new AssignToObjectBulkSelectionAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
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
			AssignToObjectBulkSelectionAction assignToObjectBulkSelectionAction,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					assignToObjectBulkSelectionAction.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					assignToObjectBulkSelectionAction.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					assignToObjectBulkSelectionAction.setName(
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

					assignToObjectBulkSelectionAction.setBulkActionItems(
						bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectionScope")) {
				if (jsonParserFieldValue != null) {
					assignToObjectBulkSelectionAction.setSelectionScope(
						SelectionScopeSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					assignToObjectBulkSelectionAction.setType(
						AssignToObjectBulkSelectionAction.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-390987307