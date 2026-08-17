/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.client.serdes.v1_0;

import com.liferay.headless.cmp.client.dto.v1_0.UserGroup;
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
public class UserGroupSerDes {

	public static UserGroup toDTO(String json) {
		UserGroupJSONParser userGroupJSONParser = new UserGroupJSONParser();

		return userGroupJSONParser.parseToDTO(json);
	}

	public static UserGroup[] toDTOs(String json) {
		UserGroupJSONParser userGroupJSONParser = new UserGroupJSONParser();

		return userGroupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(UserGroup userGroup) {
		if (userGroup == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (userGroup.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(userGroup.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (userGroup.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(userGroup.getId());
		}

		if (userGroup.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(userGroup.getName()));

			sb.append("\"");
		}

		if (userGroup.getUsersCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"usersCount\": ");

			sb.append(userGroup.getUsersCount());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		UserGroupJSONParser userGroupJSONParser = new UserGroupJSONParser();

		return userGroupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(UserGroup userGroup) {
		if (userGroup == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (userGroup.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(userGroup.getExternalReferenceCode()));
		}

		if (userGroup.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(userGroup.getId()));
		}

		if (userGroup.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(userGroup.getName()));
		}

		if (userGroup.getUsersCount() == null) {
			map.put("usersCount", null);
		}
		else {
			map.put("usersCount", String.valueOf(userGroup.getUsersCount()));
		}

		return map;
	}

	public static class UserGroupJSONParser extends BaseJSONParser<UserGroup> {

		@Override
		protected UserGroup createDTO() {
			return new UserGroup();
		}

		@Override
		protected UserGroup[] createDTOArray(int size) {
			return new UserGroup[size];
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
			else if (Objects.equals(jsonParserFieldName, "usersCount")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			UserGroup userGroup, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					userGroup.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					userGroup.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					userGroup.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "usersCount")) {
				if (jsonParserFieldValue != null) {
					userGroup.setUsersCount(
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
// LIFERAY-REST-BUILDER-HASH:-1365667513