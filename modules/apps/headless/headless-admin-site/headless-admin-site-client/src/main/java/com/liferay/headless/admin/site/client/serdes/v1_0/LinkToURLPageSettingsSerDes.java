/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.LinkToURLPageSettings;
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
public class LinkToURLPageSettingsSerDes {

	public static LinkToURLPageSettings toDTO(String json) {
		LinkToURLPageSettingsJSONParser linkToURLPageSettingsJSONParser =
			new LinkToURLPageSettingsJSONParser();

		return linkToURLPageSettingsJSONParser.parseToDTO(json);
	}

	public static LinkToURLPageSettings[] toDTOs(String json) {
		LinkToURLPageSettingsJSONParser linkToURLPageSettingsJSONParser =
			new LinkToURLPageSettingsJSONParser();

		return linkToURLPageSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(LinkToURLPageSettings linkToURLPageSettings) {
		if (linkToURLPageSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (linkToURLPageSettings.getPageURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageURL\": ");

			sb.append("\"");

			sb.append(_escape(linkToURLPageSettings.getPageURL()));

			sb.append("\"");
		}

		if (linkToURLPageSettings.getHiddenFromNavigation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hiddenFromNavigation\": ");

			sb.append(linkToURLPageSettings.getHiddenFromNavigation());
		}

		if (linkToURLPageSettings.getNavigationSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationSettings\": ");

			sb.append(
				String.valueOf(linkToURLPageSettings.getNavigationSettings()));
		}

		if (linkToURLPageSettings.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(linkToURLPageSettings.getPriority());
		}

		if (linkToURLPageSettings.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(linkToURLPageSettings.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		LinkToURLPageSettingsJSONParser linkToURLPageSettingsJSONParser =
			new LinkToURLPageSettingsJSONParser();

		return linkToURLPageSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		LinkToURLPageSettings linkToURLPageSettings) {

		if (linkToURLPageSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (linkToURLPageSettings.getPageURL() == null) {
			map.put("pageURL", null);
		}
		else {
			map.put(
				"pageURL", String.valueOf(linkToURLPageSettings.getPageURL()));
		}

		if (linkToURLPageSettings.getHiddenFromNavigation() == null) {
			map.put("hiddenFromNavigation", null);
		}
		else {
			map.put(
				"hiddenFromNavigation",
				String.valueOf(
					linkToURLPageSettings.getHiddenFromNavigation()));
		}

		if (linkToURLPageSettings.getNavigationSettings() == null) {
			map.put("navigationSettings", null);
		}
		else {
			map.put(
				"navigationSettings",
				String.valueOf(linkToURLPageSettings.getNavigationSettings()));
		}

		if (linkToURLPageSettings.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority",
				String.valueOf(linkToURLPageSettings.getPriority()));
		}

		if (linkToURLPageSettings.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(linkToURLPageSettings.getType()));
		}

		return map;
	}

	public static class LinkToURLPageSettingsJSONParser
		extends BaseJSONParser<LinkToURLPageSettings> {

		@Override
		protected LinkToURLPageSettings createDTO() {
			return new LinkToURLPageSettings();
		}

		@Override
		protected LinkToURLPageSettings[] createDTOArray(int size) {
			return new LinkToURLPageSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "pageURL")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "hiddenFromNavigation")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationSettings")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			LinkToURLPageSettings linkToURLPageSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "pageURL")) {
				if (jsonParserFieldValue != null) {
					linkToURLPageSettings.setPageURL(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "hiddenFromNavigation")) {

				if (jsonParserFieldValue != null) {
					linkToURLPageSettings.setHiddenFromNavigation(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationSettings")) {

				if (jsonParserFieldValue != null) {
					linkToURLPageSettings.setNavigationSettings(
						SitePageNavigationSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					linkToURLPageSettings.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					linkToURLPageSettings.setType(
						LinkToURLPageSettings.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-445225544