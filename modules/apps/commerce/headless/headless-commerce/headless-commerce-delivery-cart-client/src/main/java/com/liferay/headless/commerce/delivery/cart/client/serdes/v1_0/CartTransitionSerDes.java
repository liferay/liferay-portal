/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.CartTransition;
import com.liferay.headless.commerce.delivery.cart.client.json.BaseJSONParser;

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
public class CartTransitionSerDes {

	public static CartTransition toDTO(String json) {
		CartTransitionJSONParser cartTransitionJSONParser =
			new CartTransitionJSONParser();

		return cartTransitionJSONParser.parseToDTO(json);
	}

	public static CartTransition[] toDTOs(String json) {
		CartTransitionJSONParser cartTransitionJSONParser =
			new CartTransitionJSONParser();

		return cartTransitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CartTransition cartTransition) {
		if (cartTransition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (cartTransition.getCartId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cartId\": ");

			sb.append(cartTransition.getCartId());
		}

		if (cartTransition.getComment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comment\": ");

			sb.append("\"");

			sb.append(_escape(cartTransition.getComment()));

			sb.append("\"");
		}

		if (cartTransition.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(cartTransition.getLabel()));

			sb.append("\"");
		}

		if (cartTransition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(cartTransition.getName()));

			sb.append("\"");
		}

		if (cartTransition.getOpen() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"open\": ");

			sb.append(cartTransition.getOpen());
		}

		if (cartTransition.getWorkflowTaskId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowTaskId\": ");

			sb.append(cartTransition.getWorkflowTaskId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CartTransitionJSONParser cartTransitionJSONParser =
			new CartTransitionJSONParser();

		return cartTransitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CartTransition cartTransition) {
		if (cartTransition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (cartTransition.getCartId() == null) {
			map.put("cartId", null);
		}
		else {
			map.put("cartId", String.valueOf(cartTransition.getCartId()));
		}

		if (cartTransition.getComment() == null) {
			map.put("comment", null);
		}
		else {
			map.put("comment", String.valueOf(cartTransition.getComment()));
		}

		if (cartTransition.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(cartTransition.getLabel()));
		}

		if (cartTransition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(cartTransition.getName()));
		}

		if (cartTransition.getOpen() == null) {
			map.put("open", null);
		}
		else {
			map.put("open", String.valueOf(cartTransition.getOpen()));
		}

		if (cartTransition.getWorkflowTaskId() == null) {
			map.put("workflowTaskId", null);
		}
		else {
			map.put(
				"workflowTaskId",
				String.valueOf(cartTransition.getWorkflowTaskId()));
		}

		return map;
	}

	public static class CartTransitionJSONParser
		extends BaseJSONParser<CartTransition> {

		@Override
		protected CartTransition createDTO() {
			return new CartTransition();
		}

		@Override
		protected CartTransition[] createDTOArray(int size) {
			return new CartTransition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "cartId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "comment")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "open")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "workflowTaskId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CartTransition cartTransition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cartId")) {
				if (jsonParserFieldValue != null) {
					cartTransition.setCartId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "comment")) {
				if (jsonParserFieldValue != null) {
					cartTransition.setComment((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					cartTransition.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					cartTransition.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "open")) {
				if (jsonParserFieldValue != null) {
					cartTransition.setOpen((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "workflowTaskId")) {
				if (jsonParserFieldValue != null) {
					cartTransition.setWorkflowTaskId(
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