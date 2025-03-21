/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.serdes.v1_0;

import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowDefinitionLink;
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
public class WorkflowDefinitionLinkSerDes {

	public static WorkflowDefinitionLink toDTO(String json) {
		WorkflowDefinitionLinkJSONParser workflowDefinitionLinkJSONParser =
			new WorkflowDefinitionLinkJSONParser();

		return workflowDefinitionLinkJSONParser.parseToDTO(json);
	}

	public static WorkflowDefinitionLink[] toDTOs(String json) {
		WorkflowDefinitionLinkJSONParser workflowDefinitionLinkJSONParser =
			new WorkflowDefinitionLinkJSONParser();

		return workflowDefinitionLinkJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WorkflowDefinitionLink workflowDefinitionLink) {
		if (workflowDefinitionLink == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (workflowDefinitionLink.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(workflowDefinitionLink.getClassName()));

			sb.append("\"");
		}

		if (workflowDefinitionLink.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(workflowDefinitionLink.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (workflowDefinitionLink.getGroupExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					workflowDefinitionLink.getGroupExternalReferenceCode()));

			sb.append("\"");
		}

		if (workflowDefinitionLink.getGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groupId\": ");

			sb.append(workflowDefinitionLink.getGroupId());
		}

		if (workflowDefinitionLink.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(workflowDefinitionLink.getId());
		}

		if (workflowDefinitionLink.getWorkflowDefinitionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionName\": ");

			sb.append("\"");

			sb.append(
				_escape(workflowDefinitionLink.getWorkflowDefinitionName()));

			sb.append("\"");
		}

		if (workflowDefinitionLink.getWorkflowDefinitionVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionVersion\": ");

			sb.append(workflowDefinitionLink.getWorkflowDefinitionVersion());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WorkflowDefinitionLinkJSONParser workflowDefinitionLinkJSONParser =
			new WorkflowDefinitionLinkJSONParser();

		return workflowDefinitionLinkJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WorkflowDefinitionLink workflowDefinitionLink) {

		if (workflowDefinitionLink == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (workflowDefinitionLink.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(workflowDefinitionLink.getClassName()));
		}

		if (workflowDefinitionLink.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					workflowDefinitionLink.getExternalReferenceCode()));
		}

		if (workflowDefinitionLink.getGroupExternalReferenceCode() == null) {
			map.put("groupExternalReferenceCode", null);
		}
		else {
			map.put(
				"groupExternalReferenceCode",
				String.valueOf(
					workflowDefinitionLink.getGroupExternalReferenceCode()));
		}

		if (workflowDefinitionLink.getGroupId() == null) {
			map.put("groupId", null);
		}
		else {
			map.put(
				"groupId", String.valueOf(workflowDefinitionLink.getGroupId()));
		}

		if (workflowDefinitionLink.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(workflowDefinitionLink.getId()));
		}

		if (workflowDefinitionLink.getWorkflowDefinitionName() == null) {
			map.put("workflowDefinitionName", null);
		}
		else {
			map.put(
				"workflowDefinitionName",
				String.valueOf(
					workflowDefinitionLink.getWorkflowDefinitionName()));
		}

		if (workflowDefinitionLink.getWorkflowDefinitionVersion() == null) {
			map.put("workflowDefinitionVersion", null);
		}
		else {
			map.put(
				"workflowDefinitionVersion",
				String.valueOf(
					workflowDefinitionLink.getWorkflowDefinitionVersion()));
		}

		return map;
	}

	public static class WorkflowDefinitionLinkJSONParser
		extends BaseJSONParser<WorkflowDefinitionLink> {

		@Override
		protected WorkflowDefinitionLink createDTO() {
			return new WorkflowDefinitionLink();
		}

		@Override
		protected WorkflowDefinitionLink[] createDTOArray(int size) {
			return new WorkflowDefinitionLink[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "groupExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "groupId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
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
			WorkflowDefinitionLink workflowDefinitionLink,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					workflowDefinitionLink.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					workflowDefinitionLink.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "groupExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					workflowDefinitionLink.setGroupExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "groupId")) {
				if (jsonParserFieldValue != null) {
					workflowDefinitionLink.setGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					workflowDefinitionLink.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionName")) {

				if (jsonParserFieldValue != null) {
					workflowDefinitionLink.setWorkflowDefinitionName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "workflowDefinitionVersion")) {

				if (jsonParserFieldValue != null) {
					workflowDefinitionLink.setWorkflowDefinitionVersion(
						Integer.valueOf((String)jsonParserFieldValue));
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