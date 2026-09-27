/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.TextValidation;
import com.liferay.headless.admin.fragment.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class TextValidationSerDes {

	public static TextValidation toDTO(String json) {
		TextValidationJSONParser textValidationJSONParser =
			new TextValidationJSONParser();

		return textValidationJSONParser.parseToDTO(json);
	}

	public static TextValidation[] toDTOs(String json) {
		TextValidationJSONParser textValidationJSONParser =
			new TextValidationJSONParser();

		return textValidationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TextValidation textValidation) {
		if (textValidation == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (textValidation.getMaxLength() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxLength\": ");

			sb.append(textValidation.getMaxLength());
		}

		if (textValidation.getMinLength() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minLength\": ");

			sb.append(textValidation.getMinLength());
		}

		if (textValidation.getErrorMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append("\"");

			sb.append(_escape(textValidation.getErrorMessage()));

			sb.append("\"");
		}

		if (textValidation.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(textValidation.getRequired());
		}

		if (textValidation.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(textValidation.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TextValidationJSONParser textValidationJSONParser =
			new TextValidationJSONParser();

		return textValidationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TextValidation textValidation) {
		if (textValidation == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (textValidation.getMaxLength() == null) {
			map.put("maxLength", null);
		}
		else {
			map.put("maxLength", String.valueOf(textValidation.getMaxLength()));
		}

		if (textValidation.getMinLength() == null) {
			map.put("minLength", null);
		}
		else {
			map.put("minLength", String.valueOf(textValidation.getMinLength()));
		}

		if (textValidation.getErrorMessage() == null) {
			map.put("errorMessage", null);
		}
		else {
			map.put(
				"errorMessage",
				String.valueOf(textValidation.getErrorMessage()));
		}

		if (textValidation.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put("required", String.valueOf(textValidation.getRequired()));
		}

		if (textValidation.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(textValidation.getType()));
		}

		return map;
	}

	public static class TextValidationJSONParser
		extends BaseJSONParser<TextValidation> {

		@Override
		protected TextValidation createDTO() {
			return new TextValidation();
		}

		@Override
		protected TextValidation[] createDTOArray(int size) {
			return new TextValidation[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "maxLength")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "minLength")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TextValidation textValidation, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "maxLength")) {
				if (jsonParserFieldValue != null) {
					textValidation.setMaxLength(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "minLength")) {
				if (jsonParserFieldValue != null) {
					textValidation.setMinLength(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				if (jsonParserFieldValue != null) {
					textValidation.setErrorMessage(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					textValidation.setRequired((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					textValidation.setType(
						TextValidation.Type.create(
							(String)jsonParserFieldValue));
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:-610418402