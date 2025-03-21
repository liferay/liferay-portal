/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.SiteMapSettings;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

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
public class SiteMapSettingsSerDes {

	public static SiteMapSettings toDTO(String json) {
		SiteMapSettingsJSONParser siteMapSettingsJSONParser =
			new SiteMapSettingsJSONParser();

		return siteMapSettingsJSONParser.parseToDTO(json);
	}

	public static SiteMapSettings[] toDTOs(String json) {
		SiteMapSettingsJSONParser siteMapSettingsJSONParser =
			new SiteMapSettingsJSONParser();

		return siteMapSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SiteMapSettings siteMapSettings) {
		if (siteMapSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (siteMapSettings.getChangeFrequency() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"changeFrequency\": ");

			sb.append("\"");

			sb.append(siteMapSettings.getChangeFrequency());

			sb.append("\"");
		}

		if (siteMapSettings.getInclude() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"include\": ");

			sb.append(siteMapSettings.getInclude());
		}

		if (siteMapSettings.getIncludeChildSitePages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"includeChildSitePages\": ");

			sb.append(siteMapSettings.getIncludeChildSitePages());
		}

		if (siteMapSettings.getPagePriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pagePriority\": ");

			sb.append(siteMapSettings.getPagePriority());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SiteMapSettingsJSONParser siteMapSettingsJSONParser =
			new SiteMapSettingsJSONParser();

		return siteMapSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SiteMapSettings siteMapSettings) {
		if (siteMapSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (siteMapSettings.getChangeFrequency() == null) {
			map.put("changeFrequency", null);
		}
		else {
			map.put(
				"changeFrequency",
				String.valueOf(siteMapSettings.getChangeFrequency()));
		}

		if (siteMapSettings.getInclude() == null) {
			map.put("include", null);
		}
		else {
			map.put("include", String.valueOf(siteMapSettings.getInclude()));
		}

		if (siteMapSettings.getIncludeChildSitePages() == null) {
			map.put("includeChildSitePages", null);
		}
		else {
			map.put(
				"includeChildSitePages",
				String.valueOf(siteMapSettings.getIncludeChildSitePages()));
		}

		if (siteMapSettings.getPagePriority() == null) {
			map.put("pagePriority", null);
		}
		else {
			map.put(
				"pagePriority",
				String.valueOf(siteMapSettings.getPagePriority()));
		}

		return map;
	}

	public static class SiteMapSettingsJSONParser
		extends BaseJSONParser<SiteMapSettings> {

		@Override
		protected SiteMapSettings createDTO() {
			return new SiteMapSettings();
		}

		@Override
		protected SiteMapSettings[] createDTOArray(int size) {
			return new SiteMapSettings[size];
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
			SiteMapSettings siteMapSettings, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "changeFrequency")) {
				if (jsonParserFieldValue != null) {
					siteMapSettings.setChangeFrequency(
						SiteMapSettings.ChangeFrequency.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "include")) {
				if (jsonParserFieldValue != null) {
					siteMapSettings.setInclude((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "includeChildSitePages")) {

				if (jsonParserFieldValue != null) {
					siteMapSettings.setIncludeChildSitePages(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pagePriority")) {
				if (jsonParserFieldValue != null) {
					siteMapSettings.setPagePriority(
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