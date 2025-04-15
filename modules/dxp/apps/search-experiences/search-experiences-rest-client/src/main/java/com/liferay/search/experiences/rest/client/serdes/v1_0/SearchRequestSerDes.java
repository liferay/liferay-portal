/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.SearchRequest;
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
public class SearchRequestSerDes {

	public static SearchRequest toDTO(String json) {
		SearchRequestJSONParser searchRequestJSONParser =
			new SearchRequestJSONParser();

		return searchRequestJSONParser.parseToDTO(json);
	}

	public static SearchRequest[] toDTOs(String json) {
		SearchRequestJSONParser searchRequestJSONParser =
			new SearchRequestJSONParser();

		return searchRequestJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SearchRequest searchRequest) {
		if (searchRequest == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (searchRequest.getQueryString() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"queryString\": ");

			sb.append("\"");

			sb.append(_escape(searchRequest.getQueryString()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SearchRequestJSONParser searchRequestJSONParser =
			new SearchRequestJSONParser();

		return searchRequestJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SearchRequest searchRequest) {
		if (searchRequest == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (searchRequest.getQueryString() == null) {
			map.put("queryString", null);
		}
		else {
			map.put(
				"queryString", String.valueOf(searchRequest.getQueryString()));
		}

		return map;
	}

	public static class SearchRequestJSONParser
		extends BaseJSONParser<SearchRequest> {

		@Override
		protected SearchRequest createDTO() {
			return new SearchRequest();
		}

		@Override
		protected SearchRequest[] createDTOArray(int size) {
			return new SearchRequest[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "queryString")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SearchRequest searchRequest, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "queryString")) {
				if (jsonParserFieldValue != null) {
					searchRequest.setQueryString((String)jsonParserFieldValue);
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