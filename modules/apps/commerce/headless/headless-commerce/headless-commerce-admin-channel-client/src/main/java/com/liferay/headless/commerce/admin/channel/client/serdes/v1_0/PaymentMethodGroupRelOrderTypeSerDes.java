/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.PaymentMethodGroupRelOrderType;
import com.liferay.headless.commerce.admin.channel.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class PaymentMethodGroupRelOrderTypeSerDes {

	public static PaymentMethodGroupRelOrderType toDTO(String json) {
		PaymentMethodGroupRelOrderTypeJSONParser
			paymentMethodGroupRelOrderTypeJSONParser =
				new PaymentMethodGroupRelOrderTypeJSONParser();

		return paymentMethodGroupRelOrderTypeJSONParser.parseToDTO(json);
	}

	public static PaymentMethodGroupRelOrderType[] toDTOs(String json) {
		PaymentMethodGroupRelOrderTypeJSONParser
			paymentMethodGroupRelOrderTypeJSONParser =
				new PaymentMethodGroupRelOrderTypeJSONParser();

		return paymentMethodGroupRelOrderTypeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType) {

		if (paymentMethodGroupRelOrderType == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (paymentMethodGroupRelOrderType.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(paymentMethodGroupRelOrderType.getActions()));
		}

		if (paymentMethodGroupRelOrderType.getOrderType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderType\": ");

			sb.append(
				String.valueOf(paymentMethodGroupRelOrderType.getOrderType()));
		}

		if (paymentMethodGroupRelOrderType.
				getOrderTypeExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					paymentMethodGroupRelOrderType.
						getOrderTypeExternalReferenceCode()));

			sb.append("\"");
		}

		if (paymentMethodGroupRelOrderType.getOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(paymentMethodGroupRelOrderType.getOrderTypeId());
		}

		if (paymentMethodGroupRelOrderType.getPaymentMethodGroupRelId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentMethodGroupRelId\": ");

			sb.append(
				paymentMethodGroupRelOrderType.getPaymentMethodGroupRelId());
		}

		if (paymentMethodGroupRelOrderType.
				getPaymentMethodGroupRelOrderTypeId() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paymentMethodGroupRelOrderTypeId\": ");

			sb.append(
				paymentMethodGroupRelOrderType.
					getPaymentMethodGroupRelOrderTypeId());
		}

		if (paymentMethodGroupRelOrderType.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(paymentMethodGroupRelOrderType.getPriority());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PaymentMethodGroupRelOrderTypeJSONParser
			paymentMethodGroupRelOrderTypeJSONParser =
				new PaymentMethodGroupRelOrderTypeJSONParser();

		return paymentMethodGroupRelOrderTypeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType) {

		if (paymentMethodGroupRelOrderType == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (paymentMethodGroupRelOrderType.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(paymentMethodGroupRelOrderType.getActions()));
		}

		if (paymentMethodGroupRelOrderType.getOrderType() == null) {
			map.put("orderType", null);
		}
		else {
			map.put(
				"orderType",
				String.valueOf(paymentMethodGroupRelOrderType.getOrderType()));
		}

		if (paymentMethodGroupRelOrderType.
				getOrderTypeExternalReferenceCode() == null) {

			map.put("orderTypeExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderTypeExternalReferenceCode",
				String.valueOf(
					paymentMethodGroupRelOrderType.
						getOrderTypeExternalReferenceCode()));
		}

		if (paymentMethodGroupRelOrderType.getOrderTypeId() == null) {
			map.put("orderTypeId", null);
		}
		else {
			map.put(
				"orderTypeId",
				String.valueOf(
					paymentMethodGroupRelOrderType.getOrderTypeId()));
		}

		if (paymentMethodGroupRelOrderType.getPaymentMethodGroupRelId() ==
				null) {

			map.put("paymentMethodGroupRelId", null);
		}
		else {
			map.put(
				"paymentMethodGroupRelId",
				String.valueOf(
					paymentMethodGroupRelOrderType.
						getPaymentMethodGroupRelId()));
		}

		if (paymentMethodGroupRelOrderType.
				getPaymentMethodGroupRelOrderTypeId() == null) {

			map.put("paymentMethodGroupRelOrderTypeId", null);
		}
		else {
			map.put(
				"paymentMethodGroupRelOrderTypeId",
				String.valueOf(
					paymentMethodGroupRelOrderType.
						getPaymentMethodGroupRelOrderTypeId()));
		}

		if (paymentMethodGroupRelOrderType.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority",
				String.valueOf(paymentMethodGroupRelOrderType.getPriority()));
		}

		return map;
	}

	public static class PaymentMethodGroupRelOrderTypeJSONParser
		extends BaseJSONParser<PaymentMethodGroupRelOrderType> {

		@Override
		protected PaymentMethodGroupRelOrderType createDTO() {
			return new PaymentMethodGroupRelOrderType();
		}

		@Override
		protected PaymentMethodGroupRelOrderType[] createDTOArray(int size) {
			return new PaymentMethodGroupRelOrderType[size];
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
			else if (Objects.equals(
						jsonParserFieldName, "paymentMethodGroupRelId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"paymentMethodGroupRelOrderTypeId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PaymentMethodGroupRelOrderType paymentMethodGroupRelOrderType,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					paymentMethodGroupRelOrderType.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderType")) {
				if (jsonParserFieldValue != null) {
					paymentMethodGroupRelOrderType.setOrderType(
						OrderTypeSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					paymentMethodGroupRelOrderType.
						setOrderTypeExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				if (jsonParserFieldValue != null) {
					paymentMethodGroupRelOrderType.setOrderTypeId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "paymentMethodGroupRelId")) {

				if (jsonParserFieldValue != null) {
					paymentMethodGroupRelOrderType.setPaymentMethodGroupRelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"paymentMethodGroupRelOrderTypeId")) {

				if (jsonParserFieldValue != null) {
					paymentMethodGroupRelOrderType.
						setPaymentMethodGroupRelOrderTypeId(
							Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					paymentMethodGroupRelOrderType.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
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