/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Task;
import com.liferay.portal.workflow.metrics.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class TaskSerDes {

	public static Task toDTO(String json) {
		TaskJSONParser taskJSONParser = new TaskJSONParser();

		return taskJSONParser.parseToDTO(json);
	}

	public static Task[] toDTOs(String json) {
		TaskJSONParser taskJSONParser = new TaskJSONParser();

		return taskJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Task task) {
		if (task == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (task.getAssetTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(task.getAssetTitle()));

			sb.append("\"");
		}

		if (task.getAssetTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle_i18n\": ");

			sb.append(_toJSON(task.getAssetTitle_i18n()));
		}

		if (task.getAssetType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType\": ");

			sb.append("\"");

			sb.append(_escape(task.getAssetType()));

			sb.append("\"");
		}

		if (task.getAssetType_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType_i18n\": ");

			sb.append(_toJSON(task.getAssetType_i18n()));
		}

		if (task.getAssignee() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assignee\": ");

			sb.append(String.valueOf(task.getAssignee()));
		}

		if (task.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(task.getClassName()));

			sb.append("\"");
		}

		if (task.getClassPK() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classPK\": ");

			sb.append(task.getClassPK());
		}

		if (task.getCompleted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completed\": ");

			sb.append(task.getCompleted());
		}

		if (task.getCompletionUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completionUserId\": ");

			sb.append(task.getCompletionUserId());
		}

		if (task.getDateCompletion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCompletion\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(task.getDateCompletion()));

			sb.append("\"");
		}

		if (task.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(task.getDateCreated()));

			sb.append("\"");
		}

		if (task.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(task.getDateModified()));

			sb.append("\"");
		}

		if (task.getDuration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"duration\": ");

			sb.append(task.getDuration());
		}

		if (task.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(task.getId());
		}

		if (task.getInstanceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"instanceId\": ");

			sb.append(task.getInstanceId());
		}

		if (task.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(task.getLabel()));

			sb.append("\"");
		}

		if (task.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(task.getName()));

			sb.append("\"");
		}

		if (task.getNodeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nodeId\": ");

			sb.append(task.getNodeId());
		}

		if (task.getProcessId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"processId\": ");

			sb.append(task.getProcessId());
		}

		if (task.getProcessVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"processVersion\": ");

			sb.append("\"");

			sb.append(_escape(task.getProcessVersion()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaskJSONParser taskJSONParser = new TaskJSONParser();

		return taskJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Task task) {
		if (task == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (task.getAssetTitle() == null) {
			map.put("assetTitle", null);
		}
		else {
			map.put("assetTitle", String.valueOf(task.getAssetTitle()));
		}

		if (task.getAssetTitle_i18n() == null) {
			map.put("assetTitle_i18n", null);
		}
		else {
			map.put(
				"assetTitle_i18n", String.valueOf(task.getAssetTitle_i18n()));
		}

		if (task.getAssetType() == null) {
			map.put("assetType", null);
		}
		else {
			map.put("assetType", String.valueOf(task.getAssetType()));
		}

		if (task.getAssetType_i18n() == null) {
			map.put("assetType_i18n", null);
		}
		else {
			map.put("assetType_i18n", String.valueOf(task.getAssetType_i18n()));
		}

		if (task.getAssignee() == null) {
			map.put("assignee", null);
		}
		else {
			map.put("assignee", String.valueOf(task.getAssignee()));
		}

		if (task.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put("className", String.valueOf(task.getClassName()));
		}

		if (task.getClassPK() == null) {
			map.put("classPK", null);
		}
		else {
			map.put("classPK", String.valueOf(task.getClassPK()));
		}

		if (task.getCompleted() == null) {
			map.put("completed", null);
		}
		else {
			map.put("completed", String.valueOf(task.getCompleted()));
		}

		if (task.getCompletionUserId() == null) {
			map.put("completionUserId", null);
		}
		else {
			map.put(
				"completionUserId", String.valueOf(task.getCompletionUserId()));
		}

		if (task.getDateCompletion() == null) {
			map.put("dateCompletion", null);
		}
		else {
			map.put(
				"dateCompletion",
				liferayToJSONDateFormat.format(task.getDateCompletion()));
		}

		if (task.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(task.getDateCreated()));
		}

		if (task.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(task.getDateModified()));
		}

		if (task.getDuration() == null) {
			map.put("duration", null);
		}
		else {
			map.put("duration", String.valueOf(task.getDuration()));
		}

		if (task.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(task.getId()));
		}

		if (task.getInstanceId() == null) {
			map.put("instanceId", null);
		}
		else {
			map.put("instanceId", String.valueOf(task.getInstanceId()));
		}

		if (task.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(task.getLabel()));
		}

		if (task.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(task.getName()));
		}

		if (task.getNodeId() == null) {
			map.put("nodeId", null);
		}
		else {
			map.put("nodeId", String.valueOf(task.getNodeId()));
		}

		if (task.getProcessId() == null) {
			map.put("processId", null);
		}
		else {
			map.put("processId", String.valueOf(task.getProcessId()));
		}

		if (task.getProcessVersion() == null) {
			map.put("processVersion", null);
		}
		else {
			map.put("processVersion", String.valueOf(task.getProcessVersion()));
		}

		return map;
	}

	public static class TaskJSONParser extends BaseJSONParser<Task> {

		@Override
		protected Task createDTO() {
			return new Task();
		}

		@Override
		protected Task[] createDTOArray(int size) {
			return new Task[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetType_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "assignee")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "completed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "completionUserId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCompletion")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "duration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "instanceId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "nodeId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "processId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "processVersion")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Task task, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				if (jsonParserFieldValue != null) {
					task.setAssetTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle_i18n")) {
				if (jsonParserFieldValue != null) {
					task.setAssetTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				if (jsonParserFieldValue != null) {
					task.setAssetType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetType_i18n")) {
				if (jsonParserFieldValue != null) {
					task.setAssetType_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assignee")) {
				if (jsonParserFieldValue != null) {
					task.setAssignee(
						AssigneeSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					task.setClassName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				if (jsonParserFieldValue != null) {
					task.setClassPK(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "completed")) {
				if (jsonParserFieldValue != null) {
					task.setCompleted((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "completionUserId")) {
				if (jsonParserFieldValue != null) {
					task.setCompletionUserId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCompletion")) {
				if (jsonParserFieldValue != null) {
					task.setDateCompletion(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					task.setDateCreated(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					task.setDateModified(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "duration")) {
				if (jsonParserFieldValue != null) {
					task.setDuration(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					task.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "instanceId")) {
				if (jsonParserFieldValue != null) {
					task.setInstanceId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					task.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					task.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "nodeId")) {
				if (jsonParserFieldValue != null) {
					task.setNodeId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "processId")) {
				if (jsonParserFieldValue != null) {
					task.setProcessId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "processVersion")) {
				if (jsonParserFieldValue != null) {
					task.setProcessVersion((String)jsonParserFieldValue);
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