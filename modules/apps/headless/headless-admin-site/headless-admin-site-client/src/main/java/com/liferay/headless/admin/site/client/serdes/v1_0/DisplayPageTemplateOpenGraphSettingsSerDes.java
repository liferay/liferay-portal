/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateOpenGraphSettings;
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
public class DisplayPageTemplateOpenGraphSettingsSerDes {

	public static DisplayPageTemplateOpenGraphSettings toDTO(String json) {
		DisplayPageTemplateOpenGraphSettingsJSONParser
			displayPageTemplateOpenGraphSettingsJSONParser =
				new DisplayPageTemplateOpenGraphSettingsJSONParser();

		return displayPageTemplateOpenGraphSettingsJSONParser.parseToDTO(json);
	}

	public static DisplayPageTemplateOpenGraphSettings[] toDTOs(String json) {
		DisplayPageTemplateOpenGraphSettingsJSONParser
			displayPageTemplateOpenGraphSettingsJSONParser =
				new DisplayPageTemplateOpenGraphSettingsJSONParser();

		return displayPageTemplateOpenGraphSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DisplayPageTemplateOpenGraphSettings
			displayPageTemplateOpenGraphSettings) {

		if (displayPageTemplateOpenGraphSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (displayPageTemplateOpenGraphSettings.getDescriptionTemplate() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptionTemplate\": ");

			sb.append("\"");

			sb.append(
				_escape(
					displayPageTemplateOpenGraphSettings.
						getDescriptionTemplate()));

			sb.append("\"");
		}

		if (displayPageTemplateOpenGraphSettings.getImageAltTemplate() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageAltTemplate\": ");

			sb.append("\"");

			sb.append(
				_escape(
					displayPageTemplateOpenGraphSettings.
						getImageAltTemplate()));

			sb.append("\"");
		}

		if (displayPageTemplateOpenGraphSettings.getImageTemplate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageTemplate\": ");

			sb.append("\"");

			sb.append(
				_escape(
					displayPageTemplateOpenGraphSettings.getImageTemplate()));

			sb.append("\"");
		}

		if (displayPageTemplateOpenGraphSettings.getTitleTemplate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"titleTemplate\": ");

			sb.append("\"");

			sb.append(
				_escape(
					displayPageTemplateOpenGraphSettings.getTitleTemplate()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DisplayPageTemplateOpenGraphSettingsJSONParser
			displayPageTemplateOpenGraphSettingsJSONParser =
				new DisplayPageTemplateOpenGraphSettingsJSONParser();

		return displayPageTemplateOpenGraphSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DisplayPageTemplateOpenGraphSettings
			displayPageTemplateOpenGraphSettings) {

		if (displayPageTemplateOpenGraphSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (displayPageTemplateOpenGraphSettings.getDescriptionTemplate() ==
				null) {

			map.put("descriptionTemplate", null);
		}
		else {
			map.put(
				"descriptionTemplate",
				String.valueOf(
					displayPageTemplateOpenGraphSettings.
						getDescriptionTemplate()));
		}

		if (displayPageTemplateOpenGraphSettings.getImageAltTemplate() ==
				null) {

			map.put("imageAltTemplate", null);
		}
		else {
			map.put(
				"imageAltTemplate",
				String.valueOf(
					displayPageTemplateOpenGraphSettings.
						getImageAltTemplate()));
		}

		if (displayPageTemplateOpenGraphSettings.getImageTemplate() == null) {
			map.put("imageTemplate", null);
		}
		else {
			map.put(
				"imageTemplate",
				String.valueOf(
					displayPageTemplateOpenGraphSettings.getImageTemplate()));
		}

		if (displayPageTemplateOpenGraphSettings.getTitleTemplate() == null) {
			map.put("titleTemplate", null);
		}
		else {
			map.put(
				"titleTemplate",
				String.valueOf(
					displayPageTemplateOpenGraphSettings.getTitleTemplate()));
		}

		return map;
	}

	public static class DisplayPageTemplateOpenGraphSettingsJSONParser
		extends BaseJSONParser<DisplayPageTemplateOpenGraphSettings> {

		@Override
		protected DisplayPageTemplateOpenGraphSettings createDTO() {
			return new DisplayPageTemplateOpenGraphSettings();
		}

		@Override
		protected DisplayPageTemplateOpenGraphSettings[] createDTOArray(
			int size) {

			return new DisplayPageTemplateOpenGraphSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "descriptionTemplate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "imageAltTemplate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "imageTemplate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "titleTemplate")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DisplayPageTemplateOpenGraphSettings
				displayPageTemplateOpenGraphSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "descriptionTemplate")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateOpenGraphSettings.setDescriptionTemplate(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "imageAltTemplate")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateOpenGraphSettings.setImageAltTemplate(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "imageTemplate")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateOpenGraphSettings.setImageTemplate(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "titleTemplate")) {
				if (jsonParserFieldValue != null) {
					displayPageTemplateOpenGraphSettings.setTitleTemplate(
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
// LIFERAY-REST-BUILDER-HASH:1952362192