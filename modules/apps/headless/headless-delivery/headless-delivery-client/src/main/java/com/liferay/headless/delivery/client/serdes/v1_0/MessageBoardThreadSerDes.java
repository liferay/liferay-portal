/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.MessageBoardThread;
import com.liferay.headless.delivery.client.dto.v1_0.RelatedContent;
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
public class MessageBoardThreadSerDes {

	public static MessageBoardThread toDTO(String json) {
		MessageBoardThreadJSONParser messageBoardThreadJSONParser =
			new MessageBoardThreadJSONParser();

		return messageBoardThreadJSONParser.parseToDTO(json);
	}

	public static MessageBoardThread[] toDTOs(String json) {
		MessageBoardThreadJSONParser messageBoardThreadJSONParser =
			new MessageBoardThreadJSONParser();

		return messageBoardThreadJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MessageBoardThread messageBoardThread) {
		if (messageBoardThread == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (messageBoardThread.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(messageBoardThread.getActions()));
		}

		if (messageBoardThread.getAggregateRating() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aggregateRating\": ");

			sb.append(String.valueOf(messageBoardThread.getAggregateRating()));
		}

		if (messageBoardThread.getArticleBody() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"articleBody\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardThread.getArticleBody()));

			sb.append("\"");
		}

		if (messageBoardThread.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(messageBoardThread.getCreator()));
		}

		if (messageBoardThread.getCreatorStatistics() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorStatistics\": ");

			sb.append(
				String.valueOf(messageBoardThread.getCreatorStatistics()));
		}

		if (messageBoardThread.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < messageBoardThread.getCustomFields().length;
				 i++) {

				sb.append(messageBoardThread.getCustomFields()[i]);

				if ((i + 1) < messageBoardThread.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (messageBoardThread.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					messageBoardThread.getDateCreated()));

			sb.append("\"");
		}

		if (messageBoardThread.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					messageBoardThread.getDateModified()));

			sb.append("\"");
		}

		if (messageBoardThread.getEncodingFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"encodingFormat\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardThread.getEncodingFormat()));

			sb.append("\"");
		}

		if (messageBoardThread.getFriendlyUrlPath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardThread.getFriendlyUrlPath()));

			sb.append("\"");
		}

		if (messageBoardThread.getHasValidAnswer() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hasValidAnswer\": ");

			sb.append(messageBoardThread.getHasValidAnswer());
		}

		if (messageBoardThread.getHeadline() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"headline\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardThread.getHeadline()));

			sb.append("\"");
		}

		if (messageBoardThread.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(messageBoardThread.getId());
		}

		if (messageBoardThread.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < messageBoardThread.getKeywords().length; i++) {
				sb.append(_toJSON(messageBoardThread.getKeywords()[i]));

				if ((i + 1) < messageBoardThread.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (messageBoardThread.getLastPostDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastPostDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					messageBoardThread.getLastPostDate()));

			sb.append("\"");
		}

		if (messageBoardThread.getLocked() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"locked\": ");

			sb.append(messageBoardThread.getLocked());
		}

		if (messageBoardThread.getMessageBoardRootMessageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"messageBoardRootMessageId\": ");

			sb.append(messageBoardThread.getMessageBoardRootMessageId());
		}

		if (messageBoardThread.getMessageBoardSectionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"messageBoardSectionId\": ");

			sb.append(messageBoardThread.getMessageBoardSectionId());
		}

		if (messageBoardThread.getNumberOfMessageBoardAttachments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfMessageBoardAttachments\": ");

			sb.append(messageBoardThread.getNumberOfMessageBoardAttachments());
		}

		if (messageBoardThread.getNumberOfMessageBoardMessages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfMessageBoardMessages\": ");

			sb.append(messageBoardThread.getNumberOfMessageBoardMessages());
		}

		if (messageBoardThread.getRelatedContents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedContents\": ");

			sb.append("[");

			for (int i = 0; i < messageBoardThread.getRelatedContents().length;
				 i++) {

				sb.append(
					String.valueOf(messageBoardThread.getRelatedContents()[i]));

				if ((i + 1) < messageBoardThread.getRelatedContents().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (messageBoardThread.getSeen() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seen\": ");

			sb.append(messageBoardThread.getSeen());
		}

		if (messageBoardThread.getShowAsQuestion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showAsQuestion\": ");

			sb.append(messageBoardThread.getShowAsQuestion());
		}

		if (messageBoardThread.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(messageBoardThread.getSiteId());
		}

		if (messageBoardThread.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardThread.getStatus()));

			sb.append("\"");
		}

		if (messageBoardThread.getSubscribed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscribed\": ");

			sb.append(messageBoardThread.getSubscribed());
		}

		if (messageBoardThread.getTaxonomyCategoryBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryBriefs\": ");

			sb.append("[");

			for (int i = 0;
				 i < messageBoardThread.getTaxonomyCategoryBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(
						messageBoardThread.getTaxonomyCategoryBriefs()[i]));

				if ((i + 1) <
						messageBoardThread.getTaxonomyCategoryBriefs().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (messageBoardThread.getTaxonomyCategoryIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIds\": ");

			sb.append("[");

			for (int i = 0;
				 i < messageBoardThread.getTaxonomyCategoryIds().length; i++) {

				sb.append(messageBoardThread.getTaxonomyCategoryIds()[i]);

				if ((i + 1) <
						messageBoardThread.getTaxonomyCategoryIds().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (messageBoardThread.getThreadType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"threadType\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardThread.getThreadType()));

			sb.append("\"");
		}

		if (messageBoardThread.getViewCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewCount\": ");

			sb.append(messageBoardThread.getViewCount());
		}

		if (messageBoardThread.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(messageBoardThread.getViewableBy());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MessageBoardThreadJSONParser messageBoardThreadJSONParser =
			new MessageBoardThreadJSONParser();

		return messageBoardThreadJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		MessageBoardThread messageBoardThread) {

		if (messageBoardThread == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (messageBoardThread.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(messageBoardThread.getActions()));
		}

		if (messageBoardThread.getAggregateRating() == null) {
			map.put("aggregateRating", null);
		}
		else {
			map.put(
				"aggregateRating",
				String.valueOf(messageBoardThread.getAggregateRating()));
		}

		if (messageBoardThread.getArticleBody() == null) {
			map.put("articleBody", null);
		}
		else {
			map.put(
				"articleBody",
				String.valueOf(messageBoardThread.getArticleBody()));
		}

		if (messageBoardThread.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(messageBoardThread.getCreator()));
		}

		if (messageBoardThread.getCreatorStatistics() == null) {
			map.put("creatorStatistics", null);
		}
		else {
			map.put(
				"creatorStatistics",
				String.valueOf(messageBoardThread.getCreatorStatistics()));
		}

		if (messageBoardThread.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(messageBoardThread.getCustomFields()));
		}

		if (messageBoardThread.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					messageBoardThread.getDateCreated()));
		}

		if (messageBoardThread.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					messageBoardThread.getDateModified()));
		}

		if (messageBoardThread.getEncodingFormat() == null) {
			map.put("encodingFormat", null);
		}
		else {
			map.put(
				"encodingFormat",
				String.valueOf(messageBoardThread.getEncodingFormat()));
		}

		if (messageBoardThread.getFriendlyUrlPath() == null) {
			map.put("friendlyUrlPath", null);
		}
		else {
			map.put(
				"friendlyUrlPath",
				String.valueOf(messageBoardThread.getFriendlyUrlPath()));
		}

		if (messageBoardThread.getHasValidAnswer() == null) {
			map.put("hasValidAnswer", null);
		}
		else {
			map.put(
				"hasValidAnswer",
				String.valueOf(messageBoardThread.getHasValidAnswer()));
		}

		if (messageBoardThread.getHeadline() == null) {
			map.put("headline", null);
		}
		else {
			map.put(
				"headline", String.valueOf(messageBoardThread.getHeadline()));
		}

		if (messageBoardThread.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(messageBoardThread.getId()));
		}

		if (messageBoardThread.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put(
				"keywords", String.valueOf(messageBoardThread.getKeywords()));
		}

		if (messageBoardThread.getLastPostDate() == null) {
			map.put("lastPostDate", null);
		}
		else {
			map.put(
				"lastPostDate",
				liferayToJSONDateFormat.format(
					messageBoardThread.getLastPostDate()));
		}

		if (messageBoardThread.getLocked() == null) {
			map.put("locked", null);
		}
		else {
			map.put("locked", String.valueOf(messageBoardThread.getLocked()));
		}

		if (messageBoardThread.getMessageBoardRootMessageId() == null) {
			map.put("messageBoardRootMessageId", null);
		}
		else {
			map.put(
				"messageBoardRootMessageId",
				String.valueOf(
					messageBoardThread.getMessageBoardRootMessageId()));
		}

		if (messageBoardThread.getMessageBoardSectionId() == null) {
			map.put("messageBoardSectionId", null);
		}
		else {
			map.put(
				"messageBoardSectionId",
				String.valueOf(messageBoardThread.getMessageBoardSectionId()));
		}

		if (messageBoardThread.getNumberOfMessageBoardAttachments() == null) {
			map.put("numberOfMessageBoardAttachments", null);
		}
		else {
			map.put(
				"numberOfMessageBoardAttachments",
				String.valueOf(
					messageBoardThread.getNumberOfMessageBoardAttachments()));
		}

		if (messageBoardThread.getNumberOfMessageBoardMessages() == null) {
			map.put("numberOfMessageBoardMessages", null);
		}
		else {
			map.put(
				"numberOfMessageBoardMessages",
				String.valueOf(
					messageBoardThread.getNumberOfMessageBoardMessages()));
		}

		if (messageBoardThread.getRelatedContents() == null) {
			map.put("relatedContents", null);
		}
		else {
			map.put(
				"relatedContents",
				String.valueOf(messageBoardThread.getRelatedContents()));
		}

		if (messageBoardThread.getSeen() == null) {
			map.put("seen", null);
		}
		else {
			map.put("seen", String.valueOf(messageBoardThread.getSeen()));
		}

		if (messageBoardThread.getShowAsQuestion() == null) {
			map.put("showAsQuestion", null);
		}
		else {
			map.put(
				"showAsQuestion",
				String.valueOf(messageBoardThread.getShowAsQuestion()));
		}

		if (messageBoardThread.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(messageBoardThread.getSiteId()));
		}

		if (messageBoardThread.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(messageBoardThread.getStatus()));
		}

		if (messageBoardThread.getSubscribed() == null) {
			map.put("subscribed", null);
		}
		else {
			map.put(
				"subscribed",
				String.valueOf(messageBoardThread.getSubscribed()));
		}

		if (messageBoardThread.getTaxonomyCategoryBriefs() == null) {
			map.put("taxonomyCategoryBriefs", null);
		}
		else {
			map.put(
				"taxonomyCategoryBriefs",
				String.valueOf(messageBoardThread.getTaxonomyCategoryBriefs()));
		}

		if (messageBoardThread.getTaxonomyCategoryIds() == null) {
			map.put("taxonomyCategoryIds", null);
		}
		else {
			map.put(
				"taxonomyCategoryIds",
				String.valueOf(messageBoardThread.getTaxonomyCategoryIds()));
		}

		if (messageBoardThread.getThreadType() == null) {
			map.put("threadType", null);
		}
		else {
			map.put(
				"threadType",
				String.valueOf(messageBoardThread.getThreadType()));
		}

		if (messageBoardThread.getViewCount() == null) {
			map.put("viewCount", null);
		}
		else {
			map.put(
				"viewCount", String.valueOf(messageBoardThread.getViewCount()));
		}

		if (messageBoardThread.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put(
				"viewableBy",
				String.valueOf(messageBoardThread.getViewableBy()));
		}

		return map;
	}

	public static class MessageBoardThreadJSONParser
		extends BaseJSONParser<MessageBoardThread> {

		@Override
		protected MessageBoardThread createDTO() {
			return new MessageBoardThread();
		}

		@Override
		protected MessageBoardThread[] createDTOArray(int size) {
			return new MessageBoardThread[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "aggregateRating")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "articleBody")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creatorStatistics")) {
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
			else if (Objects.equals(jsonParserFieldName, "encodingFormat")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "hasValidAnswer")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "headline")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastPostDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "locked")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "messageBoardRootMessageId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "messageBoardSectionId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"numberOfMessageBoardAttachments")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfMessageBoardMessages")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "relatedContents")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "seen")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "showAsQuestion")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
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
			else if (Objects.equals(jsonParserFieldName, "threadType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "viewCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			MessageBoardThread messageBoardThread, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "aggregateRating")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setAggregateRating(
						AggregateRatingSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "articleBody")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setArticleBody(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creatorStatistics")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setCreatorStatistics(
						CreatorStatisticsSerDes.toDTO(
							(String)jsonParserFieldValue));
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

					messageBoardThread.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "encodingFormat")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setEncodingFormat(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setFriendlyUrlPath(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "hasValidAnswer")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setHasValidAnswer(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "headline")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setHeadline(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setKeywords(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastPostDate")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setLastPostDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "locked")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setLocked((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "messageBoardRootMessageId")) {

				if (jsonParserFieldValue != null) {
					messageBoardThread.setMessageBoardRootMessageId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "messageBoardSectionId")) {

				if (jsonParserFieldValue != null) {
					messageBoardThread.setMessageBoardSectionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"numberOfMessageBoardAttachments")) {

				if (jsonParserFieldValue != null) {
					messageBoardThread.setNumberOfMessageBoardAttachments(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfMessageBoardMessages")) {

				if (jsonParserFieldValue != null) {
					messageBoardThread.setNumberOfMessageBoardMessages(
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

					messageBoardThread.setRelatedContents(relatedContentsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "seen")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setSeen((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "showAsQuestion")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setShowAsQuestion(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setStatus((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subscribed")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setSubscribed(
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

					messageBoardThread.setTaxonomyCategoryBriefs(
						taxonomyCategoryBriefsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryIds")) {

				if (jsonParserFieldValue != null) {
					messageBoardThread.setTaxonomyCategoryIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "threadType")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setThreadType(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewCount")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setViewCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					messageBoardThread.setViewableBy(
						MessageBoardThread.ViewableBy.create(
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