/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.OrderTransition;
import com.liferay.headless.commerce.delivery.order.client.json.BaseJSONParser;

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
public class OrderTransitionSerDes {

	public static OrderTransition toDTO(String json) {
		OrderTransitionJSONParser orderTransitionJSONParser =
			new OrderTransitionJSONParser();

		return orderTransitionJSONParser.parseToDTO(json);
	}

	public static OrderTransition[] toDTOs(String json) {
		OrderTransitionJSONParser orderTransitionJSONParser =
			new OrderTransitionJSONParser();

		return orderTransitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(OrderTransition orderTransition) {
		if (orderTransition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (orderTransition.getComment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comment\": ");

			sb.append("\"");

			sb.append(_escape(orderTransition.getComment()));

			sb.append("\"");
		}

		if (orderTransition.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(orderTransition.getLabel()));

			sb.append("\"");
		}

		if (orderTransition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(orderTransition.getName()));

			sb.append("\"");
		}

		if (orderTransition.getOrderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderId\": ");

			sb.append(orderTransition.getOrderId());
		}

		if (orderTransition.getPlacedOrderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"placedOrderId\": ");

			sb.append(orderTransition.getPlacedOrderId());
		}

		if (orderTransition.getWorkflowTaskId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowTaskId\": ");

			sb.append(orderTransition.getWorkflowTaskId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrderTransitionJSONParser orderTransitionJSONParser =
			new OrderTransitionJSONParser();

		return orderTransitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(OrderTransition orderTransition) {
		if (orderTransition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (orderTransition.getComment() == null) {
			map.put("comment", null);
		}
		else {
			map.put("comment", String.valueOf(orderTransition.getComment()));
		}

		if (orderTransition.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(orderTransition.getLabel()));
		}

		if (orderTransition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(orderTransition.getName()));
		}

		if (orderTransition.getOrderId() == null) {
			map.put("orderId", null);
		}
		else {
			map.put("orderId", String.valueOf(orderTransition.getOrderId()));
		}

		if (orderTransition.getPlacedOrderId() == null) {
			map.put("placedOrderId", null);
		}
		else {
			map.put(
				"placedOrderId",
				String.valueOf(orderTransition.getPlacedOrderId()));
		}

		if (orderTransition.getWorkflowTaskId() == null) {
			map.put("workflowTaskId", null);
		}
		else {
			map.put(
				"workflowTaskId",
				String.valueOf(orderTransition.getWorkflowTaskId()));
		}

		return map;
	}

	public static class OrderTransitionJSONParser
		extends BaseJSONParser<OrderTransition> {

		@Override
		protected OrderTransition createDTO() {
			return new OrderTransition();
		}

		@Override
		protected OrderTransition[] createDTOArray(int size) {
			return new OrderTransition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "comment")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "placedOrderId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "workflowTaskId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OrderTransition orderTransition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "comment")) {
				if (jsonParserFieldValue != null) {
					orderTransition.setComment((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					orderTransition.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					orderTransition.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				if (jsonParserFieldValue != null) {
					orderTransition.setOrderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "placedOrderId")) {
				if (jsonParserFieldValue != null) {
					orderTransition.setPlacedOrderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "workflowTaskId")) {
				if (jsonParserFieldValue != null) {
					orderTransition.setWorkflowTaskId(
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