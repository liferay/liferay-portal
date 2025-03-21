/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.WikiPageAttachment;
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
public class WikiPageAttachmentSerDes {

	public static WikiPageAttachment toDTO(String json) {
		WikiPageAttachmentJSONParser wikiPageAttachmentJSONParser =
			new WikiPageAttachmentJSONParser();

		return wikiPageAttachmentJSONParser.parseToDTO(json);
	}

	public static WikiPageAttachment[] toDTOs(String json) {
		WikiPageAttachmentJSONParser wikiPageAttachmentJSONParser =
			new WikiPageAttachmentJSONParser();

		return wikiPageAttachmentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WikiPageAttachment wikiPageAttachment) {
		if (wikiPageAttachment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (wikiPageAttachment.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(wikiPageAttachment.getActions()));
		}

		if (wikiPageAttachment.getContentUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentUrl\": ");

			sb.append("\"");

			sb.append(_escape(wikiPageAttachment.getContentUrl()));

			sb.append("\"");
		}

		if (wikiPageAttachment.getContentValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentValue\": ");

			sb.append("\"");

			sb.append(_escape(wikiPageAttachment.getContentValue()));

			sb.append("\"");
		}

		if (wikiPageAttachment.getEncodingFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"encodingFormat\": ");

			sb.append("\"");

			sb.append(_escape(wikiPageAttachment.getEncodingFormat()));

			sb.append("\"");
		}

		if (wikiPageAttachment.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(wikiPageAttachment.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (wikiPageAttachment.getFileExtension() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileExtension\": ");

			sb.append("\"");

			sb.append(_escape(wikiPageAttachment.getFileExtension()));

			sb.append("\"");
		}

		if (wikiPageAttachment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(wikiPageAttachment.getId());
		}

		if (wikiPageAttachment.getSizeInBytes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sizeInBytes\": ");

			sb.append(wikiPageAttachment.getSizeInBytes());
		}

		if (wikiPageAttachment.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(wikiPageAttachment.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WikiPageAttachmentJSONParser wikiPageAttachmentJSONParser =
			new WikiPageAttachmentJSONParser();

		return wikiPageAttachmentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WikiPageAttachment wikiPageAttachment) {

		if (wikiPageAttachment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (wikiPageAttachment.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(wikiPageAttachment.getActions()));
		}

		if (wikiPageAttachment.getContentUrl() == null) {
			map.put("contentUrl", null);
		}
		else {
			map.put(
				"contentUrl",
				String.valueOf(wikiPageAttachment.getContentUrl()));
		}

		if (wikiPageAttachment.getContentValue() == null) {
			map.put("contentValue", null);
		}
		else {
			map.put(
				"contentValue",
				String.valueOf(wikiPageAttachment.getContentValue()));
		}

		if (wikiPageAttachment.getEncodingFormat() == null) {
			map.put("encodingFormat", null);
		}
		else {
			map.put(
				"encodingFormat",
				String.valueOf(wikiPageAttachment.getEncodingFormat()));
		}

		if (wikiPageAttachment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(wikiPageAttachment.getExternalReferenceCode()));
		}

		if (wikiPageAttachment.getFileExtension() == null) {
			map.put("fileExtension", null);
		}
		else {
			map.put(
				"fileExtension",
				String.valueOf(wikiPageAttachment.getFileExtension()));
		}

		if (wikiPageAttachment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(wikiPageAttachment.getId()));
		}

		if (wikiPageAttachment.getSizeInBytes() == null) {
			map.put("sizeInBytes", null);
		}
		else {
			map.put(
				"sizeInBytes",
				String.valueOf(wikiPageAttachment.getSizeInBytes()));
		}

		if (wikiPageAttachment.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(wikiPageAttachment.getTitle()));
		}

		return map;
	}

	public static class WikiPageAttachmentJSONParser
		extends BaseJSONParser<WikiPageAttachment> {

		@Override
		protected WikiPageAttachment createDTO() {
			return new WikiPageAttachment();
		}

		@Override
		protected WikiPageAttachment[] createDTOArray(int size) {
			return new WikiPageAttachment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "contentUrl")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "encodingFormat")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fileExtension")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sizeInBytes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WikiPageAttachment wikiPageAttachment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					wikiPageAttachment.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentUrl")) {
				if (jsonParserFieldValue != null) {
					wikiPageAttachment.setContentUrl(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentValue")) {
				if (jsonParserFieldValue != null) {
					wikiPageAttachment.setContentValue(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "encodingFormat")) {
				if (jsonParserFieldValue != null) {
					wikiPageAttachment.setEncodingFormat(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					wikiPageAttachment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileExtension")) {
				if (jsonParserFieldValue != null) {
					wikiPageAttachment.setFileExtension(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					wikiPageAttachment.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sizeInBytes")) {
				if (jsonParserFieldValue != null) {
					wikiPageAttachment.setSizeInBytes(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					wikiPageAttachment.setTitle((String)jsonParserFieldValue);
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