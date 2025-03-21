/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.HighlightField;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class HighlightFieldSerDes {

	public static HighlightField toDTO(String json) {
		HighlightFieldJSONParser highlightFieldJSONParser =
			new HighlightFieldJSONParser();

		return highlightFieldJSONParser.parseToDTO(json);
	}

	public static HighlightField[] toDTOs(String json) {
		HighlightFieldJSONParser highlightFieldJSONParser =
			new HighlightFieldJSONParser();

		return highlightFieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(HighlightField highlightField) {
		if (highlightField == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (highlightField.getFragment_offset() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragment_offset\": ");

			sb.append(highlightField.getFragment_offset());
		}

		if (highlightField.getFragment_size() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragment_size\": ");

			sb.append(highlightField.getFragment_size());
		}

		if (highlightField.getNumber_of_fragments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"number_of_fragments\": ");

			sb.append(highlightField.getNumber_of_fragments());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		HighlightFieldJSONParser highlightFieldJSONParser =
			new HighlightFieldJSONParser();

		return highlightFieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(HighlightField highlightField) {
		if (highlightField == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (highlightField.getFragment_offset() == null) {
			map.put("fragment_offset", null);
		}
		else {
			map.put(
				"fragment_offset",
				String.valueOf(highlightField.getFragment_offset()));
		}

		if (highlightField.getFragment_size() == null) {
			map.put("fragment_size", null);
		}
		else {
			map.put(
				"fragment_size",
				String.valueOf(highlightField.getFragment_size()));
		}

		if (highlightField.getNumber_of_fragments() == null) {
			map.put("number_of_fragments", null);
		}
		else {
			map.put(
				"number_of_fragments",
				String.valueOf(highlightField.getNumber_of_fragments()));
		}

		return map;
	}

	public static class HighlightFieldJSONParser
		extends BaseJSONParser<HighlightField> {

		@Override
		protected HighlightField createDTO() {
			return new HighlightField();
		}

		@Override
		protected HighlightField[] createDTOArray(int size) {
			return new HighlightField[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "fragment_offset")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragment_size")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "number_of_fragments")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			HighlightField highlightField, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "fragment_offset")) {
				if (jsonParserFieldValue != null) {
					highlightField.setFragment_offset(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragment_size")) {
				if (jsonParserFieldValue != null) {
					highlightField.setFragment_size(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "number_of_fragments")) {

				if (jsonParserFieldValue != null) {
					highlightField.setNumber_of_fragments(
						Integer.valueOf((String)jsonParserFieldValue));
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