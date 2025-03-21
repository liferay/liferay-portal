/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageTemplateSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.PageTemplateSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageTemplateSettings;
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
public class PageTemplateSettingsSerDes {

	public static PageTemplateSettings toDTO(String json) {
		PageTemplateSettingsJSONParser pageTemplateSettingsJSONParser =
			new PageTemplateSettingsJSONParser();

		return pageTemplateSettingsJSONParser.parseToDTO(json);
	}

	public static PageTemplateSettings[] toDTOs(String json) {
		PageTemplateSettingsJSONParser pageTemplateSettingsJSONParser =
			new PageTemplateSettingsJSONParser();

		return pageTemplateSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageTemplateSettings pageTemplateSettings) {
		if (pageTemplateSettings == null) {
			return "null";
		}

		PageTemplateSettings.Type type = pageTemplateSettings.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("ContentPageTemplate")) {
				return ContentPageTemplateSettingsSerDes.toJSON(
					(ContentPageTemplateSettings)pageTemplateSettings);
			}

			if (typeString.equals("WidgetPageTemplate")) {
				return WidgetPageTemplateSettingsSerDes.toJSON(
					(WidgetPageTemplateSettings)pageTemplateSettings);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		PageTemplateSettingsJSONParser pageTemplateSettingsJSONParser =
			new PageTemplateSettingsJSONParser();

		return pageTemplateSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageTemplateSettings pageTemplateSettings) {

		if (pageTemplateSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageTemplateSettings.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(pageTemplateSettings.getType()));
		}

		return map;
	}

	public static class PageTemplateSettingsJSONParser
		extends BaseJSONParser<PageTemplateSettings> {

		@Override
		protected PageTemplateSettings createDTO() {
			return null;
		}

		@Override
		protected PageTemplateSettings[] createDTOArray(int size) {
			return new PageTemplateSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public PageTemplateSettings parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("ContentPageTemplate")) {
					return ContentPageTemplateSettings.toDTO(json);
				}

				if (typeString.equals("WidgetPageTemplate")) {
					return WidgetPageTemplateSettings.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			PageTemplateSettings pageTemplateSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					pageTemplateSettings.setType(
						PageTemplateSettings.Type.create(
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