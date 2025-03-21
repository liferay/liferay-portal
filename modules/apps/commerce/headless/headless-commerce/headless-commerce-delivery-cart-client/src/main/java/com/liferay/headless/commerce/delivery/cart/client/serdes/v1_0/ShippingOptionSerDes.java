/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.ShippingOption;
import com.liferay.headless.commerce.delivery.cart.client.json.BaseJSONParser;

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
public class ShippingOptionSerDes {

	public static ShippingOption toDTO(String json) {
		ShippingOptionJSONParser shippingOptionJSONParser =
			new ShippingOptionJSONParser();

		return shippingOptionJSONParser.parseToDTO(json);
	}

	public static ShippingOption[] toDTOs(String json) {
		ShippingOptionJSONParser shippingOptionJSONParser =
			new ShippingOptionJSONParser();

		return shippingOptionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ShippingOption shippingOption) {
		if (shippingOption == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (shippingOption.getAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"amount\": ");

			sb.append(shippingOption.getAmount());
		}

		if (shippingOption.getAmountFormatted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"amountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(shippingOption.getAmountFormatted()));

			sb.append("\"");
		}

		if (shippingOption.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(shippingOption.getLabel()));

			sb.append("\"");
		}

		if (shippingOption.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(shippingOption.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ShippingOptionJSONParser shippingOptionJSONParser =
			new ShippingOptionJSONParser();

		return shippingOptionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ShippingOption shippingOption) {
		if (shippingOption == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (shippingOption.getAmount() == null) {
			map.put("amount", null);
		}
		else {
			map.put("amount", String.valueOf(shippingOption.getAmount()));
		}

		if (shippingOption.getAmountFormatted() == null) {
			map.put("amountFormatted", null);
		}
		else {
			map.put(
				"amountFormatted",
				String.valueOf(shippingOption.getAmountFormatted()));
		}

		if (shippingOption.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(shippingOption.getLabel()));
		}

		if (shippingOption.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(shippingOption.getName()));
		}

		return map;
	}

	public static class ShippingOptionJSONParser
		extends BaseJSONParser<ShippingOption> {

		@Override
		protected ShippingOption createDTO() {
			return new ShippingOption();
		}

		@Override
		protected ShippingOption[] createDTOArray(int size) {
			return new ShippingOption[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "amount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "amountFormatted")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ShippingOption shippingOption, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "amount")) {
				if (jsonParserFieldValue != null) {
					shippingOption.setAmount(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "amountFormatted")) {
				if (jsonParserFieldValue != null) {
					shippingOption.setAmountFormatted(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					shippingOption.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					shippingOption.setName((String)jsonParserFieldValue);
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