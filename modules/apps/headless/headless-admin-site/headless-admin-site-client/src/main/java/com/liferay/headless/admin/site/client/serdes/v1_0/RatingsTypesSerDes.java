/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.RatingsTypes;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class RatingsTypesSerDes {

	public static RatingsTypes toDTO(String json) {
		RatingsTypesJSONParser ratingsTypesJSONParser =
			new RatingsTypesJSONParser();

		return ratingsTypesJSONParser.parseToDTO(json);
	}

	public static RatingsTypes[] toDTOs(String json) {
		RatingsTypesJSONParser ratingsTypesJSONParser =
			new RatingsTypesJSONParser();

		return ratingsTypesJSONParser.parseToDTOs(json);
	}

	public static String toJSON(RatingsTypes ratingsTypes) {
		if (ratingsTypes == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (ratingsTypes.getBlogPosting() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"blogPosting\": ");

			sb.append("\"");
			sb.append(ratingsTypes.getBlogPosting());
			sb.append("\"");
		}

		if (ratingsTypes.getBookmarksEntry() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bookmarksEntry\": ");

			sb.append("\"");
			sb.append(ratingsTypes.getBookmarksEntry());
			sb.append("\"");
		}

		if (ratingsTypes.getComment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comment\": ");

			sb.append("\"");
			sb.append(ratingsTypes.getComment());
			sb.append("\"");
		}

		if (ratingsTypes.getDocument() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"document\": ");

			sb.append("\"");
			sb.append(ratingsTypes.getDocument());
			sb.append("\"");
		}

		if (ratingsTypes.getKnowledgeBaseArticle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"knowledgeBaseArticle\": ");

			sb.append("\"");
			sb.append(ratingsTypes.getKnowledgeBaseArticle());
			sb.append("\"");
		}

		if (ratingsTypes.getMessageBoardMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"messageBoardMessage\": ");

			sb.append("\"");
			sb.append(ratingsTypes.getMessageBoardMessage());
			sb.append("\"");
		}

		if (ratingsTypes.getSitePage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitePage\": ");

			sb.append("\"");
			sb.append(ratingsTypes.getSitePage());
			sb.append("\"");
		}

		if (ratingsTypes.getStructuredContent() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"structuredContent\": ");

			sb.append("\"");
			sb.append(ratingsTypes.getStructuredContent());
			sb.append("\"");
		}

		if (ratingsTypes.getWikiPage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"wikiPage\": ");

			sb.append("\"");
			sb.append(ratingsTypes.getWikiPage());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RatingsTypesJSONParser ratingsTypesJSONParser =
			new RatingsTypesJSONParser();

		return ratingsTypesJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(RatingsTypes ratingsTypes) {
		if (ratingsTypes == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (ratingsTypes.getBlogPosting() == null) {
			map.put("blogPosting", null);
		}
		else {
			map.put(
				"blogPosting", String.valueOf(ratingsTypes.getBlogPosting()));
		}

		if (ratingsTypes.getBookmarksEntry() == null) {
			map.put("bookmarksEntry", null);
		}
		else {
			map.put(
				"bookmarksEntry",
				String.valueOf(ratingsTypes.getBookmarksEntry()));
		}

		if (ratingsTypes.getComment() == null) {
			map.put("comment", null);
		}
		else {
			map.put("comment", String.valueOf(ratingsTypes.getComment()));
		}

		if (ratingsTypes.getDocument() == null) {
			map.put("document", null);
		}
		else {
			map.put("document", String.valueOf(ratingsTypes.getDocument()));
		}

		if (ratingsTypes.getKnowledgeBaseArticle() == null) {
			map.put("knowledgeBaseArticle", null);
		}
		else {
			map.put(
				"knowledgeBaseArticle",
				String.valueOf(ratingsTypes.getKnowledgeBaseArticle()));
		}

		if (ratingsTypes.getMessageBoardMessage() == null) {
			map.put("messageBoardMessage", null);
		}
		else {
			map.put(
				"messageBoardMessage",
				String.valueOf(ratingsTypes.getMessageBoardMessage()));
		}

		if (ratingsTypes.getSitePage() == null) {
			map.put("sitePage", null);
		}
		else {
			map.put("sitePage", String.valueOf(ratingsTypes.getSitePage()));
		}

		if (ratingsTypes.getStructuredContent() == null) {
			map.put("structuredContent", null);
		}
		else {
			map.put(
				"structuredContent",
				String.valueOf(ratingsTypes.getStructuredContent()));
		}

		if (ratingsTypes.getWikiPage() == null) {
			map.put("wikiPage", null);
		}
		else {
			map.put("wikiPage", String.valueOf(ratingsTypes.getWikiPage()));
		}

		return map;
	}

	public static class RatingsTypesJSONParser
		extends BaseJSONParser<RatingsTypes> {

		@Override
		protected RatingsTypes createDTO() {
			return new RatingsTypes();
		}

		@Override
		protected RatingsTypes[] createDTOArray(int size) {
			return new RatingsTypes[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "blogPosting")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "bookmarksEntry")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "comment")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "document")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "knowledgeBaseArticle")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "messageBoardMessage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sitePage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "structuredContent")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "wikiPage")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			RatingsTypes ratingsTypes, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "blogPosting")) {
				if (jsonParserFieldValue != null) {
					ratingsTypes.setBlogPosting(
						RatingsTypes.BlogPosting.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "bookmarksEntry")) {
				if (jsonParserFieldValue != null) {
					ratingsTypes.setBookmarksEntry(
						RatingsTypes.BookmarksEntry.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "comment")) {
				if (jsonParserFieldValue != null) {
					ratingsTypes.setComment(
						RatingsTypes.Comment.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "document")) {
				if (jsonParserFieldValue != null) {
					ratingsTypes.setDocument(
						RatingsTypes.Document.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "knowledgeBaseArticle")) {

				if (jsonParserFieldValue != null) {
					ratingsTypes.setKnowledgeBaseArticle(
						RatingsTypes.KnowledgeBaseArticle.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "messageBoardMessage")) {

				if (jsonParserFieldValue != null) {
					ratingsTypes.setMessageBoardMessage(
						RatingsTypes.MessageBoardMessage.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sitePage")) {
				if (jsonParserFieldValue != null) {
					ratingsTypes.setSitePage(
						RatingsTypes.SitePage.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "structuredContent")) {
				if (jsonParserFieldValue != null) {
					ratingsTypes.setStructuredContent(
						RatingsTypes.StructuredContent.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "wikiPage")) {
				if (jsonParserFieldValue != null) {
					ratingsTypes.setWikiPage(
						RatingsTypes.WikiPage.create(
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:151032605