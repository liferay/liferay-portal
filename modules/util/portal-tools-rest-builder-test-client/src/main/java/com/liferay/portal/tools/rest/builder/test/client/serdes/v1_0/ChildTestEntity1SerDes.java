/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.constant.v1_0.StringTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity1;
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
public class ChildTestEntity1SerDes {

	public static ChildTestEntity1 toDTO(String json) {
		ChildTestEntity1JSONParser childTestEntity1JSONParser =
			new ChildTestEntity1JSONParser();

		return childTestEntity1JSONParser.parseToDTO(json);
	}

	public static ChildTestEntity1[] toDTOs(String json) {
		ChildTestEntity1JSONParser childTestEntity1JSONParser =
			new ChildTestEntity1JSONParser();

		return childTestEntity1JSONParser.parseToDTOs(json);
	}

	public static String toJSON(ChildTestEntity1 childTestEntity1) {
		if (childTestEntity1 == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (childTestEntity1.getProperty1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"property1\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity1.getProperty1()));

			sb.append("\"");
		}

		if (childTestEntity1.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					childTestEntity1.getDateCreated()));

			sb.append("\"");
		}

		if (childTestEntity1.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					childTestEntity1.getDateModified()));

			sb.append("\"");
		}

		if (childTestEntity1.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity1.getDescription()));

			sb.append("\"");
		}

		if (childTestEntity1.getDocumentId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentId\": ");

			sb.append(childTestEntity1.getDocumentId());
		}

		if (childTestEntity1.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(childTestEntity1.getId());
		}

		if (childTestEntity1.getJsonProperty() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"jsonProperty\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity1.getJsonProperty()));

			sb.append("\"");
		}

		if (childTestEntity1.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity1.getName()));

			sb.append("\"");
		}

		if (childTestEntity1.getNestedTestEntity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedTestEntity\": ");

			sb.append(String.valueOf(childTestEntity1.getNestedTestEntity()));
		}

		if (childTestEntity1.getSelf() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"self\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity1.getSelf()));

			sb.append("\"");
		}

		if (childTestEntity1.getStringTestEntities() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stringTestEntities\": ");

			sb.append("[");

			for (int i = 0; i < childTestEntity1.getStringTestEntities().length;
				 i++) {

				sb.append(childTestEntity1.getStringTestEntities()[i]);

				if ((i + 1) < childTestEntity1.getStringTestEntities().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (childTestEntity1.getStringTestEntity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stringTestEntity\": ");

			sb.append(childTestEntity1.getStringTestEntity());
		}

		if (childTestEntity1.getTestEntities() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"testEntities\": ");

			sb.append(String.valueOf(childTestEntity1.getTestEntities()));
		}

		if (childTestEntity1.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(childTestEntity1.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ChildTestEntity1JSONParser childTestEntity1JSONParser =
			new ChildTestEntity1JSONParser();

		return childTestEntity1JSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ChildTestEntity1 childTestEntity1) {
		if (childTestEntity1 == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (childTestEntity1.getProperty1() == null) {
			map.put("property1", null);
		}
		else {
			map.put(
				"property1", String.valueOf(childTestEntity1.getProperty1()));
		}

		if (childTestEntity1.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					childTestEntity1.getDateCreated()));
		}

		if (childTestEntity1.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					childTestEntity1.getDateModified()));
		}

		if (childTestEntity1.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(childTestEntity1.getDescription()));
		}

		if (childTestEntity1.getDocumentId() == null) {
			map.put("documentId", null);
		}
		else {
			map.put(
				"documentId", String.valueOf(childTestEntity1.getDocumentId()));
		}

		if (childTestEntity1.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(childTestEntity1.getId()));
		}

		if (childTestEntity1.getJsonProperty() == null) {
			map.put("jsonProperty", null);
		}
		else {
			map.put(
				"jsonProperty",
				String.valueOf(childTestEntity1.getJsonProperty()));
		}

		if (childTestEntity1.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(childTestEntity1.getName()));
		}

		if (childTestEntity1.getNestedTestEntity() == null) {
			map.put("nestedTestEntity", null);
		}
		else {
			map.put(
				"nestedTestEntity",
				String.valueOf(childTestEntity1.getNestedTestEntity()));
		}

		if (childTestEntity1.getSelf() == null) {
			map.put("self", null);
		}
		else {
			map.put("self", String.valueOf(childTestEntity1.getSelf()));
		}

		if (childTestEntity1.getStringTestEntities() == null) {
			map.put("stringTestEntities", null);
		}
		else {
			map.put(
				"stringTestEntities",
				String.valueOf(childTestEntity1.getStringTestEntities()));
		}

		if (childTestEntity1.getStringTestEntity() == null) {
			map.put("stringTestEntity", null);
		}
		else {
			map.put(
				"stringTestEntity",
				String.valueOf(childTestEntity1.getStringTestEntity()));
		}

		if (childTestEntity1.getTestEntities() == null) {
			map.put("testEntities", null);
		}
		else {
			map.put(
				"testEntities",
				String.valueOf(childTestEntity1.getTestEntities()));
		}

		if (childTestEntity1.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(childTestEntity1.getType()));
		}

		return map;
	}

	public static class ChildTestEntity1JSONParser
		extends BaseJSONParser<ChildTestEntity1> {

		@Override
		protected ChildTestEntity1 createDTO() {
			return new ChildTestEntity1();
		}

		@Override
		protected ChildTestEntity1[] createDTOArray(int size) {
			return new ChildTestEntity1[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "property1")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
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
			ChildTestEntity1 childTestEntity1, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "property1")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setProperty1((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "documentId")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setDocumentId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "jsonProperty")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setJsonProperty(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "nestedTestEntity")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setNestedTestEntity(
						NestedTestEntitySerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "self")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setSelf((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "stringTestEntities")) {

				if (jsonParserFieldValue != null) {
					childTestEntity1.setStringTestEntities(
						(StringTestEntity[])jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "stringTestEntity")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setStringTestEntity(
						StringTestEntity.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "testEntities")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setTestEntities(
						TestEntitySerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					childTestEntity1.setType(
						ChildTestEntity1.Type.create(
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