/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.client.serdes.v2_0;

import com.liferay.data.engine.rest.client.dto.v2_0.DataDefinitionFieldLink;
import com.liferay.data.engine.rest.client.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.client.dto.v2_0.DataListView;
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
public class DataDefinitionFieldLinkSerDes {

	public static DataDefinitionFieldLink toDTO(String json) {
		DataDefinitionFieldLinkJSONParser dataDefinitionFieldLinkJSONParser =
			new DataDefinitionFieldLinkJSONParser();

		return dataDefinitionFieldLinkJSONParser.parseToDTO(json);
	}

	public static DataDefinitionFieldLink[] toDTOs(String json) {
		DataDefinitionFieldLinkJSONParser dataDefinitionFieldLinkJSONParser =
			new DataDefinitionFieldLinkJSONParser();

		return dataDefinitionFieldLinkJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DataDefinitionFieldLink dataDefinitionFieldLink) {

		if (dataDefinitionFieldLink == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataDefinitionFieldLink.getDataDefinition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataDefinition\": ");

			sb.append(
				String.valueOf(dataDefinitionFieldLink.getDataDefinition()));
		}

		if (dataDefinitionFieldLink.getDataLayouts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataLayouts\": ");

			sb.append("[");

			for (int i = 0; i < dataDefinitionFieldLink.getDataLayouts().length;
				 i++) {

				sb.append(
					String.valueOf(
						dataDefinitionFieldLink.getDataLayouts()[i]));

				if ((i + 1) < dataDefinitionFieldLink.getDataLayouts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataDefinitionFieldLink.getDataListViews() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataListViews\": ");

			sb.append("[");

			for (int i = 0;
				 i < dataDefinitionFieldLink.getDataListViews().length; i++) {

				sb.append(
					String.valueOf(
						dataDefinitionFieldLink.getDataListViews()[i]));

				if ((i + 1) <
						dataDefinitionFieldLink.getDataListViews().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataDefinitionFieldLinkJSONParser dataDefinitionFieldLinkJSONParser =
			new DataDefinitionFieldLinkJSONParser();

		return dataDefinitionFieldLinkJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DataDefinitionFieldLink dataDefinitionFieldLink) {

		if (dataDefinitionFieldLink == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataDefinitionFieldLink.getDataDefinition() == null) {
			map.put("dataDefinition", null);
		}
		else {
			map.put(
				"dataDefinition",
				String.valueOf(dataDefinitionFieldLink.getDataDefinition()));
		}

		if (dataDefinitionFieldLink.getDataLayouts() == null) {
			map.put("dataLayouts", null);
		}
		else {
			map.put(
				"dataLayouts",
				String.valueOf(dataDefinitionFieldLink.getDataLayouts()));
		}

		if (dataDefinitionFieldLink.getDataListViews() == null) {
			map.put("dataListViews", null);
		}
		else {
			map.put(
				"dataListViews",
				String.valueOf(dataDefinitionFieldLink.getDataListViews()));
		}

		return map;
	}

	public static class DataDefinitionFieldLinkJSONParser
		extends BaseJSONParser<DataDefinitionFieldLink> {

		@Override
		protected DataDefinitionFieldLink createDTO() {
			return new DataDefinitionFieldLink();
		}

		@Override
		protected DataDefinitionFieldLink[] createDTOArray(int size) {
			return new DataDefinitionFieldLink[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dataDefinition")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataLayouts")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataListViews")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataDefinitionFieldLink dataDefinitionFieldLink,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dataDefinition")) {
				if (jsonParserFieldValue != null) {
					dataDefinitionFieldLink.setDataDefinition(
						DataDefinitionSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataLayouts")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DataLayout[] dataLayoutsArray =
						new DataLayout[jsonParserFieldValues.length];

					for (int i = 0; i < dataLayoutsArray.length; i++) {
						dataLayoutsArray[i] = DataLayoutSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					dataDefinitionFieldLink.setDataLayouts(dataLayoutsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataListViews")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					DataListView[] dataListViewsArray =
						new DataListView[jsonParserFieldValues.length];

					for (int i = 0; i < dataListViewsArray.length; i++) {
						dataListViewsArray[i] = DataListViewSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					dataDefinitionFieldLink.setDataListViews(
						dataListViewsArray);
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