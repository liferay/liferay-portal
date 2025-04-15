/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CustomCSSViewport;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class CustomCSSViewportSerDes {

	public static CustomCSSViewport toDTO(String json) {
		CustomCSSViewportJSONParser customCSSViewportJSONParser =
			new CustomCSSViewportJSONParser();

		return customCSSViewportJSONParser.parseToDTO(json);
	}

	public static CustomCSSViewport[] toDTOs(String json) {
		CustomCSSViewportJSONParser customCSSViewportJSONParser =
			new CustomCSSViewportJSONParser();

		return customCSSViewportJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CustomCSSViewport customCSSViewport) {
		if (customCSSViewport == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (customCSSViewport.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(customCSSViewport.getCustomCSS()));

			sb.append("\"");
		}

		if (customCSSViewport.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(customCSSViewport.getId()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CustomCSSViewportJSONParser customCSSViewportJSONParser =
			new CustomCSSViewportJSONParser();

		return customCSSViewportJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		CustomCSSViewport customCSSViewport) {

		if (customCSSViewport == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (customCSSViewport.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS", String.valueOf(customCSSViewport.getCustomCSS()));
		}

		if (customCSSViewport.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(customCSSViewport.getId()));
		}

		return map;
	}

	public static class CustomCSSViewportJSONParser
		extends BaseJSONParser<CustomCSSViewport> {

		@Override
		protected CustomCSSViewport createDTO() {
			return new CustomCSSViewport();
		}

		@Override
		protected CustomCSSViewport[] createDTOArray(int size) {
			return new CustomCSSViewport[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "customCSS")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CustomCSSViewport customCSSViewport, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					customCSSViewport.setCustomCSS(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					customCSSViewport.setId((String)jsonParserFieldValue);
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