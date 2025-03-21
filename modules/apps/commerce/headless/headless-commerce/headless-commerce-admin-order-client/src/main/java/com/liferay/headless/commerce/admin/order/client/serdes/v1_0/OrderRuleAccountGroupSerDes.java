/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.order.client.dto.v1_0.OrderRuleAccountGroup;
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
public class OrderRuleAccountGroupSerDes {

	public static OrderRuleAccountGroup toDTO(String json) {
		OrderRuleAccountGroupJSONParser orderRuleAccountGroupJSONParser =
			new OrderRuleAccountGroupJSONParser();

		return orderRuleAccountGroupJSONParser.parseToDTO(json);
	}

	public static OrderRuleAccountGroup[] toDTOs(String json) {
		OrderRuleAccountGroupJSONParser orderRuleAccountGroupJSONParser =
			new OrderRuleAccountGroupJSONParser();

		return orderRuleAccountGroupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(OrderRuleAccountGroup orderRuleAccountGroup) {
		if (orderRuleAccountGroup == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (orderRuleAccountGroup.getAccountGroup() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroup\": ");

			sb.append(String.valueOf(orderRuleAccountGroup.getAccountGroup()));
		}

		if (orderRuleAccountGroup.getAccountGroupExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					orderRuleAccountGroup.
						getAccountGroupExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderRuleAccountGroup.getAccountGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupId\": ");

			sb.append(orderRuleAccountGroup.getAccountGroupId());
		}

		if (orderRuleAccountGroup.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(orderRuleAccountGroup.getActions()));
		}

		if (orderRuleAccountGroup.getOrderRuleAccountGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderRuleAccountGroupId\": ");

			sb.append(orderRuleAccountGroup.getOrderRuleAccountGroupId());
		}

		if (orderRuleAccountGroup.getOrderRuleExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderRuleExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					orderRuleAccountGroup.getOrderRuleExternalReferenceCode()));

			sb.append("\"");
		}

		if (orderRuleAccountGroup.getOrderRuleId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderRuleId\": ");

			sb.append(orderRuleAccountGroup.getOrderRuleId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrderRuleAccountGroupJSONParser orderRuleAccountGroupJSONParser =
			new OrderRuleAccountGroupJSONParser();

		return orderRuleAccountGroupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		OrderRuleAccountGroup orderRuleAccountGroup) {

		if (orderRuleAccountGroup == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (orderRuleAccountGroup.getAccountGroup() == null) {
			map.put("accountGroup", null);
		}
		else {
			map.put(
				"accountGroup",
				String.valueOf(orderRuleAccountGroup.getAccountGroup()));
		}

		if (orderRuleAccountGroup.getAccountGroupExternalReferenceCode() ==
				null) {

			map.put("accountGroupExternalReferenceCode", null);
		}
		else {
			map.put(
				"accountGroupExternalReferenceCode",
				String.valueOf(
					orderRuleAccountGroup.
						getAccountGroupExternalReferenceCode()));
		}

		if (orderRuleAccountGroup.getAccountGroupId() == null) {
			map.put("accountGroupId", null);
		}
		else {
			map.put(
				"accountGroupId",
				String.valueOf(orderRuleAccountGroup.getAccountGroupId()));
		}

		if (orderRuleAccountGroup.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(orderRuleAccountGroup.getActions()));
		}

		if (orderRuleAccountGroup.getOrderRuleAccountGroupId() == null) {
			map.put("orderRuleAccountGroupId", null);
		}
		else {
			map.put(
				"orderRuleAccountGroupId",
				String.valueOf(
					orderRuleAccountGroup.getOrderRuleAccountGroupId()));
		}

		if (orderRuleAccountGroup.getOrderRuleExternalReferenceCode() == null) {
			map.put("orderRuleExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderRuleExternalReferenceCode",
				String.valueOf(
					orderRuleAccountGroup.getOrderRuleExternalReferenceCode()));
		}

		if (orderRuleAccountGroup.getOrderRuleId() == null) {
			map.put("orderRuleId", null);
		}
		else {
			map.put(
				"orderRuleId",
				String.valueOf(orderRuleAccountGroup.getOrderRuleId()));
		}

		return map;
	}

	public static class OrderRuleAccountGroupJSONParser
		extends BaseJSONParser<OrderRuleAccountGroup> {

		@Override
		protected OrderRuleAccountGroup createDTO() {
			return new OrderRuleAccountGroup();
		}

		@Override
		protected OrderRuleAccountGroup[] createDTOArray(int size) {
			return new OrderRuleAccountGroup[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountGroup")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"accountGroupExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "accountGroupId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderRuleAccountGroupId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderRuleExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderRuleId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OrderRuleAccountGroup orderRuleAccountGroup,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountGroup")) {
				if (jsonParserFieldValue != null) {
					orderRuleAccountGroup.setAccountGroup(
						OrderAccountGroupSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"accountGroupExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderRuleAccountGroup.setAccountGroupExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountGroupId")) {
				if (jsonParserFieldValue != null) {
					orderRuleAccountGroup.setAccountGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					orderRuleAccountGroup.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "orderRuleAccountGroupId")) {

				if (jsonParserFieldValue != null) {
					orderRuleAccountGroup.setOrderRuleAccountGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderRuleExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					orderRuleAccountGroup.setOrderRuleExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderRuleId")) {
				if (jsonParserFieldValue != null) {
					orderRuleAccountGroup.setOrderRuleId(
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