/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountOrderType;
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
public class DiscountOrderTypeSerDes {

	public static DiscountOrderType toDTO(String json) {
		DiscountOrderTypeJSONParser discountOrderTypeJSONParser =
			new DiscountOrderTypeJSONParser();

		return discountOrderTypeJSONParser.parseToDTO(json);
	}

	public static DiscountOrderType[] toDTOs(String json) {
		DiscountOrderTypeJSONParser discountOrderTypeJSONParser =
			new DiscountOrderTypeJSONParser();

		return discountOrderTypeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DiscountOrderType discountOrderType) {
		if (discountOrderType == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (discountOrderType.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(discountOrderType.getActions()));
		}

		if (discountOrderType.getDiscountExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(discountOrderType.getDiscountExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountOrderType.getDiscountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountId\": ");

			sb.append(discountOrderType.getDiscountId());
		}

		if (discountOrderType.getDiscountOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountOrderTypeId\": ");

			sb.append(discountOrderType.getDiscountOrderTypeId());
		}

		if (discountOrderType.getOrderType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderType\": ");

			sb.append(String.valueOf(discountOrderType.getOrderType()));
		}

		if (discountOrderType.getOrderTypeExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(discountOrderType.getOrderTypeExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountOrderType.getOrderTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderTypeId\": ");

			sb.append(discountOrderType.getOrderTypeId());
		}

		if (discountOrderType.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(discountOrderType.getPriority());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DiscountOrderTypeJSONParser discountOrderTypeJSONParser =
			new DiscountOrderTypeJSONParser();

		return discountOrderTypeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DiscountOrderType discountOrderType) {

		if (discountOrderType == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (discountOrderType.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(discountOrderType.getActions()));
		}

		if (discountOrderType.getDiscountExternalReferenceCode() == null) {
			map.put("discountExternalReferenceCode", null);
		}
		else {
			map.put(
				"discountExternalReferenceCode",
				String.valueOf(
					discountOrderType.getDiscountExternalReferenceCode()));
		}

		if (discountOrderType.getDiscountId() == null) {
			map.put("discountId", null);
		}
		else {
			map.put(
				"discountId",
				String.valueOf(discountOrderType.getDiscountId()));
		}

		if (discountOrderType.getDiscountOrderTypeId() == null) {
			map.put("discountOrderTypeId", null);
		}
		else {
			map.put(
				"discountOrderTypeId",
				String.valueOf(discountOrderType.getDiscountOrderTypeId()));
		}

		if (discountOrderType.getOrderType() == null) {
			map.put("orderType", null);
		}
		else {
			map.put(
				"orderType", String.valueOf(discountOrderType.getOrderType()));
		}

		if (discountOrderType.getOrderTypeExternalReferenceCode() == null) {
			map.put("orderTypeExternalReferenceCode", null);
		}
		else {
			map.put(
				"orderTypeExternalReferenceCode",
				String.valueOf(
					discountOrderType.getOrderTypeExternalReferenceCode()));
		}

		if (discountOrderType.getOrderTypeId() == null) {
			map.put("orderTypeId", null);
		}
		else {
			map.put(
				"orderTypeId",
				String.valueOf(discountOrderType.getOrderTypeId()));
		}

		if (discountOrderType.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority", String.valueOf(discountOrderType.getPriority()));
		}

		return map;
	}

	public static class DiscountOrderTypeJSONParser
		extends BaseJSONParser<DiscountOrderType> {

		@Override
		protected DiscountOrderType createDTO() {
			return new DiscountOrderType();
		}

		@Override
		protected DiscountOrderType[] createDTOArray(int size) {
			return new DiscountOrderType[size];
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
						jsonParserFieldName, "discountOrderTypeId")) {

				return false;
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

			return false;
		}

		@Override
		protected void setField(
			DiscountOrderType discountOrderType, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					discountOrderType.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountOrderType.setDiscountExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountId")) {
				if (jsonParserFieldValue != null) {
					discountOrderType.setDiscountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountOrderTypeId")) {

				if (jsonParserFieldValue != null) {
					discountOrderType.setDiscountOrderTypeId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderType")) {
				if (jsonParserFieldValue != null) {
					discountOrderType.setOrderType(
						OrderTypeSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"orderTypeExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountOrderType.setOrderTypeExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderTypeId")) {
				if (jsonParserFieldValue != null) {
					discountOrderType.setOrderTypeId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					discountOrderType.setPriority(
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