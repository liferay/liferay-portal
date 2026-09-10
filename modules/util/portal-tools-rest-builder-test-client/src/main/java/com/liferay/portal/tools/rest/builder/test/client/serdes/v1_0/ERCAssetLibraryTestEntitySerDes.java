/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ERCAssetLibraryTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
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
public class ERCAssetLibraryTestEntitySerDes {

	public static ERCAssetLibraryTestEntity toDTO(String json) {
		ERCAssetLibraryTestEntityJSONParser
			ercAssetLibraryTestEntityJSONParser =
				new ERCAssetLibraryTestEntityJSONParser();

		return ercAssetLibraryTestEntityJSONParser.parseToDTO(json);
	}

	public static ERCAssetLibraryTestEntity[] toDTOs(String json) {
		ERCAssetLibraryTestEntityJSONParser
			ercAssetLibraryTestEntityJSONParser =
				new ERCAssetLibraryTestEntityJSONParser();

		return ercAssetLibraryTestEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity) {

		if (ercAssetLibraryTestEntity == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ercAssetLibraryTestEntity.getAssetLibraryExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					ercAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode()));

			sb.append("\"");
		}

		if (ercAssetLibraryTestEntity.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					ercAssetLibraryTestEntity.getDateCreated()));

			sb.append("\"");
		}

		if (ercAssetLibraryTestEntity.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					ercAssetLibraryTestEntity.getDateModified()));

			sb.append("\"");
		}

		if (ercAssetLibraryTestEntity.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(ercAssetLibraryTestEntity.getDescription()));

			sb.append("\"");
		}

		if (ercAssetLibraryTestEntity.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(ercAssetLibraryTestEntity.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (ercAssetLibraryTestEntity.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0;
				 i < ercAssetLibraryTestEntity.getPermissions().length; i++) {

				sb.append(ercAssetLibraryTestEntity.getPermissions()[i]);

				if ((i + 1) <
						ercAssetLibraryTestEntity.getPermissions().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ERCAssetLibraryTestEntityJSONParser
			ercAssetLibraryTestEntityJSONParser =
				new ERCAssetLibraryTestEntityJSONParser();

		return ercAssetLibraryTestEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ERCAssetLibraryTestEntity ercAssetLibraryTestEntity) {

		if (ercAssetLibraryTestEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ercAssetLibraryTestEntity.getAssetLibraryExternalReferenceCode() ==
				null) {

			map.put("assetLibraryExternalReferenceCode", null);
		}
		else {
			map.put(
				"assetLibraryExternalReferenceCode",
				String.valueOf(
					ercAssetLibraryTestEntity.
						getAssetLibraryExternalReferenceCode()));
		}

		if (ercAssetLibraryTestEntity.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					ercAssetLibraryTestEntity.getDateCreated()));
		}

		if (ercAssetLibraryTestEntity.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					ercAssetLibraryTestEntity.getDateModified()));
		}

		if (ercAssetLibraryTestEntity.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(ercAssetLibraryTestEntity.getDescription()));
		}

		if (ercAssetLibraryTestEntity.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					ercAssetLibraryTestEntity.getExternalReferenceCode()));
		}

		if (ercAssetLibraryTestEntity.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(ercAssetLibraryTestEntity.getPermissions()));
		}

		return map;
	}

	public static class ERCAssetLibraryTestEntityJSONParser
		extends BaseJSONParser<ERCAssetLibraryTestEntity> {

		@Override
		protected ERCAssetLibraryTestEntity createDTO() {
			return new ERCAssetLibraryTestEntity();
		}

		@Override
		protected ERCAssetLibraryTestEntity[] createDTOArray(int size) {
			return new ERCAssetLibraryTestEntity[size];
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

			return false;
		}

		@Override
		protected void setField(
			ERCAssetLibraryTestEntity ercAssetLibraryTestEntity,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "assetLibraryExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					ercAssetLibraryTestEntity.
						setAssetLibraryExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					ercAssetLibraryTestEntity.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					ercAssetLibraryTestEntity.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					ercAssetLibraryTestEntity.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					ercAssetLibraryTestEntity.setExternalReferenceCode(
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

					ercAssetLibraryTestEntity.setPermissions(permissionsArray);
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:922025888