/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.NavigationMenuSettings;
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
public class NavigationMenuSettingsSerDes {

	public static NavigationMenuSettings toDTO(String json) {
		NavigationMenuSettingsJSONParser navigationMenuSettingsJSONParser =
			new NavigationMenuSettingsJSONParser();

		return navigationMenuSettingsJSONParser.parseToDTO(json);
	}

	public static NavigationMenuSettings[] toDTOs(String json) {
		NavigationMenuSettingsJSONParser navigationMenuSettingsJSONParser =
			new NavigationMenuSettingsJSONParser();

		return navigationMenuSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(NavigationMenuSettings navigationMenuSettings) {
		if (navigationMenuSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (navigationMenuSettings.getTarget() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"target\": ");

			sb.append("\"");

			sb.append(_escape(navigationMenuSettings.getTarget()));

			sb.append("\"");
		}

		if (navigationMenuSettings.getTargetType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"targetType\": ");

			sb.append("\"");

			sb.append(navigationMenuSettings.getTargetType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		NavigationMenuSettingsJSONParser navigationMenuSettingsJSONParser =
			new NavigationMenuSettingsJSONParser();

		return navigationMenuSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		NavigationMenuSettings navigationMenuSettings) {

		if (navigationMenuSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (navigationMenuSettings.getTarget() == null) {
			map.put("target", null);
		}
		else {
			map.put(
				"target", String.valueOf(navigationMenuSettings.getTarget()));
		}

		if (navigationMenuSettings.getTargetType() == null) {
			map.put("targetType", null);
		}
		else {
			map.put(
				"targetType",
				String.valueOf(navigationMenuSettings.getTargetType()));
		}

		return map;
	}

	public static class NavigationMenuSettingsJSONParser
		extends BaseJSONParser<NavigationMenuSettings> {

		@Override
		protected NavigationMenuSettings createDTO() {
			return new NavigationMenuSettings();
		}

		@Override
		protected NavigationMenuSettings[] createDTOArray(int size) {
			return new NavigationMenuSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "target")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "targetType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			NavigationMenuSettings navigationMenuSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "target")) {
				if (jsonParserFieldValue != null) {
					navigationMenuSettings.setTarget(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "targetType")) {
				if (jsonParserFieldValue != null) {
					navigationMenuSettings.setTargetType(
						NavigationMenuSettings.TargetType.create(
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