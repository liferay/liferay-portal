/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationListOrderType;
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
public class ProductConfigurationListOrderTypeSerDes {

	public static ProductConfigurationListOrderType toDTO(String json) {
		ProductConfigurationListOrderTypeJSONParser
			productConfigurationListOrderTypeJSONParser =
				new ProductConfigurationListOrderTypeJSONParser();

		return productConfigurationListOrderTypeJSONParser.parseToDTO(json);
	}

	public static ProductConfigurationListOrderType[] toDTOs(String json) {
		ProductConfigurationListOrderTypeJSONParser
			productConfigurationListOrderTypeJSONParser =
				new ProductConfigurationListOrderTypeJSONParser();

		return productConfigurationListOrderTypeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ProductConfigurationListOrderType productConfigurationListOrderType) {

		if (productConfigurationListOrderType == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productConfigurationListOrderType.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(productConfigurationListOrderType.getActions()));
		}

		if (productConfigurationListOrderType.getOrderType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderType\": ");

			sb.append(
				String.valueOf(
					productConfigurationListOrderType.getOrderType()));
		}

		if (productConfigurationListOrderType.
				getOrderTypeExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					productConfigurationListOrderType.
						getOrderTypeExternalReferenceCode()));

			sb.append("\"");
		}

		if (productConfigurationListOrderType.getOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(productConfigurationListOrderType.getOrderTypeId());
		}

		if (productConfigurationListOrderType.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(productConfigurationListOrderType.getPriority());
		}

		if (productConfigurationListOrderType.
				getProductConfigurationListExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					productConfigurationListOrderType.
						getProductConfigurationListExternalReferenceCode()));

			sb.append("\"");
		}

		if (productConfigurationListOrderType.getProductConfigurationListId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListId\": ");

			sb.append(
				productConfigurationListOrderType.
					getProductConfigurationListId());
		}

		if (productConfigurationListOrderType.
				getProductConfigurationListOrderTypeId() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListOrderTypeId\": ");

			sb.append(
				productConfigurationListOrderType.
					getProductConfigurationListOrderTypeId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductConfigurationListOrderTypeJSONParser
			productConfigurationListOrderTypeJSONParser =
				new ProductConfigurationListOrderTypeJSONParser();

		return productConfigurationListOrderTypeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProductConfigurationListOrderType productConfigurationListOrderType) {

		if (productConfigurationListOrderType == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productConfigurationListOrderType.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(productConfigurationListOrderType.getActions()));
		}

		if (productConfigurationListOrderType.getOrderType() == null) {
			map.put("orderType", null);
		}
		else {
			map.put(
				"orderType",
				String.valueOf(
					productConfigurationListOrderType.getOrderType()));
		}

		if (productConfigurationListOrderType.
				getOrderTypeExternalReferenceCode() == null) {

			map.put("orderTypeExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderTypeExternalReferenceCode",
				String.valueOf(
					productConfigurationListOrderType.
						getOrderTypeExternalReferenceCode()));
		}

		if (productConfigurationListOrderType.getOrderTypeId() == null) {
			map.put("orderTypeId", null);
		}
		else {
			map.put(
				"orderTypeId",
				String.valueOf(
					productConfigurationListOrderType.getOrderTypeId()));
		}

		if (productConfigurationListOrderType.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority",
				String.valueOf(
					productConfigurationListOrderType.getPriority()));
		}

		if (productConfigurationListOrderType.
				getProductConfigurationListExternalReferenceCode() == null) {

			map.put("productConfigurationListExternalReferenceCode", null);
		}
		else {
			map.put(
				"productConfigurationListExternalReferenceCode",
				String.valueOf(
					productConfigurationListOrderType.
						getProductConfigurationListExternalReferenceCode()));
		}

		if (productConfigurationListOrderType.getProductConfigurationListId() ==
				null) {

			map.put("productConfigurationListId", null);
		}
		else {
			map.put(
				"productConfigurationListId",
				String.valueOf(
					productConfigurationListOrderType.
						getProductConfigurationListId()));
		}

		if (productConfigurationListOrderType.
				getProductConfigurationListOrderTypeId() == null) {

			map.put("productConfigurationListOrderTypeId", null);
		}
		else {
			map.put(
				"productConfigurationListOrderTypeId",
				String.valueOf(
					productConfigurationListOrderType.
						getProductConfigurationListOrderTypeId()));
		}

		return map;
	}

	public static class ProductConfigurationListOrderTypeJSONParser
		extends BaseJSONParser<ProductConfigurationListOrderType> {

		@Override
		protected ProductConfigurationListOrderType createDTO() {
			return new ProductConfigurationListOrderType();
		}

		@Override
		protected ProductConfigurationListOrderType[] createDTOArray(int size) {
			return new ProductConfigurationListOrderType[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "orderType")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfigurationListId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListOrderTypeId")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductConfigurationListOrderType productConfigurationListOrderType,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListOrderType.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderType")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListOrderType.setOrderType(
						OrderTypeSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListOrderType.
						setOrderTypeExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListOrderType.setOrderTypeId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListOrderType.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListOrderType.
						setProductConfigurationListExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfigurationListId")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListOrderType.
						setProductConfigurationListId(
							Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListOrderTypeId")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListOrderType.
						setProductConfigurationListOrderTypeId(
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