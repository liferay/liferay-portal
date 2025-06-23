/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.AssetLibraryTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class AssetLibraryTestEntitySerDes {

	public static AssetLibraryTestEntity toDTO(String json) {
		AssetLibraryTestEntityJSONParser assetLibraryTestEntityJSONParser =
			new AssetLibraryTestEntityJSONParser();

		return assetLibraryTestEntityJSONParser.parseToDTO(json);
	}

	public static AssetLibraryTestEntity[] toDTOs(String json) {
		AssetLibraryTestEntityJSONParser assetLibraryTestEntityJSONParser =
			new AssetLibraryTestEntityJSONParser();

		return assetLibraryTestEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetLibraryTestEntity assetLibraryTestEntity) {
		if (assetLibraryTestEntity == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (assetLibraryTestEntity.getAssetLibraryId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryId\": ");

			sb.append(assetLibraryTestEntity.getAssetLibraryId());
		}

		if (assetLibraryTestEntity.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					assetLibraryTestEntity.getDateCreated()));

			sb.append("\"");
		}

		if (assetLibraryTestEntity.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					assetLibraryTestEntity.getDateModified()));

			sb.append("\"");
		}

		if (assetLibraryTestEntity.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(assetLibraryTestEntity.getDescription()));

			sb.append("\"");
		}

		if (assetLibraryTestEntity.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(assetLibraryTestEntity.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (assetLibraryTestEntity.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < assetLibraryTestEntity.getPermissions().length;
				 i++) {

				sb.append(assetLibraryTestEntity.getPermissions()[i]);

				if ((i + 1) < assetLibraryTestEntity.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetLibraryTestEntityJSONParser assetLibraryTestEntityJSONParser =
			new AssetLibraryTestEntityJSONParser();

		return assetLibraryTestEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AssetLibraryTestEntity assetLibraryTestEntity) {

		if (assetLibraryTestEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (assetLibraryTestEntity.getAssetLibraryId() == null) {
			map.put("assetLibraryId", null);
		}
		else {
			map.put(
				"assetLibraryId",
				String.valueOf(assetLibraryTestEntity.getAssetLibraryId()));
		}

		if (assetLibraryTestEntity.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					assetLibraryTestEntity.getDateCreated()));
		}

		if (assetLibraryTestEntity.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					assetLibraryTestEntity.getDateModified()));
		}

		if (assetLibraryTestEntity.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(assetLibraryTestEntity.getDescription()));
		}

		if (assetLibraryTestEntity.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					assetLibraryTestEntity.getExternalReferenceCode()));
		}

		if (assetLibraryTestEntity.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(assetLibraryTestEntity.getPermissions()));
		}

		return map;
	}

	public static class AssetLibraryTestEntityJSONParser
		extends BaseJSONParser<AssetLibraryTestEntity> {

		@Override
		protected AssetLibraryTestEntity createDTO() {
			return new AssetLibraryTestEntity();
		}

		@Override
		protected AssetLibraryTestEntity[] createDTOArray(int size) {
			return new AssetLibraryTestEntity[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetLibraryId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetLibraryTestEntity assetLibraryTestEntity,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetLibraryId")) {
				if (jsonParserFieldValue != null) {
					assetLibraryTestEntity.setAssetLibraryId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					assetLibraryTestEntity.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					assetLibraryTestEntity.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					assetLibraryTestEntity.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					assetLibraryTestEntity.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.portal.tools.rest.builder.test.client.
						permission.Permission[] permissionsArray = new
						com.liferay.portal.tools.rest.builder.test.client.
							permission.Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.portal.tools.rest.builder.test.client.
								permission.Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					assetLibraryTestEntity.setPermissions(permissionsArray);
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