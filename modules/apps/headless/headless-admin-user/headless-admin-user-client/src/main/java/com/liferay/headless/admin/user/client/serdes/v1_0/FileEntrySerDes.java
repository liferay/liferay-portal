/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.FileEntry;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class FileEntrySerDes {

	public static FileEntry toDTO(String json) {
		FileEntryJSONParser fileEntryJSONParser = new FileEntryJSONParser();

		return fileEntryJSONParser.parseToDTO(json);
	}

	public static FileEntry[] toDTOs(String json) {
		FileEntryJSONParser fileEntryJSONParser = new FileEntryJSONParser();

		return fileEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FileEntry fileEntry) {
		if (fileEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fileEntry.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(fileEntry.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (fileEntry.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(fileEntry.getId());
		}

		if (fileEntry.getLink() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"link\": ");

			sb.append(String.valueOf(fileEntry.getLink()));
		}

		if (fileEntry.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(fileEntry.getName()));

			sb.append("\"");
		}

		if (fileEntry.getThumbnailURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnailURL\": ");

			sb.append("\"");

			sb.append(_escape(fileEntry.getThumbnailURL()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FileEntryJSONParser fileEntryJSONParser = new FileEntryJSONParser();

		return fileEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FileEntry fileEntry) {
		if (fileEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fileEntry.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(fileEntry.getExternalReferenceCode()));
		}

		if (fileEntry.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(fileEntry.getId()));
		}

		if (fileEntry.getLink() == null) {
			map.put("link", null);
		}
		else {
			map.put("link", String.valueOf(fileEntry.getLink()));
		}

		if (fileEntry.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(fileEntry.getName()));
		}

		if (fileEntry.getThumbnailURL() == null) {
			map.put("thumbnailURL", null);
		}
		else {
			map.put(
				"thumbnailURL", String.valueOf(fileEntry.getThumbnailURL()));
		}

		return map;
	}

	public static class FileEntryJSONParser extends BaseJSONParser<FileEntry> {

		@Override
		protected FileEntry createDTO() {
			return new FileEntry();
		}

		@Override
		protected FileEntry[] createDTOArray(int size) {
			return new FileEntry[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "link")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnailURL")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FileEntry fileEntry, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					fileEntry.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					fileEntry.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "link")) {
				if (jsonParserFieldValue != null) {
					fileEntry.setLink(
						LinkSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					fileEntry.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnailURL")) {
				if (jsonParserFieldValue != null) {
					fileEntry.setThumbnailURL((String)jsonParserFieldValue);
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