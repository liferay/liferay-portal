/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.Rescore;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class RescoreSerDes {

	public static Rescore toDTO(String json) {
		RescoreJSONParser rescoreJSONParser = new RescoreJSONParser();

		return rescoreJSONParser.parseToDTO(json);
	}

	public static Rescore[] toDTOs(String json) {
		RescoreJSONParser rescoreJSONParser = new RescoreJSONParser();

		return rescoreJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Rescore rescore) {
		if (rescore == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (rescore.getQuery() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"query\": ");

			if (rescore.getQuery() instanceof String) {
				sb.append("\"");
				sb.append((String)rescore.getQuery());
				sb.append("\"");
			}
			else {
				sb.append(rescore.getQuery());
			}
		}

		if (rescore.getQueryWeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"queryWeight\": ");

			if (rescore.getQueryWeight() instanceof String) {
				sb.append("\"");
				sb.append((String)rescore.getQueryWeight());
				sb.append("\"");
			}
			else {
				sb.append(rescore.getQueryWeight());
			}
		}

		if (rescore.getRescoreQueryWeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rescoreQueryWeight\": ");

			if (rescore.getRescoreQueryWeight() instanceof String) {
				sb.append("\"");
				sb.append((String)rescore.getRescoreQueryWeight());
				sb.append("\"");
			}
			else {
				sb.append(rescore.getRescoreQueryWeight());
			}
		}

		if (rescore.getScoreMode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scoreMode\": ");

			sb.append("\"");

			sb.append(_escape(rescore.getScoreMode()));

			sb.append("\"");
		}

		if (rescore.getWindowSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"windowSize\": ");

			if (rescore.getWindowSize() instanceof String) {
				sb.append("\"");
				sb.append((String)rescore.getWindowSize());
				sb.append("\"");
			}
			else {
				sb.append(rescore.getWindowSize());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RescoreJSONParser rescoreJSONParser = new RescoreJSONParser();

		return rescoreJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Rescore rescore) {
		if (rescore == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (rescore.getQuery() == null) {
			map.put("query", null);
		}
		else {
			map.put("query", String.valueOf(rescore.getQuery()));
		}

		if (rescore.getQueryWeight() == null) {
			map.put("queryWeight", null);
		}
		else {
			map.put("queryWeight", String.valueOf(rescore.getQueryWeight()));
		}

		if (rescore.getRescoreQueryWeight() == null) {
			map.put("rescoreQueryWeight", null);
		}
		else {
			map.put(
				"rescoreQueryWeight",
				String.valueOf(rescore.getRescoreQueryWeight()));
		}

		if (rescore.getScoreMode() == null) {
			map.put("scoreMode", null);
		}
		else {
			map.put("scoreMode", String.valueOf(rescore.getScoreMode()));
		}

		if (rescore.getWindowSize() == null) {
			map.put("windowSize", null);
		}
		else {
			map.put("windowSize", String.valueOf(rescore.getWindowSize()));
		}

		return map;
	}

	public static class RescoreJSONParser extends BaseJSONParser<Rescore> {

		@Override
		protected Rescore createDTO() {
			return new Rescore();
		}

		@Override
		protected Rescore[] createDTOArray(int size) {
			return new Rescore[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "query")) {
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
			Rescore rescore, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "query")) {
				if (jsonParserFieldValue != null) {
					rescore.setQuery((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "queryWeight")) {
				if (jsonParserFieldValue != null) {
					rescore.setQueryWeight((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "rescoreQueryWeight")) {

				if (jsonParserFieldValue != null) {
					rescore.setRescoreQueryWeight((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scoreMode")) {
				if (jsonParserFieldValue != null) {
					rescore.setScoreMode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "windowSize")) {
				if (jsonParserFieldValue != null) {
					rescore.setWindowSize((Object)jsonParserFieldValue);
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