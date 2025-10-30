/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.constant.v1_0.StringTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity3;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class ChildTestEntity3SerDes {

	public static ChildTestEntity3 toDTO(String json) {
		ChildTestEntity3JSONParser childTestEntity3JSONParser =
			new ChildTestEntity3JSONParser();

		return childTestEntity3JSONParser.parseToDTO(json);
	}

	public static ChildTestEntity3[] toDTOs(String json) {
		ChildTestEntity3JSONParser childTestEntity3JSONParser =
			new ChildTestEntity3JSONParser();

		return childTestEntity3JSONParser.parseToDTOs(json);
	}

	public static String toJSON(ChildTestEntity3 childTestEntity3) {
		if (childTestEntity3 == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (childTestEntity3.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					childTestEntity3.getDateCreated()));

			sb.append("\"");
		}

		if (childTestEntity3.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					childTestEntity3.getDateModified()));

			sb.append("\"");
		}

		if (childTestEntity3.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity3.getDescription()));

			sb.append("\"");
		}

		if (childTestEntity3.getDocumentId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentId\": ");

			sb.append(childTestEntity3.getDocumentId());
		}

		if (childTestEntity3.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(childTestEntity3.getId());
		}

		if (childTestEntity3.getJsonProperty() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"jsonProperty\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity3.getJsonProperty()));

			sb.append("\"");
		}

		if (childTestEntity3.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity3.getName()));

			sb.append("\"");
		}

		if (childTestEntity3.getNestedTestEntity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedTestEntity\": ");

			sb.append(String.valueOf(childTestEntity3.getNestedTestEntity()));
		}

		if (childTestEntity3.getSelf() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"self\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity3.getSelf()));

			sb.append("\"");
		}

		if (childTestEntity3.getStringTestEntities() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stringTestEntities\": ");

			sb.append("[");

			for (int i = 0; i < childTestEntity3.getStringTestEntities().length;
				 i++) {

				sb.append(childTestEntity3.getStringTestEntities()[i]);

				if ((i + 1) < childTestEntity3.getStringTestEntities().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (childTestEntity3.getStringTestEntity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stringTestEntity\": ");

			sb.append(childTestEntity3.getStringTestEntity());
		}

		if (childTestEntity3.getTestEntities() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"testEntities\": ");

			sb.append(String.valueOf(childTestEntity3.getTestEntities()));
		}

		if (childTestEntity3.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(childTestEntity3.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ChildTestEntity3JSONParser childTestEntity3JSONParser =
			new ChildTestEntity3JSONParser();

		return childTestEntity3JSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ChildTestEntity3 childTestEntity3) {
		if (childTestEntity3 == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (childTestEntity3.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					childTestEntity3.getDateCreated()));
		}

		if (childTestEntity3.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					childTestEntity3.getDateModified()));
		}

		if (childTestEntity3.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(childTestEntity3.getDescription()));
		}

		if (childTestEntity3.getDocumentId() == null) {
			map.put("documentId", null);
		}
		else {
			map.put(
				"documentId", String.valueOf(childTestEntity3.getDocumentId()));
		}

		if (childTestEntity3.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(childTestEntity3.getId()));
		}

		if (childTestEntity3.getJsonProperty() == null) {
			map.put("jsonProperty", null);
		}
		else {
			map.put(
				"jsonProperty",
				String.valueOf(childTestEntity3.getJsonProperty()));
		}

		if (childTestEntity3.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(childTestEntity3.getName()));
		}

		if (childTestEntity3.getNestedTestEntity() == null) {
			map.put("nestedTestEntity", null);
		}
		else {
			map.put(
				"nestedTestEntity",
				String.valueOf(childTestEntity3.getNestedTestEntity()));
		}

		if (childTestEntity3.getSelf() == null) {
			map.put("self", null);
		}
		else {
			map.put("self", String.valueOf(childTestEntity3.getSelf()));
		}

		if (childTestEntity3.getStringTestEntities() == null) {
			map.put("stringTestEntities", null);
		}
		else {
			map.put(
				"stringTestEntities",
				String.valueOf(childTestEntity3.getStringTestEntities()));
		}

		if (childTestEntity3.getStringTestEntity() == null) {
			map.put("stringTestEntity", null);
		}
		else {
			map.put(
				"stringTestEntity",
				String.valueOf(childTestEntity3.getStringTestEntity()));
		}

		if (childTestEntity3.getTestEntities() == null) {
			map.put("testEntities", null);
		}
		else {
			map.put(
				"testEntities",
				String.valueOf(childTestEntity3.getTestEntities()));
		}

		if (childTestEntity3.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(childTestEntity3.getType()));
		}

		return map;
	}

	public static class ChildTestEntity3JSONParser
		extends BaseJSONParser<ChildTestEntity3> {

		@Override
		protected ChildTestEntity3 createDTO() {
			return new ChildTestEntity3();
		}

		@Override
		protected ChildTestEntity3[] createDTOArray(int size) {
			return new ChildTestEntity3[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "documentId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "jsonProperty")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "nestedTestEntity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "self")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "stringTestEntities")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "stringTestEntity")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "testEntities")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ChildTestEntity3 childTestEntity3, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "documentId")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setDocumentId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "jsonProperty")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setJsonProperty(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "nestedTestEntity")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setNestedTestEntity(
						NestedTestEntitySerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "self")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setSelf((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "stringTestEntities")) {

				if (jsonParserFieldValue != null) {
					childTestEntity3.setStringTestEntities(
						(StringTestEntity[])jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "stringTestEntity")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setStringTestEntity(
						StringTestEntity.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "testEntities")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setTestEntities(
						TestEntitySerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					childTestEntity3.setType(
						ChildTestEntity3.Type.create(
							(String)jsonParserFieldValue));
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