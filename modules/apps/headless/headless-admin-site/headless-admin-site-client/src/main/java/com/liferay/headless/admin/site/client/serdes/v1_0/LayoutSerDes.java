/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.Layout;
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
public class LayoutSerDes {

	public static Layout toDTO(String json) {
		LayoutJSONParser layoutJSONParser = new LayoutJSONParser();

		return layoutJSONParser.parseToDTO(json);
	}

	public static Layout[] toDTOs(String json) {
		LayoutJSONParser layoutJSONParser = new LayoutJSONParser();

		return layoutJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Layout layout) {
		if (layout == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (layout.getAlign() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"align\": ");

			sb.append("\"");

			sb.append(layout.getAlign());

			sb.append("\"");
		}

		if (layout.getContentDisplay() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentDisplay\": ");

			sb.append("\"");

			sb.append(layout.getContentDisplay());

			sb.append("\"");
		}

		if (layout.getFlexWrap() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"flexWrap\": ");

			sb.append("\"");

			sb.append(layout.getFlexWrap());

			sb.append("\"");
		}

		if (layout.getJustify() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"justify\": ");

			sb.append("\"");

			sb.append(layout.getJustify());

			sb.append("\"");
		}

		if (layout.getWidthType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widthType\": ");

			sb.append("\"");

			sb.append(layout.getWidthType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		LayoutJSONParser layoutJSONParser = new LayoutJSONParser();

		return layoutJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Layout layout) {
		if (layout == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (layout.getAlign() == null) {
			map.put("align", null);
		}
		else {
			map.put("align", String.valueOf(layout.getAlign()));
		}

		if (layout.getContentDisplay() == null) {
			map.put("contentDisplay", null);
		}
		else {
			map.put(
				"contentDisplay", String.valueOf(layout.getContentDisplay()));
		}

		if (layout.getFlexWrap() == null) {
			map.put("flexWrap", null);
		}
		else {
			map.put("flexWrap", String.valueOf(layout.getFlexWrap()));
		}

		if (layout.getJustify() == null) {
			map.put("justify", null);
		}
		else {
			map.put("justify", String.valueOf(layout.getJustify()));
		}

		if (layout.getWidthType() == null) {
			map.put("widthType", null);
		}
		else {
			map.put("widthType", String.valueOf(layout.getWidthType()));
		}

		return map;
	}

	public static class LayoutJSONParser extends BaseJSONParser<Layout> {

		@Override
		protected Layout createDTO() {
			return new Layout();
		}

		@Override
		protected Layout[] createDTOArray(int size) {
			return new Layout[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "align")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentDisplay")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "flexWrap")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "justify")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "widthType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Layout layout, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "align")) {
				if (jsonParserFieldValue != null) {
					layout.setAlign(
						Layout.Align.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentDisplay")) {
				if (jsonParserFieldValue != null) {
					layout.setContentDisplay(
						Layout.ContentDisplay.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "flexWrap")) {
				if (jsonParserFieldValue != null) {
					layout.setFlexWrap(
						Layout.FlexWrap.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "justify")) {
				if (jsonParserFieldValue != null) {
					layout.setJustify(
						Layout.Justify.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widthType")) {
				if (jsonParserFieldValue != null) {
					layout.setWidthType(
						Layout.WidthType.create((String)jsonParserFieldValue));
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