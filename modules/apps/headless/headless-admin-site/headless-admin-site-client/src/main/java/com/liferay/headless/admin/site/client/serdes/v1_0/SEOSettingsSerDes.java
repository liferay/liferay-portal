/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.SEOSettings;
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
public class SEOSettingsSerDes {

	public static SEOSettings toDTO(String json) {
		SEOSettingsJSONParser seoSettingsJSONParser =
			new SEOSettingsJSONParser();

		return seoSettingsJSONParser.parseToDTO(json);
	}

	public static SEOSettings[] toDTOs(String json) {
		SEOSettingsJSONParser seoSettingsJSONParser =
			new SEOSettingsJSONParser();

		return seoSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SEOSettings seoSettings) {
		if (seoSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (seoSettings.getCustomCanonicalURL_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCanonicalURL_i18n\": ");

			sb.append(_toJSON(seoSettings.getCustomCanonicalURL_i18n()));
		}

		if (seoSettings.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(seoSettings.getDescription_i18n()));
		}

		if (seoSettings.getHtmlTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"htmlTitle_i18n\": ");

			sb.append(_toJSON(seoSettings.getHtmlTitle_i18n()));
		}

		if (seoSettings.getRobots_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"robots_i18n\": ");

			sb.append(_toJSON(seoSettings.getRobots_i18n()));
		}

		if (seoSettings.getSeoKeywords_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seoKeywords_i18n\": ");

			sb.append(_toJSON(seoSettings.getSeoKeywords_i18n()));
		}

		if (seoSettings.getSiteMapSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteMapSettings\": ");

			sb.append(String.valueOf(seoSettings.getSiteMapSettings()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SEOSettingsJSONParser seoSettingsJSONParser =
			new SEOSettingsJSONParser();

		return seoSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SEOSettings seoSettings) {
		if (seoSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (seoSettings.getCustomCanonicalURL_i18n() == null) {
			map.put("customCanonicalURL_i18n", null);
		}
		else {
			map.put(
				"customCanonicalURL_i18n",
				String.valueOf(seoSettings.getCustomCanonicalURL_i18n()));
		}

		if (seoSettings.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(seoSettings.getDescription_i18n()));
		}

		if (seoSettings.getHtmlTitle_i18n() == null) {
			map.put("htmlTitle_i18n", null);
		}
		else {
			map.put(
				"htmlTitle_i18n",
				String.valueOf(seoSettings.getHtmlTitle_i18n()));
		}

		if (seoSettings.getRobots_i18n() == null) {
			map.put("robots_i18n", null);
		}
		else {
			map.put(
				"robots_i18n", String.valueOf(seoSettings.getRobots_i18n()));
		}

		if (seoSettings.getSeoKeywords_i18n() == null) {
			map.put("seoKeywords_i18n", null);
		}
		else {
			map.put(
				"seoKeywords_i18n",
				String.valueOf(seoSettings.getSeoKeywords_i18n()));
		}

		if (seoSettings.getSiteMapSettings() == null) {
			map.put("siteMapSettings", null);
		}
		else {
			map.put(
				"siteMapSettings",
				String.valueOf(seoSettings.getSiteMapSettings()));
		}

		return map;
	}

	public static class SEOSettingsJSONParser
		extends BaseJSONParser<SEOSettings> {

		@Override
		protected SEOSettings createDTO() {
			return new SEOSettings();
		}

		@Override
		protected SEOSettings[] createDTOArray(int size) {
			return new SEOSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "customCanonicalURL_i18n")) {

				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "htmlTitle_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "robots_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "seoKeywords_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "siteMapSettings")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SEOSettings seoSettings, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "customCanonicalURL_i18n")) {

				if (jsonParserFieldValue != null) {
					seoSettings.setCustomCanonicalURL_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "htmlTitle_i18n")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setHtmlTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "robots_i18n")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setRobots_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "seoKeywords_i18n")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setSeoKeywords_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteMapSettings")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setSiteMapSettings(
						SiteMapSettingsSerDes.toDTO(
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