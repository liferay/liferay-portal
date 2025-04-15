/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.client.serdes.v1_0;

import com.liferay.change.tracking.rest.client.dto.v1_0.CTProcess;
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
public class CTProcessSerDes {

	public static CTProcess toDTO(String json) {
		CTProcessJSONParser ctProcessJSONParser = new CTProcessJSONParser();

		return ctProcessJSONParser.parseToDTO(json);
	}

	public static CTProcess[] toDTOs(String json) {
		CTProcessJSONParser ctProcessJSONParser = new CTProcessJSONParser();

		return ctProcessJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CTProcess ctProcess) {
		if (ctProcess == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ctProcess.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(ctProcess.getActions()));
		}

		if (ctProcess.getCtCollectionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ctCollectionId\": ");

			sb.append(ctProcess.getCtCollectionId());
		}

		if (ctProcess.getDatePublished() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(ctProcess.getDatePublished()));

			sb.append("\"");
		}

		if (ctProcess.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(ctProcess.getDescription()));

			sb.append("\"");
		}

		if (ctProcess.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(ctProcess.getId());
		}

		if (ctProcess.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(ctProcess.getName()));

			sb.append("\"");
		}

		if (ctProcess.getOwnerName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ownerName\": ");

			sb.append("\"");

			sb.append(_escape(ctProcess.getOwnerName()));

			sb.append("\"");
		}

		if (ctProcess.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(ctProcess.getStatus()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CTProcessJSONParser ctProcessJSONParser = new CTProcessJSONParser();

		return ctProcessJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CTProcess ctProcess) {
		if (ctProcess == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (ctProcess.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(ctProcess.getActions()));
		}

		if (ctProcess.getCtCollectionId() == null) {
			map.put("ctCollectionId", null);
		}
		else {
			map.put(
				"ctCollectionId",
				String.valueOf(ctProcess.getCtCollectionId()));
		}

		if (ctProcess.getDatePublished() == null) {
			map.put("datePublished", null);
		}
		else {
			map.put(
				"datePublished",
				liferayToJSONDateFormat.format(ctProcess.getDatePublished()));
		}

		if (ctProcess.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(ctProcess.getDescription()));
		}

		if (ctProcess.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(ctProcess.getId()));
		}

		if (ctProcess.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(ctProcess.getName()));
		}

		if (ctProcess.getOwnerName() == null) {
			map.put("ownerName", null);
		}
		else {
			map.put("ownerName", String.valueOf(ctProcess.getOwnerName()));
		}

		if (ctProcess.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(ctProcess.getStatus()));
		}

		return map;
	}

	public static class CTProcessJSONParser extends BaseJSONParser<CTProcess> {

		@Override
		protected CTProcess createDTO() {
			return new CTProcess();
		}

		@Override
		protected CTProcess[] createDTOArray(int size) {
			return new CTProcess[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "ctCollectionId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
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

			return false;
		}

		@Override
		protected void setField(
			CTProcess ctProcess, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					ctProcess.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ctCollectionId")) {
				if (jsonParserFieldValue != null) {
					ctProcess.setCtCollectionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				if (jsonParserFieldValue != null) {
					ctProcess.setDatePublished(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					ctProcess.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					ctProcess.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					ctProcess.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ownerName")) {
				if (jsonParserFieldValue != null) {
					ctProcess.setOwnerName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					ctProcess.setStatus(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
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