/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.constant.v1_0.StringTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity2;
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
public class ChildTestEntity2SerDes {

	public static ChildTestEntity2 toDTO(String json) {
		ChildTestEntity2JSONParser childTestEntity2JSONParser =
			new ChildTestEntity2JSONParser();

		return childTestEntity2JSONParser.parseToDTO(json);
	}

	public static ChildTestEntity2[] toDTOs(String json) {
		ChildTestEntity2JSONParser childTestEntity2JSONParser =
			new ChildTestEntity2JSONParser();

		return childTestEntity2JSONParser.parseToDTOs(json);
	}

	public static String toJSON(ChildTestEntity2 childTestEntity2) {
		if (childTestEntity2 == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (childTestEntity2.getProperty2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"property2\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity2.getProperty2()));

			sb.append("\"");
		}

		if (childTestEntity2.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					childTestEntity2.getDateCreated()));

			sb.append("\"");
		}

		if (childTestEntity2.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					childTestEntity2.getDateModified()));

			sb.append("\"");
		}

		if (childTestEntity2.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity2.getDescription()));

			sb.append("\"");
		}

		if (childTestEntity2.getDocumentId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentId\": ");

			sb.append(childTestEntity2.getDocumentId());
		}

		if (childTestEntity2.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(childTestEntity2.getId());
		}

		if (childTestEntity2.getJsonProperty() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"jsonProperty\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity2.getJsonProperty()));

			sb.append("\"");
		}

		if (childTestEntity2.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity2.getName()));

			sb.append("\"");
		}

		if (childTestEntity2.getNestedTestEntity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedTestEntity\": ");

			sb.append(String.valueOf(childTestEntity2.getNestedTestEntity()));
		}

		if (childTestEntity2.getSelf() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"self\": ");

			sb.append("\"");

			sb.append(_escape(childTestEntity2.getSelf()));

			sb.append("\"");
		}

		if (childTestEntity2.getStringTestEntities() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stringTestEntities\": ");

			sb.append("[");

			for (int i = 0; i < childTestEntity2.getStringTestEntities().length;
				 i++) {

				sb.append(childTestEntity2.getStringTestEntities()[i]);

				if ((i + 1) < childTestEntity2.getStringTestEntities().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (childTestEntity2.getStringTestEntity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stringTestEntity\": ");

			sb.append(childTestEntity2.getStringTestEntity());
		}

		if (childTestEntity2.getTestEntities() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"testEntities\": ");

			sb.append(String.valueOf(childTestEntity2.getTestEntities()));
		}

		if (childTestEntity2.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(childTestEntity2.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ChildTestEntity2JSONParser childTestEntity2JSONParser =
			new ChildTestEntity2JSONParser();

		return childTestEntity2JSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ChildTestEntity2 childTestEntity2) {
		if (childTestEntity2 == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (childTestEntity2.getProperty2() == null) {
			map.put("property2", null);
		}
		else {
			map.put(
				"property2", String.valueOf(childTestEntity2.getProperty2()));
		}

		if (childTestEntity2.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					childTestEntity2.getDateCreated()));
		}

		if (childTestEntity2.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					childTestEntity2.getDateModified()));
		}

		if (childTestEntity2.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(childTestEntity2.getDescription()));
		}

		if (childTestEntity2.getDocumentId() == null) {
			map.put("documentId", null);
		}
		else {
			map.put(
				"documentId", String.valueOf(childTestEntity2.getDocumentId()));
		}

		if (childTestEntity2.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(childTestEntity2.getId()));
		}

		if (childTestEntity2.getJsonProperty() == null) {
			map.put("jsonProperty", null);
		}
		else {
			map.put(
				"jsonProperty",
				String.valueOf(childTestEntity2.getJsonProperty()));
		}

		if (childTestEntity2.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(childTestEntity2.getName()));
		}

		if (childTestEntity2.getNestedTestEntity() == null) {
			map.put("nestedTestEntity", null);
		}
		else {
			map.put(
				"nestedTestEntity",
				String.valueOf(childTestEntity2.getNestedTestEntity()));
		}

		if (childTestEntity2.getSelf() == null) {
			map.put("self", null);
		}
		else {
			map.put("self", String.valueOf(childTestEntity2.getSelf()));
		}

		if (childTestEntity2.getStringTestEntities() == null) {
			map.put("stringTestEntities", null);
		}
		else {
			map.put(
				"stringTestEntities",
				String.valueOf(childTestEntity2.getStringTestEntities()));
		}

		if (childTestEntity2.getStringTestEntity() == null) {
			map.put("stringTestEntity", null);
		}
		else {
			map.put(
				"stringTestEntity",
				String.valueOf(childTestEntity2.getStringTestEntity()));
		}

		if (childTestEntity2.getTestEntities() == null) {
			map.put("testEntities", null);
		}
		else {
			map.put(
				"testEntities",
				String.valueOf(childTestEntity2.getTestEntities()));
		}

		if (childTestEntity2.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(childTestEntity2.getType()));
		}

		return map;
	}

	public static class ChildTestEntity2JSONParser
		extends BaseJSONParser<ChildTestEntity2> {

		@Override
		protected ChildTestEntity2 createDTO() {
			return new ChildTestEntity2();
		}

		@Override
		protected ChildTestEntity2[] createDTOArray(int size) {
			return new ChildTestEntity2[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "property2")) {
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
			ChildTestEntity2 childTestEntity2, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "property2")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setProperty2((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "documentId")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setDocumentId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "jsonProperty")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setJsonProperty(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "nestedTestEntity")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setNestedTestEntity(
						NestedTestEntitySerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "self")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setSelf((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "stringTestEntities")) {

				if (jsonParserFieldValue != null) {
					childTestEntity2.setStringTestEntities(
						(StringTestEntity[])jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "stringTestEntity")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setStringTestEntity(
						StringTestEntity.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "testEntities")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setTestEntities(
						TestEntitySerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					childTestEntity2.setType(
						ChildTestEntity2.Type.create(
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