/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.AttachmentUrl;
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
public class AttachmentUrlSerDes {

	public static AttachmentUrl toDTO(String json) {
		AttachmentUrlJSONParser attachmentUrlJSONParser =
			new AttachmentUrlJSONParser();

		return attachmentUrlJSONParser.parseToDTO(json);
	}

	public static AttachmentUrl[] toDTOs(String json) {
		AttachmentUrlJSONParser attachmentUrlJSONParser =
			new AttachmentUrlJSONParser();

		return attachmentUrlJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AttachmentUrl attachmentUrl) {
		if (attachmentUrl == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (attachmentUrl.getContentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(attachmentUrl.getContentType()));

			sb.append("\"");
		}

		if (attachmentUrl.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < attachmentUrl.getCustomFields().length; i++) {
				sb.append(attachmentUrl.getCustomFields()[i]);

				if ((i + 1) < attachmentUrl.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (attachmentUrl.getDisplayDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(attachmentUrl.getDisplayDate()));

			sb.append("\"");
		}

		if (attachmentUrl.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					attachmentUrl.getExpirationDate()));

			sb.append("\"");
		}

		if (attachmentUrl.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(attachmentUrl.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (attachmentUrl.getGalleryEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"galleryEnabled\": ");

			sb.append(attachmentUrl.getGalleryEnabled());
		}

		if (attachmentUrl.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(attachmentUrl.getId());
		}

		if (attachmentUrl.getNeverExpire() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(attachmentUrl.getNeverExpire());
		}

		if (attachmentUrl.getOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append(_toJSON(attachmentUrl.getOptions()));
		}

		if (attachmentUrl.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(attachmentUrl.getPriority());
		}

		if (attachmentUrl.getSrc() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"src\": ");

			sb.append("\"");

			sb.append(_escape(attachmentUrl.getSrc()));

			sb.append("\"");
		}

		if (attachmentUrl.getTags() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tags\": ");

			sb.append("[");

			for (int i = 0; i < attachmentUrl.getTags().length; i++) {
				sb.append(_toJSON(attachmentUrl.getTags()[i]));

				if ((i + 1) < attachmentUrl.getTags().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (attachmentUrl.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append(_toJSON(attachmentUrl.getTitle()));
		}

		if (attachmentUrl.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(attachmentUrl.getType());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AttachmentUrlJSONParser attachmentUrlJSONParser =
			new AttachmentUrlJSONParser();

		return attachmentUrlJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AttachmentUrl attachmentUrl) {
		if (attachmentUrl == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (attachmentUrl.getContentType() == null) {
			map.put("contentType", null);
		}
		else {
			map.put(
				"contentType", String.valueOf(attachmentUrl.getContentType()));
		}

		if (attachmentUrl.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(attachmentUrl.getCustomFields()));
		}

		if (attachmentUrl.getDisplayDate() == null) {
			map.put("displayDate", null);
		}
		else {
			map.put(
				"displayDate",
				liferayToJSONDateFormat.format(attachmentUrl.getDisplayDate()));
		}

		if (attachmentUrl.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(
					attachmentUrl.getExpirationDate()));
		}

		if (attachmentUrl.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(attachmentUrl.getExternalReferenceCode()));
		}

		if (attachmentUrl.getGalleryEnabled() == null) {
			map.put("galleryEnabled", null);
		}
		else {
			map.put(
				"galleryEnabled",
				String.valueOf(attachmentUrl.getGalleryEnabled()));
		}

		if (attachmentUrl.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(attachmentUrl.getId()));
		}

		if (attachmentUrl.getNeverExpire() == null) {
			map.put("neverExpire", null);
		}
		else {
			map.put(
				"neverExpire", String.valueOf(attachmentUrl.getNeverExpire()));
		}

		if (attachmentUrl.getOptions() == null) {
			map.put("options", null);
		}
		else {
			map.put("options", String.valueOf(attachmentUrl.getOptions()));
		}

		if (attachmentUrl.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(attachmentUrl.getPriority()));
		}

		if (attachmentUrl.getSrc() == null) {
			map.put("src", null);
		}
		else {
			map.put("src", String.valueOf(attachmentUrl.getSrc()));
		}

		if (attachmentUrl.getTags() == null) {
			map.put("tags", null);
		}
		else {
			map.put("tags", String.valueOf(attachmentUrl.getTags()));
		}

		if (attachmentUrl.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(attachmentUrl.getTitle()));
		}

		if (attachmentUrl.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(attachmentUrl.getType()));
		}

		return map;
	}

	public static class AttachmentUrlJSONParser
		extends BaseJSONParser<AttachmentUrl> {

		@Override
		protected AttachmentUrl createDTO() {
			return new AttachmentUrl();
		}

		@Override
		protected AttachmentUrl[] createDTOArray(int size) {
			return new AttachmentUrl[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "contentType")) {
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
			AttachmentUrl attachmentUrl, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "contentType")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setContentType((String)jsonParserFieldValue);
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

					attachmentUrl.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setDisplayDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					attachmentUrl.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "galleryEnabled")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setGalleryEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setNeverExpire((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setOptions(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "src")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setSrc((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tags")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setTags(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setTitle(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					attachmentUrl.setType(
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