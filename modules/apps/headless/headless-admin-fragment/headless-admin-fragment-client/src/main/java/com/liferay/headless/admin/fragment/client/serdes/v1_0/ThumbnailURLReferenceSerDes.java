/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.ThumbnailURLReference;
import com.liferay.headless.admin.fragment.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class ThumbnailURLReferenceSerDes {

	public static ThumbnailURLReference toDTO(String json) {
		ThumbnailURLReferenceJSONParser thumbnailURLReferenceJSONParser =
			new ThumbnailURLReferenceJSONParser();

		return thumbnailURLReferenceJSONParser.parseToDTO(json);
	}

	public static ThumbnailURLReference[] toDTOs(String json) {
		ThumbnailURLReferenceJSONParser thumbnailURLReferenceJSONParser =
			new ThumbnailURLReferenceJSONParser();

		return thumbnailURLReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ThumbnailURLReference thumbnailURLReference) {
		if (thumbnailURLReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (thumbnailURLReference.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(thumbnailURLReference.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (thumbnailURLReference.getFileBase64() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileBase64\": ");

			sb.append("\"");

			sb.append(_escape(thumbnailURLReference.getFileBase64()));

			sb.append("\"");
		}

		if (thumbnailURLReference.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(thumbnailURLReference.getUrl()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ThumbnailURLReferenceJSONParser thumbnailURLReferenceJSONParser =
			new ThumbnailURLReferenceJSONParser();

		return thumbnailURLReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ThumbnailURLReference thumbnailURLReference) {

		if (thumbnailURLReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (thumbnailURLReference.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					thumbnailURLReference.getExternalReferenceCode()));
		}

		if (thumbnailURLReference.getFileBase64() == null) {
			map.put("fileBase64", null);
		}
		else {
			map.put(
				"fileBase64",
				String.valueOf(thumbnailURLReference.getFileBase64()));
		}

		if (thumbnailURLReference.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put("url", String.valueOf(thumbnailURLReference.getUrl()));
		}

		return map;
	}

	public static class ThumbnailURLReferenceJSONParser
		extends BaseJSONParser<ThumbnailURLReference> {

		@Override
		protected ThumbnailURLReference createDTO() {
			return new ThumbnailURLReference();
		}

		@Override
		protected ThumbnailURLReference[] createDTOArray(int size) {
			return new ThumbnailURLReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fileBase64")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ThumbnailURLReference thumbnailURLReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					thumbnailURLReference.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileBase64")) {
				if (jsonParserFieldValue != null) {
					thumbnailURLReference.setFileBase64(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					thumbnailURLReference.setUrl((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:352378778