/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.ProcessVersion;
import com.liferay.portal.workflow.metrics.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class ProcessVersionSerDes {

	public static ProcessVersion toDTO(String json) {
		ProcessVersionJSONParser processVersionJSONParser =
			new ProcessVersionJSONParser();

		return processVersionJSONParser.parseToDTO(json);
	}

	public static ProcessVersion[] toDTOs(String json) {
		ProcessVersionJSONParser processVersionJSONParser =
			new ProcessVersionJSONParser();

		return processVersionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ProcessVersion processVersion) {
		if (processVersion == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (processVersion.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(processVersion.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProcessVersionJSONParser processVersionJSONParser =
			new ProcessVersionJSONParser();

		return processVersionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ProcessVersion processVersion) {
		if (processVersion == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (processVersion.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(processVersion.getName()));
		}

		return map;
	}

	public static class ProcessVersionJSONParser
		extends BaseJSONParser<ProcessVersion> {

		@Override
		protected ProcessVersion createDTO() {
			return new ProcessVersion();
		}

		@Override
		protected ProcessVersion[] createDTOArray(int size) {
			return new ProcessVersion[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProcessVersion processVersion, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					processVersion.setName((String)jsonParserFieldValue);
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