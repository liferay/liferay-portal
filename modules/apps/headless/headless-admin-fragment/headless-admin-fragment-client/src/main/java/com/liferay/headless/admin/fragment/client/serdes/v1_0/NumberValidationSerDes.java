/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.NumberValidation;
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
public class NumberValidationSerDes {

	public static NumberValidation toDTO(String json) {
		NumberValidationJSONParser numberValidationJSONParser =
			new NumberValidationJSONParser();

		return numberValidationJSONParser.parseToDTO(json);
	}

	public static NumberValidation[] toDTOs(String json) {
		NumberValidationJSONParser numberValidationJSONParser =
			new NumberValidationJSONParser();

		return numberValidationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(NumberValidation numberValidation) {
		if (numberValidation == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (numberValidation.getMax() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"max\": ");

			sb.append(numberValidation.getMax());
		}

		if (numberValidation.getMin() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"min\": ");

			sb.append(numberValidation.getMin());
		}

		if (numberValidation.getErrorMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append("\"");

			sb.append(_escape(numberValidation.getErrorMessage()));

			sb.append("\"");
		}

		if (numberValidation.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(numberValidation.getRequired());
		}

		if (numberValidation.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(numberValidation.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		NumberValidationJSONParser numberValidationJSONParser =
			new NumberValidationJSONParser();

		return numberValidationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(NumberValidation numberValidation) {
		if (numberValidation == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (numberValidation.getMax() == null) {
			map.put("max", null);
		}
		else {
			map.put("max", String.valueOf(numberValidation.getMax()));
		}

		if (numberValidation.getMin() == null) {
			map.put("min", null);
		}
		else {
			map.put("min", String.valueOf(numberValidation.getMin()));
		}

		if (numberValidation.getErrorMessage() == null) {
			map.put("errorMessage", null);
		}
		else {
			map.put(
				"errorMessage",
				String.valueOf(numberValidation.getErrorMessage()));
		}

		if (numberValidation.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put("required", String.valueOf(numberValidation.getRequired()));
		}

		if (numberValidation.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(numberValidation.getType()));
		}

		return map;
	}

	public static class NumberValidationJSONParser
		extends BaseJSONParser<NumberValidation> {

		@Override
		protected NumberValidation createDTO() {
			return new NumberValidation();
		}

		@Override
		protected NumberValidation[] createDTOArray(int size) {
			return new NumberValidation[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "max")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "min")) {
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
			NumberValidation numberValidation, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "max")) {
				if (jsonParserFieldValue != null) {
					numberValidation.setMax(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "min")) {
				if (jsonParserFieldValue != null) {
					numberValidation.setMin(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				if (jsonParserFieldValue != null) {
					numberValidation.setErrorMessage(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					numberValidation.setRequired((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					numberValidation.setType(
						NumberValidation.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:1557520542