/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ColorPaletteValue;
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
public class ColorPaletteValueSerDes {

	public static ColorPaletteValue toDTO(String json) {
		ColorPaletteValueJSONParser colorPaletteValueJSONParser =
			new ColorPaletteValueJSONParser();

		return colorPaletteValueJSONParser.parseToDTO(json);
	}

	public static ColorPaletteValue[] toDTOs(String json) {
		ColorPaletteValueJSONParser colorPaletteValueJSONParser =
			new ColorPaletteValueJSONParser();

		return colorPaletteValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ColorPaletteValue colorPaletteValue) {
		if (colorPaletteValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (colorPaletteValue.getColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"color\": ");

			sb.append("\"");

			sb.append(_escape(colorPaletteValue.getColor()));

			sb.append("\"");
		}

		if (colorPaletteValue.getCssClass() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClass\": ");

			sb.append("\"");

			sb.append(_escape(colorPaletteValue.getCssClass()));

			sb.append("\"");
		}

		if (colorPaletteValue.getRgbValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rgbValue\": ");

			sb.append("\"");

			sb.append(_escape(colorPaletteValue.getRgbValue()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ColorPaletteValueJSONParser colorPaletteValueJSONParser =
			new ColorPaletteValueJSONParser();

		return colorPaletteValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ColorPaletteValue colorPaletteValue) {

		if (colorPaletteValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (colorPaletteValue.getColor() == null) {
			map.put("color", null);
		}
		else {
			map.put("color", String.valueOf(colorPaletteValue.getColor()));
		}

		if (colorPaletteValue.getCssClass() == null) {
			map.put("cssClass", null);
		}
		else {
			map.put(
				"cssClass", String.valueOf(colorPaletteValue.getCssClass()));
		}

		if (colorPaletteValue.getRgbValue() == null) {
			map.put("rgbValue", null);
		}
		else {
			map.put(
				"rgbValue", String.valueOf(colorPaletteValue.getRgbValue()));
		}

		return map;
	}

	public static class ColorPaletteValueJSONParser
		extends BaseJSONParser<ColorPaletteValue> {

		@Override
		protected ColorPaletteValue createDTO() {
			return new ColorPaletteValue();
		}

		@Override
		protected ColorPaletteValue[] createDTOArray(int size) {
			return new ColorPaletteValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "color")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cssClass")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "rgbValue")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ColorPaletteValue colorPaletteValue, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "color")) {
				if (jsonParserFieldValue != null) {
					colorPaletteValue.setColor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cssClass")) {
				if (jsonParserFieldValue != null) {
					colorPaletteValue.setCssClass((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "rgbValue")) {
				if (jsonParserFieldValue != null) {
					colorPaletteValue.setRgbValue((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1428080893