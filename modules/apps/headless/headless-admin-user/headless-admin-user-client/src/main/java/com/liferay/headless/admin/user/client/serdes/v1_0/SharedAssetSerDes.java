/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.SharedAsset;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

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
public class SharedAssetSerDes {

	public static SharedAsset toDTO(String json) {
		SharedAssetJSONParser sharedAssetJSONParser =
			new SharedAssetJSONParser();

		return sharedAssetJSONParser.parseToDTO(json);
	}

	public static SharedAsset[] toDTOs(String json) {
		SharedAssetJSONParser sharedAssetJSONParser =
			new SharedAssetJSONParser();

		return sharedAssetJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SharedAsset sharedAsset) {
		if (sharedAsset == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (sharedAsset.getActionIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionIds\": ");

			sb.append("[");

			for (int i = 0; i < sharedAsset.getActionIds().length; i++) {
				sb.append(_toJSON(sharedAsset.getActionIds()[i]));

				if ((i + 1) < sharedAsset.getActionIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sharedAsset.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(sharedAsset.getActions()));
		}

		if (sharedAsset.getAssetType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType\": ");

			sb.append("\"");

			sb.append(_escape(sharedAsset.getAssetType()));

			sb.append("\"");
		}

		if (sharedAsset.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(sharedAsset.getClassName()));

			sb.append("\"");
		}

		if (sharedAsset.getClassPK() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classPK\": ");

			sb.append(sharedAsset.getClassPK());
		}

		if (sharedAsset.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(sharedAsset.getCreator()));
		}

		if (sharedAsset.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(sharedAsset.getDateCreated()));

			sb.append("\"");
		}

		if (sharedAsset.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(sharedAsset.getDateModified()));

			sb.append("\"");
		}

		if (sharedAsset.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(sharedAsset.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (sharedAsset.getFile() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"file\": ");

			sb.append(String.valueOf(sharedAsset.getFile()));
		}

		if (sharedAsset.getFileTypeIcon() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileTypeIcon\": ");

			sb.append("\"");

			sb.append(_escape(sharedAsset.getFileTypeIcon()));

			sb.append("\"");
		}

		if (sharedAsset.getFileTypeIconColor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileTypeIconColor\": ");

			sb.append("\"");

			sb.append(_escape(sharedAsset.getFileTypeIconColor()));

			sb.append("\"");
		}

		if (sharedAsset.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(sharedAsset.getId());
		}

		if (sharedAsset.getShareable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shareable\": ");

			sb.append(sharedAsset.getShareable());
		}

		if (sharedAsset.getSiteName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteName\": ");

			sb.append("\"");

			sb.append(_escape(sharedAsset.getSiteName()));

			sb.append("\"");
		}

		if (sharedAsset.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(sharedAsset.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SharedAssetJSONParser sharedAssetJSONParser =
			new SharedAssetJSONParser();

		return sharedAssetJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SharedAsset sharedAsset) {
		if (sharedAsset == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (sharedAsset.getActionIds() == null) {
			map.put("actionIds", null);
		}
		else {
			map.put("actionIds", String.valueOf(sharedAsset.getActionIds()));
		}

		if (sharedAsset.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(sharedAsset.getActions()));
		}

		if (sharedAsset.getAssetType() == null) {
			map.put("assetType", null);
		}
		else {
			map.put("assetType", String.valueOf(sharedAsset.getAssetType()));
		}

		if (sharedAsset.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put("className", String.valueOf(sharedAsset.getClassName()));
		}

		if (sharedAsset.getClassPK() == null) {
			map.put("classPK", null);
		}
		else {
			map.put("classPK", String.valueOf(sharedAsset.getClassPK()));
		}

		if (sharedAsset.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(sharedAsset.getCreator()));
		}

		if (sharedAsset.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(sharedAsset.getDateCreated()));
		}

		if (sharedAsset.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(sharedAsset.getDateModified()));
		}

		if (sharedAsset.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(sharedAsset.getExternalReferenceCode()));
		}

		if (sharedAsset.getFile() == null) {
			map.put("file", null);
		}
		else {
			map.put("file", String.valueOf(sharedAsset.getFile()));
		}

		if (sharedAsset.getFileTypeIcon() == null) {
			map.put("fileTypeIcon", null);
		}
		else {
			map.put(
				"fileTypeIcon", String.valueOf(sharedAsset.getFileTypeIcon()));
		}

		if (sharedAsset.getFileTypeIconColor() == null) {
			map.put("fileTypeIconColor", null);
		}
		else {
			map.put(
				"fileTypeIconColor",
				String.valueOf(sharedAsset.getFileTypeIconColor()));
		}

		if (sharedAsset.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(sharedAsset.getId()));
		}

		if (sharedAsset.getShareable() == null) {
			map.put("shareable", null);
		}
		else {
			map.put("shareable", String.valueOf(sharedAsset.getShareable()));
		}

		if (sharedAsset.getSiteName() == null) {
			map.put("siteName", null);
		}
		else {
			map.put("siteName", String.valueOf(sharedAsset.getSiteName()));
		}

		if (sharedAsset.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(sharedAsset.getTitle()));
		}

		return map;
	}

	public static class SharedAssetJSONParser
		extends BaseJSONParser<SharedAsset> {

		@Override
		protected SharedAsset createDTO() {
			return new SharedAsset();
		}

		@Override
		protected SharedAsset[] createDTOArray(int size) {
			return new SharedAsset[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "file")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fileTypeIcon")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fileTypeIconColor")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "shareable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SharedAsset sharedAsset, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setActionIds(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setAssetType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setClassName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "classPK")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setClassPK(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					sharedAsset.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "file")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setFile(
						FileEntrySerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileTypeIcon")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setFileTypeIcon((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileTypeIconColor")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setFileTypeIconColor(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "shareable")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setShareable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteName")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setSiteName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					sharedAsset.setTitle((String)jsonParserFieldValue);
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