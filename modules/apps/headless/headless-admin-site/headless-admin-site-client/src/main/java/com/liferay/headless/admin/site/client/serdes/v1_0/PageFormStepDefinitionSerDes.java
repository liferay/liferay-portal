/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.PageFormStepDefinition;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class PageFormStepDefinitionSerDes {

	public static PageFormStepDefinition toDTO(String json) {
		PageFormStepDefinitionJSONParser pageFormStepDefinitionJSONParser =
			new PageFormStepDefinitionJSONParser();

		return pageFormStepDefinitionJSONParser.parseToDTO(json);
	}

	public static PageFormStepDefinition[] toDTOs(String json) {
		PageFormStepDefinitionJSONParser pageFormStepDefinitionJSONParser =
			new PageFormStepDefinitionJSONParser();

		return pageFormStepDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageFormStepDefinition pageFormStepDefinition) {
		if (pageFormStepDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageFormStepDefinition.getFormStepConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formStepConfig\": ");

			if (pageFormStepDefinition.getFormStepConfig() instanceof String) {
				sb.append("\"");
				sb.append((String)pageFormStepDefinition.getFormStepConfig());
				sb.append("\"");
			}
			else {
				sb.append(pageFormStepDefinition.getFormStepConfig());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageFormStepDefinitionJSONParser pageFormStepDefinitionJSONParser =
			new PageFormStepDefinitionJSONParser();

		return pageFormStepDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageFormStepDefinition pageFormStepDefinition) {

		if (pageFormStepDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageFormStepDefinition.getFormStepConfig() == null) {
			map.put("formStepConfig", null);
		}
		else {
			map.put(
				"formStepConfig",
				String.valueOf(pageFormStepDefinition.getFormStepConfig()));
		}

		return map;
	}

	public static class PageFormStepDefinitionJSONParser
		extends BaseJSONParser<PageFormStepDefinition> {

		@Override
		protected PageFormStepDefinition createDTO() {
			return new PageFormStepDefinition();
		}

		@Override
		protected PageFormStepDefinition[] createDTOArray(int size) {
			return new PageFormStepDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "formStepConfig")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageFormStepDefinition pageFormStepDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "formStepConfig")) {
				if (jsonParserFieldValue != null) {
					pageFormStepDefinition.setFormStepConfig(
						(Object)jsonParserFieldValue);
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