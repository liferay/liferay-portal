/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DefaultFragmentReference;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class DefaultFragmentReferenceSerDes {

	public static DefaultFragmentReference toDTO(String json) {
		DefaultFragmentReferenceJSONParser defaultFragmentReferenceJSONParser =
			new DefaultFragmentReferenceJSONParser();

		return defaultFragmentReferenceJSONParser.parseToDTO(json);
	}

	public static DefaultFragmentReference[] toDTOs(String json) {
		DefaultFragmentReferenceJSONParser defaultFragmentReferenceJSONParser =
			new DefaultFragmentReferenceJSONParser();

		return defaultFragmentReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DefaultFragmentReference defaultFragmentReference) {

		if (defaultFragmentReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (defaultFragmentReference.getDefaultFragmentKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultFragmentKey\": ");

			sb.append("\"");

			sb.append(
				_escape(defaultFragmentReference.getDefaultFragmentKey()));

			sb.append("\"");
		}

		if (defaultFragmentReference.getFragmentReferenceType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentReferenceType\": ");

			sb.append("\"");

			sb.append(defaultFragmentReference.getFragmentReferenceType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DefaultFragmentReferenceJSONParser defaultFragmentReferenceJSONParser =
			new DefaultFragmentReferenceJSONParser();

		return defaultFragmentReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DefaultFragmentReference defaultFragmentReference) {

		if (defaultFragmentReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (defaultFragmentReference.getDefaultFragmentKey() == null) {
			map.put("defaultFragmentKey", null);
		}
		else {
			map.put(
				"defaultFragmentKey",
				String.valueOf(
					defaultFragmentReference.getDefaultFragmentKey()));
		}

		if (defaultFragmentReference.getFragmentReferenceType() == null) {
			map.put("fragmentReferenceType", null);
		}
		else {
			map.put(
				"fragmentReferenceType",
				String.valueOf(
					defaultFragmentReference.getFragmentReferenceType()));
		}

		return map;
	}

	public static class DefaultFragmentReferenceJSONParser
		extends BaseJSONParser<DefaultFragmentReference> {

		@Override
		protected DefaultFragmentReference createDTO() {
			return new DefaultFragmentReference();
		}

		@Override
		protected DefaultFragmentReference[] createDTOArray(int size) {
			return new DefaultFragmentReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "defaultFragmentKey")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "fragmentReferenceType")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DefaultFragmentReference defaultFragmentReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultFragmentKey")) {
				if (jsonParserFieldValue != null) {
					defaultFragmentReference.setDefaultFragmentKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "fragmentReferenceType")) {

				if (jsonParserFieldValue != null) {
					defaultFragmentReference.setFragmentReferenceType(
						DefaultFragmentReference.FragmentReferenceType.create(
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