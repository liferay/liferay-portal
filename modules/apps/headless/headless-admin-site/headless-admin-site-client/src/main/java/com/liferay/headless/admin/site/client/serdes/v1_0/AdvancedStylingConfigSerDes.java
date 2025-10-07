/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.AdvancedStylingConfig;
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
public class AdvancedStylingConfigSerDes {

	public static AdvancedStylingConfig toDTO(String json) {
		AdvancedStylingConfigJSONParser advancedStylingConfigJSONParser =
			new AdvancedStylingConfigJSONParser();

		return advancedStylingConfigJSONParser.parseToDTO(json);
	}

	public static AdvancedStylingConfig[] toDTOs(String json) {
		AdvancedStylingConfigJSONParser advancedStylingConfigJSONParser =
			new AdvancedStylingConfigJSONParser();

		return advancedStylingConfigJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AdvancedStylingConfig advancedStylingConfig) {
		if (advancedStylingConfig == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (advancedStylingConfig.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(advancedStylingConfig.getCustomCSS()));

			sb.append("\"");
		}

		if (advancedStylingConfig.getCustomCSSClassNames() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSClassNames\": ");

			sb.append("\"");

			sb.append(_escape(advancedStylingConfig.getCustomCSSClassNames()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AdvancedStylingConfigJSONParser advancedStylingConfigJSONParser =
			new AdvancedStylingConfigJSONParser();

		return advancedStylingConfigJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AdvancedStylingConfig advancedStylingConfig) {

		if (advancedStylingConfig == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (advancedStylingConfig.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(advancedStylingConfig.getCustomCSS()));
		}

		if (advancedStylingConfig.getCustomCSSClassNames() == null) {
			map.put("customCSSClassNames", null);
		}
		else {
			map.put(
				"customCSSClassNames",
				String.valueOf(advancedStylingConfig.getCustomCSSClassNames()));
		}

		return map;
	}

	public static class AdvancedStylingConfigJSONParser
		extends BaseJSONParser<AdvancedStylingConfig> {

		@Override
		protected AdvancedStylingConfig createDTO() {
			return new AdvancedStylingConfig();
		}

		@Override
		protected AdvancedStylingConfig[] createDTOArray(int size) {
			return new AdvancedStylingConfig[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "customCSS")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "customCSSClassNames")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AdvancedStylingConfig advancedStylingConfig,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					advancedStylingConfig.setCustomCSS(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "customCSSClassNames")) {

				if (jsonParserFieldValue != null) {
					advancedStylingConfig.setCustomCSSClassNames(
						(String)jsonParserFieldValue);
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