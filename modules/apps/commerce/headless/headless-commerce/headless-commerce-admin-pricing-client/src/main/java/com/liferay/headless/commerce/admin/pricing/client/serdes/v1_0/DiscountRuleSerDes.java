/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v1_0.DiscountRule;
import com.liferay.headless.commerce.admin.pricing.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class DiscountRuleSerDes {

	public static DiscountRule toDTO(String json) {
		DiscountRuleJSONParser discountRuleJSONParser =
			new DiscountRuleJSONParser();

		return discountRuleJSONParser.parseToDTO(json);
	}

	public static DiscountRule[] toDTOs(String json) {
		DiscountRuleJSONParser discountRuleJSONParser =
			new DiscountRuleJSONParser();

		return discountRuleJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DiscountRule discountRule) {
		if (discountRule == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (discountRule.getDiscountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountId\": ");

			sb.append(discountRule.getDiscountId());
		}

		if (discountRule.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(discountRule.getId());
		}

		if (discountRule.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(discountRule.getType()));

			sb.append("\"");
		}

		if (discountRule.getTypeSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeSettings\": ");

			sb.append("\"");

			sb.append(_escape(discountRule.getTypeSettings()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DiscountRuleJSONParser discountRuleJSONParser =
			new DiscountRuleJSONParser();

		return discountRuleJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DiscountRule discountRule) {
		if (discountRule == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (discountRule.getDiscountId() == null) {
			map.put("discountId", null);
		}
		else {
			map.put("discountId", String.valueOf(discountRule.getDiscountId()));
		}

		if (discountRule.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(discountRule.getId()));
		}

		if (discountRule.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(discountRule.getType()));
		}

		if (discountRule.getTypeSettings() == null) {
			map.put("typeSettings", null);
		}
		else {
			map.put(
				"typeSettings", String.valueOf(discountRule.getTypeSettings()));
		}

		return map;
	}

	public static class DiscountRuleJSONParser
		extends BaseJSONParser<DiscountRule> {

		@Override
		protected DiscountRule createDTO() {
			return new DiscountRule();
		}

		@Override
		protected DiscountRule[] createDTOArray(int size) {
			return new DiscountRule[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "discountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "typeSettings")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DiscountRule discountRule, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "discountId")) {
				if (jsonParserFieldValue != null) {
					discountRule.setDiscountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					discountRule.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					discountRule.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "typeSettings")) {
				if (jsonParserFieldValue != null) {
					discountRule.setTypeSettings((String)jsonParserFieldValue);
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