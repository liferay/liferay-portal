/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentStyle;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FragmentStyleSerDes {

	public static FragmentStyle toDTO(String json) {
		FragmentStyleJSONParser fragmentStyleJSONParser =
			new FragmentStyleJSONParser();

		return fragmentStyleJSONParser.parseToDTO(json);
	}

	public static FragmentStyle[] toDTOs(String json) {
		FragmentStyleJSONParser fragmentStyleJSONParser =
			new FragmentStyleJSONParser();

		return fragmentStyleJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FragmentStyle fragmentStyle) {
		if (fragmentStyle == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentStyle.getBackgroundColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundColor\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getBackgroundColor()));

			sb.append("\"");
		}

		if (fragmentStyle.getBackgroundFragmentImage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundFragmentImage\": ");

			sb.append(
				String.valueOf(fragmentStyle.getBackgroundFragmentImage()));
		}

		if (fragmentStyle.getBorderColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderColor\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getBorderColor()));

			sb.append("\"");
		}

		if (fragmentStyle.getBorderRadius() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderRadius\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getBorderRadius()));

			sb.append("\"");
		}

		if (fragmentStyle.getBorderWidth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderWidth\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getBorderWidth()));

			sb.append("\"");
		}

		if (fragmentStyle.getFontFamily() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fontFamily\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getFontFamily()));

			sb.append("\"");
		}

		if (fragmentStyle.getFontSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fontSize\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getFontSize()));

			sb.append("\"");
		}

		if (fragmentStyle.getFontWeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fontWeight\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getFontWeight()));

			sb.append("\"");
		}

		if (fragmentStyle.getHeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"height\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getHeight()));

			sb.append("\"");
		}

		if (fragmentStyle.getHidden() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hidden\": ");

			sb.append(fragmentStyle.getHidden());
		}

		if (fragmentStyle.getMarginBottom() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginBottom\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getMarginBottom()));

			sb.append("\"");
		}

		if (fragmentStyle.getMarginLeft() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginLeft\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getMarginLeft()));

			sb.append("\"");
		}

		if (fragmentStyle.getMarginRight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginRight\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getMarginRight()));

			sb.append("\"");
		}

		if (fragmentStyle.getMarginTop() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginTop\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getMarginTop()));

			sb.append("\"");
		}

		if (fragmentStyle.getMaxHeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxHeight\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getMaxHeight()));

			sb.append("\"");
		}

		if (fragmentStyle.getMaxWidth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxWidth\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getMaxWidth()));

			sb.append("\"");
		}

		if (fragmentStyle.getMinHeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minHeight\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getMinHeight()));

			sb.append("\"");
		}

		if (fragmentStyle.getMinWidth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minWidth\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getMinWidth()));

			sb.append("\"");
		}

		if (fragmentStyle.getOpacity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"opacity\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getOpacity()));

			sb.append("\"");
		}

		if (fragmentStyle.getOverflow() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"overflow\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getOverflow()));

			sb.append("\"");
		}

		if (fragmentStyle.getPaddingBottom() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingBottom\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getPaddingBottom()));

			sb.append("\"");
		}

		if (fragmentStyle.getPaddingLeft() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingLeft\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getPaddingLeft()));

			sb.append("\"");
		}

		if (fragmentStyle.getPaddingRight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingRight\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getPaddingRight()));

			sb.append("\"");
		}

		if (fragmentStyle.getPaddingTop() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"paddingTop\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getPaddingTop()));

			sb.append("\"");
		}

		if (fragmentStyle.getShadow() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shadow\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getShadow()));

			sb.append("\"");
		}

		if (fragmentStyle.getTextAlign() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"textAlign\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getTextAlign()));

			sb.append("\"");
		}

		if (fragmentStyle.getTextColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"textColor\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getTextColor()));

			sb.append("\"");
		}

		if (fragmentStyle.getWidth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"width\": ");

			sb.append("\"");

			sb.append(_escape(fragmentStyle.getWidth()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentStyleJSONParser fragmentStyleJSONParser =
			new FragmentStyleJSONParser();

		return fragmentStyleJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FragmentStyle fragmentStyle) {
		if (fragmentStyle == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentStyle.getBackgroundColor() == null) {
			map.put("backgroundColor", null);
		}
		else {
			map.put(
				"backgroundColor",
				String.valueOf(fragmentStyle.getBackgroundColor()));
		}

		if (fragmentStyle.getBackgroundFragmentImage() == null) {
			map.put("backgroundFragmentImage", null);
		}
		else {
			map.put(
				"backgroundFragmentImage",
				String.valueOf(fragmentStyle.getBackgroundFragmentImage()));
		}

		if (fragmentStyle.getBorderColor() == null) {
			map.put("borderColor", null);
		}
		else {
			map.put(
				"borderColor", String.valueOf(fragmentStyle.getBorderColor()));
		}

		if (fragmentStyle.getBorderRadius() == null) {
			map.put("borderRadius", null);
		}
		else {
			map.put(
				"borderRadius",
				String.valueOf(fragmentStyle.getBorderRadius()));
		}

		if (fragmentStyle.getBorderWidth() == null) {
			map.put("borderWidth", null);
		}
		else {
			map.put(
				"borderWidth", String.valueOf(fragmentStyle.getBorderWidth()));
		}

		if (fragmentStyle.getFontFamily() == null) {
			map.put("fontFamily", null);
		}
		else {
			map.put(
				"fontFamily", String.valueOf(fragmentStyle.getFontFamily()));
		}

		if (fragmentStyle.getFontSize() == null) {
			map.put("fontSize", null);
		}
		else {
			map.put("fontSize", String.valueOf(fragmentStyle.getFontSize()));
		}

		if (fragmentStyle.getFontWeight() == null) {
			map.put("fontWeight", null);
		}
		else {
			map.put(
				"fontWeight", String.valueOf(fragmentStyle.getFontWeight()));
		}

		if (fragmentStyle.getHeight() == null) {
			map.put("height", null);
		}
		else {
			map.put("height", String.valueOf(fragmentStyle.getHeight()));
		}

		if (fragmentStyle.getHidden() == null) {
			map.put("hidden", null);
		}
		else {
			map.put("hidden", String.valueOf(fragmentStyle.getHidden()));
		}

		if (fragmentStyle.getMarginBottom() == null) {
			map.put("marginBottom", null);
		}
		else {
			map.put(
				"marginBottom",
				String.valueOf(fragmentStyle.getMarginBottom()));
		}

		if (fragmentStyle.getMarginLeft() == null) {
			map.put("marginLeft", null);
		}
		else {
			map.put(
				"marginLeft", String.valueOf(fragmentStyle.getMarginLeft()));
		}

		if (fragmentStyle.getMarginRight() == null) {
			map.put("marginRight", null);
		}
		else {
			map.put(
				"marginRight", String.valueOf(fragmentStyle.getMarginRight()));
		}

		if (fragmentStyle.getMarginTop() == null) {
			map.put("marginTop", null);
		}
		else {
			map.put("marginTop", String.valueOf(fragmentStyle.getMarginTop()));
		}

		if (fragmentStyle.getMaxHeight() == null) {
			map.put("maxHeight", null);
		}
		else {
			map.put("maxHeight", String.valueOf(fragmentStyle.getMaxHeight()));
		}

		if (fragmentStyle.getMaxWidth() == null) {
			map.put("maxWidth", null);
		}
		else {
			map.put("maxWidth", String.valueOf(fragmentStyle.getMaxWidth()));
		}

		if (fragmentStyle.getMinHeight() == null) {
			map.put("minHeight", null);
		}
		else {
			map.put("minHeight", String.valueOf(fragmentStyle.getMinHeight()));
		}

		if (fragmentStyle.getMinWidth() == null) {
			map.put("minWidth", null);
		}
		else {
			map.put("minWidth", String.valueOf(fragmentStyle.getMinWidth()));
		}

		if (fragmentStyle.getOpacity() == null) {
			map.put("opacity", null);
		}
		else {
			map.put("opacity", String.valueOf(fragmentStyle.getOpacity()));
		}

		if (fragmentStyle.getOverflow() == null) {
			map.put("overflow", null);
		}
		else {
			map.put("overflow", String.valueOf(fragmentStyle.getOverflow()));
		}

		if (fragmentStyle.getPaddingBottom() == null) {
			map.put("paddingBottom", null);
		}
		else {
			map.put(
				"paddingBottom",
				String.valueOf(fragmentStyle.getPaddingBottom()));
		}

		if (fragmentStyle.getPaddingLeft() == null) {
			map.put("paddingLeft", null);
		}
		else {
			map.put(
				"paddingLeft", String.valueOf(fragmentStyle.getPaddingLeft()));
		}

		if (fragmentStyle.getPaddingRight() == null) {
			map.put("paddingRight", null);
		}
		else {
			map.put(
				"paddingRight",
				String.valueOf(fragmentStyle.getPaddingRight()));
		}

		if (fragmentStyle.getPaddingTop() == null) {
			map.put("paddingTop", null);
		}
		else {
			map.put(
				"paddingTop", String.valueOf(fragmentStyle.getPaddingTop()));
		}

		if (fragmentStyle.getShadow() == null) {
			map.put("shadow", null);
		}
		else {
			map.put("shadow", String.valueOf(fragmentStyle.getShadow()));
		}

		if (fragmentStyle.getTextAlign() == null) {
			map.put("textAlign", null);
		}
		else {
			map.put("textAlign", String.valueOf(fragmentStyle.getTextAlign()));
		}

		if (fragmentStyle.getTextColor() == null) {
			map.put("textColor", null);
		}
		else {
			map.put("textColor", String.valueOf(fragmentStyle.getTextColor()));
		}

		if (fragmentStyle.getWidth() == null) {
			map.put("width", null);
		}
		else {
			map.put("width", String.valueOf(fragmentStyle.getWidth()));
		}

		return map;
	}

	public static class FragmentStyleJSONParser
		extends BaseJSONParser<FragmentStyle> {

		@Override
		protected FragmentStyle createDTO() {
			return new FragmentStyle();
		}

		@Override
		protected FragmentStyle[] createDTOArray(int size) {
			return new FragmentStyle[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "backgroundColor")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "backgroundFragmentImage")) {

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
			FragmentStyle fragmentStyle, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "backgroundColor")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setBackgroundColor(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "backgroundFragmentImage")) {

				if (jsonParserFieldValue != null) {
					fragmentStyle.setBackgroundFragmentImage(
						FragmentImageSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "borderColor")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setBorderColor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "borderRadius")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setBorderRadius((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "borderWidth")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setBorderWidth((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fontFamily")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setFontFamily((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fontSize")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setFontSize((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fontWeight")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setFontWeight((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "height")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setHeight((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "hidden")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setHidden((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marginBottom")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setMarginBottom((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marginLeft")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setMarginLeft((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marginRight")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setMarginRight((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marginTop")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setMarginTop((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "maxHeight")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setMaxHeight((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "maxWidth")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setMaxWidth((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "minHeight")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setMinHeight((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "minWidth")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setMinWidth((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "opacity")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setOpacity((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "overflow")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setOverflow((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingBottom")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setPaddingBottom(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingLeft")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setPaddingLeft((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingRight")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setPaddingRight((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "paddingTop")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setPaddingTop((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shadow")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setShadow((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "textAlign")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setTextAlign((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "textColor")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setTextColor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "width")) {
				if (jsonParserFieldValue != null) {
					fragmentStyle.setWidth((String)jsonParserFieldValue);
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