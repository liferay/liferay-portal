/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerClassSubtypeReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerContextReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FormContainerReference;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

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
public class FormContainerReferenceSerDes {

	public static FormContainerReference toDTO(String json) {
		FormContainerReferenceJSONParser formContainerReferenceJSONParser =
			new FormContainerReferenceJSONParser();

		return formContainerReferenceJSONParser.parseToDTO(json);
	}

	public static FormContainerReference[] toDTOs(String json) {
		FormContainerReferenceJSONParser formContainerReferenceJSONParser =
			new FormContainerReferenceJSONParser();

		return formContainerReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FormContainerReference formContainerReference) {
		if (formContainerReference == null) {
			return "null";
		}

		FormContainerReference.Type type = formContainerReference.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("FormContainerClassSubtypeReference")) {
				return FormContainerClassSubtypeReferenceSerDes.toJSON(
					(FormContainerClassSubtypeReference)formContainerReference);
			}

			if (typeString.equals("FormContainerContextReference")) {
				return FormContainerContextReferenceSerDes.toJSON(
					(FormContainerContextReference)formContainerReference);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		FormContainerReferenceJSONParser formContainerReferenceJSONParser =
			new FormContainerReferenceJSONParser();

		return formContainerReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FormContainerReference formContainerReference) {

		if (formContainerReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formContainerReference.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(formContainerReference.getType()));
		}

		return map;
	}

	public static class FormContainerReferenceJSONParser
		extends BaseJSONParser<FormContainerReference> {

		@Override
		protected FormContainerReference createDTO() {
			return null;
		}

		@Override
		protected FormContainerReference[] createDTOArray(int size) {
			return new FormContainerReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public FormContainerReference parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("FormContainerClassSubtypeReference")) {
					return FormContainerClassSubtypeReference.toDTO(json);
				}

				if (typeString.equals("FormContainerContextReference")) {
					return FormContainerContextReference.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			FormContainerReference formContainerReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					formContainerReference.setType(
						FormContainerReference.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:472092599