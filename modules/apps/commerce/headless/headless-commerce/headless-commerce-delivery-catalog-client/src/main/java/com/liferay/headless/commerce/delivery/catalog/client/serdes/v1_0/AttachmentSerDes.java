/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.Attachment;
import com.liferay.headless.commerce.delivery.catalog.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class AttachmentSerDes {

	public static Attachment toDTO(String json) {
		AttachmentJSONParser attachmentJSONParser = new AttachmentJSONParser();

		return attachmentJSONParser.parseToDTO(json);
	}

	public static Attachment[] toDTOs(String json) {
		AttachmentJSONParser attachmentJSONParser = new AttachmentJSONParser();

		return attachmentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Attachment attachment) {
		if (attachment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (attachment.getAttachment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attachment\": ");

			sb.append("\"");

			sb.append(_escape(attachment.getAttachment()));

			sb.append("\"");
		}

		if (attachment.getCdnEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cdnEnabled\": ");

			sb.append(attachment.getCdnEnabled());
		}

		if (attachment.getCdnURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cdnURL\": ");

			sb.append("\"");

			sb.append(_escape(attachment.getCdnURL()));

			sb.append("\"");
		}

		if (attachment.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < attachment.getCustomFields().length; i++) {
				sb.append(attachment.getCustomFields()[i]);

				if ((i + 1) < attachment.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (attachment.getDisplayDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(attachment.getDisplayDate()));

			sb.append("\"");
		}

		if (attachment.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(attachment.getExpirationDate()));

			sb.append("\"");
		}

		if (attachment.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(attachment.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (attachment.getFileEntryId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileEntryId\": ");

			sb.append(attachment.getFileEntryId());
		}

		if (attachment.getGalleryEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"galleryEnabled\": ");

			sb.append(attachment.getGalleryEnabled());
		}

		if (attachment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(attachment.getId());
		}

		if (attachment.getNeverExpire() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(attachment.getNeverExpire());
		}

		if (attachment.getOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append(_toJSON(attachment.getOptions()));
		}

		if (attachment.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(attachment.getPriority());
		}

		if (attachment.getSrc() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"src\": ");

			sb.append("\"");

			sb.append(_escape(attachment.getSrc()));

			sb.append("\"");
		}

		if (attachment.getTags() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tags\": ");

			sb.append("[");

			for (int i = 0; i < attachment.getTags().length; i++) {
				sb.append(_toJSON(attachment.getTags()[i]));

				if ((i + 1) < attachment.getTags().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (attachment.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(attachment.getTitle()));

			sb.append("\"");
		}

		if (attachment.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(attachment.getType());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AttachmentJSONParser attachmentJSONParser = new AttachmentJSONParser();

		return attachmentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Attachment attachment) {
		if (attachment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (attachment.getAttachment() == null) {
			map.put("attachment", null);
		}
		else {
			map.put("attachment", String.valueOf(attachment.getAttachment()));
		}

		if (attachment.getCdnEnabled() == null) {
			map.put("cdnEnabled", null);
		}
		else {
			map.put("cdnEnabled", String.valueOf(attachment.getCdnEnabled()));
		}

		if (attachment.getCdnURL() == null) {
			map.put("cdnURL", null);
		}
		else {
			map.put("cdnURL", String.valueOf(attachment.getCdnURL()));
		}

		if (attachment.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields", String.valueOf(attachment.getCustomFields()));
		}

		if (attachment.getDisplayDate() == null) {
			map.put("displayDate", null);
		}
		else {
			map.put(
				"displayDate",
				liferayToJSONDateFormat.format(attachment.getDisplayDate()));
		}

		if (attachment.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(attachment.getExpirationDate()));
		}

		if (attachment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(attachment.getExternalReferenceCode()));
		}

		if (attachment.getFileEntryId() == null) {
			map.put("fileEntryId", null);
		}
		else {
			map.put("fileEntryId", String.valueOf(attachment.getFileEntryId()));
		}

		if (attachment.getGalleryEnabled() == null) {
			map.put("galleryEnabled", null);
		}
		else {
			map.put(
				"galleryEnabled",
				String.valueOf(attachment.getGalleryEnabled()));
		}

		if (attachment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(attachment.getId()));
		}

		if (attachment.getNeverExpire() == null) {
			map.put("neverExpire", null);
		}
		else {
			map.put("neverExpire", String.valueOf(attachment.getNeverExpire()));
		}

		if (attachment.getOptions() == null) {
			map.put("options", null);
		}
		else {
			map.put("options", String.valueOf(attachment.getOptions()));
		}

		if (attachment.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(attachment.getPriority()));
		}

		if (attachment.getSrc() == null) {
			map.put("src", null);
		}
		else {
			map.put("src", String.valueOf(attachment.getSrc()));
		}

		if (attachment.getTags() == null) {
			map.put("tags", null);
		}
		else {
			map.put("tags", String.valueOf(attachment.getTags()));
		}

		if (attachment.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(attachment.getTitle()));
		}

		if (attachment.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(attachment.getType()));
		}

		return map;
	}

	public static class AttachmentJSONParser
		extends BaseJSONParser<Attachment> {

		@Override
		protected Attachment createDTO() {
			return new Attachment();
		}

		@Override
		protected Attachment[] createDTOArray(int size) {
			return new Attachment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "attachment")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cdnEnabled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cdnURL")) {
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
			else if (Objects.equals(jsonParserFieldName, "fileEntryId")) {
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
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Attachment attachment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "attachment")) {
				if (jsonParserFieldValue != null) {
					attachment.setAttachment((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cdnEnabled")) {
				if (jsonParserFieldValue != null) {
					attachment.setCdnEnabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cdnURL")) {
				if (jsonParserFieldValue != null) {
					attachment.setCdnURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.commerce.delivery.catalog.client.
						custom.field.CustomField[] customFieldsArray = new
						com.liferay.headless.commerce.delivery.catalog.client.
							custom.field.CustomField
							[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.commerce.delivery.catalog.
								client.custom.field.CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					attachment.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				if (jsonParserFieldValue != null) {
					attachment.setDisplayDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					attachment.setExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					attachment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileEntryId")) {
				if (jsonParserFieldValue != null) {
					attachment.setFileEntryId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "galleryEnabled")) {
				if (jsonParserFieldValue != null) {
					attachment.setGalleryEnabled((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					attachment.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				if (jsonParserFieldValue != null) {
					attachment.setNeverExpire((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				if (jsonParserFieldValue != null) {
					attachment.setOptions(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					attachment.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "src")) {
				if (jsonParserFieldValue != null) {
					attachment.setSrc((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tags")) {
				if (jsonParserFieldValue != null) {
					attachment.setTags(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					attachment.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					attachment.setType(
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