/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.Source;
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
public class SourceSerDes {

	public static Source toDTO(String json) {
		SourceJSONParser sourceJSONParser = new SourceJSONParser();

		return sourceJSONParser.parseToDTO(json);
	}

	public static Source[] toDTOs(String json) {
		SourceJSONParser sourceJSONParser = new SourceJSONParser();

		return sourceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Source source) {
		if (source == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (source.getExcludes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"excludes\": ");

			sb.append("[");

			for (int i = 0; i < source.getExcludes().length; i++) {
				sb.append(_toJSON(source.getExcludes()[i]));

				if ((i + 1) < source.getExcludes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (source.getFetchSource() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fetchSource\": ");

			sb.append(source.getFetchSource());
		}

		if (source.getIncludes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"includes\": ");

			sb.append("[");

			for (int i = 0; i < source.getIncludes().length; i++) {
				sb.append(_toJSON(source.getIncludes()[i]));

				if ((i + 1) < source.getIncludes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SourceJSONParser sourceJSONParser = new SourceJSONParser();

		return sourceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Source source) {
		if (source == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (source.getExcludes() == null) {
			map.put("excludes", null);
		}
		else {
			map.put("excludes", String.valueOf(source.getExcludes()));
		}

		if (source.getFetchSource() == null) {
			map.put("fetchSource", null);
		}
		else {
			map.put("fetchSource", String.valueOf(source.getFetchSource()));
		}

		if (source.getIncludes() == null) {
			map.put("includes", null);
		}
		else {
			map.put("includes", String.valueOf(source.getIncludes()));
		}

		return map;
	}

	public static class SourceJSONParser extends BaseJSONParser<Source> {

		@Override
		protected Source createDTO() {
			return new Source();
		}

		@Override
		protected Source[] createDTOArray(int size) {
			return new Source[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "excludes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fetchSource")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "includes")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Source source, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "excludes")) {
				if (jsonParserFieldValue != null) {
					source.setExcludes(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fetchSource")) {
				if (jsonParserFieldValue != null) {
					source.setFetchSource((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "includes")) {
				if (jsonParserFieldValue != null) {
					source.setIncludes(
						toStrings((Object[])jsonParserFieldValue));
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