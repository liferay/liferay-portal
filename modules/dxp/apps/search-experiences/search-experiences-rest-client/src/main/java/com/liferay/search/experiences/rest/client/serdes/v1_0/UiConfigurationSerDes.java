/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.FieldSet;
import com.liferay.search.experiences.rest.client.dto.v1_0.UiConfiguration;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class UiConfigurationSerDes {

	public static UiConfiguration toDTO(String json) {
		UiConfigurationJSONParser uiConfigurationJSONParser =
			new UiConfigurationJSONParser();

		return uiConfigurationJSONParser.parseToDTO(json);
	}

	public static UiConfiguration[] toDTOs(String json) {
		UiConfigurationJSONParser uiConfigurationJSONParser =
			new UiConfigurationJSONParser();

		return uiConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(UiConfiguration uiConfiguration) {
		if (uiConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (uiConfiguration.getFieldSets() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldSets\": ");

			sb.append("[");

			for (int i = 0; i < uiConfiguration.getFieldSets().length; i++) {
				sb.append(String.valueOf(uiConfiguration.getFieldSets()[i]));

				if ((i + 1) < uiConfiguration.getFieldSets().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UiConfigurationJSONParser uiConfigurationJSONParser =
			new UiConfigurationJSONParser();

		return uiConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(UiConfiguration uiConfiguration) {
		if (uiConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (uiConfiguration.getFieldSets() == null) {
			map.put("fieldSets", null);
		}
		else {
			map.put(
				"fieldSets", String.valueOf(uiConfiguration.getFieldSets()));
		}

		return map;
	}

	public static class UiConfigurationJSONParser
		extends BaseJSONParser<UiConfiguration> {

		@Override
		protected UiConfiguration createDTO() {
			return new UiConfiguration();
		}

		@Override
		protected UiConfiguration[] createDTOArray(int size) {
			return new UiConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "fieldSets")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			UiConfiguration uiConfiguration, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "fieldSets")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FieldSet[] fieldSetsArray =
						new FieldSet[jsonParserFieldValues.length];

					for (int i = 0; i < fieldSetsArray.length; i++) {
						fieldSetsArray[i] = FieldSetSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					uiConfiguration.setFieldSets(fieldSetsArray);
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