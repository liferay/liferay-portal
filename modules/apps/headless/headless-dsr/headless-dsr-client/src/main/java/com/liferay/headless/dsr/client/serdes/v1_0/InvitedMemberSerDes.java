/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.dsr.client.serdes.v1_0;

import com.liferay.headless.dsr.client.dto.v1_0.InvitedMember;
import com.liferay.headless.dsr.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Stefano Motta
 * @generated
 */
@Generated("")
public class InvitedMemberSerDes {

	public static InvitedMember toDTO(String json) {
		InvitedMemberJSONParser invitedMemberJSONParser =
			new InvitedMemberJSONParser();

		return invitedMemberJSONParser.parseToDTO(json);
	}

	public static InvitedMember[] toDTOs(String json) {
		InvitedMemberJSONParser invitedMemberJSONParser =
			new InvitedMemberJSONParser();

		return invitedMemberJSONParser.parseToDTOs(json);
	}

	public static String toJSON(InvitedMember invitedMember) {
		if (invitedMember == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (invitedMember.getEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(invitedMember.getEmailAddress()));

			sb.append("\"");
		}

		if (invitedMember.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(invitedMember.getId());
		}

		if (invitedMember.getMembershipExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"membershipExpirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					invitedMember.getMembershipExpirationDate()));

			sb.append("\"");
		}

		if (invitedMember.getOwnerId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ownerId\": ");

			sb.append(invitedMember.getOwnerId());
		}

		if (invitedMember.getRoleKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleKey\": ");

			sb.append("\"");

			sb.append(_escape(invitedMember.getRoleKey()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		InvitedMemberJSONParser invitedMemberJSONParser =
			new InvitedMemberJSONParser();

		return invitedMemberJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(InvitedMember invitedMember) {
		if (invitedMember == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (invitedMember.getEmailAddress() == null) {
			map.put("emailAddress", null);
		}
		else {
			map.put(
				"emailAddress",
				String.valueOf(invitedMember.getEmailAddress()));
		}

		if (invitedMember.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(invitedMember.getId()));
		}

		if (invitedMember.getMembershipExpirationDate() == null) {
			map.put("membershipExpirationDate", null);
		}
		else {
			map.put(
				"membershipExpirationDate",
				liferayToJSONDateFormat.format(
					invitedMember.getMembershipExpirationDate()));
		}

		if (invitedMember.getOwnerId() == null) {
			map.put("ownerId", null);
		}
		else {
			map.put("ownerId", String.valueOf(invitedMember.getOwnerId()));
		}

		if (invitedMember.getRoleKey() == null) {
			map.put("roleKey", null);
		}
		else {
			map.put("roleKey", String.valueOf(invitedMember.getRoleKey()));
		}

		return map;
	}

	public static class InvitedMemberJSONParser
		extends BaseJSONParser<InvitedMember> {

		@Override
		protected InvitedMember createDTO() {
			return new InvitedMember();
		}

		@Override
		protected InvitedMember[] createDTOArray(int size) {
			return new InvitedMember[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "membershipExpirationDate")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ownerId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleKey")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			InvitedMember invitedMember, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				if (jsonParserFieldValue != null) {
					invitedMember.setEmailAddress((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					invitedMember.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "membershipExpirationDate")) {

				if (jsonParserFieldValue != null) {
					invitedMember.setMembershipExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ownerId")) {
				if (jsonParserFieldValue != null) {
					invitedMember.setOwnerId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleKey")) {
				if (jsonParserFieldValue != null) {
					invitedMember.setRoleKey((String)jsonParserFieldValue);
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:1084540055