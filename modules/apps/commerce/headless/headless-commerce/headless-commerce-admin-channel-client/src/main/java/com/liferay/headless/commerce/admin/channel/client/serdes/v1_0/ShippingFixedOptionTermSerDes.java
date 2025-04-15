/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.ShippingFixedOptionTerm;
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
public class ShippingFixedOptionTermSerDes {

	public static ShippingFixedOptionTerm toDTO(String json) {
		ShippingFixedOptionTermJSONParser shippingFixedOptionTermJSONParser =
			new ShippingFixedOptionTermJSONParser();

		return shippingFixedOptionTermJSONParser.parseToDTO(json);
	}

	public static ShippingFixedOptionTerm[] toDTOs(String json) {
		ShippingFixedOptionTermJSONParser shippingFixedOptionTermJSONParser =
			new ShippingFixedOptionTermJSONParser();

		return shippingFixedOptionTermJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ShippingFixedOptionTerm shippingFixedOptionTerm) {

		if (shippingFixedOptionTerm == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (shippingFixedOptionTerm.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(shippingFixedOptionTerm.getActions()));
		}

		if (shippingFixedOptionTerm.getShippingFixedOptionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingFixedOptionId\": ");

			sb.append(shippingFixedOptionTerm.getShippingFixedOptionId());
		}

		if (shippingFixedOptionTerm.getShippingFixedOptionTermId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingFixedOptionTermId\": ");

			sb.append(shippingFixedOptionTerm.getShippingFixedOptionTermId());
		}

		if (shippingFixedOptionTerm.getTerm() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"term\": ");

			sb.append(String.valueOf(shippingFixedOptionTerm.getTerm()));
		}

		if (shippingFixedOptionTerm.getTermExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"termExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					shippingFixedOptionTerm.getTermExternalReferenceCode()));

			sb.append("\"");
		}

		if (shippingFixedOptionTerm.getTermId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"termId\": ");

			sb.append(shippingFixedOptionTerm.getTermId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ShippingFixedOptionTermJSONParser shippingFixedOptionTermJSONParser =
			new ShippingFixedOptionTermJSONParser();

		return shippingFixedOptionTermJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ShippingFixedOptionTerm shippingFixedOptionTerm) {

		if (shippingFixedOptionTerm == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (shippingFixedOptionTerm.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(shippingFixedOptionTerm.getActions()));
		}

		if (shippingFixedOptionTerm.getShippingFixedOptionId() == null) {
			map.put("shippingFixedOptionId", null);
		}
		else {
			map.put(
				"shippingFixedOptionId",
				String.valueOf(
					shippingFixedOptionTerm.getShippingFixedOptionId()));
		}

		if (shippingFixedOptionTerm.getShippingFixedOptionTermId() == null) {
			map.put("shippingFixedOptionTermId", null);
		}
		else {
			map.put(
				"shippingFixedOptionTermId",
				String.valueOf(
					shippingFixedOptionTerm.getShippingFixedOptionTermId()));
		}

		if (shippingFixedOptionTerm.getTerm() == null) {
			map.put("term", null);
		}
		else {
			map.put("term", String.valueOf(shippingFixedOptionTerm.getTerm()));
		}

		if (shippingFixedOptionTerm.getTermExternalReferenceCode() == null) {
			map.put("termExternalReferenceCode", null);
		}
		else {
			map.put(
				"termExternalReferenceCode",
				String.valueOf(
					shippingFixedOptionTerm.getTermExternalReferenceCode()));
		}

		if (shippingFixedOptionTerm.getTermId() == null) {
			map.put("termId", null);
		}
		else {
			map.put(
				"termId", String.valueOf(shippingFixedOptionTerm.getTermId()));
		}

		return map;
	}

	public static class ShippingFixedOptionTermJSONParser
		extends BaseJSONParser<ShippingFixedOptionTerm> {

		@Override
		protected ShippingFixedOptionTerm createDTO() {
			return new ShippingFixedOptionTerm();
		}

		@Override
		protected ShippingFixedOptionTerm[] createDTOArray(int size) {
			return new ShippingFixedOptionTerm[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingFixedOptionId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingFixedOptionTermId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "term")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "termExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "termId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ShippingFixedOptionTerm shippingFixedOptionTerm,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					shippingFixedOptionTerm.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingFixedOptionId")) {

				if (jsonParserFieldValue != null) {
					shippingFixedOptionTerm.setShippingFixedOptionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "shippingFixedOptionTermId")) {

				if (jsonParserFieldValue != null) {
					shippingFixedOptionTerm.setShippingFixedOptionTermId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "term")) {
				if (jsonParserFieldValue != null) {
					shippingFixedOptionTerm.setTerm(
						TermSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "termExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					shippingFixedOptionTerm.setTermExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "termId")) {
				if (jsonParserFieldValue != null) {
					shippingFixedOptionTerm.setTermId(
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