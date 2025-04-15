/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.serdes.v1_0;

import com.liferay.headless.admin.content.client.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.content.client.json.BaseJSONParser;

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
public class DisplayPageTemplateSerDes {

	public static DisplayPageTemplate toDTO(String json) {
		DisplayPageTemplateJSONParser displayPageTemplateJSONParser =
			new DisplayPageTemplateJSONParser();

		return displayPageTemplateJSONParser.parseToDTO(json);
	}

	public static DisplayPageTemplate[] toDTOs(String json) {
		DisplayPageTemplateJSONParser displayPageTemplateJSONParser =
			new DisplayPageTemplateJSONParser();

		return displayPageTemplateJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DisplayPageTemplate displayPageTemplate) {
		if (displayPageTemplate == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (displayPageTemplate.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(displayPageTemplate.getActions()));
		}

		if (displayPageTemplate.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0;
				 i < displayPageTemplate.getAvailableLanguages().length; i++) {

				sb.append(
					_toJSON(displayPageTemplate.getAvailableLanguages()[i]));

				if ((i + 1) <
						displayPageTemplate.getAvailableLanguages().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (displayPageTemplate.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(displayPageTemplate.getCreator());
		}

		if (displayPageTemplate.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < displayPageTemplate.getCustomFields().length;
				 i++) {

				sb.append(displayPageTemplate.getCustomFields()[i]);

				if ((i + 1) < displayPageTemplate.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (displayPageTemplate.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					displayPageTemplate.getDateCreated()));

			sb.append("\"");
		}

		if (displayPageTemplate.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					displayPageTemplate.getDateModified()));

			sb.append("\"");
		}

		if (displayPageTemplate.getDisplayPageTemplateKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayPageTemplateKey\": ");

			sb.append("\"");

			sb.append(_escape(displayPageTemplate.getDisplayPageTemplateKey()));

			sb.append("\"");
		}

		if (displayPageTemplate.getDisplayPageTemplateSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayPageTemplateSettings\": ");

			sb.append(
				String.valueOf(
					displayPageTemplate.getDisplayPageTemplateSettings()));
		}

		if (displayPageTemplate.getMarkedAsDefault() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"markedAsDefault\": ");

			sb.append(displayPageTemplate.getMarkedAsDefault());
		}

		if (displayPageTemplate.getPageDefinition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageDefinition\": ");

			sb.append(displayPageTemplate.getPageDefinition());
		}

		if (displayPageTemplate.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(displayPageTemplate.getSiteId());
		}

		if (displayPageTemplate.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(displayPageTemplate.getTitle()));

			sb.append("\"");
		}

		if (displayPageTemplate.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(displayPageTemplate.getUuid()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DisplayPageTemplateJSONParser displayPageTemplateJSONParser =
			new DisplayPageTemplateJSONParser();

		return displayPageTemplateJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DisplayPageTemplate displayPageTemplate) {

		if (displayPageTemplate == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (displayPageTemplate.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(displayPageTemplate.getActions()));
		}

		if (displayPageTemplate.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(displayPageTemplate.getAvailableLanguages()));
		}

		if (displayPageTemplate.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator", String.valueOf(displayPageTemplate.getCreator()));
		}

		if (displayPageTemplate.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(displayPageTemplate.getCustomFields()));
		}

		if (displayPageTemplate.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					displayPageTemplate.getDateCreated()));
		}

		if (displayPageTemplate.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					displayPageTemplate.getDateModified()));
		}

		if (displayPageTemplate.getDisplayPageTemplateKey() == null) {
			map.put("displayPageTemplateKey", null);
		}
		else {
			map.put(
				"displayPageTemplateKey",
				String.valueOf(
					displayPageTemplate.getDisplayPageTemplateKey()));
		}

		if (displayPageTemplate.getDisplayPageTemplateSettings() == null) {
			map.put("displayPageTemplateSettings", null);
		}
		else {
			map.put(
				"displayPageTemplateSettings",
				String.valueOf(
					displayPageTemplate.getDisplayPageTemplateSettings()));
		}

		if (displayPageTemplate.getMarkedAsDefault() == null) {
			map.put("markedAsDefault", null);
		}
		else {
			map.put(
				"markedAsDefault",
				String.valueOf(displayPageTemplate.getMarkedAsDefault()));
		}

		if (displayPageTemplate.getPageDefinition() == null) {
			map.put("pageDefinition", null);
		}
		else {
			map.put(
				"pageDefinition",
				String.valueOf(displayPageTemplate.getPageDefinition()));
		}

		if (displayPageTemplate.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(displayPageTemplate.getSiteId()));
		}

		if (displayPageTemplate.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(displayPageTemplate.getTitle()));
		}

		if (displayPageTemplate.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put("uuid", String.valueOf(displayPageTemplate.getUuid()));
		}

		return map;
	}

	public static class DisplayPageTemplateJSONParser
		extends BaseJSONParser<DisplayPageTemplate> {

		@Override
		protected DisplayPageTemplate createDTO() {
			return new DisplayPageTemplate();
		}

		@Override
		protected DisplayPageTemplate[] createDTOArray(int size) {
			return new DisplayPageTemplate[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

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
						jsonParserFieldName, "displayPageTemplateKey")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "displayPageTemplateSettings")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "markedAsDefault")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageDefinition")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DisplayPageTemplate displayPageTemplate, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				if (jsonParserFieldValue != null) {
					displayPageTemplate.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.admin.content.client.custom.field.
						CustomField[] customFieldsArray = new
						com.liferay.headless.admin.content.client.custom.field.
							CustomField[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.admin.content.client.custom.
								field.CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					displayPageTemplate.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "displayPageTemplateKey")) {

				if (jsonParserFieldValue != null) {
					displayPageTemplate.setDisplayPageTemplateKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "displayPageTemplateSettings")) {

				if (jsonParserFieldValue != null) {
					displayPageTemplate.setDisplayPageTemplateSettings(
						DisplayPageTemplateSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "markedAsDefault")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setMarkedAsDefault(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageDefinition")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setPageDefinition(
						PageDefinitionSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setUuid((String)jsonParserFieldValue);
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