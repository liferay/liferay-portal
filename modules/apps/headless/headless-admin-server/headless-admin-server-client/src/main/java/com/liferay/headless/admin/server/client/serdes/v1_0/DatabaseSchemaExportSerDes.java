/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.server.client.serdes.v1_0;

import com.liferay.headless.admin.server.client.dto.v1_0.DatabaseSchemaExport;
import com.liferay.headless.admin.server.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Luis Ortiz
 * @generated
 */
@Generated("")
public class DatabaseSchemaExportSerDes {

	public static DatabaseSchemaExport toDTO(String json) {
		DatabaseSchemaExportJSONParser databaseSchemaExportJSONParser =
			new DatabaseSchemaExportJSONParser();

		return databaseSchemaExportJSONParser.parseToDTO(json);
	}

	public static DatabaseSchemaExport[] toDTOs(String json) {
		DatabaseSchemaExportJSONParser databaseSchemaExportJSONParser =
			new DatabaseSchemaExportJSONParser();

		return databaseSchemaExportJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DatabaseSchemaExport databaseSchemaExport) {
		if (databaseSchemaExport == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (databaseSchemaExport.getExportFilesPath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"exportFilesPath\": ");

			sb.append("\"");

			sb.append(_escape(databaseSchemaExport.getExportFilesPath()));

			sb.append("\"");
		}

		if (databaseSchemaExport.getFileNames() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileNames\": ");

			sb.append("[");

			for (int i = 0; i < databaseSchemaExport.getFileNames().length;
				 i++) {

				sb.append(_toJSON(databaseSchemaExport.getFileNames()[i]));

				if ((i + 1) < databaseSchemaExport.getFileNames().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (databaseSchemaExport.getReportFileName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reportFileName\": ");

			sb.append("\"");

			sb.append(_escape(databaseSchemaExport.getReportFileName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DatabaseSchemaExportJSONParser databaseSchemaExportJSONParser =
			new DatabaseSchemaExportJSONParser();

		return databaseSchemaExportJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DatabaseSchemaExport databaseSchemaExport) {

		if (databaseSchemaExport == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (databaseSchemaExport.getExportFilesPath() == null) {
			map.put("exportFilesPath", null);
		}
		else {
			map.put(
				"exportFilesPath",
				String.valueOf(databaseSchemaExport.getExportFilesPath()));
		}

		if (databaseSchemaExport.getFileNames() == null) {
			map.put("fileNames", null);
		}
		else {
			map.put(
				"fileNames",
				String.valueOf(databaseSchemaExport.getFileNames()));
		}

		if (databaseSchemaExport.getReportFileName() == null) {
			map.put("reportFileName", null);
		}
		else {
			map.put(
				"reportFileName",
				String.valueOf(databaseSchemaExport.getReportFileName()));
		}

		return map;
	}

	public static class DatabaseSchemaExportJSONParser
		extends BaseJSONParser<DatabaseSchemaExport> {

		@Override
		protected DatabaseSchemaExport createDTO() {
			return new DatabaseSchemaExport();
		}

		@Override
		protected DatabaseSchemaExport[] createDTOArray(int size) {
			return new DatabaseSchemaExport[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "exportFilesPath")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fileNames")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "reportFileName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DatabaseSchemaExport databaseSchemaExport,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "exportFilesPath")) {
				if (jsonParserFieldValue != null) {
					databaseSchemaExport.setExportFilesPath(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileNames")) {
				if (jsonParserFieldValue != null) {
					databaseSchemaExport.setFileNames(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "reportFileName")) {
				if (jsonParserFieldValue != null) {
					databaseSchemaExport.setReportFileName(
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
// LIFERAY-REST-BUILDER-HASH:911659823