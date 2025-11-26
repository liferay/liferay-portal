/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.FragmentFieldDate;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FragmentFieldDateSerDes {

	public static FragmentFieldDate toDTO(String json) {
		FragmentFieldDateJSONParser fragmentFieldDateJSONParser =
			new FragmentFieldDateJSONParser();

		return fragmentFieldDateJSONParser.parseToDTO(json);
	}

	public static FragmentFieldDate[] toDTOs(String json) {
		FragmentFieldDateJSONParser fragmentFieldDateJSONParser =
			new FragmentFieldDateJSONParser();

		return fragmentFieldDateJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FragmentFieldDate fragmentFieldDate) {
		if (fragmentFieldDate == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentFieldDate.getDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"date\": ");

			sb.append(String.valueOf(fragmentFieldDate.getDate()));
		}

		if (fragmentFieldDate.getDateFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateFormat\": ");

			sb.append(String.valueOf(fragmentFieldDate.getDateFormat()));
		}

		if (fragmentFieldDate.getFragmentLink() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentLink\": ");

			sb.append(String.valueOf(fragmentFieldDate.getFragmentLink()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentFieldDateJSONParser fragmentFieldDateJSONParser =
			new FragmentFieldDateJSONParser();

		return fragmentFieldDateJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentFieldDate fragmentFieldDate) {

		if (fragmentFieldDate == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentFieldDate.getDate() == null) {
			map.put("date", null);
		}
		else {
			map.put("date", String.valueOf(fragmentFieldDate.getDate()));
		}

		if (fragmentFieldDate.getDateFormat() == null) {
			map.put("dateFormat", null);
		}
		else {
			map.put(
				"dateFormat",
				String.valueOf(fragmentFieldDate.getDateFormat()));
		}

		if (fragmentFieldDate.getFragmentLink() == null) {
			map.put("fragmentLink", null);
		}
		else {
			map.put(
				"fragmentLink",
				String.valueOf(fragmentFieldDate.getFragmentLink()));
		}

		return map;
	}

	public static class FragmentFieldDateJSONParser
		extends BaseJSONParser<FragmentFieldDate> {

		@Override
		protected FragmentFieldDate createDTO() {
			return new FragmentFieldDate();
		}

		@Override
		protected FragmentFieldDate[] createDTOArray(int size) {
			return new FragmentFieldDate[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "date")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateFormat")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentLink")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentFieldDate fragmentFieldDate, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "date")) {
				if (jsonParserFieldValue != null) {
					fragmentFieldDate.setDate(
						FragmentMappedValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateFormat")) {
				if (jsonParserFieldValue != null) {
					fragmentFieldDate.setDateFormat(
						FragmentInlineValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentLink")) {
				if (jsonParserFieldValue != null) {
					fragmentFieldDate.setFragmentLink(
						FragmentLinkSerDes.toDTO((String)jsonParserFieldValue));
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