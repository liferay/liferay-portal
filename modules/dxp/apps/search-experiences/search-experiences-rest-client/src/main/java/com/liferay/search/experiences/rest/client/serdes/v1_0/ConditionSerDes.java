/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.Condition;
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
public class ConditionSerDes {

	public static Condition toDTO(String json) {
		ConditionJSONParser conditionJSONParser = new ConditionJSONParser();

		return conditionJSONParser.parseToDTO(json);
	}

	public static Condition[] toDTOs(String json) {
		ConditionJSONParser conditionJSONParser = new ConditionJSONParser();

		return conditionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Condition condition) {
		if (condition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (condition.getAllConditions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allConditions\": ");

			sb.append("[");

			for (int i = 0; i < condition.getAllConditions().length; i++) {
				sb.append(String.valueOf(condition.getAllConditions()[i]));

				if ((i + 1) < condition.getAllConditions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (condition.getAnyConditions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"anyConditions\": ");

			sb.append("[");

			for (int i = 0; i < condition.getAnyConditions().length; i++) {
				sb.append(String.valueOf(condition.getAnyConditions()[i]));

				if ((i + 1) < condition.getAnyConditions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (condition.getContains() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contains\": ");

			sb.append(String.valueOf(condition.getContains()));
		}

		if (condition.getEquals() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"equals\": ");

			sb.append(String.valueOf(condition.getEquals()));
		}

		if (condition.getExists() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"exists\": ");

			sb.append(String.valueOf(condition.getExists()));
		}

		if (condition.getIn() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"in\": ");

			sb.append(String.valueOf(condition.getIn()));
		}

		if (condition.getNot() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"not\": ");

			sb.append(String.valueOf(condition.getNot()));
		}

		if (condition.getRange() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"range\": ");

			sb.append(String.valueOf(condition.getRange()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ConditionJSONParser conditionJSONParser = new ConditionJSONParser();

		return conditionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Condition condition) {
		if (condition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (condition.getAllConditions() == null) {
			map.put("allConditions", null);
		}
		else {
			map.put(
				"allConditions", String.valueOf(condition.getAllConditions()));
		}

		if (condition.getAnyConditions() == null) {
			map.put("anyConditions", null);
		}
		else {
			map.put(
				"anyConditions", String.valueOf(condition.getAnyConditions()));
		}

		if (condition.getContains() == null) {
			map.put("contains", null);
		}
		else {
			map.put("contains", String.valueOf(condition.getContains()));
		}

		if (condition.getEquals() == null) {
			map.put("equals", null);
		}
		else {
			map.put("equals", String.valueOf(condition.getEquals()));
		}

		if (condition.getExists() == null) {
			map.put("exists", null);
		}
		else {
			map.put("exists", String.valueOf(condition.getExists()));
		}

		if (condition.getIn() == null) {
			map.put("in", null);
		}
		else {
			map.put("in", String.valueOf(condition.getIn()));
		}

		if (condition.getNot() == null) {
			map.put("not", null);
		}
		else {
			map.put("not", String.valueOf(condition.getNot()));
		}

		if (condition.getRange() == null) {
			map.put("range", null);
		}
		else {
			map.put("range", String.valueOf(condition.getRange()));
		}

		return map;
	}

	public static class ConditionJSONParser extends BaseJSONParser<Condition> {

		@Override
		protected Condition createDTO() {
			return new Condition();
		}

		@Override
		protected Condition[] createDTOArray(int size) {
			return new Condition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "allConditions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "anyConditions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contains")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "equals")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "exists")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "in")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "not")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "range")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Condition condition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "allConditions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Condition[] allConditionsArray =
						new Condition[jsonParserFieldValues.length];

					for (int i = 0; i < allConditionsArray.length; i++) {
						allConditionsArray[i] = ConditionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					condition.setAllConditions(allConditionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "anyConditions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Condition[] anyConditionsArray =
						new Condition[jsonParserFieldValues.length];

					for (int i = 0; i < anyConditionsArray.length; i++) {
						anyConditionsArray[i] = ConditionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					condition.setAnyConditions(anyConditionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contains")) {
				if (jsonParserFieldValue != null) {
					condition.setContains(
						ContainsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "equals")) {
				if (jsonParserFieldValue != null) {
					condition.setEquals(
						EqualsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "exists")) {
				if (jsonParserFieldValue != null) {
					condition.setExists(
						ExistsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "in")) {
				if (jsonParserFieldValue != null) {
					condition.setIn(
						InSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "not")) {
				if (jsonParserFieldValue != null) {
					condition.setNot(
						ConditionSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "range")) {
				if (jsonParserFieldValue != null) {
					condition.setRange(
						RangeSerDes.toDTO((String)jsonParserFieldValue));
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