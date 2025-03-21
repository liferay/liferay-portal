/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.ClassFieldReference;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

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
public class ClassFieldReferenceSerDes {

	public static ClassFieldReference toDTO(String json) {
		ClassFieldReferenceJSONParser classFieldReferenceJSONParser =
			new ClassFieldReferenceJSONParser();

		return classFieldReferenceJSONParser.parseToDTO(json);
	}

	public static ClassFieldReference[] toDTOs(String json) {
		ClassFieldReferenceJSONParser classFieldReferenceJSONParser =
			new ClassFieldReferenceJSONParser();

		return classFieldReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ClassFieldReference classFieldReference) {
		if (classFieldReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (classFieldReference.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(classFieldReference.getClassName()));

			sb.append("\"");
		}

		if (classFieldReference.getFieldName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldName\": ");

			sb.append("\"");

			sb.append(_escape(classFieldReference.getFieldName()));

			sb.append("\"");
		}

		if (classFieldReference.getFieldValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldValue\": ");

			sb.append("\"");

			sb.append(_escape(classFieldReference.getFieldValue()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ClassFieldReferenceJSONParser classFieldReferenceJSONParser =
			new ClassFieldReferenceJSONParser();

		return classFieldReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ClassFieldReference classFieldReference) {

		if (classFieldReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (classFieldReference.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(classFieldReference.getClassName()));
		}

		if (classFieldReference.getFieldName() == null) {
			map.put("fieldName", null);
		}
		else {
			map.put(
				"fieldName",
				String.valueOf(classFieldReference.getFieldName()));
		}

		if (classFieldReference.getFieldValue() == null) {
			map.put("fieldValue", null);
		}
		else {
			map.put(
				"fieldValue",
				String.valueOf(classFieldReference.getFieldValue()));
		}

		return map;
	}

	public static class ClassFieldReferenceJSONParser
		extends BaseJSONParser<ClassFieldReference> {

		@Override
		protected ClassFieldReference createDTO() {
			return new ClassFieldReference();
		}

		@Override
		protected ClassFieldReference[] createDTOArray(int size) {
			return new ClassFieldReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fieldName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fieldValue")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ClassFieldReference classFieldReference, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					classFieldReference.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fieldName")) {
				if (jsonParserFieldValue != null) {
					classFieldReference.setFieldName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fieldValue")) {
				if (jsonParserFieldValue != null) {
					classFieldReference.setFieldValue(
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