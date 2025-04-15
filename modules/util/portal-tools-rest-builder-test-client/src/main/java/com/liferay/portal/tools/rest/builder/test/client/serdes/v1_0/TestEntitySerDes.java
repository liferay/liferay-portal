/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity1;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity2;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.ChildTestEntity3;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.TestEntity;
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
public class TestEntitySerDes {

	public static TestEntity toDTO(String json) {
		TestEntityJSONParser testEntityJSONParser = new TestEntityJSONParser();

		return testEntityJSONParser.parseToDTO(json);
	}

	public static TestEntity[] toDTOs(String json) {
		TestEntityJSONParser testEntityJSONParser = new TestEntityJSONParser();

		return testEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TestEntity testEntity) {
		if (testEntity == null) {
			return "null";
		}

		TestEntity.Type type = testEntity.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("ChildTestEntity1")) {
				return ChildTestEntity1SerDes.toJSON(
					(ChildTestEntity1)testEntity);
			}

			if (typeString.equals("ChildTestEntity2")) {
				return ChildTestEntity2SerDes.toJSON(
					(ChildTestEntity2)testEntity);
			}

			if (typeString.equals("ChildTestEntity3")) {
				return ChildTestEntity3SerDes.toJSON(
					(ChildTestEntity3)testEntity);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		TestEntityJSONParser testEntityJSONParser = new TestEntityJSONParser();

		return testEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TestEntity testEntity) {
		if (testEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (testEntity.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(testEntity.getDateCreated()));
		}

		if (testEntity.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(testEntity.getDateModified()));
		}

		if (testEntity.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(testEntity.getDescription()));
		}

		if (testEntity.getDocumentId() == null) {
			map.put("documentId", null);
		}
		else {
			map.put("documentId", String.valueOf(testEntity.getDocumentId()));
		}

		if (testEntity.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(testEntity.getId()));
		}

		if (testEntity.getJsonProperty() == null) {
			map.put("jsonProperty", null);
		}
		else {
			map.put(
				"jsonProperty", String.valueOf(testEntity.getJsonProperty()));
		}

		if (testEntity.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(testEntity.getName()));
		}

		if (testEntity.getNestedTestEntity() == null) {
			map.put("nestedTestEntity", null);
		}
		else {
			map.put(
				"nestedTestEntity",
				String.valueOf(testEntity.getNestedTestEntity()));
		}

		if (testEntity.getSelf() == null) {
			map.put("self", null);
		}
		else {
			map.put("self", String.valueOf(testEntity.getSelf()));
		}

		if (testEntity.getTestEntities() == null) {
			map.put("testEntities", null);
		}
		else {
			map.put(
				"testEntities", String.valueOf(testEntity.getTestEntities()));
		}

		if (testEntity.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(testEntity.getType()));
		}

		return map;
	}

	public static class TestEntityJSONParser
		extends BaseJSONParser<TestEntity> {

		@Override
		protected TestEntity createDTO() {
			return null;
		}

		@Override
		protected TestEntity[] createDTOArray(int size) {
			return new TestEntity[size];
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
			else if (Objects.equals(jsonParserFieldName, "testEntities")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public TestEntity parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("ChildTestEntity1")) {
					return ChildTestEntity1.toDTO(json);
				}

				if (typeString.equals("ChildTestEntity2")) {
					return ChildTestEntity2.toDTO(json);
				}

				if (typeString.equals("ChildTestEntity3")) {
					return ChildTestEntity3.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			TestEntity testEntity, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					testEntity.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					testEntity.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					testEntity.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "documentId")) {
				if (jsonParserFieldValue != null) {
					testEntity.setDocumentId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					testEntity.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "jsonProperty")) {
				if (jsonParserFieldValue != null) {
					testEntity.setJsonProperty((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					testEntity.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "nestedTestEntity")) {
				if (jsonParserFieldValue != null) {
					testEntity.setNestedTestEntity(
						NestedTestEntitySerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "self")) {
				if (jsonParserFieldValue != null) {
					testEntity.setSelf((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "testEntities")) {
				if (jsonParserFieldValue != null) {
					testEntity.setTestEntities(
						TestEntitySerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					testEntity.setType(
						TestEntity.Type.create((String)jsonParserFieldValue));
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