/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
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

		if (displayPageTemplate.getContentTypeReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentTypeReference\": ");

			sb.append(
				String.valueOf(displayPageTemplate.getContentTypeReference()));
		}

		if (displayPageTemplate.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(displayPageTemplate.getCreator());
		}

		if (displayPageTemplate.getCreatorExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(displayPageTemplate.getCreatorExternalReferenceCode()));

			sb.append("\"");
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

		if (displayPageTemplate.getDatePublished() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					displayPageTemplate.getDatePublished()));

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

		if (displayPageTemplate.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(displayPageTemplate.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (displayPageTemplate.getFriendlyUrlHistory() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlHistory\": ");

			sb.append(
				String.valueOf(displayPageTemplate.getFriendlyUrlHistory()));
		}

		if (displayPageTemplate.getFriendlyUrlPath_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath_i18n\": ");

			sb.append(_toJSON(displayPageTemplate.getFriendlyUrlPath_i18n()));
		}

		if (displayPageTemplate.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(displayPageTemplate.getKey()));

			sb.append("\"");
		}

		if (displayPageTemplate.getMarkedAsDefault() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"markedAsDefault\": ");

			sb.append(displayPageTemplate.getMarkedAsDefault());
		}

		if (displayPageTemplate.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(displayPageTemplate.getName()));

			sb.append("\"");
		}

		if (displayPageTemplate.getPageSpecifications() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSpecifications\": ");

			sb.append("[");

			for (int i = 0;
				 i < displayPageTemplate.getPageSpecifications().length; i++) {

				sb.append(
					String.valueOf(
						displayPageTemplate.getPageSpecifications()[i]));

				if ((i + 1) <
						displayPageTemplate.getPageSpecifications().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (displayPageTemplate.getParentFolder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentFolder\": ");

			sb.append(String.valueOf(displayPageTemplate.getParentFolder()));
		}

		if (displayPageTemplate.getThumbnail() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnail\": ");

			sb.append(String.valueOf(displayPageTemplate.getThumbnail()));
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

		if (displayPageTemplate.getContentTypeReference() == null) {
			map.put("contentTypeReference", null);
		}
		else {
			map.put(
				"contentTypeReference",
				String.valueOf(displayPageTemplate.getContentTypeReference()));
		}

		if (displayPageTemplate.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator", String.valueOf(displayPageTemplate.getCreator()));
		}

		if (displayPageTemplate.getCreatorExternalReferenceCode() == null) {
			map.put("creatorExternalReferenceCode", null);
		}
		else {
			map.put(
				"creatorExternalReferenceCode",
				String.valueOf(
					displayPageTemplate.getCreatorExternalReferenceCode()));
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

		if (displayPageTemplate.getDatePublished() == null) {
			map.put("datePublished", null);
		}
		else {
			map.put(
				"datePublished",
				liferayToJSONDateFormat.format(
					displayPageTemplate.getDatePublished()));
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

		if (displayPageTemplate.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(displayPageTemplate.getExternalReferenceCode()));
		}

		if (displayPageTemplate.getFriendlyUrlHistory() == null) {
			map.put("friendlyUrlHistory", null);
		}
		else {
			map.put(
				"friendlyUrlHistory",
				String.valueOf(displayPageTemplate.getFriendlyUrlHistory()));
		}

		if (displayPageTemplate.getFriendlyUrlPath_i18n() == null) {
			map.put("friendlyUrlPath_i18n", null);
		}
		else {
			map.put(
				"friendlyUrlPath_i18n",
				String.valueOf(displayPageTemplate.getFriendlyUrlPath_i18n()));
		}

		if (displayPageTemplate.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(displayPageTemplate.getKey()));
		}

		if (displayPageTemplate.getMarkedAsDefault() == null) {
			map.put("markedAsDefault", null);
		}
		else {
			map.put(
				"markedAsDefault",
				String.valueOf(displayPageTemplate.getMarkedAsDefault()));
		}

		if (displayPageTemplate.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(displayPageTemplate.getName()));
		}

		if (displayPageTemplate.getPageSpecifications() == null) {
			map.put("pageSpecifications", null);
		}
		else {
			map.put(
				"pageSpecifications",
				String.valueOf(displayPageTemplate.getPageSpecifications()));
		}

		if (displayPageTemplate.getParentFolder() == null) {
			map.put("parentFolder", null);
		}
		else {
			map.put(
				"parentFolder",
				String.valueOf(displayPageTemplate.getParentFolder()));
		}

		if (displayPageTemplate.getThumbnail() == null) {
			map.put("thumbnail", null);
		}
		else {
			map.put(
				"thumbnail",
				String.valueOf(displayPageTemplate.getThumbnail()));
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
			if (Objects.equals(jsonParserFieldName, "contentTypeReference")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "creatorExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "displayPageTemplateSettings")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlHistory")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlPath_i18n")) {

				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "markedAsDefault")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageSpecifications")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parentFolder")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
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

			if (Objects.equals(jsonParserFieldName, "contentTypeReference")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setContentTypeReference(
						ClassSubtypeReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "creatorExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					displayPageTemplate.setCreatorExternalReferenceCode(
						(String)jsonParserFieldValue);
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
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setDatePublished(
						toDate((String)jsonParserFieldValue));
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
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					displayPageTemplate.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlHistory")) {

				if (jsonParserFieldValue != null) {
					displayPageTemplate.setFriendlyUrlHistory(
						FriendlyUrlHistorySerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlPath_i18n")) {

				if (jsonParserFieldValue != null) {
					displayPageTemplate.setFriendlyUrlPath_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "markedAsDefault")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setMarkedAsDefault(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageSpecifications")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PageSpecification[] pageSpecificationsArray =
						new PageSpecification[jsonParserFieldValues.length];

					for (int i = 0; i < pageSpecificationsArray.length; i++) {
						pageSpecificationsArray[i] =
							PageSpecificationSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					displayPageTemplate.setPageSpecifications(
						pageSpecificationsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentFolder")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setParentFolder(
						DisplayPageTemplateFolderSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplate.setThumbnail(
						ItemExternalReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
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