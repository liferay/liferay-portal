/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountAccountGroup;
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
public class DiscountAccountGroupSerDes {

	public static DiscountAccountGroup toDTO(String json) {
		DiscountAccountGroupJSONParser discountAccountGroupJSONParser =
			new DiscountAccountGroupJSONParser();

		return discountAccountGroupJSONParser.parseToDTO(json);
	}

	public static DiscountAccountGroup[] toDTOs(String json) {
		DiscountAccountGroupJSONParser discountAccountGroupJSONParser =
			new DiscountAccountGroupJSONParser();

		return discountAccountGroupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DiscountAccountGroup discountAccountGroup) {
		if (discountAccountGroup == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (discountAccountGroup.getAccountGroup() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroup\": ");

			sb.append(String.valueOf(discountAccountGroup.getAccountGroup()));
		}

		if (discountAccountGroup.getAccountGroupExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					discountAccountGroup.
						getAccountGroupExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountAccountGroup.getAccountGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupId\": ");

			sb.append(discountAccountGroup.getAccountGroupId());
		}

		if (discountAccountGroup.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(discountAccountGroup.getActions()));
		}

		if (discountAccountGroup.getDiscountAccountGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountAccountGroupId\": ");

			sb.append(discountAccountGroup.getDiscountAccountGroupId());
		}

		if (discountAccountGroup.getDiscountExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					discountAccountGroup.getDiscountExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountAccountGroup.getDiscountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountId\": ");

			sb.append(discountAccountGroup.getDiscountId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DiscountAccountGroupJSONParser discountAccountGroupJSONParser =
			new DiscountAccountGroupJSONParser();

		return discountAccountGroupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DiscountAccountGroup discountAccountGroup) {

		if (discountAccountGroup == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (discountAccountGroup.getAccountGroup() == null) {
			map.put("accountGroup", null);
		}
		else {
			map.put(
				"accountGroup",
				String.valueOf(discountAccountGroup.getAccountGroup()));
		}

		if (discountAccountGroup.getAccountGroupExternalReferenceCode() ==
				null) {

			map.put("accountGroupExternalReferenceCode", null);
		}
		else {
			map.put(
				"accountGroupExternalReferenceCode",
				String.valueOf(
					discountAccountGroup.
						getAccountGroupExternalReferenceCode()));
		}

		if (discountAccountGroup.getAccountGroupId() == null) {
			map.put("accountGroupId", null);
		}
		else {
			map.put(
				"accountGroupId",
				String.valueOf(discountAccountGroup.getAccountGroupId()));
		}

		if (discountAccountGroup.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(discountAccountGroup.getActions()));
		}

		if (discountAccountGroup.getDiscountAccountGroupId() == null) {
			map.put("discountAccountGroupId", null);
		}
		else {
			map.put(
				"discountAccountGroupId",
				String.valueOf(
					discountAccountGroup.getDiscountAccountGroupId()));
		}

		if (discountAccountGroup.getDiscountExternalReferenceCode() == null) {
			map.put("discountExternalReferenceCode", null);
		}
		else {
			map.put(
				"discountExternalReferenceCode",
				String.valueOf(
					discountAccountGroup.getDiscountExternalReferenceCode()));
		}

		if (discountAccountGroup.getDiscountId() == null) {
			map.put("discountId", null);
		}
		else {
			map.put(
				"discountId",
				String.valueOf(discountAccountGroup.getDiscountId()));
		}

		return map;
	}

	public static class DiscountAccountGroupJSONParser
		extends BaseJSONParser<DiscountAccountGroup> {

		@Override
		protected DiscountAccountGroup createDTO() {
			return new DiscountAccountGroup();
		}

		@Override
		protected DiscountAccountGroup[] createDTOArray(int size) {
			return new DiscountAccountGroup[size];
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
						jsonParserFieldName, "discountAccountGroupId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "discountId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DiscountAccountGroup discountAccountGroup,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountGroup")) {
				if (jsonParserFieldValue != null) {
					discountAccountGroup.setAccountGroup(
						PricingAccountGroupSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"accountGroupExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountAccountGroup.setAccountGroupExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountGroupId")) {
				if (jsonParserFieldValue != null) {
					discountAccountGroup.setAccountGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					discountAccountGroup.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountAccountGroupId")) {

				if (jsonParserFieldValue != null) {
					discountAccountGroup.setDiscountAccountGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountAccountGroup.setDiscountExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountId")) {
				if (jsonParserFieldValue != null) {
					discountAccountGroup.setDiscountId(
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