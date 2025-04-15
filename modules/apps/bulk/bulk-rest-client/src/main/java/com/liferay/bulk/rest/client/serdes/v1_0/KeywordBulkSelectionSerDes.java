/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.KeywordBulkSelection;
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
public class KeywordBulkSelectionSerDes {

	public static KeywordBulkSelection toDTO(String json) {
		KeywordBulkSelectionJSONParser keywordBulkSelectionJSONParser =
			new KeywordBulkSelectionJSONParser();

		return keywordBulkSelectionJSONParser.parseToDTO(json);
	}

	public static KeywordBulkSelection[] toDTOs(String json) {
		KeywordBulkSelectionJSONParser keywordBulkSelectionJSONParser =
			new KeywordBulkSelectionJSONParser();

		return keywordBulkSelectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(KeywordBulkSelection keywordBulkSelection) {
		if (keywordBulkSelection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (keywordBulkSelection.getDocumentBulkSelection() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentBulkSelection\": ");

			sb.append(
				String.valueOf(
					keywordBulkSelection.getDocumentBulkSelection()));
		}

		if (keywordBulkSelection.getKeywordsToAdd() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywordsToAdd\": ");

			sb.append("[");

			for (int i = 0; i < keywordBulkSelection.getKeywordsToAdd().length;
				 i++) {

				sb.append(_toJSON(keywordBulkSelection.getKeywordsToAdd()[i]));

				if ((i + 1) < keywordBulkSelection.getKeywordsToAdd().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (keywordBulkSelection.getKeywordsToRemove() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywordsToRemove\": ");

			sb.append("[");

			for (int i = 0;
				 i < keywordBulkSelection.getKeywordsToRemove().length; i++) {

				sb.append(
					_toJSON(keywordBulkSelection.getKeywordsToRemove()[i]));

				if ((i + 1) <
						keywordBulkSelection.getKeywordsToRemove().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		KeywordBulkSelectionJSONParser keywordBulkSelectionJSONParser =
			new KeywordBulkSelectionJSONParser();

		return keywordBulkSelectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		KeywordBulkSelection keywordBulkSelection) {

		if (keywordBulkSelection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (keywordBulkSelection.getDocumentBulkSelection() == null) {
			map.put("documentBulkSelection", null);
		}
		else {
			map.put(
				"documentBulkSelection",
				String.valueOf(
					keywordBulkSelection.getDocumentBulkSelection()));
		}

		if (keywordBulkSelection.getKeywordsToAdd() == null) {
			map.put("keywordsToAdd", null);
		}
		else {
			map.put(
				"keywordsToAdd",
				String.valueOf(keywordBulkSelection.getKeywordsToAdd()));
		}

		if (keywordBulkSelection.getKeywordsToRemove() == null) {
			map.put("keywordsToRemove", null);
		}
		else {
			map.put(
				"keywordsToRemove",
				String.valueOf(keywordBulkSelection.getKeywordsToRemove()));
		}

		return map;
	}

	public static class KeywordBulkSelectionJSONParser
		extends BaseJSONParser<KeywordBulkSelection> {

		@Override
		protected KeywordBulkSelection createDTO() {
			return new KeywordBulkSelection();
		}

		@Override
		protected KeywordBulkSelection[] createDTOArray(int size) {
			return new KeywordBulkSelection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "documentBulkSelection")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywordsToAdd")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywordsToRemove")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			KeywordBulkSelection keywordBulkSelection,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "documentBulkSelection")) {
				if (jsonParserFieldValue != null) {
					keywordBulkSelection.setDocumentBulkSelection(
						DocumentBulkSelectionSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywordsToAdd")) {
				if (jsonParserFieldValue != null) {
					keywordBulkSelection.setKeywordsToAdd(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywordsToRemove")) {
				if (jsonParserFieldValue != null) {
					keywordBulkSelection.setKeywordsToRemove(
						toStrings((Object[])jsonParserFieldValue));
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