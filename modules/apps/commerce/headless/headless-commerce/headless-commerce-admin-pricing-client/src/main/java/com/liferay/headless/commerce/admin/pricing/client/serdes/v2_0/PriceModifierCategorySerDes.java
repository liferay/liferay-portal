/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifierCategory;
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
public class PriceModifierCategorySerDes {

	public static PriceModifierCategory toDTO(String json) {
		PriceModifierCategoryJSONParser priceModifierCategoryJSONParser =
			new PriceModifierCategoryJSONParser();

		return priceModifierCategoryJSONParser.parseToDTO(json);
	}

	public static PriceModifierCategory[] toDTOs(String json) {
		PriceModifierCategoryJSONParser priceModifierCategoryJSONParser =
			new PriceModifierCategoryJSONParser();

		return priceModifierCategoryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PriceModifierCategory priceModifierCategory) {
		if (priceModifierCategory == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (priceModifierCategory.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(priceModifierCategory.getActions()));
		}

		if (priceModifierCategory.getCategory() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"category\": ");

			sb.append(String.valueOf(priceModifierCategory.getCategory()));
		}

		if (priceModifierCategory.getCategoryExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					priceModifierCategory.getCategoryExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceModifierCategory.getCategoryId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoryId\": ");

			sb.append(priceModifierCategory.getCategoryId());
		}

		if (priceModifierCategory.getPriceModifierCategoryId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierCategoryId\": ");

			sb.append(priceModifierCategory.getPriceModifierCategoryId());
		}

		if (priceModifierCategory.getPriceModifierExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					priceModifierCategory.
						getPriceModifierExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceModifierCategory.getPriceModifierId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierId\": ");

			sb.append(priceModifierCategory.getPriceModifierId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PriceModifierCategoryJSONParser priceModifierCategoryJSONParser =
			new PriceModifierCategoryJSONParser();

		return priceModifierCategoryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PriceModifierCategory priceModifierCategory) {

		if (priceModifierCategory == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (priceModifierCategory.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(priceModifierCategory.getActions()));
		}

		if (priceModifierCategory.getCategory() == null) {
			map.put("category", null);
		}
		else {
			map.put(
				"category",
				String.valueOf(priceModifierCategory.getCategory()));
		}

		if (priceModifierCategory.getCategoryExternalReferenceCode() == null) {
			map.put("categoryExternalReferenceCode", null);
		}
		else {
			map.put(
				"categoryExternalReferenceCode",
				String.valueOf(
					priceModifierCategory.getCategoryExternalReferenceCode()));
		}

		if (priceModifierCategory.getCategoryId() == null) {
			map.put("categoryId", null);
		}
		else {
			map.put(
				"categoryId",
				String.valueOf(priceModifierCategory.getCategoryId()));
		}

		if (priceModifierCategory.getPriceModifierCategoryId() == null) {
			map.put("priceModifierCategoryId", null);
		}
		else {
			map.put(
				"priceModifierCategoryId",
				String.valueOf(
					priceModifierCategory.getPriceModifierCategoryId()));
		}

		if (priceModifierCategory.getPriceModifierExternalReferenceCode() ==
				null) {

			map.put("priceModifierExternalReferenceCode", null);
		}
		else {
			map.put(
				"priceModifierExternalReferenceCode",
				String.valueOf(
					priceModifierCategory.
						getPriceModifierExternalReferenceCode()));
		}

		if (priceModifierCategory.getPriceModifierId() == null) {
			map.put("priceModifierId", null);
		}
		else {
			map.put(
				"priceModifierId",
				String.valueOf(priceModifierCategory.getPriceModifierId()));
		}

		return map;
	}

	public static class PriceModifierCategoryJSONParser
		extends BaseJSONParser<PriceModifierCategory> {

		@Override
		protected PriceModifierCategory createDTO() {
			return new PriceModifierCategory();
		}

		@Override
		protected PriceModifierCategory[] createDTOArray(int size) {
			return new PriceModifierCategory[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "category")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "categoryExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "categoryId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierCategoryId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"priceModifierExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priceModifierId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PriceModifierCategory priceModifierCategory,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					priceModifierCategory.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "category")) {
				if (jsonParserFieldValue != null) {
					priceModifierCategory.setCategory(
						CategorySerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "categoryExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceModifierCategory.setCategoryExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "categoryId")) {
				if (jsonParserFieldValue != null) {
					priceModifierCategory.setCategoryId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierCategoryId")) {

				if (jsonParserFieldValue != null) {
					priceModifierCategory.setPriceModifierCategoryId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"priceModifierExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceModifierCategory.setPriceModifierExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priceModifierId")) {
				if (jsonParserFieldValue != null) {
					priceModifierCategory.setPriceModifierId(
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