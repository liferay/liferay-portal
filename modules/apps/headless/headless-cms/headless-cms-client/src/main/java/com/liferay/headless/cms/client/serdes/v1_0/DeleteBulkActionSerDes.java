/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.serdes.v1_0;

import com.liferay.headless.cms.client.dto.v1_0.BulkActionItem;
import com.liferay.headless.cms.client.dto.v1_0.DeleteBulkAction;
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
public class DeleteBulkActionSerDes {

	public static DeleteBulkAction toDTO(String json) {
		DeleteBulkActionJSONParser deleteBulkActionJSONParser =
			new DeleteBulkActionJSONParser();

		return deleteBulkActionJSONParser.parseToDTO(json);
	}

	public static DeleteBulkAction[] toDTOs(String json) {
		DeleteBulkActionJSONParser deleteBulkActionJSONParser =
			new DeleteBulkActionJSONParser();

		return deleteBulkActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DeleteBulkAction deleteBulkAction) {
		if (deleteBulkAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (deleteBulkAction.getBulkActionItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0; i < deleteBulkAction.getBulkActionItems().length;
				 i++) {

				sb.append(
					String.valueOf(deleteBulkAction.getBulkActionItems()[i]));

				if ((i + 1) < deleteBulkAction.getBulkActionItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (deleteBulkAction.getSelectAll() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectAll\": ");

			sb.append(deleteBulkAction.getSelectAll());
		}

		if (deleteBulkAction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(deleteBulkAction.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DeleteBulkActionJSONParser deleteBulkActionJSONParser =
			new DeleteBulkActionJSONParser();

		return deleteBulkActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DeleteBulkAction deleteBulkAction) {
		if (deleteBulkAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (deleteBulkAction.getBulkActionItems() == null) {
			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(deleteBulkAction.getBulkActionItems()));
		}

		if (deleteBulkAction.getSelectAll() == null) {
			map.put("selectAll", null);
		}
		else {
			map.put(
				"selectAll", String.valueOf(deleteBulkAction.getSelectAll()));
		}

		if (deleteBulkAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(deleteBulkAction.getType()));
		}

		return map;
	}

	public static class DeleteBulkActionJSONParser
		extends BaseJSONParser<DeleteBulkAction> {

		@Override
		protected DeleteBulkAction createDTO() {
			return new DeleteBulkAction();
		}

		@Override
		protected DeleteBulkAction[] createDTOArray(int size) {
			return new DeleteBulkAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "bulkActionItems")) {
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
			DeleteBulkAction deleteBulkAction, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "bulkActionItems")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					BulkActionItem[] bulkActionItemsArray =
						new BulkActionItem[jsonParserFieldValues.length];

					for (int i = 0; i < bulkActionItemsArray.length; i++) {
						bulkActionItemsArray[i] = BulkActionItemSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					deleteBulkAction.setBulkActionItems(bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectAll")) {
				if (jsonParserFieldValue != null) {
					deleteBulkAction.setSelectAll(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					deleteBulkAction.setType(
						DeleteBulkAction.Type.create(
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