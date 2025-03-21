/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.DataLayoutPage;
import com.liferay.headless.delivery.client.dto.v1_0.DataLayoutRow;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class DataLayoutPageSerDes {

	public static DataLayoutPage toDTO(String json) {
		DataLayoutPageJSONParser dataLayoutPageJSONParser =
			new DataLayoutPageJSONParser();

		return dataLayoutPageJSONParser.parseToDTO(json);
	}

	public static DataLayoutPage[] toDTOs(String json) {
		DataLayoutPageJSONParser dataLayoutPageJSONParser =
			new DataLayoutPageJSONParser();

		return dataLayoutPageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataLayoutPage dataLayoutPage) {
		if (dataLayoutPage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataLayoutPage.getDataLayoutRows() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataLayoutRows\": ");

			sb.append("[");

			for (int i = 0; i < dataLayoutPage.getDataLayoutRows().length;
				 i++) {

				sb.append(dataLayoutPage.getDataLayoutRows()[i]);

				if ((i + 1) < dataLayoutPage.getDataLayoutRows().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataLayoutPage.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(dataLayoutPage.getDescription()));
		}

		if (dataLayoutPage.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append(_toJSON(dataLayoutPage.getTitle()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataLayoutPageJSONParser dataLayoutPageJSONParser =
			new DataLayoutPageJSONParser();

		return dataLayoutPageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DataLayoutPage dataLayoutPage) {
		if (dataLayoutPage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataLayoutPage.getDataLayoutRows() == null) {
			map.put("dataLayoutRows", null);
		}
		else {
			map.put(
				"dataLayoutRows",
				String.valueOf(dataLayoutPage.getDataLayoutRows()));
		}

		if (dataLayoutPage.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(dataLayoutPage.getDescription()));
		}

		if (dataLayoutPage.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(dataLayoutPage.getTitle()));
		}

		return map;
	}

	public static class DataLayoutPageJSONParser
		extends BaseJSONParser<DataLayoutPage> {

		@Override
		protected DataLayoutPage createDTO() {
			return new DataLayoutPage();
		}

		@Override
		protected DataLayoutPage[] createDTOArray(int size) {
			return new DataLayoutPage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dataLayoutRows")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			DataLayoutPage dataLayoutPage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dataLayoutRows")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DataLayoutRow[] dataLayoutRowsArray =
						new DataLayoutRow[jsonParserFieldValues.length];

					for (int i = 0; i < dataLayoutRowsArray.length; i++) {
						dataLayoutRowsArray[i] = DataLayoutRowSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					dataLayoutPage.setDataLayoutRows(dataLayoutRowsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					dataLayoutPage.setDescription(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					dataLayoutPage.setTitle(
						(Map<String, Object>)jsonParserFieldValue);
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