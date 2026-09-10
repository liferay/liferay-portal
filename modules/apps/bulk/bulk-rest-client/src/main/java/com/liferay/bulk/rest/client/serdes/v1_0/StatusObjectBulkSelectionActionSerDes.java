/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.client.dto.v1_0.StatusObjectBulkSelectionAction;
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
public class StatusObjectBulkSelectionActionSerDes {

	public static StatusObjectBulkSelectionAction toDTO(String json) {
		StatusObjectBulkSelectionActionJSONParser
			statusObjectBulkSelectionActionJSONParser =
				new StatusObjectBulkSelectionActionJSONParser();

		return statusObjectBulkSelectionActionJSONParser.parseToDTO(json);
	}

	public static StatusObjectBulkSelectionAction[] toDTOs(String json) {
		StatusObjectBulkSelectionActionJSONParser
			statusObjectBulkSelectionActionJSONParser =
				new StatusObjectBulkSelectionActionJSONParser();

		return statusObjectBulkSelectionActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		StatusObjectBulkSelectionAction statusObjectBulkSelectionAction) {

		if (statusObjectBulkSelectionAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (statusObjectBulkSelectionAction.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(statusObjectBulkSelectionAction.getStatus()));

			sb.append("\"");
		}

		if (statusObjectBulkSelectionAction.getBulkActionItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 statusObjectBulkSelectionAction.
						 getBulkActionItems().length;
				 i++) {

				sb.append(
					String.valueOf(
						statusObjectBulkSelectionAction.getBulkActionItems()
							[i]));

				if ((i + 1) < statusObjectBulkSelectionAction.
						getBulkActionItems().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (statusObjectBulkSelectionAction.getSelectionScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectionScope\": ");

			sb.append(
				String.valueOf(
					statusObjectBulkSelectionAction.getSelectionScope()));
		}

		if (statusObjectBulkSelectionAction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(statusObjectBulkSelectionAction.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		StatusObjectBulkSelectionActionJSONParser
			statusObjectBulkSelectionActionJSONParser =
				new StatusObjectBulkSelectionActionJSONParser();

		return statusObjectBulkSelectionActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		StatusObjectBulkSelectionAction statusObjectBulkSelectionAction) {

		if (statusObjectBulkSelectionAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (statusObjectBulkSelectionAction.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put(
				"status",
				String.valueOf(statusObjectBulkSelectionAction.getStatus()));
		}

		if (statusObjectBulkSelectionAction.getBulkActionItems() == null) {
			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(
					statusObjectBulkSelectionAction.getBulkActionItems()));
		}

		if (statusObjectBulkSelectionAction.getSelectionScope() == null) {
			map.put("selectionScope", null);
		}
		else {
			map.put(
				"selectionScope",
				String.valueOf(
					statusObjectBulkSelectionAction.getSelectionScope()));
		}

		if (statusObjectBulkSelectionAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(statusObjectBulkSelectionAction.getType()));
		}

		return map;
	}

	public static class StatusObjectBulkSelectionActionJSONParser
		extends BaseJSONParser<StatusObjectBulkSelectionAction> {

		@Override
		protected StatusObjectBulkSelectionAction createDTO() {
			return new StatusObjectBulkSelectionAction();
		}

		@Override
		protected StatusObjectBulkSelectionAction[] createDTOArray(int size) {
			return new StatusObjectBulkSelectionAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "status")) {
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
			StatusObjectBulkSelectionAction statusObjectBulkSelectionAction,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					statusObjectBulkSelectionAction.setStatus(
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

					statusObjectBulkSelectionAction.setBulkActionItems(
						bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectionScope")) {
				if (jsonParserFieldValue != null) {
					statusObjectBulkSelectionAction.setSelectionScope(
						SelectionScopeSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					statusObjectBulkSelectionAction.setType(
						StatusObjectBulkSelectionAction.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:1545583517