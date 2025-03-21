/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.client.serdes.v1_0;

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
public class OrderItemSerDes {

	public static OrderItem toDTO(String json) {
		OrderItemJSONParser orderItemJSONParser = new OrderItemJSONParser();

		return orderItemJSONParser.parseToDTO(json);
	}

	public static OrderItem[] toDTOs(String json) {
		OrderItemJSONParser orderItemJSONParser = new OrderItemJSONParser();

		return orderItemJSONParser.parseToDTOs(json);
	}

	public static String toJSON(OrderItem orderItem) {
		if (orderItem == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (orderItem.getCpDefinitionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cpDefinitionId\": ");

			sb.append(orderItem.getCpDefinitionId());
		}

		if (orderItem.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(orderItem.getCreateDate()));

			sb.append("\"");
		}

		if (orderItem.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(orderItem.getCustomFields()));
		}

		if (orderItem.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderItem.getFinalPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"finalPrice\": ");

			sb.append(orderItem.getFinalPrice());
		}

		if (orderItem.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(orderItem.getId());
		}

		if (orderItem.getModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(orderItem.getModifiedDate()));

			sb.append("\"");
		}

		if (orderItem.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(orderItem.getName()));
		}

		if (orderItem.getOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getOptions()));

			sb.append("\"");
		}

		if (orderItem.getOrderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderId\": ");

			sb.append(orderItem.getOrderId());
		}

		if (orderItem.getParentOrderItemId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentOrderItemId\": ");

			sb.append(orderItem.getParentOrderItemId());
		}

		if (orderItem.getQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(orderItem.getQuantity());
		}

		if (orderItem.getSku() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getSku()));

			sb.append("\"");
		}

		if (orderItem.getSubscription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscription\": ");

			sb.append(orderItem.getSubscription());
		}

		if (orderItem.getUnitOfMeasure() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitOfMeasure\": ");

			sb.append("\"");

			sb.append(_escape(orderItem.getUnitOfMeasure()));

			sb.append("\"");
		}

		if (orderItem.getUnitPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitPrice\": ");

			sb.append(orderItem.getUnitPrice());
		}

		if (orderItem.getUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(orderItem.getUserId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrderItemJSONParser orderItemJSONParser = new OrderItemJSONParser();

		return orderItemJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(OrderItem orderItem) {
		if (orderItem == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (orderItem.getCpDefinitionId() == null) {
			map.put("cpDefinitionId", null);
		}
		else {
			map.put(
				"cpDefinitionId",
				String.valueOf(orderItem.getCpDefinitionId()));
		}

		if (orderItem.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(orderItem.getCreateDate()));
		}

		if (orderItem.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields", String.valueOf(orderItem.getCustomFields()));
		}

		if (orderItem.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(orderItem.getExternalReferenceCode()));
		}

		if (orderItem.getFinalPrice() == null) {
			map.put("finalPrice", null);
		}
		else {
			map.put("finalPrice", String.valueOf(orderItem.getFinalPrice()));
		}

		if (orderItem.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(orderItem.getId()));
		}

		if (orderItem.getModifiedDate() == null) {
			map.put("modifiedDate", null);
		}
		else {
			map.put(
				"modifiedDate",
				liferayToJSONDateFormat.format(orderItem.getModifiedDate()));
		}

		if (orderItem.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(orderItem.getName()));
		}

		if (orderItem.getOptions() == null) {
			map.put("options", null);
		}
		else {
			map.put("options", String.valueOf(orderItem.getOptions()));
		}

		if (orderItem.getOrderId() == null) {
			map.put("orderId", null);
		}
		else {
			map.put("orderId", String.valueOf(orderItem.getOrderId()));
		}

		if (orderItem.getParentOrderItemId() == null) {
			map.put("parentOrderItemId", null);
		}
		else {
			map.put(
				"parentOrderItemId",
				String.valueOf(orderItem.getParentOrderItemId()));
		}

		if (orderItem.getQuantity() == null) {
			map.put("quantity", null);
		}
		else {
			map.put("quantity", String.valueOf(orderItem.getQuantity()));
		}

		if (orderItem.getSku() == null) {
			map.put("sku", null);
		}
		else {
			map.put("sku", String.valueOf(orderItem.getSku()));
		}

		if (orderItem.getSubscription() == null) {
			map.put("subscription", null);
		}
		else {
			map.put(
				"subscription", String.valueOf(orderItem.getSubscription()));
		}

		if (orderItem.getUnitOfMeasure() == null) {
			map.put("unitOfMeasure", null);
		}
		else {
			map.put(
				"unitOfMeasure", String.valueOf(orderItem.getUnitOfMeasure()));
		}

		if (orderItem.getUnitPrice() == null) {
			map.put("unitPrice", null);
		}
		else {
			map.put("unitPrice", String.valueOf(orderItem.getUnitPrice()));
		}

		if (orderItem.getUserId() == null) {
			map.put("userId", null);
		}
		else {
			map.put("userId", String.valueOf(orderItem.getUserId()));
		}

		return map;
	}

	public static class OrderItemJSONParser extends BaseJSONParser<OrderItem> {

		@Override
		protected OrderItem createDTO() {
			return new OrderItem();
		}

		@Override
		protected OrderItem[] createDTOArray(int size) {
			return new OrderItem[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "cpDefinitionId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "finalPrice")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parentOrderItemId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subscription")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasure")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unitPrice")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OrderItem orderItem, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cpDefinitionId")) {
				if (jsonParserFieldValue != null) {
					orderItem.setCpDefinitionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					orderItem.setCreateDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					orderItem.setCustomFields(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderItem.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "finalPrice")) {
				if (jsonParserFieldValue != null) {
					orderItem.setFinalPrice(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					orderItem.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				if (jsonParserFieldValue != null) {
					orderItem.setModifiedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					orderItem.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				if (jsonParserFieldValue != null) {
					orderItem.setOptions((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				if (jsonParserFieldValue != null) {
					orderItem.setOrderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentOrderItemId")) {
				if (jsonParserFieldValue != null) {
					orderItem.setParentOrderItemId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "quantity")) {
				if (jsonParserFieldValue != null) {
					orderItem.setQuantity(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sku")) {
				if (jsonParserFieldValue != null) {
					orderItem.setSku((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subscription")) {
				if (jsonParserFieldValue != null) {
					orderItem.setSubscription((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unitOfMeasure")) {
				if (jsonParserFieldValue != null) {
					orderItem.setUnitOfMeasure((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unitPrice")) {
				if (jsonParserFieldValue != null) {
					orderItem.setUnitPrice(
						new BigDecimal((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				if (jsonParserFieldValue != null) {
					orderItem.setUserId(
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