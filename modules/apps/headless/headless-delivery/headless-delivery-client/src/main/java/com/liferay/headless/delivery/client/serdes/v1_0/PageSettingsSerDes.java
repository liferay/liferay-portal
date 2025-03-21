/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.CustomMetaTag;
import com.liferay.headless.delivery.client.dto.v1_0.PageSettings;
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

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageSettings.getCustomMetaTags() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customMetaTags\": ");

			sb.append("[");

			for (int i = 0; i < pageSettings.getCustomMetaTags().length; i++) {
				sb.append(String.valueOf(pageSettings.getCustomMetaTags()[i]));

				if ((i + 1) < pageSettings.getCustomMetaTags().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageSettings.getHiddenFromNavigation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hiddenFromNavigation\": ");

			sb.append(pageSettings.getHiddenFromNavigation());
		}

		if (pageSettings.getOpenGraphSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"openGraphSettings\": ");

			sb.append(String.valueOf(pageSettings.getOpenGraphSettings()));
		}

		if (pageSettings.getSeoSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seoSettings\": ");

			sb.append(String.valueOf(pageSettings.getSeoSettings()));
		}

		if (pageSettings.getSitePageNavigationMenuSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitePageNavigationMenuSettings\": ");

			sb.append(
				String.valueOf(
					pageSettings.getSitePageNavigationMenuSettings()));
		}

		sb.append("}");

		return sb.toString();
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

		if (pageSettings.getCustomMetaTags() == null) {
			map.put("customMetaTags", null);
		}
		else {
			map.put(
				"customMetaTags",
				String.valueOf(pageSettings.getCustomMetaTags()));
		}

		if (pageSettings.getHiddenFromNavigation() == null) {
			map.put("hiddenFromNavigation", null);
		}
		else {
			map.put(
				"hiddenFromNavigation",
				String.valueOf(pageSettings.getHiddenFromNavigation()));
		}

		if (pageSettings.getOpenGraphSettings() == null) {
			map.put("openGraphSettings", null);
		}
		else {
			map.put(
				"openGraphSettings",
				String.valueOf(pageSettings.getOpenGraphSettings()));
		}

		if (pageSettings.getSeoSettings() == null) {
			map.put("seoSettings", null);
		}
		else {
			map.put(
				"seoSettings", String.valueOf(pageSettings.getSeoSettings()));
		}

		if (pageSettings.getSitePageNavigationMenuSettings() == null) {
			map.put("sitePageNavigationMenuSettings", null);
		}
		else {
			map.put(
				"sitePageNavigationMenuSettings",
				String.valueOf(
					pageSettings.getSitePageNavigationMenuSettings()));
		}

		return map;
	}

	public static class PageSettingsJSONParser
		extends BaseJSONParser<PageSettings> {

		@Override
		protected PageSettings createDTO() {
			return new PageSettings();
		}

		@Override
		protected PageSettings[] createDTOArray(int size) {
			return new PageSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "customMetaTags")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "hiddenFromNavigation")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "openGraphSettings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "seoSettings")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"sitePageNavigationMenuSettings")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageSettings pageSettings, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "customMetaTags")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					CustomMetaTag[] customMetaTagsArray =
						new CustomMetaTag[jsonParserFieldValues.length];

					for (int i = 0; i < customMetaTagsArray.length; i++) {
						customMetaTagsArray[i] = CustomMetaTagSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					pageSettings.setCustomMetaTags(customMetaTagsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "hiddenFromNavigation")) {

				if (jsonParserFieldValue != null) {
					pageSettings.setHiddenFromNavigation(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "openGraphSettings")) {
				if (jsonParserFieldValue != null) {
					pageSettings.setOpenGraphSettings(
						OpenGraphSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "seoSettings")) {
				if (jsonParserFieldValue != null) {
					pageSettings.setSeoSettings(
						SEOSettingsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"sitePageNavigationMenuSettings")) {

				if (jsonParserFieldValue != null) {
					pageSettings.setSitePageNavigationMenuSettings(
						SitePageNavigationMenuSettingsSerDes.toDTO(
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