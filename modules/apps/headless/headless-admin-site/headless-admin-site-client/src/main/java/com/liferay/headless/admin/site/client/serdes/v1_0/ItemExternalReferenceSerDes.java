/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
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
public class ItemExternalReferenceSerDes {

	public static ItemExternalReference toDTO(String json) {
		ItemExternalReferenceJSONParser itemExternalReferenceJSONParser =
			new ItemExternalReferenceJSONParser();

		return itemExternalReferenceJSONParser.parseToDTO(json);
	}

	public static ItemExternalReference[] toDTOs(String json) {
		ItemExternalReferenceJSONParser itemExternalReferenceJSONParser =
			new ItemExternalReferenceJSONParser();

		return itemExternalReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ItemExternalReference itemExternalReference) {
		if (itemExternalReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (itemExternalReference.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(itemExternalReference.getClassName()));

			sb.append("\"");
		}

		if (itemExternalReference.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(itemExternalReference.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (itemExternalReference.getScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append(String.valueOf(itemExternalReference.getScope()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ItemExternalReferenceJSONParser itemExternalReferenceJSONParser =
			new ItemExternalReferenceJSONParser();

		return itemExternalReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ItemExternalReference itemExternalReference) {

		if (itemExternalReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (itemExternalReference.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(itemExternalReference.getClassName()));
		}

		if (itemExternalReference.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					itemExternalReference.getExternalReferenceCode()));
		}

		if (itemExternalReference.getScope() == null) {
			map.put("scope", null);
		}
		else {
			map.put("scope", String.valueOf(itemExternalReference.getScope()));
		}

		return map;
	}

	public static class ItemExternalReferenceJSONParser
		extends BaseJSONParser<ItemExternalReference> {

		@Override
		protected ItemExternalReference createDTO() {
			return new ItemExternalReference();
		}

		@Override
		protected ItemExternalReference[] createDTOArray(int size) {
			return new ItemExternalReference[size];
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

			return false;
		}

		@Override
		protected void setField(
			ItemExternalReference itemExternalReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					itemExternalReference.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					itemExternalReference.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				if (jsonParserFieldValue != null) {
					itemExternalReference.setScope(
						ScopeSerDes.toDTO((String)jsonParserFieldValue));
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