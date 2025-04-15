/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.Filter;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class FilterSerDes {

	public static Filter toDTO(String json) {
		FilterJSONParser filterJSONParser = new FilterJSONParser();

		return filterJSONParser.parseToDTO(json);
	}

	public static Filter[] toDTOs(String json) {
		FilterJSONParser filterJSONParser = new FilterJSONParser();

		return filterJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Filter filter) {
		if (filter == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (filter.getMaxResults() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxResults\": ");

			sb.append(filter.getMaxResults());
		}

		if (filter.getSupported() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"supported\": ");

			sb.append(filter.getSupported());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FilterJSONParser filterJSONParser = new FilterJSONParser();

		return filterJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Filter filter) {
		if (filter == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (filter.getMaxResults() == null) {
			map.put("maxResults", null);
		}
		else {
			map.put("maxResults", String.valueOf(filter.getMaxResults()));
		}

		if (filter.getSupported() == null) {
			map.put("supported", null);
		}
		else {
			map.put("supported", String.valueOf(filter.getSupported()));
		}

		return map;
	}

	public static class FilterJSONParser extends BaseJSONParser<Filter> {

		@Override
		protected Filter createDTO() {
			return new Filter();
		}

		@Override
		protected Filter[] createDTOArray(int size) {
			return new Filter[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "maxResults")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "supported")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Filter filter, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "maxResults")) {
				if (jsonParserFieldValue != null) {
					filter.setMaxResults(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "supported")) {
				if (jsonParserFieldValue != null) {
					filter.setSupported((Boolean)jsonParserFieldValue);
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