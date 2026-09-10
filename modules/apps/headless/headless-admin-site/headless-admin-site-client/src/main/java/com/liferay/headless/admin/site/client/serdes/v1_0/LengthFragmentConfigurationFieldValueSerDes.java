/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.LengthFragmentConfigurationFieldValue;
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
public class LengthFragmentConfigurationFieldValueSerDes {

	public static LengthFragmentConfigurationFieldValue toDTO(String json) {
		LengthFragmentConfigurationFieldValueJSONParser
			lengthFragmentConfigurationFieldValueJSONParser =
				new LengthFragmentConfigurationFieldValueJSONParser();

		return lengthFragmentConfigurationFieldValueJSONParser.parseToDTO(json);
	}

	public static LengthFragmentConfigurationFieldValue[] toDTOs(String json) {
		LengthFragmentConfigurationFieldValueJSONParser
			lengthFragmentConfigurationFieldValueJSONParser =
				new LengthFragmentConfigurationFieldValueJSONParser();

		return lengthFragmentConfigurationFieldValueJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		LengthFragmentConfigurationFieldValue
			lengthFragmentConfigurationFieldValue) {

		if (lengthFragmentConfigurationFieldValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (lengthFragmentConfigurationFieldValue.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(
				_escape(lengthFragmentConfigurationFieldValue.getValue()));

			sb.append("\"");
		}

		if (lengthFragmentConfigurationFieldValue.getValue_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value_i18n\": ");

			sb.append(
				_toJSON(lengthFragmentConfigurationFieldValue.getValue_i18n()));
		}

		if (lengthFragmentConfigurationFieldValue.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(lengthFragmentConfigurationFieldValue.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		LengthFragmentConfigurationFieldValueJSONParser
			lengthFragmentConfigurationFieldValueJSONParser =
				new LengthFragmentConfigurationFieldValueJSONParser();

		return lengthFragmentConfigurationFieldValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		LengthFragmentConfigurationFieldValue
			lengthFragmentConfigurationFieldValue) {

		if (lengthFragmentConfigurationFieldValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (lengthFragmentConfigurationFieldValue.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put(
				"value",
				String.valueOf(
					lengthFragmentConfigurationFieldValue.getValue()));
		}

		if (lengthFragmentConfigurationFieldValue.getValue_i18n() == null) {
			map.put("value_i18n", null);
		}
		else {
			map.put(
				"value_i18n",
				String.valueOf(
					lengthFragmentConfigurationFieldValue.getValue_i18n()));
		}

		if (lengthFragmentConfigurationFieldValue.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					lengthFragmentConfigurationFieldValue.getType()));
		}

		return map;
	}

	public static class LengthFragmentConfigurationFieldValueJSONParser
		extends BaseJSONParser<LengthFragmentConfigurationFieldValue> {

		@Override
		protected LengthFragmentConfigurationFieldValue createDTO() {
			return new LengthFragmentConfigurationFieldValue();
		}

		@Override
		protected LengthFragmentConfigurationFieldValue[] createDTOArray(
			int size) {

			return new LengthFragmentConfigurationFieldValue[size];
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
			LengthFragmentConfigurationFieldValue
				lengthFragmentConfigurationFieldValue,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					lengthFragmentConfigurationFieldValue.setValue(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value_i18n")) {
				if (jsonParserFieldValue != null) {
					lengthFragmentConfigurationFieldValue.setValue_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					lengthFragmentConfigurationFieldValue.setType(
						LengthFragmentConfigurationFieldValue.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:656205864