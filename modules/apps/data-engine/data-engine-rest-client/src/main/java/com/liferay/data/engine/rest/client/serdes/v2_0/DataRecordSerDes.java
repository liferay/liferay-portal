/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.client.serdes.v2_0;

import com.liferay.data.engine.rest.client.dto.v2_0.DataRecord;
import com.liferay.data.engine.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public class DataRecordSerDes {

	public static DataRecord toDTO(String json) {
		DataRecordJSONParser dataRecordJSONParser = new DataRecordJSONParser();

		return dataRecordJSONParser.parseToDTO(json);
	}

	public static DataRecord[] toDTOs(String json) {
		DataRecordJSONParser dataRecordJSONParser = new DataRecordJSONParser();

		return dataRecordJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataRecord dataRecord) {
		if (dataRecord == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataRecord.getDataRecordCollectionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataRecordCollectionId\": ");

			sb.append(dataRecord.getDataRecordCollectionId());
		}

		if (dataRecord.getDataRecordValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataRecordValues\": ");

			sb.append(_toJSON(dataRecord.getDataRecordValues()));
		}

		if (dataRecord.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(dataRecord.getId());
		}

		if (dataRecord.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(dataRecord.getStatus());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataRecordJSONParser dataRecordJSONParser = new DataRecordJSONParser();

		return dataRecordJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DataRecord dataRecord) {
		if (dataRecord == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataRecord.getDataRecordCollectionId() == null) {
			map.put("dataRecordCollectionId", null);
		}
		else {
			map.put(
				"dataRecordCollectionId",
				String.valueOf(dataRecord.getDataRecordCollectionId()));
		}

		if (dataRecord.getDataRecordValues() == null) {
			map.put("dataRecordValues", null);
		}
		else {
			map.put(
				"dataRecordValues",
				String.valueOf(dataRecord.getDataRecordValues()));
		}

		if (dataRecord.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(dataRecord.getId()));
		}

		if (dataRecord.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(dataRecord.getStatus()));
		}

		return map;
	}

	public static class DataRecordJSONParser
		extends BaseJSONParser<DataRecord> {

		@Override
		protected DataRecord createDTO() {
			return new DataRecord();
		}

		@Override
		protected DataRecord[] createDTOArray(int size) {
			return new DataRecord[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dataRecordCollectionId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataRecordValues")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataRecord dataRecord, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dataRecordCollectionId")) {
				if (jsonParserFieldValue != null) {
					dataRecord.setDataRecordCollectionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataRecordValues")) {
				if (jsonParserFieldValue != null) {
					dataRecord.setDataRecordValues(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					dataRecord.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					dataRecord.setStatus(
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