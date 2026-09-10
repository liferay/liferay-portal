/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.EmbeddedPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.LinkToPagePageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.LinkToURLPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSetPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSettings;
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
public class PageSettingsSerDes {

	public static PageSettings toDTO(String json) {
		PageSettingsJSONParser pageSettingsJSONParser =
			new PageSettingsJSONParser();

		return pageSettingsJSONParser.parseToDTO(json);
	}

	public static PageSettings[] toDTOs(String json) {
		PageSettingsJSONParser pageSettingsJSONParser =
			new PageSettingsJSONParser();

		return pageSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageSettings pageSettings) {
		if (pageSettings == null) {
			return "null";
		}

		PageSettings.Type type = pageSettings.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("ContentPageSettings")) {
				return ContentPageSettingsSerDes.toJSON(
					(ContentPageSettings)pageSettings);
			}

			if (typeString.equals("EmbeddedPageSettings")) {
				return EmbeddedPageSettingsSerDes.toJSON(
					(EmbeddedPageSettings)pageSettings);
			}

			if (typeString.equals("LinkToPagePageSettings")) {
				return LinkToPagePageSettingsSerDes.toJSON(
					(LinkToPagePageSettings)pageSettings);
			}

			if (typeString.equals("LinkToURLPageSettings")) {
				return LinkToURLPageSettingsSerDes.toJSON(
					(LinkToURLPageSettings)pageSettings);
			}

			if (typeString.equals("PageSetPageSettings")) {
				return PageSetPageSettingsSerDes.toJSON(
					(PageSetPageSettings)pageSettings);
			}

			if (typeString.equals("WidgetPageSettings")) {
				return WidgetPageSettingsSerDes.toJSON(
					(WidgetPageSettings)pageSettings);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		PageSettingsJSONParser pageSettingsJSONParser =
			new PageSettingsJSONParser();

		return pageSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PageSettings pageSettings) {
		if (pageSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageSettings.getHiddenFromNavigation() == null) {
			map.put("hiddenFromNavigation", null);
		}
		else {
			map.put(
				"hiddenFromNavigation",
				String.valueOf(pageSettings.getHiddenFromNavigation()));
		}

		if (pageSettings.getNavigationSettings() == null) {
			map.put("navigationSettings", null);
		}
		else {
			map.put(
				"navigationSettings",
				String.valueOf(pageSettings.getNavigationSettings()));
		}

		if (pageSettings.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(pageSettings.getPriority()));
		}

		if (pageSettings.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(pageSettings.getType()));
		}

		return map;
	}

	public static class PageSettingsJSONParser
		extends BaseJSONParser<PageSettings> {

		@Override
		protected PageSettings createDTO() {
			return null;
		}

		@Override
		protected PageSettings[] createDTOArray(int size) {
			return new PageSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "hiddenFromNavigation")) {
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
		public PageSettings parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("ContentPageSettings")) {
					return ContentPageSettings.toDTO(json);
				}

				if (typeString.equals("EmbeddedPageSettings")) {
					return EmbeddedPageSettings.toDTO(json);
				}

				if (typeString.equals("LinkToPagePageSettings")) {
					return LinkToPagePageSettings.toDTO(json);
				}

				if (typeString.equals("LinkToURLPageSettings")) {
					return LinkToURLPageSettings.toDTO(json);
				}

				if (typeString.equals("PageSetPageSettings")) {
					return PageSetPageSettings.toDTO(json);
				}

				if (typeString.equals("WidgetPageSettings")) {
					return WidgetPageSettings.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			PageSettings pageSettings, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "hiddenFromNavigation")) {
				if (jsonParserFieldValue != null) {
					pageSettings.setHiddenFromNavigation(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationSettings")) {

				if (jsonParserFieldValue != null) {
					pageSettings.setNavigationSettings(
						SitePageNavigationSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					pageSettings.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					pageSettings.setType(
						PageSettings.Type.create((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:42355235