/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.client.serdes.v1_0;

import com.liferay.headless.batch.engine.client.dto.v1_0.ExportTask;
import com.liferay.headless.batch.engine.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Ivica Cardic
 * @generated
 */
@Generated("")
public class ExportTaskSerDes {

	public static ExportTask toDTO(String json) {
		ExportTaskJSONParser exportTaskJSONParser = new ExportTaskJSONParser();

		return exportTaskJSONParser.parseToDTO(json);
	}

	public static ExportTask[] toDTOs(String json) {
		ExportTaskJSONParser exportTaskJSONParser = new ExportTaskJSONParser();

		return exportTaskJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ExportTask exportTask) {
		if (exportTask == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (exportTask.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(exportTask.getClassName()));

			sb.append("\"");
		}

		if (exportTask.getContentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(exportTask.getContentType()));

			sb.append("\"");
		}

		if (exportTask.getEndTime() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endTime\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(exportTask.getEndTime()));

			sb.append("\"");
		}

		if (exportTask.getErrorMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append("\"");

			sb.append(_escape(exportTask.getErrorMessage()));

			sb.append("\"");
		}

		if (exportTask.getExecuteStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"executeStatus\": ");

			sb.append("\"");

			sb.append(exportTask.getExecuteStatus());

			sb.append("\"");
		}

		if (exportTask.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(exportTask.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (exportTask.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(exportTask.getId());
		}

		if (exportTask.getProcessedItemsCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"processedItemsCount\": ");

			sb.append(exportTask.getProcessedItemsCount());
		}

		if (exportTask.getStartTime() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startTime\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(exportTask.getStartTime()));

			sb.append("\"");
		}

		if (exportTask.getTotalItemsCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalItemsCount\": ");

			sb.append(exportTask.getTotalItemsCount());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ExportTaskJSONParser exportTaskJSONParser = new ExportTaskJSONParser();

		return exportTaskJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ExportTask exportTask) {
		if (exportTask == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (exportTask.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put("className", String.valueOf(exportTask.getClassName()));
		}

		if (exportTask.getContentType() == null) {
			map.put("contentType", null);
		}
		else {
			map.put("contentType", String.valueOf(exportTask.getContentType()));
		}

		if (exportTask.getEndTime() == null) {
			map.put("endTime", null);
		}
		else {
			map.put(
				"endTime",
				liferayToJSONDateFormat.format(exportTask.getEndTime()));
		}

		if (exportTask.getErrorMessage() == null) {
			map.put("errorMessage", null);
		}
		else {
			map.put(
				"errorMessage", String.valueOf(exportTask.getErrorMessage()));
		}

		if (exportTask.getExecuteStatus() == null) {
			map.put("executeStatus", null);
		}
		else {
			map.put(
				"executeStatus", String.valueOf(exportTask.getExecuteStatus()));
		}

		if (exportTask.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(exportTask.getExternalReferenceCode()));
		}

		if (exportTask.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(exportTask.getId()));
		}

		if (exportTask.getProcessedItemsCount() == null) {
			map.put("processedItemsCount", null);
		}
		else {
			map.put(
				"processedItemsCount",
				String.valueOf(exportTask.getProcessedItemsCount()));
		}

		if (exportTask.getStartTime() == null) {
			map.put("startTime", null);
		}
		else {
			map.put(
				"startTime",
				liferayToJSONDateFormat.format(exportTask.getStartTime()));
		}

		if (exportTask.getTotalItemsCount() == null) {
			map.put("totalItemsCount", null);
		}
		else {
			map.put(
				"totalItemsCount",
				String.valueOf(exportTask.getTotalItemsCount()));
		}

		return map;
	}

	public static class ExportTaskJSONParser
		extends BaseJSONParser<ExportTask> {

		@Override
		protected ExportTask createDTO() {
			return new ExportTask();
		}

		@Override
		protected ExportTask[] createDTOArray(int size) {
			return new ExportTask[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "endTime")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "executeStatus")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "processedItemsCount")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "startTime")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalItemsCount")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ExportTask exportTask, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					exportTask.setClassName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				if (jsonParserFieldValue != null) {
					exportTask.setContentType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "endTime")) {
				if (jsonParserFieldValue != null) {
					exportTask.setEndTime(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				if (jsonParserFieldValue != null) {
					exportTask.setErrorMessage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "executeStatus")) {
				if (jsonParserFieldValue != null) {
					exportTask.setExecuteStatus(
						ExportTask.ExecuteStatus.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					exportTask.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					exportTask.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "processedItemsCount")) {

				if (jsonParserFieldValue != null) {
					exportTask.setProcessedItemsCount(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "startTime")) {
				if (jsonParserFieldValue != null) {
					exportTask.setStartTime(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalItemsCount")) {
				if (jsonParserFieldValue != null) {
					exportTask.setTotalItemsCount(
						Integer.valueOf((String)jsonParserFieldValue));
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