/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.DiscountAccount;
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
public class DiscountAccountSerDes {

	public static DiscountAccount toDTO(String json) {
		DiscountAccountJSONParser discountAccountJSONParser =
			new DiscountAccountJSONParser();

		return discountAccountJSONParser.parseToDTO(json);
	}

	public static DiscountAccount[] toDTOs(String json) {
		DiscountAccountJSONParser discountAccountJSONParser =
			new DiscountAccountJSONParser();

		return discountAccountJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DiscountAccount discountAccount) {
		if (discountAccount == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (discountAccount.getAccount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"account\": ");

			sb.append(String.valueOf(discountAccount.getAccount()));
		}

		if (discountAccount.getAccountExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(discountAccount.getAccountExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountAccount.getAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(discountAccount.getAccountId());
		}

		if (discountAccount.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(discountAccount.getActions()));
		}

		if (discountAccount.getDiscountAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountAccountId\": ");

			sb.append(discountAccount.getDiscountAccountId());
		}

		if (discountAccount.getDiscountExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(discountAccount.getDiscountExternalReferenceCode()));

			sb.append("\"");
		}

		if (discountAccount.getDiscountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountId\": ");

			sb.append(discountAccount.getDiscountId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DiscountAccountJSONParser discountAccountJSONParser =
			new DiscountAccountJSONParser();

		return discountAccountJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DiscountAccount discountAccount) {
		if (discountAccount == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (discountAccount.getAccount() == null) {
			map.put("account", null);
		}
		else {
			map.put("account", String.valueOf(discountAccount.getAccount()));
		}

		if (discountAccount.getAccountExternalReferenceCode() == null) {
			map.put("accountExternalReferenceCode", null);
		}
		else {
			map.put(
				"accountExternalReferenceCode",
				String.valueOf(
					discountAccount.getAccountExternalReferenceCode()));
		}

		if (discountAccount.getAccountId() == null) {
			map.put("accountId", null);
		}
		else {
			map.put(
				"accountId", String.valueOf(discountAccount.getAccountId()));
		}

		if (discountAccount.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(discountAccount.getActions()));
		}

		if (discountAccount.getDiscountAccountId() == null) {
			map.put("discountAccountId", null);
		}
		else {
			map.put(
				"discountAccountId",
				String.valueOf(discountAccount.getDiscountAccountId()));
		}

		if (discountAccount.getDiscountExternalReferenceCode() == null) {
			map.put("discountExternalReferenceCode", null);
		}
		else {
			map.put(
				"discountExternalReferenceCode",
				String.valueOf(
					discountAccount.getDiscountExternalReferenceCode()));
		}

		if (discountAccount.getDiscountId() == null) {
			map.put("discountId", null);
		}
		else {
			map.put(
				"discountId", String.valueOf(discountAccount.getDiscountId()));
		}

		return map;
	}

	public static class DiscountAccountJSONParser
		extends BaseJSONParser<DiscountAccount> {

		@Override
		protected DiscountAccount createDTO() {
			return new DiscountAccount();
		}

		@Override
		protected DiscountAccount[] createDTOArray(int size) {
			return new DiscountAccount[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "account")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "accountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "discountAccountId")) {
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
			DiscountAccount discountAccount, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "account")) {
				if (jsonParserFieldValue != null) {
					discountAccount.setAccount(
						AccountSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountAccount.setAccountExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountId")) {
				if (jsonParserFieldValue != null) {
					discountAccount.setAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					discountAccount.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountAccountId")) {
				if (jsonParserFieldValue != null) {
					discountAccount.setDiscountAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "discountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					discountAccount.setDiscountExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "discountId")) {
				if (jsonParserFieldValue != null) {
					discountAccount.setDiscountId(
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