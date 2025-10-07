/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.serdes.v1_0;

import com.liferay.headless.cms.client.dto.v1_0.BulkActionItem;
import com.liferay.headless.cms.client.dto.v1_0.MoveBulkAction;
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
public class MoveBulkActionSerDes {

	public static MoveBulkAction toDTO(String json) {
		MoveBulkActionJSONParser moveBulkActionJSONParser =
			new MoveBulkActionJSONParser();

		return moveBulkActionJSONParser.parseToDTO(json);
	}

	public static MoveBulkAction[] toDTOs(String json) {
		MoveBulkActionJSONParser moveBulkActionJSONParser =
			new MoveBulkActionJSONParser();

		return moveBulkActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MoveBulkAction moveBulkAction) {
		if (moveBulkAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (moveBulkAction.getObjectEntryFolderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectEntryFolderId\": ");

			sb.append(moveBulkAction.getObjectEntryFolderId());
		}

		if (moveBulkAction.getBulkActionItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0; i < moveBulkAction.getBulkActionItems().length;
				 i++) {

				sb.append(
					String.valueOf(moveBulkAction.getBulkActionItems()[i]));

				if ((i + 1) < moveBulkAction.getBulkActionItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (moveBulkAction.getSelectAll() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectAll\": ");

			sb.append(moveBulkAction.getSelectAll());
		}

		if (moveBulkAction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(moveBulkAction.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MoveBulkActionJSONParser moveBulkActionJSONParser =
			new MoveBulkActionJSONParser();

		return moveBulkActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(MoveBulkAction moveBulkAction) {
		if (moveBulkAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (moveBulkAction.getObjectEntryFolderId() == null) {
			map.put("objectEntryFolderId", null);
		}
		else {
			map.put(
				"objectEntryFolderId",
				String.valueOf(moveBulkAction.getObjectEntryFolderId()));
		}

		if (moveBulkAction.getBulkActionItems() == null) {
			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(moveBulkAction.getBulkActionItems()));
		}

		if (moveBulkAction.getSelectAll() == null) {
			map.put("selectAll", null);
		}
		else {
			map.put("selectAll", String.valueOf(moveBulkAction.getSelectAll()));
		}

		if (moveBulkAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(moveBulkAction.getType()));
		}

		return map;
	}

	public static class MoveBulkActionJSONParser
		extends BaseJSONParser<MoveBulkAction> {

		@Override
		protected MoveBulkAction createDTO() {
			return new MoveBulkAction();
		}

		@Override
		protected MoveBulkAction[] createDTOArray(int size) {
			return new MoveBulkAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "objectEntryFolderId")) {
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
			MoveBulkAction moveBulkAction, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "objectEntryFolderId")) {
				if (jsonParserFieldValue != null) {
					moveBulkAction.setObjectEntryFolderId(
						Long.valueOf((String)jsonParserFieldValue));
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

					moveBulkAction.setBulkActionItems(bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectAll")) {
				if (jsonParserFieldValue != null) {
					moveBulkAction.setSelectAll((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					moveBulkAction.setType(
						MoveBulkAction.Type.create(
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