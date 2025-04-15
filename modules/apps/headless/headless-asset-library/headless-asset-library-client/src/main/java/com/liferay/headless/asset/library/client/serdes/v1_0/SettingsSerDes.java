/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.client.serdes.v1_0;

import com.liferay.headless.asset.library.client.dto.v1_0.MimeTypeLimit;
import com.liferay.headless.asset.library.client.dto.v1_0.Settings;
import com.liferay.headless.asset.library.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Roberto Díaz
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

		if (settings.getAutoTaggingEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"autoTaggingEnabled\": ");

			sb.append(settings.getAutoTaggingEnabled());
		}

		if (settings.getAvailableLanguageIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguageIds\": ");

			sb.append("[");

			for (int i = 0; i < settings.getAvailableLanguageIds().length;
				 i++) {

				sb.append(_toJSON(settings.getAvailableLanguageIds()[i]));

				if ((i + 1) < settings.getAvailableLanguageIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (settings.getDefaultLanguageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(settings.getDefaultLanguageId()));

			sb.append("\"");
		}

		if (settings.getLogoColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logoColor\": ");

			sb.append("\"");

			sb.append(_escape(settings.getLogoColor()));

			sb.append("\"");
		}

		if (settings.getMimeTypeLimits() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mimeTypeLimits\": ");

			sb.append("[");

			for (int i = 0; i < settings.getMimeTypeLimits().length; i++) {
				sb.append(String.valueOf(settings.getMimeTypeLimits()[i]));

				if ((i + 1) < settings.getMimeTypeLimits().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (settings.getSharingEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sharingEnabled\": ");

			sb.append(settings.getSharingEnabled());
		}

		if (settings.getUseCustomLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useCustomLanguages\": ");

			sb.append(settings.getUseCustomLanguages());
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

		if (settings.getAutoTaggingEnabled() == null) {
			map.put("autoTaggingEnabled", null);
		}
		else {
			map.put(
				"autoTaggingEnabled",
				String.valueOf(settings.getAutoTaggingEnabled()));
		}

		if (settings.getAvailableLanguageIds() == null) {
			map.put("availableLanguageIds", null);
		}
		else {
			map.put(
				"availableLanguageIds",
				String.valueOf(settings.getAvailableLanguageIds()));
		}

		if (settings.getDefaultLanguageId() == null) {
			map.put("defaultLanguageId", null);
		}
		else {
			map.put(
				"defaultLanguageId",
				String.valueOf(settings.getDefaultLanguageId()));
		}

		if (settings.getLogoColor() == null) {
			map.put("logoColor", null);
		}
		else {
			map.put("logoColor", String.valueOf(settings.getLogoColor()));
		}

		if (settings.getMimeTypeLimits() == null) {
			map.put("mimeTypeLimits", null);
		}
		else {
			map.put(
				"mimeTypeLimits", String.valueOf(settings.getMimeTypeLimits()));
		}

		if (settings.getSharingEnabled() == null) {
			map.put("sharingEnabled", null);
		}
		else {
			map.put(
				"sharingEnabled", String.valueOf(settings.getSharingEnabled()));
		}

		if (settings.getUseCustomLanguages() == null) {
			map.put("useCustomLanguages", null);
		}
		else {
			map.put(
				"useCustomLanguages",
				String.valueOf(settings.getUseCustomLanguages()));
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
			if (Objects.equals(jsonParserFieldName, "autoTaggingEnabled")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguageIds")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "logoColor")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "mimeTypeLimits")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sharingEnabled")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "useCustomLanguages")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Settings settings, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "autoTaggingEnabled")) {
				if (jsonParserFieldValue != null) {
					settings.setAutoTaggingEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguageIds")) {

				if (jsonParserFieldValue != null) {
					settings.setAvailableLanguageIds(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				if (jsonParserFieldValue != null) {
					settings.setDefaultLanguageId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "logoColor")) {
				if (jsonParserFieldValue != null) {
					settings.setLogoColor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "mimeTypeLimits")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					MimeTypeLimit[] mimeTypeLimitsArray =
						new MimeTypeLimit[jsonParserFieldValues.length];

					for (int i = 0; i < mimeTypeLimitsArray.length; i++) {
						mimeTypeLimitsArray[i] = MimeTypeLimitSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					settings.setMimeTypeLimits(mimeTypeLimitsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sharingEnabled")) {
				if (jsonParserFieldValue != null) {
					settings.setSharingEnabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "useCustomLanguages")) {

				if (jsonParserFieldValue != null) {
					settings.setUseCustomLanguages(
						(Boolean)jsonParserFieldValue);
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