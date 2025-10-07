/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageTemplate;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class WidgetPageTemplateSerDes {

	public static WidgetPageTemplate toDTO(String json) {
		WidgetPageTemplateJSONParser widgetPageTemplateJSONParser =
			new WidgetPageTemplateJSONParser();

		return widgetPageTemplateJSONParser.parseToDTO(json);
	}

	public static WidgetPageTemplate[] toDTOs(String json) {
		WidgetPageTemplateJSONParser widgetPageTemplateJSONParser =
			new WidgetPageTemplateJSONParser();

		return widgetPageTemplateJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WidgetPageTemplate widgetPageTemplate) {
		if (widgetPageTemplate == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (widgetPageTemplate.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(widgetPageTemplate.getActive());
		}

		if (widgetPageTemplate.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(widgetPageTemplate.getDescription_i18n()));
		}

		if (widgetPageTemplate.getFriendlyUrlPath_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath_i18n\": ");

			sb.append(_toJSON(widgetPageTemplate.getFriendlyUrlPath_i18n()));
		}

		if (widgetPageTemplate.getHiddenFromNavigation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hiddenFromNavigation\": ");

			sb.append(widgetPageTemplate.getHiddenFromNavigation());
		}

		if (widgetPageTemplate.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(widgetPageTemplate.getName_i18n()));
		}

		if (widgetPageTemplate.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(widgetPageTemplate.getCreator());
		}

		if (widgetPageTemplate.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					widgetPageTemplate.getDateCreated()));

			sb.append("\"");
		}

		if (widgetPageTemplate.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					widgetPageTemplate.getDateModified()));

			sb.append("\"");
		}

		if (widgetPageTemplate.getDatePublished() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					widgetPageTemplate.getDatePublished()));

			sb.append("\"");
		}

		if (widgetPageTemplate.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(widgetPageTemplate.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (widgetPageTemplate.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(widgetPageTemplate.getKey()));

			sb.append("\"");
		}

		if (widgetPageTemplate.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < widgetPageTemplate.getKeywords().length; i++) {
				sb.append(_toJSON(widgetPageTemplate.getKeywords()[i]));

				if ((i + 1) < widgetPageTemplate.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (widgetPageTemplate.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(widgetPageTemplate.getName()));

			sb.append("\"");
		}

		if (widgetPageTemplate.getPageSpecifications() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSpecifications\": ");

			sb.append("[");

			for (int i = 0;
				 i < widgetPageTemplate.getPageSpecifications().length; i++) {

				sb.append(
					String.valueOf(
						widgetPageTemplate.getPageSpecifications()[i]));

				if ((i + 1) <
						widgetPageTemplate.getPageSpecifications().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (widgetPageTemplate.getPageTemplateSet() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTemplateSet\": ");

			sb.append(String.valueOf(widgetPageTemplate.getPageTemplateSet()));
		}

		if (widgetPageTemplate.getPageTemplateSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTemplateSettings\": ");

			sb.append(
				String.valueOf(widgetPageTemplate.getPageTemplateSettings()));
		}

		if (widgetPageTemplate.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < widgetPageTemplate.getPermissions().length;
				 i++) {

				sb.append(widgetPageTemplate.getPermissions()[i]);

				if ((i + 1) < widgetPageTemplate.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (widgetPageTemplate.getTaxonomyCategoryItemExternalReferences() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryItemExternalReferences\": ");

			sb.append("[");

			for (int i = 0;
				 i < widgetPageTemplate.
					 getTaxonomyCategoryItemExternalReferences().length;
				 i++) {

				sb.append(
					String.valueOf(
						widgetPageTemplate.
							getTaxonomyCategoryItemExternalReferences()[i]));

				if ((i + 1) < widgetPageTemplate.
						getTaxonomyCategoryItemExternalReferences().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (widgetPageTemplate.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(widgetPageTemplate.getType());

			sb.append("\"");
		}

		if (widgetPageTemplate.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(widgetPageTemplate.getUuid()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WidgetPageTemplateJSONParser widgetPageTemplateJSONParser =
			new WidgetPageTemplateJSONParser();

		return widgetPageTemplateJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WidgetPageTemplate widgetPageTemplate) {

		if (widgetPageTemplate == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (widgetPageTemplate.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(widgetPageTemplate.getActive()));
		}

		if (widgetPageTemplate.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(widgetPageTemplate.getDescription_i18n()));
		}

		if (widgetPageTemplate.getFriendlyUrlPath_i18n() == null) {
			map.put("friendlyUrlPath_i18n", null);
		}
		else {
			map.put(
				"friendlyUrlPath_i18n",
				String.valueOf(widgetPageTemplate.getFriendlyUrlPath_i18n()));
		}

		if (widgetPageTemplate.getHiddenFromNavigation() == null) {
			map.put("hiddenFromNavigation", null);
		}
		else {
			map.put(
				"hiddenFromNavigation",
				String.valueOf(widgetPageTemplate.getHiddenFromNavigation()));
		}

		if (widgetPageTemplate.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put(
				"name_i18n", String.valueOf(widgetPageTemplate.getName_i18n()));
		}

		if (widgetPageTemplate.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(widgetPageTemplate.getCreator()));
		}

		if (widgetPageTemplate.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					widgetPageTemplate.getDateCreated()));
		}

		if (widgetPageTemplate.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					widgetPageTemplate.getDateModified()));
		}

		if (widgetPageTemplate.getDatePublished() == null) {
			map.put("datePublished", null);
		}
		else {
			map.put(
				"datePublished",
				liferayToJSONDateFormat.format(
					widgetPageTemplate.getDatePublished()));
		}

		if (widgetPageTemplate.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(widgetPageTemplate.getExternalReferenceCode()));
		}

		if (widgetPageTemplate.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(widgetPageTemplate.getKey()));
		}

		if (widgetPageTemplate.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put(
				"keywords", String.valueOf(widgetPageTemplate.getKeywords()));
		}

		if (widgetPageTemplate.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(widgetPageTemplate.getName()));
		}

		if (widgetPageTemplate.getPageSpecifications() == null) {
			map.put("pageSpecifications", null);
		}
		else {
			map.put(
				"pageSpecifications",
				String.valueOf(widgetPageTemplate.getPageSpecifications()));
		}

		if (widgetPageTemplate.getPageTemplateSet() == null) {
			map.put("pageTemplateSet", null);
		}
		else {
			map.put(
				"pageTemplateSet",
				String.valueOf(widgetPageTemplate.getPageTemplateSet()));
		}

		if (widgetPageTemplate.getPageTemplateSettings() == null) {
			map.put("pageTemplateSettings", null);
		}
		else {
			map.put(
				"pageTemplateSettings",
				String.valueOf(widgetPageTemplate.getPageTemplateSettings()));
		}

		if (widgetPageTemplate.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(widgetPageTemplate.getPermissions()));
		}

		if (widgetPageTemplate.getTaxonomyCategoryItemExternalReferences() ==
				null) {

			map.put("taxonomyCategoryItemExternalReferences", null);
		}
		else {
			map.put(
				"taxonomyCategoryItemExternalReferences",
				String.valueOf(
					widgetPageTemplate.
						getTaxonomyCategoryItemExternalReferences()));
		}

		if (widgetPageTemplate.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(widgetPageTemplate.getType()));
		}

		if (widgetPageTemplate.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put("uuid", String.valueOf(widgetPageTemplate.getUuid()));
		}

		return map;
	}

	public static class WidgetPageTemplateJSONParser
		extends BaseJSONParser<WidgetPageTemplate> {

		@Override
		protected WidgetPageTemplate createDTO() {
			return new WidgetPageTemplate();
		}

		@Override
		protected WidgetPageTemplate[] createDTOArray(int size) {
			return new WidgetPageTemplate[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlPath_i18n")) {

				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "hiddenFromNavigation")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
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
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageSpecifications")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageTemplateSet")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageTemplateSettings")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"taxonomyCategoryItemExternalReferences")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WidgetPageTemplate widgetPageTemplate, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlPath_i18n")) {

				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setFriendlyUrlPath_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "hiddenFromNavigation")) {

				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setHiddenFromNavigation(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setDatePublished(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setKeywords(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setName((String)jsonParserFieldValue);
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

					widgetPageTemplate.setPageSpecifications(
						pageSpecificationsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageTemplateSet")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setPageTemplateSet(
						PageTemplateSetSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageTemplateSettings")) {

				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setPageTemplateSettings(
						PageTemplateSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.admin.site.client.permission.
						Permission[] permissionsArray = new
						com.liferay.headless.admin.site.client.permission.
							Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.admin.site.client.permission.
								Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					widgetPageTemplate.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"taxonomyCategoryItemExternalReferences")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ItemExternalReference[]
						taxonomyCategoryItemExternalReferencesArray =
							new ItemExternalReference
								[jsonParserFieldValues.length];

					for (int i = 0;
						 i < taxonomyCategoryItemExternalReferencesArray.length;
						 i++) {

						taxonomyCategoryItemExternalReferencesArray[i] =
							ItemExternalReferenceSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					widgetPageTemplate.
						setTaxonomyCategoryItemExternalReferences(
							taxonomyCategoryItemExternalReferencesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setType(
						WidgetPageTemplate.Type.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplate.setUuid((String)jsonParserFieldValue);
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