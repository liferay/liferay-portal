/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageNavigationMenuItemSettings;
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
public class DisplayPageNavigationMenuItemSettingsSerDes {

	public static DisplayPageNavigationMenuItemSettings toDTO(String json) {
		DisplayPageNavigationMenuItemSettingsJSONParser
			displayPageNavigationMenuItemSettingsJSONParser =
				new DisplayPageNavigationMenuItemSettingsJSONParser();

		return displayPageNavigationMenuItemSettingsJSONParser.parseToDTO(json);
	}

	public static DisplayPageNavigationMenuItemSettings[] toDTOs(String json) {
		DisplayPageNavigationMenuItemSettingsJSONParser
			displayPageNavigationMenuItemSettingsJSONParser =
				new DisplayPageNavigationMenuItemSettingsJSONParser();

		return displayPageNavigationMenuItemSettingsJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		DisplayPageNavigationMenuItemSettings
			displayPageNavigationMenuItemSettings) {

		if (displayPageNavigationMenuItemSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (displayPageNavigationMenuItemSettings.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(
				_escape(displayPageNavigationMenuItemSettings.getClassName()));

			sb.append("\"");
		}

		if (displayPageNavigationMenuItemSettings.getExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					displayPageNavigationMenuItemSettings.
						getExternalReferenceCode()));

			sb.append("\"");
		}

		if (displayPageNavigationMenuItemSettings.
				getScopeExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scopeExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					displayPageNavigationMenuItemSettings.
						getScopeExternalReferenceCode()));

			sb.append("\"");
		}

		if (displayPageNavigationMenuItemSettings.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(
				_escape(displayPageNavigationMenuItemSettings.getTitle()));

			sb.append("\"");
		}

		if (displayPageNavigationMenuItemSettings.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(displayPageNavigationMenuItemSettings.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DisplayPageNavigationMenuItemSettingsJSONParser
			displayPageNavigationMenuItemSettingsJSONParser =
				new DisplayPageNavigationMenuItemSettingsJSONParser();

		return displayPageNavigationMenuItemSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DisplayPageNavigationMenuItemSettings
			displayPageNavigationMenuItemSettings) {

		if (displayPageNavigationMenuItemSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (displayPageNavigationMenuItemSettings.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(
					displayPageNavigationMenuItemSettings.getClassName()));
		}

		if (displayPageNavigationMenuItemSettings.getExternalReferenceCode() ==
				null) {

			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					displayPageNavigationMenuItemSettings.
						getExternalReferenceCode()));
		}

		if (displayPageNavigationMenuItemSettings.
				getScopeExternalReferenceCode() == null) {

			map.put("scopeExternalReferenceCode", null);
		}
		else {
			map.put(
				"scopeExternalReferenceCode",
				String.valueOf(
					displayPageNavigationMenuItemSettings.
						getScopeExternalReferenceCode()));
		}

		if (displayPageNavigationMenuItemSettings.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put(
				"title",
				String.valueOf(
					displayPageNavigationMenuItemSettings.getTitle()));
		}

		if (displayPageNavigationMenuItemSettings.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					displayPageNavigationMenuItemSettings.getType()));
		}

		return map;
	}

	public static class DisplayPageNavigationMenuItemSettingsJSONParser
		extends BaseJSONParser<DisplayPageNavigationMenuItemSettings> {

		@Override
		protected DisplayPageNavigationMenuItemSettings createDTO() {
			return new DisplayPageNavigationMenuItemSettings();
		}

		@Override
		protected DisplayPageNavigationMenuItemSettings[] createDTOArray(
			int size) {

			return new DisplayPageNavigationMenuItemSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "scopeExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DisplayPageNavigationMenuItemSettings
				displayPageNavigationMenuItemSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					displayPageNavigationMenuItemSettings.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					displayPageNavigationMenuItemSettings.
						setExternalReferenceCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "scopeExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					displayPageNavigationMenuItemSettings.
						setScopeExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					displayPageNavigationMenuItemSettings.setTitle(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					displayPageNavigationMenuItemSettings.setType(
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
// LIFERAY-REST-BUILDER-HASH:1147301540