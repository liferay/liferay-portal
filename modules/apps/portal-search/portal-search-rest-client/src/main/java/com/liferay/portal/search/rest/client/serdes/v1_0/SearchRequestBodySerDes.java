/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.serdes.v1_0;

import com.liferay.portal.search.rest.client.dto.v1_0.FacetConfiguration;
import com.liferay.portal.search.rest.client.dto.v1_0.SearchRequestBody;
import com.liferay.portal.search.rest.client.json.BaseJSONParser;

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
public class SearchRequestBodySerDes {

	public static SearchRequestBody toDTO(String json) {
		SearchRequestBodyJSONParser searchRequestBodyJSONParser =
			new SearchRequestBodyJSONParser();

		return searchRequestBodyJSONParser.parseToDTO(json);
	}

	public static SearchRequestBody[] toDTOs(String json) {
		SearchRequestBodyJSONParser searchRequestBodyJSONParser =
			new SearchRequestBodyJSONParser();

		return searchRequestBodyJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SearchRequestBody searchRequestBody) {
		if (searchRequestBody == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (searchRequestBody.getAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			sb.append(_toJSON(searchRequestBody.getAttributes()));
		}

		if (searchRequestBody.getFacetConfigurations() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"facetConfigurations\": ");

			sb.append("[");

			for (int i = 0;
				 i < searchRequestBody.getFacetConfigurations().length; i++) {

				sb.append(
					String.valueOf(
						searchRequestBody.getFacetConfigurations()[i]));

				if ((i + 1) <
						searchRequestBody.getFacetConfigurations().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SearchRequestBodyJSONParser searchRequestBodyJSONParser =
			new SearchRequestBodyJSONParser();

		return searchRequestBodyJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SearchRequestBody searchRequestBody) {

		if (searchRequestBody == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (searchRequestBody.getAttributes() == null) {
			map.put("attributes", null);
		}
		else {
			map.put(
				"attributes",
				String.valueOf(searchRequestBody.getAttributes()));
		}

		if (searchRequestBody.getFacetConfigurations() == null) {
			map.put("facetConfigurations", null);
		}
		else {
			map.put(
				"facetConfigurations",
				String.valueOf(searchRequestBody.getFacetConfigurations()));
		}

		return map;
	}

	public static class SearchRequestBodyJSONParser
		extends BaseJSONParser<SearchRequestBody> {

		@Override
		protected SearchRequestBody createDTO() {
			return new SearchRequestBody();
		}

		@Override
		protected SearchRequestBody[] createDTOArray(int size) {
			return new SearchRequestBody[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "attributes")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "facetConfigurations")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SearchRequestBody searchRequestBody, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "attributes")) {
				if (jsonParserFieldValue != null) {
					searchRequestBody.setAttributes(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "facetConfigurations")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FacetConfiguration[] facetConfigurationsArray =
						new FacetConfiguration[jsonParserFieldValues.length];

					for (int i = 0; i < facetConfigurationsArray.length; i++) {
						facetConfigurationsArray[i] =
							FacetConfigurationSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					searchRequestBody.setFacetConfigurations(
						facetConfigurationsArray);
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