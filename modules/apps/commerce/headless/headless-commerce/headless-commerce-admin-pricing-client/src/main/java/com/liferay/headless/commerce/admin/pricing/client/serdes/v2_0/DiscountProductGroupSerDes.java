/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountProductGroup;
import com.liferay.headless.commerce.admin.pricing.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class DiscountProductGroupSerDes {

	public static DiscountProductGroup toDTO(String json) {
		DiscountProductGroupJSONParser discountProductGroupJSONParser =
			new DiscountProductGroupJSONParser();

		return discountProductGroupJSONParser.parseToDTO(json);
	}

	public static DiscountProductGroup[] toDTOs(String json) {
		DiscountProductGroupJSONParser discountProductGroupJSONParser =
			new DiscountProductGroupJSONParser();

		return discountProductGroupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DiscountProductGroup discountProductGroup) {
		if (discountProductGroup == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (discountProductGroup.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(discountProductGroup.getActions()));
		}

		if (discountProductGroup.getDiscountExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					discountProductGroup.getDiscountExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountProductGroup.getDiscountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountId\": ");

			sb.append(discountProductGroup.getDiscountId());
		}

		if (discountProductGroup.getDiscountProductGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountProductGroupId\": ");

			sb.append(discountProductGroup.getDiscountProductGroupId());
		}

		if (discountProductGroup.getProductGroup() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productGroup\": ");

			sb.append(String.valueOf(discountProductGroup.getProductGroup()));
		}

		if (discountProductGroup.getProductGroupExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productGroupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					discountProductGroup.
						getProductGroupExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountProductGroup.getProductGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productGroupId\": ");

			sb.append(discountProductGroup.getProductGroupId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DiscountProductGroupJSONParser discountProductGroupJSONParser =
			new DiscountProductGroupJSONParser();

		return discountProductGroupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DiscountProductGroup discountProductGroup) {

		if (discountProductGroup == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (discountProductGroup.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(discountProductGroup.getActions()));
		}

		if (discountProductGroup.getDiscountExternalReferenceCode() == null) {
			map.put("discountExternalReferenceCode", null);
		}
		else {
			map.put(
				"discountExternalReferenceCode",
				String.valueOf(
					discountProductGroup.getDiscountExternalReferenceCode()));
		}

		if (discountProductGroup.getDiscountId() == null) {
			map.put("discountId", null);
		}
		else {
			map.put(
				"discountId",
				String.valueOf(discountProductGroup.getDiscountId()));
		}

		if (discountProductGroup.getDiscountProductGroupId() == null) {
			map.put("discountProductGroupId", null);
		}
		else {
			map.put(
				"discountProductGroupId",
				String.valueOf(
					discountProductGroup.getDiscountProductGroupId()));
		}

		if (discountProductGroup.getProductGroup() == null) {
			map.put("productGroup", null);
		}
		else {
			map.put(
				"productGroup",
				String.valueOf(discountProductGroup.getProductGroup()));
		}

		if (discountProductGroup.getProductGroupExternalReferenceCode() ==
				null) {

			map.put("productGroupExternalReferenceCode", null);
		}
		else {
			map.put(
				"productGroupExternalReferenceCode",
				String.valueOf(
					discountProductGroup.
						getProductGroupExternalReferenceCode()));
		}

		if (discountProductGroup.getProductGroupId() == null) {
			map.put("productGroupId", null);
		}
		else {
			map.put(
				"productGroupId",
				String.valueOf(discountProductGroup.getProductGroupId()));
		}

		return map;
	}

	public static class DiscountProductGroupJSONParser
		extends BaseJSONParser<DiscountProductGroup> {

		@Override
		protected DiscountProductGroup createDTO() {
			return new DiscountProductGroup();
		}

		@Override
		protected DiscountProductGroup[] createDTOArray(int size) {
			return new DiscountProductGroup[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discountId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountProductGroupId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productGroup")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productGroupExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productGroupId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DiscountProductGroup discountProductGroup,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					discountProductGroup.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountProductGroup.setDiscountExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountId")) {
				if (jsonParserFieldValue != null) {
					discountProductGroup.setDiscountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountProductGroupId")) {

				if (jsonParserFieldValue != null) {
					discountProductGroup.setDiscountProductGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productGroup")) {
				if (jsonParserFieldValue != null) {
					discountProductGroup.setProductGroup(
						ProductGroupSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productGroupExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountProductGroup.setProductGroupExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productGroupId")) {
				if (jsonParserFieldValue != null) {
					discountProductGroup.setProductGroupId(
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