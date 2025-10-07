/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.serdes.v1_0;

import com.liferay.headless.cms.client.dto.v1_0.AssetUsage;
import com.liferay.headless.cms.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
public class AssetUsageSerDes {

	public static AssetUsage toDTO(String json) {
		AssetUsageJSONParser assetUsageJSONParser = new AssetUsageJSONParser();

		return assetUsageJSONParser.parseToDTO(json);
	}

	public static AssetUsage[] toDTOs(String json) {
		AssetUsageJSONParser assetUsageJSONParser = new AssetUsageJSONParser();

		return assetUsageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetUsage assetUsage) {
		if (assetUsage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assetUsage.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(assetUsage.getName()));

			sb.append("\"");
		}

		if (assetUsage.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(assetUsage.getType()));

			sb.append("\"");
		}

		if (assetUsage.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(assetUsage.getUrl()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetUsageJSONParser assetUsageJSONParser = new AssetUsageJSONParser();

		return assetUsageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AssetUsage assetUsage) {
		if (assetUsage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assetUsage.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(assetUsage.getName()));
		}

		if (assetUsage.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(assetUsage.getType()));
		}

		if (assetUsage.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put("url", String.valueOf(assetUsage.getUrl()));
		}

		return map;
	}

	public static class AssetUsageJSONParser
		extends BaseJSONParser<AssetUsage> {

		@Override
		protected AssetUsage createDTO() {
			return new AssetUsage();
		}

		@Override
		protected AssetUsage[] createDTOArray(int size) {
			return new AssetUsage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetUsage assetUsage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					assetUsage.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					assetUsage.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					assetUsage.setUrl((String)jsonParserFieldValue);
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