/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.ShippingOption;
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

		if (shippingOption.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(shippingOption.getActive());
		}

		if (shippingOption.getAmount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"amount\": ");

			sb.append(shippingOption.getAmount());
		}

		if (shippingOption.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(shippingOption.getDescription()));
		}

		if (shippingOption.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(shippingOption.getId());
		}

		if (shippingOption.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(shippingOption.getName()));
		}

		if (shippingOption.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(shippingOption.getPriority());
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

		if (shippingOption.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(shippingOption.getActive()));
		}

		if (shippingOption.getAmount() == null) {
			map.put("amount", null);
		}
		else {
			map.put("amount", String.valueOf(shippingOption.getAmount()));
		}

		if (shippingOption.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(shippingOption.getDescription()));
		}

		if (shippingOption.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(shippingOption.getId()));
		}

		if (shippingOption.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(shippingOption.getName()));
		}

		if (shippingOption.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(shippingOption.getPriority()));
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
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "amount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ShippingOption shippingOption, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					shippingOption.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "amount")) {
				if (jsonParserFieldValue != null) {
					shippingOption.setAmount(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					shippingOption.setDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					shippingOption.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					shippingOption.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					shippingOption.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
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