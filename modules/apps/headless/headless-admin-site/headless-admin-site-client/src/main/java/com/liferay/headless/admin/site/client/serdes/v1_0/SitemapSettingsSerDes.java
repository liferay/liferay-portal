/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.SitemapSettings;
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
public class SitemapSettingsSerDes {

	public static SitemapSettings toDTO(String json) {
		SitemapSettingsJSONParser sitemapSettingsJSONParser =
			new SitemapSettingsJSONParser();

		return sitemapSettingsJSONParser.parseToDTO(json);
	}

	public static SitemapSettings[] toDTOs(String json) {
		SitemapSettingsJSONParser sitemapSettingsJSONParser =
			new SitemapSettingsJSONParser();

		return sitemapSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SitemapSettings sitemapSettings) {
		if (sitemapSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (sitemapSettings.getChangeFrequency() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"changeFrequency\": ");

			sb.append("\"");
			sb.append(sitemapSettings.getChangeFrequency());
			sb.append("\"");
		}

		if (sitemapSettings.getInclude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"include\": ");

			sb.append(sitemapSettings.getInclude());
		}

		if (sitemapSettings.getIncludeChildSitePages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"includeChildSitePages\": ");

			sb.append(sitemapSettings.getIncludeChildSitePages());
		}

		if (sitemapSettings.getPagePriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pagePriority\": ");

			sb.append(sitemapSettings.getPagePriority());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SitemapSettingsJSONParser sitemapSettingsJSONParser =
			new SitemapSettingsJSONParser();

		return sitemapSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SitemapSettings sitemapSettings) {
		if (sitemapSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (sitemapSettings.getChangeFrequency() == null) {
			map.put("changeFrequency", null);
		}
		else {
			map.put(
				"changeFrequency",
				String.valueOf(sitemapSettings.getChangeFrequency()));
		}

		if (sitemapSettings.getInclude() == null) {
			map.put("include", null);
		}
		else {
			map.put("include", String.valueOf(sitemapSettings.getInclude()));
		}

		if (sitemapSettings.getIncludeChildSitePages() == null) {
			map.put("includeChildSitePages", null);
		}
		else {
			map.put(
				"includeChildSitePages",
				String.valueOf(sitemapSettings.getIncludeChildSitePages()));
		}

		if (sitemapSettings.getPagePriority() == null) {
			map.put("pagePriority", null);
		}
		else {
			map.put(
				"pagePriority",
				String.valueOf(sitemapSettings.getPagePriority()));
		}

		return map;
	}

	public static class SitemapSettingsJSONParser
		extends BaseJSONParser<SitemapSettings> {

		@Override
		protected SitemapSettings createDTO() {
			return new SitemapSettings();
		}

		@Override
		protected SitemapSettings[] createDTOArray(int size) {
			return new SitemapSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "changeFrequency")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "include")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "includeChildSitePages")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pagePriority")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SitemapSettings sitemapSettings, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "changeFrequency")) {
				if (jsonParserFieldValue != null) {
					sitemapSettings.setChangeFrequency(
						SitemapSettings.ChangeFrequency.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "include")) {
				if (jsonParserFieldValue != null) {
					sitemapSettings.setInclude((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "includeChildSitePages")) {

				if (jsonParserFieldValue != null) {
					sitemapSettings.setIncludeChildSitePages(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pagePriority")) {
				if (jsonParserFieldValue != null) {
					sitemapSettings.setPagePriority(
						Double.valueOf((String)jsonParserFieldValue));
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