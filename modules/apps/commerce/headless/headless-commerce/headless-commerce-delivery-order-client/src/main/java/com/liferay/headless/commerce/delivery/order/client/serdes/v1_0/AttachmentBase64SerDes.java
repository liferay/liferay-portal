/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.AttachmentBase64;
import com.liferay.headless.commerce.delivery.order.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class AttachmentBase64SerDes {

	public static AttachmentBase64 toDTO(String json) {
		AttachmentBase64JSONParser attachmentBase64JSONParser =
			new AttachmentBase64JSONParser();

		return attachmentBase64JSONParser.parseToDTO(json);
	}

	public static AttachmentBase64[] toDTOs(String json) {
		AttachmentBase64JSONParser attachmentBase64JSONParser =
			new AttachmentBase64JSONParser();

		return attachmentBase64JSONParser.parseToDTOs(json);
	}

	public static String toJSON(AttachmentBase64 attachmentBase64) {
		if (attachmentBase64 == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (attachmentBase64.getAttachment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachment\": ");

			sb.append("\"");

			sb.append(_escape(attachmentBase64.getAttachment()));

			sb.append("\"");
		}

		if (attachmentBase64.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(attachmentBase64.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (attachmentBase64.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(attachmentBase64.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AttachmentBase64JSONParser attachmentBase64JSONParser =
			new AttachmentBase64JSONParser();

		return attachmentBase64JSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AttachmentBase64 attachmentBase64) {
		if (attachmentBase64 == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (attachmentBase64.getAttachment() == null) {
			map.put("attachment", null);
		}
		else {
			map.put(
				"attachment", String.valueOf(attachmentBase64.getAttachment()));
		}

		if (attachmentBase64.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(attachmentBase64.getExternalReferenceCode()));
		}

		if (attachmentBase64.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(attachmentBase64.getTitle()));
		}

		return map;
	}

	public static class AttachmentBase64JSONParser
		extends BaseJSONParser<AttachmentBase64> {

		@Override
		protected AttachmentBase64 createDTO() {
			return new AttachmentBase64();
		}

		@Override
		protected AttachmentBase64[] createDTOArray(int size) {
			return new AttachmentBase64[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "attachment")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AttachmentBase64 attachmentBase64, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "attachment")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setAttachment(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					attachmentBase64.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setTitle((String)jsonParserFieldValue);
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