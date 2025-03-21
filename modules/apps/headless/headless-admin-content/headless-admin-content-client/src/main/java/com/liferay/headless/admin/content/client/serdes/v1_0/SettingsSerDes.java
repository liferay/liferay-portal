/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.serdes.v1_0;

import com.liferay.headless.admin.content.client.dto.v1_0.ClientExtension;
import com.liferay.headless.admin.content.client.dto.v1_0.Settings;
import com.liferay.headless.admin.content.client.json.BaseJSONParser;

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
public class SettingsSerDes {

	public static Settings toDTO(String json) {
		SettingsJSONParser settingsJSONParser = new SettingsJSONParser();

		return settingsJSONParser.parseToDTO(json);
	}

	public static Settings[] toDTOs(String json) {
		SettingsJSONParser settingsJSONParser = new SettingsJSONParser();

		return settingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Settings settings) {
		if (settings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (settings.getColorSchemeName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"colorSchemeName\": ");

			sb.append("\"");

			sb.append(_escape(settings.getColorSchemeName()));

			sb.append("\"");
		}

		if (settings.getCss() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"css\": ");

			sb.append("\"");

			sb.append(_escape(settings.getCss()));

			sb.append("\"");
		}

		if (settings.getFavIcon() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"favIcon\": ");

			if (settings.getFavIcon() instanceof String) {
				sb.append("\"");
				sb.append((String)settings.getFavIcon());
				sb.append("\"");
			}
			else {
				sb.append(settings.getFavIcon());
			}
		}

		if (settings.getGlobalCSSClientExtensions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"globalCSSClientExtensions\": ");

			sb.append("[");

			for (int i = 0; i < settings.getGlobalCSSClientExtensions().length;
				 i++) {

				sb.append(settings.getGlobalCSSClientExtensions()[i]);

				if ((i + 1) < settings.getGlobalCSSClientExtensions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (settings.getGlobalJSClientExtensions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"globalJSClientExtensions\": ");

			sb.append("[");

			for (int i = 0; i < settings.getGlobalJSClientExtensions().length;
				 i++) {

				sb.append(settings.getGlobalJSClientExtensions()[i]);

				if ((i + 1) < settings.getGlobalJSClientExtensions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (settings.getJavascript() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"javascript\": ");

			sb.append("\"");

			sb.append(_escape(settings.getJavascript()));

			sb.append("\"");
		}

		if (settings.getMasterPage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"masterPage\": ");

			sb.append(settings.getMasterPage());
		}

		if (settings.getStyleBook() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"styleBook\": ");

			sb.append(settings.getStyleBook());
		}

		if (settings.getThemeCSSClientExtension() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeCSSClientExtension\": ");

			sb.append(settings.getThemeCSSClientExtension());
		}

		if (settings.getThemeName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeName\": ");

			sb.append("\"");

			sb.append(_escape(settings.getThemeName()));

			sb.append("\"");
		}

		if (settings.getThemeSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeSettings\": ");

			if (settings.getThemeSettings() instanceof String) {
				sb.append("\"");
				sb.append((String)settings.getThemeSettings());
				sb.append("\"");
			}
			else {
				sb.append(settings.getThemeSettings());
			}
		}

		if (settings.getThemeSpritemapClientExtension() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeSpritemapClientExtension\": ");

			sb.append(settings.getThemeSpritemapClientExtension());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SettingsJSONParser settingsJSONParser = new SettingsJSONParser();

		return settingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Settings settings) {
		if (settings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (settings.getColorSchemeName() == null) {
			map.put("colorSchemeName", null);
		}
		else {
			map.put(
				"colorSchemeName",
				String.valueOf(settings.getColorSchemeName()));
		}

		if (settings.getCss() == null) {
			map.put("css", null);
		}
		else {
			map.put("css", String.valueOf(settings.getCss()));
		}

		if (settings.getFavIcon() == null) {
			map.put("favIcon", null);
		}
		else {
			map.put("favIcon", String.valueOf(settings.getFavIcon()));
		}

		if (settings.getGlobalCSSClientExtensions() == null) {
			map.put("globalCSSClientExtensions", null);
		}
		else {
			map.put(
				"globalCSSClientExtensions",
				String.valueOf(settings.getGlobalCSSClientExtensions()));
		}

		if (settings.getGlobalJSClientExtensions() == null) {
			map.put("globalJSClientExtensions", null);
		}
		else {
			map.put(
				"globalJSClientExtensions",
				String.valueOf(settings.getGlobalJSClientExtensions()));
		}

		if (settings.getJavascript() == null) {
			map.put("javascript", null);
		}
		else {
			map.put("javascript", String.valueOf(settings.getJavascript()));
		}

		if (settings.getMasterPage() == null) {
			map.put("masterPage", null);
		}
		else {
			map.put("masterPage", String.valueOf(settings.getMasterPage()));
		}

		if (settings.getStyleBook() == null) {
			map.put("styleBook", null);
		}
		else {
			map.put("styleBook", String.valueOf(settings.getStyleBook()));
		}

		if (settings.getThemeCSSClientExtension() == null) {
			map.put("themeCSSClientExtension", null);
		}
		else {
			map.put(
				"themeCSSClientExtension",
				String.valueOf(settings.getThemeCSSClientExtension()));
		}

		if (settings.getThemeName() == null) {
			map.put("themeName", null);
		}
		else {
			map.put("themeName", String.valueOf(settings.getThemeName()));
		}

		if (settings.getThemeSettings() == null) {
			map.put("themeSettings", null);
		}
		else {
			map.put(
				"themeSettings", String.valueOf(settings.getThemeSettings()));
		}

		if (settings.getThemeSpritemapClientExtension() == null) {
			map.put("themeSpritemapClientExtension", null);
		}
		else {
			map.put(
				"themeSpritemapClientExtension",
				String.valueOf(settings.getThemeSpritemapClientExtension()));
		}

		return map;
	}

	public static class SettingsJSONParser extends BaseJSONParser<Settings> {

		@Override
		protected Settings createDTO() {
			return new Settings();
		}

		@Override
		protected Settings[] createDTOArray(int size) {
			return new Settings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "colorSchemeName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "css")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "favIcon")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "globalCSSClientExtensions")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "globalJSClientExtensions")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "javascript")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "masterPage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "styleBook")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "themeCSSClientExtension")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "themeName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "themeSettings")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "themeSpritemapClientExtension")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Settings settings, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "colorSchemeName")) {
				if (jsonParserFieldValue != null) {
					settings.setColorSchemeName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "css")) {
				if (jsonParserFieldValue != null) {
					settings.setCss((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "favIcon")) {
				if (jsonParserFieldValue != null) {
					settings.setFavIcon((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "globalCSSClientExtensions")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ClientExtension[] globalCSSClientExtensionsArray =
						new ClientExtension[jsonParserFieldValues.length];

					for (int i = 0; i < globalCSSClientExtensionsArray.length;
						 i++) {

						globalCSSClientExtensionsArray[i] =
							ClientExtensionSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					settings.setGlobalCSSClientExtensions(
						globalCSSClientExtensionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "globalJSClientExtensions")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ClientExtension[] globalJSClientExtensionsArray =
						new ClientExtension[jsonParserFieldValues.length];

					for (int i = 0; i < globalJSClientExtensionsArray.length;
						 i++) {

						globalJSClientExtensionsArray[i] =
							ClientExtensionSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					settings.setGlobalJSClientExtensions(
						globalJSClientExtensionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "javascript")) {
				if (jsonParserFieldValue != null) {
					settings.setJavascript((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "masterPage")) {
				if (jsonParserFieldValue != null) {
					settings.setMasterPage(
						MasterPageSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "styleBook")) {
				if (jsonParserFieldValue != null) {
					settings.setStyleBook(
						StyleBookSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "themeCSSClientExtension")) {

				if (jsonParserFieldValue != null) {
					settings.setThemeCSSClientExtension(
						ClientExtensionSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "themeName")) {
				if (jsonParserFieldValue != null) {
					settings.setThemeName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "themeSettings")) {
				if (jsonParserFieldValue != null) {
					settings.setThemeSettings((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "themeSpritemapClientExtension")) {

				if (jsonParserFieldValue != null) {
					settings.setThemeSpritemapClientExtension(
						ClientExtensionSerDes.toDTO(
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