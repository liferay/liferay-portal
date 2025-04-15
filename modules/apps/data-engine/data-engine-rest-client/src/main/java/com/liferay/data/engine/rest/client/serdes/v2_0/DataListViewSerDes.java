/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.client.serdes.v2_0;

import com.liferay.data.engine.rest.client.dto.v2_0.DataListView;
import com.liferay.data.engine.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public class DataListViewSerDes {

	public static DataListView toDTO(String json) {
		DataListViewJSONParser dataListViewJSONParser =
			new DataListViewJSONParser();

		return dataListViewJSONParser.parseToDTO(json);
	}

	public static DataListView[] toDTOs(String json) {
		DataListViewJSONParser dataListViewJSONParser =
			new DataListViewJSONParser();

		return dataListViewJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataListView dataListView) {
		if (dataListView == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (dataListView.getAppliedFilters() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"appliedFilters\": ");

			sb.append(_toJSON(dataListView.getAppliedFilters()));
		}

		if (dataListView.getDataDefinitionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataDefinitionId\": ");

			sb.append(dataListView.getDataDefinitionId());
		}

		if (dataListView.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(dataListView.getDateCreated()));

			sb.append("\"");
		}

		if (dataListView.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(dataListView.getDateModified()));

			sb.append("\"");
		}

		if (dataListView.getFieldNames() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldNames\": ");

			sb.append("[");

			for (int i = 0; i < dataListView.getFieldNames().length; i++) {
				sb.append(_toJSON(dataListView.getFieldNames()[i]));

				if ((i + 1) < dataListView.getFieldNames().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataListView.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(dataListView.getId());
		}

		if (dataListView.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(dataListView.getName()));
		}

		if (dataListView.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(dataListView.getSiteId());
		}

		if (dataListView.getSortField() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sortField\": ");

			sb.append("\"");

			sb.append(_escape(dataListView.getSortField()));

			sb.append("\"");
		}

		if (dataListView.getUserId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(dataListView.getUserId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataListViewJSONParser dataListViewJSONParser =
			new DataListViewJSONParser();

		return dataListViewJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DataListView dataListView) {
		if (dataListView == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (dataListView.getAppliedFilters() == null) {
			map.put("appliedFilters", null);
		}
		else {
			map.put(
				"appliedFilters",
				String.valueOf(dataListView.getAppliedFilters()));
		}

		if (dataListView.getDataDefinitionId() == null) {
			map.put("dataDefinitionId", null);
		}
		else {
			map.put(
				"dataDefinitionId",
				String.valueOf(dataListView.getDataDefinitionId()));
		}

		if (dataListView.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(dataListView.getDateCreated()));
		}

		if (dataListView.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(dataListView.getDateModified()));
		}

		if (dataListView.getFieldNames() == null) {
			map.put("fieldNames", null);
		}
		else {
			map.put("fieldNames", String.valueOf(dataListView.getFieldNames()));
		}

		if (dataListView.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(dataListView.getId()));
		}

		if (dataListView.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(dataListView.getName()));
		}

		if (dataListView.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(dataListView.getSiteId()));
		}

		if (dataListView.getSortField() == null) {
			map.put("sortField", null);
		}
		else {
			map.put("sortField", String.valueOf(dataListView.getSortField()));
		}

		if (dataListView.getUserId() == null) {
			map.put("userId", null);
		}
		else {
			map.put("userId", String.valueOf(dataListView.getUserId()));
		}

		return map;
	}

	public static class DataListViewJSONParser
		extends BaseJSONParser<DataListView> {

		@Override
		protected DataListView createDTO() {
			return new DataListView();
		}

		@Override
		protected DataListView[] createDTOArray(int size) {
			return new DataListView[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "appliedFilters")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "dataDefinitionId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fieldNames")) {
				return false;
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
			else if (Objects.equals(jsonParserFieldName, "sortField")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataListView dataListView, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "appliedFilters")) {
				if (jsonParserFieldValue != null) {
					dataListView.setAppliedFilters(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataDefinitionId")) {
				if (jsonParserFieldValue != null) {
					dataListView.setDataDefinitionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					dataListView.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					dataListView.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fieldNames")) {
				if (jsonParserFieldValue != null) {
					dataListView.setFieldNames(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					dataListView.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					dataListView.setName(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					dataListView.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sortField")) {
				if (jsonParserFieldValue != null) {
					dataListView.setSortField((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userId")) {
				if (jsonParserFieldValue != null) {
					dataListView.setUserId(
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