/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.serdes.v1_0;

import com.liferay.portal.search.rest.client.dto.v1_0.SearchResult;
import com.liferay.portal.search.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class SearchResultSerDes {

	public static SearchResult toDTO(String json) {
		SearchResultJSONParser searchResultJSONParser =
			new SearchResultJSONParser();

		return searchResultJSONParser.parseToDTO(json);
	}

	public static SearchResult[] toDTOs(String json) {
		SearchResultJSONParser searchResultJSONParser =
			new SearchResultJSONParser();

		return searchResultJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SearchResult searchResult) {
		if (searchResult == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (searchResult.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(searchResult.getActions()));
		}

		if (searchResult.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(searchResult.getDateCreated()));

			sb.append("\"");
		}

		if (searchResult.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(searchResult.getDateModified()));

			sb.append("\"");
		}

		if (searchResult.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(searchResult.getDescription()));

			sb.append("\"");
		}

		if (searchResult.getEmbedded() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"embedded\": ");

			if (searchResult.getEmbedded() instanceof String) {
				sb.append("\"");
				sb.append((String)searchResult.getEmbedded());
				sb.append("\"");
			}
			else {
				sb.append(searchResult.getEmbedded());
			}
		}

		if (searchResult.getEntryClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entryClassName\": ");

			sb.append("\"");

			sb.append(_escape(searchResult.getEntryClassName()));

			sb.append("\"");
		}

		if (searchResult.getItemURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemURL\": ");

			sb.append("\"");

			sb.append(_escape(searchResult.getItemURL()));

			sb.append("\"");
		}

		if (searchResult.getScore() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"score\": ");

			sb.append(searchResult.getScore());
		}

		if (searchResult.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(searchResult.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SearchResultJSONParser searchResultJSONParser =
			new SearchResultJSONParser();

		return searchResultJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SearchResult searchResult) {
		if (searchResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (searchResult.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(searchResult.getActions()));
		}

		if (searchResult.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(searchResult.getDateCreated()));
		}

		if (searchResult.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(searchResult.getDateModified()));
		}

		if (searchResult.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(searchResult.getDescription()));
		}

		if (searchResult.getEmbedded() == null) {
			map.put("embedded", null);
		}
		else {
			map.put("embedded", String.valueOf(searchResult.getEmbedded()));
		}

		if (searchResult.getEntryClassName() == null) {
			map.put("entryClassName", null);
		}
		else {
			map.put(
				"entryClassName",
				String.valueOf(searchResult.getEntryClassName()));
		}

		if (searchResult.getItemURL() == null) {
			map.put("itemURL", null);
		}
		else {
			map.put("itemURL", String.valueOf(searchResult.getItemURL()));
		}

		if (searchResult.getScore() == null) {
			map.put("score", null);
		}
		else {
			map.put("score", String.valueOf(searchResult.getScore()));
		}

		if (searchResult.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(searchResult.getTitle()));
		}

		return map;
	}

	public static class SearchResultJSONParser
		extends BaseJSONParser<SearchResult> {

		@Override
		protected SearchResult createDTO() {
			return new SearchResult();
		}

		@Override
		protected SearchResult[] createDTOArray(int size) {
			return new SearchResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "embedded")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "entryClassName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "itemURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "score")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SearchResult searchResult, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					searchResult.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					searchResult.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					searchResult.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					searchResult.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "embedded")) {
				if (jsonParserFieldValue != null) {
					searchResult.setEmbedded((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "entryClassName")) {
				if (jsonParserFieldValue != null) {
					searchResult.setEntryClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "itemURL")) {
				if (jsonParserFieldValue != null) {
					searchResult.setItemURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "score")) {
				if (jsonParserFieldValue != null) {
					searchResult.setScore(
						Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					searchResult.setTitle((String)jsonParserFieldValue);
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