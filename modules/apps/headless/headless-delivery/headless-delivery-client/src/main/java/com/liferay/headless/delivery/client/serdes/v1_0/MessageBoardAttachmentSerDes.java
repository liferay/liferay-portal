/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.MessageBoardAttachment;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

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
public class MessageBoardAttachmentSerDes {

	public static MessageBoardAttachment toDTO(String json) {
		MessageBoardAttachmentJSONParser messageBoardAttachmentJSONParser =
			new MessageBoardAttachmentJSONParser();

		return messageBoardAttachmentJSONParser.parseToDTO(json);
	}

	public static MessageBoardAttachment[] toDTOs(String json) {
		MessageBoardAttachmentJSONParser messageBoardAttachmentJSONParser =
			new MessageBoardAttachmentJSONParser();

		return messageBoardAttachmentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MessageBoardAttachment messageBoardAttachment) {
		if (messageBoardAttachment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (messageBoardAttachment.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(messageBoardAttachment.getActions()));
		}

		if (messageBoardAttachment.getContentUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentUrl\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardAttachment.getContentUrl()));

			sb.append("\"");
		}

		if (messageBoardAttachment.getContentValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentValue\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardAttachment.getContentValue()));

			sb.append("\"");
		}

		if (messageBoardAttachment.getEncodingFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"encodingFormat\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardAttachment.getEncodingFormat()));

			sb.append("\"");
		}

		if (messageBoardAttachment.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(messageBoardAttachment.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (messageBoardAttachment.getFileExtension() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileExtension\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardAttachment.getFileExtension()));

			sb.append("\"");
		}

		if (messageBoardAttachment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(messageBoardAttachment.getId());
		}

		if (messageBoardAttachment.getSizeInBytes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sizeInBytes\": ");

			sb.append(messageBoardAttachment.getSizeInBytes());
		}

		if (messageBoardAttachment.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(messageBoardAttachment.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MessageBoardAttachmentJSONParser messageBoardAttachmentJSONParser =
			new MessageBoardAttachmentJSONParser();

		return messageBoardAttachmentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		MessageBoardAttachment messageBoardAttachment) {

		if (messageBoardAttachment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (messageBoardAttachment.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions", String.valueOf(messageBoardAttachment.getActions()));
		}

		if (messageBoardAttachment.getContentUrl() == null) {
			map.put("contentUrl", null);
		}
		else {
			map.put(
				"contentUrl",
				String.valueOf(messageBoardAttachment.getContentUrl()));
		}

		if (messageBoardAttachment.getContentValue() == null) {
			map.put("contentValue", null);
		}
		else {
			map.put(
				"contentValue",
				String.valueOf(messageBoardAttachment.getContentValue()));
		}

		if (messageBoardAttachment.getEncodingFormat() == null) {
			map.put("encodingFormat", null);
		}
		else {
			map.put(
				"encodingFormat",
				String.valueOf(messageBoardAttachment.getEncodingFormat()));
		}

		if (messageBoardAttachment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					messageBoardAttachment.getExternalReferenceCode()));
		}

		if (messageBoardAttachment.getFileExtension() == null) {
			map.put("fileExtension", null);
		}
		else {
			map.put(
				"fileExtension",
				String.valueOf(messageBoardAttachment.getFileExtension()));
		}

		if (messageBoardAttachment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(messageBoardAttachment.getId()));
		}

		if (messageBoardAttachment.getSizeInBytes() == null) {
			map.put("sizeInBytes", null);
		}
		else {
			map.put(
				"sizeInBytes",
				String.valueOf(messageBoardAttachment.getSizeInBytes()));
		}

		if (messageBoardAttachment.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(messageBoardAttachment.getTitle()));
		}

		return map;
	}

	public static class MessageBoardAttachmentJSONParser
		extends BaseJSONParser<MessageBoardAttachment> {

		@Override
		protected MessageBoardAttachment createDTO() {
			return new MessageBoardAttachment();
		}

		@Override
		protected MessageBoardAttachment[] createDTOArray(int size) {
			return new MessageBoardAttachment[size];
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
			MessageBoardAttachment messageBoardAttachment,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					messageBoardAttachment.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentUrl")) {
				if (jsonParserFieldValue != null) {
					messageBoardAttachment.setContentUrl(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentValue")) {
				if (jsonParserFieldValue != null) {
					messageBoardAttachment.setContentValue(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "encodingFormat")) {
				if (jsonParserFieldValue != null) {
					messageBoardAttachment.setEncodingFormat(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					messageBoardAttachment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileExtension")) {
				if (jsonParserFieldValue != null) {
					messageBoardAttachment.setFileExtension(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					messageBoardAttachment.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sizeInBytes")) {
				if (jsonParserFieldValue != null) {
					messageBoardAttachment.setSizeInBytes(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					messageBoardAttachment.setTitle(
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