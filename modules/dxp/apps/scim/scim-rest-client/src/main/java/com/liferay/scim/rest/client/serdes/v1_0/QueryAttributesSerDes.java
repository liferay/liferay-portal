/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.QueryAttributes;
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
public class QueryAttributesSerDes {

	public static QueryAttributes toDTO(String json) {
		QueryAttributesJSONParser queryAttributesJSONParser =
			new QueryAttributesJSONParser();

		return queryAttributesJSONParser.parseToDTO(json);
	}

	public static QueryAttributes[] toDTOs(String json) {
		QueryAttributesJSONParser queryAttributesJSONParser =
			new QueryAttributesJSONParser();

		return queryAttributesJSONParser.parseToDTOs(json);
	}

	public static String toJSON(QueryAttributes queryAttributes) {
		if (queryAttributes == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (queryAttributes.getAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			sb.append("[");

			for (int i = 0; i < queryAttributes.getAttributes().length; i++) {
				sb.append(_toJSON(queryAttributes.getAttributes()[i]));

				if ((i + 1) < queryAttributes.getAttributes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (queryAttributes.getCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"count\": ");

			sb.append(queryAttributes.getCount());
		}

		if (queryAttributes.getExcludedAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"excludedAttributes\": ");

			sb.append("[");

			for (int i = 0; i < queryAttributes.getExcludedAttributes().length;
				 i++) {

				sb.append(_toJSON(queryAttributes.getExcludedAttributes()[i]));

				if ((i + 1) < queryAttributes.getExcludedAttributes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (queryAttributes.getFilter() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"filter\": ");

			sb.append("\"");

			sb.append(_escape(queryAttributes.getFilter()));

			sb.append("\"");
		}

		if (queryAttributes.getSortBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sortBy\": ");

			sb.append("\"");

			sb.append(_escape(queryAttributes.getSortBy()));

			sb.append("\"");
		}

		if (queryAttributes.getSortOrder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sortOrder\": ");

			sb.append("\"");

			sb.append(_escape(queryAttributes.getSortOrder()));

			sb.append("\"");
		}

		if (queryAttributes.getStartIndex() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startIndex\": ");

			sb.append(queryAttributes.getStartIndex());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		QueryAttributesJSONParser queryAttributesJSONParser =
			new QueryAttributesJSONParser();

		return queryAttributesJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(QueryAttributes queryAttributes) {
		if (queryAttributes == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (queryAttributes.getAttributes() == null) {
			map.put("attributes", null);
		}
		else {
			map.put(
				"attributes", String.valueOf(queryAttributes.getAttributes()));
		}

		if (queryAttributes.getCount() == null) {
			map.put("count", null);
		}
		else {
			map.put("count", String.valueOf(queryAttributes.getCount()));
		}

		if (queryAttributes.getExcludedAttributes() == null) {
			map.put("excludedAttributes", null);
		}
		else {
			map.put(
				"excludedAttributes",
				String.valueOf(queryAttributes.getExcludedAttributes()));
		}

		if (queryAttributes.getFilter() == null) {
			map.put("filter", null);
		}
		else {
			map.put("filter", String.valueOf(queryAttributes.getFilter()));
		}

		if (queryAttributes.getSortBy() == null) {
			map.put("sortBy", null);
		}
		else {
			map.put("sortBy", String.valueOf(queryAttributes.getSortBy()));
		}

		if (queryAttributes.getSortOrder() == null) {
			map.put("sortOrder", null);
		}
		else {
			map.put(
				"sortOrder", String.valueOf(queryAttributes.getSortOrder()));
		}

		if (queryAttributes.getStartIndex() == null) {
			map.put("startIndex", null);
		}
		else {
			map.put(
				"startIndex", String.valueOf(queryAttributes.getStartIndex()));
		}

		return map;
	}

	public static class QueryAttributesJSONParser
		extends BaseJSONParser<QueryAttributes> {

		@Override
		protected QueryAttributes createDTO() {
			return new QueryAttributes();
		}

		@Override
		protected QueryAttributes[] createDTOArray(int size) {
			return new QueryAttributes[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "attributes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "count")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "excludedAttributes")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "filter")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sortBy")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sortOrder")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "startIndex")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			QueryAttributes queryAttributes, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "attributes")) {
				if (jsonParserFieldValue != null) {
					queryAttributes.setAttributes(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "count")) {
				if (jsonParserFieldValue != null) {
					queryAttributes.setCount(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "excludedAttributes")) {

				if (jsonParserFieldValue != null) {
					queryAttributes.setExcludedAttributes(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "filter")) {
				if (jsonParserFieldValue != null) {
					queryAttributes.setFilter((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sortBy")) {
				if (jsonParserFieldValue != null) {
					queryAttributes.setSortBy((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sortOrder")) {
				if (jsonParserFieldValue != null) {
					queryAttributes.setSortOrder((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "startIndex")) {
				if (jsonParserFieldValue != null) {
					queryAttributes.setStartIndex(
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