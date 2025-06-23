/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.serdes.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.InventoryAnalysisItem;
import com.liferay.analytics.cms.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class InventoryAnalysisItemSerDes {

	public static InventoryAnalysisItem toDTO(String json) {
		InventoryAnalysisItemJSONParser inventoryAnalysisItemJSONParser =
			new InventoryAnalysisItemJSONParser();

		return inventoryAnalysisItemJSONParser.parseToDTO(json);
	}

	public static InventoryAnalysisItem[] toDTOs(String json) {
		InventoryAnalysisItemJSONParser inventoryAnalysisItemJSONParser =
			new InventoryAnalysisItemJSONParser();

		return inventoryAnalysisItemJSONParser.parseToDTOs(json);
	}

	public static String toJSON(InventoryAnalysisItem inventoryAnalysisItem) {
		if (inventoryAnalysisItem == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (inventoryAnalysisItem.getCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"count\": ");

			sb.append(inventoryAnalysisItem.getCount());
		}

		if (inventoryAnalysisItem.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(inventoryAnalysisItem.getKey()));

			sb.append("\"");
		}

		if (inventoryAnalysisItem.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(inventoryAnalysisItem.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		InventoryAnalysisItemJSONParser inventoryAnalysisItemJSONParser =
			new InventoryAnalysisItemJSONParser();

		return inventoryAnalysisItemJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		InventoryAnalysisItem inventoryAnalysisItem) {

		if (inventoryAnalysisItem == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (inventoryAnalysisItem.getCount() == null) {
			map.put("count", null);
		}
		else {
			map.put("count", String.valueOf(inventoryAnalysisItem.getCount()));
		}

		if (inventoryAnalysisItem.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(inventoryAnalysisItem.getKey()));
		}

		if (inventoryAnalysisItem.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(inventoryAnalysisItem.getTitle()));
		}

		return map;
	}

	public static class InventoryAnalysisItemJSONParser
		extends BaseJSONParser<InventoryAnalysisItem> {

		@Override
		protected InventoryAnalysisItem createDTO() {
			return new InventoryAnalysisItem();
		}

		@Override
		protected InventoryAnalysisItem[] createDTOArray(int size) {
			return new InventoryAnalysisItem[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "count")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			InventoryAnalysisItem inventoryAnalysisItem,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "count")) {
				if (jsonParserFieldValue != null) {
					inventoryAnalysisItem.setCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					inventoryAnalysisItem.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					inventoryAnalysisItem.setTitle(
						(String)jsonParserFieldValue);
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