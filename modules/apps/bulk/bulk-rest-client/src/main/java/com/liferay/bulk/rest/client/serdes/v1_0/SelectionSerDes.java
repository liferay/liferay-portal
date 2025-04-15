/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.Selection;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class SelectionSerDes {

	public static Selection toDTO(String json) {
		SelectionJSONParser selectionJSONParser = new SelectionJSONParser();

		return selectionJSONParser.parseToDTO(json);
	}

	public static Selection[] toDTOs(String json) {
		SelectionJSONParser selectionJSONParser = new SelectionJSONParser();

		return selectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Selection selection) {
		if (selection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (selection.getSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append(selection.getSize());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SelectionJSONParser selectionJSONParser = new SelectionJSONParser();

		return selectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Selection selection) {
		if (selection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (selection.getSize() == null) {
			map.put("size", null);
		}
		else {
			map.put("size", String.valueOf(selection.getSize()));
		}

		return map;
	}

	public static class SelectionJSONParser extends BaseJSONParser<Selection> {

		@Override
		protected Selection createDTO() {
			return new Selection();
		}

		@Override
		protected Selection[] createDTOArray(int size) {
			return new Selection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "size")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Selection selection, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "size")) {
				if (jsonParserFieldValue != null) {
					selection.setSize(
						Long.valueOf((String)jsonParserFieldValue));
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