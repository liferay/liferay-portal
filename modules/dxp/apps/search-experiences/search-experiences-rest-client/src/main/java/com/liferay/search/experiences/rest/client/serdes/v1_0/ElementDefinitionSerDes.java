/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.ElementDefinition;
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
public class ElementDefinitionSerDes {

	public static ElementDefinition toDTO(String json) {
		ElementDefinitionJSONParser elementDefinitionJSONParser =
			new ElementDefinitionJSONParser();

		return elementDefinitionJSONParser.parseToDTO(json);
	}

	public static ElementDefinition[] toDTOs(String json) {
		ElementDefinitionJSONParser elementDefinitionJSONParser =
			new ElementDefinitionJSONParser();

		return elementDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ElementDefinition elementDefinition) {
		if (elementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (elementDefinition.getCategory() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"category\": ");

			sb.append("\"");

			sb.append(_escape(elementDefinition.getCategory()));

			sb.append("\"");
		}

		if (elementDefinition.getConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configuration\": ");

			sb.append(String.valueOf(elementDefinition.getConfiguration()));
		}

		if (elementDefinition.getIcon() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"icon\": ");

			sb.append("\"");

			sb.append(_escape(elementDefinition.getIcon()));

			sb.append("\"");
		}

		if (elementDefinition.getUiConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uiConfiguration\": ");

			sb.append(String.valueOf(elementDefinition.getUiConfiguration()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ElementDefinitionJSONParser elementDefinitionJSONParser =
			new ElementDefinitionJSONParser();

		return elementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ElementDefinition elementDefinition) {

		if (elementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (elementDefinition.getCategory() == null) {
			map.put("category", null);
		}
		else {
			map.put(
				"category", String.valueOf(elementDefinition.getCategory()));
		}

		if (elementDefinition.getConfiguration() == null) {
			map.put("configuration", null);
		}
		else {
			map.put(
				"configuration",
				String.valueOf(elementDefinition.getConfiguration()));
		}

		if (elementDefinition.getIcon() == null) {
			map.put("icon", null);
		}
		else {
			map.put("icon", String.valueOf(elementDefinition.getIcon()));
		}

		if (elementDefinition.getUiConfiguration() == null) {
			map.put("uiConfiguration", null);
		}
		else {
			map.put(
				"uiConfiguration",
				String.valueOf(elementDefinition.getUiConfiguration()));
		}

		return map;
	}

	public static class ElementDefinitionJSONParser
		extends BaseJSONParser<ElementDefinition> {

		@Override
		protected ElementDefinition createDTO() {
			return new ElementDefinition();
		}

		@Override
		protected ElementDefinition[] createDTOArray(int size) {
			return new ElementDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "category")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "configuration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "icon")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "uiConfiguration")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ElementDefinition elementDefinition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "category")) {
				if (jsonParserFieldValue != null) {
					elementDefinition.setCategory((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "configuration")) {
				if (jsonParserFieldValue != null) {
					elementDefinition.setConfiguration(
						ConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "icon")) {
				if (jsonParserFieldValue != null) {
					elementDefinition.setIcon((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uiConfiguration")) {
				if (jsonParserFieldValue != null) {
					elementDefinition.setUiConfiguration(
						UiConfigurationSerDes.toDTO(
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