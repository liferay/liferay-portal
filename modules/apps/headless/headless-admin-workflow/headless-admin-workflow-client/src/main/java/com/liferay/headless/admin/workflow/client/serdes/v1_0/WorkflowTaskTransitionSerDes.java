/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.serdes.v1_0;

import com.liferay.headless.admin.workflow.client.dto.v1_0.Transition;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowTaskTransition;
import com.liferay.headless.admin.workflow.client.json.BaseJSONParser;

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
public class WorkflowTaskTransitionSerDes {

	public static WorkflowTaskTransition toDTO(String json) {
		WorkflowTaskTransitionJSONParser workflowTaskTransitionJSONParser =
			new WorkflowTaskTransitionJSONParser();

		return workflowTaskTransitionJSONParser.parseToDTO(json);
	}

	public static WorkflowTaskTransition[] toDTOs(String json) {
		WorkflowTaskTransitionJSONParser workflowTaskTransitionJSONParser =
			new WorkflowTaskTransitionJSONParser();

		return workflowTaskTransitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WorkflowTaskTransition workflowTaskTransition) {
		if (workflowTaskTransition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (workflowTaskTransition.getTransitions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transitions\": ");

			sb.append("[");

			for (int i = 0; i < workflowTaskTransition.getTransitions().length;
				 i++) {

				sb.append(
					String.valueOf(workflowTaskTransition.getTransitions()[i]));

				if ((i + 1) < workflowTaskTransition.getTransitions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (workflowTaskTransition.getWorkflowDefinitionVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionVersion\": ");

			sb.append("\"");

			sb.append(
				_escape(workflowTaskTransition.getWorkflowDefinitionVersion()));

			sb.append("\"");
		}

		if (workflowTaskTransition.getWorkflowTaskLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowTaskLabel\": ");

			sb.append("\"");

			sb.append(_escape(workflowTaskTransition.getWorkflowTaskLabel()));

			sb.append("\"");
		}

		if (workflowTaskTransition.getWorkflowTaskName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowTaskName\": ");

			sb.append("\"");

			sb.append(_escape(workflowTaskTransition.getWorkflowTaskName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WorkflowTaskTransitionJSONParser workflowTaskTransitionJSONParser =
			new WorkflowTaskTransitionJSONParser();

		return workflowTaskTransitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WorkflowTaskTransition workflowTaskTransition) {

		if (workflowTaskTransition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (workflowTaskTransition.getTransitions() == null) {
			map.put("transitions", null);
		}
		else {
			map.put(
				"transitions",
				String.valueOf(workflowTaskTransition.getTransitions()));
		}

		if (workflowTaskTransition.getWorkflowDefinitionVersion() == null) {
			map.put("workflowDefinitionVersion", null);
		}
		else {
			map.put(
				"workflowDefinitionVersion",
				String.valueOf(
					workflowTaskTransition.getWorkflowDefinitionVersion()));
		}

		if (workflowTaskTransition.getWorkflowTaskLabel() == null) {
			map.put("workflowTaskLabel", null);
		}
		else {
			map.put(
				"workflowTaskLabel",
				String.valueOf(workflowTaskTransition.getWorkflowTaskLabel()));
		}

		if (workflowTaskTransition.getWorkflowTaskName() == null) {
			map.put("workflowTaskName", null);
		}
		else {
			map.put(
				"workflowTaskName",
				String.valueOf(workflowTaskTransition.getWorkflowTaskName()));
		}

		return map;
	}

	public static class WorkflowTaskTransitionJSONParser
		extends BaseJSONParser<WorkflowTaskTransition> {

		@Override
		protected WorkflowTaskTransition createDTO() {
			return new WorkflowTaskTransition();
		}

		@Override
		protected WorkflowTaskTransition[] createDTOArray(int size) {
			return new WorkflowTaskTransition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "transitions")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionVersion")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "workflowTaskLabel")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "workflowTaskName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WorkflowTaskTransition workflowTaskTransition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "transitions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Transition[] transitionsArray =
						new Transition[jsonParserFieldValues.length];

					for (int i = 0; i < transitionsArray.length; i++) {
						transitionsArray[i] = TransitionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					workflowTaskTransition.setTransitions(transitionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionVersion")) {

				if (jsonParserFieldValue != null) {
					workflowTaskTransition.setWorkflowDefinitionVersion(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "workflowTaskLabel")) {
				if (jsonParserFieldValue != null) {
					workflowTaskTransition.setWorkflowTaskLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "workflowTaskName")) {
				if (jsonParserFieldValue != null) {
					workflowTaskTransition.setWorkflowTaskName(
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