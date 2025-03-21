/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductVirtualSettingsFileEntrySerDes {

	public static ProductVirtualSettingsFileEntry toDTO(String json) {
		ProductVirtualSettingsFileEntryJSONParser
			productVirtualSettingsFileEntryJSONParser =
				new ProductVirtualSettingsFileEntryJSONParser();

		return productVirtualSettingsFileEntryJSONParser.parseToDTO(json);
	}

	public static ProductVirtualSettingsFileEntry[] toDTOs(String json) {
		ProductVirtualSettingsFileEntryJSONParser
			productVirtualSettingsFileEntryJSONParser =
				new ProductVirtualSettingsFileEntryJSONParser();

		return productVirtualSettingsFileEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry) {

		if (productVirtualSettingsFileEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productVirtualSettingsFileEntry.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(productVirtualSettingsFileEntry.getActions()));
		}

		if (productVirtualSettingsFileEntry.getAttachment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachment\": ");

			sb.append("\"");

			sb.append(_escape(productVirtualSettingsFileEntry.getAttachment()));

			sb.append("\"");
		}

		if (productVirtualSettingsFileEntry.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(productVirtualSettingsFileEntry.getId());
		}

		if (productVirtualSettingsFileEntry.getSrc() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"src\": ");

			sb.append("\"");

			sb.append(_escape(productVirtualSettingsFileEntry.getSrc()));

			sb.append("\"");
		}

		if (productVirtualSettingsFileEntry.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(productVirtualSettingsFileEntry.getUrl()));

			sb.append("\"");
		}

		if (productVirtualSettingsFileEntry.getVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append("\"");

			sb.append(_escape(productVirtualSettingsFileEntry.getVersion()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductVirtualSettingsFileEntryJSONParser
			productVirtualSettingsFileEntryJSONParser =
				new ProductVirtualSettingsFileEntryJSONParser();

		return productVirtualSettingsFileEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry) {

		if (productVirtualSettingsFileEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productVirtualSettingsFileEntry.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(productVirtualSettingsFileEntry.getActions()));
		}

		if (productVirtualSettingsFileEntry.getAttachment() == null) {
			map.put("attachment", null);
		}
		else {
			map.put(
				"attachment",
				String.valueOf(
					productVirtualSettingsFileEntry.getAttachment()));
		}

		if (productVirtualSettingsFileEntry.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put(
				"id", String.valueOf(productVirtualSettingsFileEntry.getId()));
		}

		if (productVirtualSettingsFileEntry.getSrc() == null) {
			map.put("src", null);
		}
		else {
			map.put(
				"src",
				String.valueOf(productVirtualSettingsFileEntry.getSrc()));
		}

		if (productVirtualSettingsFileEntry.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put(
				"url",
				String.valueOf(productVirtualSettingsFileEntry.getUrl()));
		}

		if (productVirtualSettingsFileEntry.getVersion() == null) {
			map.put("version", null);
		}
		else {
			map.put(
				"version",
				String.valueOf(productVirtualSettingsFileEntry.getVersion()));
		}

		return map;
	}

	public static class ProductVirtualSettingsFileEntryJSONParser
		extends BaseJSONParser<ProductVirtualSettingsFileEntry> {

		@Override
		protected ProductVirtualSettingsFileEntry createDTO() {
			return new ProductVirtualSettingsFileEntry();
		}

		@Override
		protected ProductVirtualSettingsFileEntry[] createDTOArray(int size) {
			return new ProductVirtualSettingsFileEntry[size];
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
			ProductVirtualSettingsFileEntry productVirtualSettingsFileEntry,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettingsFileEntry.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "attachment")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettingsFileEntry.setAttachment(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettingsFileEntry.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "src")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettingsFileEntry.setSrc(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettingsFileEntry.setUrl(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				if (jsonParserFieldValue != null) {
					productVirtualSettingsFileEntry.setVersion(
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