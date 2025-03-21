/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.client.serdes.v1_0;

import com.liferay.headless.form.client.dto.v1_0.Validation;
import com.liferay.headless.form.client.json.BaseJSONParser;

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
public class ValidationSerDes {

	public static Validation toDTO(String json) {
		ValidationJSONParser validationJSONParser = new ValidationJSONParser();

		return validationJSONParser.parseToDTO(json);
	}

	public static Validation[] toDTOs(String json) {
		ValidationJSONParser validationJSONParser = new ValidationJSONParser();

		return validationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Validation validation) {
		if (validation == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (validation.getErrorMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append("\"");

			sb.append(_escape(validation.getErrorMessage()));

			sb.append("\"");
		}

		if (validation.getErrorMessage_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage_i18n\": ");

			sb.append(_toJSON(validation.getErrorMessage_i18n()));
		}

		if (validation.getExpression() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expression\": ");

			sb.append("\"");

			sb.append(_escape(validation.getExpression()));

			sb.append("\"");
		}

		if (validation.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(validation.getId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ValidationJSONParser validationJSONParser = new ValidationJSONParser();

		return validationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Validation validation) {
		if (validation == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (validation.getErrorMessage() == null) {
			map.put("errorMessage", null);
		}
		else {
			map.put(
				"errorMessage", String.valueOf(validation.getErrorMessage()));
		}

		if (validation.getErrorMessage_i18n() == null) {
			map.put("errorMessage_i18n", null);
		}
		else {
			map.put(
				"errorMessage_i18n",
				String.valueOf(validation.getErrorMessage_i18n()));
		}

		if (validation.getExpression() == null) {
			map.put("expression", null);
		}
		else {
			map.put("expression", String.valueOf(validation.getExpression()));
		}

		if (validation.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(validation.getId()));
		}

		return map;
	}

	public static class ValidationJSONParser
		extends BaseJSONParser<Validation> {

		@Override
		protected Validation createDTO() {
			return new Validation();
		}

		@Override
		protected Validation[] createDTOArray(int size) {
			return new Validation[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessage_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "expression")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Validation validation, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				if (jsonParserFieldValue != null) {
					validation.setErrorMessage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessage_i18n")) {
				if (jsonParserFieldValue != null) {
					validation.setErrorMessage_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expression")) {
				if (jsonParserFieldValue != null) {
					validation.setExpression((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					validation.setId(
						Long.valueOf((String)jsonParserFieldValue));
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