/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.MappedProduct;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

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

		if (mappedProduct.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < mappedProduct.getCustomFields().length; i++) {
				sb.append(mappedProduct.getCustomFields()[i]);

				if ((i + 1) < mappedProduct.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (mappedProduct.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(mappedProduct.getId());
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

		if (mappedProduct.getQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(mappedProduct.getQuantity());
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

		if (mappedProduct.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(mappedProduct.getType());

			sb.append("\"");
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

		if (mappedProduct.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(mappedProduct.getCustomFields()));
		}

		if (mappedProduct.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(mappedProduct.getId()));
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

		if (mappedProduct.getQuantity() == null) {
			map.put("quantity", null);
		}
		else {
			map.put("quantity", String.valueOf(mappedProduct.getQuantity()));
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

		if (mappedProduct.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(mappedProduct.getType()));
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
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
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
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
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
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
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
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.commerce.admin.catalog.client.custom.
						field.CustomField[] customFieldsArray = new
						com.liferay.headless.commerce.admin.catalog.client.
							custom.field.CustomField
							[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.commerce.admin.catalog.client.
								custom.field.CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					mappedProduct.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setId(
						Long.valueOf((String)jsonParserFieldValue));
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
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setQuantity(
						Integer.valueOf((String)jsonParserFieldValue));
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
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					mappedProduct.setType(
						MappedProduct.Type.create(
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