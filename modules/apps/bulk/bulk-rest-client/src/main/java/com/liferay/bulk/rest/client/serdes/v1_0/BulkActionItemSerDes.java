/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class BulkActionItemSerDes {

	public static BulkActionItem toDTO(String json) {
		BulkActionItemJSONParser bulkActionItemJSONParser =
			new BulkActionItemJSONParser();

		return bulkActionItemJSONParser.parseToDTO(json);
	}

	public static BulkActionItem[] toDTOs(String json) {
		BulkActionItemJSONParser bulkActionItemJSONParser =
			new BulkActionItemJSONParser();

		return bulkActionItemJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BulkActionItem bulkActionItem) {
		if (bulkActionItem == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (bulkActionItem.getAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			sb.append(_toJSON(bulkActionItem.getAttributes()));
		}

		if (bulkActionItem.getClassExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(bulkActionItem.getClassExternalReferenceCode()));

			sb.append("\"");
		}

		if (bulkActionItem.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(bulkActionItem.getClassName()));

			sb.append("\"");
		}

		if (bulkActionItem.getClassPK() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classPK\": ");

			sb.append(bulkActionItem.getClassPK());
		}

		if (bulkActionItem.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(bulkActionItem.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BulkActionItemJSONParser bulkActionItemJSONParser =
			new BulkActionItemJSONParser();

		return bulkActionItemJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(BulkActionItem bulkActionItem) {
		if (bulkActionItem == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (bulkActionItem.getAttributes() == null) {
			map.put("attributes", null);
		}
		else {
			map.put(
				"attributes", String.valueOf(bulkActionItem.getAttributes()));
		}

		if (bulkActionItem.getClassExternalReferenceCode() == null) {
			map.put("classExternalReferenceCode", null);
		}
		else {
			map.put(
				"classExternalReferenceCode",
				String.valueOf(bulkActionItem.getClassExternalReferenceCode()));
		}

		if (bulkActionItem.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put("className", String.valueOf(bulkActionItem.getClassName()));
		}

		if (bulkActionItem.getClassPK() == null) {
			map.put("classPK", null);
		}
		else {
			map.put("classPK", String.valueOf(bulkActionItem.getClassPK()));
		}

		if (bulkActionItem.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(bulkActionItem.getName()));
		}

		return map;
	}

	public static class BulkActionItemJSONParser
		extends BaseJSONParser<BulkActionItem> {

		@Override
		protected BulkActionItem createDTO() {
			return new BulkActionItem();
		}

		@Override
		protected BulkActionItem[] createDTOArray(int size) {
			return new BulkActionItem[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "attributes")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "classExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			BulkActionItem bulkActionItem, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "attributes")) {
				if (jsonParserFieldValue != null) {
					bulkActionItem.setAttributes(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "classExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					bulkActionItem.setClassExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					bulkActionItem.setClassName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				if (jsonParserFieldValue != null) {
					bulkActionItem.setClassPK(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					bulkActionItem.setName((String)jsonParserFieldValue);
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