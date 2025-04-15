/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.Operation;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class OperationSerDes {

	public static Operation toDTO(String json) {
		OperationJSONParser operationJSONParser = new OperationJSONParser();

		return operationJSONParser.parseToDTO(json);
	}

	public static Operation[] toDTOs(String json) {
		OperationJSONParser operationJSONParser = new OperationJSONParser();

		return operationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Operation operation) {
		if (operation == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (operation.getOp() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"op\": ");

			sb.append("\"");

			sb.append(_escape(operation.getOp()));

			sb.append("\"");
		}

		if (operation.getPath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"path\": ");

			sb.append("\"");

			sb.append(_escape(operation.getPath()));

			sb.append("\"");
		}

		if (operation.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			if (operation.getValue() instanceof String) {
				sb.append("\"");
				sb.append((String)operation.getValue());
				sb.append("\"");
			}
			else {
				sb.append(operation.getValue());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OperationJSONParser operationJSONParser = new OperationJSONParser();

		return operationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Operation operation) {
		if (operation == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (operation.getOp() == null) {
			map.put("op", null);
		}
		else {
			map.put("op", String.valueOf(operation.getOp()));
		}

		if (operation.getPath() == null) {
			map.put("path", null);
		}
		else {
			map.put("path", String.valueOf(operation.getPath()));
		}

		if (operation.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(operation.getValue()));
		}

		return map;
	}

	public static class OperationJSONParser extends BaseJSONParser<Operation> {

		@Override
		protected Operation createDTO() {
			return new Operation();
		}

		@Override
		protected Operation[] createDTOArray(int size) {
			return new Operation[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "op")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "path")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Operation operation, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "op")) {
				if (jsonParserFieldValue != null) {
					operation.setOp((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "path")) {
				if (jsonParserFieldValue != null) {
					operation.setPath((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					operation.setValue((Object)jsonParserFieldValue);
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