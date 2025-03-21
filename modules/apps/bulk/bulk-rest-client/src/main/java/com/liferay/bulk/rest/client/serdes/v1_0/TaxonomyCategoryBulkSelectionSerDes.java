/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.TaxonomyCategoryBulkSelection;
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
public class TaxonomyCategoryBulkSelectionSerDes {

	public static TaxonomyCategoryBulkSelection toDTO(String json) {
		TaxonomyCategoryBulkSelectionJSONParser
			taxonomyCategoryBulkSelectionJSONParser =
				new TaxonomyCategoryBulkSelectionJSONParser();

		return taxonomyCategoryBulkSelectionJSONParser.parseToDTO(json);
	}

	public static TaxonomyCategoryBulkSelection[] toDTOs(String json) {
		TaxonomyCategoryBulkSelectionJSONParser
			taxonomyCategoryBulkSelectionJSONParser =
				new TaxonomyCategoryBulkSelectionJSONParser();

		return taxonomyCategoryBulkSelectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		TaxonomyCategoryBulkSelection taxonomyCategoryBulkSelection) {

		if (taxonomyCategoryBulkSelection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (taxonomyCategoryBulkSelection.getDocumentBulkSelection() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentBulkSelection\": ");

			sb.append(
				String.valueOf(
					taxonomyCategoryBulkSelection.getDocumentBulkSelection()));
		}

		if (taxonomyCategoryBulkSelection.getTaxonomyCategoryIdsToAdd() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIdsToAdd\": ");

			sb.append("[");

			for (int i = 0;
				 i < taxonomyCategoryBulkSelection.
					 getTaxonomyCategoryIdsToAdd().length;
				 i++) {

				sb.append(
					taxonomyCategoryBulkSelection.getTaxonomyCategoryIdsToAdd()
						[i]);

				if ((i + 1) < taxonomyCategoryBulkSelection.
						getTaxonomyCategoryIdsToAdd().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (taxonomyCategoryBulkSelection.getTaxonomyCategoryIdsToRemove() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIdsToRemove\": ");

			sb.append("[");

			for (int i = 0;
				 i < taxonomyCategoryBulkSelection.
					 getTaxonomyCategoryIdsToRemove().length;
				 i++) {

				sb.append(
					taxonomyCategoryBulkSelection.
						getTaxonomyCategoryIdsToRemove()[i]);

				if ((i + 1) < taxonomyCategoryBulkSelection.
						getTaxonomyCategoryIdsToRemove().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaxonomyCategoryBulkSelectionJSONParser
			taxonomyCategoryBulkSelectionJSONParser =
				new TaxonomyCategoryBulkSelectionJSONParser();

		return taxonomyCategoryBulkSelectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TaxonomyCategoryBulkSelection taxonomyCategoryBulkSelection) {

		if (taxonomyCategoryBulkSelection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (taxonomyCategoryBulkSelection.getDocumentBulkSelection() == null) {
			map.put("documentBulkSelection", null);
		}
		else {
			map.put(
				"documentBulkSelection",
				String.valueOf(
					taxonomyCategoryBulkSelection.getDocumentBulkSelection()));
		}

		if (taxonomyCategoryBulkSelection.getTaxonomyCategoryIdsToAdd() ==
				null) {

			map.put("taxonomyCategoryIdsToAdd", null);
		}
		else {
			map.put(
				"taxonomyCategoryIdsToAdd",
				String.valueOf(
					taxonomyCategoryBulkSelection.
						getTaxonomyCategoryIdsToAdd()));
		}

		if (taxonomyCategoryBulkSelection.getTaxonomyCategoryIdsToRemove() ==
				null) {

			map.put("taxonomyCategoryIdsToRemove", null);
		}
		else {
			map.put(
				"taxonomyCategoryIdsToRemove",
				String.valueOf(
					taxonomyCategoryBulkSelection.
						getTaxonomyCategoryIdsToRemove()));
		}

		return map;
	}

	public static class TaxonomyCategoryBulkSelectionJSONParser
		extends BaseJSONParser<TaxonomyCategoryBulkSelection> {

		@Override
		protected TaxonomyCategoryBulkSelection createDTO() {
			return new TaxonomyCategoryBulkSelection();
		}

		@Override
		protected TaxonomyCategoryBulkSelection[] createDTOArray(int size) {
			return new TaxonomyCategoryBulkSelection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "documentBulkSelection")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryIdsToAdd")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryIdsToRemove")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TaxonomyCategoryBulkSelection taxonomyCategoryBulkSelection,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "documentBulkSelection")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategoryBulkSelection.setDocumentBulkSelection(
						DocumentBulkSelectionSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryIdsToAdd")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategoryBulkSelection.setTaxonomyCategoryIdsToAdd(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryIdsToRemove")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategoryBulkSelection.
						setTaxonomyCategoryIdsToRemove(
							toLongs((Object[])jsonParserFieldValue));
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