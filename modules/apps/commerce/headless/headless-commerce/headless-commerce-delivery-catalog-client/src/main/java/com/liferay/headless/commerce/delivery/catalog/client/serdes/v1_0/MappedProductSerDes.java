/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.SkuOption;
import com.liferay.headless.commerce.delivery.catalog.client.json.BaseJSONParser;

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
public class MappedProductSerDes {

	public static MappedProduct toDTO(String json) {
		MappedProductJSONParser mappedProductJSONParser =
			new MappedProductJSONParser();

		return mappedProductJSONParser.parseToDTO(json);
	}

	public static MappedProduct[] toDTOs(String json) {
		MappedProductJSONParser mappedProductJSONParser =
			new MappedProductJSONParser();

		return mappedProductJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MappedProduct mappedProduct) {
		if (mappedProduct == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (mappedProduct.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(mappedProduct.getActions()));
		}

		if (mappedProduct.getAvailability() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availability\": ");

			sb.append(String.valueOf(mappedProduct.getAvailability()));
		}

		if (mappedProduct.getFirstAvailableReplacementMappedProduct() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"firstAvailableReplacementMappedProduct\": ");

			sb.append(
				String.valueOf(
					mappedProduct.getFirstAvailableReplacementMappedProduct()));
		}

		if (mappedProduct.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(mappedProduct.getId());
		}

		if (mappedProduct.getPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(String.valueOf(mappedProduct.getPrice()));
		}

		if (mappedProduct.getProductConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfiguration\": ");

			sb.append(String.valueOf(mappedProduct.getProductConfiguration()));
		}

		if (mappedProduct.getProductExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(mappedProduct.getProductExternalReferenceCode()));

			sb.append("\"");
		}

		if (mappedProduct.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(mappedProduct.getProductId());
		}

		if (mappedProduct.getProductName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productName\": ");

			sb.append(_toJSON(mappedProduct.getProductName()));
		}

		if (mappedProduct.getProductOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productOptions\": ");

			sb.append("[");

			for (int i = 0; i < mappedProduct.getProductOptions().length; i++) {
				sb.append(String.valueOf(mappedProduct.getProductOptions()[i]));

				if ((i + 1) < mappedProduct.getProductOptions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (mappedProduct.getPurchasable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"purchasable\": ");

			sb.append(mappedProduct.getPurchasable());
		}

		if (mappedProduct.getQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(mappedProduct.getQuantity());
		}

		if (mappedProduct.getReplacementMappedProduct() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacementMappedProduct\": ");

			sb.append(
				String.valueOf(mappedProduct.getReplacementMappedProduct()));
		}

		if (mappedProduct.getReplacementMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"replacementMessage\": ");

			sb.append("\"");

			sb.append(_escape(mappedProduct.getReplacementMessage()));

			sb.append("\"");
		}

		if (mappedProduct.getSequence() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sequence\": ");

			sb.append("\"");

			sb.append(_escape(mappedProduct.getSequence()));

			sb.append("\"");
		}

		if (mappedProduct.getSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append("\"");

			sb.append(_escape(mappedProduct.getSku()));

			sb.append("\"");
		}

		if (mappedProduct.getSkuExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(mappedProduct.getSkuExternalReferenceCode()));

			sb.append("\"");
		}

		if (mappedProduct.getSkuId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(mappedProduct.getSkuId());
		}

		if (mappedProduct.getSkuOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuOptions\": ");

			sb.append("[");

			for (int i = 0; i < mappedProduct.getSkuOptions().length; i++) {
				sb.append(String.valueOf(mappedProduct.getSkuOptions()[i]));

				if ((i + 1) < mappedProduct.getSkuOptions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (mappedProduct.getThumbnail() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnail\": ");

			sb.append("\"");

			sb.append(_escape(mappedProduct.getThumbnail()));

			sb.append("\"");
		}

		if (mappedProduct.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(mappedProduct.getType());

			sb.append("\"");
		}

		if (mappedProduct.getUrls() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"urls\": ");

			sb.append(_toJSON(mappedProduct.getUrls()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MappedProductJSONParser mappedProductJSONParser =
			new MappedProductJSONParser();

		return mappedProductJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(MappedProduct mappedProduct) {
		if (mappedProduct == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (mappedProduct.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(mappedProduct.getActions()));
		}

		if (mappedProduct.getAvailability() == null) {
			map.put("availability", null);
		}
		else {
			map.put(
				"availability",
				String.valueOf(mappedProduct.getAvailability()));
		}

		if (mappedProduct.getFirstAvailableReplacementMappedProduct() == null) {
			map.put("firstAvailableReplacementMappedProduct", null);
		}
		else {
			map.put(
				"firstAvailableReplacementMappedProduct",
				String.valueOf(
					mappedProduct.getFirstAvailableReplacementMappedProduct()));
		}

		if (mappedProduct.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(mappedProduct.getId()));
		}

		if (mappedProduct.getPrice() == null) {
			map.put("price", null);
		}
		else {
			map.put("price", String.valueOf(mappedProduct.getPrice()));
		}

		if (mappedProduct.getProductConfiguration() == null) {
			map.put("productConfiguration", null);
		}
		else {
			map.put(
				"productConfiguration",
				String.valueOf(mappedProduct.getProductConfiguration()));
		}

		if (mappedProduct.getProductExternalReferenceCode() == null) {
			map.put("productExternalReferenceCode", null);
		}
		else {
			map.put(
				"productExternalReferenceCode",
				String.valueOf(
					mappedProduct.getProductExternalReferenceCode()));
		}

		if (mappedProduct.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put("productId", String.valueOf(mappedProduct.getProductId()));
		}

		if (mappedProduct.getProductName() == null) {
			map.put("productName", null);
		}
		else {
			map.put(
				"productName", String.valueOf(mappedProduct.getProductName()));
		}

		if (mappedProduct.getProductOptions() == null) {
			map.put("productOptions", null);
		}
		else {
			map.put(
				"productOptions",
				String.valueOf(mappedProduct.getProductOptions()));
		}

		if (mappedProduct.getPurchasable() == null) {
			map.put("purchasable", null);
		}
		else {
			map.put(
				"purchasable", String.valueOf(mappedProduct.getPurchasable()));
		}

		if (mappedProduct.getQuantity() == null) {
			map.put("quantity", null);
		}
		else {
			map.put("quantity", String.valueOf(mappedProduct.getQuantity()));
		}

		if (mappedProduct.getReplacementMappedProduct() == null) {
			map.put("replacementMappedProduct", null);
		}
		else {
			map.put(
				"replacementMappedProduct",
				String.valueOf(mappedProduct.getReplacementMappedProduct()));
		}

		if (mappedProduct.getReplacementMessage() == null) {
			map.put("replacementMessage", null);
		}
		else {
			map.put(
				"replacementMessage",
				String.valueOf(mappedProduct.getReplacementMessage()));
		}

		if (mappedProduct.getSequence() == null) {
			map.put("sequence", null);
		}
		else {
			map.put("sequence", String.valueOf(mappedProduct.getSequence()));
		}

		if (mappedProduct.getSku() == null) {
			map.put("sku", null);
		}
		else {
			map.put("sku", String.valueOf(mappedProduct.getSku()));
		}

		if (mappedProduct.getSkuExternalReferenceCode() == null) {
			map.put("skuExternalReferenceCode", null);
		}
		else {
			map.put(
				"skuExternalReferenceCode",
				String.valueOf(mappedProduct.getSkuExternalReferenceCode()));
		}

		if (mappedProduct.getSkuId() == null) {
			map.put("skuId", null);
		}
		else {
			map.put("skuId", String.valueOf(mappedProduct.getSkuId()));
		}

		if (mappedProduct.getSkuOptions() == null) {
			map.put("skuOptions", null);
		}
		else {
			map.put(
				"skuOptions", String.valueOf(mappedProduct.getSkuOptions()));
		}

		if (mappedProduct.getThumbnail() == null) {
			map.put("thumbnail", null);
		}
		else {
			map.put("thumbnail", String.valueOf(mappedProduct.getThumbnail()));
		}

		if (mappedProduct.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(mappedProduct.getType()));
		}

		if (mappedProduct.getUrls() == null) {
			map.put("urls", null);
		}
		else {
			map.put("urls", String.valueOf(mappedProduct.getUrls()));
		}

		return map;
	}

	public static class MappedProductJSONParser
		extends BaseJSONParser<MappedProduct> {

		@Override
		protected MappedProduct createDTO() {
			return new MappedProduct();
		}

		@Override
		protected MappedProduct[] createDTOArray(int size) {
			return new MappedProduct[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "availability")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"firstAvailableReplacementMappedProduct")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfiguration")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productName")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "productOptions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "purchasable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "replacementMappedProduct")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "replacementMessage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sequence")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "skuExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuOptions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "urls")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			MappedProduct mappedProduct, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "availability")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setAvailability(
						AvailabilitySerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"firstAvailableReplacementMappedProduct")) {

				if (jsonParserFieldValue != null) {
					mappedProduct.setFirstAvailableReplacementMappedProduct(
						MappedProductSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setPrice(
						PriceSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfiguration")) {

				if (jsonParserFieldValue != null) {
					mappedProduct.setProductConfiguration(
						ProductConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					mappedProduct.setProductExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productName")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setProductName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productOptions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ProductOption[] productOptionsArray =
						new ProductOption[jsonParserFieldValues.length];

					for (int i = 0; i < productOptionsArray.length; i++) {
						productOptionsArray[i] = ProductOptionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					mappedProduct.setProductOptions(productOptionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "purchasable")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setPurchasable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setQuantity(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "replacementMappedProduct")) {

				if (jsonParserFieldValue != null) {
					mappedProduct.setReplacementMappedProduct(
						MappedProductSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "replacementMessage")) {

				if (jsonParserFieldValue != null) {
					mappedProduct.setReplacementMessage(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sequence")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setSequence((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "skuExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					mappedProduct.setSkuExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setSkuId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuOptions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					SkuOption[] skuOptionsArray =
						new SkuOption[jsonParserFieldValues.length];

					for (int i = 0; i < skuOptionsArray.length; i++) {
						skuOptionsArray[i] = SkuOptionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					mappedProduct.setSkuOptions(skuOptionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setThumbnail((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setType(
						MappedProduct.Type.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "urls")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setUrls(
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