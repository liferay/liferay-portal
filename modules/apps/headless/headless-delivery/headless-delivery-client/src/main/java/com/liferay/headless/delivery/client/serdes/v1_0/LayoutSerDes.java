/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.Layout;
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

		if (layout.getBorderColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderColor\": ");

			sb.append("\"");

			sb.append(_escape(layout.getBorderColor()));

			sb.append("\"");
		}

		if (layout.getBorderRadius() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderRadius\": ");

			sb.append("\"");

			sb.append(layout.getBorderRadius());

			sb.append("\"");
		}

		if (layout.getBorderWidth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderWidth\": ");

			sb.append(layout.getBorderWidth());
		}

		if (layout.getContainerType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"containerType\": ");

			sb.append("\"");

			sb.append(layout.getContainerType());

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

		if (layout.getMarginBottom() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginBottom\": ");

			sb.append(layout.getMarginBottom());
		}

		if (layout.getMarginLeft() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginLeft\": ");

			sb.append(layout.getMarginLeft());
		}

		if (layout.getMarginRight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginRight\": ");

			sb.append(layout.getMarginRight());
		}

		if (layout.getMarginTop() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginTop\": ");

			sb.append(layout.getMarginTop());
		}

		if (layout.getOpacity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"opacity\": ");

			sb.append(layout.getOpacity());
		}

		if (layout.getPaddingBottom() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingBottom\": ");

			sb.append(layout.getPaddingBottom());
		}

		if (layout.getPaddingHorizontal() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingHorizontal\": ");

			sb.append(layout.getPaddingHorizontal());
		}

		if (layout.getPaddingLeft() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingLeft\": ");

			sb.append(layout.getPaddingLeft());
		}

		if (layout.getPaddingRight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingRight\": ");

			sb.append(layout.getPaddingRight());
		}

		if (layout.getPaddingTop() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingTop\": ");

			sb.append(layout.getPaddingTop());
		}

		if (layout.getShadow() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shadow\": ");

			sb.append("\"");

			sb.append(layout.getShadow());

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

		if (layout.getBorderColor() == null) {
			map.put("borderColor", null);
		}
		else {
			map.put("borderColor", String.valueOf(layout.getBorderColor()));
		}

		if (layout.getBorderRadius() == null) {
			map.put("borderRadius", null);
		}
		else {
			map.put("borderRadius", String.valueOf(layout.getBorderRadius()));
		}

		if (layout.getBorderWidth() == null) {
			map.put("borderWidth", null);
		}
		else {
			map.put("borderWidth", String.valueOf(layout.getBorderWidth()));
		}

		if (layout.getContainerType() == null) {
			map.put("containerType", null);
		}
		else {
			map.put("containerType", String.valueOf(layout.getContainerType()));
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

		if (layout.getMarginBottom() == null) {
			map.put("marginBottom", null);
		}
		else {
			map.put("marginBottom", String.valueOf(layout.getMarginBottom()));
		}

		if (layout.getMarginLeft() == null) {
			map.put("marginLeft", null);
		}
		else {
			map.put("marginLeft", String.valueOf(layout.getMarginLeft()));
		}

		if (layout.getMarginRight() == null) {
			map.put("marginRight", null);
		}
		else {
			map.put("marginRight", String.valueOf(layout.getMarginRight()));
		}

		if (layout.getMarginTop() == null) {
			map.put("marginTop", null);
		}
		else {
			map.put("marginTop", String.valueOf(layout.getMarginTop()));
		}

		if (layout.getOpacity() == null) {
			map.put("opacity", null);
		}
		else {
			map.put("opacity", String.valueOf(layout.getOpacity()));
		}

		if (layout.getPaddingBottom() == null) {
			map.put("paddingBottom", null);
		}
		else {
			map.put("paddingBottom", String.valueOf(layout.getPaddingBottom()));
		}

		if (layout.getPaddingHorizontal() == null) {
			map.put("paddingHorizontal", null);
		}
		else {
			map.put(
				"paddingHorizontal",
				String.valueOf(layout.getPaddingHorizontal()));
		}

		if (layout.getPaddingLeft() == null) {
			map.put("paddingLeft", null);
		}
		else {
			map.put("paddingLeft", String.valueOf(layout.getPaddingLeft()));
		}

		if (layout.getPaddingRight() == null) {
			map.put("paddingRight", null);
		}
		else {
			map.put("paddingRight", String.valueOf(layout.getPaddingRight()));
		}

		if (layout.getPaddingTop() == null) {
			map.put("paddingTop", null);
		}
		else {
			map.put("paddingTop", String.valueOf(layout.getPaddingTop()));
		}

		if (layout.getShadow() == null) {
			map.put("shadow", null);
		}
		else {
			map.put("shadow", String.valueOf(layout.getShadow()));
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
			else if (Objects.equals(jsonParserFieldName, "borderColor")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "borderRadius")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "borderWidth")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "containerType")) {
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
			else if (Objects.equals(jsonParserFieldName, "marginBottom")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "marginLeft")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "marginRight")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "marginTop")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "opacity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paddingBottom")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paddingHorizontal")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paddingLeft")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paddingRight")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paddingTop")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shadow")) {
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
			else if (Objects.equals(jsonParserFieldName, "borderColor")) {
				if (jsonParserFieldValue != null) {
					layout.setBorderColor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "borderRadius")) {
				if (jsonParserFieldValue != null) {
					layout.setBorderRadius(
						Layout.BorderRadius.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "borderWidth")) {
				if (jsonParserFieldValue != null) {
					layout.setBorderWidth(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "containerType")) {
				if (jsonParserFieldValue != null) {
					layout.setContainerType(
						Layout.ContainerType.create(
							(String)jsonParserFieldValue));
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
			else if (Objects.equals(jsonParserFieldName, "marginBottom")) {
				if (jsonParserFieldValue != null) {
					layout.setMarginBottom(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marginLeft")) {
				if (jsonParserFieldValue != null) {
					layout.setMarginLeft(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marginRight")) {
				if (jsonParserFieldValue != null) {
					layout.setMarginRight(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marginTop")) {
				if (jsonParserFieldValue != null) {
					layout.setMarginTop(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "opacity")) {
				if (jsonParserFieldValue != null) {
					layout.setOpacity(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingBottom")) {
				if (jsonParserFieldValue != null) {
					layout.setPaddingBottom(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingHorizontal")) {
				if (jsonParserFieldValue != null) {
					layout.setPaddingHorizontal(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingLeft")) {
				if (jsonParserFieldValue != null) {
					layout.setPaddingLeft(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingRight")) {
				if (jsonParserFieldValue != null) {
					layout.setPaddingRight(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingTop")) {
				if (jsonParserFieldValue != null) {
					layout.setPaddingTop(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shadow")) {
				if (jsonParserFieldValue != null) {
					layout.setShadow(
						Layout.Shadow.create((String)jsonParserFieldValue));
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