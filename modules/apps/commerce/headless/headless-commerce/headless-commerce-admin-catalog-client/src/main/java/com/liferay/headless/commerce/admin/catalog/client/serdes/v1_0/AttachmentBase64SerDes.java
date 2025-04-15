/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.AttachmentBase64;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
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

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (attachmentBase64.getAttachment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachment\": ");

			sb.append("\"");

			sb.append(_escape(attachmentBase64.getAttachment()));

			sb.append("\"");
		}

		if (attachmentBase64.getContentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(attachmentBase64.getContentType()));

			sb.append("\"");
		}

		if (attachmentBase64.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < attachmentBase64.getCustomFields().length;
				 i++) {

				sb.append(attachmentBase64.getCustomFields()[i]);

				if ((i + 1) < attachmentBase64.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (attachmentBase64.getDisplayDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					attachmentBase64.getDisplayDate()));

			sb.append("\"");
		}

		if (attachmentBase64.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					attachmentBase64.getExpirationDate()));

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

		if (attachmentBase64.getGalleryEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"galleryEnabled\": ");

			sb.append(attachmentBase64.getGalleryEnabled());
		}

		if (attachmentBase64.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(attachmentBase64.getId());
		}

		if (attachmentBase64.getNeverExpire() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(attachmentBase64.getNeverExpire());
		}

		if (attachmentBase64.getOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append(_toJSON(attachmentBase64.getOptions()));
		}

		if (attachmentBase64.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(attachmentBase64.getPriority());
		}

		if (attachmentBase64.getSrc() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"src\": ");

			sb.append("\"");

			sb.append(_escape(attachmentBase64.getSrc()));

			sb.append("\"");
		}

		if (attachmentBase64.getTags() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tags\": ");

			sb.append("[");

			for (int i = 0; i < attachmentBase64.getTags().length; i++) {
				sb.append(_toJSON(attachmentBase64.getTags()[i]));

				if ((i + 1) < attachmentBase64.getTags().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (attachmentBase64.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append(_toJSON(attachmentBase64.getTitle()));
		}

		if (attachmentBase64.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(attachmentBase64.getType());
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

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (attachmentBase64.getAttachment() == null) {
			map.put("attachment", null);
		}
		else {
			map.put(
				"attachment", String.valueOf(attachmentBase64.getAttachment()));
		}

		if (attachmentBase64.getContentType() == null) {
			map.put("contentType", null);
		}
		else {
			map.put(
				"contentType",
				String.valueOf(attachmentBase64.getContentType()));
		}

		if (attachmentBase64.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(attachmentBase64.getCustomFields()));
		}

		if (attachmentBase64.getDisplayDate() == null) {
			map.put("displayDate", null);
		}
		else {
			map.put(
				"displayDate",
				liferayToJSONDateFormat.format(
					attachmentBase64.getDisplayDate()));
		}

		if (attachmentBase64.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(
					attachmentBase64.getExpirationDate()));
		}

		if (attachmentBase64.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(attachmentBase64.getExternalReferenceCode()));
		}

		if (attachmentBase64.getGalleryEnabled() == null) {
			map.put("galleryEnabled", null);
		}
		else {
			map.put(
				"galleryEnabled",
				String.valueOf(attachmentBase64.getGalleryEnabled()));
		}

		if (attachmentBase64.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(attachmentBase64.getId()));
		}

		if (attachmentBase64.getNeverExpire() == null) {
			map.put("neverExpire", null);
		}
		else {
			map.put(
				"neverExpire",
				String.valueOf(attachmentBase64.getNeverExpire()));
		}

		if (attachmentBase64.getOptions() == null) {
			map.put("options", null);
		}
		else {
			map.put("options", String.valueOf(attachmentBase64.getOptions()));
		}

		if (attachmentBase64.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(attachmentBase64.getPriority()));
		}

		if (attachmentBase64.getSrc() == null) {
			map.put("src", null);
		}
		else {
			map.put("src", String.valueOf(attachmentBase64.getSrc()));
		}

		if (attachmentBase64.getTags() == null) {
			map.put("tags", null);
		}
		else {
			map.put("tags", String.valueOf(attachmentBase64.getTags()));
		}

		if (attachmentBase64.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(attachmentBase64.getTitle()));
		}

		if (attachmentBase64.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(attachmentBase64.getType()));
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
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "galleryEnabled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "src")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tags")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
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
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setContentType(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.commerce.admin.catalog.client.custom.
						field.CustomField[] customFieldsArray = new
						com.liferay.headless.commerce.admin.catalog.client.
							custom.field.CustomField
							[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.commerce.admin.catalog.client.
								custom.field.CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					attachmentBase64.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setDisplayDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					attachmentBase64.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "galleryEnabled")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setGalleryEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setNeverExpire(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setOptions(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "src")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setSrc((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tags")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setTags(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setTitle(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					attachmentBase64.setType(
						Integer.valueOf((String)jsonParserFieldValue));
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