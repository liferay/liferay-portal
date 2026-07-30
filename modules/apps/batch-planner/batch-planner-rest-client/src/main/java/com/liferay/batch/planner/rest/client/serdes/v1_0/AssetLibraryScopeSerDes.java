/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.client.serdes.v1_0;

import com.liferay.batch.planner.rest.client.dto.v1_0.AssetLibraryScope;
import com.liferay.batch.planner.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Matija Petanjek
 * @generated
 */
@Generated("")
public class AssetLibraryScopeSerDes {

	public static AssetLibraryScope toDTO(String json) {
		AssetLibraryScopeJSONParser assetLibraryScopeJSONParser =
			new AssetLibraryScopeJSONParser();

		return assetLibraryScopeJSONParser.parseToDTO(json);
	}

	public static AssetLibraryScope[] toDTOs(String json) {
		AssetLibraryScopeJSONParser assetLibraryScopeJSONParser =
			new AssetLibraryScopeJSONParser();

		return assetLibraryScopeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetLibraryScope assetLibraryScope) {
		if (assetLibraryScope == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assetLibraryScope.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(assetLibraryScope.getLabel()));

			sb.append("\"");
		}

		if (assetLibraryScope.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append(assetLibraryScope.getValue());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetLibraryScopeJSONParser assetLibraryScopeJSONParser =
			new AssetLibraryScopeJSONParser();

		return assetLibraryScopeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AssetLibraryScope assetLibraryScope) {

		if (assetLibraryScope == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assetLibraryScope.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(assetLibraryScope.getLabel()));
		}

		if (assetLibraryScope.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(assetLibraryScope.getValue()));
		}

		return map;
	}

	public static class AssetLibraryScopeJSONParser
		extends BaseJSONParser<AssetLibraryScope> {

		@Override
		protected AssetLibraryScope createDTO() {
			return new AssetLibraryScope();
		}

		@Override
		protected AssetLibraryScope[] createDTOArray(int size) {
			return new AssetLibraryScope[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetLibraryScope assetLibraryScope, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					assetLibraryScope.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					assetLibraryScope.setValue(
						Long.valueOf((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1987613380