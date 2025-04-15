/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.serdes.v1_0;

import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowTaskAssignableUser;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowTaskAssignableUsers;
import com.liferay.headless.admin.workflow.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowTaskAssignableUsersSerDes {

	public static WorkflowTaskAssignableUsers toDTO(String json) {
		WorkflowTaskAssignableUsersJSONParser
			workflowTaskAssignableUsersJSONParser =
				new WorkflowTaskAssignableUsersJSONParser();

		return workflowTaskAssignableUsersJSONParser.parseToDTO(json);
	}

	public static WorkflowTaskAssignableUsers[] toDTOs(String json) {
		WorkflowTaskAssignableUsersJSONParser
			workflowTaskAssignableUsersJSONParser =
				new WorkflowTaskAssignableUsersJSONParser();

		return workflowTaskAssignableUsersJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		WorkflowTaskAssignableUsers workflowTaskAssignableUsers) {

		if (workflowTaskAssignableUsers == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (workflowTaskAssignableUsers.getWorkflowTaskAssignableUsers() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowTaskAssignableUsers\": ");

			sb.append("[");

			for (int i = 0;
				 i < workflowTaskAssignableUsers.
					 getWorkflowTaskAssignableUsers().length;
				 i++) {

				sb.append(
					String.valueOf(
						workflowTaskAssignableUsers.
							getWorkflowTaskAssignableUsers()[i]));

				if ((i + 1) < workflowTaskAssignableUsers.
						getWorkflowTaskAssignableUsers().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WorkflowTaskAssignableUsersJSONParser
			workflowTaskAssignableUsersJSONParser =
				new WorkflowTaskAssignableUsersJSONParser();

		return workflowTaskAssignableUsersJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WorkflowTaskAssignableUsers workflowTaskAssignableUsers) {

		if (workflowTaskAssignableUsers == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (workflowTaskAssignableUsers.getWorkflowTaskAssignableUsers() ==
				null) {

			map.put("workflowTaskAssignableUsers", null);
		}
		else {
			map.put(
				"workflowTaskAssignableUsers",
				String.valueOf(
					workflowTaskAssignableUsers.
						getWorkflowTaskAssignableUsers()));
		}

		return map;
	}

	public static class WorkflowTaskAssignableUsersJSONParser
		extends BaseJSONParser<WorkflowTaskAssignableUsers> {

		@Override
		protected WorkflowTaskAssignableUsers createDTO() {
			return new WorkflowTaskAssignableUsers();
		}

		@Override
		protected WorkflowTaskAssignableUsers[] createDTOArray(int size) {
			return new WorkflowTaskAssignableUsers[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "workflowTaskAssignableUsers")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WorkflowTaskAssignableUsers workflowTaskAssignableUsers,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "workflowTaskAssignableUsers")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					WorkflowTaskAssignableUser[]
						workflowTaskAssignableUsersArray =
							new WorkflowTaskAssignableUser
								[jsonParserFieldValues.length];

					for (int i = 0; i < workflowTaskAssignableUsersArray.length;
						 i++) {

						workflowTaskAssignableUsersArray[i] =
							WorkflowTaskAssignableUserSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					workflowTaskAssignableUsers.setWorkflowTaskAssignableUsers(
						workflowTaskAssignableUsersArray);
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