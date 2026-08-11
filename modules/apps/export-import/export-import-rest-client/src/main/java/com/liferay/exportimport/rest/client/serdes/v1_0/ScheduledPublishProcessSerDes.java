/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.ScheduledPublishProcess;
import com.liferay.exportimport.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class ScheduledPublishProcessSerDes {

	public static ScheduledPublishProcess toDTO(String json) {
		ScheduledPublishProcessJSONParser scheduledPublishProcessJSONParser =
			new ScheduledPublishProcessJSONParser();

		return scheduledPublishProcessJSONParser.parseToDTO(json);
	}

	public static ScheduledPublishProcess[] toDTOs(String json) {
		ScheduledPublishProcessJSONParser scheduledPublishProcessJSONParser =
			new ScheduledPublishProcessJSONParser();

		return scheduledPublishProcessJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ScheduledPublishProcess scheduledPublishProcess) {

		if (scheduledPublishProcess == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (scheduledPublishProcess.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(scheduledPublishProcess.getCreator());
		}

		if (scheduledPublishProcess.getCronExpression() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cronExpression\": ");

			sb.append("\"");

			sb.append(_escape(scheduledPublishProcess.getCronExpression()));

			sb.append("\"");
		}

		if (scheduledPublishProcess.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					scheduledPublishProcess.getDateCreated()));

			sb.append("\"");
		}

		if (scheduledPublishProcess.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(scheduledPublishProcess.getId());
		}

		if (scheduledPublishProcess.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(scheduledPublishProcess.getName()));

			sb.append("\"");
		}

		if (scheduledPublishProcess.getNextFireDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nextFireDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					scheduledPublishProcess.getNextFireDate()));

			sb.append("\"");
		}

		if (scheduledPublishProcess.getPublishParameters() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"publishParameters\": ");

			if (scheduledPublishProcess.getPublishParameters() instanceof
					String) {

				sb.append("\"");
				sb.append(
					(String)scheduledPublishProcess.getPublishParameters());
				sb.append("\"");
			}
			else {
				sb.append(scheduledPublishProcess.getPublishParameters());
			}
		}

		if (scheduledPublishProcess.getScheduleEndDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduleEndDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					scheduledPublishProcess.getScheduleEndDate()));

			sb.append("\"");
		}

		if (scheduledPublishProcess.getScheduleStartDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduleStartDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					scheduledPublishProcess.getScheduleStartDate()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ScheduledPublishProcessJSONParser scheduledPublishProcessJSONParser =
			new ScheduledPublishProcessJSONParser();

		return scheduledPublishProcessJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ScheduledPublishProcess scheduledPublishProcess) {

		if (scheduledPublishProcess == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (scheduledPublishProcess.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator",
				String.valueOf(scheduledPublishProcess.getCreator()));
		}

		if (scheduledPublishProcess.getCronExpression() == null) {
			map.put("cronExpression", null);
		}
		else {
			map.put(
				"cronExpression",
				String.valueOf(scheduledPublishProcess.getCronExpression()));
		}

		if (scheduledPublishProcess.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					scheduledPublishProcess.getDateCreated()));
		}

		if (scheduledPublishProcess.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(scheduledPublishProcess.getId()));
		}

		if (scheduledPublishProcess.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(scheduledPublishProcess.getName()));
		}

		if (scheduledPublishProcess.getNextFireDate() == null) {
			map.put("nextFireDate", null);
		}
		else {
			map.put(
				"nextFireDate",
				liferayToJSONDateFormat.format(
					scheduledPublishProcess.getNextFireDate()));
		}

		if (scheduledPublishProcess.getPublishParameters() == null) {
			map.put("publishParameters", null);
		}
		else {
			map.put(
				"publishParameters",
				String.valueOf(scheduledPublishProcess.getPublishParameters()));
		}

		if (scheduledPublishProcess.getScheduleEndDate() == null) {
			map.put("scheduleEndDate", null);
		}
		else {
			map.put(
				"scheduleEndDate",
				liferayToJSONDateFormat.format(
					scheduledPublishProcess.getScheduleEndDate()));
		}

		if (scheduledPublishProcess.getScheduleStartDate() == null) {
			map.put("scheduleStartDate", null);
		}
		else {
			map.put(
				"scheduleStartDate",
				liferayToJSONDateFormat.format(
					scheduledPublishProcess.getScheduleStartDate()));
		}

		return map;
	}

	public static class ScheduledPublishProcessJSONParser
		extends BaseJSONParser<ScheduledPublishProcess> {

		@Override
		protected ScheduledPublishProcess createDTO() {
			return new ScheduledPublishProcess();
		}

		@Override
		protected ScheduledPublishProcess[] createDTOArray(int size) {
			return new ScheduledPublishProcess[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cronExpression")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "nextFireDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "publishParameters")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleEndDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleStartDate")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ScheduledPublishProcess scheduledPublishProcess,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcess.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cronExpression")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcess.setCronExpression(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcess.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcess.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcess.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "nextFireDate")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcess.setNextFireDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "publishParameters")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcess.setPublishParameters(
						(Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleEndDate")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcess.setScheduleEndDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleStartDate")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcess.setScheduleStartDate(
						toDate((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:-1419210200