/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v1_0.DiscountCategory;
import com.liferay.headless.commerce.admin.pricing.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class DiscountCategorySerDes {

	public static DiscountCategory toDTO(String json) {
		DiscountCategoryJSONParser discountCategoryJSONParser =
			new DiscountCategoryJSONParser();

		return discountCategoryJSONParser.parseToDTO(json);
	}

	public static DiscountCategory[] toDTOs(String json) {
		DiscountCategoryJSONParser discountCategoryJSONParser =
			new DiscountCategoryJSONParser();

		return discountCategoryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DiscountCategory discountCategory) {
		if (discountCategory == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (discountCategory.getCategoryExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(discountCategory.getCategoryExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountCategory.getCategoryId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoryId\": ");

			sb.append(discountCategory.getCategoryId());
		}

		if (discountCategory.getDiscountExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(discountCategory.getDiscountExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountCategory.getDiscountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountId\": ");

			sb.append(discountCategory.getDiscountId());
		}

		if (discountCategory.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(discountCategory.getId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DiscountCategoryJSONParser discountCategoryJSONParser =
			new DiscountCategoryJSONParser();

		return discountCategoryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DiscountCategory discountCategory) {
		if (discountCategory == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (discountCategory.getCategoryExternalReferenceCode() == null) {
			map.put("categoryExternalReferenceCode", null);
		}
		else {
			map.put(
				"categoryExternalReferenceCode",
				String.valueOf(
					discountCategory.getCategoryExternalReferenceCode()));
		}

		if (discountCategory.getCategoryId() == null) {
			map.put("categoryId", null);
		}
		else {
			map.put(
				"categoryId", String.valueOf(discountCategory.getCategoryId()));
		}

		if (discountCategory.getDiscountExternalReferenceCode() == null) {
			map.put("discountExternalReferenceCode", null);
		}
		else {
			map.put(
				"discountExternalReferenceCode",
				String.valueOf(
					discountCategory.getDiscountExternalReferenceCode()));
		}

		if (discountCategory.getDiscountId() == null) {
			map.put("discountId", null);
		}
		else {
			map.put(
				"discountId", String.valueOf(discountCategory.getDiscountId()));
		}

		if (discountCategory.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(discountCategory.getId()));
		}

		return map;
	}

	public static class DiscountCategoryJSONParser
		extends BaseJSONParser<DiscountCategory> {

		@Override
		protected DiscountCategory createDTO() {
			return new DiscountCategory();
		}

		@Override
		protected DiscountCategory[] createDTOArray(int size) {
			return new DiscountCategory[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "categoryExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "categoryId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DiscountCategory discountCategory, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "categoryExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountCategory.setCategoryExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "categoryId")) {
				if (jsonParserFieldValue != null) {
					discountCategory.setCategoryId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountCategory.setDiscountExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountId")) {
				if (jsonParserFieldValue != null) {
					discountCategory.setDiscountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					discountCategory.setId(
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