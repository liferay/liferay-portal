/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.client.serdes.v1_0;

import com.liferay.headless.commerce.machine.learning.client.dto.v1_0.Order;
import com.liferay.headless.commerce.machine.learning.client.dto.v1_0.OrderItem;
import com.liferay.headless.commerce.machine.learning.client.json.BaseJSONParser;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class OrderSerDes {

	public static Order toDTO(String json) {
		OrderJSONParser orderJSONParser = new OrderJSONParser();

		return orderJSONParser.parseToDTO(json);
	}

	public static Order[] toDTOs(String json) {
		OrderJSONParser orderJSONParser = new OrderJSONParser();

		return orderJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Order order) {
		if (order == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (order.getAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(order.getAccountId());
		}

		if (order.getChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append(order.getChannelId());
		}

		if (order.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(order.getCreateDate()));

			sb.append("\"");
		}

		if (order.getCurrencyCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getCurrencyCode()));

			sb.append("\"");
		}

		if (order.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(order.getCustomFields()));
		}

		if (order.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (order.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(order.getId());
		}

		if (order.getModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(order.getModifiedDate()));

			sb.append("\"");
		}

		if (order.getOrderDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(order.getOrderDate()));

			sb.append("\"");
		}

		if (order.getOrderItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderItems\": ");

			sb.append("[");

			for (int i = 0; i < order.getOrderItems().length; i++) {
				sb.append(String.valueOf(order.getOrderItems()[i]));

				if ((i + 1) < order.getOrderItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (order.getOrderStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderStatus\": ");

			sb.append(order.getOrderStatus());
		}

		if (order.getOrderTypeExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(order.getOrderTypeExternalReferenceCode()));

			sb.append("\"");
		}

		if (order.getOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(order.getOrderTypeId());
		}

		if (order.getPaymentMethod() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentMethod\": ");

			sb.append("\"");

			sb.append(_escape(order.getPaymentMethod()));

			sb.append("\"");
		}

		if (order.getPaymentStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentStatus\": ");

			sb.append(order.getPaymentStatus());
		}

		if (order.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(order.getStatus());
		}

		if (order.getTotal() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(order.getTotal());
		}

		if (order.getUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(order.getUserId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrderJSONParser orderJSONParser = new OrderJSONParser();

		return orderJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Order order) {
		if (order == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (order.getAccountId() == null) {
			map.put("accountId", null);
		}
		else {
			map.put("accountId", String.valueOf(order.getAccountId()));
		}

		if (order.getChannelId() == null) {
			map.put("channelId", null);
		}
		else {
			map.put("channelId", String.valueOf(order.getChannelId()));
		}

		if (order.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(order.getCreateDate()));
		}

		if (order.getCurrencyCode() == null) {
			map.put("currencyCode", null);
		}
		else {
			map.put("currencyCode", String.valueOf(order.getCurrencyCode()));
		}

		if (order.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put("customFields", String.valueOf(order.getCustomFields()));
		}

		if (order.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(order.getExternalReferenceCode()));
		}

		if (order.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(order.getId()));
		}

		if (order.getModifiedDate() == null) {
			map.put("modifiedDate", null);
		}
		else {
			map.put(
				"modifiedDate",
				liferayToJSONDateFormat.format(order.getModifiedDate()));
		}

		if (order.getOrderDate() == null) {
			map.put("orderDate", null);
		}
		else {
			map.put(
				"orderDate",
				liferayToJSONDateFormat.format(order.getOrderDate()));
		}

		if (order.getOrderItems() == null) {
			map.put("orderItems", null);
		}
		else {
			map.put("orderItems", String.valueOf(order.getOrderItems()));
		}

		if (order.getOrderStatus() == null) {
			map.put("orderStatus", null);
		}
		else {
			map.put("orderStatus", String.valueOf(order.getOrderStatus()));
		}

		if (order.getOrderTypeExternalReferenceCode() == null) {
			map.put("orderTypeExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderTypeExternalReferenceCode",
				String.valueOf(order.getOrderTypeExternalReferenceCode()));
		}

		if (order.getOrderTypeId() == null) {
			map.put("orderTypeId", null);
		}
		else {
			map.put("orderTypeId", String.valueOf(order.getOrderTypeId()));
		}

		if (order.getPaymentMethod() == null) {
			map.put("paymentMethod", null);
		}
		else {
			map.put("paymentMethod", String.valueOf(order.getPaymentMethod()));
		}

		if (order.getPaymentStatus() == null) {
			map.put("paymentStatus", null);
		}
		else {
			map.put("paymentStatus", String.valueOf(order.getPaymentStatus()));
		}

		if (order.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(order.getStatus()));
		}

		if (order.getTotal() == null) {
			map.put("total", null);
		}
		else {
			map.put("total", String.valueOf(order.getTotal()));
		}

		if (order.getUserId() == null) {
			map.put("userId", null);
		}
		else {
			map.put("userId", String.valueOf(order.getUserId()));
		}

		return map;
	}

	public static class OrderJSONParser extends BaseJSONParser<Order> {

		@Override
		protected Order createDTO() {
			return new Order();
		}

		@Override
		protected Order[] createDTOArray(int size) {
			return new Order[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderItems")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderStatus")) {
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
			else if (Objects.equals(jsonParserFieldName, "paymentMethod")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatus")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Order order, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountId")) {
				if (jsonParserFieldValue != null) {
					order.setAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				if (jsonParserFieldValue != null) {
					order.setChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					order.setCreateDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				if (jsonParserFieldValue != null) {
					order.setCurrencyCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					order.setCustomFields((Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					order.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					order.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				if (jsonParserFieldValue != null) {
					order.setModifiedDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderDate")) {
				if (jsonParserFieldValue != null) {
					order.setOrderDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderItems")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					OrderItem[] orderItemsArray =
						new OrderItem[jsonParserFieldValues.length];

					for (int i = 0; i < orderItemsArray.length; i++) {
						orderItemsArray[i] = OrderItemSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					order.setOrderItems(orderItemsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderStatus")) {
				if (jsonParserFieldValue != null) {
					order.setOrderStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					order.setOrderTypeExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				if (jsonParserFieldValue != null) {
					order.setOrderTypeId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paymentMethod")) {
				if (jsonParserFieldValue != null) {
					order.setPaymentMethod((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paymentStatus")) {
				if (jsonParserFieldValue != null) {
					order.setPaymentStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					order.setStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				if (jsonParserFieldValue != null) {
					order.setTotal(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				if (jsonParserFieldValue != null) {
					order.setUserId(Long.valueOf((String)jsonParserFieldValue));
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