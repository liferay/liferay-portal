/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v2_0;

import com.liferay.search.experiences.rest.client.dto.v2_0.Clause;
import com.liferay.search.experiences.rest.client.dto.v2_0.RescoreConfiguration;
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
public class RescoreConfigurationSerDes {

	public static RescoreConfiguration toDTO(String json) {
		RescoreConfigurationJSONParser rescoreConfigurationJSONParser =
			new RescoreConfigurationJSONParser();

		return rescoreConfigurationJSONParser.parseToDTO(json);
	}

	public static RescoreConfiguration[] toDTOs(String json) {
		RescoreConfigurationJSONParser rescoreConfigurationJSONParser =
			new RescoreConfigurationJSONParser();

		return rescoreConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(RescoreConfiguration rescoreConfiguration) {
		if (rescoreConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (rescoreConfiguration.getClauses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clauses\": ");

			sb.append("[");

			for (int i = 0; i < rescoreConfiguration.getClauses().length; i++) {
				sb.append(String.valueOf(rescoreConfiguration.getClauses()[i]));

				if ((i + 1) < rescoreConfiguration.getClauses().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (rescoreConfiguration.getQueryWeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"queryWeight\": ");

			if (rescoreConfiguration.getQueryWeight() instanceof String) {
				sb.append("\"");
				sb.append((String)rescoreConfiguration.getQueryWeight());
				sb.append("\"");
			}
			else {
				sb.append(rescoreConfiguration.getQueryWeight());
			}
		}

		if (rescoreConfiguration.getRescoreQueryWeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rescoreQueryWeight\": ");

			if (rescoreConfiguration.getRescoreQueryWeight() instanceof
					String) {

				sb.append("\"");
				sb.append((String)rescoreConfiguration.getRescoreQueryWeight());
				sb.append("\"");
			}
			else {
				sb.append(rescoreConfiguration.getRescoreQueryWeight());
			}
		}

		if (rescoreConfiguration.getScoreMode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scoreMode\": ");

			sb.append("\"");

			sb.append(_escape(rescoreConfiguration.getScoreMode()));

			sb.append("\"");
		}

		if (rescoreConfiguration.getWindowSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"windowSize\": ");

			if (rescoreConfiguration.getWindowSize() instanceof String) {
				sb.append("\"");
				sb.append((String)rescoreConfiguration.getWindowSize());
				sb.append("\"");
			}
			else {
				sb.append(rescoreConfiguration.getWindowSize());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RescoreConfigurationJSONParser rescoreConfigurationJSONParser =
			new RescoreConfigurationJSONParser();

		return rescoreConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		RescoreConfiguration rescoreConfiguration) {

		if (rescoreConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (rescoreConfiguration.getClauses() == null) {
			map.put("clauses", null);
		}
		else {
			map.put(
				"clauses", String.valueOf(rescoreConfiguration.getClauses()));
		}

		if (rescoreConfiguration.getQueryWeight() == null) {
			map.put("queryWeight", null);
		}
		else {
			map.put(
				"queryWeight",
				String.valueOf(rescoreConfiguration.getQueryWeight()));
		}

		if (rescoreConfiguration.getRescoreQueryWeight() == null) {
			map.put("rescoreQueryWeight", null);
		}
		else {
			map.put(
				"rescoreQueryWeight",
				String.valueOf(rescoreConfiguration.getRescoreQueryWeight()));
		}

		if (rescoreConfiguration.getScoreMode() == null) {
			map.put("scoreMode", null);
		}
		else {
			map.put(
				"scoreMode",
				String.valueOf(rescoreConfiguration.getScoreMode()));
		}

		if (rescoreConfiguration.getWindowSize() == null) {
			map.put("windowSize", null);
		}
		else {
			map.put(
				"windowSize",
				String.valueOf(rescoreConfiguration.getWindowSize()));
		}

		return map;
	}

	public static class RescoreConfigurationJSONParser
		extends BaseJSONParser<RescoreConfiguration> {

		@Override
		protected RescoreConfiguration createDTO() {
			return new RescoreConfiguration();
		}

		@Override
		protected RescoreConfiguration[] createDTOArray(int size) {
			return new RescoreConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "clauses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "queryWeight")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "rescoreQueryWeight")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scoreMode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "windowSize")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			RescoreConfiguration rescoreConfiguration,
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

					rescoreConfiguration.setClauses(clausesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "queryWeight")) {
				if (jsonParserFieldValue != null) {
					rescoreConfiguration.setQueryWeight(
						(Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "rescoreQueryWeight")) {

				if (jsonParserFieldValue != null) {
					rescoreConfiguration.setRescoreQueryWeight(
						(Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scoreMode")) {
				if (jsonParserFieldValue != null) {
					rescoreConfiguration.setScoreMode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "windowSize")) {
				if (jsonParserFieldValue != null) {
					rescoreConfiguration.setWindowSize(
						(Object)jsonParserFieldValue);
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