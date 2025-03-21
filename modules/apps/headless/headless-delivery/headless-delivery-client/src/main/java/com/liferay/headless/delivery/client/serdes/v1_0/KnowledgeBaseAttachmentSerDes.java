/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.KnowledgeBaseAttachment;
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
public class KnowledgeBaseAttachmentSerDes {

	public static KnowledgeBaseAttachment toDTO(String json) {
		KnowledgeBaseAttachmentJSONParser knowledgeBaseAttachmentJSONParser =
			new KnowledgeBaseAttachmentJSONParser();

		return knowledgeBaseAttachmentJSONParser.parseToDTO(json);
	}

	public static KnowledgeBaseAttachment[] toDTOs(String json) {
		KnowledgeBaseAttachmentJSONParser knowledgeBaseAttachmentJSONParser =
			new KnowledgeBaseAttachmentJSONParser();

		return knowledgeBaseAttachmentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		KnowledgeBaseAttachment knowledgeBaseAttachment) {

		if (knowledgeBaseAttachment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (knowledgeBaseAttachment.getContentUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentUrl\": ");

			sb.append("\"");

			sb.append(_escape(knowledgeBaseAttachment.getContentUrl()));

			sb.append("\"");
		}

		if (knowledgeBaseAttachment.getContentValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentValue\": ");

			sb.append("\"");

			sb.append(_escape(knowledgeBaseAttachment.getContentValue()));

			sb.append("\"");
		}

		if (knowledgeBaseAttachment.getEncodingFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"encodingFormat\": ");

			sb.append("\"");

			sb.append(_escape(knowledgeBaseAttachment.getEncodingFormat()));

			sb.append("\"");
		}

		if (knowledgeBaseAttachment.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(knowledgeBaseAttachment.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (knowledgeBaseAttachment.getFileExtension() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileExtension\": ");

			sb.append("\"");

			sb.append(_escape(knowledgeBaseAttachment.getFileExtension()));

			sb.append("\"");
		}

		if (knowledgeBaseAttachment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(knowledgeBaseAttachment.getId());
		}

		if (knowledgeBaseAttachment.getSizeInBytes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sizeInBytes\": ");

			sb.append(knowledgeBaseAttachment.getSizeInBytes());
		}

		if (knowledgeBaseAttachment.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(knowledgeBaseAttachment.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		KnowledgeBaseAttachmentJSONParser knowledgeBaseAttachmentJSONParser =
			new KnowledgeBaseAttachmentJSONParser();

		return knowledgeBaseAttachmentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		KnowledgeBaseAttachment knowledgeBaseAttachment) {

		if (knowledgeBaseAttachment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (knowledgeBaseAttachment.getContentUrl() == null) {
			map.put("contentUrl", null);
		}
		else {
			map.put(
				"contentUrl",
				String.valueOf(knowledgeBaseAttachment.getContentUrl()));
		}

		if (knowledgeBaseAttachment.getContentValue() == null) {
			map.put("contentValue", null);
		}
		else {
			map.put(
				"contentValue",
				String.valueOf(knowledgeBaseAttachment.getContentValue()));
		}

		if (knowledgeBaseAttachment.getEncodingFormat() == null) {
			map.put("encodingFormat", null);
		}
		else {
			map.put(
				"encodingFormat",
				String.valueOf(knowledgeBaseAttachment.getEncodingFormat()));
		}

		if (knowledgeBaseAttachment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					knowledgeBaseAttachment.getExternalReferenceCode()));
		}

		if (knowledgeBaseAttachment.getFileExtension() == null) {
			map.put("fileExtension", null);
		}
		else {
			map.put(
				"fileExtension",
				String.valueOf(knowledgeBaseAttachment.getFileExtension()));
		}

		if (knowledgeBaseAttachment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(knowledgeBaseAttachment.getId()));
		}

		if (knowledgeBaseAttachment.getSizeInBytes() == null) {
			map.put("sizeInBytes", null);
		}
		else {
			map.put(
				"sizeInBytes",
				String.valueOf(knowledgeBaseAttachment.getSizeInBytes()));
		}

		if (knowledgeBaseAttachment.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put(
				"title", String.valueOf(knowledgeBaseAttachment.getTitle()));
		}

		return map;
	}

	public static class KnowledgeBaseAttachmentJSONParser
		extends BaseJSONParser<KnowledgeBaseAttachment> {

		@Override
		protected KnowledgeBaseAttachment createDTO() {
			return new KnowledgeBaseAttachment();
		}

		@Override
		protected KnowledgeBaseAttachment[] createDTOArray(int size) {
			return new KnowledgeBaseAttachment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "contentUrl")) {
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
			KnowledgeBaseAttachment knowledgeBaseAttachment,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "contentUrl")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseAttachment.setContentUrl(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentValue")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseAttachment.setContentValue(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "encodingFormat")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseAttachment.setEncodingFormat(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					knowledgeBaseAttachment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileExtension")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseAttachment.setFileExtension(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseAttachment.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sizeInBytes")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseAttachment.setSizeInBytes(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					knowledgeBaseAttachment.setTitle(
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