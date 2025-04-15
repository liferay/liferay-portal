/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.AccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.AccountGroup;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (accountGroup.getAccountBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountBriefs\": ");

			sb.append("[");

			for (int i = 0; i < accountGroup.getAccountBriefs().length; i++) {
				sb.append(String.valueOf(accountGroup.getAccountBriefs()[i]));

				if ((i + 1) < accountGroup.getAccountBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (accountGroup.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(accountGroup.getActions()));
		}

		if (accountGroup.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(accountGroup.getCreator()));
		}

		if (accountGroup.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < accountGroup.getCustomFields().length; i++) {
				sb.append(accountGroup.getCustomFields()[i]);

				if ((i + 1) < accountGroup.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (accountGroup.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(accountGroup.getDateCreated()));

			sb.append("\"");
		}

		if (accountGroup.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(accountGroup.getDateModified()));

			sb.append("\"");
		}

		if (accountGroup.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(accountGroup.getDescription()));

			sb.append("\"");
		}

		if (accountGroup.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(accountGroup.getExternalReferenceCode()));

			sb.append("\"");
		}

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

		if (accountGroup.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < accountGroup.getPermissions().length; i++) {
				sb.append(accountGroup.getPermissions()[i]);

				if ((i + 1) < accountGroup.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (accountGroup.getAccountBriefs() == null) {
			map.put("accountBriefs", null);
		}
		else {
			map.put(
				"accountBriefs",
				String.valueOf(accountGroup.getAccountBriefs()));
		}

		if (accountGroup.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(accountGroup.getActions()));
		}

		if (accountGroup.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(accountGroup.getCreator()));
		}

		if (accountGroup.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields", String.valueOf(accountGroup.getCustomFields()));
		}

		if (accountGroup.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(accountGroup.getDateCreated()));
		}

		if (accountGroup.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(accountGroup.getDateModified()));
		}

		if (accountGroup.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(accountGroup.getDescription()));
		}

		if (accountGroup.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(accountGroup.getExternalReferenceCode()));
		}

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

		if (accountGroup.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions", String.valueOf(accountGroup.getPermissions()));
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
			if (Objects.equals(jsonParserFieldName, "accountBriefs")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountGroup accountGroup, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountBriefs")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AccountBrief[] accountBriefsArray =
						new AccountBrief[jsonParserFieldValues.length];

					for (int i = 0; i < accountBriefsArray.length; i++) {
						accountBriefsArray[i] = AccountBriefSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					accountGroup.setAccountBriefs(accountBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					accountGroup.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					accountGroup.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.admin.user.client.custom.field.
						CustomField[] customFieldsArray = new
						com.liferay.headless.admin.user.client.custom.field.
							CustomField[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.admin.user.client.custom.field.
								CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					accountGroup.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					accountGroup.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					accountGroup.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					accountGroup.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					accountGroup.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
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
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.admin.user.client.permission.
						Permission[] permissionsArray = new
						com.liferay.headless.admin.user.client.permission.
							Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.admin.user.client.permission.
								Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					accountGroup.setPermissions(permissionsArray);
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