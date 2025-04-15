/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.MessageBoardMessage;
import com.liferay.headless.delivery.client.dto.v1_0.RelatedContent;
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
public class MessageBoardMessageSerDes {

	public static MessageBoardMessage toDTO(String json) {
		MessageBoardMessageJSONParser messageBoardMessageJSONParser =
			new MessageBoardMessageJSONParser();

		return messageBoardMessageJSONParser.parseToDTO(json);
	}

	public static MessageBoardMessage[] toDTOs(String json) {
		MessageBoardMessageJSONParser messageBoardMessageJSONParser =
			new MessageBoardMessageJSONParser();

		return messageBoardMessageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MessageBoardMessage messageBoardMessage) {
		if (messageBoardMessage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (messageBoardMessage.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(messageBoardMessage.getActions()));
		}

		if (messageBoardMessage.getAggregateRating() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aggregateRating\": ");

			sb.append(String.valueOf(messageBoardMessage.getAggregateRating()));
		}

		if (messageBoardMessage.getAnonymous() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"anonymous\": ");

			sb.append(messageBoardMessage.getAnonymous());
		}

		if (messageBoardMessage.getArticleBody() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"articleBody\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardMessage.getArticleBody()));

			sb.append("\"");
		}

		if (messageBoardMessage.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(messageBoardMessage.getCreator()));
		}

		if (messageBoardMessage.getCreatorStatistics() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorStatistics\": ");

			sb.append(
				String.valueOf(messageBoardMessage.getCreatorStatistics()));
		}

		if (messageBoardMessage.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < messageBoardMessage.getCustomFields().length;
				 i++) {

				sb.append(messageBoardMessage.getCustomFields()[i]);

				if ((i + 1) < messageBoardMessage.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (messageBoardMessage.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					messageBoardMessage.getDateCreated()));

			sb.append("\"");
		}

		if (messageBoardMessage.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					messageBoardMessage.getDateModified()));

			sb.append("\"");
		}

		if (messageBoardMessage.getEncodingFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"encodingFormat\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardMessage.getEncodingFormat()));

			sb.append("\"");
		}

		if (messageBoardMessage.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardMessage.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (messageBoardMessage.getFriendlyUrlPath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardMessage.getFriendlyUrlPath()));

			sb.append("\"");
		}

		if (messageBoardMessage.getHasCompanyMx() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hasCompanyMx\": ");

			sb.append(messageBoardMessage.getHasCompanyMx());
		}

		if (messageBoardMessage.getHeadline() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"headline\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardMessage.getHeadline()));

			sb.append("\"");
		}

		if (messageBoardMessage.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(messageBoardMessage.getId());
		}

		if (messageBoardMessage.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < messageBoardMessage.getKeywords().length; i++) {
				sb.append(_toJSON(messageBoardMessage.getKeywords()[i]));

				if ((i + 1) < messageBoardMessage.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (messageBoardMessage.getMessageBoardSectionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"messageBoardSectionId\": ");

			sb.append(messageBoardMessage.getMessageBoardSectionId());
		}

		if (messageBoardMessage.getMessageBoardThreadId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"messageBoardThreadId\": ");

			sb.append(messageBoardMessage.getMessageBoardThreadId());
		}

		if (messageBoardMessage.getModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modified\": ");

			sb.append(messageBoardMessage.getModified());
		}

		if (messageBoardMessage.getNumberOfMessageBoardAttachments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfMessageBoardAttachments\": ");

			sb.append(messageBoardMessage.getNumberOfMessageBoardAttachments());
		}

		if (messageBoardMessage.getNumberOfMessageBoardMessages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfMessageBoardMessages\": ");

			sb.append(messageBoardMessage.getNumberOfMessageBoardMessages());
		}

		if (messageBoardMessage.getParentMessageBoardMessageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentMessageBoardMessageId\": ");

			sb.append(messageBoardMessage.getParentMessageBoardMessageId());
		}

		if (messageBoardMessage.getRelatedContents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedContents\": ");

			sb.append("[");

			for (int i = 0; i < messageBoardMessage.getRelatedContents().length;
				 i++) {

				sb.append(
					String.valueOf(
						messageBoardMessage.getRelatedContents()[i]));

				if ((i + 1) < messageBoardMessage.getRelatedContents().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (messageBoardMessage.getShowAsAnswer() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showAsAnswer\": ");

			sb.append(messageBoardMessage.getShowAsAnswer());
		}

		if (messageBoardMessage.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(messageBoardMessage.getSiteId());
		}

		if (messageBoardMessage.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardMessage.getStatus()));

			sb.append("\"");
		}

		if (messageBoardMessage.getSubscribed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscribed\": ");

			sb.append(messageBoardMessage.getSubscribed());
		}

		if (messageBoardMessage.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(messageBoardMessage.getViewableBy());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MessageBoardMessageJSONParser messageBoardMessageJSONParser =
			new MessageBoardMessageJSONParser();

		return messageBoardMessageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		MessageBoardMessage messageBoardMessage) {

		if (messageBoardMessage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (messageBoardMessage.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(messageBoardMessage.getActions()));
		}

		if (messageBoardMessage.getAggregateRating() == null) {
			map.put("aggregateRating", null);
		}
		else {
			map.put(
				"aggregateRating",
				String.valueOf(messageBoardMessage.getAggregateRating()));
		}

		if (messageBoardMessage.getAnonymous() == null) {
			map.put("anonymous", null);
		}
		else {
			map.put(
				"anonymous",
				String.valueOf(messageBoardMessage.getAnonymous()));
		}

		if (messageBoardMessage.getArticleBody() == null) {
			map.put("articleBody", null);
		}
		else {
			map.put(
				"articleBody",
				String.valueOf(messageBoardMessage.getArticleBody()));
		}

		if (messageBoardMessage.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator", String.valueOf(messageBoardMessage.getCreator()));
		}

		if (messageBoardMessage.getCreatorStatistics() == null) {
			map.put("creatorStatistics", null);
		}
		else {
			map.put(
				"creatorStatistics",
				String.valueOf(messageBoardMessage.getCreatorStatistics()));
		}

		if (messageBoardMessage.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(messageBoardMessage.getCustomFields()));
		}

		if (messageBoardMessage.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					messageBoardMessage.getDateCreated()));
		}

		if (messageBoardMessage.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					messageBoardMessage.getDateModified()));
		}

		if (messageBoardMessage.getEncodingFormat() == null) {
			map.put("encodingFormat", null);
		}
		else {
			map.put(
				"encodingFormat",
				String.valueOf(messageBoardMessage.getEncodingFormat()));
		}

		if (messageBoardMessage.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(messageBoardMessage.getExternalReferenceCode()));
		}

		if (messageBoardMessage.getFriendlyUrlPath() == null) {
			map.put("friendlyUrlPath", null);
		}
		else {
			map.put(
				"friendlyUrlPath",
				String.valueOf(messageBoardMessage.getFriendlyUrlPath()));
		}

		if (messageBoardMessage.getHasCompanyMx() == null) {
			map.put("hasCompanyMx", null);
		}
		else {
			map.put(
				"hasCompanyMx",
				String.valueOf(messageBoardMessage.getHasCompanyMx()));
		}

		if (messageBoardMessage.getHeadline() == null) {
			map.put("headline", null);
		}
		else {
			map.put(
				"headline", String.valueOf(messageBoardMessage.getHeadline()));
		}

		if (messageBoardMessage.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(messageBoardMessage.getId()));
		}

		if (messageBoardMessage.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put(
				"keywords", String.valueOf(messageBoardMessage.getKeywords()));
		}

		if (messageBoardMessage.getMessageBoardSectionId() == null) {
			map.put("messageBoardSectionId", null);
		}
		else {
			map.put(
				"messageBoardSectionId",
				String.valueOf(messageBoardMessage.getMessageBoardSectionId()));
		}

		if (messageBoardMessage.getMessageBoardThreadId() == null) {
			map.put("messageBoardThreadId", null);
		}
		else {
			map.put(
				"messageBoardThreadId",
				String.valueOf(messageBoardMessage.getMessageBoardThreadId()));
		}

		if (messageBoardMessage.getModified() == null) {
			map.put("modified", null);
		}
		else {
			map.put(
				"modified", String.valueOf(messageBoardMessage.getModified()));
		}

		if (messageBoardMessage.getNumberOfMessageBoardAttachments() == null) {
			map.put("numberOfMessageBoardAttachments", null);
		}
		else {
			map.put(
				"numberOfMessageBoardAttachments",
				String.valueOf(
					messageBoardMessage.getNumberOfMessageBoardAttachments()));
		}

		if (messageBoardMessage.getNumberOfMessageBoardMessages() == null) {
			map.put("numberOfMessageBoardMessages", null);
		}
		else {
			map.put(
				"numberOfMessageBoardMessages",
				String.valueOf(
					messageBoardMessage.getNumberOfMessageBoardMessages()));
		}

		if (messageBoardMessage.getParentMessageBoardMessageId() == null) {
			map.put("parentMessageBoardMessageId", null);
		}
		else {
			map.put(
				"parentMessageBoardMessageId",
				String.valueOf(
					messageBoardMessage.getParentMessageBoardMessageId()));
		}

		if (messageBoardMessage.getRelatedContents() == null) {
			map.put("relatedContents", null);
		}
		else {
			map.put(
				"relatedContents",
				String.valueOf(messageBoardMessage.getRelatedContents()));
		}

		if (messageBoardMessage.getShowAsAnswer() == null) {
			map.put("showAsAnswer", null);
		}
		else {
			map.put(
				"showAsAnswer",
				String.valueOf(messageBoardMessage.getShowAsAnswer()));
		}

		if (messageBoardMessage.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(messageBoardMessage.getSiteId()));
		}

		if (messageBoardMessage.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(messageBoardMessage.getStatus()));
		}

		if (messageBoardMessage.getSubscribed() == null) {
			map.put("subscribed", null);
		}
		else {
			map.put(
				"subscribed",
				String.valueOf(messageBoardMessage.getSubscribed()));
		}

		if (messageBoardMessage.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put(
				"viewableBy",
				String.valueOf(messageBoardMessage.getViewableBy()));
		}

		return map;
	}

	public static class MessageBoardMessageJSONParser
		extends BaseJSONParser<MessageBoardMessage> {

		@Override
		protected MessageBoardMessage createDTO() {
			return new MessageBoardMessage();
		}

		@Override
		protected MessageBoardMessage[] createDTOArray(int size) {
			return new MessageBoardMessage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "aggregateRating")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "anonymous")) {
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
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "hasCompanyMx")) {
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
			else if (Objects.equals(
						jsonParserFieldName, "messageBoardSectionId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "messageBoardThreadId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modified")) {
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
			else if (Objects.equals(
						jsonParserFieldName, "parentMessageBoardMessageId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "relatedContents")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "showAsAnswer")) {
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
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			MessageBoardMessage messageBoardMessage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "aggregateRating")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setAggregateRating(
						AggregateRatingSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "anonymous")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setAnonymous(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "articleBody")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setArticleBody(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creatorStatistics")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setCreatorStatistics(
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

					messageBoardMessage.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "encodingFormat")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setEncodingFormat(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					messageBoardMessage.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setFriendlyUrlPath(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "hasCompanyMx")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setHasCompanyMx(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "headline")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setHeadline(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setKeywords(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "messageBoardSectionId")) {

				if (jsonParserFieldValue != null) {
					messageBoardMessage.setMessageBoardSectionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "messageBoardThreadId")) {

				if (jsonParserFieldValue != null) {
					messageBoardMessage.setMessageBoardThreadId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modified")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setModified(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"numberOfMessageBoardAttachments")) {

				if (jsonParserFieldValue != null) {
					messageBoardMessage.setNumberOfMessageBoardAttachments(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfMessageBoardMessages")) {

				if (jsonParserFieldValue != null) {
					messageBoardMessage.setNumberOfMessageBoardMessages(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentMessageBoardMessageId")) {

				if (jsonParserFieldValue != null) {
					messageBoardMessage.setParentMessageBoardMessageId(
						Long.valueOf((String)jsonParserFieldValue));
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

					messageBoardMessage.setRelatedContents(
						relatedContentsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "showAsAnswer")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setShowAsAnswer(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setStatus((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subscribed")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setSubscribed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					messageBoardMessage.setViewableBy(
						MessageBoardMessage.ViewableBy.create(
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