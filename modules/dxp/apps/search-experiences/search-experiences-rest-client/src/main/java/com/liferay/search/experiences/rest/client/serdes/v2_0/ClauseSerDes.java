/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v2_0;

import com.liferay.search.experiences.rest.client.dto.v2_0.Clause;
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
public class ClauseSerDes {

	public static Clause toDTO(String json) {
		ClauseJSONParser clauseJSONParser = new ClauseJSONParser();

		return clauseJSONParser.parseToDTO(json);
	}

	public static Clause[] toDTOs(String json) {
		ClauseJSONParser clauseJSONParser = new ClauseJSONParser();

		return clauseJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Clause clause) {
		if (clause == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (clause.getEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(clause.getEnabled());
		}

		if (clause.getOccur() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"occur\": ");

			sb.append("\"");

			sb.append(_escape(clause.getOccur()));

			sb.append("\"");
		}

		if (clause.getQuery() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"query\": ");

			if (clause.getQuery() instanceof String) {
				sb.append("\"");
				sb.append((String)clause.getQuery());
				sb.append("\"");
			}
			else {
				sb.append(clause.getQuery());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ClauseJSONParser clauseJSONParser = new ClauseJSONParser();

		return clauseJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Clause clause) {
		if (clause == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (clause.getEnabled() == null) {
			map.put("enabled", null);
		}
		else {
			map.put("enabled", String.valueOf(clause.getEnabled()));
		}

		if (clause.getOccur() == null) {
			map.put("occur", null);
		}
		else {
			map.put("occur", String.valueOf(clause.getOccur()));
		}

		if (clause.getQuery() == null) {
			map.put("query", null);
		}
		else {
			map.put("query", String.valueOf(clause.getQuery()));
		}

		return map;
	}

	public static class ClauseJSONParser extends BaseJSONParser<Clause> {

		@Override
		protected Clause createDTO() {
			return new Clause();
		}

		@Override
		protected Clause[] createDTOArray(int size) {
			return new Clause[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "enabled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "occur")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "query")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Clause clause, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "enabled")) {
				if (jsonParserFieldValue != null) {
					clause.setEnabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "occur")) {
				if (jsonParserFieldValue != null) {
					clause.setOccur((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "query")) {
				if (jsonParserFieldValue != null) {
					clause.setQuery((Object)jsonParserFieldValue);
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