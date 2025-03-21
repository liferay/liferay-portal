/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class AccountBriefSerDes {

	public static AccountBrief toDTO(String json) {
		AccountBriefJSONParser accountBriefJSONParser =
			new AccountBriefJSONParser();

		return accountBriefJSONParser.parseToDTO(json);
	}

	public static AccountBrief[] toDTOs(String json) {
		AccountBriefJSONParser accountBriefJSONParser =
			new AccountBriefJSONParser();

		return accountBriefJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AccountBrief accountBrief) {
		if (accountBrief == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (accountBrief.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(accountBrief.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (accountBrief.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(accountBrief.getId());
		}

		if (accountBrief.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(accountBrief.getName()));

			sb.append("\"");
		}

		if (accountBrief.getRoleBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleBriefs\": ");

			sb.append("[");

			for (int i = 0; i < accountBrief.getRoleBriefs().length; i++) {
				sb.append(String.valueOf(accountBrief.getRoleBriefs()[i]));

				if ((i + 1) < accountBrief.getRoleBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (accountBrief.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(accountBrief.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountBriefJSONParser accountBriefJSONParser =
			new AccountBriefJSONParser();

		return accountBriefJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AccountBrief accountBrief) {
		if (accountBrief == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (accountBrief.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(accountBrief.getExternalReferenceCode()));
		}

		if (accountBrief.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(accountBrief.getId()));
		}

		if (accountBrief.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(accountBrief.getName()));
		}

		if (accountBrief.getRoleBriefs() == null) {
			map.put("roleBriefs", null);
		}
		else {
			map.put("roleBriefs", String.valueOf(accountBrief.getRoleBriefs()));
		}

		if (accountBrief.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(accountBrief.getType()));
		}

		return map;
	}

	public static class AccountBriefJSONParser
		extends BaseJSONParser<AccountBrief> {

		@Override
		protected AccountBrief createDTO() {
			return new AccountBrief();
		}

		@Override
		protected AccountBrief[] createDTOArray(int size) {
			return new AccountBrief[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleBriefs")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountBrief accountBrief, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					accountBrief.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					accountBrief.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					accountBrief.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleBriefs")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					RoleBrief[] roleBriefsArray =
						new RoleBrief[jsonParserFieldValues.length];

					for (int i = 0; i < roleBriefsArray.length; i++) {
						roleBriefsArray[i] = RoleBriefSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					accountBrief.setRoleBriefs(roleBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					accountBrief.setType((String)jsonParserFieldValue);
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