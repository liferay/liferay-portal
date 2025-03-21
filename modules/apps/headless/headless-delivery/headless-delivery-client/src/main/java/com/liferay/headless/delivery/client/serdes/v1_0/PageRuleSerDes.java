/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.PageRule;
import com.liferay.headless.delivery.client.dto.v1_0.PageRuleAction;
import com.liferay.headless.delivery.client.dto.v1_0.PageRuleCondition;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PageRuleSerDes {

	public static PageRule toDTO(String json) {
		PageRuleJSONParser pageRuleJSONParser = new PageRuleJSONParser();

		return pageRuleJSONParser.parseToDTO(json);
	}

	public static PageRule[] toDTOs(String json) {
		PageRuleJSONParser pageRuleJSONParser = new PageRuleJSONParser();

		return pageRuleJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageRule pageRule) {
		if (pageRule == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageRule.getConditionType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"conditionType\": ");

			sb.append("\"");

			sb.append(pageRule.getConditionType());

			sb.append("\"");
		}

		if (pageRule.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(pageRule.getId()));

			sb.append("\"");
		}

		if (pageRule.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(pageRule.getName()));

			sb.append("\"");
		}

		if (pageRule.getPageRuleActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageRuleActions\": ");

			sb.append("[");

			for (int i = 0; i < pageRule.getPageRuleActions().length; i++) {
				sb.append(String.valueOf(pageRule.getPageRuleActions()[i]));

				if ((i + 1) < pageRule.getPageRuleActions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageRule.getPageRuleConditions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageRuleConditions\": ");

			sb.append("[");

			for (int i = 0; i < pageRule.getPageRuleConditions().length; i++) {
				sb.append(String.valueOf(pageRule.getPageRuleConditions()[i]));

				if ((i + 1) < pageRule.getPageRuleConditions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageRuleJSONParser pageRuleJSONParser = new PageRuleJSONParser();

		return pageRuleJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PageRule pageRule) {
		if (pageRule == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageRule.getConditionType() == null) {
			map.put("conditionType", null);
		}
		else {
			map.put(
				"conditionType", String.valueOf(pageRule.getConditionType()));
		}

		if (pageRule.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(pageRule.getId()));
		}

		if (pageRule.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(pageRule.getName()));
		}

		if (pageRule.getPageRuleActions() == null) {
			map.put("pageRuleActions", null);
		}
		else {
			map.put(
				"pageRuleActions",
				String.valueOf(pageRule.getPageRuleActions()));
		}

		if (pageRule.getPageRuleConditions() == null) {
			map.put("pageRuleConditions", null);
		}
		else {
			map.put(
				"pageRuleConditions",
				String.valueOf(pageRule.getPageRuleConditions()));
		}

		return map;
	}

	public static class PageRuleJSONParser extends BaseJSONParser<PageRule> {

		@Override
		protected PageRule createDTO() {
			return new PageRule();
		}

		@Override
		protected PageRule[] createDTOArray(int size) {
			return new PageRule[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "conditionType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageRuleActions")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageRuleConditions")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageRule pageRule, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "conditionType")) {
				if (jsonParserFieldValue != null) {
					pageRule.setConditionType(
						PageRule.ConditionType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					pageRule.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					pageRule.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageRuleActions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PageRuleAction[] pageRuleActionsArray =
						new PageRuleAction[jsonParserFieldValues.length];

					for (int i = 0; i < pageRuleActionsArray.length; i++) {
						pageRuleActionsArray[i] = PageRuleActionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					pageRule.setPageRuleActions(pageRuleActionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageRuleConditions")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PageRuleCondition[] pageRuleConditionsArray =
						new PageRuleCondition[jsonParserFieldValues.length];

					for (int i = 0; i < pageRuleConditionsArray.length; i++) {
						pageRuleConditionsArray[i] =
							PageRuleConditionSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					pageRule.setPageRuleConditions(pageRuleConditionsArray);
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