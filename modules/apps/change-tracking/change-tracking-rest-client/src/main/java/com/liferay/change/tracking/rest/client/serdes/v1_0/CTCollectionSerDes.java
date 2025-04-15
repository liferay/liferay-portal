/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.client.serdes.v1_0;

import com.liferay.change.tracking.rest.client.dto.v1_0.CTCollection;
import com.liferay.change.tracking.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author David Truong
 * @generated
 */
@Generated("")
public class CTCollectionSerDes {

	public static CTCollection toDTO(String json) {
		CTCollectionJSONParser ctCollectionJSONParser =
			new CTCollectionJSONParser();

		return ctCollectionJSONParser.parseToDTO(json);
	}

	public static CTCollection[] toDTOs(String json) {
		CTCollectionJSONParser ctCollectionJSONParser =
			new CTCollectionJSONParser();

		return ctCollectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CTCollection ctCollection) {
		if (ctCollection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ctCollection.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(ctCollection.getActions()));
		}

		if (ctCollection.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(ctCollection.getDateCreated()));

			sb.append("\"");
		}

		if (ctCollection.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(ctCollection.getDateModified()));

			sb.append("\"");
		}

		if (ctCollection.getDateScheduled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateScheduled\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					ctCollection.getDateScheduled()));

			sb.append("\"");
		}

		if (ctCollection.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(ctCollection.getDescription()));

			sb.append("\"");
		}

		if (ctCollection.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(ctCollection.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (ctCollection.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(ctCollection.getId());
		}

		if (ctCollection.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(ctCollection.getName()));

			sb.append("\"");
		}

		if (ctCollection.getOwnerName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ownerName\": ");

			sb.append("\"");

			sb.append(_escape(ctCollection.getOwnerName()));

			sb.append("\"");
		}

		if (ctCollection.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(ctCollection.getStatus()));
		}

		if (ctCollection.getStatusMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"statusMessage\": ");

			sb.append("\"");

			sb.append(_escape(ctCollection.getStatusMessage()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CTCollectionJSONParser ctCollectionJSONParser =
			new CTCollectionJSONParser();

		return ctCollectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CTCollection ctCollection) {
		if (ctCollection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ctCollection.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(ctCollection.getActions()));
		}

		if (ctCollection.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(ctCollection.getDateCreated()));
		}

		if (ctCollection.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(ctCollection.getDateModified()));
		}

		if (ctCollection.getDateScheduled() == null) {
			map.put("dateScheduled", null);
		}
		else {
			map.put(
				"dateScheduled",
				liferayToJSONDateFormat.format(
					ctCollection.getDateScheduled()));
		}

		if (ctCollection.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(ctCollection.getDescription()));
		}

		if (ctCollection.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(ctCollection.getExternalReferenceCode()));
		}

		if (ctCollection.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(ctCollection.getId()));
		}

		if (ctCollection.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(ctCollection.getName()));
		}

		if (ctCollection.getOwnerName() == null) {
			map.put("ownerName", null);
		}
		else {
			map.put("ownerName", String.valueOf(ctCollection.getOwnerName()));
		}

		if (ctCollection.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(ctCollection.getStatus()));
		}

		if (ctCollection.getStatusMessage() == null) {
			map.put("statusMessage", null);
		}
		else {
			map.put(
				"statusMessage",
				String.valueOf(ctCollection.getStatusMessage()));
		}

		return map;
	}

	public static class CTCollectionJSONParser
		extends BaseJSONParser<CTCollection> {

		@Override
		protected CTCollection createDTO() {
			return new CTCollection();
		}

		@Override
		protected CTCollection[] createDTOArray(int size) {
			return new CTCollection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateScheduled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ownerName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "statusMessage")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CTCollection ctCollection, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					ctCollection.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					ctCollection.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					ctCollection.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateScheduled")) {
				if (jsonParserFieldValue != null) {
					ctCollection.setDateScheduled(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					ctCollection.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					ctCollection.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					ctCollection.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					ctCollection.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ownerName")) {
				if (jsonParserFieldValue != null) {
					ctCollection.setOwnerName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					ctCollection.setStatus(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "statusMessage")) {
				if (jsonParserFieldValue != null) {
					ctCollection.setStatusMessage((String)jsonParserFieldValue);
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