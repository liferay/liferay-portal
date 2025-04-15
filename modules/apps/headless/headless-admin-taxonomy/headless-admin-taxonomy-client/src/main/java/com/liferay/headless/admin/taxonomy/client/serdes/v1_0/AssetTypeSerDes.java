/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.client.serdes.v1_0;

import com.liferay.headless.admin.taxonomy.client.dto.v1_0.AssetType;
import com.liferay.headless.admin.taxonomy.client.json.BaseJSONParser;

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
public class AssetTypeSerDes {

	public static AssetType toDTO(String json) {
		AssetTypeJSONParser assetTypeJSONParser = new AssetTypeJSONParser();

		return assetTypeJSONParser.parseToDTO(json);
	}

	public static AssetType[] toDTOs(String json) {
		AssetTypeJSONParser assetTypeJSONParser = new AssetTypeJSONParser();

		return assetTypeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetType assetType) {
		if (assetType == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assetType.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(assetType.getRequired());
		}

		if (assetType.getSubtype() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtype\": ");

			sb.append("\"");

			sb.append(_escape(assetType.getSubtype()));

			sb.append("\"");
		}

		if (assetType.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(assetType.getType()));

			sb.append("\"");
		}

		if (assetType.getTypeId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeId\": ");

			sb.append(assetType.getTypeId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetTypeJSONParser assetTypeJSONParser = new AssetTypeJSONParser();

		return assetTypeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AssetType assetType) {
		if (assetType == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assetType.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put("required", String.valueOf(assetType.getRequired()));
		}

		if (assetType.getSubtype() == null) {
			map.put("subtype", null);
		}
		else {
			map.put("subtype", String.valueOf(assetType.getSubtype()));
		}

		if (assetType.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(assetType.getType()));
		}

		if (assetType.getTypeId() == null) {
			map.put("typeId", null);
		}
		else {
			map.put("typeId", String.valueOf(assetType.getTypeId()));
		}

		return map;
	}

	public static class AssetTypeJSONParser extends BaseJSONParser<AssetType> {

		@Override
		protected AssetType createDTO() {
			return new AssetType();
		}

		@Override
		protected AssetType[] createDTOArray(int size) {
			return new AssetType[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subtype")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "typeId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetType assetType, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					assetType.setRequired((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subtype")) {
				if (jsonParserFieldValue != null) {
					assetType.setSubtype((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					assetType.setType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "typeId")) {
				if (jsonParserFieldValue != null) {
					assetType.setTypeId(
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