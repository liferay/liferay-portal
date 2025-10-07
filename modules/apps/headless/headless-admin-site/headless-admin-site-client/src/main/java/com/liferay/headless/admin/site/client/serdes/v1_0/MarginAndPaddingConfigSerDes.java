/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.MarginAndPaddingConfig;
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
public class MarginAndPaddingConfigSerDes {

	public static MarginAndPaddingConfig toDTO(String json) {
		MarginAndPaddingConfigJSONParser marginAndPaddingConfigJSONParser =
			new MarginAndPaddingConfigJSONParser();

		return marginAndPaddingConfigJSONParser.parseToDTO(json);
	}

	public static MarginAndPaddingConfig[] toDTOs(String json) {
		MarginAndPaddingConfigJSONParser marginAndPaddingConfigJSONParser =
			new MarginAndPaddingConfigJSONParser();

		return marginAndPaddingConfigJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MarginAndPaddingConfig marginAndPaddingConfig) {
		if (marginAndPaddingConfig == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (marginAndPaddingConfig.getMargin() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"margin\": ");

			if (marginAndPaddingConfig.getMargin() instanceof String) {
				sb.append("\"");
				sb.append((String)marginAndPaddingConfig.getMargin());
				sb.append("\"");
			}
			else {
				sb.append(marginAndPaddingConfig.getMargin());
			}
		}

		if (marginAndPaddingConfig.getPadding() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"padding\": ");

			if (marginAndPaddingConfig.getPadding() instanceof String) {
				sb.append("\"");
				sb.append((String)marginAndPaddingConfig.getPadding());
				sb.append("\"");
			}
			else {
				sb.append(marginAndPaddingConfig.getPadding());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MarginAndPaddingConfigJSONParser marginAndPaddingConfigJSONParser =
			new MarginAndPaddingConfigJSONParser();

		return marginAndPaddingConfigJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		MarginAndPaddingConfig marginAndPaddingConfig) {

		if (marginAndPaddingConfig == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (marginAndPaddingConfig.getMargin() == null) {
			map.put("margin", null);
		}
		else {
			map.put(
				"margin", String.valueOf(marginAndPaddingConfig.getMargin()));
		}

		if (marginAndPaddingConfig.getPadding() == null) {
			map.put("padding", null);
		}
		else {
			map.put(
				"padding", String.valueOf(marginAndPaddingConfig.getPadding()));
		}

		return map;
	}

	public static class MarginAndPaddingConfigJSONParser
		extends BaseJSONParser<MarginAndPaddingConfig> {

		@Override
		protected MarginAndPaddingConfig createDTO() {
			return new MarginAndPaddingConfig();
		}

		@Override
		protected MarginAndPaddingConfig[] createDTOArray(int size) {
			return new MarginAndPaddingConfig[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "margin")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "padding")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			MarginAndPaddingConfig marginAndPaddingConfig,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "margin")) {
				if (jsonParserFieldValue != null) {
					marginAndPaddingConfig.setMargin(
						(Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "padding")) {
				if (jsonParserFieldValue != null) {
					marginAndPaddingConfig.setPadding(
						(Object)jsonParserFieldValue);
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