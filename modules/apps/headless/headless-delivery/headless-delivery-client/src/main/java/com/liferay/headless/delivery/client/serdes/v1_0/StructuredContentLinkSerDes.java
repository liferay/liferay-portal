/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.StructuredContentLink;
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
public class StructuredContentLinkSerDes {

	public static StructuredContentLink toDTO(String json) {
		StructuredContentLinkJSONParser structuredContentLinkJSONParser =
			new StructuredContentLinkJSONParser();

		return structuredContentLinkJSONParser.parseToDTO(json);
	}

	public static StructuredContentLink[] toDTOs(String json) {
		StructuredContentLinkJSONParser structuredContentLinkJSONParser =
			new StructuredContentLinkJSONParser();

		return structuredContentLinkJSONParser.parseToDTOs(json);
	}

	public static String toJSON(StructuredContentLink structuredContentLink) {
		if (structuredContentLink == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (structuredContentLink.getContentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(structuredContentLink.getContentType()));

			sb.append("\"");
		}

		if (structuredContentLink.getEmbeddedStructuredContent() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"embeddedStructuredContent\": ");

			sb.append(
				String.valueOf(
					structuredContentLink.getEmbeddedStructuredContent()));
		}

		if (structuredContentLink.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(structuredContentLink.getId());
		}

		if (structuredContentLink.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(structuredContentLink.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		StructuredContentLinkJSONParser structuredContentLinkJSONParser =
			new StructuredContentLinkJSONParser();

		return structuredContentLinkJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		StructuredContentLink structuredContentLink) {

		if (structuredContentLink == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (structuredContentLink.getContentType() == null) {
			map.put("contentType", null);
		}
		else {
			map.put(
				"contentType",
				String.valueOf(structuredContentLink.getContentType()));
		}

		if (structuredContentLink.getEmbeddedStructuredContent() == null) {
			map.put("embeddedStructuredContent", null);
		}
		else {
			map.put(
				"embeddedStructuredContent",
				String.valueOf(
					structuredContentLink.getEmbeddedStructuredContent()));
		}

		if (structuredContentLink.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(structuredContentLink.getId()));
		}

		if (structuredContentLink.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(structuredContentLink.getTitle()));
		}

		return map;
	}

	public static class StructuredContentLinkJSONParser
		extends BaseJSONParser<StructuredContentLink> {

		@Override
		protected StructuredContentLink createDTO() {
			return new StructuredContentLink();
		}

		@Override
		protected StructuredContentLink[] createDTOArray(int size) {
			return new StructuredContentLink[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "contentType")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "embeddedStructuredContent")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			StructuredContentLink structuredContentLink,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "contentType")) {
				if (jsonParserFieldValue != null) {
					structuredContentLink.setContentType(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "embeddedStructuredContent")) {

				if (jsonParserFieldValue != null) {
					structuredContentLink.setEmbeddedStructuredContent(
						StructuredContentSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					structuredContentLink.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					structuredContentLink.setTitle(
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