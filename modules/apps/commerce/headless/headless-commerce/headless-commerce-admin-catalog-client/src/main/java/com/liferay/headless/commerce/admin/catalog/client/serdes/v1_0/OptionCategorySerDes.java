/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.OptionCategory;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class OptionCategorySerDes {

	public static OptionCategory toDTO(String json) {
		OptionCategoryJSONParser optionCategoryJSONParser =
			new OptionCategoryJSONParser();

		return optionCategoryJSONParser.parseToDTO(json);
	}

	public static OptionCategory[] toDTOs(String json) {
		OptionCategoryJSONParser optionCategoryJSONParser =
			new OptionCategoryJSONParser();

		return optionCategoryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(OptionCategory optionCategory) {
		if (optionCategory == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (optionCategory.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(optionCategory.getDescription()));
		}

		if (optionCategory.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(optionCategory.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (optionCategory.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(optionCategory.getId());
		}

		if (optionCategory.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(optionCategory.getKey()));

			sb.append("\"");
		}

		if (optionCategory.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(optionCategory.getPriority());
		}

		if (optionCategory.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append(_toJSON(optionCategory.getTitle()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OptionCategoryJSONParser optionCategoryJSONParser =
			new OptionCategoryJSONParser();

		return optionCategoryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(OptionCategory optionCategory) {
		if (optionCategory == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (optionCategory.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(optionCategory.getDescription()));
		}

		if (optionCategory.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(optionCategory.getExternalReferenceCode()));
		}

		if (optionCategory.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(optionCategory.getId()));
		}

		if (optionCategory.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(optionCategory.getKey()));
		}

		if (optionCategory.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(optionCategory.getPriority()));
		}

		if (optionCategory.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(optionCategory.getTitle()));
		}

		return map;
	}

	public static class OptionCategoryJSONParser
		extends BaseJSONParser<OptionCategory> {

		@Override
		protected OptionCategory createDTO() {
			return new OptionCategory();
		}

		@Override
		protected OptionCategory[] createDTOArray(int size) {
			return new OptionCategory[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			OptionCategory optionCategory, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					optionCategory.setDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					optionCategory.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					optionCategory.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					optionCategory.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					optionCategory.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					optionCategory.setTitle(
						(Map<String, String>)jsonParserFieldValue);
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