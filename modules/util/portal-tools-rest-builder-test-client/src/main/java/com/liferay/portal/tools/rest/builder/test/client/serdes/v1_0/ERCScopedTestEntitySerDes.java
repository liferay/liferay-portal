/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ERCScopedTestEntity;
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
public class ERCScopedTestEntitySerDes {

	public static ERCScopedTestEntity toDTO(String json) {
		ERCScopedTestEntityJSONParser ercScopedTestEntityJSONParser =
			new ERCScopedTestEntityJSONParser();

		return ercScopedTestEntityJSONParser.parseToDTO(json);
	}

	public static ERCScopedTestEntity[] toDTOs(String json) {
		ERCScopedTestEntityJSONParser ercScopedTestEntityJSONParser =
			new ERCScopedTestEntityJSONParser();

		return ercScopedTestEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ERCScopedTestEntity ercScopedTestEntity) {
		if (ercScopedTestEntity == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ercScopedTestEntity.getAssetLibraryExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					ercScopedTestEntity.
						getAssetLibraryExternalReferenceCode()));

			sb.append("\"");
		}

		if (ercScopedTestEntity.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					ercScopedTestEntity.getDateCreated()));

			sb.append("\"");
		}

		if (ercScopedTestEntity.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					ercScopedTestEntity.getDateModified()));

			sb.append("\"");
		}

		if (ercScopedTestEntity.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(ercScopedTestEntity.getDescription()));

			sb.append("\"");
		}

		if (ercScopedTestEntity.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(ercScopedTestEntity.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (ercScopedTestEntity.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < ercScopedTestEntity.getPermissions().length;
				 i++) {

				sb.append(ercScopedTestEntity.getPermissions()[i]);

				if ((i + 1) < ercScopedTestEntity.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (ercScopedTestEntity.getSiteExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(ercScopedTestEntity.getSiteExternalReferenceCode()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ERCScopedTestEntityJSONParser ercScopedTestEntityJSONParser =
			new ERCScopedTestEntityJSONParser();

		return ercScopedTestEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ERCScopedTestEntity ercScopedTestEntity) {

		if (ercScopedTestEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ercScopedTestEntity.getAssetLibraryExternalReferenceCode() ==
				null) {

			map.put("assetLibraryExternalReferenceCode", null);
		}
		else {
			map.put(
				"assetLibraryExternalReferenceCode",
				String.valueOf(
					ercScopedTestEntity.
						getAssetLibraryExternalReferenceCode()));
		}

		if (ercScopedTestEntity.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					ercScopedTestEntity.getDateCreated()));
		}

		if (ercScopedTestEntity.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					ercScopedTestEntity.getDateModified()));
		}

		if (ercScopedTestEntity.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(ercScopedTestEntity.getDescription()));
		}

		if (ercScopedTestEntity.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(ercScopedTestEntity.getExternalReferenceCode()));
		}

		if (ercScopedTestEntity.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(ercScopedTestEntity.getPermissions()));
		}

		if (ercScopedTestEntity.getSiteExternalReferenceCode() == null) {
			map.put("siteExternalReferenceCode", null);
		}
		else {
			map.put(
				"siteExternalReferenceCode",
				String.valueOf(
					ercScopedTestEntity.getSiteExternalReferenceCode()));
		}

		return map;
	}

	public static class ERCScopedTestEntityJSONParser
		extends BaseJSONParser<ERCScopedTestEntity> {

		@Override
		protected ERCScopedTestEntity createDTO() {
			return new ERCScopedTestEntity();
		}

		@Override
		protected ERCScopedTestEntity[] createDTOArray(int size) {
			return new ERCScopedTestEntity[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "assetLibraryExternalReferenceCode")) {

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
			else if (Objects.equals(
						jsonParserFieldName, "siteExternalReferenceCode")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ERCScopedTestEntity ercScopedTestEntity, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "assetLibraryExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					ercScopedTestEntity.setAssetLibraryExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					ercScopedTestEntity.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					ercScopedTestEntity.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					ercScopedTestEntity.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					ercScopedTestEntity.setExternalReferenceCode(
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

					ercScopedTestEntity.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					ercScopedTestEntity.setSiteExternalReferenceCode(
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