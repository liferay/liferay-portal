/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Assignee;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Instance;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.SLAResult;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Transition;
import com.liferay.portal.workflow.metrics.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class InstanceSerDes {

	public static Instance toDTO(String json) {
		InstanceJSONParser instanceJSONParser = new InstanceJSONParser();

		return instanceJSONParser.parseToDTO(json);
	}

	public static Instance[] toDTOs(String json) {
		InstanceJSONParser instanceJSONParser = new InstanceJSONParser();

		return instanceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Instance instance) {
		if (instance == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (instance.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(instance.getActive());
		}

		if (instance.getAssetTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(instance.getAssetTitle()));

			sb.append("\"");
		}

		if (instance.getAssetTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle_i18n\": ");

			sb.append(_toJSON(instance.getAssetTitle_i18n()));
		}

		if (instance.getAssetType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType\": ");

			sb.append("\"");

			sb.append(_escape(instance.getAssetType()));

			sb.append("\"");
		}

		if (instance.getAssetType_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType_i18n\": ");

			sb.append(_toJSON(instance.getAssetType_i18n()));
		}

		if (instance.getAssignees() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assignees\": ");

			sb.append("[");

			for (int i = 0; i < instance.getAssignees().length; i++) {
				sb.append(String.valueOf(instance.getAssignees()[i]));

				if ((i + 1) < instance.getAssignees().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (instance.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(instance.getClassName()));

			sb.append("\"");
		}

		if (instance.getClassPK() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classPK\": ");

			sb.append(instance.getClassPK());
		}

		if (instance.getCompleted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completed\": ");

			sb.append(instance.getCompleted());
		}

		if (instance.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(instance.getCreator()));
		}

		if (instance.getDateCompletion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCompletion\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(instance.getDateCompletion()));

			sb.append("\"");
		}

		if (instance.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(instance.getDateCreated()));

			sb.append("\"");
		}

		if (instance.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(instance.getDateModified()));

			sb.append("\"");
		}

		if (instance.getDuration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"duration\": ");

			sb.append(instance.getDuration());
		}

		if (instance.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(instance.getId());
		}

		if (instance.getProcessId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"processId\": ");

			sb.append(instance.getProcessId());
		}

		if (instance.getProcessVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"processVersion\": ");

			sb.append("\"");

			sb.append(_escape(instance.getProcessVersion()));

			sb.append("\"");
		}

		if (instance.getSlaResults() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"slaResults\": ");

			sb.append("[");

			for (int i = 0; i < instance.getSlaResults().length; i++) {
				sb.append(String.valueOf(instance.getSlaResults()[i]));

				if ((i + 1) < instance.getSlaResults().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (instance.getSLAStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"slaStatus\": ");

			sb.append("\"");

			sb.append(instance.getSLAStatus());

			sb.append("\"");
		}

		if (instance.getTaskNames() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taskNames\": ");

			sb.append("[");

			for (int i = 0; i < instance.getTaskNames().length; i++) {
				sb.append(_toJSON(instance.getTaskNames()[i]));

				if ((i + 1) < instance.getTaskNames().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (instance.getTransitions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"transitions\": ");

			sb.append("[");

			for (int i = 0; i < instance.getTransitions().length; i++) {
				sb.append(String.valueOf(instance.getTransitions()[i]));

				if ((i + 1) < instance.getTransitions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		InstanceJSONParser instanceJSONParser = new InstanceJSONParser();

		return instanceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Instance instance) {
		if (instance == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (instance.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(instance.getActive()));
		}

		if (instance.getAssetTitle() == null) {
			map.put("assetTitle", null);
		}
		else {
			map.put("assetTitle", String.valueOf(instance.getAssetTitle()));
		}

		if (instance.getAssetTitle_i18n() == null) {
			map.put("assetTitle_i18n", null);
		}
		else {
			map.put(
				"assetTitle_i18n",
				String.valueOf(instance.getAssetTitle_i18n()));
		}

		if (instance.getAssetType() == null) {
			map.put("assetType", null);
		}
		else {
			map.put("assetType", String.valueOf(instance.getAssetType()));
		}

		if (instance.getAssetType_i18n() == null) {
			map.put("assetType_i18n", null);
		}
		else {
			map.put(
				"assetType_i18n", String.valueOf(instance.getAssetType_i18n()));
		}

		if (instance.getAssignees() == null) {
			map.put("assignees", null);
		}
		else {
			map.put("assignees", String.valueOf(instance.getAssignees()));
		}

		if (instance.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put("className", String.valueOf(instance.getClassName()));
		}

		if (instance.getClassPK() == null) {
			map.put("classPK", null);
		}
		else {
			map.put("classPK", String.valueOf(instance.getClassPK()));
		}

		if (instance.getCompleted() == null) {
			map.put("completed", null);
		}
		else {
			map.put("completed", String.valueOf(instance.getCompleted()));
		}

		if (instance.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(instance.getCreator()));
		}

		if (instance.getDateCompletion() == null) {
			map.put("dateCompletion", null);
		}
		else {
			map.put(
				"dateCompletion",
				liferayToJSONDateFormat.format(instance.getDateCompletion()));
		}

		if (instance.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(instance.getDateCreated()));
		}

		if (instance.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(instance.getDateModified()));
		}

		if (instance.getDuration() == null) {
			map.put("duration", null);
		}
		else {
			map.put("duration", String.valueOf(instance.getDuration()));
		}

		if (instance.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(instance.getId()));
		}

		if (instance.getProcessId() == null) {
			map.put("processId", null);
		}
		else {
			map.put("processId", String.valueOf(instance.getProcessId()));
		}

		if (instance.getProcessVersion() == null) {
			map.put("processVersion", null);
		}
		else {
			map.put(
				"processVersion", String.valueOf(instance.getProcessVersion()));
		}

		if (instance.getSlaResults() == null) {
			map.put("slaResults", null);
		}
		else {
			map.put("slaResults", String.valueOf(instance.getSlaResults()));
		}

		if (instance.getSLAStatus() == null) {
			map.put("slaStatus", null);
		}
		else {
			map.put("slaStatus", String.valueOf(instance.getSLAStatus()));
		}

		if (instance.getTaskNames() == null) {
			map.put("taskNames", null);
		}
		else {
			map.put("taskNames", String.valueOf(instance.getTaskNames()));
		}

		if (instance.getTransitions() == null) {
			map.put("transitions", null);
		}
		else {
			map.put("transitions", String.valueOf(instance.getTransitions()));
		}

		return map;
	}

	public static class InstanceJSONParser extends BaseJSONParser<Instance> {

		@Override
		protected Instance createDTO() {
			return new Instance();
		}

		@Override
		protected Instance[] createDTOArray(int size) {
			return new Instance[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
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
			else if (Objects.equals(jsonParserFieldName, "assignees")) {
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
			else if (Objects.equals(jsonParserFieldName, "creator")) {
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
			else if (Objects.equals(jsonParserFieldName, "processId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "processVersion")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "slaResults")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "slaStatus")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "taskNames")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "transitions")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Instance instance, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					instance.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				if (jsonParserFieldValue != null) {
					instance.setAssetTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle_i18n")) {
				if (jsonParserFieldValue != null) {
					instance.setAssetTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				if (jsonParserFieldValue != null) {
					instance.setAssetType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetType_i18n")) {
				if (jsonParserFieldValue != null) {
					instance.setAssetType_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assignees")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Assignee[] assigneesArray =
						new Assignee[jsonParserFieldValues.length];

					for (int i = 0; i < assigneesArray.length; i++) {
						assigneesArray[i] = AssigneeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					instance.setAssignees(assigneesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					instance.setClassName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				if (jsonParserFieldValue != null) {
					instance.setClassPK(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "completed")) {
				if (jsonParserFieldValue != null) {
					instance.setCompleted((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					instance.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCompletion")) {
				if (jsonParserFieldValue != null) {
					instance.setDateCompletion(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					instance.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					instance.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "duration")) {
				if (jsonParserFieldValue != null) {
					instance.setDuration(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					instance.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "processId")) {
				if (jsonParserFieldValue != null) {
					instance.setProcessId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "processVersion")) {
				if (jsonParserFieldValue != null) {
					instance.setProcessVersion((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "slaResults")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					SLAResult[] slaResultsArray =
						new SLAResult[jsonParserFieldValues.length];

					for (int i = 0; i < slaResultsArray.length; i++) {
						slaResultsArray[i] = SLAResultSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					instance.setSlaResults(slaResultsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "slaStatus")) {
				if (jsonParserFieldValue != null) {
					instance.setSLAStatus(
						Instance.SLAStatus.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taskNames")) {
				if (jsonParserFieldValue != null) {
					instance.setTaskNames(
						toStrings((Object[])jsonParserFieldValue));
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

					instance.setTransitions(transitionsArray);
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