/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountOrganization;
import com.liferay.headless.commerce.admin.account.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class AccountOrganizationSerDes {

	public static AccountOrganization toDTO(String json) {
		AccountOrganizationJSONParser accountOrganizationJSONParser =
			new AccountOrganizationJSONParser();

		return accountOrganizationJSONParser.parseToDTO(json);
	}

	public static AccountOrganization[] toDTOs(String json) {
		AccountOrganizationJSONParser accountOrganizationJSONParser =
			new AccountOrganizationJSONParser();

		return accountOrganizationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AccountOrganization accountOrganization) {
		if (accountOrganization == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (accountOrganization.getAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(accountOrganization.getAccountId());
		}

		if (accountOrganization.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(accountOrganization.getName()));

			sb.append("\"");
		}

		if (accountOrganization.getOrganizationExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					accountOrganization.
						getOrganizationExternalReferenceCode()));

			sb.append("\"");
		}

		if (accountOrganization.getOrganizationId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"organizationId\": ");

			sb.append(accountOrganization.getOrganizationId());
		}

		if (accountOrganization.getTreePath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"treePath\": ");

			sb.append("\"");

			sb.append(_escape(accountOrganization.getTreePath()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountOrganizationJSONParser accountOrganizationJSONParser =
			new AccountOrganizationJSONParser();

		return accountOrganizationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AccountOrganization accountOrganization) {

		if (accountOrganization == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (accountOrganization.getAccountId() == null) {
			map.put("accountId", null);
		}
		else {
			map.put(
				"accountId",
				String.valueOf(accountOrganization.getAccountId()));
		}

		if (accountOrganization.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(accountOrganization.getName()));
		}

		if (accountOrganization.getOrganizationExternalReferenceCode() ==
				null) {

			map.put("organizationExternalReferenceCode", null);
		}
		else {
			map.put(
				"organizationExternalReferenceCode",
				String.valueOf(
					accountOrganization.
						getOrganizationExternalReferenceCode()));
		}

		if (accountOrganization.getOrganizationId() == null) {
			map.put("organizationId", null);
		}
		else {
			map.put(
				"organizationId",
				String.valueOf(accountOrganization.getOrganizationId()));
		}

		if (accountOrganization.getTreePath() == null) {
			map.put("treePath", null);
		}
		else {
			map.put(
				"treePath", String.valueOf(accountOrganization.getTreePath()));
		}

		return map;
	}

	public static class AccountOrganizationJSONParser
		extends BaseJSONParser<AccountOrganization> {

		@Override
		protected AccountOrganization createDTO() {
			return new AccountOrganization();
		}

		@Override
		protected AccountOrganization[] createDTOArray(int size) {
			return new AccountOrganization[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"organizationExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "organizationId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "treePath")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountOrganization accountOrganization, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountId")) {
				if (jsonParserFieldValue != null) {
					accountOrganization.setAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					accountOrganization.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"organizationExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					accountOrganization.setOrganizationExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "organizationId")) {
				if (jsonParserFieldValue != null) {
					accountOrganization.setOrganizationId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "treePath")) {
				if (jsonParserFieldValue != null) {
					accountOrganization.setTreePath(
						(String)jsonParserFieldValue);
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