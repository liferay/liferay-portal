/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.SitePageNavigationMenuSettings;
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
public class SitePageNavigationMenuSettingsSerDes {

	public static SitePageNavigationMenuSettings toDTO(String json) {
		SitePageNavigationMenuSettingsJSONParser
			sitePageNavigationMenuSettingsJSONParser =
				new SitePageNavigationMenuSettingsJSONParser();

		return sitePageNavigationMenuSettingsJSONParser.parseToDTO(json);
	}

	public static SitePageNavigationMenuSettings[] toDTOs(String json) {
		SitePageNavigationMenuSettingsJSONParser
			sitePageNavigationMenuSettingsJSONParser =
				new SitePageNavigationMenuSettingsJSONParser();

		return sitePageNavigationMenuSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		SitePageNavigationMenuSettings sitePageNavigationMenuSettings) {

		if (sitePageNavigationMenuSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (sitePageNavigationMenuSettings.getQueryString() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"queryString\": ");

			sb.append("\"");

			sb.append(_escape(sitePageNavigationMenuSettings.getQueryString()));

			sb.append("\"");
		}

		if (sitePageNavigationMenuSettings.getTarget() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"target\": ");

			sb.append("\"");

			sb.append(_escape(sitePageNavigationMenuSettings.getTarget()));

			sb.append("\"");
		}

		if (sitePageNavigationMenuSettings.getTargetType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"targetType\": ");

			sb.append("\"");

			sb.append(sitePageNavigationMenuSettings.getTargetType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SitePageNavigationMenuSettingsJSONParser
			sitePageNavigationMenuSettingsJSONParser =
				new SitePageNavigationMenuSettingsJSONParser();

		return sitePageNavigationMenuSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SitePageNavigationMenuSettings sitePageNavigationMenuSettings) {

		if (sitePageNavigationMenuSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (sitePageNavigationMenuSettings.getQueryString() == null) {
			map.put("queryString", null);
		}
		else {
			map.put(
				"queryString",
				String.valueOf(
					sitePageNavigationMenuSettings.getQueryString()));
		}

		if (sitePageNavigationMenuSettings.getTarget() == null) {
			map.put("target", null);
		}
		else {
			map.put(
				"target",
				String.valueOf(sitePageNavigationMenuSettings.getTarget()));
		}

		if (sitePageNavigationMenuSettings.getTargetType() == null) {
			map.put("targetType", null);
		}
		else {
			map.put(
				"targetType",
				String.valueOf(sitePageNavigationMenuSettings.getTargetType()));
		}

		return map;
	}

	public static class SitePageNavigationMenuSettingsJSONParser
		extends BaseJSONParser<SitePageNavigationMenuSettings> {

		@Override
		protected SitePageNavigationMenuSettings createDTO() {
			return new SitePageNavigationMenuSettings();
		}

		@Override
		protected SitePageNavigationMenuSettings[] createDTOArray(int size) {
			return new SitePageNavigationMenuSettings[size];
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
			SitePageNavigationMenuSettings sitePageNavigationMenuSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "queryString")) {
				if (jsonParserFieldValue != null) {
					sitePageNavigationMenuSettings.setQueryString(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "target")) {
				if (jsonParserFieldValue != null) {
					sitePageNavigationMenuSettings.setTarget(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "targetType")) {
				if (jsonParserFieldValue != null) {
					sitePageNavigationMenuSettings.setTargetType(
						SitePageNavigationMenuSettings.TargetType.create(
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