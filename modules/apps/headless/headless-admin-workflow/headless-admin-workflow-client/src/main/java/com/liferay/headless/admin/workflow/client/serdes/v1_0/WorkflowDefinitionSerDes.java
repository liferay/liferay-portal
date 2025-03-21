/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.serdes.v1_0;

import com.liferay.headless.admin.workflow.client.dto.v1_0.Node;
import com.liferay.headless.admin.workflow.client.dto.v1_0.Transition;
import com.liferay.headless.admin.workflow.client.dto.v1_0.WorkflowDefinition;
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
public class WorkflowDefinitionSerDes {

	public static WorkflowDefinition toDTO(String json) {
		WorkflowDefinitionJSONParser workflowDefinitionJSONParser =
			new WorkflowDefinitionJSONParser();

		return workflowDefinitionJSONParser.parseToDTO(json);
	}

	public static WorkflowDefinition[] toDTOs(String json) {
		WorkflowDefinitionJSONParser workflowDefinitionJSONParser =
			new WorkflowDefinitionJSONParser();

		return workflowDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WorkflowDefinition workflowDefinition) {
		if (workflowDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (workflowDefinition.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(workflowDefinition.getActions()));
		}

		if (workflowDefinition.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(workflowDefinition.getActive());
		}

		if (workflowDefinition.getContent() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"content\": ");

			sb.append("\"");

			sb.append(_escape(workflowDefinition.getContent()));

			sb.append("\"");
		}

		if (workflowDefinition.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(workflowDefinition.getCreator()));
		}

		if (workflowDefinition.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					workflowDefinition.getDateCreated()));

			sb.append("\"");
		}

		if (workflowDefinition.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					workflowDefinition.getDateModified()));

			sb.append("\"");
		}

		if (workflowDefinition.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(workflowDefinition.getDescription()));

			sb.append("\"");
		}

		if (workflowDefinition.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(workflowDefinition.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (workflowDefinition.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(workflowDefinition.getId());
		}

		if (workflowDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(workflowDefinition.getName()));

			sb.append("\"");
		}

		if (workflowDefinition.getNodes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nodes\": ");

			sb.append("[");

			for (int i = 0; i < workflowDefinition.getNodes().length; i++) {
				sb.append(String.valueOf(workflowDefinition.getNodes()[i]));

				if ((i + 1) < workflowDefinition.getNodes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (workflowDefinition.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(workflowDefinition.getTitle()));

			sb.append("\"");
		}

		if (workflowDefinition.getTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title_i18n\": ");

			sb.append(_toJSON(workflowDefinition.getTitle_i18n()));
		}

		if (workflowDefinition.getTransitions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transitions\": ");

			sb.append("[");

			for (int i = 0; i < workflowDefinition.getTransitions().length;
				 i++) {

				sb.append(
					String.valueOf(workflowDefinition.getTransitions()[i]));

				if ((i + 1) < workflowDefinition.getTransitions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (workflowDefinition.getVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append("\"");

			sb.append(_escape(workflowDefinition.getVersion()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WorkflowDefinitionJSONParser workflowDefinitionJSONParser =
			new WorkflowDefinitionJSONParser();

		return workflowDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WorkflowDefinition workflowDefinition) {

		if (workflowDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (workflowDefinition.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(workflowDefinition.getActions()));
		}

		if (workflowDefinition.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(workflowDefinition.getActive()));
		}

		if (workflowDefinition.getContent() == null) {
			map.put("content", null);
		}
		else {
			map.put("content", String.valueOf(workflowDefinition.getContent()));
		}

		if (workflowDefinition.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(workflowDefinition.getCreator()));
		}

		if (workflowDefinition.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					workflowDefinition.getDateCreated()));
		}

		if (workflowDefinition.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					workflowDefinition.getDateModified()));
		}

		if (workflowDefinition.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(workflowDefinition.getDescription()));
		}

		if (workflowDefinition.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(workflowDefinition.getExternalReferenceCode()));
		}

		if (workflowDefinition.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(workflowDefinition.getId()));
		}

		if (workflowDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(workflowDefinition.getName()));
		}

		if (workflowDefinition.getNodes() == null) {
			map.put("nodes", null);
		}
		else {
			map.put("nodes", String.valueOf(workflowDefinition.getNodes()));
		}

		if (workflowDefinition.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(workflowDefinition.getTitle()));
		}

		if (workflowDefinition.getTitle_i18n() == null) {
			map.put("title_i18n", null);
		}
		else {
			map.put(
				"title_i18n",
				String.valueOf(workflowDefinition.getTitle_i18n()));
		}

		if (workflowDefinition.getTransitions() == null) {
			map.put("transitions", null);
		}
		else {
			map.put(
				"transitions",
				String.valueOf(workflowDefinition.getTransitions()));
		}

		if (workflowDefinition.getVersion() == null) {
			map.put("version", null);
		}
		else {
			map.put("version", String.valueOf(workflowDefinition.getVersion()));
		}

		return map;
	}

	public static class WorkflowDefinitionJSONParser
		extends BaseJSONParser<WorkflowDefinition> {

		@Override
		protected WorkflowDefinition createDTO() {
			return new WorkflowDefinition();
		}

		@Override
		protected WorkflowDefinition[] createDTOArray(int size) {
			return new WorkflowDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "content")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "nodes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "transitions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WorkflowDefinition workflowDefinition, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "content")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setContent((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					workflowDefinition.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "nodes")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Node[] nodesArray = new Node[jsonParserFieldValues.length];

					for (int i = 0; i < nodesArray.length; i++) {
						nodesArray[i] = NodeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					workflowDefinition.setNodes(nodesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "transitions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Transition[] transitionsArray =
						new Transition[jsonParserFieldValues.length];

					for (int i = 0; i < transitionsArray.length; i++) {
						transitionsArray[i] = TransitionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					workflowDefinition.setTransitions(transitionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				if (jsonParserFieldValue != null) {
					workflowDefinition.setVersion((String)jsonParserFieldValue);
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