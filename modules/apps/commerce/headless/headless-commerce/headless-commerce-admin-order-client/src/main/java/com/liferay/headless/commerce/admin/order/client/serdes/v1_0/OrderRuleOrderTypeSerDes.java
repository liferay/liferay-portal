/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderRuleOrderType;
import com.liferay.headless.commerce.admin.order.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class OrderRuleOrderTypeSerDes {

	public static OrderRuleOrderType toDTO(String json) {
		OrderRuleOrderTypeJSONParser orderRuleOrderTypeJSONParser =
			new OrderRuleOrderTypeJSONParser();

		return orderRuleOrderTypeJSONParser.parseToDTO(json);
	}

	public static OrderRuleOrderType[] toDTOs(String json) {
		OrderRuleOrderTypeJSONParser orderRuleOrderTypeJSONParser =
			new OrderRuleOrderTypeJSONParser();

		return orderRuleOrderTypeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(OrderRuleOrderType orderRuleOrderType) {
		if (orderRuleOrderType == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (orderRuleOrderType.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(orderRuleOrderType.getActions()));
		}

		if (orderRuleOrderType.getOrderRuleExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderRuleExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					orderRuleOrderType.getOrderRuleExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderRuleOrderType.getOrderRuleId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderRuleId\": ");

			sb.append(orderRuleOrderType.getOrderRuleId());
		}

		if (orderRuleOrderType.getOrderRuleOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderRuleOrderTypeId\": ");

			sb.append(orderRuleOrderType.getOrderRuleOrderTypeId());
		}

		if (orderRuleOrderType.getOrderType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderType\": ");

			sb.append(String.valueOf(orderRuleOrderType.getOrderType()));
		}

		if (orderRuleOrderType.getOrderTypeExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					orderRuleOrderType.getOrderTypeExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderRuleOrderType.getOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(orderRuleOrderType.getOrderTypeId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrderRuleOrderTypeJSONParser orderRuleOrderTypeJSONParser =
			new OrderRuleOrderTypeJSONParser();

		return orderRuleOrderTypeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		OrderRuleOrderType orderRuleOrderType) {

		if (orderRuleOrderType == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (orderRuleOrderType.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(orderRuleOrderType.getActions()));
		}

		if (orderRuleOrderType.getOrderRuleExternalReferenceCode() == null) {
			map.put("orderRuleExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderRuleExternalReferenceCode",
				String.valueOf(
					orderRuleOrderType.getOrderRuleExternalReferenceCode()));
		}

		if (orderRuleOrderType.getOrderRuleId() == null) {
			map.put("orderRuleId", null);
		}
		else {
			map.put(
				"orderRuleId",
				String.valueOf(orderRuleOrderType.getOrderRuleId()));
		}

		if (orderRuleOrderType.getOrderRuleOrderTypeId() == null) {
			map.put("orderRuleOrderTypeId", null);
		}
		else {
			map.put(
				"orderRuleOrderTypeId",
				String.valueOf(orderRuleOrderType.getOrderRuleOrderTypeId()));
		}

		if (orderRuleOrderType.getOrderType() == null) {
			map.put("orderType", null);
		}
		else {
			map.put(
				"orderType", String.valueOf(orderRuleOrderType.getOrderType()));
		}

		if (orderRuleOrderType.getOrderTypeExternalReferenceCode() == null) {
			map.put("orderTypeExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderTypeExternalReferenceCode",
				String.valueOf(
					orderRuleOrderType.getOrderTypeExternalReferenceCode()));
		}

		if (orderRuleOrderType.getOrderTypeId() == null) {
			map.put("orderTypeId", null);
		}
		else {
			map.put(
				"orderTypeId",
				String.valueOf(orderRuleOrderType.getOrderTypeId()));
		}

		return map;
	}

	public static class OrderRuleOrderTypeJSONParser
		extends BaseJSONParser<OrderRuleOrderType> {

		@Override
		protected OrderRuleOrderType createDTO() {
			return new OrderRuleOrderType();
		}

		@Override
		protected OrderRuleOrderType[] createDTOArray(int size) {
			return new OrderRuleOrderType[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderRuleExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderRuleId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderRuleOrderTypeId")) {

				return false;
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

			return false;
		}

		@Override
		protected void setField(
			OrderRuleOrderType orderRuleOrderType, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					orderRuleOrderType.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderRuleExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderRuleOrderType.setOrderRuleExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderRuleId")) {
				if (jsonParserFieldValue != null) {
					orderRuleOrderType.setOrderRuleId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderRuleOrderTypeId")) {

				if (jsonParserFieldValue != null) {
					orderRuleOrderType.setOrderRuleOrderTypeId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderType")) {
				if (jsonParserFieldValue != null) {
					orderRuleOrderType.setOrderType(
						OrderTypeSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderRuleOrderType.setOrderTypeExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				if (jsonParserFieldValue != null) {
					orderRuleOrderType.setOrderTypeId(
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