/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.AssetLibrary;
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
public class AssetLibrarySerDes {

	public static AssetLibrary toDTO(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToDTO(json);
	}

	public static AssetLibrary[] toDTOs(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetLibrary assetLibrary) {
		if (assetLibrary == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assetLibrary.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(assetLibrary.getId());
		}

		if (assetLibrary.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(assetLibrary.getName()));

			sb.append("\"");
		}

		if (assetLibrary.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(assetLibrary.getName_i18n()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AssetLibrary assetLibrary) {
		if (assetLibrary == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assetLibrary.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(assetLibrary.getId()));
		}

		if (assetLibrary.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(assetLibrary.getName()));
		}

		if (assetLibrary.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(assetLibrary.getName_i18n()));
		}

		return map;
	}

	public static class AssetLibraryJSONParser
		extends BaseJSONParser<AssetLibrary> {

		@Override
		protected AssetLibrary createDTO() {
			return new AssetLibrary();
		}

		@Override
		protected AssetLibrary[] createDTOArray(int size) {
			return new AssetLibrary[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetLibrary assetLibrary, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
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