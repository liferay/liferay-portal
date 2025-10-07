/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.client.serdes.v1_0;

import com.liferay.headless.asset.library.client.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.client.dto.v1_0.ConnectedSite;
import com.liferay.headless.asset.library.client.dto.v1_0.UserAccount;
import com.liferay.headless.asset.library.client.dto.v1_0.UserGroup;
import com.liferay.headless.asset.library.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public class AssetLibrarySerDes {

	public static AssetLibrary toDTO(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToDTO(json);
	}

	public static AssetLibrary[] toDTOs(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetLibrary assetLibrary) {
		if (assetLibrary == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (assetLibrary.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(assetLibrary.getActions()));
		}

		if (assetLibrary.getAssetLibraryKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryKey\": ");

			sb.append("\"");

			sb.append(_escape(assetLibrary.getAssetLibraryKey()));

			sb.append("\"");
		}

		if (assetLibrary.getConnectedSites() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"connectedSites\": ");

			sb.append("[");

			for (int i = 0; i < assetLibrary.getConnectedSites().length; i++) {
				sb.append(String.valueOf(assetLibrary.getConnectedSites()[i]));

				if ((i + 1) < assetLibrary.getConnectedSites().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (assetLibrary.getCreatorUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorUserId\": ");

			sb.append(assetLibrary.getCreatorUserId());
		}

		if (assetLibrary.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(assetLibrary.getDateCreated()));

			sb.append("\"");
		}

		if (assetLibrary.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(assetLibrary.getDateModified()));

			sb.append("\"");
		}

		if (assetLibrary.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(assetLibrary.getDescription()));

			sb.append("\"");
		}

		if (assetLibrary.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(assetLibrary.getDescription_i18n()));
		}

		if (assetLibrary.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(assetLibrary.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (assetLibrary.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(assetLibrary.getId());
		}

		if (assetLibrary.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(assetLibrary.getName()));

			sb.append("\"");
		}

		if (assetLibrary.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(assetLibrary.getName_i18n()));
		}

		if (assetLibrary.getNumberOfConnectedSites() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfConnectedSites\": ");

			sb.append(assetLibrary.getNumberOfConnectedSites());
		}

		if (assetLibrary.getNumberOfUserAccounts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfUserAccounts\": ");

			sb.append(assetLibrary.getNumberOfUserAccounts());
		}

		if (assetLibrary.getNumberOfUserGroups() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfUserGroups\": ");

			sb.append(assetLibrary.getNumberOfUserGroups());
		}

		if (assetLibrary.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < assetLibrary.getPermissions().length; i++) {
				sb.append(assetLibrary.getPermissions()[i]);

				if ((i + 1) < assetLibrary.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (assetLibrary.getSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"settings\": ");

			sb.append(String.valueOf(assetLibrary.getSettings()));
		}

		if (assetLibrary.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(assetLibrary.getSiteId());
		}

		if (assetLibrary.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(assetLibrary.getType());

			sb.append("\"");
		}

		if (assetLibrary.getUserAccounts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAccounts\": ");

			sb.append("[");

			for (int i = 0; i < assetLibrary.getUserAccounts().length; i++) {
				sb.append(String.valueOf(assetLibrary.getUserAccounts()[i]));

				if ((i + 1) < assetLibrary.getUserAccounts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (assetLibrary.getUserGroups() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userGroups\": ");

			sb.append("[");

			for (int i = 0; i < assetLibrary.getUserGroups().length; i++) {
				sb.append(String.valueOf(assetLibrary.getUserGroups()[i]));

				if ((i + 1) < assetLibrary.getUserGroups().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AssetLibrary assetLibrary) {
		if (assetLibrary == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (assetLibrary.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(assetLibrary.getActions()));
		}

		if (assetLibrary.getAssetLibraryKey() == null) {
			map.put("assetLibraryKey", null);
		}
		else {
			map.put(
				"assetLibraryKey",
				String.valueOf(assetLibrary.getAssetLibraryKey()));
		}

		if (assetLibrary.getConnectedSites() == null) {
			map.put("connectedSites", null);
		}
		else {
			map.put(
				"connectedSites",
				String.valueOf(assetLibrary.getConnectedSites()));
		}

		if (assetLibrary.getCreatorUserId() == null) {
			map.put("creatorUserId", null);
		}
		else {
			map.put(
				"creatorUserId",
				String.valueOf(assetLibrary.getCreatorUserId()));
		}

		if (assetLibrary.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(assetLibrary.getDateCreated()));
		}

		if (assetLibrary.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(assetLibrary.getDateModified()));
		}

		if (assetLibrary.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(assetLibrary.getDescription()));
		}

		if (assetLibrary.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(assetLibrary.getDescription_i18n()));
		}

		if (assetLibrary.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(assetLibrary.getExternalReferenceCode()));
		}

		if (assetLibrary.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(assetLibrary.getId()));
		}

		if (assetLibrary.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(assetLibrary.getName()));
		}

		if (assetLibrary.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(assetLibrary.getName_i18n()));
		}

		if (assetLibrary.getNumberOfConnectedSites() == null) {
			map.put("numberOfConnectedSites", null);
		}
		else {
			map.put(
				"numberOfConnectedSites",
				String.valueOf(assetLibrary.getNumberOfConnectedSites()));
		}

		if (assetLibrary.getNumberOfUserAccounts() == null) {
			map.put("numberOfUserAccounts", null);
		}
		else {
			map.put(
				"numberOfUserAccounts",
				String.valueOf(assetLibrary.getNumberOfUserAccounts()));
		}

		if (assetLibrary.getNumberOfUserGroups() == null) {
			map.put("numberOfUserGroups", null);
		}
		else {
			map.put(
				"numberOfUserGroups",
				String.valueOf(assetLibrary.getNumberOfUserGroups()));
		}

		if (assetLibrary.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions", String.valueOf(assetLibrary.getPermissions()));
		}

		if (assetLibrary.getSettings() == null) {
			map.put("settings", null);
		}
		else {
			map.put("settings", String.valueOf(assetLibrary.getSettings()));
		}

		if (assetLibrary.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(assetLibrary.getSiteId()));
		}

		if (assetLibrary.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(assetLibrary.getType()));
		}

		if (assetLibrary.getUserAccounts() == null) {
			map.put("userAccounts", null);
		}
		else {
			map.put(
				"userAccounts", String.valueOf(assetLibrary.getUserAccounts()));
		}

		if (assetLibrary.getUserGroups() == null) {
			map.put("userGroups", null);
		}
		else {
			map.put("userGroups", String.valueOf(assetLibrary.getUserGroups()));
		}

		return map;
	}

	public static class AssetLibraryJSONParser
		extends BaseJSONParser<AssetLibrary> {

		@Override
		protected AssetLibrary createDTO() {
			return new AssetLibrary();
		}

		@Override
		protected AssetLibrary[] createDTOArray(int size) {
			return new AssetLibrary[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "connectedSites")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creatorUserId")) {
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
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
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
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfConnectedSites")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfUserAccounts")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfUserGroups")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userAccounts")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userGroups")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetLibrary assetLibrary, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setAssetLibraryKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "connectedSites")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ConnectedSite[] connectedSitesArray =
						new ConnectedSite[jsonParserFieldValues.length];

					for (int i = 0; i < connectedSitesArray.length; i++) {
						connectedSitesArray[i] = ConnectedSiteSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					assetLibrary.setConnectedSites(connectedSitesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creatorUserId")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setCreatorUserId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					assetLibrary.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfConnectedSites")) {

				if (jsonParserFieldValue != null) {
					assetLibrary.setNumberOfConnectedSites(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfUserAccounts")) {

				if (jsonParserFieldValue != null) {
					assetLibrary.setNumberOfUserAccounts(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfUserGroups")) {

				if (jsonParserFieldValue != null) {
					assetLibrary.setNumberOfUserGroups(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.asset.library.client.permission.
						Permission[] permissionsArray = new
						com.liferay.headless.asset.library.client.permission.
							Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.asset.library.client.
								permission.Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					assetLibrary.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setSettings(
						SettingsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setType(
						AssetLibrary.Type.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userAccounts")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					UserAccount[] userAccountsArray =
						new UserAccount[jsonParserFieldValues.length];

					for (int i = 0; i < userAccountsArray.length; i++) {
						userAccountsArray[i] = UserAccountSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					assetLibrary.setUserAccounts(userAccountsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userGroups")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					UserGroup[] userGroupsArray =
						new UserGroup[jsonParserFieldValues.length];

					for (int i = 0; i < userGroupsArray.length; i++) {
						userGroupsArray[i] = UserGroupSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					assetLibrary.setUserGroups(userGroupsArray);
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