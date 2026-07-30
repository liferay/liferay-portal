/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.client.serdes.v1_0;

import com.liferay.headless.cmp.client.dto.v1_0.UserAccount;
import com.liferay.headless.cmp.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
public class UserAccountSerDes {

	public static UserAccount toDTO(String json) {
		UserAccountJSONParser userAccountJSONParser =
			new UserAccountJSONParser();

		return userAccountJSONParser.parseToDTO(json);
	}

	public static UserAccount[] toDTOs(String json) {
		UserAccountJSONParser userAccountJSONParser =
			new UserAccountJSONParser();

		return userAccountJSONParser.parseToDTOs(json);
	}

	public static String toJSON(UserAccount userAccount) {
		if (userAccount == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (userAccount.getEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getEmailAddress()));

			sb.append("\"");
		}

		if (userAccount.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (userAccount.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(userAccount.getId());
		}

		if (userAccount.getImage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"image\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getImage()));

			sb.append("\"");
		}

		if (userAccount.getImageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageId\": ");

			sb.append(userAccount.getImageId());
		}

		if (userAccount.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(userAccount.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UserAccountJSONParser userAccountJSONParser =
			new UserAccountJSONParser();

		return userAccountJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(UserAccount userAccount) {
		if (userAccount == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (userAccount.getEmailAddress() == null) {
			map.put("emailAddress", null);
		}
		else {
			map.put(
				"emailAddress", String.valueOf(userAccount.getEmailAddress()));
		}

		if (userAccount.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(userAccount.getExternalReferenceCode()));
		}

		if (userAccount.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(userAccount.getId()));
		}

		if (userAccount.getImage() == null) {
			map.put("image", null);
		}
		else {
			map.put("image", String.valueOf(userAccount.getImage()));
		}

		if (userAccount.getImageId() == null) {
			map.put("imageId", null);
		}
		else {
			map.put("imageId", String.valueOf(userAccount.getImageId()));
		}

		if (userAccount.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(userAccount.getName()));
		}

		return map;
	}

	public static class UserAccountJSONParser
		extends BaseJSONParser<UserAccount> {

		@Override
		protected UserAccount createDTO() {
			return new UserAccount();
		}

		@Override
		protected UserAccount[] createDTOArray(int size) {
			return new UserAccount[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "image")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "imageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			UserAccount userAccount, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				if (jsonParserFieldValue != null) {
					userAccount.setEmailAddress((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					userAccount.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					userAccount.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "image")) {
				if (jsonParserFieldValue != null) {
					userAccount.setImage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "imageId")) {
				if (jsonParserFieldValue != null) {
					userAccount.setImageId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					userAccount.setName((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1213540765