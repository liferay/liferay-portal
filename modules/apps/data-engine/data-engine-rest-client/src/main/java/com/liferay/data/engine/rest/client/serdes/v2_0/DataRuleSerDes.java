/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.client.serdes.v2_0;

import com.liferay.data.engine.rest.client.dto.v2_0.DataRule;
import com.liferay.data.engine.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public class DataRuleSerDes {

	public static DataRule toDTO(String json) {
		DataRuleJSONParser dataRuleJSONParser = new DataRuleJSONParser();

		return dataRuleJSONParser.parseToDTO(json);
	}

	public static DataRule[] toDTOs(String json) {
		DataRuleJSONParser dataRuleJSONParser = new DataRuleJSONParser();

		return dataRuleJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataRule dataRule) {
		if (dataRule == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataRule.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append("[");

			for (int i = 0; i < dataRule.getActions().length; i++) {
				sb.append(dataRule.getActions()[i]);

				if ((i + 1) < dataRule.getActions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataRule.getConditions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"conditions\": ");

			sb.append("[");

			for (int i = 0; i < dataRule.getConditions().length; i++) {
				sb.append(dataRule.getConditions()[i]);

				if ((i + 1) < dataRule.getConditions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataRule.getLogicalOperator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logicalOperator\": ");

			sb.append("\"");

			sb.append(_escape(dataRule.getLogicalOperator()));

			sb.append("\"");
		}

		if (dataRule.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(dataRule.getName()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataRuleJSONParser dataRuleJSONParser = new DataRuleJSONParser();

		return dataRuleJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DataRule dataRule) {
		if (dataRule == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataRule.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(dataRule.getActions()));
		}

		if (dataRule.getConditions() == null) {
			map.put("conditions", null);
		}
		else {
			map.put("conditions", String.valueOf(dataRule.getConditions()));
		}

		if (dataRule.getLogicalOperator() == null) {
			map.put("logicalOperator", null);
		}
		else {
			map.put(
				"logicalOperator",
				String.valueOf(dataRule.getLogicalOperator()));
		}

		if (dataRule.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(dataRule.getName()));
		}

		return map;
	}

	public static class DataRuleJSONParser extends BaseJSONParser<DataRule> {

		@Override
		protected DataRule createDTO() {
			return new DataRule();
		}

		@Override
		protected DataRule[] createDTOArray(int size) {
			return new DataRule[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "conditions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "logicalOperator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			DataRule dataRule, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					dataRule.setActions((Map[])jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "conditions")) {
				if (jsonParserFieldValue != null) {
					dataRule.setConditions((Map[])jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "logicalOperator")) {
				if (jsonParserFieldValue != null) {
					dataRule.setLogicalOperator((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					dataRule.setName((Map<String, Object>)jsonParserFieldValue);
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