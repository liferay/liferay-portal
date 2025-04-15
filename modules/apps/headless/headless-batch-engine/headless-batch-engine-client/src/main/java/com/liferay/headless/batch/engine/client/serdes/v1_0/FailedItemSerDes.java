/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.client.serdes.v1_0;

import com.liferay.headless.batch.engine.client.dto.v1_0.FailedItem;
import com.liferay.headless.batch.engine.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Ivica Cardic
 * @generated
 */
@Generated("")
public class FailedItemSerDes {

	public static FailedItem toDTO(String json) {
		FailedItemJSONParser failedItemJSONParser = new FailedItemJSONParser();

		return failedItemJSONParser.parseToDTO(json);
	}

	public static FailedItem[] toDTOs(String json) {
		FailedItemJSONParser failedItemJSONParser = new FailedItemJSONParser();

		return failedItemJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FailedItem failedItem) {
		if (failedItem == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (failedItem.getItem() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"item\": ");

			sb.append("\"");

			sb.append(_escape(failedItem.getItem()));

			sb.append("\"");
		}

		if (failedItem.getItemIndex() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemIndex\": ");

			sb.append(failedItem.getItemIndex());
		}

		if (failedItem.getMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"message\": ");

			sb.append("\"");

			sb.append(_escape(failedItem.getMessage()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FailedItemJSONParser failedItemJSONParser = new FailedItemJSONParser();

		return failedItemJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FailedItem failedItem) {
		if (failedItem == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (failedItem.getItem() == null) {
			map.put("item", null);
		}
		else {
			map.put("item", String.valueOf(failedItem.getItem()));
		}

		if (failedItem.getItemIndex() == null) {
			map.put("itemIndex", null);
		}
		else {
			map.put("itemIndex", String.valueOf(failedItem.getItemIndex()));
		}

		if (failedItem.getMessage() == null) {
			map.put("message", null);
		}
		else {
			map.put("message", String.valueOf(failedItem.getMessage()));
		}

		return map;
	}

	public static class FailedItemJSONParser
		extends BaseJSONParser<FailedItem> {

		@Override
		protected FailedItem createDTO() {
			return new FailedItem();
		}

		@Override
		protected FailedItem[] createDTOArray(int size) {
			return new FailedItem[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "item")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "itemIndex")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "message")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FailedItem failedItem, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "item")) {
				if (jsonParserFieldValue != null) {
					failedItem.setItem((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "itemIndex")) {
				if (jsonParserFieldValue != null) {
					failedItem.setItemIndex(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "message")) {
				if (jsonParserFieldValue != null) {
					failedItem.setMessage((String)jsonParserFieldValue);
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