/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.client.serdes.v1_0;

import com.liferay.headless.cmp.client.dto.v1_0.TaskStatistics;
import com.liferay.headless.cmp.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
public class TaskStatisticsSerDes {

	public static TaskStatistics toDTO(String json) {
		TaskStatisticsJSONParser taskStatisticsJSONParser =
			new TaskStatisticsJSONParser();

		return taskStatisticsJSONParser.parseToDTO(json);
	}

	public static TaskStatistics[] toDTOs(String json) {
		TaskStatisticsJSONParser taskStatisticsJSONParser =
			new TaskStatisticsJSONParser();

		return taskStatisticsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TaskStatistics taskStatistics) {
		if (taskStatistics == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (taskStatistics.getBlockedCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"blockedCount\": ");

			sb.append(taskStatistics.getBlockedCount());
		}

		if (taskStatistics.getInProgressCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inProgressCount\": ");

			sb.append(taskStatistics.getInProgressCount());
		}

		if (taskStatistics.getOverdueCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"overdueCount\": ");

			sb.append(taskStatistics.getOverdueCount());
		}

		if (taskStatistics.getTotalCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalCount\": ");

			sb.append(taskStatistics.getTotalCount());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaskStatisticsJSONParser taskStatisticsJSONParser =
			new TaskStatisticsJSONParser();

		return taskStatisticsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TaskStatistics taskStatistics) {
		if (taskStatistics == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (taskStatistics.getBlockedCount() == null) {
			map.put("blockedCount", null);
		}
		else {
			map.put(
				"blockedCount",
				String.valueOf(taskStatistics.getBlockedCount()));
		}

		if (taskStatistics.getInProgressCount() == null) {
			map.put("inProgressCount", null);
		}
		else {
			map.put(
				"inProgressCount",
				String.valueOf(taskStatistics.getInProgressCount()));
		}

		if (taskStatistics.getOverdueCount() == null) {
			map.put("overdueCount", null);
		}
		else {
			map.put(
				"overdueCount",
				String.valueOf(taskStatistics.getOverdueCount()));
		}

		if (taskStatistics.getTotalCount() == null) {
			map.put("totalCount", null);
		}
		else {
			map.put(
				"totalCount", String.valueOf(taskStatistics.getTotalCount()));
		}

		return map;
	}

	public static class TaskStatisticsJSONParser
		extends BaseJSONParser<TaskStatistics> {

		@Override
		protected TaskStatistics createDTO() {
			return new TaskStatistics();
		}

		@Override
		protected TaskStatistics[] createDTOArray(int size) {
			return new TaskStatistics[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "blockedCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "inProgressCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "overdueCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TaskStatistics taskStatistics, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "blockedCount")) {
				if (jsonParserFieldValue != null) {
					taskStatistics.setBlockedCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "inProgressCount")) {
				if (jsonParserFieldValue != null) {
					taskStatistics.setInProgressCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "overdueCount")) {
				if (jsonParserFieldValue != null) {
					taskStatistics.setOverdueCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				if (jsonParserFieldValue != null) {
					taskStatistics.setTotalCount(
						Long.valueOf((String)jsonParserFieldValue));
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:116428115