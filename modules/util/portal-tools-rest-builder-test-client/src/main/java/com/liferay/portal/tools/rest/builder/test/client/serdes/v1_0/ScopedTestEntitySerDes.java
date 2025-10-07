/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ScopedTestEntity;
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
public class ScopedTestEntitySerDes {

	public static ScopedTestEntity toDTO(String json) {
		ScopedTestEntityJSONParser scopedTestEntityJSONParser =
			new ScopedTestEntityJSONParser();

		return scopedTestEntityJSONParser.parseToDTO(json);
	}

	public static ScopedTestEntity[] toDTOs(String json) {
		ScopedTestEntityJSONParser scopedTestEntityJSONParser =
			new ScopedTestEntityJSONParser();

		return scopedTestEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ScopedTestEntity scopedTestEntity) {
		if (scopedTestEntity == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (scopedTestEntity.getAssetLibraryKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryKey\": ");

			sb.append("\"");

			sb.append(_escape(scopedTestEntity.getAssetLibraryKey()));

			sb.append("\"");
		}

		if (scopedTestEntity.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					scopedTestEntity.getDateCreated()));

			sb.append("\"");
		}

		if (scopedTestEntity.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					scopedTestEntity.getDateModified()));

			sb.append("\"");
		}

		if (scopedTestEntity.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(scopedTestEntity.getDescription()));

			sb.append("\"");
		}

		if (scopedTestEntity.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(scopedTestEntity.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (scopedTestEntity.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(scopedTestEntity.getId());
		}

		if (scopedTestEntity.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < scopedTestEntity.getPermissions().length; i++) {
				sb.append(scopedTestEntity.getPermissions()[i]);

				if ((i + 1) < scopedTestEntity.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (scopedTestEntity.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(scopedTestEntity.getSiteId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ScopedTestEntityJSONParser scopedTestEntityJSONParser =
			new ScopedTestEntityJSONParser();

		return scopedTestEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ScopedTestEntity scopedTestEntity) {
		if (scopedTestEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (scopedTestEntity.getAssetLibraryKey() == null) {
			map.put("assetLibraryKey", null);
		}
		else {
			map.put(
				"assetLibraryKey",
				String.valueOf(scopedTestEntity.getAssetLibraryKey()));
		}

		if (scopedTestEntity.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					scopedTestEntity.getDateCreated()));
		}

		if (scopedTestEntity.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					scopedTestEntity.getDateModified()));
		}

		if (scopedTestEntity.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(scopedTestEntity.getDescription()));
		}

		if (scopedTestEntity.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(scopedTestEntity.getExternalReferenceCode()));
		}

		if (scopedTestEntity.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(scopedTestEntity.getId()));
		}

		if (scopedTestEntity.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(scopedTestEntity.getPermissions()));
		}

		if (scopedTestEntity.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(scopedTestEntity.getSiteId()));
		}

		return map;
	}

	public static class ScopedTestEntityJSONParser
		extends BaseJSONParser<ScopedTestEntity> {

		@Override
		protected ScopedTestEntity createDTO() {
			return new ScopedTestEntity();
		}

		@Override
		protected ScopedTestEntity[] createDTOArray(int size) {
			return new ScopedTestEntity[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
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
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ScopedTestEntity scopedTestEntity, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				if (jsonParserFieldValue != null) {
					scopedTestEntity.setAssetLibraryKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					scopedTestEntity.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					scopedTestEntity.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					scopedTestEntity.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					scopedTestEntity.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					scopedTestEntity.setId(
						Long.valueOf((String)jsonParserFieldValue));
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

					scopedTestEntity.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					scopedTestEntity.setSiteId(
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