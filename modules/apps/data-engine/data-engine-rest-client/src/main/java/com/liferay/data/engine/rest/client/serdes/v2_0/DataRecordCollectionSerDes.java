/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.client.serdes.v2_0;

import com.liferay.data.engine.rest.client.dto.v2_0.DataRecordCollection;
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
public class DataRecordCollectionSerDes {

	public static DataRecordCollection toDTO(String json) {
		DataRecordCollectionJSONParser dataRecordCollectionJSONParser =
			new DataRecordCollectionJSONParser();

		return dataRecordCollectionJSONParser.parseToDTO(json);
	}

	public static DataRecordCollection[] toDTOs(String json) {
		DataRecordCollectionJSONParser dataRecordCollectionJSONParser =
			new DataRecordCollectionJSONParser();

		return dataRecordCollectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataRecordCollection dataRecordCollection) {
		if (dataRecordCollection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataRecordCollection.getDataDefinitionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataDefinitionId\": ");

			sb.append(dataRecordCollection.getDataDefinitionId());
		}

		if (dataRecordCollection.getDataRecordCollectionKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataRecordCollectionKey\": ");

			sb.append("\"");

			sb.append(
				_escape(dataRecordCollection.getDataRecordCollectionKey()));

			sb.append("\"");
		}

		if (dataRecordCollection.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(dataRecordCollection.getDescription()));
		}

		if (dataRecordCollection.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(dataRecordCollection.getId());
		}

		if (dataRecordCollection.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(dataRecordCollection.getName()));
		}

		if (dataRecordCollection.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(dataRecordCollection.getSiteId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataRecordCollectionJSONParser dataRecordCollectionJSONParser =
			new DataRecordCollectionJSONParser();

		return dataRecordCollectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DataRecordCollection dataRecordCollection) {

		if (dataRecordCollection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataRecordCollection.getDataDefinitionId() == null) {
			map.put("dataDefinitionId", null);
		}
		else {
			map.put(
				"dataDefinitionId",
				String.valueOf(dataRecordCollection.getDataDefinitionId()));
		}

		if (dataRecordCollection.getDataRecordCollectionKey() == null) {
			map.put("dataRecordCollectionKey", null);
		}
		else {
			map.put(
				"dataRecordCollectionKey",
				String.valueOf(
					dataRecordCollection.getDataRecordCollectionKey()));
		}

		if (dataRecordCollection.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(dataRecordCollection.getDescription()));
		}

		if (dataRecordCollection.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(dataRecordCollection.getId()));
		}

		if (dataRecordCollection.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(dataRecordCollection.getName()));
		}

		if (dataRecordCollection.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(dataRecordCollection.getSiteId()));
		}

		return map;
	}

	public static class DataRecordCollectionJSONParser
		extends BaseJSONParser<DataRecordCollection> {

		@Override
		protected DataRecordCollection createDTO() {
			return new DataRecordCollection();
		}

		@Override
		protected DataRecordCollection[] createDTOArray(int size) {
			return new DataRecordCollection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dataDefinitionId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "dataRecordCollectionKey")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataRecordCollection dataRecordCollection,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dataDefinitionId")) {
				if (jsonParserFieldValue != null) {
					dataRecordCollection.setDataDefinitionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "dataRecordCollectionKey")) {

				if (jsonParserFieldValue != null) {
					dataRecordCollection.setDataRecordCollectionKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					dataRecordCollection.setDescription(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					dataRecordCollection.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					dataRecordCollection.setName(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					dataRecordCollection.setSiteId(
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