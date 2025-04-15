/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.serdes.v1_0;

import com.liferay.headless.admin.content.client.dto.v1_0.ContentFieldValue;
import com.liferay.headless.admin.content.client.json.BaseJSONParser;

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
public class ContentFieldValueSerDes {

	public static ContentFieldValue toDTO(String json) {
		ContentFieldValueJSONParser contentFieldValueJSONParser =
			new ContentFieldValueJSONParser();

		return contentFieldValueJSONParser.parseToDTO(json);
	}

	public static ContentFieldValue[] toDTOs(String json) {
		ContentFieldValueJSONParser contentFieldValueJSONParser =
			new ContentFieldValueJSONParser();

		return contentFieldValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ContentFieldValue contentFieldValue) {
		if (contentFieldValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contentFieldValue.getData() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"data\": ");

			sb.append("\"");

			sb.append(_escape(contentFieldValue.getData()));

			sb.append("\"");
		}

		if (contentFieldValue.getDocument() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"document\": ");

			sb.append(contentFieldValue.getDocument());
		}

		if (contentFieldValue.getGeo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"geo\": ");

			sb.append(contentFieldValue.getGeo());
		}

		if (contentFieldValue.getImage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"image\": ");

			sb.append(contentFieldValue.getImage());
		}

		if (contentFieldValue.getLink() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"link\": ");

			sb.append("\"");

			sb.append(_escape(contentFieldValue.getLink()));

			sb.append("\"");
		}

		if (contentFieldValue.getStructuredContentLink() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"structuredContentLink\": ");

			sb.append(contentFieldValue.getStructuredContentLink());
		}

		if (contentFieldValue.getValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append("\"");

			sb.append(_escape(contentFieldValue.getValue()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContentFieldValueJSONParser contentFieldValueJSONParser =
			new ContentFieldValueJSONParser();

		return contentFieldValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ContentFieldValue contentFieldValue) {

		if (contentFieldValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contentFieldValue.getData() == null) {
			map.put("data", null);
		}
		else {
			map.put("data", String.valueOf(contentFieldValue.getData()));
		}

		if (contentFieldValue.getDocument() == null) {
			map.put("document", null);
		}
		else {
			map.put(
				"document", String.valueOf(contentFieldValue.getDocument()));
		}

		if (contentFieldValue.getGeo() == null) {
			map.put("geo", null);
		}
		else {
			map.put("geo", String.valueOf(contentFieldValue.getGeo()));
		}

		if (contentFieldValue.getImage() == null) {
			map.put("image", null);
		}
		else {
			map.put("image", String.valueOf(contentFieldValue.getImage()));
		}

		if (contentFieldValue.getLink() == null) {
			map.put("link", null);
		}
		else {
			map.put("link", String.valueOf(contentFieldValue.getLink()));
		}

		if (contentFieldValue.getStructuredContentLink() == null) {
			map.put("structuredContentLink", null);
		}
		else {
			map.put(
				"structuredContentLink",
				String.valueOf(contentFieldValue.getStructuredContentLink()));
		}

		if (contentFieldValue.getValue() == null) {
			map.put("value", null);
		}
		else {
			map.put("value", String.valueOf(contentFieldValue.getValue()));
		}

		return map;
	}

	public static class ContentFieldValueJSONParser
		extends BaseJSONParser<ContentFieldValue> {

		@Override
		protected ContentFieldValue createDTO() {
			return new ContentFieldValue();
		}

		@Override
		protected ContentFieldValue[] createDTOArray(int size) {
			return new ContentFieldValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "data")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "document")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "geo")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "image")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "link")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "structuredContentLink")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContentFieldValue contentFieldValue, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "data")) {
				if (jsonParserFieldValue != null) {
					contentFieldValue.setData((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "document")) {
				if (jsonParserFieldValue != null) {
					contentFieldValue.setDocument(
						ContentDocumentSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "geo")) {
				if (jsonParserFieldValue != null) {
					contentFieldValue.setGeo(
						GeoSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "image")) {
				if (jsonParserFieldValue != null) {
					contentFieldValue.setImage(
						ContentDocumentSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "link")) {
				if (jsonParserFieldValue != null) {
					contentFieldValue.setLink((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "structuredContentLink")) {

				if (jsonParserFieldValue != null) {
					contentFieldValue.setStructuredContentLink(
						StructuredContentLinkSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					contentFieldValue.setValue((String)jsonParserFieldValue);
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