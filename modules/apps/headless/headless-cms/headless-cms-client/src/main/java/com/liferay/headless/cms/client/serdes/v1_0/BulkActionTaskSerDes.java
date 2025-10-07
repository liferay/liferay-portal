/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.serdes.v1_0;

import com.liferay.headless.cms.client.dto.v1_0.BulkActionTask;
import com.liferay.headless.cms.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class BulkActionTaskSerDes {

	public static BulkActionTask toDTO(String json) {
		BulkActionTaskJSONParser bulkActionTaskJSONParser =
			new BulkActionTaskJSONParser();

		return bulkActionTaskJSONParser.parseToDTO(json);
	}

	public static BulkActionTask[] toDTOs(String json) {
		BulkActionTaskJSONParser bulkActionTaskJSONParser =
			new BulkActionTaskJSONParser();

		return bulkActionTaskJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BulkActionTask bulkActionTask) {
		if (bulkActionTask == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (bulkActionTask.getActionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionName\": ");

			sb.append("\"");

			sb.append(_escape(bulkActionTask.getActionName()));

			sb.append("\"");
		}

		if (bulkActionTask.getAuthor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"author\": ");

			sb.append("\"");

			sb.append(_escape(bulkActionTask.getAuthor()));

			sb.append("\"");
		}

		if (bulkActionTask.getCompletionDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completionDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					bulkActionTask.getCompletionDate()));

			sb.append("\"");
		}

		if (bulkActionTask.getCreatedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createdDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					bulkActionTask.getCreatedDate()));

			sb.append("\"");
		}

		if (bulkActionTask.getExecuteStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"executeStatus\": ");

			sb.append("\"");

			sb.append(_escape(bulkActionTask.getExecuteStatus()));

			sb.append("\"");
		}

		if (bulkActionTask.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(bulkActionTask.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (bulkActionTask.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(bulkActionTask.getId());
		}

		if (bulkActionTask.getNumberOfItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfItems\": ");

			sb.append(bulkActionTask.getNumberOfItems());
		}

		if (bulkActionTask.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(bulkActionTask.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BulkActionTaskJSONParser bulkActionTaskJSONParser =
			new BulkActionTaskJSONParser();

		return bulkActionTaskJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(BulkActionTask bulkActionTask) {
		if (bulkActionTask == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (bulkActionTask.getActionName() == null) {
			map.put("actionName", null);
		}
		else {
			map.put(
				"actionName", String.valueOf(bulkActionTask.getActionName()));
		}

		if (bulkActionTask.getAuthor() == null) {
			map.put("author", null);
		}
		else {
			map.put("author", String.valueOf(bulkActionTask.getAuthor()));
		}

		if (bulkActionTask.getCompletionDate() == null) {
			map.put("completionDate", null);
		}
		else {
			map.put(
				"completionDate",
				liferayToJSONDateFormat.format(
					bulkActionTask.getCompletionDate()));
		}

		if (bulkActionTask.getCreatedDate() == null) {
			map.put("createdDate", null);
		}
		else {
			map.put(
				"createdDate",
				liferayToJSONDateFormat.format(
					bulkActionTask.getCreatedDate()));
		}

		if (bulkActionTask.getExecuteStatus() == null) {
			map.put("executeStatus", null);
		}
		else {
			map.put(
				"executeStatus",
				String.valueOf(bulkActionTask.getExecuteStatus()));
		}

		if (bulkActionTask.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(bulkActionTask.getExternalReferenceCode()));
		}

		if (bulkActionTask.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(bulkActionTask.getId()));
		}

		if (bulkActionTask.getNumberOfItems() == null) {
			map.put("numberOfItems", null);
		}
		else {
			map.put(
				"numberOfItems",
				String.valueOf(bulkActionTask.getNumberOfItems()));
		}

		if (bulkActionTask.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(bulkActionTask.getType()));
		}

		return map;
	}

	public static class BulkActionTaskJSONParser
		extends BaseJSONParser<BulkActionTask> {

		@Override
		protected BulkActionTask createDTO() {
			return new BulkActionTask();
		}

		@Override
		protected BulkActionTask[] createDTOArray(int size) {
			return new BulkActionTask[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actionName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "author")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "completionDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createdDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "executeStatus")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfItems")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			BulkActionTask bulkActionTask, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actionName")) {
				if (jsonParserFieldValue != null) {
					bulkActionTask.setActionName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "author")) {
				if (jsonParserFieldValue != null) {
					bulkActionTask.setAuthor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "completionDate")) {
				if (jsonParserFieldValue != null) {
					bulkActionTask.setCompletionDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createdDate")) {
				if (jsonParserFieldValue != null) {
					bulkActionTask.setCreatedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "executeStatus")) {
				if (jsonParserFieldValue != null) {
					bulkActionTask.setExecuteStatus(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					bulkActionTask.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					bulkActionTask.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfItems")) {
				if (jsonParserFieldValue != null) {
					bulkActionTask.setNumberOfItems(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					bulkActionTask.setType((String)jsonParserFieldValue);
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