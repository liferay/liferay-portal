/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.SkuUnitOfMeasure;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.TierPrice;
import com.liferay.headless.commerce.delivery.catalog.client.json.BaseJSONParser;

import java.math.BigDecimal;

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
public class SkuUnitOfMeasureSerDes {

	public static SkuUnitOfMeasure toDTO(String json) {
		SkuUnitOfMeasureJSONParser skuUnitOfMeasureJSONParser =
			new SkuUnitOfMeasureJSONParser();

		return skuUnitOfMeasureJSONParser.parseToDTO(json);
	}

	public static SkuUnitOfMeasure[] toDTOs(String json) {
		SkuUnitOfMeasureJSONParser skuUnitOfMeasureJSONParser =
			new SkuUnitOfMeasureJSONParser();

		return skuUnitOfMeasureJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SkuUnitOfMeasure skuUnitOfMeasure) {
		if (skuUnitOfMeasure == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (skuUnitOfMeasure.getIncrementalOrderQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"incrementalOrderQuantity\": ");

			sb.append(skuUnitOfMeasure.getIncrementalOrderQuantity());
		}

		if (skuUnitOfMeasure.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(skuUnitOfMeasure.getKey()));

			sb.append("\"");
		}

		if (skuUnitOfMeasure.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(skuUnitOfMeasure.getName()));

			sb.append("\"");
		}

		if (skuUnitOfMeasure.getPrecision() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"precision\": ");

			sb.append(skuUnitOfMeasure.getPrecision());
		}

		if (skuUnitOfMeasure.getPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(String.valueOf(skuUnitOfMeasure.getPrice()));
		}

		if (skuUnitOfMeasure.getPrimary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"primary\": ");

			sb.append(skuUnitOfMeasure.getPrimary());
		}

		if (skuUnitOfMeasure.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(skuUnitOfMeasure.getPriority());
		}

		if (skuUnitOfMeasure.getRate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rate\": ");

			sb.append(skuUnitOfMeasure.getRate());
		}

		if (skuUnitOfMeasure.getTierPrices() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tierPrices\": ");

			sb.append("[");

			for (int i = 0; i < skuUnitOfMeasure.getTierPrices().length; i++) {
				sb.append(String.valueOf(skuUnitOfMeasure.getTierPrices()[i]));

				if ((i + 1) < skuUnitOfMeasure.getTierPrices().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SkuUnitOfMeasureJSONParser skuUnitOfMeasureJSONParser =
			new SkuUnitOfMeasureJSONParser();

		return skuUnitOfMeasureJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SkuUnitOfMeasure skuUnitOfMeasure) {
		if (skuUnitOfMeasure == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (skuUnitOfMeasure.getIncrementalOrderQuantity() == null) {
			map.put("incrementalOrderQuantity", null);
		}
		else {
			map.put(
				"incrementalOrderQuantity",
				String.valueOf(skuUnitOfMeasure.getIncrementalOrderQuantity()));
		}

		if (skuUnitOfMeasure.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(skuUnitOfMeasure.getKey()));
		}

		if (skuUnitOfMeasure.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(skuUnitOfMeasure.getName()));
		}

		if (skuUnitOfMeasure.getPrecision() == null) {
			map.put("precision", null);
		}
		else {
			map.put(
				"precision", String.valueOf(skuUnitOfMeasure.getPrecision()));
		}

		if (skuUnitOfMeasure.getPrice() == null) {
			map.put("price", null);
		}
		else {
			map.put("price", String.valueOf(skuUnitOfMeasure.getPrice()));
		}

		if (skuUnitOfMeasure.getPrimary() == null) {
			map.put("primary", null);
		}
		else {
			map.put("primary", String.valueOf(skuUnitOfMeasure.getPrimary()));
		}

		if (skuUnitOfMeasure.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(skuUnitOfMeasure.getPriority()));
		}

		if (skuUnitOfMeasure.getRate() == null) {
			map.put("rate", null);
		}
		else {
			map.put("rate", String.valueOf(skuUnitOfMeasure.getRate()));
		}

		if (skuUnitOfMeasure.getTierPrices() == null) {
			map.put("tierPrices", null);
		}
		else {
			map.put(
				"tierPrices", String.valueOf(skuUnitOfMeasure.getTierPrices()));
		}

		return map;
	}

	public static class SkuUnitOfMeasureJSONParser
		extends BaseJSONParser<SkuUnitOfMeasure> {

		@Override
		protected SkuUnitOfMeasure createDTO() {
			return new SkuUnitOfMeasure();
		}

		@Override
		protected SkuUnitOfMeasure[] createDTOArray(int size) {
			return new SkuUnitOfMeasure[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "incrementalOrderQuantity")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "precision")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "rate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tierPrices")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SkuUnitOfMeasure skuUnitOfMeasure, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "incrementalOrderQuantity")) {

				if (jsonParserFieldValue != null) {
					skuUnitOfMeasure.setIncrementalOrderQuantity(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					skuUnitOfMeasure.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					skuUnitOfMeasure.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "precision")) {
				if (jsonParserFieldValue != null) {
					skuUnitOfMeasure.setPrecision(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "price")) {
				if (jsonParserFieldValue != null) {
					skuUnitOfMeasure.setPrice(
						PriceSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "primary")) {
				if (jsonParserFieldValue != null) {
					skuUnitOfMeasure.setPrimary((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					skuUnitOfMeasure.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "rate")) {
				if (jsonParserFieldValue != null) {
					skuUnitOfMeasure.setRate(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tierPrices")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					TierPrice[] tierPricesArray =
						new TierPrice[jsonParserFieldValues.length];

					for (int i = 0; i < tierPricesArray.length; i++) {
						tierPricesArray[i] = TierPriceSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					skuUnitOfMeasure.setTierPrices(tierPricesArray);
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