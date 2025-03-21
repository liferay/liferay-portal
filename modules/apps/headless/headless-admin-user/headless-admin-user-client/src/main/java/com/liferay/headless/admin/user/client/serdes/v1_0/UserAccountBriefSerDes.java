/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.UserAccountBrief;
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
public class UserAccountBriefSerDes {

	public static UserAccountBrief toDTO(String json) {
		UserAccountBriefJSONParser userAccountBriefJSONParser =
			new UserAccountBriefJSONParser();

		return userAccountBriefJSONParser.parseToDTO(json);
	}

	public static UserAccountBrief[] toDTOs(String json) {
		UserAccountBriefJSONParser userAccountBriefJSONParser =
			new UserAccountBriefJSONParser();

		return userAccountBriefJSONParser.parseToDTOs(json);
	}

	public static String toJSON(UserAccountBrief userAccountBrief) {
		if (userAccountBrief == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (userAccountBrief.getAlternateName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"alternateName\": ");

			sb.append("\"");

			sb.append(_escape(userAccountBrief.getAlternateName()));

			sb.append("\"");
		}

		if (userAccountBrief.getEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(userAccountBrief.getEmailAddress()));

			sb.append("\"");
		}

		if (userAccountBrief.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(userAccountBrief.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (userAccountBrief.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(userAccountBrief.getId());
		}

		if (userAccountBrief.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(userAccountBrief.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UserAccountBriefJSONParser userAccountBriefJSONParser =
			new UserAccountBriefJSONParser();

		return userAccountBriefJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(UserAccountBrief userAccountBrief) {
		if (userAccountBrief == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (userAccountBrief.getAlternateName() == null) {
			map.put("alternateName", null);
		}
		else {
			map.put(
				"alternateName",
				String.valueOf(userAccountBrief.getAlternateName()));
		}

		if (userAccountBrief.getEmailAddress() == null) {
			map.put("emailAddress", null);
		}
		else {
			map.put(
				"emailAddress",
				String.valueOf(userAccountBrief.getEmailAddress()));
		}

		if (userAccountBrief.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(userAccountBrief.getExternalReferenceCode()));
		}

		if (userAccountBrief.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(userAccountBrief.getId()));
		}

		if (userAccountBrief.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(userAccountBrief.getName()));
		}

		return map;
	}

	public static class UserAccountBriefJSONParser
		extends BaseJSONParser<UserAccountBrief> {

		@Override
		protected UserAccountBrief createDTO() {
			return new UserAccountBrief();
		}

		@Override
		protected UserAccountBrief[] createDTOArray(int size) {
			return new UserAccountBrief[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "alternateName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
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

			return false;
		}

		@Override
		protected void setField(
			UserAccountBrief userAccountBrief, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "alternateName")) {
				if (jsonParserFieldValue != null) {
					userAccountBrief.setAlternateName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				if (jsonParserFieldValue != null) {
					userAccountBrief.setEmailAddress(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					userAccountBrief.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					userAccountBrief.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					userAccountBrief.setName((String)jsonParserFieldValue);
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