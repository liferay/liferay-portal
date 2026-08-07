/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.AssetEntry;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class AssetEntrySerDes {

	public static AssetEntry toDTO(String json) {
		AssetEntryJSONParser assetEntryJSONParser = new AssetEntryJSONParser();

		return assetEntryJSONParser.parseToDTO(json);
	}

	public static AssetEntry[] toDTOs(String json) {
		AssetEntryJSONParser assetEntryJSONParser = new AssetEntryJSONParser();

		return assetEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetEntry assetEntry) {
		if (assetEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (assetEntry.getAssetEntryId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetEntryId\": ");

			sb.append(assetEntry.getAssetEntryId());
		}

		if (assetEntry.getAssetType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType\": ");

			sb.append("\"");

			sb.append(_escape(assetEntry.getAssetType()));

			sb.append("\"");
		}

		if (assetEntry.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(assetEntry.getClassName()));

			sb.append("\"");
		}

		if (assetEntry.getClassNameId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classNameId\": ");

			sb.append(assetEntry.getClassNameId());
		}

		if (assetEntry.getClassPK() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classPK\": ");

			sb.append(assetEntry.getClassPK());
		}

		if (assetEntry.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(assetEntry.getCreator()));
		}

		if (assetEntry.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(assetEntry.getDateModified()));

			sb.append("\"");
		}

		if (assetEntry.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(assetEntry.getDescription()));

			sb.append("\"");
		}

		if (assetEntry.getGroupDescriptiveName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groupDescriptiveName\": ");

			sb.append("\"");

			sb.append(_escape(assetEntry.getGroupDescriptiveName()));

			sb.append("\"");
		}

		if (assetEntry.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < assetEntry.getPermissions().length; i++) {
				sb.append(assetEntry.getPermissions()[i]);

				if ((i + 1) < assetEntry.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (assetEntry.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(assetEntry.getStatus());
		}

		if (assetEntry.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(assetEntry.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetEntryJSONParser assetEntryJSONParser = new AssetEntryJSONParser();

		return assetEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AssetEntry assetEntry) {
		if (assetEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (assetEntry.getAssetEntryId() == null) {
			map.put("assetEntryId", null);
		}
		else {
			map.put(
				"assetEntryId", String.valueOf(assetEntry.getAssetEntryId()));
		}

		if (assetEntry.getAssetType() == null) {
			map.put("assetType", null);
		}
		else {
			map.put("assetType", String.valueOf(assetEntry.getAssetType()));
		}

		if (assetEntry.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put("className", String.valueOf(assetEntry.getClassName()));
		}

		if (assetEntry.getClassNameId() == null) {
			map.put("classNameId", null);
		}
		else {
			map.put("classNameId", String.valueOf(assetEntry.getClassNameId()));
		}

		if (assetEntry.getClassPK() == null) {
			map.put("classPK", null);
		}
		else {
			map.put("classPK", String.valueOf(assetEntry.getClassPK()));
		}

		if (assetEntry.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(assetEntry.getCreator()));
		}

		if (assetEntry.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(assetEntry.getDateModified()));
		}

		if (assetEntry.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(assetEntry.getDescription()));
		}

		if (assetEntry.getGroupDescriptiveName() == null) {
			map.put("groupDescriptiveName", null);
		}
		else {
			map.put(
				"groupDescriptiveName",
				String.valueOf(assetEntry.getGroupDescriptiveName()));
		}

		if (assetEntry.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put("permissions", String.valueOf(assetEntry.getPermissions()));
		}

		if (assetEntry.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(assetEntry.getStatus()));
		}

		if (assetEntry.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(assetEntry.getTitle()));
		}

		return map;
	}

	public static class AssetEntryJSONParser
		extends BaseJSONParser<AssetEntry> {

		@Override
		protected AssetEntry createDTO() {
			return new AssetEntry();
		}

		@Override
		protected AssetEntry[] createDTOArray(int size) {
			return new AssetEntry[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetEntryId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "classNameId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "groupDescriptiveName")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetEntry assetEntry, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetEntryId")) {
				if (jsonParserFieldValue != null) {
					assetEntry.setAssetEntryId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				if (jsonParserFieldValue != null) {
					assetEntry.setAssetType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					assetEntry.setClassName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "classNameId")) {
				if (jsonParserFieldValue != null) {
					assetEntry.setClassNameId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				if (jsonParserFieldValue != null) {
					assetEntry.setClassPK(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					assetEntry.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					assetEntry.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					assetEntry.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "groupDescriptiveName")) {

				if (jsonParserFieldValue != null) {
					assetEntry.setGroupDescriptiveName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.delivery.client.permission.Permission[]
						permissionsArray = new
						com.liferay.headless.delivery.client.permission.
							Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.delivery.client.permission.
								Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					assetEntry.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					assetEntry.setStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					assetEntry.setTitle((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-1196076573