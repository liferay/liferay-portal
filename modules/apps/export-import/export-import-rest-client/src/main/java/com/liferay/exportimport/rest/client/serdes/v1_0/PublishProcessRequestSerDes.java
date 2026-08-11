/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PublishProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.RequestPortletDataHandler;
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
public class PublishProcessRequestSerDes {

	public static PublishProcessRequest toDTO(String json) {
		PublishProcessRequestJSONParser publishProcessRequestJSONParser =
			new PublishProcessRequestJSONParser();

		return publishProcessRequestJSONParser.parseToDTO(json);
	}

	public static PublishProcessRequest[] toDTOs(String json) {
		PublishProcessRequestJSONParser publishProcessRequestJSONParser =
			new PublishProcessRequestJSONParser();

		return publishProcessRequestJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PublishProcessRequest publishProcessRequest) {
		if (publishProcessRequest == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (publishProcessRequest.getComments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append(publishProcessRequest.getComments());
		}

		if (publishProcessRequest.getCronExpression() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cronExpression\": ");

			sb.append("\"");

			sb.append(_escape(publishProcessRequest.getCronExpression()));

			sb.append("\"");
		}

		if (publishProcessRequest.getDateRangeType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateRangeType\": ");

			sb.append("\"");
			sb.append(publishProcessRequest.getDateRangeType());
			sb.append("\"");
		}

		if (publishProcessRequest.getDeletions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletions\": ");

			sb.append(publishProcessRequest.getDeletions());
		}

		if (publishProcessRequest.getEndDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					publishProcessRequest.getEndDate()));

			sb.append("\"");
		}

		if (publishProcessRequest.getLogo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logo\": ");

			sb.append(publishProcessRequest.getLogo());
		}

		if (publishProcessRequest.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(publishProcessRequest.getName()));

			sb.append("\"");
		}

		if (publishProcessRequest.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append(publishProcessRequest.getPermissions());
		}

		if (publishProcessRequest.getRatings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratings\": ");

			sb.append(publishProcessRequest.getRatings());
		}

		if (publishProcessRequest.getRequestPortletDataHandlers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestPortletDataHandlers\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 publishProcessRequest.
						 getRequestPortletDataHandlers().length;
				 i++) {

				sb.append(
					String.valueOf(
						publishProcessRequest.getRequestPortletDataHandlers()
							[i]));

				if ((i + 1) < publishProcessRequest.
						getRequestPortletDataHandlers().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (publishProcessRequest.getScheduleEndDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduleEndDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					publishProcessRequest.getScheduleEndDate()));

			sb.append("\"");
		}

		if (publishProcessRequest.getScheduleStartDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduleStartDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					publishProcessRequest.getScheduleStartDate()));

			sb.append("\"");
		}

		if (publishProcessRequest.getSitePagesSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitePagesSettings\": ");

			sb.append(publishProcessRequest.getSitePagesSettings());
		}

		if (publishProcessRequest.getSiteTemplateSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteTemplateSettings\": ");

			sb.append(publishProcessRequest.getSiteTemplateSettings());
		}

		if (publishProcessRequest.getStartDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					publishProcessRequest.getStartDate()));

			sb.append("\"");
		}

		if (publishProcessRequest.getThemeSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeSettings\": ");

			sb.append(publishProcessRequest.getThemeSettings());
		}

		if (publishProcessRequest.getTimeZoneId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"timeZoneId\": ");

			sb.append("\"");

			sb.append(_escape(publishProcessRequest.getTimeZoneId()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PublishProcessRequestJSONParser publishProcessRequestJSONParser =
			new PublishProcessRequestJSONParser();

		return publishProcessRequestJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PublishProcessRequest publishProcessRequest) {

		if (publishProcessRequest == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (publishProcessRequest.getComments() == null) {
			map.put("comments", null);
		}
		else {
			map.put(
				"comments",
				String.valueOf(publishProcessRequest.getComments()));
		}

		if (publishProcessRequest.getCronExpression() == null) {
			map.put("cronExpression", null);
		}
		else {
			map.put(
				"cronExpression",
				String.valueOf(publishProcessRequest.getCronExpression()));
		}

		if (publishProcessRequest.getDateRangeType() == null) {
			map.put("dateRangeType", null);
		}
		else {
			map.put(
				"dateRangeType",
				String.valueOf(publishProcessRequest.getDateRangeType()));
		}

		if (publishProcessRequest.getDeletions() == null) {
			map.put("deletions", null);
		}
		else {
			map.put(
				"deletions",
				String.valueOf(publishProcessRequest.getDeletions()));
		}

		if (publishProcessRequest.getEndDate() == null) {
			map.put("endDate", null);
		}
		else {
			map.put(
				"endDate",
				liferayToJSONDateFormat.format(
					publishProcessRequest.getEndDate()));
		}

		if (publishProcessRequest.getLogo() == null) {
			map.put("logo", null);
		}
		else {
			map.put("logo", String.valueOf(publishProcessRequest.getLogo()));
		}

		if (publishProcessRequest.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(publishProcessRequest.getName()));
		}

		if (publishProcessRequest.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(publishProcessRequest.getPermissions()));
		}

		if (publishProcessRequest.getRatings() == null) {
			map.put("ratings", null);
		}
		else {
			map.put(
				"ratings", String.valueOf(publishProcessRequest.getRatings()));
		}

		if (publishProcessRequest.getRequestPortletDataHandlers() == null) {
			map.put("requestPortletDataHandlers", null);
		}
		else {
			map.put(
				"requestPortletDataHandlers",
				String.valueOf(
					publishProcessRequest.getRequestPortletDataHandlers()));
		}

		if (publishProcessRequest.getScheduleEndDate() == null) {
			map.put("scheduleEndDate", null);
		}
		else {
			map.put(
				"scheduleEndDate",
				liferayToJSONDateFormat.format(
					publishProcessRequest.getScheduleEndDate()));
		}

		if (publishProcessRequest.getScheduleStartDate() == null) {
			map.put("scheduleStartDate", null);
		}
		else {
			map.put(
				"scheduleStartDate",
				liferayToJSONDateFormat.format(
					publishProcessRequest.getScheduleStartDate()));
		}

		if (publishProcessRequest.getSitePagesSettings() == null) {
			map.put("sitePagesSettings", null);
		}
		else {
			map.put(
				"sitePagesSettings",
				String.valueOf(publishProcessRequest.getSitePagesSettings()));
		}

		if (publishProcessRequest.getSiteTemplateSettings() == null) {
			map.put("siteTemplateSettings", null);
		}
		else {
			map.put(
				"siteTemplateSettings",
				String.valueOf(
					publishProcessRequest.getSiteTemplateSettings()));
		}

		if (publishProcessRequest.getStartDate() == null) {
			map.put("startDate", null);
		}
		else {
			map.put(
				"startDate",
				liferayToJSONDateFormat.format(
					publishProcessRequest.getStartDate()));
		}

		if (publishProcessRequest.getThemeSettings() == null) {
			map.put("themeSettings", null);
		}
		else {
			map.put(
				"themeSettings",
				String.valueOf(publishProcessRequest.getThemeSettings()));
		}

		if (publishProcessRequest.getTimeZoneId() == null) {
			map.put("timeZoneId", null);
		}
		else {
			map.put(
				"timeZoneId",
				String.valueOf(publishProcessRequest.getTimeZoneId()));
		}

		return map;
	}

	public static class PublishProcessRequestJSONParser
		extends BaseJSONParser<PublishProcessRequest> {

		@Override
		protected PublishProcessRequest createDTO() {
			return new PublishProcessRequest();
		}

		@Override
		protected PublishProcessRequest[] createDTOArray(int size) {
			return new PublishProcessRequest[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "comments")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cronExpression")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateRangeType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deletions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "logo")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ratings")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "requestPortletDataHandlers")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleEndDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleStartDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sitePagesSettings")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteTemplateSettings")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "themeSettings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "timeZoneId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PublishProcessRequest publishProcessRequest,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "comments")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setComments(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cronExpression")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setCronExpression(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateRangeType")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setDateRangeType(
						PublishProcessRequest.DateRangeType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletions")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setDeletions(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setEndDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "logo")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setLogo(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setPermissions(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ratings")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setRatings(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "requestPortletDataHandlers")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					RequestPortletDataHandler[]
						requestPortletDataHandlersArray =
							new RequestPortletDataHandler
								[jsonParserFieldValues.length];

					for (int i = 0; i < requestPortletDataHandlersArray.length;
						 i++) {

						requestPortletDataHandlersArray[i] =
							RequestPortletDataHandlerSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					publishProcessRequest.setRequestPortletDataHandlers(
						requestPortletDataHandlersArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleEndDate")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setScheduleEndDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scheduleStartDate")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setScheduleStartDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sitePagesSettings")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setSitePagesSettings(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteTemplateSettings")) {

				if (jsonParserFieldValue != null) {
					publishProcessRequest.setSiteTemplateSettings(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setStartDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "themeSettings")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setThemeSettings(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "timeZoneId")) {
				if (jsonParserFieldValue != null) {
					publishProcessRequest.setTimeZoneId(
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
// LIFERAY-REST-BUILDER-HASH:1849791204