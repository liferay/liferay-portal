/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.SelectionScope;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class SelectionScopeSerDes {

	public static SelectionScope toDTO(String json) {
		SelectionScopeJSONParser selectionScopeJSONParser =
			new SelectionScopeJSONParser();

		return selectionScopeJSONParser.parseToDTO(json);
	}

	public static SelectionScope[] toDTOs(String json) {
		SelectionScopeJSONParser selectionScopeJSONParser =
			new SelectionScopeJSONParser();

		return selectionScopeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SelectionScope selectionScope) {
		if (selectionScope == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (selectionScope.getFolderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"folderId\": ");

			sb.append(selectionScope.getFolderId());
		}

		if (selectionScope.getRepositoryId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"repositoryId\": ");

			sb.append(selectionScope.getRepositoryId());
		}

		if (selectionScope.getSelectAll() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectAll\": ");

			sb.append(selectionScope.getSelectAll());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SelectionScopeJSONParser selectionScopeJSONParser =
			new SelectionScopeJSONParser();

		return selectionScopeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SelectionScope selectionScope) {
		if (selectionScope == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (selectionScope.getFolderId() == null) {
			map.put("folderId", null);
		}
		else {
			map.put("folderId", String.valueOf(selectionScope.getFolderId()));
		}

		if (selectionScope.getRepositoryId() == null) {
			map.put("repositoryId", null);
		}
		else {
			map.put(
				"repositoryId",
				String.valueOf(selectionScope.getRepositoryId()));
		}

		if (selectionScope.getSelectAll() == null) {
			map.put("selectAll", null);
		}
		else {
			map.put("selectAll", String.valueOf(selectionScope.getSelectAll()));
		}

		return map;
	}

	public static class SelectionScopeJSONParser
		extends BaseJSONParser<SelectionScope> {

		@Override
		protected SelectionScope createDTO() {
			return new SelectionScope();
		}

		@Override
		protected SelectionScope[] createDTOArray(int size) {
			return new SelectionScope[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "folderId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "repositoryId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selectAll")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SelectionScope selectionScope, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "folderId")) {
				if (jsonParserFieldValue != null) {
					selectionScope.setFolderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "repositoryId")) {
				if (jsonParserFieldValue != null) {
					selectionScope.setRepositoryId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectAll")) {
				if (jsonParserFieldValue != null) {
					selectionScope.setSelectAll((Boolean)jsonParserFieldValue);
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