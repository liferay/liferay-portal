/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountChannelEntry;
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
public class AccountChannelEntrySerDes {

	public static AccountChannelEntry toDTO(String json) {
		AccountChannelEntryJSONParser accountChannelEntryJSONParser =
			new AccountChannelEntryJSONParser();

		return accountChannelEntryJSONParser.parseToDTO(json);
	}

	public static AccountChannelEntry[] toDTOs(String json) {
		AccountChannelEntryJSONParser accountChannelEntryJSONParser =
			new AccountChannelEntryJSONParser();

		return accountChannelEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AccountChannelEntry accountChannelEntry) {
		if (accountChannelEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (accountChannelEntry.getAccountExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(accountChannelEntry.getAccountExternalReferenceCode()));

			sb.append("\"");
		}

		if (accountChannelEntry.getAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(accountChannelEntry.getAccountId());
		}

		if (accountChannelEntry.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(accountChannelEntry.getActions()));
		}

		if (accountChannelEntry.getChannelExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(accountChannelEntry.getChannelExternalReferenceCode()));

			sb.append("\"");
		}

		if (accountChannelEntry.getChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append(accountChannelEntry.getChannelId());
		}

		if (accountChannelEntry.getClassExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(accountChannelEntry.getClassExternalReferenceCode()));

			sb.append("\"");
		}

		if (accountChannelEntry.getClassPK() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classPK\": ");

			sb.append(accountChannelEntry.getClassPK());
		}

		if (accountChannelEntry.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(accountChannelEntry.getId());
		}

		if (accountChannelEntry.getOverrideEligibility() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"overrideEligibility\": ");

			sb.append(accountChannelEntry.getOverrideEligibility());
		}

		if (accountChannelEntry.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(accountChannelEntry.getPriority());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountChannelEntryJSONParser accountChannelEntryJSONParser =
			new AccountChannelEntryJSONParser();

		return accountChannelEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AccountChannelEntry accountChannelEntry) {

		if (accountChannelEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (accountChannelEntry.getAccountExternalReferenceCode() == null) {
			map.put("accountExternalReferenceCode", null);
		}
		else {
			map.put(
				"accountExternalReferenceCode",
				String.valueOf(
					accountChannelEntry.getAccountExternalReferenceCode()));
		}

		if (accountChannelEntry.getAccountId() == null) {
			map.put("accountId", null);
		}
		else {
			map.put(
				"accountId",
				String.valueOf(accountChannelEntry.getAccountId()));
		}

		if (accountChannelEntry.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(accountChannelEntry.getActions()));
		}

		if (accountChannelEntry.getChannelExternalReferenceCode() == null) {
			map.put("channelExternalReferenceCode", null);
		}
		else {
			map.put(
				"channelExternalReferenceCode",
				String.valueOf(
					accountChannelEntry.getChannelExternalReferenceCode()));
		}

		if (accountChannelEntry.getChannelId() == null) {
			map.put("channelId", null);
		}
		else {
			map.put(
				"channelId",
				String.valueOf(accountChannelEntry.getChannelId()));
		}

		if (accountChannelEntry.getClassExternalReferenceCode() == null) {
			map.put("classExternalReferenceCode", null);
		}
		else {
			map.put(
				"classExternalReferenceCode",
				String.valueOf(
					accountChannelEntry.getClassExternalReferenceCode()));
		}

		if (accountChannelEntry.getClassPK() == null) {
			map.put("classPK", null);
		}
		else {
			map.put(
				"classPK", String.valueOf(accountChannelEntry.getClassPK()));
		}

		if (accountChannelEntry.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(accountChannelEntry.getId()));
		}

		if (accountChannelEntry.getOverrideEligibility() == null) {
			map.put("overrideEligibility", null);
		}
		else {
			map.put(
				"overrideEligibility",
				String.valueOf(accountChannelEntry.getOverrideEligibility()));
		}

		if (accountChannelEntry.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority", String.valueOf(accountChannelEntry.getPriority()));
		}

		return map;
	}

	public static class AccountChannelEntryJSONParser
		extends BaseJSONParser<AccountChannelEntry> {

		@Override
		protected AccountChannelEntry createDTO() {
			return new AccountChannelEntry();
		}

		@Override
		protected AccountChannelEntry[] createDTOArray(int size) {
			return new AccountChannelEntry[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
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
						jsonParserFieldName, "channelExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "classExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "overrideEligibility")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountChannelEntry accountChannelEntry, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "accountExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					accountChannelEntry.setAccountExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountId")) {
				if (jsonParserFieldValue != null) {
					accountChannelEntry.setAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					accountChannelEntry.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "channelExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					accountChannelEntry.setChannelExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				if (jsonParserFieldValue != null) {
					accountChannelEntry.setChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "classExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					accountChannelEntry.setClassExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				if (jsonParserFieldValue != null) {
					accountChannelEntry.setClassPK(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					accountChannelEntry.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "overrideEligibility")) {

				if (jsonParserFieldValue != null) {
					accountChannelEntry.setOverrideEligibility(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					accountChannelEntry.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
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