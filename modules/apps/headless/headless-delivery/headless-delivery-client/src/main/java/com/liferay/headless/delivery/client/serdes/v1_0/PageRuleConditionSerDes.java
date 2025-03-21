/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.PageRuleCondition;
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
public class PageRuleConditionSerDes {

	public static PageRuleCondition toDTO(String json) {
		PageRuleConditionJSONParser pageRuleConditionJSONParser =
			new PageRuleConditionJSONParser();

		return pageRuleConditionJSONParser.parseToDTO(json);
	}

	public static PageRuleCondition[] toDTOs(String json) {
		PageRuleConditionJSONParser pageRuleConditionJSONParser =
			new PageRuleConditionJSONParser();

		return pageRuleConditionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageRuleCondition pageRuleCondition) {
		if (pageRuleCondition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageRuleCondition.getField() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"field\": ");

			sb.append("\"");

			sb.append(_escape(pageRuleCondition.getField()));

			sb.append("\"");
		}

		if (pageRuleCondition.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(pageRuleCondition.getId()));

			sb.append("\"");
		}

		if (pageRuleCondition.getOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append(String.valueOf(pageRuleCondition.getOptions()));
		}

		if (pageRuleCondition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(pageRuleCondition.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageRuleConditionJSONParser pageRuleConditionJSONParser =
			new PageRuleConditionJSONParser();

		return pageRuleConditionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageRuleCondition pageRuleCondition) {

		if (pageRuleCondition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageRuleCondition.getField() == null) {
			map.put("field", null);
		}
		else {
			map.put("field", String.valueOf(pageRuleCondition.getField()));
		}

		if (pageRuleCondition.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(pageRuleCondition.getId()));
		}

		if (pageRuleCondition.getOptions() == null) {
			map.put("options", null);
		}
		else {
			map.put("options", String.valueOf(pageRuleCondition.getOptions()));
		}

		if (pageRuleCondition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(pageRuleCondition.getType()));
		}

		return map;
	}

	public static class PageRuleConditionJSONParser
		extends BaseJSONParser<PageRuleCondition> {

		@Override
		protected PageRuleCondition createDTO() {
			return new PageRuleCondition();
		}

		@Override
		protected PageRuleCondition[] createDTOArray(int size) {
			return new PageRuleCondition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "field")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageRuleCondition pageRuleCondition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "field")) {
				if (jsonParserFieldValue != null) {
					pageRuleCondition.setField((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					pageRuleCondition.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				if (jsonParserFieldValue != null) {
					pageRuleCondition.setOptions(
						OptionsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					pageRuleCondition.setType((String)jsonParserFieldValue);
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