/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.CustomMetaTag;
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
public class ContentPageSettingsSerDes {

	public static ContentPageSettings toDTO(String json) {
		ContentPageSettingsJSONParser contentPageSettingsJSONParser =
			new ContentPageSettingsJSONParser();

		return contentPageSettingsJSONParser.parseToDTO(json);
	}

	public static ContentPageSettings[] toDTOs(String json) {
		ContentPageSettingsJSONParser contentPageSettingsJSONParser =
			new ContentPageSettingsJSONParser();

		return contentPageSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ContentPageSettings contentPageSettings) {
		if (contentPageSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contentPageSettings.getCustomMetaTags() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customMetaTags\": ");

			sb.append("[");

			for (int i = 0; i < contentPageSettings.getCustomMetaTags().length;
				 i++) {

				sb.append(
					String.valueOf(contentPageSettings.getCustomMetaTags()[i]));

				if ((i + 1) < contentPageSettings.getCustomMetaTags().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentPageSettings.getHiddenFromNavigation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hiddenFromNavigation\": ");

			sb.append(contentPageSettings.getHiddenFromNavigation());
		}

		if (contentPageSettings.getNavigationMenuSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuSettings\": ");

			sb.append(
				String.valueOf(
					contentPageSettings.getNavigationMenuSettings()));
		}

		if (contentPageSettings.getOpenGraphSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"openGraphSettings\": ");

			sb.append(
				String.valueOf(contentPageSettings.getOpenGraphSettings()));
		}

		if (contentPageSettings.getSeoSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seoSettings\": ");

			sb.append(String.valueOf(contentPageSettings.getSeoSettings()));
		}

		if (contentPageSettings.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(contentPageSettings.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContentPageSettingsJSONParser contentPageSettingsJSONParser =
			new ContentPageSettingsJSONParser();

		return contentPageSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ContentPageSettings contentPageSettings) {

		if (contentPageSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contentPageSettings.getCustomMetaTags() == null) {
			map.put("customMetaTags", null);
		}
		else {
			map.put(
				"customMetaTags",
				String.valueOf(contentPageSettings.getCustomMetaTags()));
		}

		if (contentPageSettings.getHiddenFromNavigation() == null) {
			map.put("hiddenFromNavigation", null);
		}
		else {
			map.put(
				"hiddenFromNavigation",
				String.valueOf(contentPageSettings.getHiddenFromNavigation()));
		}

		if (contentPageSettings.getNavigationMenuSettings() == null) {
			map.put("navigationMenuSettings", null);
		}
		else {
			map.put(
				"navigationMenuSettings",
				String.valueOf(
					contentPageSettings.getNavigationMenuSettings()));
		}

		if (contentPageSettings.getOpenGraphSettings() == null) {
			map.put("openGraphSettings", null);
		}
		else {
			map.put(
				"openGraphSettings",
				String.valueOf(contentPageSettings.getOpenGraphSettings()));
		}

		if (contentPageSettings.getSeoSettings() == null) {
			map.put("seoSettings", null);
		}
		else {
			map.put(
				"seoSettings",
				String.valueOf(contentPageSettings.getSeoSettings()));
		}

		if (contentPageSettings.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(contentPageSettings.getType()));
		}

		return map;
	}

	public static class ContentPageSettingsJSONParser
		extends BaseJSONParser<ContentPageSettings> {

		@Override
		protected ContentPageSettings createDTO() {
			return new ContentPageSettings();
		}

		@Override
		protected ContentPageSettings[] createDTOArray(int size) {
			return new ContentPageSettings[size];
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
			else if (Objects.equals(
						jsonParserFieldName, "navigationMenuSettings")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "openGraphSettings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "seoSettings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContentPageSettings contentPageSettings, String jsonParserFieldName,
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

					contentPageSettings.setCustomMetaTags(customMetaTagsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "hiddenFromNavigation")) {

				if (jsonParserFieldValue != null) {
					contentPageSettings.setHiddenFromNavigation(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationMenuSettings")) {

				if (jsonParserFieldValue != null) {
					contentPageSettings.setNavigationMenuSettings(
						NavigationMenuSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "openGraphSettings")) {
				if (jsonParserFieldValue != null) {
					contentPageSettings.setOpenGraphSettings(
						OpenGraphSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "seoSettings")) {
				if (jsonParserFieldValue != null) {
					contentPageSettings.setSeoSettings(
						SEOSettingsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					contentPageSettings.setType(
						ContentPageSettings.Type.create(
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