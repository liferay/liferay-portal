/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemExternalReference;
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
public class FragmentMappedValueItemExternalReferenceSerDes {

	public static FragmentMappedValueItemExternalReference toDTO(String json) {
		FragmentMappedValueItemExternalReferenceJSONParser
			fragmentMappedValueItemExternalReferenceJSONParser =
				new FragmentMappedValueItemExternalReferenceJSONParser();

		return fragmentMappedValueItemExternalReferenceJSONParser.parseToDTO(
			json);
	}

	public static FragmentMappedValueItemExternalReference[] toDTOs(
		String json) {

		FragmentMappedValueItemExternalReferenceJSONParser
			fragmentMappedValueItemExternalReferenceJSONParser =
				new FragmentMappedValueItemExternalReferenceJSONParser();

		return fragmentMappedValueItemExternalReferenceJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference) {

		if (fragmentMappedValueItemExternalReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentMappedValueItemExternalReference.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(
				_escape(
					fragmentMappedValueItemExternalReference.getClassName()));

			sb.append("\"");
		}

		if (fragmentMappedValueItemExternalReference.
				getExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					fragmentMappedValueItemExternalReference.
						getExternalReferenceCode()));

			sb.append("\"");
		}

		if (fragmentMappedValueItemExternalReference.getScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append(
				String.valueOf(
					fragmentMappedValueItemExternalReference.getScope()));
		}

		if (fragmentMappedValueItemExternalReference.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(fragmentMappedValueItemExternalReference.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentMappedValueItemExternalReferenceJSONParser
			fragmentMappedValueItemExternalReferenceJSONParser =
				new FragmentMappedValueItemExternalReferenceJSONParser();

		return fragmentMappedValueItemExternalReferenceJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		FragmentMappedValueItemExternalReference
			fragmentMappedValueItemExternalReference) {

		if (fragmentMappedValueItemExternalReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentMappedValueItemExternalReference.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(
					fragmentMappedValueItemExternalReference.getClassName()));
		}

		if (fragmentMappedValueItemExternalReference.
				getExternalReferenceCode() == null) {

			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					fragmentMappedValueItemExternalReference.
						getExternalReferenceCode()));
		}

		if (fragmentMappedValueItemExternalReference.getScope() == null) {
			map.put("scope", null);
		}
		else {
			map.put(
				"scope",
				String.valueOf(
					fragmentMappedValueItemExternalReference.getScope()));
		}

		if (fragmentMappedValueItemExternalReference.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					fragmentMappedValueItemExternalReference.getType()));
		}

		return map;
	}

	public static class FragmentMappedValueItemExternalReferenceJSONParser
		extends BaseJSONParser<FragmentMappedValueItemExternalReference> {

		@Override
		protected FragmentMappedValueItemExternalReference createDTO() {
			return new FragmentMappedValueItemExternalReference();
		}

		@Override
		protected FragmentMappedValueItemExternalReference[] createDTOArray(
			int size) {

			return new FragmentMappedValueItemExternalReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentMappedValueItemExternalReference
				fragmentMappedValueItemExternalReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					fragmentMappedValueItemExternalReference.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					fragmentMappedValueItemExternalReference.
						setExternalReferenceCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				if (jsonParserFieldValue != null) {
					fragmentMappedValueItemExternalReference.setScope(
						ScopeSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					fragmentMappedValueItemExternalReference.setType(
						FragmentMappedValueItemExternalReference.Type.create(
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