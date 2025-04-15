/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.AssigneeBulkSelection;
import com.liferay.portal.workflow.metrics.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class AssigneeBulkSelectionSerDes {

	public static AssigneeBulkSelection toDTO(String json) {
		AssigneeBulkSelectionJSONParser assigneeBulkSelectionJSONParser =
			new AssigneeBulkSelectionJSONParser();

		return assigneeBulkSelectionJSONParser.parseToDTO(json);
	}

	public static AssigneeBulkSelection[] toDTOs(String json) {
		AssigneeBulkSelectionJSONParser assigneeBulkSelectionJSONParser =
			new AssigneeBulkSelectionJSONParser();

		return assigneeBulkSelectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssigneeBulkSelection assigneeBulkSelection) {
		if (assigneeBulkSelection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assigneeBulkSelection.getInstanceIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"instanceIds\": ");

			sb.append("[");

			for (int i = 0; i < assigneeBulkSelection.getInstanceIds().length;
				 i++) {

				sb.append(assigneeBulkSelection.getInstanceIds()[i]);

				if ((i + 1) < assigneeBulkSelection.getInstanceIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssigneeBulkSelectionJSONParser assigneeBulkSelectionJSONParser =
			new AssigneeBulkSelectionJSONParser();

		return assigneeBulkSelectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AssigneeBulkSelection assigneeBulkSelection) {

		if (assigneeBulkSelection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assigneeBulkSelection.getInstanceIds() == null) {
			map.put("instanceIds", null);
		}
		else {
			map.put(
				"instanceIds",
				String.valueOf(assigneeBulkSelection.getInstanceIds()));
		}

		return map;
	}

	public static class AssigneeBulkSelectionJSONParser
		extends BaseJSONParser<AssigneeBulkSelection> {

		@Override
		protected AssigneeBulkSelection createDTO() {
			return new AssigneeBulkSelection();
		}

		@Override
		protected AssigneeBulkSelection[] createDTOArray(int size) {
			return new AssigneeBulkSelection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "instanceIds")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssigneeBulkSelection assigneeBulkSelection,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "instanceIds")) {
				if (jsonParserFieldValue != null) {
					assigneeBulkSelection.setInstanceIds(
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