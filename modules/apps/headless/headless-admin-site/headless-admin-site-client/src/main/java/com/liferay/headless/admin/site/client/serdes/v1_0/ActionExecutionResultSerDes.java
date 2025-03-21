/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ActionExecutionResult;
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
public class ActionExecutionResultSerDes {

	public static ActionExecutionResult toDTO(String json) {
		ActionExecutionResultJSONParser actionExecutionResultJSONParser =
			new ActionExecutionResultJSONParser();

		return actionExecutionResultJSONParser.parseToDTO(json);
	}

	public static ActionExecutionResult[] toDTOs(String json) {
		ActionExecutionResultJSONParser actionExecutionResultJSONParser =
			new ActionExecutionResultJSONParser();

		return actionExecutionResultJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ActionExecutionResult actionExecutionResult) {
		if (actionExecutionResult == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (actionExecutionResult.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(actionExecutionResult.getType());

			sb.append("\"");
		}

		if (actionExecutionResult.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			if (actionExecutionResult.getValue() instanceof String) {
				sb.append("\"");
				sb.append((String)actionExecutionResult.getValue());
				sb.append("\"");
			}
			else {
				sb.append(actionExecutionResult.getValue());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ActionExecutionResultJSONParser actionExecutionResultJSONParser =
			new ActionExecutionResultJSONParser();

		return actionExecutionResultJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ActionExecutionResult actionExecutionResult) {

		if (actionExecutionResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (actionExecutionResult.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(actionExecutionResult.getType()));
		}

		if (actionExecutionResult.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(actionExecutionResult.getValue()));
		}

		return map;
	}

	public static class ActionExecutionResultJSONParser
		extends BaseJSONParser<ActionExecutionResult> {

		@Override
		protected ActionExecutionResult createDTO() {
			return new ActionExecutionResult();
		}

		@Override
		protected ActionExecutionResult[] createDTOArray(int size) {
			return new ActionExecutionResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ActionExecutionResult actionExecutionResult,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					actionExecutionResult.setType(
						ActionExecutionResult.Type.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					actionExecutionResult.setValue(
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