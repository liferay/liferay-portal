/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.client.dto.v1_0.DueDateObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class DueDateObjectBulkSelectionActionSerDes {

	public static DueDateObjectBulkSelectionAction toDTO(String json) {
		DueDateObjectBulkSelectionActionJSONParser
			dueDateObjectBulkSelectionActionJSONParser =
				new DueDateObjectBulkSelectionActionJSONParser();

		return dueDateObjectBulkSelectionActionJSONParser.parseToDTO(json);
	}

	public static DueDateObjectBulkSelectionAction[] toDTOs(String json) {
		DueDateObjectBulkSelectionActionJSONParser
			dueDateObjectBulkSelectionActionJSONParser =
				new DueDateObjectBulkSelectionActionJSONParser();

		return dueDateObjectBulkSelectionActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DueDateObjectBulkSelectionAction dueDateObjectBulkSelectionAction) {

		if (dueDateObjectBulkSelectionAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (dueDateObjectBulkSelectionAction.getDueDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dueDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					dueDateObjectBulkSelectionAction.getDueDate()));

			sb.append("\"");
		}

		if (dueDateObjectBulkSelectionAction.getBulkActionItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 dueDateObjectBulkSelectionAction.
						 getBulkActionItems().length;
				 i++) {

				sb.append(
					String.valueOf(
						dueDateObjectBulkSelectionAction.getBulkActionItems()
							[i]));

				if ((i + 1) < dueDateObjectBulkSelectionAction.
						getBulkActionItems().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dueDateObjectBulkSelectionAction.getSelectionScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectionScope\": ");

			sb.append(
				String.valueOf(
					dueDateObjectBulkSelectionAction.getSelectionScope()));
		}

		if (dueDateObjectBulkSelectionAction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(dueDateObjectBulkSelectionAction.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DueDateObjectBulkSelectionActionJSONParser
			dueDateObjectBulkSelectionActionJSONParser =
				new DueDateObjectBulkSelectionActionJSONParser();

		return dueDateObjectBulkSelectionActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DueDateObjectBulkSelectionAction dueDateObjectBulkSelectionAction) {

		if (dueDateObjectBulkSelectionAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (dueDateObjectBulkSelectionAction.getDueDate() == null) {
			map.put("dueDate", null);
		}
		else {
			map.put(
				"dueDate",
				liferayToJSONDateFormat.format(
					dueDateObjectBulkSelectionAction.getDueDate()));
		}

		if (dueDateObjectBulkSelectionAction.getBulkActionItems() == null) {
			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(
					dueDateObjectBulkSelectionAction.getBulkActionItems()));
		}

		if (dueDateObjectBulkSelectionAction.getSelectionScope() == null) {
			map.put("selectionScope", null);
		}
		else {
			map.put(
				"selectionScope",
				String.valueOf(
					dueDateObjectBulkSelectionAction.getSelectionScope()));
		}

		if (dueDateObjectBulkSelectionAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(dueDateObjectBulkSelectionAction.getType()));
		}

		return map;
	}

	public static class DueDateObjectBulkSelectionActionJSONParser
		extends BaseJSONParser<DueDateObjectBulkSelectionAction> {

		@Override
		protected DueDateObjectBulkSelectionAction createDTO() {
			return new DueDateObjectBulkSelectionAction();
		}

		@Override
		protected DueDateObjectBulkSelectionAction[] createDTOArray(int size) {
			return new DueDateObjectBulkSelectionAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dueDate")) {
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
			DueDateObjectBulkSelectionAction dueDateObjectBulkSelectionAction,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dueDate")) {
				if (jsonParserFieldValue != null) {
					dueDateObjectBulkSelectionAction.setDueDate(
						toDate((String)jsonParserFieldValue));
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

					dueDateObjectBulkSelectionAction.setBulkActionItems(
						bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectionScope")) {
				if (jsonParserFieldValue != null) {
					dueDateObjectBulkSelectionAction.setSelectionScope(
						SelectionScopeSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					dueDateObjectBulkSelectionAction.setType(
						DueDateObjectBulkSelectionAction.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:1591236315