/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.serdes.v1_0;

import com.liferay.portal.search.rest.client.dto.v1_0.FacetConfiguration;
import com.liferay.portal.search.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class FacetConfigurationSerDes {

	public static FacetConfiguration toDTO(String json) {
		FacetConfigurationJSONParser facetConfigurationJSONParser =
			new FacetConfigurationJSONParser();

		return facetConfigurationJSONParser.parseToDTO(json);
	}

	public static FacetConfiguration[] toDTOs(String json) {
		FacetConfigurationJSONParser facetConfigurationJSONParser =
			new FacetConfigurationJSONParser();

		return facetConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FacetConfiguration facetConfiguration) {
		if (facetConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (facetConfiguration.getAggregationName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aggregationName\": ");

			sb.append("\"");

			sb.append(_escape(facetConfiguration.getAggregationName()));

			sb.append("\"");
		}

		if (facetConfiguration.getAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			sb.append(_toJSON(facetConfiguration.getAttributes()));
		}

		if (facetConfiguration.getFrequencyThreshold() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"frequencyThreshold\": ");

			sb.append(facetConfiguration.getFrequencyThreshold());
		}

		if (facetConfiguration.getMaxTerms() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxTerms\": ");

			sb.append(facetConfiguration.getMaxTerms());
		}

		if (facetConfiguration.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(facetConfiguration.getName()));

			sb.append("\"");
		}

		if (facetConfiguration.getValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"values\": ");

			sb.append("[");

			for (int i = 0; i < facetConfiguration.getValues().length; i++) {
				sb.append(_toJSON(facetConfiguration.getValues()[i]));

				if ((i + 1) < facetConfiguration.getValues().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FacetConfigurationJSONParser facetConfigurationJSONParser =
			new FacetConfigurationJSONParser();

		return facetConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FacetConfiguration facetConfiguration) {

		if (facetConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (facetConfiguration.getAggregationName() == null) {
			map.put("aggregationName", null);
		}
		else {
			map.put(
				"aggregationName",
				String.valueOf(facetConfiguration.getAggregationName()));
		}

		if (facetConfiguration.getAttributes() == null) {
			map.put("attributes", null);
		}
		else {
			map.put(
				"attributes",
				String.valueOf(facetConfiguration.getAttributes()));
		}

		if (facetConfiguration.getFrequencyThreshold() == null) {
			map.put("frequencyThreshold", null);
		}
		else {
			map.put(
				"frequencyThreshold",
				String.valueOf(facetConfiguration.getFrequencyThreshold()));
		}

		if (facetConfiguration.getMaxTerms() == null) {
			map.put("maxTerms", null);
		}
		else {
			map.put(
				"maxTerms", String.valueOf(facetConfiguration.getMaxTerms()));
		}

		if (facetConfiguration.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(facetConfiguration.getName()));
		}

		if (facetConfiguration.getValues() == null) {
			map.put("values", null);
		}
		else {
			map.put("values", String.valueOf(facetConfiguration.getValues()));
		}

		return map;
	}

	public static class FacetConfigurationJSONParser
		extends BaseJSONParser<FacetConfiguration> {

		@Override
		protected FacetConfiguration createDTO() {
			return new FacetConfiguration();
		}

		@Override
		protected FacetConfiguration[] createDTOArray(int size) {
			return new FacetConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "aggregationName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "attributes")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "frequencyThreshold")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "maxTerms")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "values")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			FacetConfiguration facetConfiguration, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "aggregationName")) {
				if (jsonParserFieldValue != null) {
					facetConfiguration.setAggregationName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "attributes")) {
				if (jsonParserFieldValue != null) {
					facetConfiguration.setAttributes(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "frequencyThreshold")) {

				if (jsonParserFieldValue != null) {
					facetConfiguration.setFrequencyThreshold(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "maxTerms")) {
				if (jsonParserFieldValue != null) {
					facetConfiguration.setMaxTerms(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					facetConfiguration.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "values")) {
				if (jsonParserFieldValue != null) {
					facetConfiguration.setValues(
						(Object[])jsonParserFieldValue);
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