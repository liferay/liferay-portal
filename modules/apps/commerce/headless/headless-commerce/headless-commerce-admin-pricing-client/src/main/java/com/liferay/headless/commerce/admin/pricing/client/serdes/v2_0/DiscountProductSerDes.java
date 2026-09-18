/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountProduct;
import com.liferay.headless.commerce.admin.pricing.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class DiscountProductSerDes {

	public static DiscountProduct toDTO(String json) {
		DiscountProductJSONParser discountProductJSONParser =
			new DiscountProductJSONParser();

		return discountProductJSONParser.parseToDTO(json);
	}

	public static DiscountProduct[] toDTOs(String json) {
		DiscountProductJSONParser discountProductJSONParser =
			new DiscountProductJSONParser();

		return discountProductJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DiscountProduct discountProduct) {
		if (discountProduct == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (discountProduct.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(discountProduct.getActions()));
		}

		if (discountProduct.getCatalogCurrencyCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogCurrencyCode\": ");

			sb.append("\"");

			sb.append(_escape(discountProduct.getCatalogCurrencyCode()));

			sb.append("\"");
		}

		if (discountProduct.getCatalogCurrencyExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogCurrencyExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					discountProduct.getCatalogCurrencyExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountProduct.getCatalogExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(discountProduct.getCatalogExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountProduct.getDiscountExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(discountProduct.getDiscountExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountProduct.getDiscountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountId\": ");

			sb.append(discountProduct.getDiscountId());
		}

		if (discountProduct.getDiscountProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountProductId\": ");

			sb.append(discountProduct.getDiscountProductId());
		}

		if (discountProduct.getProduct() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"product\": ");

			sb.append(String.valueOf(discountProduct.getProduct()));
		}

		if (discountProduct.getProductExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(discountProduct.getProductExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountProduct.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(discountProduct.getProductId());
		}

		if (discountProduct.getProductType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productType\": ");

			sb.append("\"");

			sb.append(_escape(discountProduct.getProductType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DiscountProductJSONParser discountProductJSONParser =
			new DiscountProductJSONParser();

		return discountProductJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DiscountProduct discountProduct) {
		if (discountProduct == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (discountProduct.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(discountProduct.getActions()));
		}

		if (discountProduct.getCatalogCurrencyCode() == null) {
			map.put("catalogCurrencyCode", null);
		}
		else {
			map.put(
				"catalogCurrencyCode",
				String.valueOf(discountProduct.getCatalogCurrencyCode()));
		}

		if (discountProduct.getCatalogCurrencyExternalReferenceCode() == null) {
			map.put("catalogCurrencyExternalReferenceCode", null);
		}
		else {
			map.put(
				"catalogCurrencyExternalReferenceCode",
				String.valueOf(
					discountProduct.getCatalogCurrencyExternalReferenceCode()));
		}

		if (discountProduct.getCatalogExternalReferenceCode() == null) {
			map.put("catalogExternalReferenceCode", null);
		}
		else {
			map.put(
				"catalogExternalReferenceCode",
				String.valueOf(
					discountProduct.getCatalogExternalReferenceCode()));
		}

		if (discountProduct.getDiscountExternalReferenceCode() == null) {
			map.put("discountExternalReferenceCode", null);
		}
		else {
			map.put(
				"discountExternalReferenceCode",
				String.valueOf(
					discountProduct.getDiscountExternalReferenceCode()));
		}

		if (discountProduct.getDiscountId() == null) {
			map.put("discountId", null);
		}
		else {
			map.put(
				"discountId", String.valueOf(discountProduct.getDiscountId()));
		}

		if (discountProduct.getDiscountProductId() == null) {
			map.put("discountProductId", null);
		}
		else {
			map.put(
				"discountProductId",
				String.valueOf(discountProduct.getDiscountProductId()));
		}

		if (discountProduct.getProduct() == null) {
			map.put("product", null);
		}
		else {
			map.put("product", String.valueOf(discountProduct.getProduct()));
		}

		if (discountProduct.getProductExternalReferenceCode() == null) {
			map.put("productExternalReferenceCode", null);
		}
		else {
			map.put(
				"productExternalReferenceCode",
				String.valueOf(
					discountProduct.getProductExternalReferenceCode()));
		}

		if (discountProduct.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put(
				"productId", String.valueOf(discountProduct.getProductId()));
		}

		if (discountProduct.getProductType() == null) {
			map.put("productType", null);
		}
		else {
			map.put(
				"productType",
				String.valueOf(discountProduct.getProductType()));
		}

		return map;
	}

	public static class DiscountProductJSONParser
		extends BaseJSONParser<DiscountProduct> {

		@Override
		protected DiscountProduct createDTO() {
			return new DiscountProduct();
		}

		@Override
		protected DiscountProduct[] createDTOArray(int size) {
			return new DiscountProduct[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "catalogCurrencyCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"catalogCurrencyExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "catalogExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discountProductId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "product")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DiscountProduct discountProduct, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					discountProduct.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "catalogCurrencyCode")) {

				if (jsonParserFieldValue != null) {
					discountProduct.setCatalogCurrencyCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"catalogCurrencyExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountProduct.setCatalogCurrencyExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "catalogExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountProduct.setCatalogExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountProduct.setDiscountExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountId")) {
				if (jsonParserFieldValue != null) {
					discountProduct.setDiscountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountProductId")) {
				if (jsonParserFieldValue != null) {
					discountProduct.setDiscountProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "product")) {
				if (jsonParserFieldValue != null) {
					discountProduct.setProduct(
						ProductSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountProduct.setProductExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					discountProduct.setProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productType")) {
				if (jsonParserFieldValue != null) {
					discountProduct.setProductType(
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:-1897573510