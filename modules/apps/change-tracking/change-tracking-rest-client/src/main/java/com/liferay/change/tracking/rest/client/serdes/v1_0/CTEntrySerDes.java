/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.client.serdes.v1_0;

import com.liferay.change.tracking.rest.client.dto.v1_0.CTEntry;
import com.liferay.change.tracking.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author David Truong
 * @generated
 */
@Generated("")
public class CTEntrySerDes {

	public static CTEntry toDTO(String json) {
		CTEntryJSONParser ctEntryJSONParser = new CTEntryJSONParser();

		return ctEntryJSONParser.parseToDTO(json);
	}

	public static CTEntry[] toDTOs(String json) {
		CTEntryJSONParser ctEntryJSONParser = new CTEntryJSONParser();

		return ctEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CTEntry ctEntry) {
		if (ctEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ctEntry.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(ctEntry.getActions()));
		}

		if (ctEntry.getChangeType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"changeType\": ");

			sb.append("\"");

			sb.append(_escape(ctEntry.getChangeType()));

			sb.append("\"");
		}

		if (ctEntry.getCtCollectionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ctCollectionId\": ");

			sb.append(ctEntry.getCtCollectionId());
		}

		if (ctEntry.getCtCollectionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ctCollectionName\": ");

			sb.append("\"");

			sb.append(_escape(ctEntry.getCtCollectionName()));

			sb.append("\"");
		}

		if (ctEntry.getCtCollectionStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ctCollectionStatus\": ");

			sb.append(String.valueOf(ctEntry.getCtCollectionStatus()));
		}

		if (ctEntry.getCtCollectionStatusDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ctCollectionStatusDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					ctEntry.getCtCollectionStatusDate()));

			sb.append("\"");
		}

		if (ctEntry.getCtCollectionStatusUserName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ctCollectionStatusUserName\": ");

			sb.append("\"");

			sb.append(_escape(ctEntry.getCtCollectionStatusUserName()));

			sb.append("\"");
		}

		if (ctEntry.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(ctEntry.getDateCreated()));

			sb.append("\"");
		}

		if (ctEntry.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(ctEntry.getDateModified()));

			sb.append("\"");
		}

		if (ctEntry.getHideable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hideable\": ");

			sb.append(ctEntry.getHideable());
		}

		if (ctEntry.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(ctEntry.getId());
		}

		if (ctEntry.getModelClassNameId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modelClassNameId\": ");

			sb.append(ctEntry.getModelClassNameId());
		}

		if (ctEntry.getModelClassPK() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modelClassPK\": ");

			sb.append(ctEntry.getModelClassPK());
		}

		if (ctEntry.getOwnerId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ownerId\": ");

			sb.append(ctEntry.getOwnerId());
		}

		if (ctEntry.getOwnerName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ownerName\": ");

			sb.append("\"");

			sb.append(_escape(ctEntry.getOwnerName()));

			sb.append("\"");
		}

		if (ctEntry.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(ctEntry.getSiteId());
		}

		if (ctEntry.getSiteName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteName\": ");

			sb.append("\"");

			sb.append(_escape(ctEntry.getSiteName()));

			sb.append("\"");
		}

		if (ctEntry.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(ctEntry.getStatus()));
		}

		if (ctEntry.getStatusMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"statusMessage\": ");

			sb.append("\"");

			sb.append(_escape(ctEntry.getStatusMessage()));

			sb.append("\"");
		}

		if (ctEntry.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(ctEntry.getTitle()));

			sb.append("\"");
		}

		if (ctEntry.getTypeName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeName\": ");

			sb.append("\"");

			sb.append(_escape(ctEntry.getTypeName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CTEntryJSONParser ctEntryJSONParser = new CTEntryJSONParser();

		return ctEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CTEntry ctEntry) {
		if (ctEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ctEntry.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(ctEntry.getActions()));
		}

		if (ctEntry.getChangeType() == null) {
			map.put("changeType", null);
		}
		else {
			map.put("changeType", String.valueOf(ctEntry.getChangeType()));
		}

		if (ctEntry.getCtCollectionId() == null) {
			map.put("ctCollectionId", null);
		}
		else {
			map.put(
				"ctCollectionId", String.valueOf(ctEntry.getCtCollectionId()));
		}

		if (ctEntry.getCtCollectionName() == null) {
			map.put("ctCollectionName", null);
		}
		else {
			map.put(
				"ctCollectionName",
				String.valueOf(ctEntry.getCtCollectionName()));
		}

		if (ctEntry.getCtCollectionStatus() == null) {
			map.put("ctCollectionStatus", null);
		}
		else {
			map.put(
				"ctCollectionStatus",
				String.valueOf(ctEntry.getCtCollectionStatus()));
		}

		if (ctEntry.getCtCollectionStatusDate() == null) {
			map.put("ctCollectionStatusDate", null);
		}
		else {
			map.put(
				"ctCollectionStatusDate",
				liferayToJSONDateFormat.format(
					ctEntry.getCtCollectionStatusDate()));
		}

		if (ctEntry.getCtCollectionStatusUserName() == null) {
			map.put("ctCollectionStatusUserName", null);
		}
		else {
			map.put(
				"ctCollectionStatusUserName",
				String.valueOf(ctEntry.getCtCollectionStatusUserName()));
		}

		if (ctEntry.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(ctEntry.getDateCreated()));
		}

		if (ctEntry.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(ctEntry.getDateModified()));
		}

		if (ctEntry.getHideable() == null) {
			map.put("hideable", null);
		}
		else {
			map.put("hideable", String.valueOf(ctEntry.getHideable()));
		}

		if (ctEntry.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(ctEntry.getId()));
		}

		if (ctEntry.getModelClassNameId() == null) {
			map.put("modelClassNameId", null);
		}
		else {
			map.put(
				"modelClassNameId",
				String.valueOf(ctEntry.getModelClassNameId()));
		}

		if (ctEntry.getModelClassPK() == null) {
			map.put("modelClassPK", null);
		}
		else {
			map.put("modelClassPK", String.valueOf(ctEntry.getModelClassPK()));
		}

		if (ctEntry.getOwnerId() == null) {
			map.put("ownerId", null);
		}
		else {
			map.put("ownerId", String.valueOf(ctEntry.getOwnerId()));
		}

		if (ctEntry.getOwnerName() == null) {
			map.put("ownerName", null);
		}
		else {
			map.put("ownerName", String.valueOf(ctEntry.getOwnerName()));
		}

		if (ctEntry.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(ctEntry.getSiteId()));
		}

		if (ctEntry.getSiteName() == null) {
			map.put("siteName", null);
		}
		else {
			map.put("siteName", String.valueOf(ctEntry.getSiteName()));
		}

		if (ctEntry.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(ctEntry.getStatus()));
		}

		if (ctEntry.getStatusMessage() == null) {
			map.put("statusMessage", null);
		}
		else {
			map.put(
				"statusMessage", String.valueOf(ctEntry.getStatusMessage()));
		}

		if (ctEntry.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(ctEntry.getTitle()));
		}

		if (ctEntry.getTypeName() == null) {
			map.put("typeName", null);
		}
		else {
			map.put("typeName", String.valueOf(ctEntry.getTypeName()));
		}

		return map;
	}

	public static class CTEntryJSONParser extends BaseJSONParser<CTEntry> {

		@Override
		protected CTEntry createDTO() {
			return new CTEntry();
		}

		@Override
		protected CTEntry[] createDTOArray(int size) {
			return new CTEntry[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "changeType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ctCollectionId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ctCollectionName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "ctCollectionStatus")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "ctCollectionStatusDate")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "ctCollectionStatusUserName")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "hideable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modelClassNameId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modelClassPK")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ownerId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ownerName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "statusMessage")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "typeName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CTEntry ctEntry, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "changeType")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setChangeType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ctCollectionId")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setCtCollectionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ctCollectionName")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setCtCollectionName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "ctCollectionStatus")) {

				if (jsonParserFieldValue != null) {
					ctEntry.setCtCollectionStatus(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "ctCollectionStatusDate")) {

				if (jsonParserFieldValue != null) {
					ctEntry.setCtCollectionStatusDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "ctCollectionStatusUserName")) {

				if (jsonParserFieldValue != null) {
					ctEntry.setCtCollectionStatusUserName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "hideable")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setHideable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modelClassNameId")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setModelClassNameId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modelClassPK")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setModelClassPK(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ownerId")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setOwnerId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ownerName")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setOwnerName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteName")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setSiteName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setStatus(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "statusMessage")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setStatusMessage((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "typeName")) {
				if (jsonParserFieldValue != null) {
					ctEntry.setTypeName((String)jsonParserFieldValue);
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