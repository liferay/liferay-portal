/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ClassSubtypeReference;
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
public class ClassSubtypeReferenceSerDes {

	public static ClassSubtypeReference toDTO(String json) {
		ClassSubtypeReferenceJSONParser classSubtypeReferenceJSONParser =
			new ClassSubtypeReferenceJSONParser();

		return classSubtypeReferenceJSONParser.parseToDTO(json);
	}

	public static ClassSubtypeReference[] toDTOs(String json) {
		ClassSubtypeReferenceJSONParser classSubtypeReferenceJSONParser =
			new ClassSubtypeReferenceJSONParser();

		return classSubtypeReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ClassSubtypeReference classSubtypeReference) {
		if (classSubtypeReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (classSubtypeReference.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(classSubtypeReference.getClassName()));

			sb.append("\"");
		}

		if (classSubtypeReference.getSubTypeExternalReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subTypeExternalReference\": ");

			sb.append(
				String.valueOf(
					classSubtypeReference.getSubTypeExternalReference()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ClassSubtypeReferenceJSONParser classSubtypeReferenceJSONParser =
			new ClassSubtypeReferenceJSONParser();

		return classSubtypeReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ClassSubtypeReference classSubtypeReference) {

		if (classSubtypeReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (classSubtypeReference.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(classSubtypeReference.getClassName()));
		}

		if (classSubtypeReference.getSubTypeExternalReference() == null) {
			map.put("subTypeExternalReference", null);
		}
		else {
			map.put(
				"subTypeExternalReference",
				String.valueOf(
					classSubtypeReference.getSubTypeExternalReference()));
		}

		return map;
	}

	public static class ClassSubtypeReferenceJSONParser
		extends BaseJSONParser<ClassSubtypeReference> {

		@Override
		protected ClassSubtypeReference createDTO() {
			return new ClassSubtypeReference();
		}

		@Override
		protected ClassSubtypeReference[] createDTOArray(int size) {
			return new ClassSubtypeReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "subTypeExternalReference")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ClassSubtypeReference classSubtypeReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					classSubtypeReference.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "subTypeExternalReference")) {

				if (jsonParserFieldValue != null) {
					classSubtypeReference.setSubTypeExternalReference(
						ItemExternalReferenceSerDes.toDTO(
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