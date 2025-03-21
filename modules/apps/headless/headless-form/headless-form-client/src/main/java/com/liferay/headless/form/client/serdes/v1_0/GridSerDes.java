/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.client.serdes.v1_0;

import com.liferay.headless.form.client.dto.v1_0.FormFieldOption;
import com.liferay.headless.form.client.dto.v1_0.Grid;
import com.liferay.headless.form.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class GridSerDes {

	public static Grid toDTO(String json) {
		GridJSONParser gridJSONParser = new GridJSONParser();

		return gridJSONParser.parseToDTO(json);
	}

	public static Grid[] toDTOs(String json) {
		GridJSONParser gridJSONParser = new GridJSONParser();

		return gridJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Grid grid) {
		if (grid == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (grid.getColumns() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"columns\": ");

			sb.append("[");

			for (int i = 0; i < grid.getColumns().length; i++) {
				sb.append(String.valueOf(grid.getColumns()[i]));

				if ((i + 1) < grid.getColumns().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (grid.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(grid.getId());
		}

		if (grid.getRows() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rows\": ");

			sb.append("[");

			for (int i = 0; i < grid.getRows().length; i++) {
				sb.append(String.valueOf(grid.getRows()[i]));

				if ((i + 1) < grid.getRows().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		GridJSONParser gridJSONParser = new GridJSONParser();

		return gridJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Grid grid) {
		if (grid == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (grid.getColumns() == null) {
			map.put("columns", null);
		}
		else {
			map.put("columns", String.valueOf(grid.getColumns()));
		}

		if (grid.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(grid.getId()));
		}

		if (grid.getRows() == null) {
			map.put("rows", null);
		}
		else {
			map.put("rows", String.valueOf(grid.getRows()));
		}

		return map;
	}

	public static class GridJSONParser extends BaseJSONParser<Grid> {

		@Override
		protected Grid createDTO() {
			return new Grid();
		}

		@Override
		protected Grid[] createDTOArray(int size) {
			return new Grid[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "columns")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "rows")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Grid grid, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "columns")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FormFieldOption[] columnsArray =
						new FormFieldOption[jsonParserFieldValues.length];

					for (int i = 0; i < columnsArray.length; i++) {
						columnsArray[i] = FormFieldOptionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					grid.setColumns(columnsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					grid.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "rows")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FormFieldOption[] rowsArray =
						new FormFieldOption[jsonParserFieldValues.length];

					for (int i = 0; i < rowsArray.length; i++) {
						rowsArray[i] = FormFieldOptionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					grid.setRows(rowsArray);
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