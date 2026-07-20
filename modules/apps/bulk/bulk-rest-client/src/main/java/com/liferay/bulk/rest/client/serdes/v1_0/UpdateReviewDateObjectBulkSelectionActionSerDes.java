/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.client.dto.v1_0.UpdateReviewDateObjectBulkSelectionAction;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class UpdateReviewDateObjectBulkSelectionActionSerDes {

	public static UpdateReviewDateObjectBulkSelectionAction toDTO(String json) {
		UpdateReviewDateObjectBulkSelectionActionJSONParser
			updateReviewDateObjectBulkSelectionActionJSONParser =
				new UpdateReviewDateObjectBulkSelectionActionJSONParser();

		return updateReviewDateObjectBulkSelectionActionJSONParser.parseToDTO(
			json);
	}

	public static UpdateReviewDateObjectBulkSelectionAction[] toDTOs(
		String json) {

		UpdateReviewDateObjectBulkSelectionActionJSONParser
			updateReviewDateObjectBulkSelectionActionJSONParser =
				new UpdateReviewDateObjectBulkSelectionActionJSONParser();

		return updateReviewDateObjectBulkSelectionActionJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		UpdateReviewDateObjectBulkSelectionAction
			updateReviewDateObjectBulkSelectionAction) {

		if (updateReviewDateObjectBulkSelectionAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (updateReviewDateObjectBulkSelectionAction.getReviewDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reviewDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					updateReviewDateObjectBulkSelectionAction.getReviewDate()));

			sb.append("\"");
		}

		if (updateReviewDateObjectBulkSelectionAction.getBulkActionItems() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulkActionItems\": ");

			sb.append("[");

			for (int i = 0;
				 i < updateReviewDateObjectBulkSelectionAction.
					 getBulkActionItems().length;
				 i++) {

				sb.append(
					String.valueOf(
						updateReviewDateObjectBulkSelectionAction.
							getBulkActionItems()[i]));

				if ((i + 1) < updateReviewDateObjectBulkSelectionAction.
						getBulkActionItems().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (updateReviewDateObjectBulkSelectionAction.getSelectionScope() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectionScope\": ");

			sb.append(
				String.valueOf(
					updateReviewDateObjectBulkSelectionAction.
						getSelectionScope()));
		}

		if (updateReviewDateObjectBulkSelectionAction.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(updateReviewDateObjectBulkSelectionAction.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UpdateReviewDateObjectBulkSelectionActionJSONParser
			updateReviewDateObjectBulkSelectionActionJSONParser =
				new UpdateReviewDateObjectBulkSelectionActionJSONParser();

		return updateReviewDateObjectBulkSelectionActionJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		UpdateReviewDateObjectBulkSelectionAction
			updateReviewDateObjectBulkSelectionAction) {

		if (updateReviewDateObjectBulkSelectionAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (updateReviewDateObjectBulkSelectionAction.getReviewDate() == null) {
			map.put("reviewDate", null);
		}
		else {
			map.put(
				"reviewDate",
				liferayToJSONDateFormat.format(
					updateReviewDateObjectBulkSelectionAction.getReviewDate()));
		}

		if (updateReviewDateObjectBulkSelectionAction.getBulkActionItems() ==
				null) {

			map.put("bulkActionItems", null);
		}
		else {
			map.put(
				"bulkActionItems",
				String.valueOf(
					updateReviewDateObjectBulkSelectionAction.
						getBulkActionItems()));
		}

		if (updateReviewDateObjectBulkSelectionAction.getSelectionScope() ==
				null) {

			map.put("selectionScope", null);
		}
		else {
			map.put(
				"selectionScope",
				String.valueOf(
					updateReviewDateObjectBulkSelectionAction.
						getSelectionScope()));
		}

		if (updateReviewDateObjectBulkSelectionAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					updateReviewDateObjectBulkSelectionAction.getType()));
		}

		return map;
	}

	public static class UpdateReviewDateObjectBulkSelectionActionJSONParser
		extends BaseJSONParser<UpdateReviewDateObjectBulkSelectionAction> {

		@Override
		protected UpdateReviewDateObjectBulkSelectionAction createDTO() {
			return new UpdateReviewDateObjectBulkSelectionAction();
		}

		@Override
		protected UpdateReviewDateObjectBulkSelectionAction[] createDTOArray(
			int size) {

			return new UpdateReviewDateObjectBulkSelectionAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "reviewDate")) {
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
			UpdateReviewDateObjectBulkSelectionAction
				updateReviewDateObjectBulkSelectionAction,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "reviewDate")) {
				if (jsonParserFieldValue != null) {
					updateReviewDateObjectBulkSelectionAction.setReviewDate(
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

					updateReviewDateObjectBulkSelectionAction.
						setBulkActionItems(bulkActionItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectionScope")) {
				if (jsonParserFieldValue != null) {
					updateReviewDateObjectBulkSelectionAction.setSelectionScope(
						SelectionScopeSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					updateReviewDateObjectBulkSelectionAction.setType(
						UpdateReviewDateObjectBulkSelectionAction.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-619235484