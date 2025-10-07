/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CustomMetaTag;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSettings;
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
public class WidgetPageSettingsSerDes {

	public static WidgetPageSettings toDTO(String json) {
		WidgetPageSettingsJSONParser widgetPageSettingsJSONParser =
			new WidgetPageSettingsJSONParser();

		return widgetPageSettingsJSONParser.parseToDTO(json);
	}

	public static WidgetPageSettings[] toDTOs(String json) {
		WidgetPageSettingsJSONParser widgetPageSettingsJSONParser =
			new WidgetPageSettingsJSONParser();

		return widgetPageSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WidgetPageSettings widgetPageSettings) {
		if (widgetPageSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (widgetPageSettings.getCustomizable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customizable\": ");

			sb.append(widgetPageSettings.getCustomizable());
		}

		if (widgetPageSettings.getCustomizableSectionIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customizableSectionIds\": ");

			sb.append("[");

			for (int i = 0;
				 i < widgetPageSettings.getCustomizableSectionIds().length;
				 i++) {

				sb.append(
					_toJSON(widgetPageSettings.getCustomizableSectionIds()[i]));

				if ((i + 1) <
						widgetPageSettings.getCustomizableSectionIds().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (widgetPageSettings.getInheritChanges() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inheritChanges\": ");

			sb.append(widgetPageSettings.getInheritChanges());
		}

		if (widgetPageSettings.getLayoutTemplateId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"layoutTemplateId\": ");

			sb.append("\"");

			sb.append(_escape(widgetPageSettings.getLayoutTemplateId()));

			sb.append("\"");
		}

		if (widgetPageSettings.getWidgetPageTemplateReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetPageTemplateReference\": ");

			sb.append(
				String.valueOf(
					widgetPageSettings.getWidgetPageTemplateReference()));
		}

		if (widgetPageSettings.getCustomMetaTags() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customMetaTags\": ");

			sb.append("[");

			for (int i = 0; i < widgetPageSettings.getCustomMetaTags().length;
				 i++) {

				sb.append(
					String.valueOf(widgetPageSettings.getCustomMetaTags()[i]));

				if ((i + 1) < widgetPageSettings.getCustomMetaTags().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (widgetPageSettings.getHiddenFromNavigation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hiddenFromNavigation\": ");

			sb.append(widgetPageSettings.getHiddenFromNavigation());
		}

		if (widgetPageSettings.getNavigationSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationSettings\": ");

			sb.append(
				String.valueOf(widgetPageSettings.getNavigationSettings()));
		}

		if (widgetPageSettings.getOpenGraphSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"openGraphSettings\": ");

			sb.append(
				String.valueOf(widgetPageSettings.getOpenGraphSettings()));
		}

		if (widgetPageSettings.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(widgetPageSettings.getPriority());
		}

		if (widgetPageSettings.getQueryString() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"queryString\": ");

			sb.append("\"");

			sb.append(_escape(widgetPageSettings.getQueryString()));

			sb.append("\"");
		}

		if (widgetPageSettings.getSeoSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seoSettings\": ");

			sb.append(String.valueOf(widgetPageSettings.getSeoSettings()));
		}

		if (widgetPageSettings.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(widgetPageSettings.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WidgetPageSettingsJSONParser widgetPageSettingsJSONParser =
			new WidgetPageSettingsJSONParser();

		return widgetPageSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WidgetPageSettings widgetPageSettings) {

		if (widgetPageSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (widgetPageSettings.getCustomizable() == null) {
			map.put("customizable", null);
		}
		else {
			map.put(
				"customizable",
				String.valueOf(widgetPageSettings.getCustomizable()));
		}

		if (widgetPageSettings.getCustomizableSectionIds() == null) {
			map.put("customizableSectionIds", null);
		}
		else {
			map.put(
				"customizableSectionIds",
				String.valueOf(widgetPageSettings.getCustomizableSectionIds()));
		}

		if (widgetPageSettings.getInheritChanges() == null) {
			map.put("inheritChanges", null);
		}
		else {
			map.put(
				"inheritChanges",
				String.valueOf(widgetPageSettings.getInheritChanges()));
		}

		if (widgetPageSettings.getLayoutTemplateId() == null) {
			map.put("layoutTemplateId", null);
		}
		else {
			map.put(
				"layoutTemplateId",
				String.valueOf(widgetPageSettings.getLayoutTemplateId()));
		}

		if (widgetPageSettings.getWidgetPageTemplateReference() == null) {
			map.put("widgetPageTemplateReference", null);
		}
		else {
			map.put(
				"widgetPageTemplateReference",
				String.valueOf(
					widgetPageSettings.getWidgetPageTemplateReference()));
		}

		if (widgetPageSettings.getCustomMetaTags() == null) {
			map.put("customMetaTags", null);
		}
		else {
			map.put(
				"customMetaTags",
				String.valueOf(widgetPageSettings.getCustomMetaTags()));
		}

		if (widgetPageSettings.getHiddenFromNavigation() == null) {
			map.put("hiddenFromNavigation", null);
		}
		else {
			map.put(
				"hiddenFromNavigation",
				String.valueOf(widgetPageSettings.getHiddenFromNavigation()));
		}

		if (widgetPageSettings.getNavigationSettings() == null) {
			map.put("navigationSettings", null);
		}
		else {
			map.put(
				"navigationSettings",
				String.valueOf(widgetPageSettings.getNavigationSettings()));
		}

		if (widgetPageSettings.getOpenGraphSettings() == null) {
			map.put("openGraphSettings", null);
		}
		else {
			map.put(
				"openGraphSettings",
				String.valueOf(widgetPageSettings.getOpenGraphSettings()));
		}

		if (widgetPageSettings.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority", String.valueOf(widgetPageSettings.getPriority()));
		}

		if (widgetPageSettings.getQueryString() == null) {
			map.put("queryString", null);
		}
		else {
			map.put(
				"queryString",
				String.valueOf(widgetPageSettings.getQueryString()));
		}

		if (widgetPageSettings.getSeoSettings() == null) {
			map.put("seoSettings", null);
		}
		else {
			map.put(
				"seoSettings",
				String.valueOf(widgetPageSettings.getSeoSettings()));
		}

		if (widgetPageSettings.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(widgetPageSettings.getType()));
		}

		return map;
	}

	public static class WidgetPageSettingsJSONParser
		extends BaseJSONParser<WidgetPageSettings> {

		@Override
		protected WidgetPageSettings createDTO() {
			return new WidgetPageSettings();
		}

		@Override
		protected WidgetPageSettings[] createDTOArray(int size) {
			return new WidgetPageSettings[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "customizable")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "customizableSectionIds")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "inheritChanges")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "layoutTemplateId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "widgetPageTemplateReference")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customMetaTags")) {
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
			else if (Objects.equals(jsonParserFieldName, "openGraphSettings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "queryString")) {
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
			WidgetPageSettings widgetPageSettings, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "customizable")) {
				if (jsonParserFieldValue != null) {
					widgetPageSettings.setCustomizable(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "customizableSectionIds")) {

				if (jsonParserFieldValue != null) {
					widgetPageSettings.setCustomizableSectionIds(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "inheritChanges")) {
				if (jsonParserFieldValue != null) {
					widgetPageSettings.setInheritChanges(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "layoutTemplateId")) {
				if (jsonParserFieldValue != null) {
					widgetPageSettings.setLayoutTemplateId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "widgetPageTemplateReference")) {

				if (jsonParserFieldValue != null) {
					widgetPageSettings.setWidgetPageTemplateReference(
						ItemExternalReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customMetaTags")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					CustomMetaTag[] customMetaTagsArray =
						new CustomMetaTag[jsonParserFieldValues.length];

					for (int i = 0; i < customMetaTagsArray.length; i++) {
						customMetaTagsArray[i] = CustomMetaTagSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					widgetPageSettings.setCustomMetaTags(customMetaTagsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "hiddenFromNavigation")) {

				if (jsonParserFieldValue != null) {
					widgetPageSettings.setHiddenFromNavigation(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationSettings")) {

				if (jsonParserFieldValue != null) {
					widgetPageSettings.setNavigationSettings(
						NavigationSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "openGraphSettings")) {
				if (jsonParserFieldValue != null) {
					widgetPageSettings.setOpenGraphSettings(
						OpenGraphSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					widgetPageSettings.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "queryString")) {
				if (jsonParserFieldValue != null) {
					widgetPageSettings.setQueryString(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "seoSettings")) {
				if (jsonParserFieldValue != null) {
					widgetPageSettings.setSeoSettings(
						SEOSettingsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					widgetPageSettings.setType(
						WidgetPageSettings.Type.create(
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