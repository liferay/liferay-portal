/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.rest.client.serdes.v1_0;

import com.liferay.dispatch.rest.client.dto.v1_0.DispatchTrigger;
import com.liferay.dispatch.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Nilton Vieira
 * @generated
 */
@Generated("")
public class DispatchTriggerSerDes {

	public static DispatchTrigger toDTO(String json) {
		DispatchTriggerJSONParser dispatchTriggerJSONParser =
			new DispatchTriggerJSONParser();

		return dispatchTriggerJSONParser.parseToDTO(json);
	}

	public static DispatchTrigger[] toDTOs(String json) {
		DispatchTriggerJSONParser dispatchTriggerJSONParser =
			new DispatchTriggerJSONParser();

		return dispatchTriggerJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DispatchTrigger dispatchTrigger) {
		if (dispatchTrigger == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (dispatchTrigger.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(dispatchTrigger.getActive());
		}

		if (dispatchTrigger.getCompanyId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"companyId\": ");

			sb.append(dispatchTrigger.getCompanyId());
		}

		if (dispatchTrigger.getCronExpression() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cronExpression\": ");

			sb.append("\"");

			sb.append(_escape(dispatchTrigger.getCronExpression()));

			sb.append("\"");
		}

		if (dispatchTrigger.getDispatchTaskClusterMode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dispatchTaskClusterMode\": ");

			sb.append(dispatchTrigger.getDispatchTaskClusterMode());
		}

		if (dispatchTrigger.getDispatchTaskExecutorType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dispatchTaskExecutorType\": ");

			sb.append("\"");

			sb.append(_escape(dispatchTrigger.getDispatchTaskExecutorType()));

			sb.append("\"");
		}

		if (dispatchTrigger.getDispatchTaskSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dispatchTaskSettings\": ");

			sb.append(_toJSON(dispatchTrigger.getDispatchTaskSettings()));
		}

		if (dispatchTrigger.getEndDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(dispatchTrigger.getEndDate()));

			sb.append("\"");
		}

		if (dispatchTrigger.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(dispatchTrigger.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (dispatchTrigger.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(dispatchTrigger.getId());
		}

		if (dispatchTrigger.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(dispatchTrigger.getName()));

			sb.append("\"");
		}

		if (dispatchTrigger.getOverlapAllowed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"overlapAllowed\": ");

			sb.append(dispatchTrigger.getOverlapAllowed());
		}

		if (dispatchTrigger.getStartDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(dispatchTrigger.getStartDate()));

			sb.append("\"");
		}

		if (dispatchTrigger.getSystem() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(dispatchTrigger.getSystem());
		}

		if (dispatchTrigger.getTimeZoneId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"timeZoneId\": ");

			sb.append("\"");

			sb.append(_escape(dispatchTrigger.getTimeZoneId()));

			sb.append("\"");
		}

		if (dispatchTrigger.getUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(dispatchTrigger.getUserId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DispatchTriggerJSONParser dispatchTriggerJSONParser =
			new DispatchTriggerJSONParser();

		return dispatchTriggerJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DispatchTrigger dispatchTrigger) {
		if (dispatchTrigger == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (dispatchTrigger.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(dispatchTrigger.getActive()));
		}

		if (dispatchTrigger.getCompanyId() == null) {
			map.put("companyId", null);
		}
		else {
			map.put(
				"companyId", String.valueOf(dispatchTrigger.getCompanyId()));
		}

		if (dispatchTrigger.getCronExpression() == null) {
			map.put("cronExpression", null);
		}
		else {
			map.put(
				"cronExpression",
				String.valueOf(dispatchTrigger.getCronExpression()));
		}

		if (dispatchTrigger.getDispatchTaskClusterMode() == null) {
			map.put("dispatchTaskClusterMode", null);
		}
		else {
			map.put(
				"dispatchTaskClusterMode",
				String.valueOf(dispatchTrigger.getDispatchTaskClusterMode()));
		}

		if (dispatchTrigger.getDispatchTaskExecutorType() == null) {
			map.put("dispatchTaskExecutorType", null);
		}
		else {
			map.put(
				"dispatchTaskExecutorType",
				String.valueOf(dispatchTrigger.getDispatchTaskExecutorType()));
		}

		if (dispatchTrigger.getDispatchTaskSettings() == null) {
			map.put("dispatchTaskSettings", null);
		}
		else {
			map.put(
				"dispatchTaskSettings",
				String.valueOf(dispatchTrigger.getDispatchTaskSettings()));
		}

		if (dispatchTrigger.getEndDate() == null) {
			map.put("endDate", null);
		}
		else {
			map.put(
				"endDate",
				liferayToJSONDateFormat.format(dispatchTrigger.getEndDate()));
		}

		if (dispatchTrigger.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(dispatchTrigger.getExternalReferenceCode()));
		}

		if (dispatchTrigger.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(dispatchTrigger.getId()));
		}

		if (dispatchTrigger.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(dispatchTrigger.getName()));
		}

		if (dispatchTrigger.getOverlapAllowed() == null) {
			map.put("overlapAllowed", null);
		}
		else {
			map.put(
				"overlapAllowed",
				String.valueOf(dispatchTrigger.getOverlapAllowed()));
		}

		if (dispatchTrigger.getStartDate() == null) {
			map.put("startDate", null);
		}
		else {
			map.put(
				"startDate",
				liferayToJSONDateFormat.format(dispatchTrigger.getStartDate()));
		}

		if (dispatchTrigger.getSystem() == null) {
			map.put("system", null);
		}
		else {
			map.put("system", String.valueOf(dispatchTrigger.getSystem()));
		}

		if (dispatchTrigger.getTimeZoneId() == null) {
			map.put("timeZoneId", null);
		}
		else {
			map.put(
				"timeZoneId", String.valueOf(dispatchTrigger.getTimeZoneId()));
		}

		if (dispatchTrigger.getUserId() == null) {
			map.put("userId", null);
		}
		else {
			map.put("userId", String.valueOf(dispatchTrigger.getUserId()));
		}

		return map;
	}

	public static class DispatchTriggerJSONParser
		extends BaseJSONParser<DispatchTrigger> {

		@Override
		protected DispatchTrigger createDTO() {
			return new DispatchTrigger();
		}

		@Override
		protected DispatchTrigger[] createDTOArray(int size) {
			return new DispatchTrigger[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "companyId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cronExpression")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "dispatchTaskClusterMode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "dispatchTaskExecutorType")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "dispatchTaskSettings")) {

				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
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
			else if (Objects.equals(jsonParserFieldName, "overlapAllowed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "timeZoneId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DispatchTrigger dispatchTrigger, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					dispatchTrigger.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "companyId")) {
				if (jsonParserFieldValue != null) {
					dispatchTrigger.setCompanyId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cronExpression")) {
				if (jsonParserFieldValue != null) {
					dispatchTrigger.setCronExpression(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "dispatchTaskClusterMode")) {

				if (jsonParserFieldValue != null) {
					dispatchTrigger.setDispatchTaskClusterMode(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "dispatchTaskExecutorType")) {

				if (jsonParserFieldValue != null) {
					dispatchTrigger.setDispatchTaskExecutorType(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "dispatchTaskSettings")) {

				if (jsonParserFieldValue != null) {
					dispatchTrigger.setDispatchTaskSettings(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				if (jsonParserFieldValue != null) {
					dispatchTrigger.setEndDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					dispatchTrigger.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					dispatchTrigger.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					dispatchTrigger.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "overlapAllowed")) {
				if (jsonParserFieldValue != null) {
					dispatchTrigger.setOverlapAllowed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				if (jsonParserFieldValue != null) {
					dispatchTrigger.setStartDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				if (jsonParserFieldValue != null) {
					dispatchTrigger.setSystem((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "timeZoneId")) {
				if (jsonParserFieldValue != null) {
					dispatchTrigger.setTimeZoneId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				if (jsonParserFieldValue != null) {
					dispatchTrigger.setUserId(
						Long.valueOf((String)jsonParserFieldValue));
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