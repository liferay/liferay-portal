/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderNote;
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
public class OrderNoteSerDes {

	public static OrderNote toDTO(String json) {
		OrderNoteJSONParser orderNoteJSONParser = new OrderNoteJSONParser();

		return orderNoteJSONParser.parseToDTO(json);
	}

	public static OrderNote[] toDTOs(String json) {
		OrderNoteJSONParser orderNoteJSONParser = new OrderNoteJSONParser();

		return orderNoteJSONParser.parseToDTOs(json);
	}

	public static String toJSON(OrderNote orderNote) {
		if (orderNote == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (orderNote.getAuthor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"author\": ");

			sb.append("\"");

			sb.append(_escape(orderNote.getAuthor()));

			sb.append("\"");
		}

		if (orderNote.getContent() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"content\": ");

			sb.append("\"");

			sb.append(_escape(orderNote.getContent()));

			sb.append("\"");
		}

		if (orderNote.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderNote.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderNote.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(orderNote.getId());
		}

		if (orderNote.getOrderExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(orderNote.getOrderExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderNote.getOrderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderId\": ");

			sb.append(orderNote.getOrderId());
		}

		if (orderNote.getRestricted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"restricted\": ");

			sb.append(orderNote.getRestricted());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrderNoteJSONParser orderNoteJSONParser = new OrderNoteJSONParser();

		return orderNoteJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(OrderNote orderNote) {
		if (orderNote == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (orderNote.getAuthor() == null) {
			map.put("author", null);
		}
		else {
			map.put("author", String.valueOf(orderNote.getAuthor()));
		}

		if (orderNote.getContent() == null) {
			map.put("content", null);
		}
		else {
			map.put("content", String.valueOf(orderNote.getContent()));
		}

		if (orderNote.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(orderNote.getExternalReferenceCode()));
		}

		if (orderNote.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(orderNote.getId()));
		}

		if (orderNote.getOrderExternalReferenceCode() == null) {
			map.put("orderExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderExternalReferenceCode",
				String.valueOf(orderNote.getOrderExternalReferenceCode()));
		}

		if (orderNote.getOrderId() == null) {
			map.put("orderId", null);
		}
		else {
			map.put("orderId", String.valueOf(orderNote.getOrderId()));
		}

		if (orderNote.getRestricted() == null) {
			map.put("restricted", null);
		}
		else {
			map.put("restricted", String.valueOf(orderNote.getRestricted()));
		}

		return map;
	}

	public static class OrderNoteJSONParser extends BaseJSONParser<OrderNote> {

		@Override
		protected OrderNote createDTO() {
			return new OrderNote();
		}

		@Override
		protected OrderNote[] createDTOArray(int size) {
			return new OrderNote[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "author")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "content")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "restricted")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OrderNote orderNote, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "author")) {
				if (jsonParserFieldValue != null) {
					orderNote.setAuthor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "content")) {
				if (jsonParserFieldValue != null) {
					orderNote.setContent((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderNote.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					orderNote.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderNote.setOrderExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				if (jsonParserFieldValue != null) {
					orderNote.setOrderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "restricted")) {
				if (jsonParserFieldValue != null) {
					orderNote.setRestricted((Boolean)jsonParserFieldValue);
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