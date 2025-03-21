/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.Clause;
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
public class ClauseSerDes {

	public static Clause toDTO(String json) {
		ClauseJSONParser clauseJSONParser = new ClauseJSONParser();

		return clauseJSONParser.parseToDTO(json);
	}

	public static Clause[] toDTOs(String json) {
		ClauseJSONParser clauseJSONParser = new ClauseJSONParser();

		return clauseJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Clause clause) {
		if (clause == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (clause.getAdditive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additive\": ");

			sb.append(clause.getAdditive());
		}

		if (clause.getBoost() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"boost\": ");

			sb.append(clause.getBoost());
		}

		if (clause.getContext() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"context\": ");

			sb.append("\"");

			sb.append(_escape(clause.getContext()));

			sb.append("\"");
		}

		if (clause.getDisabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"disabled\": ");

			sb.append(clause.getDisabled());
		}

		if (clause.getField() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"field\": ");

			sb.append("\"");

			sb.append(_escape(clause.getField()));

			sb.append("\"");
		}

		if (clause.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(clause.getName()));

			sb.append("\"");
		}

		if (clause.getOccur() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"occur\": ");

			sb.append("\"");

			sb.append(_escape(clause.getOccur()));

			sb.append("\"");
		}

		if (clause.getParent() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parent\": ");

			sb.append("\"");

			sb.append(_escape(clause.getParent()));

			sb.append("\"");
		}

		if (clause.getQuery() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"query\": ");

			if (clause.getQuery() instanceof String) {
				sb.append("\"");
				sb.append((String)clause.getQuery());
				sb.append("\"");
			}
			else {
				sb.append(clause.getQuery());
			}
		}

		if (clause.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(clause.getType()));

			sb.append("\"");
		}

		if (clause.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(clause.getValue()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ClauseJSONParser clauseJSONParser = new ClauseJSONParser();

		return clauseJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Clause clause) {
		if (clause == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (clause.getAdditive() == null) {
			map.put("additive", null);
		}
		else {
			map.put("additive", String.valueOf(clause.getAdditive()));
		}

		if (clause.getBoost() == null) {
			map.put("boost", null);
		}
		else {
			map.put("boost", String.valueOf(clause.getBoost()));
		}

		if (clause.getContext() == null) {
			map.put("context", null);
		}
		else {
			map.put("context", String.valueOf(clause.getContext()));
		}

		if (clause.getDisabled() == null) {
			map.put("disabled", null);
		}
		else {
			map.put("disabled", String.valueOf(clause.getDisabled()));
		}

		if (clause.getField() == null) {
			map.put("field", null);
		}
		else {
			map.put("field", String.valueOf(clause.getField()));
		}

		if (clause.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(clause.getName()));
		}

		if (clause.getOccur() == null) {
			map.put("occur", null);
		}
		else {
			map.put("occur", String.valueOf(clause.getOccur()));
		}

		if (clause.getParent() == null) {
			map.put("parent", null);
		}
		else {
			map.put("parent", String.valueOf(clause.getParent()));
		}

		if (clause.getQuery() == null) {
			map.put("query", null);
		}
		else {
			map.put("query", String.valueOf(clause.getQuery()));
		}

		if (clause.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(clause.getType()));
		}

		if (clause.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(clause.getValue()));
		}

		return map;
	}

	public static class ClauseJSONParser extends BaseJSONParser<Clause> {

		@Override
		protected Clause createDTO() {
			return new Clause();
		}

		@Override
		protected Clause[] createDTOArray(int size) {
			return new Clause[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "additive")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "boost")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "context")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "disabled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "field")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "occur")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parent")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "query")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Clause clause, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "additive")) {
				if (jsonParserFieldValue != null) {
					clause.setAdditive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "boost")) {
				if (jsonParserFieldValue != null) {
					clause.setBoost(
						Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "context")) {
				if (jsonParserFieldValue != null) {
					clause.setContext((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "disabled")) {
				if (jsonParserFieldValue != null) {
					clause.setDisabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "field")) {
				if (jsonParserFieldValue != null) {
					clause.setField((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					clause.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "occur")) {
				if (jsonParserFieldValue != null) {
					clause.setOccur((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parent")) {
				if (jsonParserFieldValue != null) {
					clause.setParent((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "query")) {
				if (jsonParserFieldValue != null) {
					clause.setQuery((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					clause.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					clause.setValue((String)jsonParserFieldValue);
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