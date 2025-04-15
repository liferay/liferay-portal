/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.serdes.v1_0;

import com.liferay.headless.admin.workflow.client.dto.v1_0.Role;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowTask;
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
public class WorkflowTaskSerDes {

	public static WorkflowTask toDTO(String json) {
		WorkflowTaskJSONParser workflowTaskJSONParser =
			new WorkflowTaskJSONParser();

		return workflowTaskJSONParser.parseToDTO(json);
	}

	public static WorkflowTask[] toDTOs(String json) {
		WorkflowTaskJSONParser workflowTaskJSONParser =
			new WorkflowTaskJSONParser();

		return workflowTaskJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WorkflowTask workflowTask) {
		if (workflowTask == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (workflowTask.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(workflowTask.getActions()));
		}

		if (workflowTask.getAssigneePerson() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assigneePerson\": ");

			sb.append(String.valueOf(workflowTask.getAssigneePerson()));
		}

		if (workflowTask.getAssigneeRoles() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assigneeRoles\": ");

			sb.append("[");

			for (int i = 0; i < workflowTask.getAssigneeRoles().length; i++) {
				sb.append(String.valueOf(workflowTask.getAssigneeRoles()[i]));

				if ((i + 1) < workflowTask.getAssigneeRoles().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (workflowTask.getCompleted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completed\": ");

			sb.append(workflowTask.getCompleted());
		}

		if (workflowTask.getDateCompletion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCompletion\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					workflowTask.getDateCompletion()));

			sb.append("\"");
		}

		if (workflowTask.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(workflowTask.getDateCreated()));

			sb.append("\"");
		}

		if (workflowTask.getDateDue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateDue\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(workflowTask.getDateDue()));

			sb.append("\"");
		}

		if (workflowTask.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(workflowTask.getDescription()));

			sb.append("\"");
		}

		if (workflowTask.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(workflowTask.getId());
		}

		if (workflowTask.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(workflowTask.getLabel()));

			sb.append("\"");
		}

		if (workflowTask.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(workflowTask.getName()));

			sb.append("\"");
		}

		if (workflowTask.getObjectReviewed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectReviewed\": ");

			sb.append(String.valueOf(workflowTask.getObjectReviewed()));
		}

		if (workflowTask.getWorkflowDefinitionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionId\": ");

			sb.append(workflowTask.getWorkflowDefinitionId());
		}

		if (workflowTask.getWorkflowDefinitionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionName\": ");

			sb.append("\"");

			sb.append(_escape(workflowTask.getWorkflowDefinitionName()));

			sb.append("\"");
		}

		if (workflowTask.getWorkflowDefinitionVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionVersion\": ");

			sb.append("\"");

			sb.append(_escape(workflowTask.getWorkflowDefinitionVersion()));

			sb.append("\"");
		}

		if (workflowTask.getWorkflowInstanceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowInstanceId\": ");

			sb.append(workflowTask.getWorkflowInstanceId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WorkflowTaskJSONParser workflowTaskJSONParser =
			new WorkflowTaskJSONParser();

		return workflowTaskJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(WorkflowTask workflowTask) {
		if (workflowTask == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (workflowTask.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(workflowTask.getActions()));
		}

		if (workflowTask.getAssigneePerson() == null) {
			map.put("assigneePerson", null);
		}
		else {
			map.put(
				"assigneePerson",
				String.valueOf(workflowTask.getAssigneePerson()));
		}

		if (workflowTask.getAssigneeRoles() == null) {
			map.put("assigneeRoles", null);
		}
		else {
			map.put(
				"assigneeRoles",
				String.valueOf(workflowTask.getAssigneeRoles()));
		}

		if (workflowTask.getCompleted() == null) {
			map.put("completed", null);
		}
		else {
			map.put("completed", String.valueOf(workflowTask.getCompleted()));
		}

		if (workflowTask.getDateCompletion() == null) {
			map.put("dateCompletion", null);
		}
		else {
			map.put(
				"dateCompletion",
				liferayToJSONDateFormat.format(
					workflowTask.getDateCompletion()));
		}

		if (workflowTask.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(workflowTask.getDateCreated()));
		}

		if (workflowTask.getDateDue() == null) {
			map.put("dateDue", null);
		}
		else {
			map.put(
				"dateDue",
				liferayToJSONDateFormat.format(workflowTask.getDateDue()));
		}

		if (workflowTask.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(workflowTask.getDescription()));
		}

		if (workflowTask.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(workflowTask.getId()));
		}

		if (workflowTask.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(workflowTask.getLabel()));
		}

		if (workflowTask.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(workflowTask.getName()));
		}

		if (workflowTask.getObjectReviewed() == null) {
			map.put("objectReviewed", null);
		}
		else {
			map.put(
				"objectReviewed",
				String.valueOf(workflowTask.getObjectReviewed()));
		}

		if (workflowTask.getWorkflowDefinitionId() == null) {
			map.put("workflowDefinitionId", null);
		}
		else {
			map.put(
				"workflowDefinitionId",
				String.valueOf(workflowTask.getWorkflowDefinitionId()));
		}

		if (workflowTask.getWorkflowDefinitionName() == null) {
			map.put("workflowDefinitionName", null);
		}
		else {
			map.put(
				"workflowDefinitionName",
				String.valueOf(workflowTask.getWorkflowDefinitionName()));
		}

		if (workflowTask.getWorkflowDefinitionVersion() == null) {
			map.put("workflowDefinitionVersion", null);
		}
		else {
			map.put(
				"workflowDefinitionVersion",
				String.valueOf(workflowTask.getWorkflowDefinitionVersion()));
		}

		if (workflowTask.getWorkflowInstanceId() == null) {
			map.put("workflowInstanceId", null);
		}
		else {
			map.put(
				"workflowInstanceId",
				String.valueOf(workflowTask.getWorkflowInstanceId()));
		}

		return map;
	}

	public static class WorkflowTaskJSONParser
		extends BaseJSONParser<WorkflowTask> {

		@Override
		protected WorkflowTask createDTO() {
			return new WorkflowTask();
		}

		@Override
		protected WorkflowTask[] createDTOArray(int size) {
			return new WorkflowTask[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "assigneePerson")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assigneeRoles")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "completed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCompletion")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateDue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "objectReviewed")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionId")) {

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
			else if (Objects.equals(
						jsonParserFieldName, "workflowInstanceId")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WorkflowTask workflowTask, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assigneePerson")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setAssigneePerson(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assigneeRoles")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Role[] assigneeRolesArray =
						new Role[jsonParserFieldValues.length];

					for (int i = 0; i < assigneeRolesArray.length; i++) {
						assigneeRolesArray[i] = RoleSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					workflowTask.setAssigneeRoles(assigneeRolesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "completed")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setCompleted((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCompletion")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setDateCompletion(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateDue")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setDateDue(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectReviewed")) {
				if (jsonParserFieldValue != null) {
					workflowTask.setObjectReviewed(
						ObjectReviewedSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionId")) {

				if (jsonParserFieldValue != null) {
					workflowTask.setWorkflowDefinitionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionName")) {

				if (jsonParserFieldValue != null) {
					workflowTask.setWorkflowDefinitionName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionVersion")) {

				if (jsonParserFieldValue != null) {
					workflowTask.setWorkflowDefinitionVersion(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowInstanceId")) {

				if (jsonParserFieldValue != null) {
					workflowTask.setWorkflowInstanceId(
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