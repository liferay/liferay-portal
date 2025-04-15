/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.ContentField;
import com.liferay.headless.delivery.client.dto.v1_0.RelatedContent;
import com.liferay.headless.delivery.client.dto.v1_0.RenderedContent;
import com.liferay.headless.delivery.client.dto.v1_0.StructuredContent;
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
public class StructuredContentSerDes {

	public static StructuredContent toDTO(String json) {
		StructuredContentJSONParser structuredContentJSONParser =
			new StructuredContentJSONParser();

		return structuredContentJSONParser.parseToDTO(json);
	}

	public static StructuredContent[] toDTOs(String json) {
		StructuredContentJSONParser structuredContentJSONParser =
			new StructuredContentJSONParser();

		return structuredContentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(StructuredContent structuredContent) {
		if (structuredContent == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (structuredContent.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(structuredContent.getActions()));
		}

		if (structuredContent.getAggregateRating() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aggregateRating\": ");

			sb.append(String.valueOf(structuredContent.getAggregateRating()));
		}

		if (structuredContent.getAssetLibraryKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryKey\": ");

			sb.append("\"");

			sb.append(_escape(structuredContent.getAssetLibraryKey()));

			sb.append("\"");
		}

		if (structuredContent.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0;
				 i < structuredContent.getAvailableLanguages().length; i++) {

				sb.append(
					_toJSON(structuredContent.getAvailableLanguages()[i]));

				if ((i + 1) <
						structuredContent.getAvailableLanguages().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (structuredContent.getContentFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentFields\": ");

			sb.append("[");

			for (int i = 0; i < structuredContent.getContentFields().length;
				 i++) {

				sb.append(
					String.valueOf(structuredContent.getContentFields()[i]));

				if ((i + 1) < structuredContent.getContentFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (structuredContent.getContentStructureId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentStructureId\": ");

			sb.append(structuredContent.getContentStructureId());
		}

		if (structuredContent.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(structuredContent.getCreator()));
		}

		if (structuredContent.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < structuredContent.getCustomFields().length;
				 i++) {

				sb.append(structuredContent.getCustomFields()[i]);

				if ((i + 1) < structuredContent.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (structuredContent.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					structuredContent.getDateCreated()));

			sb.append("\"");
		}

		if (structuredContent.getDateExpired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateExpired\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					structuredContent.getDateExpired()));

			sb.append("\"");
		}

		if (structuredContent.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					structuredContent.getDateModified()));

			sb.append("\"");
		}

		if (structuredContent.getDatePublished() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					structuredContent.getDatePublished()));

			sb.append("\"");
		}

		if (structuredContent.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(structuredContent.getDescription()));

			sb.append("\"");
		}

		if (structuredContent.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(structuredContent.getDescription_i18n()));
		}

		if (structuredContent.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(structuredContent.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (structuredContent.getFriendlyUrlPath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath\": ");

			sb.append("\"");

			sb.append(_escape(structuredContent.getFriendlyUrlPath()));

			sb.append("\"");
		}

		if (structuredContent.getFriendlyUrlPath_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath_i18n\": ");

			sb.append(_toJSON(structuredContent.getFriendlyUrlPath_i18n()));
		}

		if (structuredContent.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(structuredContent.getId());
		}

		if (structuredContent.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(structuredContent.getKey()));

			sb.append("\"");
		}

		if (structuredContent.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < structuredContent.getKeywords().length; i++) {
				sb.append(_toJSON(structuredContent.getKeywords()[i]));

				if ((i + 1) < structuredContent.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (structuredContent.getNeverExpire() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(structuredContent.getNeverExpire());
		}

		if (structuredContent.getNumberOfComments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfComments\": ");

			sb.append(structuredContent.getNumberOfComments());
		}

		if (structuredContent.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < structuredContent.getPermissions().length;
				 i++) {

				sb.append(structuredContent.getPermissions()[i]);

				if ((i + 1) < structuredContent.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (structuredContent.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(structuredContent.getPriority());
		}

		if (structuredContent.getRelatedContents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedContents\": ");

			sb.append("[");

			for (int i = 0; i < structuredContent.getRelatedContents().length;
				 i++) {

				sb.append(
					String.valueOf(structuredContent.getRelatedContents()[i]));

				if ((i + 1) < structuredContent.getRelatedContents().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (structuredContent.getRenderedContents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"renderedContents\": ");

			sb.append("[");

			for (int i = 0; i < structuredContent.getRenderedContents().length;
				 i++) {

				sb.append(
					String.valueOf(structuredContent.getRenderedContents()[i]));

				if ((i + 1) < structuredContent.getRenderedContents().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (structuredContent.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(structuredContent.getSiteId());
		}

		if (structuredContent.getStructuredContentFolderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"structuredContentFolderId\": ");

			sb.append(structuredContent.getStructuredContentFolderId());
		}

		if (structuredContent.getSubscribed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscribed\": ");

			sb.append(structuredContent.getSubscribed());
		}

		if (structuredContent.getTaxonomyCategoryBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryBriefs\": ");

			sb.append("[");

			for (int i = 0;
				 i < structuredContent.getTaxonomyCategoryBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(
						structuredContent.getTaxonomyCategoryBriefs()[i]));

				if ((i + 1) <
						structuredContent.getTaxonomyCategoryBriefs().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (structuredContent.getTaxonomyCategoryIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIds\": ");

			sb.append("[");

			for (int i = 0;
				 i < structuredContent.getTaxonomyCategoryIds().length; i++) {

				sb.append(structuredContent.getTaxonomyCategoryIds()[i]);

				if ((i + 1) <
						structuredContent.getTaxonomyCategoryIds().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (structuredContent.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(structuredContent.getTitle()));

			sb.append("\"");
		}

		if (structuredContent.getTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title_i18n\": ");

			sb.append(_toJSON(structuredContent.getTitle_i18n()));
		}

		if (structuredContent.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(structuredContent.getUuid()));

			sb.append("\"");
		}

		if (structuredContent.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(structuredContent.getViewableBy());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		StructuredContentJSONParser structuredContentJSONParser =
			new StructuredContentJSONParser();

		return structuredContentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		StructuredContent structuredContent) {

		if (structuredContent == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (structuredContent.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(structuredContent.getActions()));
		}

		if (structuredContent.getAggregateRating() == null) {
			map.put("aggregateRating", null);
		}
		else {
			map.put(
				"aggregateRating",
				String.valueOf(structuredContent.getAggregateRating()));
		}

		if (structuredContent.getAssetLibraryKey() == null) {
			map.put("assetLibraryKey", null);
		}
		else {
			map.put(
				"assetLibraryKey",
				String.valueOf(structuredContent.getAssetLibraryKey()));
		}

		if (structuredContent.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(structuredContent.getAvailableLanguages()));
		}

		if (structuredContent.getContentFields() == null) {
			map.put("contentFields", null);
		}
		else {
			map.put(
				"contentFields",
				String.valueOf(structuredContent.getContentFields()));
		}

		if (structuredContent.getContentStructureId() == null) {
			map.put("contentStructureId", null);
		}
		else {
			map.put(
				"contentStructureId",
				String.valueOf(structuredContent.getContentStructureId()));
		}

		if (structuredContent.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(structuredContent.getCreator()));
		}

		if (structuredContent.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(structuredContent.getCustomFields()));
		}

		if (structuredContent.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					structuredContent.getDateCreated()));
		}

		if (structuredContent.getDateExpired() == null) {
			map.put("dateExpired", null);
		}
		else {
			map.put(
				"dateExpired",
				liferayToJSONDateFormat.format(
					structuredContent.getDateExpired()));
		}

		if (structuredContent.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					structuredContent.getDateModified()));
		}

		if (structuredContent.getDatePublished() == null) {
			map.put("datePublished", null);
		}
		else {
			map.put(
				"datePublished",
				liferayToJSONDateFormat.format(
					structuredContent.getDatePublished()));
		}

		if (structuredContent.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(structuredContent.getDescription()));
		}

		if (structuredContent.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(structuredContent.getDescription_i18n()));
		}

		if (structuredContent.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(structuredContent.getExternalReferenceCode()));
		}

		if (structuredContent.getFriendlyUrlPath() == null) {
			map.put("friendlyUrlPath", null);
		}
		else {
			map.put(
				"friendlyUrlPath",
				String.valueOf(structuredContent.getFriendlyUrlPath()));
		}

		if (structuredContent.getFriendlyUrlPath_i18n() == null) {
			map.put("friendlyUrlPath_i18n", null);
		}
		else {
			map.put(
				"friendlyUrlPath_i18n",
				String.valueOf(structuredContent.getFriendlyUrlPath_i18n()));
		}

		if (structuredContent.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(structuredContent.getId()));
		}

		if (structuredContent.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(structuredContent.getKey()));
		}

		if (structuredContent.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put(
				"keywords", String.valueOf(structuredContent.getKeywords()));
		}

		if (structuredContent.getNeverExpire() == null) {
			map.put("neverExpire", null);
		}
		else {
			map.put(
				"neverExpire",
				String.valueOf(structuredContent.getNeverExpire()));
		}

		if (structuredContent.getNumberOfComments() == null) {
			map.put("numberOfComments", null);
		}
		else {
			map.put(
				"numberOfComments",
				String.valueOf(structuredContent.getNumberOfComments()));
		}

		if (structuredContent.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(structuredContent.getPermissions()));
		}

		if (structuredContent.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority", String.valueOf(structuredContent.getPriority()));
		}

		if (structuredContent.getRelatedContents() == null) {
			map.put("relatedContents", null);
		}
		else {
			map.put(
				"relatedContents",
				String.valueOf(structuredContent.getRelatedContents()));
		}

		if (structuredContent.getRenderedContents() == null) {
			map.put("renderedContents", null);
		}
		else {
			map.put(
				"renderedContents",
				String.valueOf(structuredContent.getRenderedContents()));
		}

		if (structuredContent.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(structuredContent.getSiteId()));
		}

		if (structuredContent.getStructuredContentFolderId() == null) {
			map.put("structuredContentFolderId", null);
		}
		else {
			map.put(
				"structuredContentFolderId",
				String.valueOf(
					structuredContent.getStructuredContentFolderId()));
		}

		if (structuredContent.getSubscribed() == null) {
			map.put("subscribed", null);
		}
		else {
			map.put(
				"subscribed",
				String.valueOf(structuredContent.getSubscribed()));
		}

		if (structuredContent.getTaxonomyCategoryBriefs() == null) {
			map.put("taxonomyCategoryBriefs", null);
		}
		else {
			map.put(
				"taxonomyCategoryBriefs",
				String.valueOf(structuredContent.getTaxonomyCategoryBriefs()));
		}

		if (structuredContent.getTaxonomyCategoryIds() == null) {
			map.put("taxonomyCategoryIds", null);
		}
		else {
			map.put(
				"taxonomyCategoryIds",
				String.valueOf(structuredContent.getTaxonomyCategoryIds()));
		}

		if (structuredContent.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(structuredContent.getTitle()));
		}

		if (structuredContent.getTitle_i18n() == null) {
			map.put("title_i18n", null);
		}
		else {
			map.put(
				"title_i18n",
				String.valueOf(structuredContent.getTitle_i18n()));
		}

		if (structuredContent.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put("uuid", String.valueOf(structuredContent.getUuid()));
		}

		if (structuredContent.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put(
				"viewableBy",
				String.valueOf(structuredContent.getViewableBy()));
		}

		return map;
	}

	public static class StructuredContentJSONParser
		extends BaseJSONParser<StructuredContent> {

		@Override
		protected StructuredContent createDTO() {
			return new StructuredContent();
		}

		@Override
		protected StructuredContent[] createDTOArray(int size) {
			return new StructuredContent[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "aggregateRating")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentFields")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "contentStructureId")) {

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
			else if (Objects.equals(jsonParserFieldName, "dateExpired")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
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
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfComments")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "relatedContents")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "renderedContents")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "structuredContentFolderId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subscribed")) {
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
			StructuredContent structuredContent, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "aggregateRating")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setAggregateRating(
						AggregateRatingSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setAssetLibraryKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				if (jsonParserFieldValue != null) {
					structuredContent.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ContentField[] contentFieldsArray =
						new ContentField[jsonParserFieldValues.length];

					for (int i = 0; i < contentFieldsArray.length; i++) {
						contentFieldsArray[i] = ContentFieldSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					structuredContent.setContentFields(contentFieldsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "contentStructureId")) {

				if (jsonParserFieldValue != null) {
					structuredContent.setContentStructureId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setCreator(
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

					structuredContent.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateExpired")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setDateExpired(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setDatePublished(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					structuredContent.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setFriendlyUrlPath(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "friendlyUrlPath_i18n")) {

				if (jsonParserFieldValue != null) {
					structuredContent.setFriendlyUrlPath_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setKeywords(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setNeverExpire(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfComments")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setNumberOfComments(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.delivery.client.permission.Permission[]
						permissionsArray = new
						com.liferay.headless.delivery.client.permission.
							Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.delivery.client.permission.
								Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					structuredContent.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "relatedContents")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					RelatedContent[] relatedContentsArray =
						new RelatedContent[jsonParserFieldValues.length];

					for (int i = 0; i < relatedContentsArray.length; i++) {
						relatedContentsArray[i] = RelatedContentSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					structuredContent.setRelatedContents(relatedContentsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "renderedContents")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					RenderedContent[] renderedContentsArray =
						new RenderedContent[jsonParserFieldValues.length];

					for (int i = 0; i < renderedContentsArray.length; i++) {
						renderedContentsArray[i] = RenderedContentSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					structuredContent.setRenderedContents(
						renderedContentsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "structuredContentFolderId")) {

				if (jsonParserFieldValue != null) {
					structuredContent.setStructuredContentFolderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subscribed")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setSubscribed(
						(Boolean)jsonParserFieldValue);
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

					structuredContent.setTaxonomyCategoryBriefs(
						taxonomyCategoryBriefsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryIds")) {

				if (jsonParserFieldValue != null) {
					structuredContent.setTaxonomyCategoryIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setUuid((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					structuredContent.setViewableBy(
						StructuredContent.ViewableBy.create(
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