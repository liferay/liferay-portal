/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.TextFragmentMappedValue;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class TextFragmentMappedValueSerDes {

	public static TextFragmentMappedValue toDTO(String json) {
		TextFragmentMappedValueJSONParser textFragmentMappedValueJSONParser =
			new TextFragmentMappedValueJSONParser();

		return textFragmentMappedValueJSONParser.parseToDTO(json);
	}

	public static TextFragmentMappedValue[] toDTOs(String json) {
		TextFragmentMappedValueJSONParser textFragmentMappedValueJSONParser =
			new TextFragmentMappedValueJSONParser();

		return textFragmentMappedValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		TextFragmentMappedValue textFragmentMappedValue) {

		if (textFragmentMappedValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (textFragmentMappedValue.getFragmentMappedValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentMappedValue\": ");

			sb.append(
				String.valueOf(
					textFragmentMappedValue.getFragmentMappedValue()));
		}

		if (textFragmentMappedValue.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(textFragmentMappedValue.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TextFragmentMappedValueJSONParser textFragmentMappedValueJSONParser =
			new TextFragmentMappedValueJSONParser();

		return textFragmentMappedValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TextFragmentMappedValue textFragmentMappedValue) {

		if (textFragmentMappedValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (textFragmentMappedValue.getFragmentMappedValue() == null) {
			map.put("fragmentMappedValue", null);
		}
		else {
			map.put(
				"fragmentMappedValue",
				String.valueOf(
					textFragmentMappedValue.getFragmentMappedValue()));
		}

		if (textFragmentMappedValue.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(textFragmentMappedValue.getType()));
		}

		return map;
	}

	public static class TextFragmentMappedValueJSONParser
		extends BaseJSONParser<TextFragmentMappedValue> {

		@Override
		protected TextFragmentMappedValue createDTO() {
			return new TextFragmentMappedValue();
		}

		@Override
		protected TextFragmentMappedValue[] createDTOArray(int size) {
			return new TextFragmentMappedValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "fragmentMappedValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TextFragmentMappedValue textFragmentMappedValue,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "fragmentMappedValue")) {
				if (jsonParserFieldValue != null) {
					textFragmentMappedValue.setFragmentMappedValue(
						FragmentMappedValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					textFragmentMappedValue.setType(
						TextFragmentMappedValue.Type.create(
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:-1220286757