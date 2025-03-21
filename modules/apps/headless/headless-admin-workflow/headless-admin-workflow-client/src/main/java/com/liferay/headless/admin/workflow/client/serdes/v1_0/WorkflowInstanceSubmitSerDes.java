/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.serdes.v1_0;

import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowInstanceSubmit;
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
public class WorkflowInstanceSubmitSerDes {

	public static WorkflowInstanceSubmit toDTO(String json) {
		WorkflowInstanceSubmitJSONParser workflowInstanceSubmitJSONParser =
			new WorkflowInstanceSubmitJSONParser();

		return workflowInstanceSubmitJSONParser.parseToDTO(json);
	}

	public static WorkflowInstanceSubmit[] toDTOs(String json) {
		WorkflowInstanceSubmitJSONParser workflowInstanceSubmitJSONParser =
			new WorkflowInstanceSubmitJSONParser();

		return workflowInstanceSubmitJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WorkflowInstanceSubmit workflowInstanceSubmit) {
		if (workflowInstanceSubmit == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (workflowInstanceSubmit.getContext() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"context\": ");

			sb.append(_toJSON(workflowInstanceSubmit.getContext()));
		}

		if (workflowInstanceSubmit.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(workflowInstanceSubmit.getSiteId());
		}

		if (workflowInstanceSubmit.getTransitionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transitionName\": ");

			sb.append("\"");

			sb.append(_escape(workflowInstanceSubmit.getTransitionName()));

			sb.append("\"");
		}

		if (workflowInstanceSubmit.getWorkflowDefinitionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionName\": ");

			sb.append("\"");

			sb.append(
				_escape(workflowInstanceSubmit.getWorkflowDefinitionName()));

			sb.append("\"");
		}

		if (workflowInstanceSubmit.getWorkflowDefinitionVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionVersion\": ");

			sb.append("\"");

			sb.append(
				_escape(workflowInstanceSubmit.getWorkflowDefinitionVersion()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WorkflowInstanceSubmitJSONParser workflowInstanceSubmitJSONParser =
			new WorkflowInstanceSubmitJSONParser();

		return workflowInstanceSubmitJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WorkflowInstanceSubmit workflowInstanceSubmit) {

		if (workflowInstanceSubmit == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (workflowInstanceSubmit.getContext() == null) {
			map.put("context", null);
		}
		else {
			map.put(
				"context", String.valueOf(workflowInstanceSubmit.getContext()));
		}

		if (workflowInstanceSubmit.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put(
				"siteId", String.valueOf(workflowInstanceSubmit.getSiteId()));
		}

		if (workflowInstanceSubmit.getTransitionName() == null) {
			map.put("transitionName", null);
		}
		else {
			map.put(
				"transitionName",
				String.valueOf(workflowInstanceSubmit.getTransitionName()));
		}

		if (workflowInstanceSubmit.getWorkflowDefinitionName() == null) {
			map.put("workflowDefinitionName", null);
		}
		else {
			map.put(
				"workflowDefinitionName",
				String.valueOf(
					workflowInstanceSubmit.getWorkflowDefinitionName()));
		}

		if (workflowInstanceSubmit.getWorkflowDefinitionVersion() == null) {
			map.put("workflowDefinitionVersion", null);
		}
		else {
			map.put(
				"workflowDefinitionVersion",
				String.valueOf(
					workflowInstanceSubmit.getWorkflowDefinitionVersion()));
		}

		return map;
	}

	public static class WorkflowInstanceSubmitJSONParser
		extends BaseJSONParser<WorkflowInstanceSubmit> {

		@Override
		protected WorkflowInstanceSubmit createDTO() {
			return new WorkflowInstanceSubmit();
		}

		@Override
		protected WorkflowInstanceSubmit[] createDTOArray(int size) {
			return new WorkflowInstanceSubmit[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "context")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "transitionName")) {
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
			WorkflowInstanceSubmit workflowInstanceSubmit,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "context")) {
				if (jsonParserFieldValue != null) {
					workflowInstanceSubmit.setContext(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					workflowInstanceSubmit.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "transitionName")) {
				if (jsonParserFieldValue != null) {
					workflowInstanceSubmit.setTransitionName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionName")) {

				if (jsonParserFieldValue != null) {
					workflowInstanceSubmit.setWorkflowDefinitionName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionVersion")) {

				if (jsonParserFieldValue != null) {
					workflowInstanceSubmit.setWorkflowDefinitionVersion(
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