/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.ExportProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.RequestPortletDataHandler;
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
public class ExportProcessRequestSerDes {

	public static ExportProcessRequest toDTO(String json) {
		ExportProcessRequestJSONParser exportProcessRequestJSONParser =
			new ExportProcessRequestJSONParser();

		return exportProcessRequestJSONParser.parseToDTO(json);
	}

	public static ExportProcessRequest[] toDTOs(String json) {
		ExportProcessRequestJSONParser exportProcessRequestJSONParser =
			new ExportProcessRequestJSONParser();

		return exportProcessRequestJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ExportProcessRequest exportProcessRequest) {
		if (exportProcessRequest == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (exportProcessRequest.getComments() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append(exportProcessRequest.getComments());
		}

		if (exportProcessRequest.getDateRangeType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateRangeType\": ");

			sb.append("\"");
			sb.append(exportProcessRequest.getDateRangeType());
			sb.append("\"");
		}

		if (exportProcessRequest.getDeletions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletions\": ");

			sb.append(exportProcessRequest.getDeletions());
		}

		if (exportProcessRequest.getEndDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					exportProcessRequest.getEndDate()));

			sb.append("\"");
		}

		if (exportProcessRequest.getLogo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logo\": ");

			sb.append(exportProcessRequest.getLogo());
		}

		if (exportProcessRequest.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(exportProcessRequest.getName()));

			sb.append("\"");
		}

		if (exportProcessRequest.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append(exportProcessRequest.getPermissions());
		}

		if (exportProcessRequest.getRatings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratings\": ");

			sb.append(exportProcessRequest.getRatings());
		}

		if (exportProcessRequest.getRequestPortletDataHandlers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestPortletDataHandlers\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 exportProcessRequest.
						 getRequestPortletDataHandlers().length;
				 i++) {

				sb.append(
					String.valueOf(
						exportProcessRequest.getRequestPortletDataHandlers()
							[i]));

				if ((i + 1) < exportProcessRequest.
						getRequestPortletDataHandlers().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (exportProcessRequest.getSitePagesSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitePagesSettings\": ");

			sb.append(exportProcessRequest.getSitePagesSettings());
		}

		if (exportProcessRequest.getSiteTemplateSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteTemplateSettings\": ");

			sb.append(exportProcessRequest.getSiteTemplateSettings());
		}

		if (exportProcessRequest.getStartDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					exportProcessRequest.getStartDate()));

			sb.append("\"");
		}

		if (exportProcessRequest.getThemeSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeSettings\": ");

			sb.append(exportProcessRequest.getThemeSettings());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ExportProcessRequestJSONParser exportProcessRequestJSONParser =
			new ExportProcessRequestJSONParser();

		return exportProcessRequestJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ExportProcessRequest exportProcessRequest) {

		if (exportProcessRequest == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (exportProcessRequest.getComments() == null) {
			map.put("comments", null);
		}
		else {
			map.put(
				"comments", String.valueOf(exportProcessRequest.getComments()));
		}

		if (exportProcessRequest.getDateRangeType() == null) {
			map.put("dateRangeType", null);
		}
		else {
			map.put(
				"dateRangeType",
				String.valueOf(exportProcessRequest.getDateRangeType()));
		}

		if (exportProcessRequest.getDeletions() == null) {
			map.put("deletions", null);
		}
		else {
			map.put(
				"deletions",
				String.valueOf(exportProcessRequest.getDeletions()));
		}

		if (exportProcessRequest.getEndDate() == null) {
			map.put("endDate", null);
		}
		else {
			map.put(
				"endDate",
				liferayToJSONDateFormat.format(
					exportProcessRequest.getEndDate()));
		}

		if (exportProcessRequest.getLogo() == null) {
			map.put("logo", null);
		}
		else {
			map.put("logo", String.valueOf(exportProcessRequest.getLogo()));
		}

		if (exportProcessRequest.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(exportProcessRequest.getName()));
		}

		if (exportProcessRequest.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(exportProcessRequest.getPermissions()));
		}

		if (exportProcessRequest.getRatings() == null) {
			map.put("ratings", null);
		}
		else {
			map.put(
				"ratings", String.valueOf(exportProcessRequest.getRatings()));
		}

		if (exportProcessRequest.getRequestPortletDataHandlers() == null) {
			map.put("requestPortletDataHandlers", null);
		}
		else {
			map.put(
				"requestPortletDataHandlers",
				String.valueOf(
					exportProcessRequest.getRequestPortletDataHandlers()));
		}

		if (exportProcessRequest.getSitePagesSettings() == null) {
			map.put("sitePagesSettings", null);
		}
		else {
			map.put(
				"sitePagesSettings",
				String.valueOf(exportProcessRequest.getSitePagesSettings()));
		}

		if (exportProcessRequest.getSiteTemplateSettings() == null) {
			map.put("siteTemplateSettings", null);
		}
		else {
			map.put(
				"siteTemplateSettings",
				String.valueOf(exportProcessRequest.getSiteTemplateSettings()));
		}

		if (exportProcessRequest.getStartDate() == null) {
			map.put("startDate", null);
		}
		else {
			map.put(
				"startDate",
				liferayToJSONDateFormat.format(
					exportProcessRequest.getStartDate()));
		}

		if (exportProcessRequest.getThemeSettings() == null) {
			map.put("themeSettings", null);
		}
		else {
			map.put(
				"themeSettings",
				String.valueOf(exportProcessRequest.getThemeSettings()));
		}

		return map;
	}

	public static class ExportProcessRequestJSONParser
		extends BaseJSONParser<ExportProcessRequest> {

		@Override
		protected ExportProcessRequest createDTO() {
			return new ExportProcessRequest();
		}

		@Override
		protected ExportProcessRequest[] createDTOArray(int size) {
			return new ExportProcessRequest[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "comments")) {
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

			return false;
		}

		@Override
		protected void setField(
			ExportProcessRequest exportProcessRequest,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "comments")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setComments(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateRangeType")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setDateRangeType(
						ExportProcessRequest.DateRangeType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletions")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setDeletions(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setEndDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "logo")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setLogo((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setPermissions(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ratings")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setRatings(
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

					exportProcessRequest.setRequestPortletDataHandlers(
						requestPortletDataHandlersArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sitePagesSettings")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setSitePagesSettings(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteTemplateSettings")) {

				if (jsonParserFieldValue != null) {
					exportProcessRequest.setSiteTemplateSettings(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setStartDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "themeSettings")) {
				if (jsonParserFieldValue != null) {
					exportProcessRequest.setThemeSettings(
						(Boolean)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-124635644