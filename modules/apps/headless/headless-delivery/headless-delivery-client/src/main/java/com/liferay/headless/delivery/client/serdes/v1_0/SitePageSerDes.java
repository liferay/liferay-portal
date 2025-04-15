/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.PagePermission;
import com.liferay.headless.delivery.client.dto.v1_0.SitePage;
import com.liferay.headless.delivery.client.dto.v1_0.TaxonomyCategoryBrief;
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

		if (sitePage.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(sitePage.getActions()));
		}

		if (sitePage.getAggregateRating() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aggregateRating\": ");

			sb.append(String.valueOf(sitePage.getAggregateRating()));
		}

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

			sb.append(String.valueOf(sitePage.getCreator()));
		}

		if (sitePage.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < sitePage.getCustomFields().length; i++) {
				sb.append(sitePage.getCustomFields()[i]);

				if ((i + 1) < sitePage.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		if (sitePage.getExperience() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"experience\": ");

			sb.append(String.valueOf(sitePage.getExperience()));
		}

		if (sitePage.getFriendlyUrlPath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath\": ");

			sb.append("\"");

			sb.append(_escape(sitePage.getFriendlyUrlPath()));

			sb.append("\"");
		}

		if (sitePage.getFriendlyUrlPath_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath_i18n\": ");

			sb.append(_toJSON(sitePage.getFriendlyUrlPath_i18n()));
		}

		if (sitePage.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(sitePage.getId());
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

		if (sitePage.getPageDefinition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageDefinition\": ");

			sb.append(String.valueOf(sitePage.getPageDefinition()));
		}

		if (sitePage.getPagePermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pagePermissions\": ");

			sb.append("[");

			for (int i = 0; i < sitePage.getPagePermissions().length; i++) {
				sb.append(String.valueOf(sitePage.getPagePermissions()[i]));

				if ((i + 1) < sitePage.getPagePermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sitePage.getPageSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSettings\": ");

			sb.append(String.valueOf(sitePage.getPageSettings()));
		}

		if (sitePage.getPageType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageType\": ");

			sb.append("\"");

			sb.append(_escape(sitePage.getPageType()));

			sb.append("\"");
		}

		if (sitePage.getParentSitePage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSitePage\": ");

			sb.append(String.valueOf(sitePage.getParentSitePage()));
		}

		if (sitePage.getRenderedPage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"renderedPage\": ");

			sb.append(String.valueOf(sitePage.getRenderedPage()));
		}

		if (sitePage.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(sitePage.getSiteId());
		}

		if (sitePage.getTaxonomyCategoryBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < sitePage.getTaxonomyCategoryBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(sitePage.getTaxonomyCategoryBriefs()[i]));

				if ((i + 1) < sitePage.getTaxonomyCategoryBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sitePage.getTaxonomyCategoryIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIds\": ");

			sb.append("[");

			for (int i = 0; i < sitePage.getTaxonomyCategoryIds().length; i++) {
				sb.append(sitePage.getTaxonomyCategoryIds()[i]);

				if ((i + 1) < sitePage.getTaxonomyCategoryIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sitePage.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(sitePage.getTitle()));

			sb.append("\"");
		}

		if (sitePage.getTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title_i18n\": ");

			sb.append(_toJSON(sitePage.getTitle_i18n()));
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

		if (sitePage.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(sitePage.getActions()));
		}

		if (sitePage.getAggregateRating() == null) {
			map.put("aggregateRating", null);
		}
		else {
			map.put(
				"aggregateRating",
				String.valueOf(sitePage.getAggregateRating()));
		}

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

		if (sitePage.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put("customFields", String.valueOf(sitePage.getCustomFields()));
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

		if (sitePage.getExperience() == null) {
			map.put("experience", null);
		}
		else {
			map.put("experience", String.valueOf(sitePage.getExperience()));
		}

		if (sitePage.getFriendlyUrlPath() == null) {
			map.put("friendlyUrlPath", null);
		}
		else {
			map.put(
				"friendlyUrlPath",
				String.valueOf(sitePage.getFriendlyUrlPath()));
		}

		if (sitePage.getFriendlyUrlPath_i18n() == null) {
			map.put("friendlyUrlPath_i18n", null);
		}
		else {
			map.put(
				"friendlyUrlPath_i18n",
				String.valueOf(sitePage.getFriendlyUrlPath_i18n()));
		}

		if (sitePage.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(sitePage.getId()));
		}

		if (sitePage.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put("keywords", String.valueOf(sitePage.getKeywords()));
		}

		if (sitePage.getPageDefinition() == null) {
			map.put("pageDefinition", null);
		}
		else {
			map.put(
				"pageDefinition", String.valueOf(sitePage.getPageDefinition()));
		}

		if (sitePage.getPagePermissions() == null) {
			map.put("pagePermissions", null);
		}
		else {
			map.put(
				"pagePermissions",
				String.valueOf(sitePage.getPagePermissions()));
		}

		if (sitePage.getPageSettings() == null) {
			map.put("pageSettings", null);
		}
		else {
			map.put("pageSettings", String.valueOf(sitePage.getPageSettings()));
		}

		if (sitePage.getPageType() == null) {
			map.put("pageType", null);
		}
		else {
			map.put("pageType", String.valueOf(sitePage.getPageType()));
		}

		if (sitePage.getParentSitePage() == null) {
			map.put("parentSitePage", null);
		}
		else {
			map.put(
				"parentSitePage", String.valueOf(sitePage.getParentSitePage()));
		}

		if (sitePage.getRenderedPage() == null) {
			map.put("renderedPage", null);
		}
		else {
			map.put("renderedPage", String.valueOf(sitePage.getRenderedPage()));
		}

		if (sitePage.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(sitePage.getSiteId()));
		}

		if (sitePage.getTaxonomyCategoryBriefs() == null) {
			map.put("taxonomyCategoryBriefs", null);
		}
		else {
			map.put(
				"taxonomyCategoryBriefs",
				String.valueOf(sitePage.getTaxonomyCategoryBriefs()));
		}

		if (sitePage.getTaxonomyCategoryIds() == null) {
			map.put("taxonomyCategoryIds", null);
		}
		else {
			map.put(
				"taxonomyCategoryIds",
				String.valueOf(sitePage.getTaxonomyCategoryIds()));
		}

		if (sitePage.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(sitePage.getTitle()));
		}

		if (sitePage.getTitle_i18n() == null) {
			map.put("title_i18n", null);
		}
		else {
			map.put("title_i18n", String.valueOf(sitePage.getTitle_i18n()));
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
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "aggregateRating")) {
				return false;
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
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "experience")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlPath_i18n")) {

				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageDefinition")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pagePermissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageSettings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parentSitePage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "renderedPage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryBriefs")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryIds")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				return true;
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

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					sitePage.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "aggregateRating")) {
				if (jsonParserFieldValue != null) {
					sitePage.setAggregateRating(
						AggregateRatingSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

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

					sitePage.setCustomFields(customFieldsArray);
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
			else if (Objects.equals(jsonParserFieldName, "experience")) {
				if (jsonParserFieldValue != null) {
					sitePage.setExperience(
						ExperienceSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				if (jsonParserFieldValue != null) {
					sitePage.setFriendlyUrlPath((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlPath_i18n")) {

				if (jsonParserFieldValue != null) {
					sitePage.setFriendlyUrlPath_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					sitePage.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					sitePage.setKeywords(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageDefinition")) {
				if (jsonParserFieldValue != null) {
					sitePage.setPageDefinition(
						PageDefinitionSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pagePermissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PagePermission[] pagePermissionsArray =
						new PagePermission[jsonParserFieldValues.length];

					for (int i = 0; i < pagePermissionsArray.length; i++) {
						pagePermissionsArray[i] = PagePermissionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					sitePage.setPagePermissions(pagePermissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageSettings")) {
				if (jsonParserFieldValue != null) {
					sitePage.setPageSettings(
						PageSettingsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageType")) {
				if (jsonParserFieldValue != null) {
					sitePage.setPageType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentSitePage")) {
				if (jsonParserFieldValue != null) {
					sitePage.setParentSitePage(
						ParentSitePageSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "renderedPage")) {
				if (jsonParserFieldValue != null) {
					sitePage.setRenderedPage(
						RenderedPageSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					sitePage.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryBriefs")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					TaxonomyCategoryBrief[] taxonomyCategoryBriefsArray =
						new TaxonomyCategoryBrief[jsonParserFieldValues.length];

					for (int i = 0; i < taxonomyCategoryBriefsArray.length;
						 i++) {

						taxonomyCategoryBriefsArray[i] =
							TaxonomyCategoryBriefSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					sitePage.setTaxonomyCategoryBriefs(
						taxonomyCategoryBriefsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryIds")) {

				if (jsonParserFieldValue != null) {
					sitePage.setTaxonomyCategoryIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					sitePage.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				if (jsonParserFieldValue != null) {
					sitePage.setTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
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