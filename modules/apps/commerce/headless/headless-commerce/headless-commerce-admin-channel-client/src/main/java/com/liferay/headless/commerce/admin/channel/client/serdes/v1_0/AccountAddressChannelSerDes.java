/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.AccountAddressChannel;
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
public class AccountAddressChannelSerDes {

	public static AccountAddressChannel toDTO(String json) {
		AccountAddressChannelJSONParser accountAddressChannelJSONParser =
			new AccountAddressChannelJSONParser();

		return accountAddressChannelJSONParser.parseToDTO(json);
	}

	public static AccountAddressChannel[] toDTOs(String json) {
		AccountAddressChannelJSONParser accountAddressChannelJSONParser =
			new AccountAddressChannelJSONParser();

		return accountAddressChannelJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AccountAddressChannel accountAddressChannel) {
		if (accountAddressChannel == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (accountAddressChannel.getAccountAddressChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountAddressChannelId\": ");

			sb.append(accountAddressChannel.getAccountAddressChannelId());
		}

		if (accountAddressChannel.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(accountAddressChannel.getActions()));
		}

		if (accountAddressChannel.getAddressChannelExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressChannelExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					accountAddressChannel.
						getAddressChannelExternalReferenceCode()));

			sb.append("\"");
		}

		if (accountAddressChannel.getAddressChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressChannelId\": ");

			sb.append(accountAddressChannel.getAddressChannelId());
		}

		if (accountAddressChannel.getAddressExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					accountAddressChannel.getAddressExternalReferenceCode()));

			sb.append("\"");
		}

		if (accountAddressChannel.getAddressId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressId\": ");

			sb.append(accountAddressChannel.getAddressId());
		}

		if (accountAddressChannel.getChannel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channel\": ");

			sb.append(String.valueOf(accountAddressChannel.getChannel()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AccountAddressChannelJSONParser accountAddressChannelJSONParser =
			new AccountAddressChannelJSONParser();

		return accountAddressChannelJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AccountAddressChannel accountAddressChannel) {

		if (accountAddressChannel == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (accountAddressChannel.getAccountAddressChannelId() == null) {
			map.put("accountAddressChannelId", null);
		}
		else {
			map.put(
				"accountAddressChannelId",
				String.valueOf(
					accountAddressChannel.getAccountAddressChannelId()));
		}

		if (accountAddressChannel.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(accountAddressChannel.getActions()));
		}

		if (accountAddressChannel.getAddressChannelExternalReferenceCode() ==
				null) {

			map.put("addressChannelExternalReferenceCode", null);
		}
		else {
			map.put(
				"addressChannelExternalReferenceCode",
				String.valueOf(
					accountAddressChannel.
						getAddressChannelExternalReferenceCode()));
		}

		if (accountAddressChannel.getAddressChannelId() == null) {
			map.put("addressChannelId", null);
		}
		else {
			map.put(
				"addressChannelId",
				String.valueOf(accountAddressChannel.getAddressChannelId()));
		}

		if (accountAddressChannel.getAddressExternalReferenceCode() == null) {
			map.put("addressExternalReferenceCode", null);
		}
		else {
			map.put(
				"addressExternalReferenceCode",
				String.valueOf(
					accountAddressChannel.getAddressExternalReferenceCode()));
		}

		if (accountAddressChannel.getAddressId() == null) {
			map.put("addressId", null);
		}
		else {
			map.put(
				"addressId",
				String.valueOf(accountAddressChannel.getAddressId()));
		}

		if (accountAddressChannel.getChannel() == null) {
			map.put("channel", null);
		}
		else {
			map.put(
				"channel", String.valueOf(accountAddressChannel.getChannel()));
		}

		return map;
	}

	public static class AccountAddressChannelJSONParser
		extends BaseJSONParser<AccountAddressChannel> {

		@Override
		protected AccountAddressChannel createDTO() {
			return new AccountAddressChannel();
		}

		@Override
		protected AccountAddressChannel[] createDTOArray(int size) {
			return new AccountAddressChannel[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "accountAddressChannelId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"addressChannelExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "addressChannelId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "addressExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "addressId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "channel")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AccountAddressChannel accountAddressChannel,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "accountAddressChannelId")) {

				if (jsonParserFieldValue != null) {
					accountAddressChannel.setAccountAddressChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					accountAddressChannel.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"addressChannelExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					accountAddressChannel.
						setAddressChannelExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "addressChannelId")) {
				if (jsonParserFieldValue != null) {
					accountAddressChannel.setAddressChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "addressExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					accountAddressChannel.setAddressExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "addressId")) {
				if (jsonParserFieldValue != null) {
					accountAddressChannel.setAddressId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channel")) {
				if (jsonParserFieldValue != null) {
					accountAddressChannel.setChannel(
						ChannelSerDes.toDTO((String)jsonParserFieldValue));
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