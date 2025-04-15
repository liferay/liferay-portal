/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import java.math.BigDecimal;

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
public class ProductOptionValueSerDes {

	public static ProductOptionValue toDTO(String json) {
		ProductOptionValueJSONParser productOptionValueJSONParser =
			new ProductOptionValueJSONParser();

		return productOptionValueJSONParser.parseToDTO(json);
	}

	public static ProductOptionValue[] toDTOs(String json) {
		ProductOptionValueJSONParser productOptionValueJSONParser =
			new ProductOptionValueJSONParser();

		return productOptionValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ProductOptionValue productOptionValue) {
		if (productOptionValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productOptionValue.getDeltaPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deltaPrice\": ");

			sb.append(productOptionValue.getDeltaPrice());
		}

		if (productOptionValue.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(productOptionValue.getId());
		}

		if (productOptionValue.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(productOptionValue.getKey()));

			sb.append("\"");
		}

		if (productOptionValue.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(productOptionValue.getName()));
		}

		if (productOptionValue.getPreselected() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"preselected\": ");

			sb.append(productOptionValue.getPreselected());
		}

		if (productOptionValue.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(productOptionValue.getPriority());
		}

		if (productOptionValue.getQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(productOptionValue.getQuantity());
		}

		if (productOptionValue.getSkuExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(productOptionValue.getSkuExternalReferenceCode()));

			sb.append("\"");
		}

		if (productOptionValue.getSkuId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(productOptionValue.getSkuId());
		}

		if (productOptionValue.getUnitOfMeasureKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasureKey\": ");

			sb.append("\"");

			sb.append(_escape(productOptionValue.getUnitOfMeasureKey()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductOptionValueJSONParser productOptionValueJSONParser =
			new ProductOptionValueJSONParser();

		return productOptionValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProductOptionValue productOptionValue) {

		if (productOptionValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productOptionValue.getDeltaPrice() == null) {
			map.put("deltaPrice", null);
		}
		else {
			map.put(
				"deltaPrice",
				String.valueOf(productOptionValue.getDeltaPrice()));
		}

		if (productOptionValue.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(productOptionValue.getId()));
		}

		if (productOptionValue.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(productOptionValue.getKey()));
		}

		if (productOptionValue.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(productOptionValue.getName()));
		}

		if (productOptionValue.getPreselected() == null) {
			map.put("preselected", null);
		}
		else {
			map.put(
				"preselected",
				String.valueOf(productOptionValue.getPreselected()));
		}

		if (productOptionValue.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority", String.valueOf(productOptionValue.getPriority()));
		}

		if (productOptionValue.getQuantity() == null) {
			map.put("quantity", null);
		}
		else {
			map.put(
				"quantity", String.valueOf(productOptionValue.getQuantity()));
		}

		if (productOptionValue.getSkuExternalReferenceCode() == null) {
			map.put("skuExternalReferenceCode", null);
		}
		else {
			map.put(
				"skuExternalReferenceCode",
				String.valueOf(
					productOptionValue.getSkuExternalReferenceCode()));
		}

		if (productOptionValue.getSkuId() == null) {
			map.put("skuId", null);
		}
		else {
			map.put("skuId", String.valueOf(productOptionValue.getSkuId()));
		}

		if (productOptionValue.getUnitOfMeasureKey() == null) {
			map.put("unitOfMeasureKey", null);
		}
		else {
			map.put(
				"unitOfMeasureKey",
				String.valueOf(productOptionValue.getUnitOfMeasureKey()));
		}

		return map;
	}

	public static class ProductOptionValueJSONParser
		extends BaseJSONParser<ProductOptionValue> {

		@Override
		protected ProductOptionValue createDTO() {
			return new ProductOptionValue();
		}

		@Override
		protected ProductOptionValue[] createDTOArray(int size) {
			return new ProductOptionValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "deltaPrice")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "preselected")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "skuExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasureKey")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductOptionValue productOptionValue, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "deltaPrice")) {
				if (jsonParserFieldValue != null) {
					productOptionValue.setDeltaPrice(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					productOptionValue.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					productOptionValue.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					productOptionValue.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "preselected")) {
				if (jsonParserFieldValue != null) {
					productOptionValue.setPreselected(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					productOptionValue.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				if (jsonParserFieldValue != null) {
					productOptionValue.setQuantity(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "skuExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productOptionValue.setSkuExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				if (jsonParserFieldValue != null) {
					productOptionValue.setSkuId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasureKey")) {
				if (jsonParserFieldValue != null) {
					productOptionValue.setUnitOfMeasureKey(
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