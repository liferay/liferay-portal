/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.Configuration;
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
public class ConfigurationSerDes {

	public static Configuration toDTO(String json) {
		ConfigurationJSONParser configurationJSONParser =
			new ConfigurationJSONParser();

		return configurationJSONParser.parseToDTO(json);
	}

	public static Configuration[] toDTOs(String json) {
		ConfigurationJSONParser configurationJSONParser =
			new ConfigurationJSONParser();

		return configurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Configuration configuration) {
		if (configuration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (configuration.getAdvancedConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"advancedConfiguration\": ");

			sb.append(String.valueOf(configuration.getAdvancedConfiguration()));
		}

		if (configuration.getAggregationConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"aggregationConfiguration\": ");

			sb.append(
				String.valueOf(configuration.getAggregationConfiguration()));
		}

		if (configuration.getGeneralConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"generalConfiguration\": ");

			sb.append(String.valueOf(configuration.getGeneralConfiguration()));
		}

		if (configuration.getHighlightConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"highlightConfiguration\": ");

			sb.append(
				String.valueOf(configuration.getHighlightConfiguration()));
		}

		if (configuration.getIndexConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexConfiguration\": ");

			sb.append(String.valueOf(configuration.getIndexConfiguration()));
		}

		if (configuration.getParameterConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameterConfiguration\": ");

			sb.append(
				String.valueOf(configuration.getParameterConfiguration()));
		}

		if (configuration.getQueryConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"queryConfiguration\": ");

			sb.append(String.valueOf(configuration.getQueryConfiguration()));
		}

		if (configuration.getSearchContextAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchContextAttributes\": ");

			sb.append(_toJSON(configuration.getSearchContextAttributes()));
		}

		if (configuration.getSortConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sortConfiguration\": ");

			sb.append(String.valueOf(configuration.getSortConfiguration()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ConfigurationJSONParser configurationJSONParser =
			new ConfigurationJSONParser();

		return configurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Configuration configuration) {
		if (configuration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (configuration.getAdvancedConfiguration() == null) {
			map.put("advancedConfiguration", null);
		}
		else {
			map.put(
				"advancedConfiguration",
				String.valueOf(configuration.getAdvancedConfiguration()));
		}

		if (configuration.getAggregationConfiguration() == null) {
			map.put("aggregationConfiguration", null);
		}
		else {
			map.put(
				"aggregationConfiguration",
				String.valueOf(configuration.getAggregationConfiguration()));
		}

		if (configuration.getGeneralConfiguration() == null) {
			map.put("generalConfiguration", null);
		}
		else {
			map.put(
				"generalConfiguration",
				String.valueOf(configuration.getGeneralConfiguration()));
		}

		if (configuration.getHighlightConfiguration() == null) {
			map.put("highlightConfiguration", null);
		}
		else {
			map.put(
				"highlightConfiguration",
				String.valueOf(configuration.getHighlightConfiguration()));
		}

		if (configuration.getIndexConfiguration() == null) {
			map.put("indexConfiguration", null);
		}
		else {
			map.put(
				"indexConfiguration",
				String.valueOf(configuration.getIndexConfiguration()));
		}

		if (configuration.getParameterConfiguration() == null) {
			map.put("parameterConfiguration", null);
		}
		else {
			map.put(
				"parameterConfiguration",
				String.valueOf(configuration.getParameterConfiguration()));
		}

		if (configuration.getQueryConfiguration() == null) {
			map.put("queryConfiguration", null);
		}
		else {
			map.put(
				"queryConfiguration",
				String.valueOf(configuration.getQueryConfiguration()));
		}

		if (configuration.getSearchContextAttributes() == null) {
			map.put("searchContextAttributes", null);
		}
		else {
			map.put(
				"searchContextAttributes",
				String.valueOf(configuration.getSearchContextAttributes()));
		}

		if (configuration.getSortConfiguration() == null) {
			map.put("sortConfiguration", null);
		}
		else {
			map.put(
				"sortConfiguration",
				String.valueOf(configuration.getSortConfiguration()));
		}

		return map;
	}

	public static class ConfigurationJSONParser
		extends BaseJSONParser<Configuration> {

		@Override
		protected Configuration createDTO() {
			return new Configuration();
		}

		@Override
		protected Configuration[] createDTOArray(int size) {
			return new Configuration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "advancedConfiguration")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "aggregationConfiguration")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "generalConfiguration")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "highlightConfiguration")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "indexConfiguration")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parameterConfiguration")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "queryConfiguration")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "searchContextAttributes")) {

				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "sortConfiguration")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Configuration configuration, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "advancedConfiguration")) {
				if (jsonParserFieldValue != null) {
					configuration.setAdvancedConfiguration(
						AdvancedConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "aggregationConfiguration")) {

				if (jsonParserFieldValue != null) {
					configuration.setAggregationConfiguration(
						AggregationConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "generalConfiguration")) {

				if (jsonParserFieldValue != null) {
					configuration.setGeneralConfiguration(
						GeneralConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "highlightConfiguration")) {

				if (jsonParserFieldValue != null) {
					configuration.setHighlightConfiguration(
						HighlightConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "indexConfiguration")) {

				if (jsonParserFieldValue != null) {
					configuration.setIndexConfiguration(
						IndexConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parameterConfiguration")) {

				if (jsonParserFieldValue != null) {
					configuration.setParameterConfiguration(
						ParameterConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "queryConfiguration")) {

				if (jsonParserFieldValue != null) {
					configuration.setQueryConfiguration(
						QueryConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "searchContextAttributes")) {

				if (jsonParserFieldValue != null) {
					configuration.setSearchContextAttributes(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sortConfiguration")) {
				if (jsonParserFieldValue != null) {
					configuration.setSortConfiguration(
						SortConfigurationSerDes.toDTO(
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