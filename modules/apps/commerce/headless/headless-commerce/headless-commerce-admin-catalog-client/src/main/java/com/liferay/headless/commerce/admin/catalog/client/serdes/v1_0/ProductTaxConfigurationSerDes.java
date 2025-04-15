/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductTaxConfiguration;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

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
public class ProductTaxConfigurationSerDes {

	public static ProductTaxConfiguration toDTO(String json) {
		ProductTaxConfigurationJSONParser productTaxConfigurationJSONParser =
			new ProductTaxConfigurationJSONParser();

		return productTaxConfigurationJSONParser.parseToDTO(json);
	}

	public static ProductTaxConfiguration[] toDTOs(String json) {
		ProductTaxConfigurationJSONParser productTaxConfigurationJSONParser =
			new ProductTaxConfigurationJSONParser();

		return productTaxConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ProductTaxConfiguration productTaxConfiguration) {

		if (productTaxConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productTaxConfiguration.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(productTaxConfiguration.getId());
		}

		if (productTaxConfiguration.getTaxCategory() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxCategory\": ");

			sb.append("\"");

			sb.append(_escape(productTaxConfiguration.getTaxCategory()));

			sb.append("\"");
		}

		if (productTaxConfiguration.getTaxable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxable\": ");

			sb.append(productTaxConfiguration.getTaxable());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductTaxConfigurationJSONParser productTaxConfigurationJSONParser =
			new ProductTaxConfigurationJSONParser();

		return productTaxConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProductTaxConfiguration productTaxConfiguration) {

		if (productTaxConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productTaxConfiguration.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(productTaxConfiguration.getId()));
		}

		if (productTaxConfiguration.getTaxCategory() == null) {
			map.put("taxCategory", null);
		}
		else {
			map.put(
				"taxCategory",
				String.valueOf(productTaxConfiguration.getTaxCategory()));
		}

		if (productTaxConfiguration.getTaxable() == null) {
			map.put("taxable", null);
		}
		else {
			map.put(
				"taxable",
				String.valueOf(productTaxConfiguration.getTaxable()));
		}

		return map;
	}

	public static class ProductTaxConfigurationJSONParser
		extends BaseJSONParser<ProductTaxConfiguration> {

		@Override
		protected ProductTaxConfiguration createDTO() {
			return new ProductTaxConfiguration();
		}

		@Override
		protected ProductTaxConfiguration[] createDTOArray(int size) {
			return new ProductTaxConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "taxCategory")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "taxable")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductTaxConfiguration productTaxConfiguration,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					productTaxConfiguration.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taxCategory")) {
				if (jsonParserFieldValue != null) {
					productTaxConfiguration.setTaxCategory(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taxable")) {
				if (jsonParserFieldValue != null) {
					productTaxConfiguration.setTaxable(
						(Boolean)jsonParserFieldValue);
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