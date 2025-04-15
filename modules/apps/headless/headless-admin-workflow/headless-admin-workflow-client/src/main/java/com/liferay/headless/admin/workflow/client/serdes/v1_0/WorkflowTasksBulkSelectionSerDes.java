/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.serdes.v1_0;

import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowTasksBulkSelection;
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
public class WorkflowTasksBulkSelectionSerDes {

	public static WorkflowTasksBulkSelection toDTO(String json) {
		WorkflowTasksBulkSelectionJSONParser
			workflowTasksBulkSelectionJSONParser =
				new WorkflowTasksBulkSelectionJSONParser();

		return workflowTasksBulkSelectionJSONParser.parseToDTO(json);
	}

	public static WorkflowTasksBulkSelection[] toDTOs(String json) {
		WorkflowTasksBulkSelectionJSONParser
			workflowTasksBulkSelectionJSONParser =
				new WorkflowTasksBulkSelectionJSONParser();

		return workflowTasksBulkSelectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		WorkflowTasksBulkSelection workflowTasksBulkSelection) {

		if (workflowTasksBulkSelection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (workflowTasksBulkSelection.getAndOperator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"andOperator\": ");

			sb.append(workflowTasksBulkSelection.getAndOperator());
		}

		if (workflowTasksBulkSelection.getAssetPrimaryKeys() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetPrimaryKeys\": ");

			sb.append("[");

			for (int i = 0;
				 i < workflowTasksBulkSelection.getAssetPrimaryKeys().length;
				 i++) {

				sb.append(workflowTasksBulkSelection.getAssetPrimaryKeys()[i]);

				if ((i + 1) <
						workflowTasksBulkSelection.
							getAssetPrimaryKeys().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (workflowTasksBulkSelection.getAssetTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(workflowTasksBulkSelection.getAssetTitle()));

			sb.append("\"");
		}

		if (workflowTasksBulkSelection.getAssetTypes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTypes\": ");

			sb.append("[");

			for (int i = 0;
				 i < workflowTasksBulkSelection.getAssetTypes().length; i++) {

				sb.append(
					_toJSON(workflowTasksBulkSelection.getAssetTypes()[i]));

				if ((i + 1) <
						workflowTasksBulkSelection.getAssetTypes().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (workflowTasksBulkSelection.getAssigneeIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assigneeIds\": ");

			sb.append("[");

			for (int i = 0;
				 i < workflowTasksBulkSelection.getAssigneeIds().length; i++) {

				sb.append(workflowTasksBulkSelection.getAssigneeIds()[i]);

				if ((i + 1) <
						workflowTasksBulkSelection.getAssigneeIds().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (workflowTasksBulkSelection.getCompleted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completed\": ");

			sb.append(workflowTasksBulkSelection.getCompleted());
		}

		if (workflowTasksBulkSelection.getDateDueEnd() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateDueEnd\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					workflowTasksBulkSelection.getDateDueEnd()));

			sb.append("\"");
		}

		if (workflowTasksBulkSelection.getDateDueStart() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateDueStart\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					workflowTasksBulkSelection.getDateDueStart()));

			sb.append("\"");
		}

		if (workflowTasksBulkSelection.getSearchByRoles() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchByRoles\": ");

			sb.append(workflowTasksBulkSelection.getSearchByRoles());
		}

		if (workflowTasksBulkSelection.getSearchByUserRoles() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchByUserRoles\": ");

			sb.append(workflowTasksBulkSelection.getSearchByUserRoles());
		}

		if (workflowTasksBulkSelection.getWorkflowDefinitionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionId\": ");

			sb.append(workflowTasksBulkSelection.getWorkflowDefinitionId());
		}

		if (workflowTasksBulkSelection.getWorkflowInstanceIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowInstanceIds\": ");

			sb.append("[");

			for (int i = 0;
				 i < workflowTasksBulkSelection.getWorkflowInstanceIds().length;
				 i++) {

				sb.append(
					workflowTasksBulkSelection.getWorkflowInstanceIds()[i]);

				if ((i + 1) < workflowTasksBulkSelection.
						getWorkflowInstanceIds().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (workflowTasksBulkSelection.getWorkflowTaskNames() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowTaskNames\": ");

			sb.append("[");

			for (int i = 0;
				 i < workflowTasksBulkSelection.getWorkflowTaskNames().length;
				 i++) {

				sb.append(
					_toJSON(
						workflowTasksBulkSelection.getWorkflowTaskNames()[i]));

				if ((i + 1) <
						workflowTasksBulkSelection.
							getWorkflowTaskNames().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WorkflowTasksBulkSelectionJSONParser
			workflowTasksBulkSelectionJSONParser =
				new WorkflowTasksBulkSelectionJSONParser();

		return workflowTasksBulkSelectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WorkflowTasksBulkSelection workflowTasksBulkSelection) {

		if (workflowTasksBulkSelection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (workflowTasksBulkSelection.getAndOperator() == null) {
			map.put("andOperator", null);
		}
		else {
			map.put(
				"andOperator",
				String.valueOf(workflowTasksBulkSelection.getAndOperator()));
		}

		if (workflowTasksBulkSelection.getAssetPrimaryKeys() == null) {
			map.put("assetPrimaryKeys", null);
		}
		else {
			map.put(
				"assetPrimaryKeys",
				String.valueOf(
					workflowTasksBulkSelection.getAssetPrimaryKeys()));
		}

		if (workflowTasksBulkSelection.getAssetTitle() == null) {
			map.put("assetTitle", null);
		}
		else {
			map.put(
				"assetTitle",
				String.valueOf(workflowTasksBulkSelection.getAssetTitle()));
		}

		if (workflowTasksBulkSelection.getAssetTypes() == null) {
			map.put("assetTypes", null);
		}
		else {
			map.put(
				"assetTypes",
				String.valueOf(workflowTasksBulkSelection.getAssetTypes()));
		}

		if (workflowTasksBulkSelection.getAssigneeIds() == null) {
			map.put("assigneeIds", null);
		}
		else {
			map.put(
				"assigneeIds",
				String.valueOf(workflowTasksBulkSelection.getAssigneeIds()));
		}

		if (workflowTasksBulkSelection.getCompleted() == null) {
			map.put("completed", null);
		}
		else {
			map.put(
				"completed",
				String.valueOf(workflowTasksBulkSelection.getCompleted()));
		}

		if (workflowTasksBulkSelection.getDateDueEnd() == null) {
			map.put("dateDueEnd", null);
		}
		else {
			map.put(
				"dateDueEnd",
				liferayToJSONDateFormat.format(
					workflowTasksBulkSelection.getDateDueEnd()));
		}

		if (workflowTasksBulkSelection.getDateDueStart() == null) {
			map.put("dateDueStart", null);
		}
		else {
			map.put(
				"dateDueStart",
				liferayToJSONDateFormat.format(
					workflowTasksBulkSelection.getDateDueStart()));
		}

		if (workflowTasksBulkSelection.getSearchByRoles() == null) {
			map.put("searchByRoles", null);
		}
		else {
			map.put(
				"searchByRoles",
				String.valueOf(workflowTasksBulkSelection.getSearchByRoles()));
		}

		if (workflowTasksBulkSelection.getSearchByUserRoles() == null) {
			map.put("searchByUserRoles", null);
		}
		else {
			map.put(
				"searchByUserRoles",
				String.valueOf(
					workflowTasksBulkSelection.getSearchByUserRoles()));
		}

		if (workflowTasksBulkSelection.getWorkflowDefinitionId() == null) {
			map.put("workflowDefinitionId", null);
		}
		else {
			map.put(
				"workflowDefinitionId",
				String.valueOf(
					workflowTasksBulkSelection.getWorkflowDefinitionId()));
		}

		if (workflowTasksBulkSelection.getWorkflowInstanceIds() == null) {
			map.put("workflowInstanceIds", null);
		}
		else {
			map.put(
				"workflowInstanceIds",
				String.valueOf(
					workflowTasksBulkSelection.getWorkflowInstanceIds()));
		}

		if (workflowTasksBulkSelection.getWorkflowTaskNames() == null) {
			map.put("workflowTaskNames", null);
		}
		else {
			map.put(
				"workflowTaskNames",
				String.valueOf(
					workflowTasksBulkSelection.getWorkflowTaskNames()));
		}

		return map;
	}

	public static class WorkflowTasksBulkSelectionJSONParser
		extends BaseJSONParser<WorkflowTasksBulkSelection> {

		@Override
		protected WorkflowTasksBulkSelection createDTO() {
			return new WorkflowTasksBulkSelection();
		}

		@Override
		protected WorkflowTasksBulkSelection[] createDTOArray(int size) {
			return new WorkflowTasksBulkSelection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "andOperator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetPrimaryKeys")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetTypes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assigneeIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "completed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateDueEnd")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateDueStart")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "searchByRoles")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "searchByUserRoles")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowInstanceIds")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "workflowTaskNames")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WorkflowTasksBulkSelection workflowTasksBulkSelection,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "andOperator")) {
				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setAndOperator(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetPrimaryKeys")) {
				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setAssetPrimaryKeys(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setAssetTitle(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetTypes")) {
				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setAssetTypes(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assigneeIds")) {
				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setAssigneeIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "completed")) {
				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setCompleted(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateDueEnd")) {
				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setDateDueEnd(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateDueStart")) {
				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setDateDueStart(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "searchByRoles")) {
				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setSearchByRoles(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "searchByUserRoles")) {
				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setSearchByUserRoles(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionId")) {

				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setWorkflowDefinitionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowInstanceIds")) {

				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setWorkflowInstanceIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "workflowTaskNames")) {
				if (jsonParserFieldValue != null) {
					workflowTasksBulkSelection.setWorkflowTaskNames(
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