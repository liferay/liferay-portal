/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemContextReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentMappedValueItemReference;
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
public class FragmentMappedValueItemReferenceSerDes {

	public static FragmentMappedValueItemReference toDTO(String json) {
		FragmentMappedValueItemReferenceJSONParser
			fragmentMappedValueItemReferenceJSONParser =
				new FragmentMappedValueItemReferenceJSONParser();

		return fragmentMappedValueItemReferenceJSONParser.parseToDTO(json);
	}

	public static FragmentMappedValueItemReference[] toDTOs(String json) {
		FragmentMappedValueItemReferenceJSONParser
			fragmentMappedValueItemReferenceJSONParser =
				new FragmentMappedValueItemReferenceJSONParser();

		return fragmentMappedValueItemReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		FragmentMappedValueItemReference fragmentMappedValueItemReference) {

		if (fragmentMappedValueItemReference == null) {
			return "null";
		}

		FragmentMappedValueItemReference.Type type =
			fragmentMappedValueItemReference.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("ContextReference")) {
				return FragmentMappedValueItemContextReferenceSerDes.toJSON(
					(FragmentMappedValueItemContextReference)
						fragmentMappedValueItemReference);
			}

			if (typeString.equals("ItemExternalReference")) {
				return FragmentMappedValueItemExternalReferenceSerDes.toJSON(
					(FragmentMappedValueItemExternalReference)
						fragmentMappedValueItemReference);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		FragmentMappedValueItemReferenceJSONParser
			fragmentMappedValueItemReferenceJSONParser =
				new FragmentMappedValueItemReferenceJSONParser();

		return fragmentMappedValueItemReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentMappedValueItemReference fragmentMappedValueItemReference) {

		if (fragmentMappedValueItemReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentMappedValueItemReference.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(fragmentMappedValueItemReference.getType()));
		}

		return map;
	}

	public static class FragmentMappedValueItemReferenceJSONParser
		extends BaseJSONParser<FragmentMappedValueItemReference> {

		@Override
		protected FragmentMappedValueItemReference createDTO() {
			return null;
		}

		@Override
		protected FragmentMappedValueItemReference[] createDTOArray(int size) {
			return new FragmentMappedValueItemReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public FragmentMappedValueItemReference parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("ContextReference")) {
					return FragmentMappedValueItemContextReference.toDTO(json);
				}

				if (typeString.equals("ItemExternalReference")) {
					return FragmentMappedValueItemExternalReference.toDTO(json);
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
			FragmentMappedValueItemReference fragmentMappedValueItemReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					fragmentMappedValueItemReference.setType(
						FragmentMappedValueItemReference.Type.create(
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