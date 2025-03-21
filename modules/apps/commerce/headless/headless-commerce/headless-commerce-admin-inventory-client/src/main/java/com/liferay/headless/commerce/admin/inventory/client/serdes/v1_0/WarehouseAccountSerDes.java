/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseAccount;
import com.liferay.headless.commerce.admin.inventory.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class WarehouseAccountSerDes {

	public static WarehouseAccount toDTO(String json) {
		WarehouseAccountJSONParser warehouseAccountJSONParser =
			new WarehouseAccountJSONParser();

		return warehouseAccountJSONParser.parseToDTO(json);
	}

	public static WarehouseAccount[] toDTOs(String json) {
		WarehouseAccountJSONParser warehouseAccountJSONParser =
			new WarehouseAccountJSONParser();

		return warehouseAccountJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WarehouseAccount warehouseAccount) {
		if (warehouseAccount == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (warehouseAccount.getAccount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"account\": ");

			sb.append(String.valueOf(warehouseAccount.getAccount()));
		}

		if (warehouseAccount.getAccountExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(warehouseAccount.getAccountExternalReferenceCode()));

			sb.append("\"");
		}

		if (warehouseAccount.getAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(warehouseAccount.getAccountId());
		}

		if (warehouseAccount.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(warehouseAccount.getActions()));
		}

		if (warehouseAccount.getWarehouseAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseAccountId\": ");

			sb.append(warehouseAccount.getWarehouseAccountId());
		}

		if (warehouseAccount.getWarehouseExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(warehouseAccount.getWarehouseExternalReferenceCode()));

			sb.append("\"");
		}

		if (warehouseAccount.getWarehouseId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseId\": ");

			sb.append(warehouseAccount.getWarehouseId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WarehouseAccountJSONParser warehouseAccountJSONParser =
			new WarehouseAccountJSONParser();

		return warehouseAccountJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(WarehouseAccount warehouseAccount) {
		if (warehouseAccount == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (warehouseAccount.getAccount() == null) {
			map.put("account", null);
		}
		else {
			map.put("account", String.valueOf(warehouseAccount.getAccount()));
		}

		if (warehouseAccount.getAccountExternalReferenceCode() == null) {
			map.put("accountExternalReferenceCode", null);
		}
		else {
			map.put(
				"accountExternalReferenceCode",
				String.valueOf(
					warehouseAccount.getAccountExternalReferenceCode()));
		}

		if (warehouseAccount.getAccountId() == null) {
			map.put("accountId", null);
		}
		else {
			map.put(
				"accountId", String.valueOf(warehouseAccount.getAccountId()));
		}

		if (warehouseAccount.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(warehouseAccount.getActions()));
		}

		if (warehouseAccount.getWarehouseAccountId() == null) {
			map.put("warehouseAccountId", null);
		}
		else {
			map.put(
				"warehouseAccountId",
				String.valueOf(warehouseAccount.getWarehouseAccountId()));
		}

		if (warehouseAccount.getWarehouseExternalReferenceCode() == null) {
			map.put("warehouseExternalReferenceCode", null);
		}
		else {
			map.put(
				"warehouseExternalReferenceCode",
				String.valueOf(
					warehouseAccount.getWarehouseExternalReferenceCode()));
		}

		if (warehouseAccount.getWarehouseId() == null) {
			map.put("warehouseId", null);
		}
		else {
			map.put(
				"warehouseId",
				String.valueOf(warehouseAccount.getWarehouseId()));
		}

		return map;
	}

	public static class WarehouseAccountJSONParser
		extends BaseJSONParser<WarehouseAccount> {

		@Override
		protected WarehouseAccount createDTO() {
			return new WarehouseAccount();
		}

		@Override
		protected WarehouseAccount[] createDTOArray(int size) {
			return new WarehouseAccount[size];
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
						jsonParserFieldName, "warehouseAccountId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"warehouseExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "warehouseId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WarehouseAccount warehouseAccount, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "account")) {
				if (jsonParserFieldValue != null) {
					warehouseAccount.setAccount(
						AccountSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "accountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					warehouseAccount.setAccountExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountId")) {
				if (jsonParserFieldValue != null) {
					warehouseAccount.setAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					warehouseAccount.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "warehouseAccountId")) {

				if (jsonParserFieldValue != null) {
					warehouseAccount.setWarehouseAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"warehouseExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					warehouseAccount.setWarehouseExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "warehouseId")) {
				if (jsonParserFieldValue != null) {
					warehouseAccount.setWarehouseId(
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