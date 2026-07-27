/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.client.serdes.v1_0;

import com.liferay.batch.planner.rest.client.dto.v1_0.DepotScope;
import com.liferay.batch.planner.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Matija Petanjek
 * @generated
 */
@Generated("")
public class DepotScopeSerDes {

	public static DepotScope toDTO(String json) {
		DepotScopeJSONParser depotScopeJSONParser = new DepotScopeJSONParser();

		return depotScopeJSONParser.parseToDTO(json);
	}

	public static DepotScope[] toDTOs(String json) {
		DepotScopeJSONParser depotScopeJSONParser = new DepotScopeJSONParser();

		return depotScopeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DepotScope depotScope) {
		if (depotScope == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (depotScope.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(depotScope.getLabel()));

			sb.append("\"");
		}

		if (depotScope.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append(depotScope.getValue());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DepotScopeJSONParser depotScopeJSONParser = new DepotScopeJSONParser();

		return depotScopeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DepotScope depotScope) {
		if (depotScope == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (depotScope.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(depotScope.getLabel()));
		}

		if (depotScope.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(depotScope.getValue()));
		}

		return map;
	}

	public static class DepotScopeJSONParser
		extends BaseJSONParser<DepotScope> {

		@Override
		protected DepotScope createDTO() {
			return new DepotScope();
		}

		@Override
		protected DepotScope[] createDTOArray(int size) {
			return new DepotScope[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DepotScope depotScope, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					depotScope.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					depotScope.setValue(
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
// LIFERAY-REST-BUILDER-HASH:-1677778716