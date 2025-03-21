/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageTemplateSettings;
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
public class WidgetPageTemplateSettingsSerDes {

	public static WidgetPageTemplateSettings toDTO(String json) {
		WidgetPageTemplateSettingsJSONParser
			widgetPageTemplateSettingsJSONParser =
				new WidgetPageTemplateSettingsJSONParser();

		return widgetPageTemplateSettingsJSONParser.parseToDTO(json);
	}

	public static WidgetPageTemplateSettings[] toDTOs(String json) {
		WidgetPageTemplateSettingsJSONParser
			widgetPageTemplateSettingsJSONParser =
				new WidgetPageTemplateSettingsJSONParser();

		return widgetPageTemplateSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		WidgetPageTemplateSettings widgetPageTemplateSettings) {

		if (widgetPageTemplateSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (widgetPageTemplateSettings.getLayoutTemplateId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"layoutTemplateId\": ");

			sb.append("\"");

			sb.append(
				_escape(widgetPageTemplateSettings.getLayoutTemplateId()));

			sb.append("\"");
		}

		if (widgetPageTemplateSettings.getNavigationMenuSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuSettings\": ");

			sb.append(
				String.valueOf(
					widgetPageTemplateSettings.getNavigationMenuSettings()));
		}

		if (widgetPageTemplateSettings.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(widgetPageTemplateSettings.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WidgetPageTemplateSettingsJSONParser
			widgetPageTemplateSettingsJSONParser =
				new WidgetPageTemplateSettingsJSONParser();

		return widgetPageTemplateSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WidgetPageTemplateSettings widgetPageTemplateSettings) {

		if (widgetPageTemplateSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (widgetPageTemplateSettings.getLayoutTemplateId() == null) {
			map.put("layoutTemplateId", null);
		}
		else {
			map.put(
				"layoutTemplateId",
				String.valueOf(
					widgetPageTemplateSettings.getLayoutTemplateId()));
		}

		if (widgetPageTemplateSettings.getNavigationMenuSettings() == null) {
			map.put("navigationMenuSettings", null);
		}
		else {
			map.put(
				"navigationMenuSettings",
				String.valueOf(
					widgetPageTemplateSettings.getNavigationMenuSettings()));
		}

		if (widgetPageTemplateSettings.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(widgetPageTemplateSettings.getType()));
		}

		return map;
	}

	public static class WidgetPageTemplateSettingsJSONParser
		extends BaseJSONParser<WidgetPageTemplateSettings> {

		@Override
		protected WidgetPageTemplateSettings createDTO() {
			return new WidgetPageTemplateSettings();
		}

		@Override
		protected WidgetPageTemplateSettings[] createDTOArray(int size) {
			return new WidgetPageTemplateSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "layoutTemplateId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationMenuSettings")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WidgetPageTemplateSettings widgetPageTemplateSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "layoutTemplateId")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplateSettings.setLayoutTemplateId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationMenuSettings")) {

				if (jsonParserFieldValue != null) {
					widgetPageTemplateSettings.setNavigationMenuSettings(
						NavigationMenuSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					widgetPageTemplateSettings.setType(
						WidgetPageTemplateSettings.Type.create(
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