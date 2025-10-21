/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.serdes.v1_0;

import com.liferay.bulk.rest.client.dto.v1_0.AssetPermissionAction;
import com.liferay.bulk.rest.client.dto.v1_0.ResetAssetPermissionAction;
import com.liferay.bulk.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class AssetPermissionActionSerDes {

	public static AssetPermissionAction toDTO(String json) {
		AssetPermissionActionJSONParser assetPermissionActionJSONParser =
			new AssetPermissionActionJSONParser();

		return assetPermissionActionJSONParser.parseToDTO(json);
	}

	public static AssetPermissionAction[] toDTOs(String json) {
		AssetPermissionActionJSONParser assetPermissionActionJSONParser =
			new AssetPermissionActionJSONParser();

		return assetPermissionActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetPermissionAction assetPermissionAction) {
		if (assetPermissionAction == null) {
			return "null";
		}

		AssetPermissionAction.Type type = assetPermissionAction.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("ResetAssetPermissionAction")) {
				return ResetAssetPermissionActionSerDes.toJSON(
					(ResetAssetPermissionAction)assetPermissionAction);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		AssetPermissionActionJSONParser assetPermissionActionJSONParser =
			new AssetPermissionActionJSONParser();

		return assetPermissionActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AssetPermissionAction assetPermissionAction) {

		if (assetPermissionAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assetPermissionAction.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(assetPermissionAction.getClassName()));
		}

		if (assetPermissionAction.getClassPK() == null) {
			map.put("classPK", null);
		}
		else {
			map.put(
				"classPK", String.valueOf(assetPermissionAction.getClassPK()));
		}

		if (assetPermissionAction.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(assetPermissionAction.getType()));
		}

		return map;
	}

	public static class AssetPermissionActionJSONParser
		extends BaseJSONParser<AssetPermissionAction> {

		@Override
		protected AssetPermissionAction createDTO() {
			return null;
		}

		@Override
		protected AssetPermissionAction[] createDTOArray(int size) {
			return new AssetPermissionAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public AssetPermissionAction parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("ResetAssetPermissionAction")) {
					return ResetAssetPermissionAction.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			AssetPermissionAction assetPermissionAction,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					assetPermissionAction.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				if (jsonParserFieldValue != null) {
					assetPermissionAction.setClassPK(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					assetPermissionAction.setType(
						AssetPermissionAction.Type.create(
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