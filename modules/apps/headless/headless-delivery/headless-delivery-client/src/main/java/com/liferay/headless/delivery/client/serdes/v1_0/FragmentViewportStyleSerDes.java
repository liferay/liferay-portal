/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.FragmentViewportStyle;
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
public class FragmentViewportStyleSerDes {

	public static FragmentViewportStyle toDTO(String json) {
		FragmentViewportStyleJSONParser fragmentViewportStyleJSONParser =
			new FragmentViewportStyleJSONParser();

		return fragmentViewportStyleJSONParser.parseToDTO(json);
	}

	public static FragmentViewportStyle[] toDTOs(String json) {
		FragmentViewportStyleJSONParser fragmentViewportStyleJSONParser =
			new FragmentViewportStyleJSONParser();

		return fragmentViewportStyleJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FragmentViewportStyle fragmentViewportStyle) {
		if (fragmentViewportStyle == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentViewportStyle.getBackgroundColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundColor\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getBackgroundColor()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getBorderColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderColor\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getBorderColor()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getBorderRadius() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderRadius\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getBorderRadius()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getBorderWidth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderWidth\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getBorderWidth()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getFontFamily() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fontFamily\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getFontFamily()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getFontSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fontSize\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getFontSize()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getFontWeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fontWeight\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getFontWeight()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getHeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"height\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getHeight()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getHidden() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hidden\": ");

			sb.append(fragmentViewportStyle.getHidden());
		}

		if (fragmentViewportStyle.getMarginBottom() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginBottom\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getMarginBottom()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getMarginLeft() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginLeft\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getMarginLeft()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getMarginRight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginRight\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getMarginRight()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getMarginTop() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginTop\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getMarginTop()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getMaxHeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxHeight\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getMaxHeight()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getMaxWidth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxWidth\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getMaxWidth()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getMinHeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minHeight\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getMinHeight()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getMinWidth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minWidth\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getMinWidth()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getOpacity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"opacity\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getOpacity()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getOverflow() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"overflow\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getOverflow()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getPaddingBottom() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingBottom\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getPaddingBottom()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getPaddingLeft() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingLeft\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getPaddingLeft()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getPaddingRight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingRight\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getPaddingRight()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getPaddingTop() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingTop\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getPaddingTop()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getShadow() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shadow\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getShadow()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getTextAlign() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"textAlign\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getTextAlign()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getTextColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"textColor\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getTextColor()));

			sb.append("\"");
		}

		if (fragmentViewportStyle.getWidth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"width\": ");

			sb.append("\"");

			sb.append(_escape(fragmentViewportStyle.getWidth()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentViewportStyleJSONParser fragmentViewportStyleJSONParser =
			new FragmentViewportStyleJSONParser();

		return fragmentViewportStyleJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentViewportStyle fragmentViewportStyle) {

		if (fragmentViewportStyle == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentViewportStyle.getBackgroundColor() == null) {
			map.put("backgroundColor", null);
		}
		else {
			map.put(
				"backgroundColor",
				String.valueOf(fragmentViewportStyle.getBackgroundColor()));
		}

		if (fragmentViewportStyle.getBorderColor() == null) {
			map.put("borderColor", null);
		}
		else {
			map.put(
				"borderColor",
				String.valueOf(fragmentViewportStyle.getBorderColor()));
		}

		if (fragmentViewportStyle.getBorderRadius() == null) {
			map.put("borderRadius", null);
		}
		else {
			map.put(
				"borderRadius",
				String.valueOf(fragmentViewportStyle.getBorderRadius()));
		}

		if (fragmentViewportStyle.getBorderWidth() == null) {
			map.put("borderWidth", null);
		}
		else {
			map.put(
				"borderWidth",
				String.valueOf(fragmentViewportStyle.getBorderWidth()));
		}

		if (fragmentViewportStyle.getFontFamily() == null) {
			map.put("fontFamily", null);
		}
		else {
			map.put(
				"fontFamily",
				String.valueOf(fragmentViewportStyle.getFontFamily()));
		}

		if (fragmentViewportStyle.getFontSize() == null) {
			map.put("fontSize", null);
		}
		else {
			map.put(
				"fontSize",
				String.valueOf(fragmentViewportStyle.getFontSize()));
		}

		if (fragmentViewportStyle.getFontWeight() == null) {
			map.put("fontWeight", null);
		}
		else {
			map.put(
				"fontWeight",
				String.valueOf(fragmentViewportStyle.getFontWeight()));
		}

		if (fragmentViewportStyle.getHeight() == null) {
			map.put("height", null);
		}
		else {
			map.put(
				"height", String.valueOf(fragmentViewportStyle.getHeight()));
		}

		if (fragmentViewportStyle.getHidden() == null) {
			map.put("hidden", null);
		}
		else {
			map.put(
				"hidden", String.valueOf(fragmentViewportStyle.getHidden()));
		}

		if (fragmentViewportStyle.getMarginBottom() == null) {
			map.put("marginBottom", null);
		}
		else {
			map.put(
				"marginBottom",
				String.valueOf(fragmentViewportStyle.getMarginBottom()));
		}

		if (fragmentViewportStyle.getMarginLeft() == null) {
			map.put("marginLeft", null);
		}
		else {
			map.put(
				"marginLeft",
				String.valueOf(fragmentViewportStyle.getMarginLeft()));
		}

		if (fragmentViewportStyle.getMarginRight() == null) {
			map.put("marginRight", null);
		}
		else {
			map.put(
				"marginRight",
				String.valueOf(fragmentViewportStyle.getMarginRight()));
		}

		if (fragmentViewportStyle.getMarginTop() == null) {
			map.put("marginTop", null);
		}
		else {
			map.put(
				"marginTop",
				String.valueOf(fragmentViewportStyle.getMarginTop()));
		}

		if (fragmentViewportStyle.getMaxHeight() == null) {
			map.put("maxHeight", null);
		}
		else {
			map.put(
				"maxHeight",
				String.valueOf(fragmentViewportStyle.getMaxHeight()));
		}

		if (fragmentViewportStyle.getMaxWidth() == null) {
			map.put("maxWidth", null);
		}
		else {
			map.put(
				"maxWidth",
				String.valueOf(fragmentViewportStyle.getMaxWidth()));
		}

		if (fragmentViewportStyle.getMinHeight() == null) {
			map.put("minHeight", null);
		}
		else {
			map.put(
				"minHeight",
				String.valueOf(fragmentViewportStyle.getMinHeight()));
		}

		if (fragmentViewportStyle.getMinWidth() == null) {
			map.put("minWidth", null);
		}
		else {
			map.put(
				"minWidth",
				String.valueOf(fragmentViewportStyle.getMinWidth()));
		}

		if (fragmentViewportStyle.getOpacity() == null) {
			map.put("opacity", null);
		}
		else {
			map.put(
				"opacity", String.valueOf(fragmentViewportStyle.getOpacity()));
		}

		if (fragmentViewportStyle.getOverflow() == null) {
			map.put("overflow", null);
		}
		else {
			map.put(
				"overflow",
				String.valueOf(fragmentViewportStyle.getOverflow()));
		}

		if (fragmentViewportStyle.getPaddingBottom() == null) {
			map.put("paddingBottom", null);
		}
		else {
			map.put(
				"paddingBottom",
				String.valueOf(fragmentViewportStyle.getPaddingBottom()));
		}

		if (fragmentViewportStyle.getPaddingLeft() == null) {
			map.put("paddingLeft", null);
		}
		else {
			map.put(
				"paddingLeft",
				String.valueOf(fragmentViewportStyle.getPaddingLeft()));
		}

		if (fragmentViewportStyle.getPaddingRight() == null) {
			map.put("paddingRight", null);
		}
		else {
			map.put(
				"paddingRight",
				String.valueOf(fragmentViewportStyle.getPaddingRight()));
		}

		if (fragmentViewportStyle.getPaddingTop() == null) {
			map.put("paddingTop", null);
		}
		else {
			map.put(
				"paddingTop",
				String.valueOf(fragmentViewportStyle.getPaddingTop()));
		}

		if (fragmentViewportStyle.getShadow() == null) {
			map.put("shadow", null);
		}
		else {
			map.put(
				"shadow", String.valueOf(fragmentViewportStyle.getShadow()));
		}

		if (fragmentViewportStyle.getTextAlign() == null) {
			map.put("textAlign", null);
		}
		else {
			map.put(
				"textAlign",
				String.valueOf(fragmentViewportStyle.getTextAlign()));
		}

		if (fragmentViewportStyle.getTextColor() == null) {
			map.put("textColor", null);
		}
		else {
			map.put(
				"textColor",
				String.valueOf(fragmentViewportStyle.getTextColor()));
		}

		if (fragmentViewportStyle.getWidth() == null) {
			map.put("width", null);
		}
		else {
			map.put("width", String.valueOf(fragmentViewportStyle.getWidth()));
		}

		return map;
	}

	public static class FragmentViewportStyleJSONParser
		extends BaseJSONParser<FragmentViewportStyle> {

		@Override
		protected FragmentViewportStyle createDTO() {
			return new FragmentViewportStyle();
		}

		@Override
		protected FragmentViewportStyle[] createDTOArray(int size) {
			return new FragmentViewportStyle[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "backgroundColor")) {
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
			else if (Objects.equals(jsonParserFieldName, "fontFamily")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fontSize")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fontWeight")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "height")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "hidden")) {
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
			else if (Objects.equals(jsonParserFieldName, "maxHeight")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "maxWidth")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "minHeight")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "minWidth")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "opacity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "overflow")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "paddingBottom")) {
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
			else if (Objects.equals(jsonParserFieldName, "textAlign")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "textColor")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "width")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentViewportStyle fragmentViewportStyle,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "backgroundColor")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setBackgroundColor(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "borderColor")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setBorderColor(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "borderRadius")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setBorderRadius(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "borderWidth")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setBorderWidth(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fontFamily")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setFontFamily(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fontSize")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setFontSize(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fontWeight")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setFontWeight(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "height")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setHeight(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "hidden")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setHidden(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marginBottom")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setMarginBottom(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marginLeft")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setMarginLeft(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marginRight")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setMarginRight(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marginTop")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setMarginTop(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "maxHeight")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setMaxHeight(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "maxWidth")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setMaxWidth(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "minHeight")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setMinHeight(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "minWidth")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setMinWidth(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "opacity")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setOpacity(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "overflow")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setOverflow(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingBottom")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setPaddingBottom(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingLeft")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setPaddingLeft(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingRight")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setPaddingRight(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingTop")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setPaddingTop(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shadow")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setShadow(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "textAlign")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setTextAlign(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "textColor")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setTextColor(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "width")) {
				if (jsonParserFieldValue != null) {
					fragmentViewportStyle.setWidth(
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