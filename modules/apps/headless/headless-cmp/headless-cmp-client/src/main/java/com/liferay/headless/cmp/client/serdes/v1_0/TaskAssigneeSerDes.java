/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.client.serdes.v1_0;

import com.liferay.headless.cmp.client.dto.v1_0.TaskAssignee;
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
public class TaskAssigneeSerDes {

	public static TaskAssignee toDTO(String json) {
		TaskAssigneeJSONParser taskAssigneeJSONParser =
			new TaskAssigneeJSONParser();

		return taskAssigneeJSONParser.parseToDTO(json);
	}

	public static TaskAssignee[] toDTOs(String json) {
		TaskAssigneeJSONParser taskAssigneeJSONParser =
			new TaskAssigneeJSONParser();

		return taskAssigneeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TaskAssignee taskAssignee) {
		if (taskAssignee == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (taskAssignee.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(taskAssignee.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (taskAssignee.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(taskAssignee.getId());
		}

		if (taskAssignee.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(taskAssignee.getName()));

			sb.append("\"");
		}

		if (taskAssignee.getPortrait() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portrait\": ");

			sb.append("\"");

			sb.append(_escape(taskAssignee.getPortrait()));

			sb.append("\"");
		}

		if (taskAssignee.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(taskAssignee.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaskAssigneeJSONParser taskAssigneeJSONParser =
			new TaskAssigneeJSONParser();

		return taskAssigneeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TaskAssignee taskAssignee) {
		if (taskAssignee == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (taskAssignee.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(taskAssignee.getExternalReferenceCode()));
		}

		if (taskAssignee.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(taskAssignee.getId()));
		}

		if (taskAssignee.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(taskAssignee.getName()));
		}

		if (taskAssignee.getPortrait() == null) {
			map.put("portrait", null);
		}
		else {
			map.put("portrait", String.valueOf(taskAssignee.getPortrait()));
		}

		if (taskAssignee.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(taskAssignee.getType()));
		}

		return map;
	}

	public static class TaskAssigneeJSONParser
		extends BaseJSONParser<TaskAssignee> {

		@Override
		protected TaskAssignee createDTO() {
			return new TaskAssignee();
		}

		@Override
		protected TaskAssignee[] createDTOArray(int size) {
			return new TaskAssignee[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "portrait")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TaskAssignee taskAssignee, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					taskAssignee.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					taskAssignee.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					taskAssignee.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "portrait")) {
				if (jsonParserFieldValue != null) {
					taskAssignee.setPortrait((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					taskAssignee.setType((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-1559216094