/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.CategoryDisplayPage;
import com.liferay.headless.commerce.admin.channel.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class CategoryDisplayPageSerDes {

	public static CategoryDisplayPage toDTO(String json) {
		CategoryDisplayPageJSONParser categoryDisplayPageJSONParser =
			new CategoryDisplayPageJSONParser();

		return categoryDisplayPageJSONParser.parseToDTO(json);
	}

	public static CategoryDisplayPage[] toDTOs(String json) {
		CategoryDisplayPageJSONParser categoryDisplayPageJSONParser =
			new CategoryDisplayPageJSONParser();

		return categoryDisplayPageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CategoryDisplayPage categoryDisplayPage) {
		if (categoryDisplayPage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (categoryDisplayPage.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(categoryDisplayPage.getActions()));
		}

		if (categoryDisplayPage.getCategoryExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					categoryDisplayPage.getCategoryExternalReferenceCode()));

			sb.append("\"");
		}

		if (categoryDisplayPage.getCategoryId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoryId\": ");

			sb.append(categoryDisplayPage.getCategoryId());
		}

		if (categoryDisplayPage.getGroupExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(categoryDisplayPage.getGroupExternalReferenceCode()));

			sb.append("\"");
		}

		if (categoryDisplayPage.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(categoryDisplayPage.getId());
		}

		if (categoryDisplayPage.getPageUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageUuid\": ");

			sb.append("\"");

			sb.append(_escape(categoryDisplayPage.getPageUuid()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CategoryDisplayPageJSONParser categoryDisplayPageJSONParser =
			new CategoryDisplayPageJSONParser();

		return categoryDisplayPageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		CategoryDisplayPage categoryDisplayPage) {

		if (categoryDisplayPage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (categoryDisplayPage.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(categoryDisplayPage.getActions()));
		}

		if (categoryDisplayPage.getCategoryExternalReferenceCode() == null) {
			map.put("categoryExternalReferenceCode", null);
		}
		else {
			map.put(
				"categoryExternalReferenceCode",
				String.valueOf(
					categoryDisplayPage.getCategoryExternalReferenceCode()));
		}

		if (categoryDisplayPage.getCategoryId() == null) {
			map.put("categoryId", null);
		}
		else {
			map.put(
				"categoryId",
				String.valueOf(categoryDisplayPage.getCategoryId()));
		}

		if (categoryDisplayPage.getGroupExternalReferenceCode() == null) {
			map.put("groupExternalReferenceCode", null);
		}
		else {
			map.put(
				"groupExternalReferenceCode",
				String.valueOf(
					categoryDisplayPage.getGroupExternalReferenceCode()));
		}

		if (categoryDisplayPage.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(categoryDisplayPage.getId()));
		}

		if (categoryDisplayPage.getPageUuid() == null) {
			map.put("pageUuid", null);
		}
		else {
			map.put(
				"pageUuid", String.valueOf(categoryDisplayPage.getPageUuid()));
		}

		return map;
	}

	public static class CategoryDisplayPageJSONParser
		extends BaseJSONParser<CategoryDisplayPage> {

		@Override
		protected CategoryDisplayPage createDTO() {
			return new CategoryDisplayPage();
		}

		@Override
		protected CategoryDisplayPage[] createDTOArray(int size) {
			return new CategoryDisplayPage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "categoryExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "categoryId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "groupExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageUuid")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CategoryDisplayPage categoryDisplayPage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					categoryDisplayPage.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "categoryExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					categoryDisplayPage.setCategoryExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "categoryId")) {
				if (jsonParserFieldValue != null) {
					categoryDisplayPage.setCategoryId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "groupExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					categoryDisplayPage.setGroupExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					categoryDisplayPage.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageUuid")) {
				if (jsonParserFieldValue != null) {
					categoryDisplayPage.setPageUuid(
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