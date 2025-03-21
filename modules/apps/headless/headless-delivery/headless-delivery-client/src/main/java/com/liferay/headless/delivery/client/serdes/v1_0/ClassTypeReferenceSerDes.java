/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.ClassTypeReference;
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
public class ClassTypeReferenceSerDes {

	public static ClassTypeReference toDTO(String json) {
		ClassTypeReferenceJSONParser classTypeReferenceJSONParser =
			new ClassTypeReferenceJSONParser();

		return classTypeReferenceJSONParser.parseToDTO(json);
	}

	public static ClassTypeReference[] toDTOs(String json) {
		ClassTypeReferenceJSONParser classTypeReferenceJSONParser =
			new ClassTypeReferenceJSONParser();

		return classTypeReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ClassTypeReference classTypeReference) {
		if (classTypeReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (classTypeReference.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(classTypeReference.getClassName()));

			sb.append("\"");
		}

		if (classTypeReference.getClassType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classType\": ");

			sb.append(classTypeReference.getClassType());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ClassTypeReferenceJSONParser classTypeReferenceJSONParser =
			new ClassTypeReferenceJSONParser();

		return classTypeReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ClassTypeReference classTypeReference) {

		if (classTypeReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (classTypeReference.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className", String.valueOf(classTypeReference.getClassName()));
		}

		if (classTypeReference.getClassType() == null) {
			map.put("classType", null);
		}
		else {
			map.put(
				"classType", String.valueOf(classTypeReference.getClassType()));
		}

		return map;
	}

	public static class ClassTypeReferenceJSONParser
		extends BaseJSONParser<ClassTypeReference> {

		@Override
		protected ClassTypeReference createDTO() {
			return new ClassTypeReference();
		}

		@Override
		protected ClassTypeReference[] createDTOArray(int size) {
			return new ClassTypeReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "classType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ClassTypeReference classTypeReference, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					classTypeReference.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "classType")) {
				if (jsonParserFieldValue != null) {
					classTypeReference.setClassType(
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