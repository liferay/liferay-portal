/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.SEOSettings;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
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

		if (seoSettings.getCustomCanonicalURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCanonicalURL\": ");

			sb.append("\"");

			sb.append(_escape(seoSettings.getCustomCanonicalURL()));

			sb.append("\"");
		}

		if (seoSettings.getCustomCanonicalURL_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCanonicalURL_i18n\": ");

			sb.append(_toJSON(seoSettings.getCustomCanonicalURL_i18n()));
		}

		if (seoSettings.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(seoSettings.getDescription()));

			sb.append("\"");
		}

		if (seoSettings.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(seoSettings.getDescription_i18n()));
		}

		if (seoSettings.getHtmlTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"htmlTitle\": ");

			sb.append("\"");

			sb.append(_escape(seoSettings.getHtmlTitle()));

			sb.append("\"");
		}

		if (seoSettings.getHtmlTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"htmlTitle_i18n\": ");

			sb.append(_toJSON(seoSettings.getHtmlTitle_i18n()));
		}

		if (seoSettings.getRobots() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"robots\": ");

			sb.append("\"");

			sb.append(_escape(seoSettings.getRobots()));

			sb.append("\"");
		}

		if (seoSettings.getRobots_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"robots_i18n\": ");

			sb.append(_toJSON(seoSettings.getRobots_i18n()));
		}

		if (seoSettings.getSeoKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seoKeywords\": ");

			sb.append("\"");

			sb.append(_escape(seoSettings.getSeoKeywords()));

			sb.append("\"");
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

		if (seoSettings.getCustomCanonicalURL() == null) {
			map.put("customCanonicalURL", null);
		}
		else {
			map.put(
				"customCanonicalURL",
				String.valueOf(seoSettings.getCustomCanonicalURL()));
		}

		if (seoSettings.getCustomCanonicalURL_i18n() == null) {
			map.put("customCanonicalURL_i18n", null);
		}
		else {
			map.put(
				"customCanonicalURL_i18n",
				String.valueOf(seoSettings.getCustomCanonicalURL_i18n()));
		}

		if (seoSettings.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(seoSettings.getDescription()));
		}

		if (seoSettings.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(seoSettings.getDescription_i18n()));
		}

		if (seoSettings.getHtmlTitle() == null) {
			map.put("htmlTitle", null);
		}
		else {
			map.put("htmlTitle", String.valueOf(seoSettings.getHtmlTitle()));
		}

		if (seoSettings.getHtmlTitle_i18n() == null) {
			map.put("htmlTitle_i18n", null);
		}
		else {
			map.put(
				"htmlTitle_i18n",
				String.valueOf(seoSettings.getHtmlTitle_i18n()));
		}

		if (seoSettings.getRobots() == null) {
			map.put("robots", null);
		}
		else {
			map.put("robots", String.valueOf(seoSettings.getRobots()));
		}

		if (seoSettings.getRobots_i18n() == null) {
			map.put("robots_i18n", null);
		}
		else {
			map.put(
				"robots_i18n", String.valueOf(seoSettings.getRobots_i18n()));
		}

		if (seoSettings.getSeoKeywords() == null) {
			map.put("seoKeywords", null);
		}
		else {
			map.put(
				"seoKeywords", String.valueOf(seoSettings.getSeoKeywords()));
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
			if (Objects.equals(jsonParserFieldName, "customCanonicalURL")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "customCanonicalURL_i18n")) {

				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "htmlTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "htmlTitle_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "robots")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "robots_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "seoKeywords")) {
				return false;
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

			if (Objects.equals(jsonParserFieldName, "customCanonicalURL")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setCustomCanonicalURL(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "customCanonicalURL_i18n")) {

				if (jsonParserFieldValue != null) {
					seoSettings.setCustomCanonicalURL_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "htmlTitle")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setHtmlTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "htmlTitle_i18n")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setHtmlTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "robots")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setRobots((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "robots_i18n")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setRobots_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "seoKeywords")) {
				if (jsonParserFieldValue != null) {
					seoSettings.setSeoKeywords((String)jsonParserFieldValue);
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