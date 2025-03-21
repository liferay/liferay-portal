/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.Sku;
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
public class SkuSerDes {

	public static Sku toDTO(String json) {
		SkuJSONParser skuJSONParser = new SkuJSONParser();

		return skuJSONParser.parseToDTO(json);
	}

	public static Sku[] toDTOs(String json) {
		SkuJSONParser skuJSONParser = new SkuJSONParser();

		return skuJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Sku sku) {
		if (sku == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (sku.getBasePrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"basePrice\": ");

			sb.append(sku.getBasePrice());
		}

		if (sku.getBasePriceFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"basePriceFormatted\": ");

			sb.append("\"");

			sb.append(_escape(sku.getBasePriceFormatted()));

			sb.append("\"");
		}

		if (sku.getBasePromoPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"basePromoPrice\": ");

			sb.append(sku.getBasePromoPrice());
		}

		if (sku.getBasePromoPriceFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"basePromoPriceFormatted\": ");

			sb.append("\"");

			sb.append(_escape(sku.getBasePromoPriceFormatted()));

			sb.append("\"");
		}

		if (sku.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(sku.getId());
		}

		if (sku.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(sku.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SkuJSONParser skuJSONParser = new SkuJSONParser();

		return skuJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Sku sku) {
		if (sku == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (sku.getBasePrice() == null) {
			map.put("basePrice", null);
		}
		else {
			map.put("basePrice", String.valueOf(sku.getBasePrice()));
		}

		if (sku.getBasePriceFormatted() == null) {
			map.put("basePriceFormatted", null);
		}
		else {
			map.put(
				"basePriceFormatted",
				String.valueOf(sku.getBasePriceFormatted()));
		}

		if (sku.getBasePromoPrice() == null) {
			map.put("basePromoPrice", null);
		}
		else {
			map.put("basePromoPrice", String.valueOf(sku.getBasePromoPrice()));
		}

		if (sku.getBasePromoPriceFormatted() == null) {
			map.put("basePromoPriceFormatted", null);
		}
		else {
			map.put(
				"basePromoPriceFormatted",
				String.valueOf(sku.getBasePromoPriceFormatted()));
		}

		if (sku.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(sku.getId()));
		}

		if (sku.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(sku.getName()));
		}

		return map;
	}

	public static class SkuJSONParser extends BaseJSONParser<Sku> {

		@Override
		protected Sku createDTO() {
			return new Sku();
		}

		@Override
		protected Sku[] createDTOArray(int size) {
			return new Sku[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "basePrice")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "basePriceFormatted")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "basePromoPrice")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "basePromoPriceFormatted")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Sku sku, String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "basePrice")) {
				if (jsonParserFieldValue != null) {
					sku.setBasePrice(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "basePriceFormatted")) {

				if (jsonParserFieldValue != null) {
					sku.setBasePriceFormatted((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "basePromoPrice")) {
				if (jsonParserFieldValue != null) {
					sku.setBasePromoPrice(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "basePromoPriceFormatted")) {

				if (jsonParserFieldValue != null) {
					sku.setBasePromoPriceFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					sku.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					sku.setName((String)jsonParserFieldValue);
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