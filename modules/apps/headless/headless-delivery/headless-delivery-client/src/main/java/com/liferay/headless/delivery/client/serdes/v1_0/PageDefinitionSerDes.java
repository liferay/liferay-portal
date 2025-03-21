/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.PageDefinition;
import com.liferay.headless.delivery.client.dto.v1_0.PageRule;
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
public class PageDefinitionSerDes {

	public static PageDefinition toDTO(String json) {
		PageDefinitionJSONParser pageDefinitionJSONParser =
			new PageDefinitionJSONParser();

		return pageDefinitionJSONParser.parseToDTO(json);
	}

	public static PageDefinition[] toDTOs(String json) {
		PageDefinitionJSONParser pageDefinitionJSONParser =
			new PageDefinitionJSONParser();

		return pageDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageDefinition pageDefinition) {
		if (pageDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageDefinition.getPageElement() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageElement\": ");

			sb.append(String.valueOf(pageDefinition.getPageElement()));
		}

		if (pageDefinition.getPageRules() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageRules\": ");

			sb.append("[");

			for (int i = 0; i < pageDefinition.getPageRules().length; i++) {
				sb.append(String.valueOf(pageDefinition.getPageRules()[i]));

				if ((i + 1) < pageDefinition.getPageRules().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageDefinition.getSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"settings\": ");

			sb.append(String.valueOf(pageDefinition.getSettings()));
		}

		if (pageDefinition.getVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append(pageDefinition.getVersion());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageDefinitionJSONParser pageDefinitionJSONParser =
			new PageDefinitionJSONParser();

		return pageDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PageDefinition pageDefinition) {
		if (pageDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageDefinition.getPageElement() == null) {
			map.put("pageElement", null);
		}
		else {
			map.put(
				"pageElement", String.valueOf(pageDefinition.getPageElement()));
		}

		if (pageDefinition.getPageRules() == null) {
			map.put("pageRules", null);
		}
		else {
			map.put("pageRules", String.valueOf(pageDefinition.getPageRules()));
		}

		if (pageDefinition.getSettings() == null) {
			map.put("settings", null);
		}
		else {
			map.put("settings", String.valueOf(pageDefinition.getSettings()));
		}

		if (pageDefinition.getVersion() == null) {
			map.put("version", null);
		}
		else {
			map.put("version", String.valueOf(pageDefinition.getVersion()));
		}

		return map;
	}

	public static class PageDefinitionJSONParser
		extends BaseJSONParser<PageDefinition> {

		@Override
		protected PageDefinition createDTO() {
			return new PageDefinition();
		}

		@Override
		protected PageDefinition[] createDTOArray(int size) {
			return new PageDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "pageElement")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageRules")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageDefinition pageDefinition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "pageElement")) {
				if (jsonParserFieldValue != null) {
					pageDefinition.setPageElement(
						PageElementSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageRules")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PageRule[] pageRulesArray =
						new PageRule[jsonParserFieldValues.length];

					for (int i = 0; i < pageRulesArray.length; i++) {
						pageRulesArray[i] = PageRuleSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					pageDefinition.setPageRules(pageRulesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				if (jsonParserFieldValue != null) {
					pageDefinition.setSettings(
						SettingsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				if (jsonParserFieldValue != null) {
					pageDefinition.setVersion(
						Double.valueOf((String)jsonParserFieldValue));
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