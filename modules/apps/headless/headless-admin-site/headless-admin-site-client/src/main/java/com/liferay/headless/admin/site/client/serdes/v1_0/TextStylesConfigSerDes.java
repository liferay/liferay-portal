/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.TextStylesConfig;
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
public class TextStylesConfigSerDes {

	public static TextStylesConfig toDTO(String json) {
		TextStylesConfigJSONParser textStylesConfigJSONParser =
			new TextStylesConfigJSONParser();

		return textStylesConfigJSONParser.parseToDTO(json);
	}

	public static TextStylesConfig[] toDTOs(String json) {
		TextStylesConfigJSONParser textStylesConfigJSONParser =
			new TextStylesConfigJSONParser();

		return textStylesConfigJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TextStylesConfig textStylesConfig) {
		if (textStylesConfig == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (textStylesConfig.getAlignment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"alignment\": ");

			sb.append("\"");

			sb.append(textStylesConfig.getAlignment());

			sb.append("\"");
		}

		if (textStylesConfig.getBold() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bold\": ");

			sb.append(textStylesConfig.getBold());
		}

		if (textStylesConfig.getColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"color\": ");

			sb.append("\"");

			sb.append(_escape(textStylesConfig.getColor()));

			sb.append("\"");
		}

		if (textStylesConfig.getFont() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"font\": ");

			sb.append("\"");

			sb.append(textStylesConfig.getFont());

			sb.append("\"");
		}

		if (textStylesConfig.getItalic() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"italic\": ");

			sb.append(textStylesConfig.getItalic());
		}

		if (textStylesConfig.getLetterSpacing() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"letterSpacing\": ");

			sb.append("\"");

			sb.append(_escape(textStylesConfig.getLetterSpacing()));

			sb.append("\"");
		}

		if (textStylesConfig.getLineHeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lineHeight\": ");

			sb.append("\"");

			sb.append(_escape(textStylesConfig.getLineHeight()));

			sb.append("\"");
		}

		if (textStylesConfig.getSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append("\"");

			sb.append(_escape(textStylesConfig.getSize()));

			sb.append("\"");
		}

		if (textStylesConfig.getTextDecoration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"textDecoration\": ");

			sb.append("\"");

			sb.append(textStylesConfig.getTextDecoration());

			sb.append("\"");
		}

		if (textStylesConfig.getWordSpacing() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"wordSpacing\": ");

			sb.append("\"");

			sb.append(_escape(textStylesConfig.getWordSpacing()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TextStylesConfigJSONParser textStylesConfigJSONParser =
			new TextStylesConfigJSONParser();

		return textStylesConfigJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TextStylesConfig textStylesConfig) {
		if (textStylesConfig == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (textStylesConfig.getAlignment() == null) {
			map.put("alignment", null);
		}
		else {
			map.put(
				"alignment", String.valueOf(textStylesConfig.getAlignment()));
		}

		if (textStylesConfig.getBold() == null) {
			map.put("bold", null);
		}
		else {
			map.put("bold", String.valueOf(textStylesConfig.getBold()));
		}

		if (textStylesConfig.getColor() == null) {
			map.put("color", null);
		}
		else {
			map.put("color", String.valueOf(textStylesConfig.getColor()));
		}

		if (textStylesConfig.getFont() == null) {
			map.put("font", null);
		}
		else {
			map.put("font", String.valueOf(textStylesConfig.getFont()));
		}

		if (textStylesConfig.getItalic() == null) {
			map.put("italic", null);
		}
		else {
			map.put("italic", String.valueOf(textStylesConfig.getItalic()));
		}

		if (textStylesConfig.getLetterSpacing() == null) {
			map.put("letterSpacing", null);
		}
		else {
			map.put(
				"letterSpacing",
				String.valueOf(textStylesConfig.getLetterSpacing()));
		}

		if (textStylesConfig.getLineHeight() == null) {
			map.put("lineHeight", null);
		}
		else {
			map.put(
				"lineHeight", String.valueOf(textStylesConfig.getLineHeight()));
		}

		if (textStylesConfig.getSize() == null) {
			map.put("size", null);
		}
		else {
			map.put("size", String.valueOf(textStylesConfig.getSize()));
		}

		if (textStylesConfig.getTextDecoration() == null) {
			map.put("textDecoration", null);
		}
		else {
			map.put(
				"textDecoration",
				String.valueOf(textStylesConfig.getTextDecoration()));
		}

		if (textStylesConfig.getWordSpacing() == null) {
			map.put("wordSpacing", null);
		}
		else {
			map.put(
				"wordSpacing",
				String.valueOf(textStylesConfig.getWordSpacing()));
		}

		return map;
	}

	public static class TextStylesConfigJSONParser
		extends BaseJSONParser<TextStylesConfig> {

		@Override
		protected TextStylesConfig createDTO() {
			return new TextStylesConfig();
		}

		@Override
		protected TextStylesConfig[] createDTOArray(int size) {
			return new TextStylesConfig[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "alignment")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "bold")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "color")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "font")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "italic")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "letterSpacing")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lineHeight")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "textDecoration")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "wordSpacing")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TextStylesConfig textStylesConfig, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "alignment")) {
				if (jsonParserFieldValue != null) {
					textStylesConfig.setAlignment(
						TextStylesConfig.Alignment.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "bold")) {
				if (jsonParserFieldValue != null) {
					textStylesConfig.setBold((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "color")) {
				if (jsonParserFieldValue != null) {
					textStylesConfig.setColor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "font")) {
				if (jsonParserFieldValue != null) {
					textStylesConfig.setFont(
						TextStylesConfig.Font.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "italic")) {
				if (jsonParserFieldValue != null) {
					textStylesConfig.setItalic((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "letterSpacing")) {
				if (jsonParserFieldValue != null) {
					textStylesConfig.setLetterSpacing(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lineHeight")) {
				if (jsonParserFieldValue != null) {
					textStylesConfig.setLineHeight(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				if (jsonParserFieldValue != null) {
					textStylesConfig.setSize((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "textDecoration")) {
				if (jsonParserFieldValue != null) {
					textStylesConfig.setTextDecoration(
						TextStylesConfig.TextDecoration.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "wordSpacing")) {
				if (jsonParserFieldValue != null) {
					textStylesConfig.setWordSpacing(
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