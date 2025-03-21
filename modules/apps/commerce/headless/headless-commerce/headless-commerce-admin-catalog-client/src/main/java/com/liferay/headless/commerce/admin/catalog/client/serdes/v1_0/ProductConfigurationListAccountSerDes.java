/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationListAccount;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

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
public class ProductConfigurationListAccountSerDes {

	public static ProductConfigurationListAccount toDTO(String json) {
		ProductConfigurationListAccountJSONParser
			productConfigurationListAccountJSONParser =
				new ProductConfigurationListAccountJSONParser();

		return productConfigurationListAccountJSONParser.parseToDTO(json);
	}

	public static ProductConfigurationListAccount[] toDTOs(String json) {
		ProductConfigurationListAccountJSONParser
			productConfigurationListAccountJSONParser =
				new ProductConfigurationListAccountJSONParser();

		return productConfigurationListAccountJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ProductConfigurationListAccount productConfigurationListAccount) {

		if (productConfigurationListAccount == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productConfigurationListAccount.getAccount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"account\": ");

			sb.append(
				String.valueOf(productConfigurationListAccount.getAccount()));
		}

		if (productConfigurationListAccount.getAccountExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					productConfigurationListAccount.
						getAccountExternalReferenceCode()));

			sb.append("\"");
		}

		if (productConfigurationListAccount.getAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(productConfigurationListAccount.getAccountId());
		}

		if (productConfigurationListAccount.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(productConfigurationListAccount.getActions()));
		}

		if (productConfigurationListAccount.
				getProductConfigurationListAccountId() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListAccountId\": ");

			sb.append(
				productConfigurationListAccount.
					getProductConfigurationListAccountId());
		}

		if (productConfigurationListAccount.
				getProductConfigurationListExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					productConfigurationListAccount.
						getProductConfigurationListExternalReferenceCode()));

			sb.append("\"");
		}

		if (productConfigurationListAccount.getProductConfigurationListId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListId\": ");

			sb.append(
				productConfigurationListAccount.
					getProductConfigurationListId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductConfigurationListAccountJSONParser
			productConfigurationListAccountJSONParser =
				new ProductConfigurationListAccountJSONParser();

		return productConfigurationListAccountJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProductConfigurationListAccount productConfigurationListAccount) {

		if (productConfigurationListAccount == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productConfigurationListAccount.getAccount() == null) {
			map.put("account", null);
		}
		else {
			map.put(
				"account",
				String.valueOf(productConfigurationListAccount.getAccount()));
		}

		if (productConfigurationListAccount.getAccountExternalReferenceCode() ==
				null) {

			map.put("accountExternalReferenceCode", null);
		}
		else {
			map.put(
				"accountExternalReferenceCode",
				String.valueOf(
					productConfigurationListAccount.
						getAccountExternalReferenceCode()));
		}

		if (productConfigurationListAccount.getAccountId() == null) {
			map.put("accountId", null);
		}
		else {
			map.put(
				"accountId",
				String.valueOf(productConfigurationListAccount.getAccountId()));
		}

		if (productConfigurationListAccount.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(productConfigurationListAccount.getActions()));
		}

		if (productConfigurationListAccount.
				getProductConfigurationListAccountId() == null) {

			map.put("productConfigurationListAccountId", null);
		}
		else {
			map.put(
				"productConfigurationListAccountId",
				String.valueOf(
					productConfigurationListAccount.
						getProductConfigurationListAccountId()));
		}

		if (productConfigurationListAccount.
				getProductConfigurationListExternalReferenceCode() == null) {

			map.put("productConfigurationListExternalReferenceCode", null);
		}
		else {
			map.put(
				"productConfigurationListExternalReferenceCode",
				String.valueOf(
					productConfigurationListAccount.
						getProductConfigurationListExternalReferenceCode()));
		}

		if (productConfigurationListAccount.getProductConfigurationListId() ==
				null) {

			map.put("productConfigurationListId", null);
		}
		else {
			map.put(
				"productConfigurationListId",
				String.valueOf(
					productConfigurationListAccount.
						getProductConfigurationListId()));
		}

		return map;
	}

	public static class ProductConfigurationListAccountJSONParser
		extends BaseJSONParser<ProductConfigurationListAccount> {

		@Override
		protected ProductConfigurationListAccount createDTO() {
			return new ProductConfigurationListAccount();
		}

		@Override
		protected ProductConfigurationListAccount[] createDTOArray(int size) {
			return new ProductConfigurationListAccount[size];
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
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListAccountId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfigurationListId")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductConfigurationListAccount productConfigurationListAccount,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "account")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListAccount.setAccount(
						AccountSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListAccount.
						setAccountExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountId")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListAccount.setAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListAccount.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListAccountId")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListAccount.
						setProductConfigurationListAccountId(
							Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListAccount.
						setProductConfigurationListExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfigurationListId")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListAccount.
						setProductConfigurationListId(
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