/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserAccountBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.UserGroup;
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

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (userGroup.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(userGroup.getActions()));
		}

		if (userGroup.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(userGroup.getCreator()));
		}

		if (userGroup.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(userGroup.getDateCreated()));

			sb.append("\"");
		}

		if (userGroup.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(userGroup.getDateModified()));

			sb.append("\"");
		}

		if (userGroup.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(userGroup.getDescription()));

			sb.append("\"");
		}

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

		if (userGroup.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < userGroup.getPermissions().length; i++) {
				sb.append(userGroup.getPermissions()[i]);

				if ((i + 1) < userGroup.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (userGroup.getRoleBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleBriefs\": ");

			sb.append("[");

			for (int i = 0; i < userGroup.getRoleBriefs().length; i++) {
				sb.append(String.valueOf(userGroup.getRoleBriefs()[i]));

				if ((i + 1) < userGroup.getRoleBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (userGroup.getUserAccountBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAccountBriefs\": ");

			sb.append("[");

			for (int i = 0; i < userGroup.getUserAccountBriefs().length; i++) {
				sb.append(String.valueOf(userGroup.getUserAccountBriefs()[i]));

				if ((i + 1) < userGroup.getUserAccountBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (userGroup.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(userGroup.getActions()));
		}

		if (userGroup.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(userGroup.getCreator()));
		}

		if (userGroup.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(userGroup.getDateCreated()));
		}

		if (userGroup.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(userGroup.getDateModified()));
		}

		if (userGroup.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(userGroup.getDescription()));
		}

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

		if (userGroup.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put("permissions", String.valueOf(userGroup.getPermissions()));
		}

		if (userGroup.getRoleBriefs() == null) {
			map.put("roleBriefs", null);
		}
		else {
			map.put("roleBriefs", String.valueOf(userGroup.getRoleBriefs()));
		}

		if (userGroup.getUserAccountBriefs() == null) {
			map.put("userAccountBriefs", null);
		}
		else {
			map.put(
				"userAccountBriefs",
				String.valueOf(userGroup.getUserAccountBriefs()));
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
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
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
			else if (Objects.equals(jsonParserFieldName, "roleBriefs")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userAccountBriefs")) {
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

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					userGroup.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					userGroup.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					userGroup.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					userGroup.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					userGroup.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

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

					userGroup.setPermissions(permissionsArray);
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

					userGroup.setRoleBriefs(roleBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userAccountBriefs")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					UserAccountBrief[] userAccountBriefsArray =
						new UserAccountBrief[jsonParserFieldValues.length];

					for (int i = 0; i < userAccountBriefsArray.length; i++) {
						userAccountBriefsArray[i] =
							UserAccountBriefSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					userGroup.setUserAccountBriefs(userAccountBriefsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "usersCount")) {
				if (jsonParserFieldValue != null) {
					userGroup.setUsersCount(
						Integer.valueOf((String)jsonParserFieldValue));
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