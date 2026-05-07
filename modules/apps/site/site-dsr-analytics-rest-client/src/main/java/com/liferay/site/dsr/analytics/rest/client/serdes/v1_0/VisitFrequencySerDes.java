/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.client.serdes.v1_0;

import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.VisitFrequency;
import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.VisitFrequencyItem;
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
public class VisitFrequencySerDes {

	public static VisitFrequency toDTO(String json) {
		VisitFrequencyJSONParser visitFrequencyJSONParser =
			new VisitFrequencyJSONParser();

		return visitFrequencyJSONParser.parseToDTO(json);
	}

	public static VisitFrequency[] toDTOs(String json) {
		VisitFrequencyJSONParser visitFrequencyJSONParser =
			new VisitFrequencyJSONParser();

		return visitFrequencyJSONParser.parseToDTOs(json);
	}

	public static String toJSON(VisitFrequency visitFrequency) {
		if (visitFrequency == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (visitFrequency.getTotalCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalCount\": ");

			sb.append(visitFrequency.getTotalCount());
		}

		if (visitFrequency.getVisitFrequencyItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visitFrequencyItems\": ");

			sb.append("[");

			for (int i = 0; i < visitFrequency.getVisitFrequencyItems().length;
				 i++) {

				sb.append(
					String.valueOf(visitFrequency.getVisitFrequencyItems()[i]));

				if ((i + 1) < visitFrequency.getVisitFrequencyItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		VisitFrequencyJSONParser visitFrequencyJSONParser =
			new VisitFrequencyJSONParser();

		return visitFrequencyJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(VisitFrequency visitFrequency) {
		if (visitFrequency == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (visitFrequency.getTotalCount() == null) {
			map.put("totalCount", null);
		}
		else {
			map.put(
				"totalCount", String.valueOf(visitFrequency.getTotalCount()));
		}

		if (visitFrequency.getVisitFrequencyItems() == null) {
			map.put("visitFrequencyItems", null);
		}
		else {
			map.put(
				"visitFrequencyItems",
				String.valueOf(visitFrequency.getVisitFrequencyItems()));
		}

		return map;
	}

	public static class VisitFrequencyJSONParser
		extends BaseJSONParser<VisitFrequency> {

		@Override
		protected VisitFrequency createDTO() {
			return new VisitFrequency();
		}

		@Override
		protected VisitFrequency[] createDTOArray(int size) {
			return new VisitFrequency[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "totalCount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "visitFrequencyItems")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			VisitFrequency visitFrequency, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "totalCount")) {
				if (jsonParserFieldValue != null) {
					visitFrequency.setTotalCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "visitFrequencyItems")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					VisitFrequencyItem[] visitFrequencyItemsArray =
						new VisitFrequencyItem[jsonParserFieldValues.length];

					for (int i = 0; i < visitFrequencyItemsArray.length; i++) {
						visitFrequencyItemsArray[i] =
							VisitFrequencyItemSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					visitFrequency.setVisitFrequencyItems(
						visitFrequencyItemsArray);
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
// LIFERAY-REST-BUILDER-HASH:-605949812