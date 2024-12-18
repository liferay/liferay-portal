/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v2_0;

import com.liferay.search.experiences.rest.client.dto.v2_0.Clause;
import com.liferay.search.experiences.rest.client.dto.v2_0.PostFilterConfiguration;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class PostFilterConfigurationSerDes {

	public static PostFilterConfiguration toDTO(String json) {
		PostFilterConfigurationJSONParser postFilterConfigurationJSONParser =
			new PostFilterConfigurationJSONParser();

		return postFilterConfigurationJSONParser.parseToDTO(json);
	}

	public static PostFilterConfiguration[] toDTOs(String json) {
		PostFilterConfigurationJSONParser postFilterConfigurationJSONParser =
			new PostFilterConfigurationJSONParser();

		return postFilterConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PostFilterConfiguration postFilterConfiguration) {

		if (postFilterConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (postFilterConfiguration.getClauses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clauses\": ");

			sb.append("[");

			for (int i = 0; i < postFilterConfiguration.getClauses().length;
				 i++) {

				sb.append(
					String.valueOf(postFilterConfiguration.getClauses()[i]));

				if ((i + 1) < postFilterConfiguration.getClauses().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PostFilterConfigurationJSONParser postFilterConfigurationJSONParser =
			new PostFilterConfigurationJSONParser();

		return postFilterConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PostFilterConfiguration postFilterConfiguration) {

		if (postFilterConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (postFilterConfiguration.getClauses() == null) {
			map.put("clauses", null);
		}
		else {
			map.put(
				"clauses",
				String.valueOf(postFilterConfiguration.getClauses()));
		}

		return map;
	}

	public static class PostFilterConfigurationJSONParser
		extends BaseJSONParser<PostFilterConfiguration> {

		@Override
		protected PostFilterConfiguration createDTO() {
			return new PostFilterConfiguration();
		}

		@Override
		protected PostFilterConfiguration[] createDTOArray(int size) {
			return new PostFilterConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "clauses")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PostFilterConfiguration postFilterConfiguration,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "clauses")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Clause[] clausesArray =
						new Clause[jsonParserFieldValues.length];

					for (int i = 0; i < clausesArray.length; i++) {
						clausesArray[i] = ClauseSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					postFilterConfiguration.setClauses(clausesArray);
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