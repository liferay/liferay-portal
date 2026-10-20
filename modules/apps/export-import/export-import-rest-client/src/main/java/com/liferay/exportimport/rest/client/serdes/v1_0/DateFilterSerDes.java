/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.DateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.DateRangeDateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.FromLastPublishDateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.LastHoursDateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.Type;
import com.liferay.exportimport.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class DateFilterSerDes {

	public static DateFilter toDTO(String json) {
		DateFilterJSONParser dateFilterJSONParser = new DateFilterJSONParser();

		return dateFilterJSONParser.parseToDTO(json);
	}

	public static DateFilter[] toDTOs(String json) {
		DateFilterJSONParser dateFilterJSONParser = new DateFilterJSONParser();

		return dateFilterJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DateFilter dateFilter) {
		if (dateFilter == null) {
			return "null";
		}

		DateFilter.Type type = dateFilter.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("DateRange")) {
				return DateRangeDateFilterSerDes.toJSON(
					(DateRangeDateFilter)dateFilter);
			}

			if (typeString.equals("FromLastPublishDate")) {
				return FromLastPublishDateFilterSerDes.toJSON(
					(FromLastPublishDateFilter)dateFilter);
			}

			if (typeString.equals("LastHours")) {
				return LastHoursDateFilterSerDes.toJSON(
					(LastHoursDateFilter)dateFilter);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		DateFilterJSONParser dateFilterJSONParser = new DateFilterJSONParser();

		return dateFilterJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DateFilter dateFilter) {
		if (dateFilter == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dateFilter.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(dateFilter.getType()));
		}

		return map;
	}

	public static class DateFilterJSONParser
		extends BaseJSONParser<DateFilter> {

		@Override
		protected DateFilter createDTO() {
			return null;
		}

		@Override
		protected DateFilter[] createDTOArray(int size) {
			return new DateFilter[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public DateFilter parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("DateRange")) {
					return DateRangeDateFilter.toDTO(json);
				}

				if (typeString.equals("FromLastPublishDate")) {
					return FromLastPublishDateFilter.toDTO(json);
				}

				if (typeString.equals("LastHours")) {
					return LastHoursDateFilter.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			DateFilter dateFilter, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					dateFilter.setType(
						DateFilter.Type.create((String)jsonParserFieldValue));
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:-523685783