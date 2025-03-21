/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.serdes.v1_0;

import com.liferay.portal.search.rest.client.dto.v1_0.SuggestionsContributorConfiguration;
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
public class SuggestionsContributorConfigurationSerDes {

	public static SuggestionsContributorConfiguration toDTO(String json) {
		SuggestionsContributorConfigurationJSONParser
			suggestionsContributorConfigurationJSONParser =
				new SuggestionsContributorConfigurationJSONParser();

		return suggestionsContributorConfigurationJSONParser.parseToDTO(json);
	}

	public static SuggestionsContributorConfiguration[] toDTOs(String json) {
		SuggestionsContributorConfigurationJSONParser
			suggestionsContributorConfigurationJSONParser =
				new SuggestionsContributorConfigurationJSONParser();

		return suggestionsContributorConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		SuggestionsContributorConfiguration
			suggestionsContributorConfiguration) {

		if (suggestionsContributorConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (suggestionsContributorConfiguration.getAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			if (suggestionsContributorConfiguration.getAttributes() instanceof
					String) {

				sb.append("\"");
				sb.append(
					(String)
						suggestionsContributorConfiguration.getAttributes());
				sb.append("\"");
			}
			else {
				sb.append(suggestionsContributorConfiguration.getAttributes());
			}
		}

		if (suggestionsContributorConfiguration.getContributorName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contributorName\": ");

			sb.append("\"");

			sb.append(
				_escape(
					suggestionsContributorConfiguration.getContributorName()));

			sb.append("\"");
		}

		if (suggestionsContributorConfiguration.getDisplayGroupName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayGroupName\": ");

			sb.append("\"");

			sb.append(
				_escape(
					suggestionsContributorConfiguration.getDisplayGroupName()));

			sb.append("\"");
		}

		if (suggestionsContributorConfiguration.getSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append(suggestionsContributorConfiguration.getSize());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SuggestionsContributorConfigurationJSONParser
			suggestionsContributorConfigurationJSONParser =
				new SuggestionsContributorConfigurationJSONParser();

		return suggestionsContributorConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SuggestionsContributorConfiguration
			suggestionsContributorConfiguration) {

		if (suggestionsContributorConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (suggestionsContributorConfiguration.getAttributes() == null) {
			map.put("attributes", null);
		}
		else {
			map.put(
				"attributes",
				String.valueOf(
					suggestionsContributorConfiguration.getAttributes()));
		}

		if (suggestionsContributorConfiguration.getContributorName() == null) {
			map.put("contributorName", null);
		}
		else {
			map.put(
				"contributorName",
				String.valueOf(
					suggestionsContributorConfiguration.getContributorName()));
		}

		if (suggestionsContributorConfiguration.getDisplayGroupName() == null) {
			map.put("displayGroupName", null);
		}
		else {
			map.put(
				"displayGroupName",
				String.valueOf(
					suggestionsContributorConfiguration.getDisplayGroupName()));
		}

		if (suggestionsContributorConfiguration.getSize() == null) {
			map.put("size", null);
		}
		else {
			map.put(
				"size",
				String.valueOf(suggestionsContributorConfiguration.getSize()));
		}

		return map;
	}

	public static class SuggestionsContributorConfigurationJSONParser
		extends BaseJSONParser<SuggestionsContributorConfiguration> {

		@Override
		protected SuggestionsContributorConfiguration createDTO() {
			return new SuggestionsContributorConfiguration();
		}

		@Override
		protected SuggestionsContributorConfiguration[] createDTOArray(
			int size) {

			return new SuggestionsContributorConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "attributes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contributorName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "displayGroupName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SuggestionsContributorConfiguration
				suggestionsContributorConfiguration,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "attributes")) {
				if (jsonParserFieldValue != null) {
					suggestionsContributorConfiguration.setAttributes(
						(Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contributorName")) {
				if (jsonParserFieldValue != null) {
					suggestionsContributorConfiguration.setContributorName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayGroupName")) {
				if (jsonParserFieldValue != null) {
					suggestionsContributorConfiguration.setDisplayGroupName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				if (jsonParserFieldValue != null) {
					suggestionsContributorConfiguration.setSize(
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