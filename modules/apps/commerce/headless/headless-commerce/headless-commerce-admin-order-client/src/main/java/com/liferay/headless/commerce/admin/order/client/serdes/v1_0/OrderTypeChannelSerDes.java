/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderTypeChannel;
import com.liferay.headless.commerce.admin.order.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class OrderTypeChannelSerDes {

	public static OrderTypeChannel toDTO(String json) {
		OrderTypeChannelJSONParser orderTypeChannelJSONParser =
			new OrderTypeChannelJSONParser();

		return orderTypeChannelJSONParser.parseToDTO(json);
	}

	public static OrderTypeChannel[] toDTOs(String json) {
		OrderTypeChannelJSONParser orderTypeChannelJSONParser =
			new OrderTypeChannelJSONParser();

		return orderTypeChannelJSONParser.parseToDTOs(json);
	}

	public static String toJSON(OrderTypeChannel orderTypeChannel) {
		if (orderTypeChannel == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (orderTypeChannel.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(orderTypeChannel.getActions()));
		}

		if (orderTypeChannel.getChannel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channel\": ");

			sb.append(String.valueOf(orderTypeChannel.getChannel()));
		}

		if (orderTypeChannel.getChannelExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(orderTypeChannel.getChannelExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderTypeChannel.getChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append(orderTypeChannel.getChannelId());
		}

		if (orderTypeChannel.getOrderTypeChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeChannelId\": ");

			sb.append(orderTypeChannel.getOrderTypeChannelId());
		}

		if (orderTypeChannel.getOrderTypeExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(orderTypeChannel.getOrderTypeExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderTypeChannel.getOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(orderTypeChannel.getOrderTypeId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrderTypeChannelJSONParser orderTypeChannelJSONParser =
			new OrderTypeChannelJSONParser();

		return orderTypeChannelJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(OrderTypeChannel orderTypeChannel) {
		if (orderTypeChannel == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (orderTypeChannel.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(orderTypeChannel.getActions()));
		}

		if (orderTypeChannel.getChannel() == null) {
			map.put("channel", null);
		}
		else {
			map.put("channel", String.valueOf(orderTypeChannel.getChannel()));
		}

		if (orderTypeChannel.getChannelExternalReferenceCode() == null) {
			map.put("channelExternalReferenceCode", null);
		}
		else {
			map.put(
				"channelExternalReferenceCode",
				String.valueOf(
					orderTypeChannel.getChannelExternalReferenceCode()));
		}

		if (orderTypeChannel.getChannelId() == null) {
			map.put("channelId", null);
		}
		else {
			map.put(
				"channelId", String.valueOf(orderTypeChannel.getChannelId()));
		}

		if (orderTypeChannel.getOrderTypeChannelId() == null) {
			map.put("orderTypeChannelId", null);
		}
		else {
			map.put(
				"orderTypeChannelId",
				String.valueOf(orderTypeChannel.getOrderTypeChannelId()));
		}

		if (orderTypeChannel.getOrderTypeExternalReferenceCode() == null) {
			map.put("orderTypeExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderTypeExternalReferenceCode",
				String.valueOf(
					orderTypeChannel.getOrderTypeExternalReferenceCode()));
		}

		if (orderTypeChannel.getOrderTypeId() == null) {
			map.put("orderTypeId", null);
		}
		else {
			map.put(
				"orderTypeId",
				String.valueOf(orderTypeChannel.getOrderTypeId()));
		}

		return map;
	}

	public static class OrderTypeChannelJSONParser
		extends BaseJSONParser<OrderTypeChannel> {

		@Override
		protected OrderTypeChannel createDTO() {
			return new OrderTypeChannel();
		}

		@Override
		protected OrderTypeChannel[] createDTOArray(int size) {
			return new OrderTypeChannel[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "channel")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "channelExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderTypeChannelId")) {

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
			OrderTypeChannel orderTypeChannel, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					orderTypeChannel.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channel")) {
				if (jsonParserFieldValue != null) {
					orderTypeChannel.setChannel(
						ChannelSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "channelExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderTypeChannel.setChannelExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				if (jsonParserFieldValue != null) {
					orderTypeChannel.setChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderTypeChannelId")) {

				if (jsonParserFieldValue != null) {
					orderTypeChannel.setOrderTypeChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderTypeChannel.setOrderTypeExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				if (jsonParserFieldValue != null) {
					orderTypeChannel.setOrderTypeId(
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