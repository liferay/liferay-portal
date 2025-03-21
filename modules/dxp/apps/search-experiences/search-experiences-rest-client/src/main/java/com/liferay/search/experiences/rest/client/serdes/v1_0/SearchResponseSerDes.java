/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.SearchResponse;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class SearchResponseSerDes {

	public static SearchResponse toDTO(String json) {
		SearchResponseJSONParser searchResponseJSONParser =
			new SearchResponseJSONParser();

		return searchResponseJSONParser.parseToDTO(json);
	}

	public static SearchResponse[] toDTOs(String json) {
		SearchResponseJSONParser searchResponseJSONParser =
			new SearchResponseJSONParser();

		return searchResponseJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SearchResponse searchResponse) {
		if (searchResponse == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (searchResponse.getErrors() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errors\": ");

			sb.append("[");

			for (int i = 0; i < searchResponse.getErrors().length; i++) {
				sb.append(searchResponse.getErrors()[i]);

				if ((i + 1) < searchResponse.getErrors().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (searchResponse.getPage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"page\": ");

			sb.append(searchResponse.getPage());
		}

		if (searchResponse.getPageSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSize\": ");

			sb.append(searchResponse.getPageSize());
		}

		if (searchResponse.getRequest() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"request\": ");

			if (searchResponse.getRequest() instanceof String) {
				sb.append("\"");
				sb.append((String)searchResponse.getRequest());
				sb.append("\"");
			}
			else {
				sb.append(searchResponse.getRequest());
			}
		}

		if (searchResponse.getRequestString() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestString\": ");

			sb.append("\"");

			sb.append(_escape(searchResponse.getRequestString()));

			sb.append("\"");
		}

		if (searchResponse.getResponse() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"response\": ");

			if (searchResponse.getResponse() instanceof String) {
				sb.append("\"");
				sb.append((String)searchResponse.getResponse());
				sb.append("\"");
			}
			else {
				sb.append(searchResponse.getResponse());
			}
		}

		if (searchResponse.getResponseString() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"responseString\": ");

			sb.append("\"");

			sb.append(_escape(searchResponse.getResponseString()));

			sb.append("\"");
		}

		if (searchResponse.getSearchHits() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchHits\": ");

			sb.append(String.valueOf(searchResponse.getSearchHits()));
		}

		if (searchResponse.getSearchRequest() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchRequest\": ");

			sb.append(String.valueOf(searchResponse.getSearchRequest()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SearchResponseJSONParser searchResponseJSONParser =
			new SearchResponseJSONParser();

		return searchResponseJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SearchResponse searchResponse) {
		if (searchResponse == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (searchResponse.getErrors() == null) {
			map.put("errors", null);
		}
		else {
			map.put("errors", String.valueOf(searchResponse.getErrors()));
		}

		if (searchResponse.getPage() == null) {
			map.put("page", null);
		}
		else {
			map.put("page", String.valueOf(searchResponse.getPage()));
		}

		if (searchResponse.getPageSize() == null) {
			map.put("pageSize", null);
		}
		else {
			map.put("pageSize", String.valueOf(searchResponse.getPageSize()));
		}

		if (searchResponse.getRequest() == null) {
			map.put("request", null);
		}
		else {
			map.put("request", String.valueOf(searchResponse.getRequest()));
		}

		if (searchResponse.getRequestString() == null) {
			map.put("requestString", null);
		}
		else {
			map.put(
				"requestString",
				String.valueOf(searchResponse.getRequestString()));
		}

		if (searchResponse.getResponse() == null) {
			map.put("response", null);
		}
		else {
			map.put("response", String.valueOf(searchResponse.getResponse()));
		}

		if (searchResponse.getResponseString() == null) {
			map.put("responseString", null);
		}
		else {
			map.put(
				"responseString",
				String.valueOf(searchResponse.getResponseString()));
		}

		if (searchResponse.getSearchHits() == null) {
			map.put("searchHits", null);
		}
		else {
			map.put(
				"searchHits", String.valueOf(searchResponse.getSearchHits()));
		}

		if (searchResponse.getSearchRequest() == null) {
			map.put("searchRequest", null);
		}
		else {
			map.put(
				"searchRequest",
				String.valueOf(searchResponse.getSearchRequest()));
		}

		return map;
	}

	public static class SearchResponseJSONParser
		extends BaseJSONParser<SearchResponse> {

		@Override
		protected SearchResponse createDTO() {
			return new SearchResponse();
		}

		@Override
		protected SearchResponse[] createDTOArray(int size) {
			return new SearchResponse[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "errors")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "page")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageSize")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "request")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "requestString")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "response")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "responseString")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "searchHits")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "searchRequest")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SearchResponse searchResponse, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "errors")) {
				if (jsonParserFieldValue != null) {
					searchResponse.setErrors((Map[])jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "page")) {
				if (jsonParserFieldValue != null) {
					searchResponse.setPage(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageSize")) {
				if (jsonParserFieldValue != null) {
					searchResponse.setPageSize(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "request")) {
				if (jsonParserFieldValue != null) {
					searchResponse.setRequest((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "requestString")) {
				if (jsonParserFieldValue != null) {
					searchResponse.setRequestString(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "response")) {
				if (jsonParserFieldValue != null) {
					searchResponse.setResponse((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "responseString")) {
				if (jsonParserFieldValue != null) {
					searchResponse.setResponseString(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "searchHits")) {
				if (jsonParserFieldValue != null) {
					searchResponse.setSearchHits(
						SearchHitsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "searchRequest")) {
				if (jsonParserFieldValue != null) {
					searchResponse.setSearchRequest(
						SearchRequestSerDes.toDTO(
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