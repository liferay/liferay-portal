/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.client.serdes.v1_0;

import com.liferay.headless.commerce.machine.learning.client.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.machine.learning.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class ProductOptionSerDes {

	public static ProductOption toDTO(String json) {
		ProductOptionJSONParser productOptionJSONParser =
			new ProductOptionJSONParser();

		return productOptionJSONParser.parseToDTO(json);
	}

	public static ProductOption[] toDTOs(String json) {
		ProductOptionJSONParser productOptionJSONParser =
			new ProductOptionJSONParser();

		return productOptionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ProductOption productOption) {
		if (productOption == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productOption.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(productOption.getKey()));

			sb.append("\"");
		}

		if (productOption.getOptionKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionKey\": ");

			sb.append("\"");

			sb.append(_escape(productOption.getOptionKey()));

			sb.append("\"");
		}

		if (productOption.getValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"values\": ");

			sb.append("[");

			for (int i = 0; i < productOption.getValues().length; i++) {
				sb.append(productOption.getValues()[i]);

				if ((i + 1) < productOption.getValues().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductOptionJSONParser productOptionJSONParser =
			new ProductOptionJSONParser();

		return productOptionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ProductOption productOption) {
		if (productOption == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productOption.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(productOption.getKey()));
		}

		if (productOption.getOptionKey() == null) {
			map.put("optionKey", null);
		}
		else {
			map.put("optionKey", String.valueOf(productOption.getOptionKey()));
		}

		if (productOption.getValues() == null) {
			map.put("values", null);
		}
		else {
			map.put("values", String.valueOf(productOption.getValues()));
		}

		return map;
	}

	public static class ProductOptionJSONParser
		extends BaseJSONParser<ProductOption> {

		@Override
		protected ProductOption createDTO() {
			return new ProductOption();
		}

		@Override
		protected ProductOption[] createDTOArray(int size) {
			return new ProductOption[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "optionKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "values")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductOption productOption, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					productOption.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "optionKey")) {
				if (jsonParserFieldValue != null) {
					productOption.setOptionKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "values")) {
				if (jsonParserFieldValue != null) {
					productOption.setValues((Map[])jsonParserFieldValue);
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