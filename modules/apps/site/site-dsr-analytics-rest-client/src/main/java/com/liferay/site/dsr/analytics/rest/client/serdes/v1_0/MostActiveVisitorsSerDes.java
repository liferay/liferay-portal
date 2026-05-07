/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.serdes.v1_0;

import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.MostActiveVisitor;
import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.MostActiveVisitors;
import com.liferay.site.dsr.analytics.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public class MostActiveVisitorsSerDes {

	public static MostActiveVisitors toDTO(String json) {
		MostActiveVisitorsJSONParser mostActiveVisitorsJSONParser =
			new MostActiveVisitorsJSONParser();

		return mostActiveVisitorsJSONParser.parseToDTO(json);
	}

	public static MostActiveVisitors[] toDTOs(String json) {
		MostActiveVisitorsJSONParser mostActiveVisitorsJSONParser =
			new MostActiveVisitorsJSONParser();

		return mostActiveVisitorsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MostActiveVisitors mostActiveVisitors) {
		if (mostActiveVisitors == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (mostActiveVisitors.getMostActiveVisitors() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mostActiveVisitors\": ");

			sb.append("[");

			for (int i = 0;
				 i < mostActiveVisitors.getMostActiveVisitors().length; i++) {

				sb.append(
					String.valueOf(
						mostActiveVisitors.getMostActiveVisitors()[i]));

				if ((i + 1) <
						mostActiveVisitors.getMostActiveVisitors().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (mostActiveVisitors.getTotal() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(mostActiveVisitors.getTotal());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MostActiveVisitorsJSONParser mostActiveVisitorsJSONParser =
			new MostActiveVisitorsJSONParser();

		return mostActiveVisitorsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		MostActiveVisitors mostActiveVisitors) {

		if (mostActiveVisitors == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (mostActiveVisitors.getMostActiveVisitors() == null) {
			map.put("mostActiveVisitors", null);
		}
		else {
			map.put(
				"mostActiveVisitors",
				String.valueOf(mostActiveVisitors.getMostActiveVisitors()));
		}

		if (mostActiveVisitors.getTotal() == null) {
			map.put("total", null);
		}
		else {
			map.put("total", String.valueOf(mostActiveVisitors.getTotal()));
		}

		return map;
	}

	public static class MostActiveVisitorsJSONParser
		extends BaseJSONParser<MostActiveVisitors> {

		@Override
		protected MostActiveVisitors createDTO() {
			return new MostActiveVisitors();
		}

		@Override
		protected MostActiveVisitors[] createDTOArray(int size) {
			return new MostActiveVisitors[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "mostActiveVisitors")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			MostActiveVisitors mostActiveVisitors, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "mostActiveVisitors")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MostActiveVisitor[] mostActiveVisitorsArray =
						new MostActiveVisitor[jsonParserFieldValues.length];

					for (int i = 0; i < mostActiveVisitorsArray.length; i++) {
						mostActiveVisitorsArray[i] =
							MostActiveVisitorSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					mostActiveVisitors.setMostActiveVisitors(
						mostActiveVisitorsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "total")) {
				if (jsonParserFieldValue != null) {
					mostActiveVisitors.setTotal(
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
// LIFERAY-REST-BUILDER-HASH:244072078