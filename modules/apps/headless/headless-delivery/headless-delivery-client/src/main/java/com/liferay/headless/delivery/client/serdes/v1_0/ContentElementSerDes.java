/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.ContentElement;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ContentElementSerDes {

	public static ContentElement toDTO(String json) {
		ContentElementJSONParser contentElementJSONParser =
			new ContentElementJSONParser();

		return contentElementJSONParser.parseToDTO(json);
	}

	public static ContentElement[] toDTOs(String json) {
		ContentElementJSONParser contentElementJSONParser =
			new ContentElementJSONParser();

		return contentElementJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ContentElement contentElement) {
		if (contentElement == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contentElement.getContent() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"content\": ");

			if (contentElement.getContent() instanceof String) {
				sb.append("\"");
				sb.append((String)contentElement.getContent());
				sb.append("\"");
			}
			else {
				sb.append(contentElement.getContent());
			}
		}

		if (contentElement.getContentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(contentElement.getContentType()));

			sb.append("\"");
		}

		if (contentElement.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(contentElement.getId());
		}

		if (contentElement.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(contentElement.getTitle()));

			sb.append("\"");
		}

		if (contentElement.getTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title_i18n\": ");

			sb.append(_toJSON(contentElement.getTitle_i18n()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContentElementJSONParser contentElementJSONParser =
			new ContentElementJSONParser();

		return contentElementJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ContentElement contentElement) {
		if (contentElement == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contentElement.getContent() == null) {
			map.put("content", null);
		}
		else {
			map.put("content", String.valueOf(contentElement.getContent()));
		}

		if (contentElement.getContentType() == null) {
			map.put("contentType", null);
		}
		else {
			map.put(
				"contentType", String.valueOf(contentElement.getContentType()));
		}

		if (contentElement.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(contentElement.getId()));
		}

		if (contentElement.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(contentElement.getTitle()));
		}

		if (contentElement.getTitle_i18n() == null) {
			map.put("title_i18n", null);
		}
		else {
			map.put(
				"title_i18n", String.valueOf(contentElement.getTitle_i18n()));
		}

		return map;
	}

	public static class ContentElementJSONParser
		extends BaseJSONParser<ContentElement> {

		@Override
		protected ContentElement createDTO() {
			return new ContentElement();
		}

		@Override
		protected ContentElement[] createDTOArray(int size) {
			return new ContentElement[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "content")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			ContentElement contentElement, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "content")) {
				if (jsonParserFieldValue != null) {
					contentElement.setContent((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				if (jsonParserFieldValue != null) {
					contentElement.setContentType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					contentElement.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					contentElement.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				if (jsonParserFieldValue != null) {
					contentElement.setTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
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