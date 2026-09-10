/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DateFragmentEditableElementValue;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class DateFragmentEditableElementValueSerDes {

	public static DateFragmentEditableElementValue toDTO(String json) {
		DateFragmentEditableElementValueJSONParser
			dateFragmentEditableElementValueJSONParser =
				new DateFragmentEditableElementValueJSONParser();

		return dateFragmentEditableElementValueJSONParser.parseToDTO(json);
	}

	public static DateFragmentEditableElementValue[] toDTOs(String json) {
		DateFragmentEditableElementValueJSONParser
			dateFragmentEditableElementValueJSONParser =
				new DateFragmentEditableElementValueJSONParser();

		return dateFragmentEditableElementValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DateFragmentEditableElementValue dateFragmentEditableElementValue) {

		if (dateFragmentEditableElementValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dateFragmentEditableElementValue.getDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"date\": ");

			sb.append(
				String.valueOf(dateFragmentEditableElementValue.getDate()));
		}

		if (dateFragmentEditableElementValue.getDateFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateFormat\": ");

			sb.append(
				String.valueOf(
					dateFragmentEditableElementValue.getDateFormat()));
		}

		if (dateFragmentEditableElementValue.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(dateFragmentEditableElementValue.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DateFragmentEditableElementValueJSONParser
			dateFragmentEditableElementValueJSONParser =
				new DateFragmentEditableElementValueJSONParser();

		return dateFragmentEditableElementValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DateFragmentEditableElementValue dateFragmentEditableElementValue) {

		if (dateFragmentEditableElementValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dateFragmentEditableElementValue.getDate() == null) {
			map.put("date", null);
		}
		else {
			map.put(
				"date",
				String.valueOf(dateFragmentEditableElementValue.getDate()));
		}

		if (dateFragmentEditableElementValue.getDateFormat() == null) {
			map.put("dateFormat", null);
		}
		else {
			map.put(
				"dateFormat",
				String.valueOf(
					dateFragmentEditableElementValue.getDateFormat()));
		}

		if (dateFragmentEditableElementValue.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(dateFragmentEditableElementValue.getType()));
		}

		return map;
	}

	public static class DateFragmentEditableElementValueJSONParser
		extends BaseJSONParser<DateFragmentEditableElementValue> {

		@Override
		protected DateFragmentEditableElementValue createDTO() {
			return new DateFragmentEditableElementValue();
		}

		@Override
		protected DateFragmentEditableElementValue[] createDTOArray(int size) {
			return new DateFragmentEditableElementValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "date")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateFormat")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DateFragmentEditableElementValue dateFragmentEditableElementValue,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "date")) {
				if (jsonParserFieldValue != null) {
					dateFragmentEditableElementValue.setDate(
						FragmentMappedValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateFormat")) {
				if (jsonParserFieldValue != null) {
					dateFragmentEditableElementValue.setDateFormat(
						FragmentInlineValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					dateFragmentEditableElementValue.setType(
						DateFragmentEditableElementValue.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:205322168