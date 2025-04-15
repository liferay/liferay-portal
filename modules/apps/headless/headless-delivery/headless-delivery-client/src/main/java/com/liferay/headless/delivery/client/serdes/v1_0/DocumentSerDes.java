/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.AdaptedImage;
import com.liferay.headless.delivery.client.dto.v1_0.Document;
import com.liferay.headless.delivery.client.dto.v1_0.RelatedContent;
import com.liferay.headless.delivery.client.dto.v1_0.RenderedContent;
import com.liferay.headless.delivery.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

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
public class DocumentSerDes {

	public static Document toDTO(String json) {
		DocumentJSONParser documentJSONParser = new DocumentJSONParser();

		return documentJSONParser.parseToDTO(json);
	}

	public static Document[] toDTOs(String json) {
		DocumentJSONParser documentJSONParser = new DocumentJSONParser();

		return documentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Document document) {
		if (document == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (document.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(document.getActions()));
		}

		if (document.getAdaptedImages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"adaptedImages\": ");

			sb.append("[");

			for (int i = 0; i < document.getAdaptedImages().length; i++) {
				sb.append(String.valueOf(document.getAdaptedImages()[i]));

				if ((i + 1) < document.getAdaptedImages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (document.getAggregateRating() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aggregateRating\": ");

			sb.append(String.valueOf(document.getAggregateRating()));
		}

		if (document.getAssetLibraryKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryKey\": ");

			sb.append("\"");

			sb.append(_escape(document.getAssetLibraryKey()));

			sb.append("\"");
		}

		if (document.getContentUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentUrl\": ");

			sb.append("\"");

			sb.append(_escape(document.getContentUrl()));

			sb.append("\"");
		}

		if (document.getContentValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentValue\": ");

			sb.append("\"");

			sb.append(_escape(document.getContentValue()));

			sb.append("\"");
		}

		if (document.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(document.getCreator()));
		}

		if (document.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < document.getCustomFields().length; i++) {
				sb.append(document.getCustomFields()[i]);

				if ((i + 1) < document.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (document.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(document.getDateCreated()));

			sb.append("\"");
		}

		if (document.getDateExpired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateExpired\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(document.getDateExpired()));

			sb.append("\"");
		}

		if (document.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(document.getDateModified()));

			sb.append("\"");
		}

		if (document.getDatePublished() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(document.getDatePublished()));

			sb.append("\"");
		}

		if (document.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(document.getDescription()));

			sb.append("\"");
		}

		if (document.getDocumentFolderExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentFolderExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(document.getDocumentFolderExternalReferenceCode()));

			sb.append("\"");
		}

		if (document.getDocumentFolderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentFolderId\": ");

			sb.append(document.getDocumentFolderId());
		}

		if (document.getDocumentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentType\": ");

			sb.append(String.valueOf(document.getDocumentType()));
		}

		if (document.getEncodingFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"encodingFormat\": ");

			sb.append("\"");

			sb.append(_escape(document.getEncodingFormat()));

			sb.append("\"");
		}

		if (document.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(document.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (document.getFileExtension() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileExtension\": ");

			sb.append("\"");

			sb.append(_escape(document.getFileExtension()));

			sb.append("\"");
		}

		if (document.getFileName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileName\": ");

			sb.append("\"");

			sb.append(_escape(document.getFileName()));

			sb.append("\"");
		}

		if (document.getFriendlyUrlPath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath\": ");

			sb.append("\"");

			sb.append(_escape(document.getFriendlyUrlPath()));

			sb.append("\"");
		}

		if (document.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(document.getId());
		}

		if (document.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < document.getKeywords().length; i++) {
				sb.append(_toJSON(document.getKeywords()[i]));

				if ((i + 1) < document.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (document.getNumberOfComments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfComments\": ");

			sb.append(document.getNumberOfComments());
		}

		if (document.getRelatedContents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedContents\": ");

			sb.append("[");

			for (int i = 0; i < document.getRelatedContents().length; i++) {
				sb.append(String.valueOf(document.getRelatedContents()[i]));

				if ((i + 1) < document.getRelatedContents().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (document.getRenderedContents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"renderedContents\": ");

			sb.append("[");

			for (int i = 0; i < document.getRenderedContents().length; i++) {
				sb.append(String.valueOf(document.getRenderedContents()[i]));

				if ((i + 1) < document.getRenderedContents().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (document.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(document.getSiteId());
		}

		if (document.getSizeInBytes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sizeInBytes\": ");

			sb.append(document.getSizeInBytes());
		}

		if (document.getTaxonomyCategoryBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < document.getTaxonomyCategoryBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(document.getTaxonomyCategoryBriefs()[i]));

				if ((i + 1) < document.getTaxonomyCategoryBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (document.getTaxonomyCategoryIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIds\": ");

			sb.append("[");

			for (int i = 0; i < document.getTaxonomyCategoryIds().length; i++) {
				sb.append(document.getTaxonomyCategoryIds()[i]);

				if ((i + 1) < document.getTaxonomyCategoryIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (document.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(document.getTitle()));

			sb.append("\"");
		}

		if (document.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(document.getViewableBy());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DocumentJSONParser documentJSONParser = new DocumentJSONParser();

		return documentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Document document) {
		if (document == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (document.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(document.getActions()));
		}

		if (document.getAdaptedImages() == null) {
			map.put("adaptedImages", null);
		}
		else {
			map.put(
				"adaptedImages", String.valueOf(document.getAdaptedImages()));
		}

		if (document.getAggregateRating() == null) {
			map.put("aggregateRating", null);
		}
		else {
			map.put(
				"aggregateRating",
				String.valueOf(document.getAggregateRating()));
		}

		if (document.getAssetLibraryKey() == null) {
			map.put("assetLibraryKey", null);
		}
		else {
			map.put(
				"assetLibraryKey",
				String.valueOf(document.getAssetLibraryKey()));
		}

		if (document.getContentUrl() == null) {
			map.put("contentUrl", null);
		}
		else {
			map.put("contentUrl", String.valueOf(document.getContentUrl()));
		}

		if (document.getContentValue() == null) {
			map.put("contentValue", null);
		}
		else {
			map.put("contentValue", String.valueOf(document.getContentValue()));
		}

		if (document.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(document.getCreator()));
		}

		if (document.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put("customFields", String.valueOf(document.getCustomFields()));
		}

		if (document.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(document.getDateCreated()));
		}

		if (document.getDateExpired() == null) {
			map.put("dateExpired", null);
		}
		else {
			map.put(
				"dateExpired",
				liferayToJSONDateFormat.format(document.getDateExpired()));
		}

		if (document.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(document.getDateModified()));
		}

		if (document.getDatePublished() == null) {
			map.put("datePublished", null);
		}
		else {
			map.put(
				"datePublished",
				liferayToJSONDateFormat.format(document.getDatePublished()));
		}

		if (document.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(document.getDescription()));
		}

		if (document.getDocumentFolderExternalReferenceCode() == null) {
			map.put("documentFolderExternalReferenceCode", null);
		}
		else {
			map.put(
				"documentFolderExternalReferenceCode",
				String.valueOf(
					document.getDocumentFolderExternalReferenceCode()));
		}

		if (document.getDocumentFolderId() == null) {
			map.put("documentFolderId", null);
		}
		else {
			map.put(
				"documentFolderId",
				String.valueOf(document.getDocumentFolderId()));
		}

		if (document.getDocumentType() == null) {
			map.put("documentType", null);
		}
		else {
			map.put("documentType", String.valueOf(document.getDocumentType()));
		}

		if (document.getEncodingFormat() == null) {
			map.put("encodingFormat", null);
		}
		else {
			map.put(
				"encodingFormat", String.valueOf(document.getEncodingFormat()));
		}

		if (document.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(document.getExternalReferenceCode()));
		}

		if (document.getFileExtension() == null) {
			map.put("fileExtension", null);
		}
		else {
			map.put(
				"fileExtension", String.valueOf(document.getFileExtension()));
		}

		if (document.getFileName() == null) {
			map.put("fileName", null);
		}
		else {
			map.put("fileName", String.valueOf(document.getFileName()));
		}

		if (document.getFriendlyUrlPath() == null) {
			map.put("friendlyUrlPath", null);
		}
		else {
			map.put(
				"friendlyUrlPath",
				String.valueOf(document.getFriendlyUrlPath()));
		}

		if (document.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(document.getId()));
		}

		if (document.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put("keywords", String.valueOf(document.getKeywords()));
		}

		if (document.getNumberOfComments() == null) {
			map.put("numberOfComments", null);
		}
		else {
			map.put(
				"numberOfComments",
				String.valueOf(document.getNumberOfComments()));
		}

		if (document.getRelatedContents() == null) {
			map.put("relatedContents", null);
		}
		else {
			map.put(
				"relatedContents",
				String.valueOf(document.getRelatedContents()));
		}

		if (document.getRenderedContents() == null) {
			map.put("renderedContents", null);
		}
		else {
			map.put(
				"renderedContents",
				String.valueOf(document.getRenderedContents()));
		}

		if (document.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(document.getSiteId()));
		}

		if (document.getSizeInBytes() == null) {
			map.put("sizeInBytes", null);
		}
		else {
			map.put("sizeInBytes", String.valueOf(document.getSizeInBytes()));
		}

		if (document.getTaxonomyCategoryBriefs() == null) {
			map.put("taxonomyCategoryBriefs", null);
		}
		else {
			map.put(
				"taxonomyCategoryBriefs",
				String.valueOf(document.getTaxonomyCategoryBriefs()));
		}

		if (document.getTaxonomyCategoryIds() == null) {
			map.put("taxonomyCategoryIds", null);
		}
		else {
			map.put(
				"taxonomyCategoryIds",
				String.valueOf(document.getTaxonomyCategoryIds()));
		}

		if (document.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(document.getTitle()));
		}

		if (document.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put("viewableBy", String.valueOf(document.getViewableBy()));
		}

		return map;
	}

	public static class DocumentJSONParser extends BaseJSONParser<Document> {

		@Override
		protected Document createDTO() {
			return new Document();
		}

		@Override
		protected Document[] createDTOArray(int size) {
			return new Document[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "adaptedImages")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "aggregateRating")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentUrl")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentValue")) {
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
			else if (Objects.equals(
						jsonParserFieldName,
						"documentFolderExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "documentFolderId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "documentType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "encodingFormat")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fileExtension")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fileName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfComments")) {
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
			else if (Objects.equals(jsonParserFieldName, "sizeInBytes")) {
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
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Document document, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					document.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "adaptedImages")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AdaptedImage[] adaptedImagesArray =
						new AdaptedImage[jsonParserFieldValues.length];

					for (int i = 0; i < adaptedImagesArray.length; i++) {
						adaptedImagesArray[i] = AdaptedImageSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					document.setAdaptedImages(adaptedImagesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "aggregateRating")) {
				if (jsonParserFieldValue != null) {
					document.setAggregateRating(
						AggregateRatingSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				if (jsonParserFieldValue != null) {
					document.setAssetLibraryKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentUrl")) {
				if (jsonParserFieldValue != null) {
					document.setContentUrl((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentValue")) {
				if (jsonParserFieldValue != null) {
					document.setContentValue((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					document.setCreator(
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

					document.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					document.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateExpired")) {
				if (jsonParserFieldValue != null) {
					document.setDateExpired(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					document.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				if (jsonParserFieldValue != null) {
					document.setDatePublished(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					document.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"documentFolderExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					document.setDocumentFolderExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "documentFolderId")) {
				if (jsonParserFieldValue != null) {
					document.setDocumentFolderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "documentType")) {
				if (jsonParserFieldValue != null) {
					document.setDocumentType(
						DocumentTypeSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "encodingFormat")) {
				if (jsonParserFieldValue != null) {
					document.setEncodingFormat((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					document.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileExtension")) {
				if (jsonParserFieldValue != null) {
					document.setFileExtension((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileName")) {
				if (jsonParserFieldValue != null) {
					document.setFileName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				if (jsonParserFieldValue != null) {
					document.setFriendlyUrlPath((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					document.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					document.setKeywords(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "numberOfComments")) {
				if (jsonParserFieldValue != null) {
					document.setNumberOfComments(
						Integer.valueOf((String)jsonParserFieldValue));
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

					document.setRelatedContents(relatedContentsArray);
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

					document.setRenderedContents(renderedContentsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					document.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sizeInBytes")) {
				if (jsonParserFieldValue != null) {
					document.setSizeInBytes(
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

					document.setTaxonomyCategoryBriefs(
						taxonomyCategoryBriefsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryIds")) {

				if (jsonParserFieldValue != null) {
					document.setTaxonomyCategoryIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					document.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					document.setViewableBy(
						Document.ViewableBy.create(
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