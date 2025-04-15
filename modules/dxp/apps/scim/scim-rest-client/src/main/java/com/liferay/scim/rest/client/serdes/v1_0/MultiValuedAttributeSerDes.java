/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.MultiValuedAttribute;
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
public class MultiValuedAttributeSerDes {

	public static MultiValuedAttribute toDTO(String json) {
		MultiValuedAttributeJSONParser multiValuedAttributeJSONParser =
			new MultiValuedAttributeJSONParser();

		return multiValuedAttributeJSONParser.parseToDTO(json);
	}

	public static MultiValuedAttribute[] toDTOs(String json) {
		MultiValuedAttributeJSONParser multiValuedAttributeJSONParser =
			new MultiValuedAttributeJSONParser();

		return multiValuedAttributeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MultiValuedAttribute multiValuedAttribute) {
		if (multiValuedAttribute == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (multiValuedAttribute.get$ref() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"$ref\": ");

			sb.append("\"");

			sb.append(_escape(multiValuedAttribute.get$ref()));

			sb.append("\"");
		}

		if (multiValuedAttribute.getDisplay() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"display\": ");

			sb.append("\"");

			sb.append(_escape(multiValuedAttribute.getDisplay()));

			sb.append("\"");
		}

		if (multiValuedAttribute.getPrimary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(multiValuedAttribute.getPrimary());
		}

		if (multiValuedAttribute.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(multiValuedAttribute.getType()));

			sb.append("\"");
		}

		if (multiValuedAttribute.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(multiValuedAttribute.getValue()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MultiValuedAttributeJSONParser multiValuedAttributeJSONParser =
			new MultiValuedAttributeJSONParser();

		return multiValuedAttributeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		MultiValuedAttribute multiValuedAttribute) {

		if (multiValuedAttribute == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (multiValuedAttribute.get$ref() == null) {
			map.put("$ref", null);
		}
		else {
			map.put("$ref", String.valueOf(multiValuedAttribute.get$ref()));
		}

		if (multiValuedAttribute.getDisplay() == null) {
			map.put("display", null);
		}
		else {
			map.put(
				"display", String.valueOf(multiValuedAttribute.getDisplay()));
		}

		if (multiValuedAttribute.getPrimary() == null) {
			map.put("primary", null);
		}
		else {
			map.put(
				"primary", String.valueOf(multiValuedAttribute.getPrimary()));
		}

		if (multiValuedAttribute.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(multiValuedAttribute.getType()));
		}

		if (multiValuedAttribute.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(multiValuedAttribute.getValue()));
		}

		return map;
	}

	public static class MultiValuedAttributeJSONParser
		extends BaseJSONParser<MultiValuedAttribute> {

		@Override
		protected MultiValuedAttribute createDTO() {
			return new MultiValuedAttribute();
		}

		@Override
		protected MultiValuedAttribute[] createDTOArray(int size) {
			return new MultiValuedAttribute[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "$ref")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "display")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
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
			MultiValuedAttribute multiValuedAttribute,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "$ref")) {
				if (jsonParserFieldValue != null) {
					multiValuedAttribute.set$ref((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "display")) {
				if (jsonParserFieldValue != null) {
					multiValuedAttribute.setDisplay(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				if (jsonParserFieldValue != null) {
					multiValuedAttribute.setPrimary(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					multiValuedAttribute.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					multiValuedAttribute.setValue((String)jsonParserFieldValue);
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