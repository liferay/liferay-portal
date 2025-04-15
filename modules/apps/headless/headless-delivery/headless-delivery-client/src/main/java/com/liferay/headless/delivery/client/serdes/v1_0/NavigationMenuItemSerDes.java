/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.NavigationMenuItem;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class NavigationMenuItemSerDes {

	public static NavigationMenuItem toDTO(String json) {
		NavigationMenuItemJSONParser navigationMenuItemJSONParser =
			new NavigationMenuItemJSONParser();

		return navigationMenuItemJSONParser.parseToDTO(json);
	}

	public static NavigationMenuItem[] toDTOs(String json) {
		NavigationMenuItemJSONParser navigationMenuItemJSONParser =
			new NavigationMenuItemJSONParser();

		return navigationMenuItemJSONParser.parseToDTOs(json);
	}

	public static String toJSON(NavigationMenuItem navigationMenuItem) {
		if (navigationMenuItem == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (navigationMenuItem.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0;
				 i < navigationMenuItem.getAvailableLanguages().length; i++) {

				sb.append(
					_toJSON(navigationMenuItem.getAvailableLanguages()[i]));

				if ((i + 1) <
						navigationMenuItem.getAvailableLanguages().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (navigationMenuItem.getContentURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentURL\": ");

			sb.append("\"");

			sb.append(_escape(navigationMenuItem.getContentURL()));

			sb.append("\"");
		}

		if (navigationMenuItem.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(navigationMenuItem.getCreator()));
		}

		if (navigationMenuItem.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < navigationMenuItem.getCustomFields().length;
				 i++) {

				sb.append(navigationMenuItem.getCustomFields()[i]);

				if ((i + 1) < navigationMenuItem.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (navigationMenuItem.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					navigationMenuItem.getDateCreated()));

			sb.append("\"");
		}

		if (navigationMenuItem.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					navigationMenuItem.getDateModified()));

			sb.append("\"");
		}

		if (navigationMenuItem.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(navigationMenuItem.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (navigationMenuItem.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(navigationMenuItem.getId());
		}

		if (navigationMenuItem.getLink() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"link\": ");

			sb.append("\"");

			sb.append(_escape(navigationMenuItem.getLink()));

			sb.append("\"");
		}

		if (navigationMenuItem.getLink_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"link_i18n\": ");

			sb.append(_toJSON(navigationMenuItem.getLink_i18n()));
		}

		if (navigationMenuItem.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(navigationMenuItem.getName()));

			sb.append("\"");
		}

		if (navigationMenuItem.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(navigationMenuItem.getName_i18n()));
		}

		if (navigationMenuItem.getNavigationMenuItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuItems\": ");

			sb.append("[");

			for (int i = 0;
				 i < navigationMenuItem.getNavigationMenuItems().length; i++) {

				sb.append(
					String.valueOf(
						navigationMenuItem.getNavigationMenuItems()[i]));

				if ((i + 1) <
						navigationMenuItem.getNavigationMenuItems().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (navigationMenuItem.getParentNavigationMenuId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentNavigationMenuId\": ");

			sb.append(navigationMenuItem.getParentNavigationMenuId());
		}

		if (navigationMenuItem.getSitePageURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitePageURL\": ");

			sb.append("\"");

			sb.append(_escape(navigationMenuItem.getSitePageURL()));

			sb.append("\"");
		}

		if (navigationMenuItem.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(navigationMenuItem.getType()));

			sb.append("\"");
		}

		if (navigationMenuItem.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(navigationMenuItem.getUrl()));

			sb.append("\"");
		}

		if (navigationMenuItem.getUseCustomName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useCustomName\": ");

			sb.append(navigationMenuItem.getUseCustomName());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		NavigationMenuItemJSONParser navigationMenuItemJSONParser =
			new NavigationMenuItemJSONParser();

		return navigationMenuItemJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		NavigationMenuItem navigationMenuItem) {

		if (navigationMenuItem == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (navigationMenuItem.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(navigationMenuItem.getAvailableLanguages()));
		}

		if (navigationMenuItem.getContentURL() == null) {
			map.put("contentURL", null);
		}
		else {
			map.put(
				"contentURL",
				String.valueOf(navigationMenuItem.getContentURL()));
		}

		if (navigationMenuItem.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(navigationMenuItem.getCreator()));
		}

		if (navigationMenuItem.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(navigationMenuItem.getCustomFields()));
		}

		if (navigationMenuItem.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					navigationMenuItem.getDateCreated()));
		}

		if (navigationMenuItem.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					navigationMenuItem.getDateModified()));
		}

		if (navigationMenuItem.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(navigationMenuItem.getExternalReferenceCode()));
		}

		if (navigationMenuItem.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(navigationMenuItem.getId()));
		}

		if (navigationMenuItem.getLink() == null) {
			map.put("link", null);
		}
		else {
			map.put("link", String.valueOf(navigationMenuItem.getLink()));
		}

		if (navigationMenuItem.getLink_i18n() == null) {
			map.put("link_i18n", null);
		}
		else {
			map.put(
				"link_i18n", String.valueOf(navigationMenuItem.getLink_i18n()));
		}

		if (navigationMenuItem.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(navigationMenuItem.getName()));
		}

		if (navigationMenuItem.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put(
				"name_i18n", String.valueOf(navigationMenuItem.getName_i18n()));
		}

		if (navigationMenuItem.getNavigationMenuItems() == null) {
			map.put("navigationMenuItems", null);
		}
		else {
			map.put(
				"navigationMenuItems",
				String.valueOf(navigationMenuItem.getNavigationMenuItems()));
		}

		if (navigationMenuItem.getParentNavigationMenuId() == null) {
			map.put("parentNavigationMenuId", null);
		}
		else {
			map.put(
				"parentNavigationMenuId",
				String.valueOf(navigationMenuItem.getParentNavigationMenuId()));
		}

		if (navigationMenuItem.getSitePageURL() == null) {
			map.put("sitePageURL", null);
		}
		else {
			map.put(
				"sitePageURL",
				String.valueOf(navigationMenuItem.getSitePageURL()));
		}

		if (navigationMenuItem.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(navigationMenuItem.getType()));
		}

		if (navigationMenuItem.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put("url", String.valueOf(navigationMenuItem.getUrl()));
		}

		if (navigationMenuItem.getUseCustomName() == null) {
			map.put("useCustomName", null);
		}
		else {
			map.put(
				"useCustomName",
				String.valueOf(navigationMenuItem.getUseCustomName()));
		}

		return map;
	}

	public static class NavigationMenuItemJSONParser
		extends BaseJSONParser<NavigationMenuItem> {

		@Override
		protected NavigationMenuItem createDTO() {
			return new NavigationMenuItem();
		}

		@Override
		protected NavigationMenuItem[] createDTOArray(int size) {
			return new NavigationMenuItem[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "availableLanguages")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentURL")) {
				return false;
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
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "link")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "link_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationMenuItems")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentNavigationMenuId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sitePageURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "useCustomName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			NavigationMenuItem navigationMenuItem, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "availableLanguages")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentURL")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setContentURL(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.delivery.client.custom.field.
						CustomField[] customFieldsArray = new
						com.liferay.headless.delivery.client.custom.field.
							CustomField[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.delivery.client.custom.field.
								CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					navigationMenuItem.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					navigationMenuItem.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "link")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setLink((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "link_i18n")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setLink_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationMenuItems")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					NavigationMenuItem[] navigationMenuItemsArray =
						new NavigationMenuItem[jsonParserFieldValues.length];

					for (int i = 0; i < navigationMenuItemsArray.length; i++) {
						navigationMenuItemsArray[i] =
							NavigationMenuItemSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					navigationMenuItem.setNavigationMenuItems(
						navigationMenuItemsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentNavigationMenuId")) {

				if (jsonParserFieldValue != null) {
					navigationMenuItem.setParentNavigationMenuId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sitePageURL")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setSitePageURL(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setUrl((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "useCustomName")) {
				if (jsonParserFieldValue != null) {
					navigationMenuItem.setUseCustomName(
						(Boolean)jsonParserFieldValue);
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