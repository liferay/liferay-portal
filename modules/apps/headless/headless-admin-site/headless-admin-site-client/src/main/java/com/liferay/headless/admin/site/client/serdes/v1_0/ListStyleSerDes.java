/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ListStyle;
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
public class ListStyleSerDes {

	public static ListStyle toDTO(String json) {
		ListStyleJSONParser listStyleJSONParser = new ListStyleJSONParser();

		return listStyleJSONParser.parseToDTO(json);
	}

	public static ListStyle[] toDTOs(String json) {
		ListStyleJSONParser listStyleJSONParser = new ListStyleJSONParser();

		return listStyleJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ListStyle listStyle) {
		if (listStyle == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (listStyle.getListStyleDefinition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listStyleDefinition\": ");

			sb.append(String.valueOf(listStyle.getListStyleDefinition()));
		}

		if (listStyle.getListStyleType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listStyleType\": ");

			sb.append("\"");
			sb.append(listStyle.getListStyleType());
			sb.append("\"");
		}

		if (listStyle.getCollectionDisplayListStyleType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionDisplayListStyleType\": ");

			sb.append("\"");
			sb.append(listStyle.getCollectionDisplayListStyleType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ListStyleJSONParser listStyleJSONParser = new ListStyleJSONParser();

		return listStyleJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ListStyle listStyle) {
		if (listStyle == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (listStyle.getListStyleDefinition() == null) {
			map.put("listStyleDefinition", null);
		}
		else {
			map.put(
				"listStyleDefinition",
				String.valueOf(listStyle.getListStyleDefinition()));
		}

		if (listStyle.getListStyleType() == null) {
			map.put("listStyleType", null);
		}
		else {
			map.put(
				"listStyleType", String.valueOf(listStyle.getListStyleType()));
		}

		if (listStyle.getCollectionDisplayListStyleType() == null) {
			map.put("collectionDisplayListStyleType", null);
		}
		else {
			map.put(
				"collectionDisplayListStyleType",
				String.valueOf(listStyle.getCollectionDisplayListStyleType()));
		}

		return map;
	}

	public static class ListStyleJSONParser extends BaseJSONParser<ListStyle> {

		@Override
		protected ListStyle createDTO() {
			return new ListStyle();
		}

		@Override
		protected ListStyle[] createDTOArray(int size) {
			return new ListStyle[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "listStyleDefinition")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "listStyleType")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"collectionDisplayListStyleType")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ListStyle listStyle, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "listStyleDefinition")) {
				if (jsonParserFieldValue != null) {
					listStyle.setListStyleDefinition(
						ListStyleDefinitionSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "listStyleType")) {
				if (jsonParserFieldValue != null) {
					listStyle.setListStyleType(
						ListStyle.ListStyleType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"collectionDisplayListStyleType")) {

				if (jsonParserFieldValue != null) {
					listStyle.setCollectionDisplayListStyleType(
						ListStyle.CollectionDisplayListStyleType.create(
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