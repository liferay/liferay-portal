/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.SkuVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class SkuVirtualSettingsFileEntrySerDes {

	public static SkuVirtualSettingsFileEntry toDTO(String json) {
		SkuVirtualSettingsFileEntryJSONParser
			skuVirtualSettingsFileEntryJSONParser =
				new SkuVirtualSettingsFileEntryJSONParser();

		return skuVirtualSettingsFileEntryJSONParser.parseToDTO(json);
	}

	public static SkuVirtualSettingsFileEntry[] toDTOs(String json) {
		SkuVirtualSettingsFileEntryJSONParser
			skuVirtualSettingsFileEntryJSONParser =
				new SkuVirtualSettingsFileEntryJSONParser();

		return skuVirtualSettingsFileEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry) {

		if (skuVirtualSettingsFileEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (skuVirtualSettingsFileEntry.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(skuVirtualSettingsFileEntry.getActions()));
		}

		if (skuVirtualSettingsFileEntry.getAttachment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachment\": ");

			sb.append("\"");

			sb.append(_escape(skuVirtualSettingsFileEntry.getAttachment()));

			sb.append("\"");
		}

		if (skuVirtualSettingsFileEntry.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(skuVirtualSettingsFileEntry.getId());
		}

		if (skuVirtualSettingsFileEntry.getSrc() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"src\": ");

			sb.append("\"");

			sb.append(_escape(skuVirtualSettingsFileEntry.getSrc()));

			sb.append("\"");
		}

		if (skuVirtualSettingsFileEntry.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(skuVirtualSettingsFileEntry.getUrl()));

			sb.append("\"");
		}

		if (skuVirtualSettingsFileEntry.getVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append("\"");

			sb.append(_escape(skuVirtualSettingsFileEntry.getVersion()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SkuVirtualSettingsFileEntryJSONParser
			skuVirtualSettingsFileEntryJSONParser =
				new SkuVirtualSettingsFileEntryJSONParser();

		return skuVirtualSettingsFileEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry) {

		if (skuVirtualSettingsFileEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (skuVirtualSettingsFileEntry.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(skuVirtualSettingsFileEntry.getActions()));
		}

		if (skuVirtualSettingsFileEntry.getAttachment() == null) {
			map.put("attachment", null);
		}
		else {
			map.put(
				"attachment",
				String.valueOf(skuVirtualSettingsFileEntry.getAttachment()));
		}

		if (skuVirtualSettingsFileEntry.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(skuVirtualSettingsFileEntry.getId()));
		}

		if (skuVirtualSettingsFileEntry.getSrc() == null) {
			map.put("src", null);
		}
		else {
			map.put(
				"src", String.valueOf(skuVirtualSettingsFileEntry.getSrc()));
		}

		if (skuVirtualSettingsFileEntry.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put(
				"url", String.valueOf(skuVirtualSettingsFileEntry.getUrl()));
		}

		if (skuVirtualSettingsFileEntry.getVersion() == null) {
			map.put("version", null);
		}
		else {
			map.put(
				"version",
				String.valueOf(skuVirtualSettingsFileEntry.getVersion()));
		}

		return map;
	}

	public static class SkuVirtualSettingsFileEntryJSONParser
		extends BaseJSONParser<SkuVirtualSettingsFileEntry> {

		@Override
		protected SkuVirtualSettingsFileEntry createDTO() {
			return new SkuVirtualSettingsFileEntry();
		}

		@Override
		protected SkuVirtualSettingsFileEntry[] createDTOArray(int size) {
			return new SkuVirtualSettingsFileEntry[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "attachment")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "src")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SkuVirtualSettingsFileEntry skuVirtualSettingsFileEntry,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					skuVirtualSettingsFileEntry.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "attachment")) {
				if (jsonParserFieldValue != null) {
					skuVirtualSettingsFileEntry.setAttachment(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					skuVirtualSettingsFileEntry.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "src")) {
				if (jsonParserFieldValue != null) {
					skuVirtualSettingsFileEntry.setSrc(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					skuVirtualSettingsFileEntry.setUrl(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				if (jsonParserFieldValue != null) {
					skuVirtualSettingsFileEntry.setVersion(
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