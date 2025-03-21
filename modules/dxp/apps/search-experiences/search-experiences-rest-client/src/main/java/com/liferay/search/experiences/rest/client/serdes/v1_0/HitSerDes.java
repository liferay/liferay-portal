/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.DocumentField;
import com.liferay.search.experiences.rest.client.dto.v1_0.Hit;
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
public class HitSerDes {

	public static Hit toDTO(String json) {
		HitJSONParser hitJSONParser = new HitJSONParser();

		return hitJSONParser.parseToDTO(json);
	}

	public static Hit[] toDTOs(String json) {
		HitJSONParser hitJSONParser = new HitJSONParser();

		return hitJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Hit hit) {
		if (hit == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (hit.getDocumentFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentFields\": ");

			sb.append(_toJSON(hit.getDocumentFields()));
		}

		if (hit.getExplanation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"explanation\": ");

			sb.append("\"");

			sb.append(_escape(hit.getExplanation()));

			sb.append("\"");
		}

		if (hit.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(hit.getId()));

			sb.append("\"");
		}

		if (hit.getScore() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"score\": ");

			sb.append(hit.getScore());
		}

		if (hit.getVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append(hit.getVersion());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		HitJSONParser hitJSONParser = new HitJSONParser();

		return hitJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Hit hit) {
		if (hit == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (hit.getDocumentFields() == null) {
			map.put("documentFields", null);
		}
		else {
			map.put("documentFields", String.valueOf(hit.getDocumentFields()));
		}

		if (hit.getExplanation() == null) {
			map.put("explanation", null);
		}
		else {
			map.put("explanation", String.valueOf(hit.getExplanation()));
		}

		if (hit.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(hit.getId()));
		}

		if (hit.getScore() == null) {
			map.put("score", null);
		}
		else {
			map.put("score", String.valueOf(hit.getScore()));
		}

		if (hit.getVersion() == null) {
			map.put("version", null);
		}
		else {
			map.put("version", String.valueOf(hit.getVersion()));
		}

		return map;
	}

	public static class HitJSONParser extends BaseJSONParser<Hit> {

		@Override
		protected Hit createDTO() {
			return new Hit();
		}

		@Override
		protected Hit[] createDTOArray(int size) {
			return new Hit[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "documentFields")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "explanation")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "score")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Hit hit, String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "documentFields")) {
				if (jsonParserFieldValue != null) {
					hit.setDocumentFields(
						(Map<String, DocumentField>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "explanation")) {
				if (jsonParserFieldValue != null) {
					hit.setExplanation((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					hit.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "score")) {
				if (jsonParserFieldValue != null) {
					hit.setScore(Float.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				if (jsonParserFieldValue != null) {
					hit.setVersion(Long.valueOf((String)jsonParserFieldValue));
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