/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.FragmentMappedValue;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FragmentMappedValueSerDes {

	public static FragmentMappedValue toDTO(String json) {
		FragmentMappedValueJSONParser fragmentMappedValueJSONParser =
			new FragmentMappedValueJSONParser();

		return fragmentMappedValueJSONParser.parseToDTO(json);
	}

	public static FragmentMappedValue[] toDTOs(String json) {
		FragmentMappedValueJSONParser fragmentMappedValueJSONParser =
			new FragmentMappedValueJSONParser();

		return fragmentMappedValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FragmentMappedValue fragmentMappedValue) {
		if (fragmentMappedValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentMappedValue.getDefaultFragmentInlineValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultFragmentInlineValue\": ");

			sb.append(
				String.valueOf(
					fragmentMappedValue.getDefaultFragmentInlineValue()));
		}

		if (fragmentMappedValue.getDefaultValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultValue\": ");

			sb.append(String.valueOf(fragmentMappedValue.getDefaultValue()));
		}

		if (fragmentMappedValue.getMapping() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mapping\": ");

			sb.append(String.valueOf(fragmentMappedValue.getMapping()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentMappedValueJSONParser fragmentMappedValueJSONParser =
			new FragmentMappedValueJSONParser();

		return fragmentMappedValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentMappedValue fragmentMappedValue) {

		if (fragmentMappedValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentMappedValue.getDefaultFragmentInlineValue() == null) {
			map.put("defaultFragmentInlineValue", null);
		}
		else {
			map.put(
				"defaultFragmentInlineValue",
				String.valueOf(
					fragmentMappedValue.getDefaultFragmentInlineValue()));
		}

		if (fragmentMappedValue.getDefaultValue() == null) {
			map.put("defaultValue", null);
		}
		else {
			map.put(
				"defaultValue",
				String.valueOf(fragmentMappedValue.getDefaultValue()));
		}

		if (fragmentMappedValue.getMapping() == null) {
			map.put("mapping", null);
		}
		else {
			map.put(
				"mapping", String.valueOf(fragmentMappedValue.getMapping()));
		}

		return map;
	}

	public static class FragmentMappedValueJSONParser
		extends BaseJSONParser<FragmentMappedValue> {

		@Override
		protected FragmentMappedValue createDTO() {
			return new FragmentMappedValue();
		}

		@Override
		protected FragmentMappedValue[] createDTOArray(int size) {
			return new FragmentMappedValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "defaultFragmentInlineValue")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "mapping")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentMappedValue fragmentMappedValue, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "defaultFragmentInlineValue")) {

				if (jsonParserFieldValue != null) {
					fragmentMappedValue.setDefaultFragmentInlineValue(
						FragmentInlineValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				if (jsonParserFieldValue != null) {
					fragmentMappedValue.setDefaultValue(
						DefaultValueSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "mapping")) {
				if (jsonParserFieldValue != null) {
					fragmentMappedValue.setMapping(
						MappingSerDes.toDTO((String)jsonParserFieldValue));
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