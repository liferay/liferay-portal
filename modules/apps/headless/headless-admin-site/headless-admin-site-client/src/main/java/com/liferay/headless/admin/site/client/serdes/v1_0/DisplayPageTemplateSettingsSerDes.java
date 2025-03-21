/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateSettings;
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
public class DisplayPageTemplateSettingsSerDes {

	public static DisplayPageTemplateSettings toDTO(String json) {
		DisplayPageTemplateSettingsJSONParser
			displayPageTemplateSettingsJSONParser =
				new DisplayPageTemplateSettingsJSONParser();

		return displayPageTemplateSettingsJSONParser.parseToDTO(json);
	}

	public static DisplayPageTemplateSettings[] toDTOs(String json) {
		DisplayPageTemplateSettingsJSONParser
			displayPageTemplateSettingsJSONParser =
				new DisplayPageTemplateSettingsJSONParser();

		return displayPageTemplateSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DisplayPageTemplateSettings displayPageTemplateSettings) {

		if (displayPageTemplateSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (displayPageTemplateSettings.getOpenGraphSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"openGraphSettings\": ");

			sb.append(
				String.valueOf(
					displayPageTemplateSettings.getOpenGraphSettings()));
		}

		if (displayPageTemplateSettings.getSeoSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seoSettings\": ");

			sb.append(
				String.valueOf(displayPageTemplateSettings.getSeoSettings()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DisplayPageTemplateSettingsJSONParser
			displayPageTemplateSettingsJSONParser =
				new DisplayPageTemplateSettingsJSONParser();

		return displayPageTemplateSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DisplayPageTemplateSettings displayPageTemplateSettings) {

		if (displayPageTemplateSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (displayPageTemplateSettings.getOpenGraphSettings() == null) {
			map.put("openGraphSettings", null);
		}
		else {
			map.put(
				"openGraphSettings",
				String.valueOf(
					displayPageTemplateSettings.getOpenGraphSettings()));
		}

		if (displayPageTemplateSettings.getSeoSettings() == null) {
			map.put("seoSettings", null);
		}
		else {
			map.put(
				"seoSettings",
				String.valueOf(displayPageTemplateSettings.getSeoSettings()));
		}

		return map;
	}

	public static class DisplayPageTemplateSettingsJSONParser
		extends BaseJSONParser<DisplayPageTemplateSettings> {

		@Override
		protected DisplayPageTemplateSettings createDTO() {
			return new DisplayPageTemplateSettings();
		}

		@Override
		protected DisplayPageTemplateSettings[] createDTOArray(int size) {
			return new DisplayPageTemplateSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "openGraphSettings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "seoSettings")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DisplayPageTemplateSettings displayPageTemplateSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "openGraphSettings")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateSettings.setOpenGraphSettings(
						OpenGraphSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "seoSettings")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateSettings.setSeoSettings(
						SEOSettingsSerDes.toDTO((String)jsonParserFieldValue));
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