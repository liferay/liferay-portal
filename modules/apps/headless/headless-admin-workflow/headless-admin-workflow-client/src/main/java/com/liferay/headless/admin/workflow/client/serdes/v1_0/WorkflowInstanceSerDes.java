/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.serdes.v1_0;

import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowInstance;
import com.liferay.headless.admin.workflow.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowInstanceSerDes {

	public static WorkflowInstance toDTO(String json) {
		WorkflowInstanceJSONParser workflowInstanceJSONParser =
			new WorkflowInstanceJSONParser();

		return workflowInstanceJSONParser.parseToDTO(json);
	}

	public static WorkflowInstance[] toDTOs(String json) {
		WorkflowInstanceJSONParser workflowInstanceJSONParser =
			new WorkflowInstanceJSONParser();

		return workflowInstanceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WorkflowInstance workflowInstance) {
		if (workflowInstance == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (workflowInstance.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(workflowInstance.getActions()));
		}

		if (workflowInstance.getCompleted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completed\": ");

			sb.append(workflowInstance.getCompleted());
		}

		if (workflowInstance.getCurrentNodeNames() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currentNodeNames\": ");

			sb.append("[");

			for (int i = 0; i < workflowInstance.getCurrentNodeNames().length;
				 i++) {

				sb.append(_toJSON(workflowInstance.getCurrentNodeNames()[i]));

				if ((i + 1) < workflowInstance.getCurrentNodeNames().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (workflowInstance.getDateCompletion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCompletion\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					workflowInstance.getDateCompletion()));

			sb.append("\"");
		}

		if (workflowInstance.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					workflowInstance.getDateCreated()));

			sb.append("\"");
		}

		if (workflowInstance.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(workflowInstance.getId());
		}

		if (workflowInstance.getObjectReviewed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectReviewed\": ");

			sb.append(String.valueOf(workflowInstance.getObjectReviewed()));
		}

		if (workflowInstance.getWorkflowDefinitionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionName\": ");

			sb.append("\"");

			sb.append(_escape(workflowInstance.getWorkflowDefinitionName()));

			sb.append("\"");
		}

		if (workflowInstance.getWorkflowDefinitionVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionVersion\": ");

			sb.append("\"");

			sb.append(_escape(workflowInstance.getWorkflowDefinitionVersion()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WorkflowInstanceJSONParser workflowInstanceJSONParser =
			new WorkflowInstanceJSONParser();

		return workflowInstanceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(WorkflowInstance workflowInstance) {
		if (workflowInstance == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (workflowInstance.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(workflowInstance.getActions()));
		}

		if (workflowInstance.getCompleted() == null) {
			map.put("completed", null);
		}
		else {
			map.put(
				"completed", String.valueOf(workflowInstance.getCompleted()));
		}

		if (workflowInstance.getCurrentNodeNames() == null) {
			map.put("currentNodeNames", null);
		}
		else {
			map.put(
				"currentNodeNames",
				String.valueOf(workflowInstance.getCurrentNodeNames()));
		}

		if (workflowInstance.getDateCompletion() == null) {
			map.put("dateCompletion", null);
		}
		else {
			map.put(
				"dateCompletion",
				liferayToJSONDateFormat.format(
					workflowInstance.getDateCompletion()));
		}

		if (workflowInstance.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					workflowInstance.getDateCreated()));
		}

		if (workflowInstance.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(workflowInstance.getId()));
		}

		if (workflowInstance.getObjectReviewed() == null) {
			map.put("objectReviewed", null);
		}
		else {
			map.put(
				"objectReviewed",
				String.valueOf(workflowInstance.getObjectReviewed()));
		}

		if (workflowInstance.getWorkflowDefinitionName() == null) {
			map.put("workflowDefinitionName", null);
		}
		else {
			map.put(
				"workflowDefinitionName",
				String.valueOf(workflowInstance.getWorkflowDefinitionName()));
		}

		if (workflowInstance.getWorkflowDefinitionVersion() == null) {
			map.put("workflowDefinitionVersion", null);
		}
		else {
			map.put(
				"workflowDefinitionVersion",
				String.valueOf(
					workflowInstance.getWorkflowDefinitionVersion()));
		}

		return map;
	}

	public static class WorkflowInstanceJSONParser
		extends BaseJSONParser<WorkflowInstance> {

		@Override
		protected WorkflowInstance createDTO() {
			return new WorkflowInstance();
		}

		@Override
		protected WorkflowInstance[] createDTOArray(int size) {
			return new WorkflowInstance[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "completed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "currentNodeNames")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCompletion")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "objectReviewed")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionName")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionVersion")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WorkflowInstance workflowInstance, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					workflowInstance.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "completed")) {
				if (jsonParserFieldValue != null) {
					workflowInstance.setCompleted(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currentNodeNames")) {
				if (jsonParserFieldValue != null) {
					workflowInstance.setCurrentNodeNames(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCompletion")) {
				if (jsonParserFieldValue != null) {
					workflowInstance.setDateCompletion(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					workflowInstance.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					workflowInstance.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectReviewed")) {
				if (jsonParserFieldValue != null) {
					workflowInstance.setObjectReviewed(
						ObjectReviewedSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionName")) {

				if (jsonParserFieldValue != null) {
					workflowInstance.setWorkflowDefinitionName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionVersion")) {

				if (jsonParserFieldValue != null) {
					workflowInstance.setWorkflowDefinitionVersion(
						(String)jsonParserFieldValue);
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