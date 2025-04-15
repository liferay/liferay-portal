/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.serdes.v1_0;

import com.liferay.portal.search.rest.client.dto.v1_0.Suggestion;
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
public class SuggestionSerDes {

	public static Suggestion toDTO(String json) {
		SuggestionJSONParser suggestionJSONParser = new SuggestionJSONParser();

		return suggestionJSONParser.parseToDTO(json);
	}

	public static Suggestion[] toDTOs(String json) {
		SuggestionJSONParser suggestionJSONParser = new SuggestionJSONParser();

		return suggestionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Suggestion suggestion) {
		if (suggestion == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (suggestion.getAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			if (suggestion.getAttributes() instanceof String) {
				sb.append("\"");
				sb.append((String)suggestion.getAttributes());
				sb.append("\"");
			}
			else {
				sb.append(suggestion.getAttributes());
			}
		}

		if (suggestion.getScore() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"score\": ");

			sb.append(suggestion.getScore());
		}

		if (suggestion.getText() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"text\": ");

			sb.append("\"");

			sb.append(_escape(suggestion.getText()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SuggestionJSONParser suggestionJSONParser = new SuggestionJSONParser();

		return suggestionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Suggestion suggestion) {
		if (suggestion == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (suggestion.getAttributes() == null) {
			map.put("attributes", null);
		}
		else {
			map.put("attributes", String.valueOf(suggestion.getAttributes()));
		}

		if (suggestion.getScore() == null) {
			map.put("score", null);
		}
		else {
			map.put("score", String.valueOf(suggestion.getScore()));
		}

		if (suggestion.getText() == null) {
			map.put("text", null);
		}
		else {
			map.put("text", String.valueOf(suggestion.getText()));
		}

		return map;
	}

	public static class SuggestionJSONParser
		extends BaseJSONParser<Suggestion> {

		@Override
		protected Suggestion createDTO() {
			return new Suggestion();
		}

		@Override
		protected Suggestion[] createDTOArray(int size) {
			return new Suggestion[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "attributes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "score")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "text")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Suggestion suggestion, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "attributes")) {
				if (jsonParserFieldValue != null) {
					suggestion.setAttributes((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "score")) {
				if (jsonParserFieldValue != null) {
					suggestion.setScore(
						Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "text")) {
				if (jsonParserFieldValue != null) {
					suggestion.setText((String)jsonParserFieldValue);
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