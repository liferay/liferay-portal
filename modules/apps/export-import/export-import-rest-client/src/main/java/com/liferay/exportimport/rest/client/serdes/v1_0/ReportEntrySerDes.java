/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.ReportEntry;
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
public class ReportEntrySerDes {

	public static ReportEntry toDTO(String json) {
		ReportEntryJSONParser reportEntryJSONParser =
			new ReportEntryJSONParser();

		return reportEntryJSONParser.parseToDTO(json);
	}

	public static ReportEntry[] toDTOs(String json) {
		ReportEntryJSONParser reportEntryJSONParser =
			new ReportEntryJSONParser();

		return reportEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ReportEntry reportEntry) {
		if (reportEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (reportEntry.getClassExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(reportEntry.getClassExternalReferenceCode()));

			sb.append("\"");
		}

		if (reportEntry.getClassNameId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classNameId\": ");

			sb.append(reportEntry.getClassNameId());
		}

		if (reportEntry.getClassPK() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classPK\": ");

			sb.append(reportEntry.getClassPK());
		}

		if (reportEntry.getConfigurationId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configurationId\": ");

			sb.append(reportEntry.getConfigurationId());
		}

		if (reportEntry.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(reportEntry.getCreator());
		}

		if (reportEntry.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(reportEntry.getDateCreated()));

			sb.append("\"");
		}

		if (reportEntry.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(reportEntry.getDateModified()));

			sb.append("\"");
		}

		if (reportEntry.getErrorMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append("\"");

			sb.append(_escape(reportEntry.getErrorMessage()));

			sb.append("\"");
		}

		if (reportEntry.getErrorStacktrace() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorStacktrace\": ");

			sb.append("\"");

			sb.append(_escape(reportEntry.getErrorStacktrace()));

			sb.append("\"");
		}

		if (reportEntry.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(reportEntry.getId());
		}

		if (reportEntry.getModelName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modelName\": ");

			sb.append("\"");

			sb.append(_escape(reportEntry.getModelName()));

			sb.append("\"");
		}

		if (reportEntry.getOrigin() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"origin\": ");

			sb.append(String.valueOf(reportEntry.getOrigin()));
		}

		if (reportEntry.getScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append(String.valueOf(reportEntry.getScope()));
		}

		if (reportEntry.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(reportEntry.getStatus()));
		}

		if (reportEntry.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(String.valueOf(reportEntry.getType()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ReportEntryJSONParser reportEntryJSONParser =
			new ReportEntryJSONParser();

		return reportEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ReportEntry reportEntry) {
		if (reportEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (reportEntry.getClassExternalReferenceCode() == null) {
			map.put("classExternalReferenceCode", null);
		}
		else {
			map.put(
				"classExternalReferenceCode",
				String.valueOf(reportEntry.getClassExternalReferenceCode()));
		}

		if (reportEntry.getClassNameId() == null) {
			map.put("classNameId", null);
		}
		else {
			map.put(
				"classNameId", String.valueOf(reportEntry.getClassNameId()));
		}

		if (reportEntry.getClassPK() == null) {
			map.put("classPK", null);
		}
		else {
			map.put("classPK", String.valueOf(reportEntry.getClassPK()));
		}

		if (reportEntry.getConfigurationId() == null) {
			map.put("configurationId", null);
		}
		else {
			map.put(
				"configurationId",
				String.valueOf(reportEntry.getConfigurationId()));
		}

		if (reportEntry.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(reportEntry.getCreator()));
		}

		if (reportEntry.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(reportEntry.getDateCreated()));
		}

		if (reportEntry.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(reportEntry.getDateModified()));
		}

		if (reportEntry.getErrorMessage() == null) {
			map.put("errorMessage", null);
		}
		else {
			map.put(
				"errorMessage", String.valueOf(reportEntry.getErrorMessage()));
		}

		if (reportEntry.getErrorStacktrace() == null) {
			map.put("errorStacktrace", null);
		}
		else {
			map.put(
				"errorStacktrace",
				String.valueOf(reportEntry.getErrorStacktrace()));
		}

		if (reportEntry.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(reportEntry.getId()));
		}

		if (reportEntry.getModelName() == null) {
			map.put("modelName", null);
		}
		else {
			map.put("modelName", String.valueOf(reportEntry.getModelName()));
		}

		if (reportEntry.getOrigin() == null) {
			map.put("origin", null);
		}
		else {
			map.put("origin", String.valueOf(reportEntry.getOrigin()));
		}

		if (reportEntry.getScope() == null) {
			map.put("scope", null);
		}
		else {
			map.put("scope", String.valueOf(reportEntry.getScope()));
		}

		if (reportEntry.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(reportEntry.getStatus()));
		}

		if (reportEntry.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(reportEntry.getType()));
		}

		return map;
	}

	public static class ReportEntryJSONParser
		extends BaseJSONParser<ReportEntry> {

		@Override
		protected ReportEntry createDTO() {
			return new ReportEntry();
		}

		@Override
		protected ReportEntry[] createDTOArray(int size) {
			return new ReportEntry[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "classExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "classNameId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "configurationId")) {
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
			else if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "errorStacktrace")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modelName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "origin")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ReportEntry reportEntry, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "classExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					reportEntry.setClassExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "classNameId")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setClassNameId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setClassPK(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "configurationId")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setConfigurationId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setErrorMessage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorStacktrace")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setErrorStacktrace(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modelName")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setModelName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "origin")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setOrigin(
						OriginSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setScope(
						ScopeSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setStatus(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					reportEntry.setType(
						TypeSerDes.toDTO((String)jsonParserFieldValue));
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