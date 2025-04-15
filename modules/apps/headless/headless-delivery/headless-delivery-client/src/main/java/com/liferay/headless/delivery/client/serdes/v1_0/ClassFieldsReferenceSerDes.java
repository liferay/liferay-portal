/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.ClassFieldsReference;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

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
public class ClassFieldsReferenceSerDes {

	public static ClassFieldsReference toDTO(String json) {
		ClassFieldsReferenceJSONParser classFieldsReferenceJSONParser =
			new ClassFieldsReferenceJSONParser();

		return classFieldsReferenceJSONParser.parseToDTO(json);
	}

	public static ClassFieldsReference[] toDTOs(String json) {
		ClassFieldsReferenceJSONParser classFieldsReferenceJSONParser =
			new ClassFieldsReferenceJSONParser();

		return classFieldsReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ClassFieldsReference classFieldsReference) {
		if (classFieldsReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (classFieldsReference.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(classFieldsReference.getClassName()));

			sb.append("\"");
		}

		if (classFieldsReference.getFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fields\": ");

			sb.append("[");

			for (int i = 0; i < classFieldsReference.getFields().length; i++) {
				sb.append(String.valueOf(classFieldsReference.getFields()[i]));

				if ((i + 1) < classFieldsReference.getFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ClassFieldsReferenceJSONParser classFieldsReferenceJSONParser =
			new ClassFieldsReferenceJSONParser();

		return classFieldsReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ClassFieldsReference classFieldsReference) {

		if (classFieldsReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (classFieldsReference.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(classFieldsReference.getClassName()));
		}

		if (classFieldsReference.getFields() == null) {
			map.put("fields", null);
		}
		else {
			map.put("fields", String.valueOf(classFieldsReference.getFields()));
		}

		return map;
	}

	public static class ClassFieldsReferenceJSONParser
		extends BaseJSONParser<ClassFieldsReference> {

		@Override
		protected ClassFieldsReference createDTO() {
			return new ClassFieldsReference();
		}

		@Override
		protected ClassFieldsReference[] createDTOArray(int size) {
			return new ClassFieldsReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fields")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ClassFieldsReference classFieldsReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					classFieldsReference.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Field[] fieldsArray =
						new Field[jsonParserFieldValues.length];

					for (int i = 0; i < fieldsArray.length; i++) {
						fieldsArray[i] = FieldSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					classFieldsReference.setFields(fieldsArray);
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