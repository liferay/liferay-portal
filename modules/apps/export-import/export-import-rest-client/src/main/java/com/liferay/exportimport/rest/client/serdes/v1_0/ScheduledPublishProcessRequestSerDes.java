/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.ScheduledPublishProcessRequest;
import com.liferay.exportimport.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
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
public class ScheduledPublishProcessRequestSerDes {

	public static ScheduledPublishProcessRequest toDTO(String json) {
		ScheduledPublishProcessRequestJSONParser
			scheduledPublishProcessRequestJSONParser =
				new ScheduledPublishProcessRequestJSONParser();

		return scheduledPublishProcessRequestJSONParser.parseToDTO(json);
	}

	public static ScheduledPublishProcessRequest[] toDTOs(String json) {
		ScheduledPublishProcessRequestJSONParser
			scheduledPublishProcessRequestJSONParser =
				new ScheduledPublishProcessRequestJSONParser();

		return scheduledPublishProcessRequestJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ScheduledPublishProcessRequest scheduledPublishProcessRequest) {

		if (scheduledPublishProcessRequest == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (scheduledPublishProcessRequest.getCronExpression() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cronExpression\": ");

			sb.append("\"");

			sb.append(
				_escape(scheduledPublishProcessRequest.getCronExpression()));

			sb.append("\"");
		}

		if (scheduledPublishProcessRequest.getPublishProcessRequest() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"publishProcessRequest\": ");

			sb.append(
				String.valueOf(
					scheduledPublishProcessRequest.getPublishProcessRequest()));
		}

		if (scheduledPublishProcessRequest.getScheduleEndDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduleEndDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					scheduledPublishProcessRequest.getScheduleEndDate()));

			sb.append("\"");
		}

		if (scheduledPublishProcessRequest.getScheduleStartDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduleStartDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					scheduledPublishProcessRequest.getScheduleStartDate()));

			sb.append("\"");
		}

		if (scheduledPublishProcessRequest.getTimeZoneId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"timeZoneId\": ");

			sb.append("\"");

			sb.append(_escape(scheduledPublishProcessRequest.getTimeZoneId()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ScheduledPublishProcessRequestJSONParser
			scheduledPublishProcessRequestJSONParser =
				new ScheduledPublishProcessRequestJSONParser();

		return scheduledPublishProcessRequestJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ScheduledPublishProcessRequest scheduledPublishProcessRequest) {

		if (scheduledPublishProcessRequest == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (scheduledPublishProcessRequest.getCronExpression() == null) {
			map.put("cronExpression", null);
		}
		else {
			map.put(
				"cronExpression",
				String.valueOf(
					scheduledPublishProcessRequest.getCronExpression()));
		}

		if (scheduledPublishProcessRequest.getPublishProcessRequest() == null) {
			map.put("publishProcessRequest", null);
		}
		else {
			map.put(
				"publishProcessRequest",
				String.valueOf(
					scheduledPublishProcessRequest.getPublishProcessRequest()));
		}

		if (scheduledPublishProcessRequest.getScheduleEndDate() == null) {
			map.put("scheduleEndDate", null);
		}
		else {
			map.put(
				"scheduleEndDate",
				liferayToJSONDateFormat.format(
					scheduledPublishProcessRequest.getScheduleEndDate()));
		}

		if (scheduledPublishProcessRequest.getScheduleStartDate() == null) {
			map.put("scheduleStartDate", null);
		}
		else {
			map.put(
				"scheduleStartDate",
				liferayToJSONDateFormat.format(
					scheduledPublishProcessRequest.getScheduleStartDate()));
		}

		if (scheduledPublishProcessRequest.getTimeZoneId() == null) {
			map.put("timeZoneId", null);
		}
		else {
			map.put(
				"timeZoneId",
				String.valueOf(scheduledPublishProcessRequest.getTimeZoneId()));
		}

		return map;
	}

	public static class ScheduledPublishProcessRequestJSONParser
		extends BaseJSONParser<ScheduledPublishProcessRequest> {

		@Override
		protected ScheduledPublishProcessRequest createDTO() {
			return new ScheduledPublishProcessRequest();
		}

		@Override
		protected ScheduledPublishProcessRequest[] createDTOArray(int size) {
			return new ScheduledPublishProcessRequest[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "cronExpression")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "publishProcessRequest")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleEndDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleStartDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "timeZoneId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ScheduledPublishProcessRequest scheduledPublishProcessRequest,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "cronExpression")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcessRequest.setCronExpression(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "publishProcessRequest")) {

				if (jsonParserFieldValue != null) {
					scheduledPublishProcessRequest.setPublishProcessRequest(
						PublishProcessRequestSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleEndDate")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcessRequest.setScheduleEndDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleStartDate")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcessRequest.setScheduleStartDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "timeZoneId")) {
				if (jsonParserFieldValue != null) {
					scheduledPublishProcessRequest.setTimeZoneId(
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:1772722634