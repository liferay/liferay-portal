/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifierProduct;
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
public class PriceModifierProductSerDes {

	public static PriceModifierProduct toDTO(String json) {
		PriceModifierProductJSONParser priceModifierProductJSONParser =
			new PriceModifierProductJSONParser();

		return priceModifierProductJSONParser.parseToDTO(json);
	}

	public static PriceModifierProduct[] toDTOs(String json) {
		PriceModifierProductJSONParser priceModifierProductJSONParser =
			new PriceModifierProductJSONParser();

		return priceModifierProductJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PriceModifierProduct priceModifierProduct) {
		if (priceModifierProduct == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (priceModifierProduct.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(priceModifierProduct.getActions()));
		}

		if (priceModifierProduct.getPriceModifierExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					priceModifierProduct.
						getPriceModifierExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceModifierProduct.getPriceModifierId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierId\": ");

			sb.append(priceModifierProduct.getPriceModifierId());
		}

		if (priceModifierProduct.getPriceModifierProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierProductId\": ");

			sb.append(priceModifierProduct.getPriceModifierProductId());
		}

		if (priceModifierProduct.getProduct() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"product\": ");

			sb.append(String.valueOf(priceModifierProduct.getProduct()));
		}

		if (priceModifierProduct.getProductExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					priceModifierProduct.getProductExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceModifierProduct.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(priceModifierProduct.getProductId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PriceModifierProductJSONParser priceModifierProductJSONParser =
			new PriceModifierProductJSONParser();

		return priceModifierProductJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PriceModifierProduct priceModifierProduct) {

		if (priceModifierProduct == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (priceModifierProduct.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(priceModifierProduct.getActions()));
		}

		if (priceModifierProduct.getPriceModifierExternalReferenceCode() ==
				null) {

			map.put("priceModifierExternalReferenceCode", null);
		}
		else {
			map.put(
				"priceModifierExternalReferenceCode",
				String.valueOf(
					priceModifierProduct.
						getPriceModifierExternalReferenceCode()));
		}

		if (priceModifierProduct.getPriceModifierId() == null) {
			map.put("priceModifierId", null);
		}
		else {
			map.put(
				"priceModifierId",
				String.valueOf(priceModifierProduct.getPriceModifierId()));
		}

		if (priceModifierProduct.getPriceModifierProductId() == null) {
			map.put("priceModifierProductId", null);
		}
		else {
			map.put(
				"priceModifierProductId",
				String.valueOf(
					priceModifierProduct.getPriceModifierProductId()));
		}

		if (priceModifierProduct.getProduct() == null) {
			map.put("product", null);
		}
		else {
			map.put(
				"product", String.valueOf(priceModifierProduct.getProduct()));
		}

		if (priceModifierProduct.getProductExternalReferenceCode() == null) {
			map.put("productExternalReferenceCode", null);
		}
		else {
			map.put(
				"productExternalReferenceCode",
				String.valueOf(
					priceModifierProduct.getProductExternalReferenceCode()));
		}

		if (priceModifierProduct.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put(
				"productId",
				String.valueOf(priceModifierProduct.getProductId()));
		}

		return map;
	}

	public static class PriceModifierProductJSONParser
		extends BaseJSONParser<PriceModifierProduct> {

		@Override
		protected PriceModifierProduct createDTO() {
			return new PriceModifierProduct();
		}

		@Override
		protected PriceModifierProduct[] createDTOArray(int size) {
			return new PriceModifierProduct[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"priceModifierExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priceModifierId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierProductId")) {

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

			return false;
		}

		@Override
		protected void setField(
			PriceModifierProduct priceModifierProduct,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					priceModifierProduct.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"priceModifierExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceModifierProduct.setPriceModifierExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priceModifierId")) {
				if (jsonParserFieldValue != null) {
					priceModifierProduct.setPriceModifierId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierProductId")) {

				if (jsonParserFieldValue != null) {
					priceModifierProduct.setPriceModifierProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "product")) {
				if (jsonParserFieldValue != null) {
					priceModifierProduct.setProduct(
						ProductSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceModifierProduct.setProductExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					priceModifierProduct.setProductId(
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