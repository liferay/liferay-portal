/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.ProductGroup;
import com.liferay.headless.commerce.admin.pricing.client.json.BaseJSONParser;

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
public class ProductGroupSerDes {

	public static ProductGroup toDTO(String json) {
		ProductGroupJSONParser productGroupJSONParser =
			new ProductGroupJSONParser();

		return productGroupJSONParser.parseToDTO(json);
	}

	public static ProductGroup[] toDTOs(String json) {
		ProductGroupJSONParser productGroupJSONParser =
			new ProductGroupJSONParser();

		return productGroupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ProductGroup productGroup) {
		if (productGroup == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productGroup.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(productGroup.getId());
		}

		if (productGroup.getProductsCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productsCount\": ");

			sb.append(productGroup.getProductsCount());
		}

		if (productGroup.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append(_toJSON(productGroup.getTitle()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductGroupJSONParser productGroupJSONParser =
			new ProductGroupJSONParser();

		return productGroupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ProductGroup productGroup) {
		if (productGroup == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productGroup.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(productGroup.getId()));
		}

		if (productGroup.getProductsCount() == null) {
			map.put("productsCount", null);
		}
		else {
			map.put(
				"productsCount",
				String.valueOf(productGroup.getProductsCount()));
		}

		if (productGroup.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(productGroup.getTitle()));
		}

		return map;
	}

	public static class ProductGroupJSONParser
		extends BaseJSONParser<ProductGroup> {

		@Override
		protected ProductGroup createDTO() {
			return new ProductGroup();
		}

		@Override
		protected ProductGroup[] createDTOArray(int size) {
			return new ProductGroup[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productsCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductGroup productGroup, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					productGroup.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productsCount")) {
				if (jsonParserFieldValue != null) {
					productGroup.setProductsCount(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					productGroup.setTitle(
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