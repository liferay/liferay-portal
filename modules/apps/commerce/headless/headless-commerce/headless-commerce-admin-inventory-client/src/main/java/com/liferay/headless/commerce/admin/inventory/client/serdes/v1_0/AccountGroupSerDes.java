/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.AccountGroup;
import com.liferay.headless.commerce.admin.inventory.client.json.BaseJSONParser;

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
public class AccountGroupSerDes {

	public static AccountGroup toDTO(String json) {
		AccountGroupJSONParser accountGroupJSONParser =
			new AccountGroupJSONParser();

		return accountGroupJSONParser.parseToDTO(json);
	}

	public static AccountGroup[] toDTOs(String json) {
		AccountGroupJSONParser accountGroupJSONParser =
			new AccountGroupJSONParser();

		return accountGroupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AccountGroup accountGroup) {
		if (accountGroup == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (accountGroup.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(accountGroup.getId());
		}

		if (accountGroup.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(accountGroup.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountGroupJSONParser accountGroupJSONParser =
			new AccountGroupJSONParser();

		return accountGroupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AccountGroup accountGroup) {
		if (accountGroup == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (accountGroup.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(accountGroup.getId()));
		}

		if (accountGroup.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(accountGroup.getName()));
		}

		return map;
	}

	public static class AccountGroupJSONParser
		extends BaseJSONParser<AccountGroup> {

		@Override
		protected AccountGroup createDTO() {
			return new AccountGroup();
		}

		@Override
		protected AccountGroup[] createDTOArray(int size) {
			return new AccountGroup[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountGroup accountGroup, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					accountGroup.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					accountGroup.setName((String)jsonParserFieldValue);
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