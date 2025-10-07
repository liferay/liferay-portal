/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.serdes.v1_0;

import com.liferay.headless.cms.client.dto.v1_0.BulkActionItem;
import com.liferay.headless.cms.client.dto.v1_0.StatusBulkAction;
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
public class StatusBulkActionSerDes {

	public static StatusBulkAction toDTO(String json) {
		StatusBulkActionJSONParser statusBulkActionJSONParser =
			new StatusBulkActionJSONParser();

		return statusBulkActionJSONParser.parseToDTO(json);
	}

	public static StatusBulkAction[] toDTOs(String json) {
		StatusBulkActionJSONParser statusBulkActionJSONParser =
			new StatusBulkActionJSONParser();

		return statusBulkActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(StatusBulkAction statusBulkAction) {
		if (statusBulkAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (statusBulkAction.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(statusBulkAction.getStatus());
		}

		if (statusBulkAction.getBulkActionItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0; i < statusBulkAction.getBulkActionItems().length;
				 i++) {

				sb.append(
					String.valueOf(statusBulkAction.getBulkActionItems()[i]));

				if ((i + 1) < statusBulkAction.getBulkActionItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (statusBulkAction.getSelectAll() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectAll\": ");

			sb.append(statusBulkAction.getSelectAll());
		}

		if (statusBulkAction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(statusBulkAction.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		StatusBulkActionJSONParser statusBulkActionJSONParser =
			new StatusBulkActionJSONParser();

		return statusBulkActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(StatusBulkAction statusBulkAction) {
		if (statusBulkAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (statusBulkAction.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(statusBulkAction.getStatus()));
		}

		if (statusBulkAction.getBulkActionItems() == null) {
			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(statusBulkAction.getBulkActionItems()));
		}

		if (statusBulkAction.getSelectAll() == null) {
			map.put("selectAll", null);
		}
		else {
			map.put(
				"selectAll", String.valueOf(statusBulkAction.getSelectAll()));
		}

		if (statusBulkAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(statusBulkAction.getType()));
		}

		return map;
	}

	public static class StatusBulkActionJSONParser
		extends BaseJSONParser<StatusBulkAction> {

		@Override
		protected StatusBulkAction createDTO() {
			return new StatusBulkAction();
		}

		@Override
		protected StatusBulkAction[] createDTOArray(int size) {
			return new StatusBulkAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "status")) {
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
			StatusBulkAction statusBulkAction, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					statusBulkAction.setStatus(
						Integer.valueOf((String)jsonParserFieldValue));
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

					statusBulkAction.setBulkActionItems(bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectAll")) {
				if (jsonParserFieldValue != null) {
					statusBulkAction.setSelectAll(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					statusBulkAction.setType(
						StatusBulkAction.Type.create(
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