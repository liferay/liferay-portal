/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectDefinitionValidationError;
import com.liferay.object.admin.rest.client.json.BaseJSONParser;

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
public class ObjectDefinitionValidationErrorSerDes {

	public static ObjectDefinitionValidationError toDTO(String json) {
		ObjectDefinitionValidationErrorJSONParser
			objectDefinitionValidationErrorJSONParser =
				new ObjectDefinitionValidationErrorJSONParser();

		return objectDefinitionValidationErrorJSONParser.parseToDTO(json);
	}

	public static ObjectDefinitionValidationError[] toDTOs(String json) {
		ObjectDefinitionValidationErrorJSONParser
			objectDefinitionValidationErrorJSONParser =
				new ObjectDefinitionValidationErrorJSONParser();

		return objectDefinitionValidationErrorJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ObjectDefinitionValidationError objectDefinitionValidationError) {

		if (objectDefinitionValidationError == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectDefinitionValidationError.getErrorMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append("\"");

			sb.append(
				_escape(objectDefinitionValidationError.getErrorMessage()));

			sb.append("\"");
		}

		if (objectDefinitionValidationError.getExceptionClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"exceptionClassName\": ");

			sb.append("\"");

			sb.append(
				_escape(
					objectDefinitionValidationError.getExceptionClassName()));

			sb.append("\"");
		}

		if (objectDefinitionValidationError.getObjectDefinitionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionName\": ");

			sb.append("\"");

			sb.append(
				_escape(
					objectDefinitionValidationError.getObjectDefinitionName()));

			sb.append("\"");
		}

		if (objectDefinitionValidationError.getObjectFieldName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFieldName\": ");

			sb.append("\"");

			sb.append(
				_escape(objectDefinitionValidationError.getObjectFieldName()));

			sb.append("\"");
		}

		if (objectDefinitionValidationError.getObjectFieldValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectFieldValue\": ");

			sb.append("\"");

			sb.append(
				_escape(objectDefinitionValidationError.getObjectFieldValue()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectDefinitionValidationErrorJSONParser
			objectDefinitionValidationErrorJSONParser =
				new ObjectDefinitionValidationErrorJSONParser();

		return objectDefinitionValidationErrorJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ObjectDefinitionValidationError objectDefinitionValidationError) {

		if (objectDefinitionValidationError == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectDefinitionValidationError.getErrorMessage() == null) {
			map.put("errorMessage", null);
		}
		else {
			map.put(
				"errorMessage",
				String.valueOf(
					objectDefinitionValidationError.getErrorMessage()));
		}

		if (objectDefinitionValidationError.getExceptionClassName() == null) {
			map.put("exceptionClassName", null);
		}
		else {
			map.put(
				"exceptionClassName",
				String.valueOf(
					objectDefinitionValidationError.getExceptionClassName()));
		}

		if (objectDefinitionValidationError.getObjectDefinitionName() == null) {
			map.put("objectDefinitionName", null);
		}
		else {
			map.put(
				"objectDefinitionName",
				String.valueOf(
					objectDefinitionValidationError.getObjectDefinitionName()));
		}

		if (objectDefinitionValidationError.getObjectFieldName() == null) {
			map.put("objectFieldName", null);
		}
		else {
			map.put(
				"objectFieldName",
				String.valueOf(
					objectDefinitionValidationError.getObjectFieldName()));
		}

		if (objectDefinitionValidationError.getObjectFieldValue() == null) {
			map.put("objectFieldValue", null);
		}
		else {
			map.put(
				"objectFieldValue",
				String.valueOf(
					objectDefinitionValidationError.getObjectFieldValue()));
		}

		return map;
	}

	public static class ObjectDefinitionValidationErrorJSONParser
		extends BaseJSONParser<ObjectDefinitionValidationError> {

		@Override
		protected ObjectDefinitionValidationError createDTO() {
			return new ObjectDefinitionValidationError();
		}

		@Override
		protected ObjectDefinitionValidationError[] createDTOArray(int size) {
			return new ObjectDefinitionValidationError[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "exceptionClassName")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionName")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "objectFieldName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "objectFieldValue")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectDefinitionValidationError objectDefinitionValidationError,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				if (jsonParserFieldValue != null) {
					objectDefinitionValidationError.setErrorMessage(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "exceptionClassName")) {

				if (jsonParserFieldValue != null) {
					objectDefinitionValidationError.setExceptionClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionName")) {

				if (jsonParserFieldValue != null) {
					objectDefinitionValidationError.setObjectDefinitionName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectFieldName")) {
				if (jsonParserFieldValue != null) {
					objectDefinitionValidationError.setObjectFieldName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectFieldValue")) {
				if (jsonParserFieldValue != null) {
					objectDefinitionValidationError.setObjectFieldValue(
						(String)jsonParserFieldValue);
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