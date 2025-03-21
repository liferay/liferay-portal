/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.URLFormSubmissionResult;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class URLFormSubmissionResultSerDes {

	public static URLFormSubmissionResult toDTO(String json) {
		URLFormSubmissionResultJSONParser urlFormSubmissionResultJSONParser =
			new URLFormSubmissionResultJSONParser();

		return urlFormSubmissionResultJSONParser.parseToDTO(json);
	}

	public static URLFormSubmissionResult[] toDTOs(String json) {
		URLFormSubmissionResultJSONParser urlFormSubmissionResultJSONParser =
			new URLFormSubmissionResultJSONParser();

		return urlFormSubmissionResultJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		URLFormSubmissionResult urlFormSubmissionResult) {

		if (urlFormSubmissionResult == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (urlFormSubmissionResult.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append(String.valueOf(urlFormSubmissionResult.getUrl()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		URLFormSubmissionResultJSONParser urlFormSubmissionResultJSONParser =
			new URLFormSubmissionResultJSONParser();

		return urlFormSubmissionResultJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		URLFormSubmissionResult urlFormSubmissionResult) {

		if (urlFormSubmissionResult == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (urlFormSubmissionResult.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put("url", String.valueOf(urlFormSubmissionResult.getUrl()));
		}

		return map;
	}

	public static class URLFormSubmissionResultJSONParser
		extends BaseJSONParser<URLFormSubmissionResult> {

		@Override
		protected URLFormSubmissionResult createDTO() {
			return new URLFormSubmissionResult();
		}

		@Override
		protected URLFormSubmissionResult[] createDTOArray(int size) {
			return new URLFormSubmissionResult[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "url")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			URLFormSubmissionResult urlFormSubmissionResult,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					urlFormSubmissionResult.setUrl(
						FragmentInlineValueSerDes.toDTO(
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