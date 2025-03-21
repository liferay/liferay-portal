/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.ShippingFixedOptionOrderType;
import com.liferay.headless.commerce.admin.channel.client.json.BaseJSONParser;

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
public class ShippingFixedOptionOrderTypeSerDes {

	public static ShippingFixedOptionOrderType toDTO(String json) {
		ShippingFixedOptionOrderTypeJSONParser
			shippingFixedOptionOrderTypeJSONParser =
				new ShippingFixedOptionOrderTypeJSONParser();

		return shippingFixedOptionOrderTypeJSONParser.parseToDTO(json);
	}

	public static ShippingFixedOptionOrderType[] toDTOs(String json) {
		ShippingFixedOptionOrderTypeJSONParser
			shippingFixedOptionOrderTypeJSONParser =
				new ShippingFixedOptionOrderTypeJSONParser();

		return shippingFixedOptionOrderTypeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ShippingFixedOptionOrderType shippingFixedOptionOrderType) {

		if (shippingFixedOptionOrderType == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (shippingFixedOptionOrderType.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(shippingFixedOptionOrderType.getActions()));
		}

		if (shippingFixedOptionOrderType.getOrderType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderType\": ");

			sb.append(
				String.valueOf(shippingFixedOptionOrderType.getOrderType()));
		}

		if (shippingFixedOptionOrderType.getOrderTypeExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					shippingFixedOptionOrderType.
						getOrderTypeExternalReferenceCode()));

			sb.append("\"");
		}

		if (shippingFixedOptionOrderType.getOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(shippingFixedOptionOrderType.getOrderTypeId());
		}

		if (shippingFixedOptionOrderType.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(shippingFixedOptionOrderType.getPriority());
		}

		if (shippingFixedOptionOrderType.getShippingFixedOptionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingFixedOptionId\": ");

			sb.append(shippingFixedOptionOrderType.getShippingFixedOptionId());
		}

		if (shippingFixedOptionOrderType.getShippingFixedOptionOrderTypeId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingFixedOptionOrderTypeId\": ");

			sb.append(
				shippingFixedOptionOrderType.
					getShippingFixedOptionOrderTypeId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ShippingFixedOptionOrderTypeJSONParser
			shippingFixedOptionOrderTypeJSONParser =
				new ShippingFixedOptionOrderTypeJSONParser();

		return shippingFixedOptionOrderTypeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ShippingFixedOptionOrderType shippingFixedOptionOrderType) {

		if (shippingFixedOptionOrderType == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (shippingFixedOptionOrderType.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(shippingFixedOptionOrderType.getActions()));
		}

		if (shippingFixedOptionOrderType.getOrderType() == null) {
			map.put("orderType", null);
		}
		else {
			map.put(
				"orderType",
				String.valueOf(shippingFixedOptionOrderType.getOrderType()));
		}

		if (shippingFixedOptionOrderType.getOrderTypeExternalReferenceCode() ==
				null) {

			map.put("orderTypeExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderTypeExternalReferenceCode",
				String.valueOf(
					shippingFixedOptionOrderType.
						getOrderTypeExternalReferenceCode()));
		}

		if (shippingFixedOptionOrderType.getOrderTypeId() == null) {
			map.put("orderTypeId", null);
		}
		else {
			map.put(
				"orderTypeId",
				String.valueOf(shippingFixedOptionOrderType.getOrderTypeId()));
		}

		if (shippingFixedOptionOrderType.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority",
				String.valueOf(shippingFixedOptionOrderType.getPriority()));
		}

		if (shippingFixedOptionOrderType.getShippingFixedOptionId() == null) {
			map.put("shippingFixedOptionId", null);
		}
		else {
			map.put(
				"shippingFixedOptionId",
				String.valueOf(
					shippingFixedOptionOrderType.getShippingFixedOptionId()));
		}

		if (shippingFixedOptionOrderType.getShippingFixedOptionOrderTypeId() ==
				null) {

			map.put("shippingFixedOptionOrderTypeId", null);
		}
		else {
			map.put(
				"shippingFixedOptionOrderTypeId",
				String.valueOf(
					shippingFixedOptionOrderType.
						getShippingFixedOptionOrderTypeId()));
		}

		return map;
	}

	public static class ShippingFixedOptionOrderTypeJSONParser
		extends BaseJSONParser<ShippingFixedOptionOrderType> {

		@Override
		protected ShippingFixedOptionOrderType createDTO() {
			return new ShippingFixedOptionOrderType();
		}

		@Override
		protected ShippingFixedOptionOrderType[] createDTOArray(int size) {
			return new ShippingFixedOptionOrderType[size];
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
						jsonParserFieldName, "shippingFixedOptionId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingFixedOptionOrderTypeId")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ShippingFixedOptionOrderType shippingFixedOptionOrderType,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					shippingFixedOptionOrderType.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderType")) {
				if (jsonParserFieldValue != null) {
					shippingFixedOptionOrderType.setOrderType(
						OrderTypeSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					shippingFixedOptionOrderType.
						setOrderTypeExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				if (jsonParserFieldValue != null) {
					shippingFixedOptionOrderType.setOrderTypeId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					shippingFixedOptionOrderType.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingFixedOptionId")) {

				if (jsonParserFieldValue != null) {
					shippingFixedOptionOrderType.setShippingFixedOptionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"shippingFixedOptionOrderTypeId")) {

				if (jsonParserFieldValue != null) {
					shippingFixedOptionOrderType.
						setShippingFixedOptionOrderTypeId(
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