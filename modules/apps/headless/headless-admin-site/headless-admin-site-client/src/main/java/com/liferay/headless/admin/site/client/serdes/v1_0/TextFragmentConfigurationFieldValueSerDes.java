/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.TextFragmentConfigurationFieldValue;
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
public class TextFragmentConfigurationFieldValueSerDes {

	public static TextFragmentConfigurationFieldValue toDTO(String json) {
		TextFragmentConfigurationFieldValueJSONParser
			textFragmentConfigurationFieldValueJSONParser =
				new TextFragmentConfigurationFieldValueJSONParser();

		return textFragmentConfigurationFieldValueJSONParser.parseToDTO(json);
	}

	public static TextFragmentConfigurationFieldValue[] toDTOs(String json) {
		TextFragmentConfigurationFieldValueJSONParser
			textFragmentConfigurationFieldValueJSONParser =
				new TextFragmentConfigurationFieldValueJSONParser();

		return textFragmentConfigurationFieldValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		TextFragmentConfigurationFieldValue
			textFragmentConfigurationFieldValue) {

		if (textFragmentConfigurationFieldValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (textFragmentConfigurationFieldValue.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(textFragmentConfigurationFieldValue.getValue()));

			sb.append("\"");
		}

		if (textFragmentConfigurationFieldValue.getValue_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value_i18n\": ");

			sb.append(
				_toJSON(textFragmentConfigurationFieldValue.getValue_i18n()));
		}

		if (textFragmentConfigurationFieldValue.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(textFragmentConfigurationFieldValue.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TextFragmentConfigurationFieldValueJSONParser
			textFragmentConfigurationFieldValueJSONParser =
				new TextFragmentConfigurationFieldValueJSONParser();

		return textFragmentConfigurationFieldValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TextFragmentConfigurationFieldValue
			textFragmentConfigurationFieldValue) {

		if (textFragmentConfigurationFieldValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (textFragmentConfigurationFieldValue.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put(
				"value",
				String.valueOf(textFragmentConfigurationFieldValue.getValue()));
		}

		if (textFragmentConfigurationFieldValue.getValue_i18n() == null) {
			map.put("value_i18n", null);
		}
		else {
			map.put(
				"value_i18n",
				String.valueOf(
					textFragmentConfigurationFieldValue.getValue_i18n()));
		}

		if (textFragmentConfigurationFieldValue.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(textFragmentConfigurationFieldValue.getType()));
		}

		return map;
	}

	public static class TextFragmentConfigurationFieldValueJSONParser
		extends BaseJSONParser<TextFragmentConfigurationFieldValue> {

		@Override
		protected TextFragmentConfigurationFieldValue createDTO() {
			return new TextFragmentConfigurationFieldValue();
		}

		@Override
		protected TextFragmentConfigurationFieldValue[] createDTOArray(
			int size) {

			return new TextFragmentConfigurationFieldValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TextFragmentConfigurationFieldValue
				textFragmentConfigurationFieldValue,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					textFragmentConfigurationFieldValue.setValue(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value_i18n")) {
				if (jsonParserFieldValue != null) {
					textFragmentConfigurationFieldValue.setValue_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					textFragmentConfigurationFieldValue.setType(
						TextFragmentConfigurationFieldValue.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-1037172272