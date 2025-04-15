/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.DocumentBulkSelection;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class DocumentBulkSelectionSerDes {

	public static DocumentBulkSelection toDTO(String json) {
		DocumentBulkSelectionJSONParser documentBulkSelectionJSONParser =
			new DocumentBulkSelectionJSONParser();

		return documentBulkSelectionJSONParser.parseToDTO(json);
	}

	public static DocumentBulkSelection[] toDTOs(String json) {
		DocumentBulkSelectionJSONParser documentBulkSelectionJSONParser =
			new DocumentBulkSelectionJSONParser();

		return documentBulkSelectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DocumentBulkSelection documentBulkSelection) {
		if (documentBulkSelection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (documentBulkSelection.getDocumentIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentIds\": ");

			sb.append("[");

			for (int i = 0; i < documentBulkSelection.getDocumentIds().length;
				 i++) {

				sb.append(_toJSON(documentBulkSelection.getDocumentIds()[i]));

				if ((i + 1) < documentBulkSelection.getDocumentIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (documentBulkSelection.getSelectionScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectionScope\": ");

			sb.append(
				String.valueOf(documentBulkSelection.getSelectionScope()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DocumentBulkSelectionJSONParser documentBulkSelectionJSONParser =
			new DocumentBulkSelectionJSONParser();

		return documentBulkSelectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DocumentBulkSelection documentBulkSelection) {

		if (documentBulkSelection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (documentBulkSelection.getDocumentIds() == null) {
			map.put("documentIds", null);
		}
		else {
			map.put(
				"documentIds",
				String.valueOf(documentBulkSelection.getDocumentIds()));
		}

		if (documentBulkSelection.getSelectionScope() == null) {
			map.put("selectionScope", null);
		}
		else {
			map.put(
				"selectionScope",
				String.valueOf(documentBulkSelection.getSelectionScope()));
		}

		return map;
	}

	public static class DocumentBulkSelectionJSONParser
		extends BaseJSONParser<DocumentBulkSelection> {

		@Override
		protected DocumentBulkSelection createDTO() {
			return new DocumentBulkSelection();
		}

		@Override
		protected DocumentBulkSelection[] createDTOArray(int size) {
			return new DocumentBulkSelection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "documentIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selectionScope")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DocumentBulkSelection documentBulkSelection,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "documentIds")) {
				if (jsonParserFieldValue != null) {
					documentBulkSelection.setDocumentIds(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectionScope")) {
				if (jsonParserFieldValue != null) {
					documentBulkSelection.setSelectionScope(
						SelectionScopeSerDes.toDTO(
							(String)jsonParserFieldValue));
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