/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.Dependency;
import com.liferay.headless.admin.fragment.client.dto.v1_0.SelectTypeOptions;
import com.liferay.headless.admin.fragment.client.dto.v1_0.ValidValue;
import com.liferay.headless.admin.fragment.client.json.BaseJSONParser;

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
public class SelectTypeOptionsSerDes {

	public static SelectTypeOptions toDTO(String json) {
		SelectTypeOptionsJSONParser selectTypeOptionsJSONParser =
			new SelectTypeOptionsJSONParser();

		return selectTypeOptionsJSONParser.parseToDTO(json);
	}

	public static SelectTypeOptions[] toDTOs(String json) {
		SelectTypeOptionsJSONParser selectTypeOptionsJSONParser =
			new SelectTypeOptionsJSONParser();

		return selectTypeOptionsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SelectTypeOptions selectTypeOptions) {
		if (selectTypeOptions == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (selectTypeOptions.getDependency() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dependency\": ");

			sb.append(_toJSON(selectTypeOptions.getDependency()));
		}

		if (selectTypeOptions.getValidValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"validValues\": ");

			sb.append("[");

			for (int i = 0; i < selectTypeOptions.getValidValues().length;
				 i++) {

				sb.append(
					String.valueOf(selectTypeOptions.getValidValues()[i]));

				if ((i + 1) < selectTypeOptions.getValidValues().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SelectTypeOptionsJSONParser selectTypeOptionsJSONParser =
			new SelectTypeOptionsJSONParser();

		return selectTypeOptionsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SelectTypeOptions selectTypeOptions) {

		if (selectTypeOptions == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (selectTypeOptions.getDependency() == null) {
			map.put("dependency", null);
		}
		else {
			map.put(
				"dependency",
				String.valueOf(selectTypeOptions.getDependency()));
		}

		if (selectTypeOptions.getValidValues() == null) {
			map.put("validValues", null);
		}
		else {
			map.put(
				"validValues",
				String.valueOf(selectTypeOptions.getValidValues()));
		}

		return map;
	}

	public static class SelectTypeOptionsJSONParser
		extends BaseJSONParser<SelectTypeOptions> {

		@Override
		protected SelectTypeOptions createDTO() {
			return new SelectTypeOptions();
		}

		@Override
		protected SelectTypeOptions[] createDTOArray(int size) {
			return new SelectTypeOptions[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dependency")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "validValues")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SelectTypeOptions selectTypeOptions, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dependency")) {
				if (jsonParserFieldValue != null) {
					selectTypeOptions.setDependency(
						(Map<String, Dependency>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "validValues")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ValidValue[] validValuesArray =
						new ValidValue[jsonParserFieldValues.length];

					for (int i = 0; i < validValuesArray.length; i++) {
						validValuesArray[i] = ValidValueSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					selectTypeOptions.setValidValues(validValuesArray);
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
// LIFERAY-REST-BUILDER-HASH:1228586663