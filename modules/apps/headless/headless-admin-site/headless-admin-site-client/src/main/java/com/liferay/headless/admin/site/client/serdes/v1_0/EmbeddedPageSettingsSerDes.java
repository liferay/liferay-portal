/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.EmbeddedPageSettings;
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
public class EmbeddedPageSettingsSerDes {

	public static EmbeddedPageSettings toDTO(String json) {
		EmbeddedPageSettingsJSONParser embeddedPageSettingsJSONParser =
			new EmbeddedPageSettingsJSONParser();

		return embeddedPageSettingsJSONParser.parseToDTO(json);
	}

	public static EmbeddedPageSettings[] toDTOs(String json) {
		EmbeddedPageSettingsJSONParser embeddedPageSettingsJSONParser =
			new EmbeddedPageSettingsJSONParser();

		return embeddedPageSettingsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(EmbeddedPageSettings embeddedPageSettings) {
		if (embeddedPageSettings == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (embeddedPageSettings.getPageURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageURL\": ");

			sb.append("\"");

			sb.append(_escape(embeddedPageSettings.getPageURL()));

			sb.append("\"");
		}

		if (embeddedPageSettings.getHiddenFromNavigation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hiddenFromNavigation\": ");

			sb.append(embeddedPageSettings.getHiddenFromNavigation());
		}

		if (embeddedPageSettings.getNavigationSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationSettings\": ");

			sb.append(
				String.valueOf(embeddedPageSettings.getNavigationSettings()));
		}

		if (embeddedPageSettings.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(embeddedPageSettings.getPriority());
		}

		if (embeddedPageSettings.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(embeddedPageSettings.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EmbeddedPageSettingsJSONParser embeddedPageSettingsJSONParser =
			new EmbeddedPageSettingsJSONParser();

		return embeddedPageSettingsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		EmbeddedPageSettings embeddedPageSettings) {

		if (embeddedPageSettings == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (embeddedPageSettings.getPageURL() == null) {
			map.put("pageURL", null);
		}
		else {
			map.put(
				"pageURL", String.valueOf(embeddedPageSettings.getPageURL()));
		}

		if (embeddedPageSettings.getHiddenFromNavigation() == null) {
			map.put("hiddenFromNavigation", null);
		}
		else {
			map.put(
				"hiddenFromNavigation",
				String.valueOf(embeddedPageSettings.getHiddenFromNavigation()));
		}

		if (embeddedPageSettings.getNavigationSettings() == null) {
			map.put("navigationSettings", null);
		}
		else {
			map.put(
				"navigationSettings",
				String.valueOf(embeddedPageSettings.getNavigationSettings()));
		}

		if (embeddedPageSettings.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority", String.valueOf(embeddedPageSettings.getPriority()));
		}

		if (embeddedPageSettings.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(embeddedPageSettings.getType()));
		}

		return map;
	}

	public static class EmbeddedPageSettingsJSONParser
		extends BaseJSONParser<EmbeddedPageSettings> {

		@Override
		protected EmbeddedPageSettings createDTO() {
			return new EmbeddedPageSettings();
		}

		@Override
		protected EmbeddedPageSettings[] createDTOArray(int size) {
			return new EmbeddedPageSettings[size];
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
			EmbeddedPageSettings embeddedPageSettings,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "pageURL")) {
				if (jsonParserFieldValue != null) {
					embeddedPageSettings.setPageURL(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "hiddenFromNavigation")) {

				if (jsonParserFieldValue != null) {
					embeddedPageSettings.setHiddenFromNavigation(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationSettings")) {

				if (jsonParserFieldValue != null) {
					embeddedPageSettings.setNavigationSettings(
						SitePageNavigationSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					embeddedPageSettings.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					embeddedPageSettings.setType(
						EmbeddedPageSettings.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-17666234