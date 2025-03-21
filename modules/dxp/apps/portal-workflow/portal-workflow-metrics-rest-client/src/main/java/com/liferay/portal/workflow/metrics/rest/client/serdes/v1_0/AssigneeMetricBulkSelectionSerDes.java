/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.AssigneeMetricBulkSelection;
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
public class AssigneeMetricBulkSelectionSerDes {

	public static AssigneeMetricBulkSelection toDTO(String json) {
		AssigneeMetricBulkSelectionJSONParser
			assigneeMetricBulkSelectionJSONParser =
				new AssigneeMetricBulkSelectionJSONParser();

		return assigneeMetricBulkSelectionJSONParser.parseToDTO(json);
	}

	public static AssigneeMetricBulkSelection[] toDTOs(String json) {
		AssigneeMetricBulkSelectionJSONParser
			assigneeMetricBulkSelectionJSONParser =
				new AssigneeMetricBulkSelectionJSONParser();

		return assigneeMetricBulkSelectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		AssigneeMetricBulkSelection assigneeMetricBulkSelection) {

		if (assigneeMetricBulkSelection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (assigneeMetricBulkSelection.getCompleted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completed\": ");

			sb.append(assigneeMetricBulkSelection.getCompleted());
		}

		if (assigneeMetricBulkSelection.getDateEnd() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateEnd\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					assigneeMetricBulkSelection.getDateEnd()));

			sb.append("\"");
		}

		if (assigneeMetricBulkSelection.getDateStart() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateStart\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					assigneeMetricBulkSelection.getDateStart()));

			sb.append("\"");
		}

		if (assigneeMetricBulkSelection.getInstanceIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"instanceIds\": ");

			sb.append("[");

			for (int i = 0;
				 i < assigneeMetricBulkSelection.getInstanceIds().length; i++) {

				sb.append(assigneeMetricBulkSelection.getInstanceIds()[i]);

				if ((i + 1) <
						assigneeMetricBulkSelection.getInstanceIds().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (assigneeMetricBulkSelection.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("\"");

			sb.append(_escape(assigneeMetricBulkSelection.getKeywords()));

			sb.append("\"");
		}

		if (assigneeMetricBulkSelection.getRoleIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleIds\": ");

			sb.append("[");

			for (int i = 0; i < assigneeMetricBulkSelection.getRoleIds().length;
				 i++) {

				sb.append(assigneeMetricBulkSelection.getRoleIds()[i]);

				if ((i + 1) < assigneeMetricBulkSelection.getRoleIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (assigneeMetricBulkSelection.getTaskNames() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taskNames\": ");

			sb.append("[");

			for (int i = 0;
				 i < assigneeMetricBulkSelection.getTaskNames().length; i++) {

				sb.append(
					_toJSON(assigneeMetricBulkSelection.getTaskNames()[i]));

				if ((i + 1) <
						assigneeMetricBulkSelection.getTaskNames().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssigneeMetricBulkSelectionJSONParser
			assigneeMetricBulkSelectionJSONParser =
				new AssigneeMetricBulkSelectionJSONParser();

		return assigneeMetricBulkSelectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AssigneeMetricBulkSelection assigneeMetricBulkSelection) {

		if (assigneeMetricBulkSelection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (assigneeMetricBulkSelection.getCompleted() == null) {
			map.put("completed", null);
		}
		else {
			map.put(
				"completed",
				String.valueOf(assigneeMetricBulkSelection.getCompleted()));
		}

		if (assigneeMetricBulkSelection.getDateEnd() == null) {
			map.put("dateEnd", null);
		}
		else {
			map.put(
				"dateEnd",
				liferayToJSONDateFormat.format(
					assigneeMetricBulkSelection.getDateEnd()));
		}

		if (assigneeMetricBulkSelection.getDateStart() == null) {
			map.put("dateStart", null);
		}
		else {
			map.put(
				"dateStart",
				liferayToJSONDateFormat.format(
					assigneeMetricBulkSelection.getDateStart()));
		}

		if (assigneeMetricBulkSelection.getInstanceIds() == null) {
			map.put("instanceIds", null);
		}
		else {
			map.put(
				"instanceIds",
				String.valueOf(assigneeMetricBulkSelection.getInstanceIds()));
		}

		if (assigneeMetricBulkSelection.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put(
				"keywords",
				String.valueOf(assigneeMetricBulkSelection.getKeywords()));
		}

		if (assigneeMetricBulkSelection.getRoleIds() == null) {
			map.put("roleIds", null);
		}
		else {
			map.put(
				"roleIds",
				String.valueOf(assigneeMetricBulkSelection.getRoleIds()));
		}

		if (assigneeMetricBulkSelection.getTaskNames() == null) {
			map.put("taskNames", null);
		}
		else {
			map.put(
				"taskNames",
				String.valueOf(assigneeMetricBulkSelection.getTaskNames()));
		}

		return map;
	}

	public static class AssigneeMetricBulkSelectionJSONParser
		extends BaseJSONParser<AssigneeMetricBulkSelection> {

		@Override
		protected AssigneeMetricBulkSelection createDTO() {
			return new AssigneeMetricBulkSelection();
		}

		@Override
		protected AssigneeMetricBulkSelection[] createDTOArray(int size) {
			return new AssigneeMetricBulkSelection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "completed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateEnd")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateStart")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "instanceIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "taskNames")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssigneeMetricBulkSelection assigneeMetricBulkSelection,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "completed")) {
				if (jsonParserFieldValue != null) {
					assigneeMetricBulkSelection.setCompleted(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateEnd")) {
				if (jsonParserFieldValue != null) {
					assigneeMetricBulkSelection.setDateEnd(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateStart")) {
				if (jsonParserFieldValue != null) {
					assigneeMetricBulkSelection.setDateStart(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "instanceIds")) {
				if (jsonParserFieldValue != null) {
					assigneeMetricBulkSelection.setInstanceIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					assigneeMetricBulkSelection.setKeywords(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleIds")) {
				if (jsonParserFieldValue != null) {
					assigneeMetricBulkSelection.setRoleIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "taskNames")) {
				if (jsonParserFieldValue != null) {
					assigneeMetricBulkSelection.setTaskNames(
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