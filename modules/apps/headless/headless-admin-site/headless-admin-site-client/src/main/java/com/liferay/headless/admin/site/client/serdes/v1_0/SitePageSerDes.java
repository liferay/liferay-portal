/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePage;
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
public class SitePageSerDes {

	public static SitePage toDTO(String json) {
		SitePageJSONParser sitePageJSONParser = new SitePageJSONParser();

		return sitePageJSONParser.parseToDTO(json);
	}

	public static SitePage[] toDTOs(String json) {
		SitePageJSONParser sitePageJSONParser = new SitePageJSONParser();

		return sitePageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SitePage sitePage) {
		if (sitePage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (sitePage.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0; i < sitePage.getAvailableLanguages().length; i++) {
				sb.append(_toJSON(sitePage.getAvailableLanguages()[i]));

				if ((i + 1) < sitePage.getAvailableLanguages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sitePage.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(sitePage.getCreator());
		}

		if (sitePage.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(sitePage.getDateCreated()));

			sb.append("\"");
		}

		if (sitePage.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(sitePage.getDateModified()));

			sb.append("\"");
		}

		if (sitePage.getDatePublished() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(sitePage.getDatePublished()));

			sb.append("\"");
		}

		if (sitePage.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(sitePage.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (sitePage.getFriendlyUrlHistory() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlHistory\": ");

			sb.append(String.valueOf(sitePage.getFriendlyUrlHistory()));
		}

		if (sitePage.getFriendlyUrlPath_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath_i18n\": ");

			sb.append(_toJSON(sitePage.getFriendlyUrlPath_i18n()));
		}

		if (sitePage.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < sitePage.getKeywords().length; i++) {
				sb.append(_toJSON(sitePage.getKeywords()[i]));

				if ((i + 1) < sitePage.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sitePage.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(sitePage.getName_i18n()));
		}

		if (sitePage.getPageSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSettings\": ");

			sb.append(String.valueOf(sitePage.getPageSettings()));
		}

		if (sitePage.getPageSpecifications() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSpecifications\": ");

			sb.append("[");

			for (int i = 0; i < sitePage.getPageSpecifications().length; i++) {
				sb.append(String.valueOf(sitePage.getPageSpecifications()[i]));

				if ((i + 1) < sitePage.getPageSpecifications().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sitePage.getParentSitePageExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSitePageExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(sitePage.getParentSitePageExternalReferenceCode()));

			sb.append("\"");
		}

		if (sitePage.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < sitePage.getPermissions().length; i++) {
				sb.append(sitePage.getPermissions()[i]);

				if ((i + 1) < sitePage.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sitePage.getTaxonomyCategoryItemExternalReferences() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryItemExternalReferences\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 sitePage.
						 getTaxonomyCategoryItemExternalReferences().length;
				 i++) {

				sb.append(
					String.valueOf(
						sitePage.getTaxonomyCategoryItemExternalReferences()
							[i]));

				if ((i + 1) < sitePage.
						getTaxonomyCategoryItemExternalReferences().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sitePage.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(sitePage.getType());

			sb.append("\"");
		}

		if (sitePage.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(sitePage.getUuid()));

			sb.append("\"");
		}

		if (sitePage.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(sitePage.getViewableBy());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SitePageJSONParser sitePageJSONParser = new SitePageJSONParser();

		return sitePageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SitePage sitePage) {
		if (sitePage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (sitePage.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(sitePage.getAvailableLanguages()));
		}

		if (sitePage.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(sitePage.getCreator()));
		}

		if (sitePage.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(sitePage.getDateCreated()));
		}

		if (sitePage.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(sitePage.getDateModified()));
		}

		if (sitePage.getDatePublished() == null) {
			map.put("datePublished", null);
		}
		else {
			map.put(
				"datePublished",
				liferayToJSONDateFormat.format(sitePage.getDatePublished()));
		}

		if (sitePage.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(sitePage.getExternalReferenceCode()));
		}

		if (sitePage.getFriendlyUrlHistory() == null) {
			map.put("friendlyUrlHistory", null);
		}
		else {
			map.put(
				"friendlyUrlHistory",
				String.valueOf(sitePage.getFriendlyUrlHistory()));
		}

		if (sitePage.getFriendlyUrlPath_i18n() == null) {
			map.put("friendlyUrlPath_i18n", null);
		}
		else {
			map.put(
				"friendlyUrlPath_i18n",
				String.valueOf(sitePage.getFriendlyUrlPath_i18n()));
		}

		if (sitePage.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put("keywords", String.valueOf(sitePage.getKeywords()));
		}

		if (sitePage.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(sitePage.getName_i18n()));
		}

		if (sitePage.getPageSettings() == null) {
			map.put("pageSettings", null);
		}
		else {
			map.put("pageSettings", String.valueOf(sitePage.getPageSettings()));
		}

		if (sitePage.getPageSpecifications() == null) {
			map.put("pageSpecifications", null);
		}
		else {
			map.put(
				"pageSpecifications",
				String.valueOf(sitePage.getPageSpecifications()));
		}

		if (sitePage.getParentSitePageExternalReferenceCode() == null) {
			map.put("parentSitePageExternalReferenceCode", null);
		}
		else {
			map.put(
				"parentSitePageExternalReferenceCode",
				String.valueOf(
					sitePage.getParentSitePageExternalReferenceCode()));
		}

		if (sitePage.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put("permissions", String.valueOf(sitePage.getPermissions()));
		}

		if (sitePage.getTaxonomyCategoryItemExternalReferences() == null) {
			map.put("taxonomyCategoryItemExternalReferences", null);
		}
		else {
			map.put(
				"taxonomyCategoryItemExternalReferences",
				String.valueOf(
					sitePage.getTaxonomyCategoryItemExternalReferences()));
		}

		if (sitePage.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(sitePage.getType()));
		}

		if (sitePage.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put("uuid", String.valueOf(sitePage.getUuid()));
		}

		if (sitePage.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put("viewableBy", String.valueOf(sitePage.getViewableBy()));
		}

		return map;
	}

	public static class SitePageJSONParser extends BaseJSONParser<SitePage> {

		@Override
		protected SitePage createDTO() {
			return new SitePage();
		}

		@Override
		protected SitePage[] createDTOArray(int size) {
			return new SitePage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "availableLanguages")) {
				return false;
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
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlHistory")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlPath_i18n")) {

				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "pageSettings")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageSpecifications")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentSitePageExternalReferenceCode")) {

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
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SitePage sitePage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "availableLanguages")) {
				if (jsonParserFieldValue != null) {
					sitePage.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					sitePage.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					sitePage.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					sitePage.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				if (jsonParserFieldValue != null) {
					sitePage.setDatePublished(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					sitePage.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlHistory")) {

				if (jsonParserFieldValue != null) {
					sitePage.setFriendlyUrlHistory(
						FriendlyUrlHistorySerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlPath_i18n")) {

				if (jsonParserFieldValue != null) {
					sitePage.setFriendlyUrlPath_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					sitePage.setKeywords(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					sitePage.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageSettings")) {
				if (jsonParserFieldValue != null) {
					sitePage.setPageSettings(
						PageSettingsSerDes.toDTO((String)jsonParserFieldValue));
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

					sitePage.setPageSpecifications(pageSpecificationsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentSitePageExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					sitePage.setParentSitePageExternalReferenceCode(
						(String)jsonParserFieldValue);
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

					sitePage.setPermissions(permissionsArray);
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

					sitePage.setTaxonomyCategoryItemExternalReferences(
						taxonomyCategoryItemExternalReferencesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					sitePage.setType(
						SitePage.Type.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					sitePage.setUuid((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					sitePage.setViewableBy(
						SitePage.ViewableBy.create(
							(String)jsonParserFieldValue));
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