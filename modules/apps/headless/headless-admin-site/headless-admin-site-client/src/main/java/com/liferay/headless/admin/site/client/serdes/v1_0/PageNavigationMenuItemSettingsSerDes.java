/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.PageNavigationMenuItemSettings;
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
public class PageNavigationMenuItemSettingsSerDes {

	public static PageNavigationMenuItemSettings toDTO(String json) {
		PageNavigationMenuItemSettingsJSONParser
			pageNavigationMenuItemSettingsJSONParser =
				new PageNavigationMenuItemSettingsJSONParser();

		return pageNavigationMenuItemSettingsJSONParser.parseToDTO(json);
	}

	public static PageNavigationMenuItemSettings[] toDTOs(String json) {
		PageNavigationMenuItemSettingsJSONParser
			pageNavigationMenuItemSettingsJSONParser =
				new PageNavigationMenuItemSettingsJSONParser();

		return pageNavigationMenuItemSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PageNavigationMenuItemSettings pageNavigationMenuItemSettings) {

		if (pageNavigationMenuItemSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageNavigationMenuItemSettings.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					pageNavigationMenuItemSettings.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (pageNavigationMenuItemSettings.getPrivatePage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"privatePage\": ");

			sb.append(pageNavigationMenuItemSettings.getPrivatePage());
		}

		if (pageNavigationMenuItemSettings.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(pageNavigationMenuItemSettings.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageNavigationMenuItemSettingsJSONParser
			pageNavigationMenuItemSettingsJSONParser =
				new PageNavigationMenuItemSettingsJSONParser();

		return pageNavigationMenuItemSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageNavigationMenuItemSettings pageNavigationMenuItemSettings) {

		if (pageNavigationMenuItemSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageNavigationMenuItemSettings.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					pageNavigationMenuItemSettings.getExternalReferenceCode()));
		}

		if (pageNavigationMenuItemSettings.getPrivatePage() == null) {
			map.put("privatePage", null);
		}
		else {
			map.put(
				"privatePage",
				String.valueOf(
					pageNavigationMenuItemSettings.getPrivatePage()));
		}

		if (pageNavigationMenuItemSettings.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put(
				"title",
				String.valueOf(pageNavigationMenuItemSettings.getTitle()));
		}

		return map;
	}

	public static class PageNavigationMenuItemSettingsJSONParser
		extends BaseJSONParser<PageNavigationMenuItemSettings> {

		@Override
		protected PageNavigationMenuItemSettings createDTO() {
			return new PageNavigationMenuItemSettings();
		}

		@Override
		protected PageNavigationMenuItemSettings[] createDTOArray(int size) {
			return new PageNavigationMenuItemSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "privatePage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageNavigationMenuItemSettings pageNavigationMenuItemSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					pageNavigationMenuItemSettings.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "privatePage")) {
				if (jsonParserFieldValue != null) {
					pageNavigationMenuItemSettings.setPrivatePage(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					pageNavigationMenuItemSettings.setTitle(
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
// LIFERAY-REST-BUILDER-HASH:-791773011