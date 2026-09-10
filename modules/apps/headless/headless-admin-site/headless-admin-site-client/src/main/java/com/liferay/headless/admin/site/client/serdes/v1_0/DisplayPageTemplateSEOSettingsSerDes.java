/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateSEOSettings;
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
public class DisplayPageTemplateSEOSettingsSerDes {

	public static DisplayPageTemplateSEOSettings toDTO(String json) {
		DisplayPageTemplateSEOSettingsJSONParser
			displayPageTemplateSEOSettingsJSONParser =
				new DisplayPageTemplateSEOSettingsJSONParser();

		return displayPageTemplateSEOSettingsJSONParser.parseToDTO(json);
	}

	public static DisplayPageTemplateSEOSettings[] toDTOs(String json) {
		DisplayPageTemplateSEOSettingsJSONParser
			displayPageTemplateSEOSettingsJSONParser =
				new DisplayPageTemplateSEOSettingsJSONParser();

		return displayPageTemplateSEOSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DisplayPageTemplateSEOSettings displayPageTemplateSEOSettings) {

		if (displayPageTemplateSEOSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (displayPageTemplateSEOSettings.getDescriptionTemplate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptionTemplate\": ");

			sb.append("\"");

			sb.append(
				_escape(
					displayPageTemplateSEOSettings.getDescriptionTemplate()));

			sb.append("\"");
		}

		if (displayPageTemplateSEOSettings.getHtmlTitleTemplate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"htmlTitleTemplate\": ");

			sb.append("\"");

			sb.append(
				_escape(displayPageTemplateSEOSettings.getHtmlTitleTemplate()));

			sb.append("\"");
		}

		if (displayPageTemplateSEOSettings.getRobots_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"robots_i18n\": ");

			sb.append(_toJSON(displayPageTemplateSEOSettings.getRobots_i18n()));
		}

		if (displayPageTemplateSEOSettings.getSitemapSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitemapSettings\": ");

			sb.append(
				String.valueOf(
					displayPageTemplateSEOSettings.getSitemapSettings()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DisplayPageTemplateSEOSettingsJSONParser
			displayPageTemplateSEOSettingsJSONParser =
				new DisplayPageTemplateSEOSettingsJSONParser();

		return displayPageTemplateSEOSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DisplayPageTemplateSEOSettings displayPageTemplateSEOSettings) {

		if (displayPageTemplateSEOSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (displayPageTemplateSEOSettings.getDescriptionTemplate() == null) {
			map.put("descriptionTemplate", null);
		}
		else {
			map.put(
				"descriptionTemplate",
				String.valueOf(
					displayPageTemplateSEOSettings.getDescriptionTemplate()));
		}

		if (displayPageTemplateSEOSettings.getHtmlTitleTemplate() == null) {
			map.put("htmlTitleTemplate", null);
		}
		else {
			map.put(
				"htmlTitleTemplate",
				String.valueOf(
					displayPageTemplateSEOSettings.getHtmlTitleTemplate()));
		}

		if (displayPageTemplateSEOSettings.getRobots_i18n() == null) {
			map.put("robots_i18n", null);
		}
		else {
			map.put(
				"robots_i18n",
				String.valueOf(
					displayPageTemplateSEOSettings.getRobots_i18n()));
		}

		if (displayPageTemplateSEOSettings.getSitemapSettings() == null) {
			map.put("sitemapSettings", null);
		}
		else {
			map.put(
				"sitemapSettings",
				String.valueOf(
					displayPageTemplateSEOSettings.getSitemapSettings()));
		}

		return map;
	}

	public static class DisplayPageTemplateSEOSettingsJSONParser
		extends BaseJSONParser<DisplayPageTemplateSEOSettings> {

		@Override
		protected DisplayPageTemplateSEOSettings createDTO() {
			return new DisplayPageTemplateSEOSettings();
		}

		@Override
		protected DisplayPageTemplateSEOSettings[] createDTOArray(int size) {
			return new DisplayPageTemplateSEOSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "descriptionTemplate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "htmlTitleTemplate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "robots_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "sitemapSettings")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DisplayPageTemplateSEOSettings displayPageTemplateSEOSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "descriptionTemplate")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateSEOSettings.setDescriptionTemplate(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "htmlTitleTemplate")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateSEOSettings.setHtmlTitleTemplate(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "robots_i18n")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateSEOSettings.setRobots_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sitemapSettings")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateSEOSettings.setSitemapSettings(
						SitemapSettingsSerDes.toDTO(
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
// LIFERAY-REST-BUILDER-HASH:1277732810