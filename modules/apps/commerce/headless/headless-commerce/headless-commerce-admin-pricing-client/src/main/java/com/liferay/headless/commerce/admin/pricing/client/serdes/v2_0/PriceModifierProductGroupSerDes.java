/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifierProductGroup;
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
public class PriceModifierProductGroupSerDes {

	public static PriceModifierProductGroup toDTO(String json) {
		PriceModifierProductGroupJSONParser
			priceModifierProductGroupJSONParser =
				new PriceModifierProductGroupJSONParser();

		return priceModifierProductGroupJSONParser.parseToDTO(json);
	}

	public static PriceModifierProductGroup[] toDTOs(String json) {
		PriceModifierProductGroupJSONParser
			priceModifierProductGroupJSONParser =
				new PriceModifierProductGroupJSONParser();

		return priceModifierProductGroupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PriceModifierProductGroup priceModifierProductGroup) {

		if (priceModifierProductGroup == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (priceModifierProductGroup.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(priceModifierProductGroup.getActions()));
		}

		if (priceModifierProductGroup.getPriceModifierExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					priceModifierProductGroup.
						getPriceModifierExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceModifierProductGroup.getPriceModifierId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierId\": ");

			sb.append(priceModifierProductGroup.getPriceModifierId());
		}

		if (priceModifierProductGroup.getPriceModifierProductGroupId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierProductGroupId\": ");

			sb.append(
				priceModifierProductGroup.getPriceModifierProductGroupId());
		}

		if (priceModifierProductGroup.getProductGroup() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productGroup\": ");

			sb.append(
				String.valueOf(priceModifierProductGroup.getProductGroup()));
		}

		if (priceModifierProductGroup.getProductGroupExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productGroupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					priceModifierProductGroup.
						getProductGroupExternalReferenceCode()));

			sb.append("\"");
		}

		if (priceModifierProductGroup.getProductGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productGroupId\": ");

			sb.append(priceModifierProductGroup.getProductGroupId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PriceModifierProductGroupJSONParser
			priceModifierProductGroupJSONParser =
				new PriceModifierProductGroupJSONParser();

		return priceModifierProductGroupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PriceModifierProductGroup priceModifierProductGroup) {

		if (priceModifierProductGroup == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (priceModifierProductGroup.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(priceModifierProductGroup.getActions()));
		}

		if (priceModifierProductGroup.getPriceModifierExternalReferenceCode() ==
				null) {

			map.put("priceModifierExternalReferenceCode", null);
		}
		else {
			map.put(
				"priceModifierExternalReferenceCode",
				String.valueOf(
					priceModifierProductGroup.
						getPriceModifierExternalReferenceCode()));
		}

		if (priceModifierProductGroup.getPriceModifierId() == null) {
			map.put("priceModifierId", null);
		}
		else {
			map.put(
				"priceModifierId",
				String.valueOf(priceModifierProductGroup.getPriceModifierId()));
		}

		if (priceModifierProductGroup.getPriceModifierProductGroupId() ==
				null) {

			map.put("priceModifierProductGroupId", null);
		}
		else {
			map.put(
				"priceModifierProductGroupId",
				String.valueOf(
					priceModifierProductGroup.
						getPriceModifierProductGroupId()));
		}

		if (priceModifierProductGroup.getProductGroup() == null) {
			map.put("productGroup", null);
		}
		else {
			map.put(
				"productGroup",
				String.valueOf(priceModifierProductGroup.getProductGroup()));
		}

		if (priceModifierProductGroup.getProductGroupExternalReferenceCode() ==
				null) {

			map.put("productGroupExternalReferenceCode", null);
		}
		else {
			map.put(
				"productGroupExternalReferenceCode",
				String.valueOf(
					priceModifierProductGroup.
						getProductGroupExternalReferenceCode()));
		}

		if (priceModifierProductGroup.getProductGroupId() == null) {
			map.put("productGroupId", null);
		}
		else {
			map.put(
				"productGroupId",
				String.valueOf(priceModifierProductGroup.getProductGroupId()));
		}

		return map;
	}

	public static class PriceModifierProductGroupJSONParser
		extends BaseJSONParser<PriceModifierProductGroup> {

		@Override
		protected PriceModifierProductGroup createDTO() {
			return new PriceModifierProductGroup();
		}

		@Override
		protected PriceModifierProductGroup[] createDTOArray(int size) {
			return new PriceModifierProductGroup[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"priceModifierExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priceModifierId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierProductGroupId")) {

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
			PriceModifierProductGroup priceModifierProductGroup,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					priceModifierProductGroup.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"priceModifierExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceModifierProductGroup.
						setPriceModifierExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priceModifierId")) {
				if (jsonParserFieldValue != null) {
					priceModifierProductGroup.setPriceModifierId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "priceModifierProductGroupId")) {

				if (jsonParserFieldValue != null) {
					priceModifierProductGroup.setPriceModifierProductGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productGroup")) {
				if (jsonParserFieldValue != null) {
					priceModifierProductGroup.setProductGroup(
						ProductGroupSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productGroupExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					priceModifierProductGroup.
						setProductGroupExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productGroupId")) {
				if (jsonParserFieldValue != null) {
					priceModifierProductGroup.setProductGroupId(
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