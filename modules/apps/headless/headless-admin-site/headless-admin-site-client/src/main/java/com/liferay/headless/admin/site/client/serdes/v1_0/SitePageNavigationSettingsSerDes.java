/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.SitePageNavigationSettings;
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
public class SitePageNavigationSettingsSerDes {

	public static SitePageNavigationSettings toDTO(String json) {
		SitePageNavigationSettingsJSONParser
			sitePageNavigationSettingsJSONParser =
				new SitePageNavigationSettingsJSONParser();

		return sitePageNavigationSettingsJSONParser.parseToDTO(json);
	}

	public static SitePageNavigationSettings[] toDTOs(String json) {
		SitePageNavigationSettingsJSONParser
			sitePageNavigationSettingsJSONParser =
				new SitePageNavigationSettingsJSONParser();

		return sitePageNavigationSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		SitePageNavigationSettings sitePageNavigationSettings) {

		if (sitePageNavigationSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (sitePageNavigationSettings.getQueryString() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"queryString\": ");

			sb.append("\"");

			sb.append(_escape(sitePageNavigationSettings.getQueryString()));

			sb.append("\"");
		}

		if (sitePageNavigationSettings.getTarget() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"target\": ");

			sb.append("\"");

			sb.append(_escape(sitePageNavigationSettings.getTarget()));

			sb.append("\"");
		}

		if (sitePageNavigationSettings.getTargetType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"targetType\": ");

			sb.append("\"");
			sb.append(sitePageNavigationSettings.getTargetType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SitePageNavigationSettingsJSONParser
			sitePageNavigationSettingsJSONParser =
				new SitePageNavigationSettingsJSONParser();

		return sitePageNavigationSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SitePageNavigationSettings sitePageNavigationSettings) {

		if (sitePageNavigationSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (sitePageNavigationSettings.getQueryString() == null) {
			map.put("queryString", null);
		}
		else {
			map.put(
				"queryString",
				String.valueOf(sitePageNavigationSettings.getQueryString()));
		}

		if (sitePageNavigationSettings.getTarget() == null) {
			map.put("target", null);
		}
		else {
			map.put(
				"target",
				String.valueOf(sitePageNavigationSettings.getTarget()));
		}

		if (sitePageNavigationSettings.getTargetType() == null) {
			map.put("targetType", null);
		}
		else {
			map.put(
				"targetType",
				String.valueOf(sitePageNavigationSettings.getTargetType()));
		}

		return map;
	}

	public static class SitePageNavigationSettingsJSONParser
		extends BaseJSONParser<SitePageNavigationSettings> {

		@Override
		protected SitePageNavigationSettings createDTO() {
			return new SitePageNavigationSettings();
		}

		@Override
		protected SitePageNavigationSettings[] createDTOArray(int size) {
			return new SitePageNavigationSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "queryString")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "target")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "targetType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SitePageNavigationSettings sitePageNavigationSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "queryString")) {
				if (jsonParserFieldValue != null) {
					sitePageNavigationSettings.setQueryString(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "target")) {
				if (jsonParserFieldValue != null) {
					sitePageNavigationSettings.setTarget(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "targetType")) {
				if (jsonParserFieldValue != null) {
					sitePageNavigationSettings.setTargetType(
						SitePageNavigationSettings.TargetType.create(
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