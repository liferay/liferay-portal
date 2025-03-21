/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.QueryResponse;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class QueryResponseSerDes {

	public static QueryResponse toDTO(String json) {
		QueryResponseJSONParser queryResponseJSONParser =
			new QueryResponseJSONParser();

		return queryResponseJSONParser.parseToDTO(json);
	}

	public static QueryResponse[] toDTOs(String json) {
		QueryResponseJSONParser queryResponseJSONParser =
			new QueryResponseJSONParser();

		return queryResponseJSONParser.parseToDTOs(json);
	}

	public static String toJSON(QueryResponse queryResponse) {
		if (queryResponse == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (queryResponse.getResources() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"Resources\": ");

			if (queryResponse.getResources() instanceof String) {
				sb.append("\"");
				sb.append((String)queryResponse.getResources());
				sb.append("\"");
			}
			else {
				sb.append(queryResponse.getResources());
			}
		}

		if (queryResponse.getItemsPerPage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemsPerPage\": ");

			sb.append(queryResponse.getItemsPerPage());
		}

		if (queryResponse.getStartIndex() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startIndex\": ");

			sb.append(queryResponse.getStartIndex());
		}

		if (queryResponse.getTotalResults() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalResults\": ");

			sb.append(queryResponse.getTotalResults());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		QueryResponseJSONParser queryResponseJSONParser =
			new QueryResponseJSONParser();

		return queryResponseJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(QueryResponse queryResponse) {
		if (queryResponse == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (queryResponse.getResources() == null) {
			map.put("Resources", null);
		}
		else {
			map.put("Resources", String.valueOf(queryResponse.getResources()));
		}

		if (queryResponse.getItemsPerPage() == null) {
			map.put("itemsPerPage", null);
		}
		else {
			map.put(
				"itemsPerPage",
				String.valueOf(queryResponse.getItemsPerPage()));
		}

		if (queryResponse.getStartIndex() == null) {
			map.put("startIndex", null);
		}
		else {
			map.put(
				"startIndex", String.valueOf(queryResponse.getStartIndex()));
		}

		if (queryResponse.getTotalResults() == null) {
			map.put("totalResults", null);
		}
		else {
			map.put(
				"totalResults",
				String.valueOf(queryResponse.getTotalResults()));
		}

		return map;
	}

	public static class QueryResponseJSONParser
		extends BaseJSONParser<QueryResponse> {

		@Override
		protected QueryResponse createDTO() {
			return new QueryResponse();
		}

		@Override
		protected QueryResponse[] createDTOArray(int size) {
			return new QueryResponse[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "Resources")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "itemsPerPage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "startIndex")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalResults")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			QueryResponse queryResponse, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "Resources")) {
				if (jsonParserFieldValue != null) {
					queryResponse.setResources((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "itemsPerPage")) {
				if (jsonParserFieldValue != null) {
					queryResponse.setItemsPerPage(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "startIndex")) {
				if (jsonParserFieldValue != null) {
					queryResponse.setStartIndex(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalResults")) {
				if (jsonParserFieldValue != null) {
					queryResponse.setTotalResults(
						Integer.valueOf((String)jsonParserFieldValue));
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