/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.ReindexStatus;
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
public class ReindexStatusSerDes {

	public static ReindexStatus toDTO(String json) {
		ReindexStatusJSONParser reindexStatusJSONParser =
			new ReindexStatusJSONParser();

		return reindexStatusJSONParser.parseToDTO(json);
	}

	public static ReindexStatus[] toDTOs(String json) {
		ReindexStatusJSONParser reindexStatusJSONParser =
			new ReindexStatusJSONParser();

		return reindexStatusJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ReindexStatus reindexStatus) {
		if (reindexStatus == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (reindexStatus.getCompletionPercentage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completionPercentage\": ");

			sb.append(reindexStatus.getCompletionPercentage());
		}

		if (reindexStatus.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(reindexStatus.getKey()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ReindexStatusJSONParser reindexStatusJSONParser =
			new ReindexStatusJSONParser();

		return reindexStatusJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ReindexStatus reindexStatus) {
		if (reindexStatus == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (reindexStatus.getCompletionPercentage() == null) {
			map.put("completionPercentage", null);
		}
		else {
			map.put(
				"completionPercentage",
				String.valueOf(reindexStatus.getCompletionPercentage()));
		}

		if (reindexStatus.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(reindexStatus.getKey()));
		}

		return map;
	}

	public static class ReindexStatusJSONParser
		extends BaseJSONParser<ReindexStatus> {

		@Override
		protected ReindexStatus createDTO() {
			return new ReindexStatus();
		}

		@Override
		protected ReindexStatus[] createDTOArray(int size) {
			return new ReindexStatus[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "completionPercentage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ReindexStatus reindexStatus, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "completionPercentage")) {
				if (jsonParserFieldValue != null) {
					reindexStatus.setCompletionPercentage(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					reindexStatus.setKey((String)jsonParserFieldValue);
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