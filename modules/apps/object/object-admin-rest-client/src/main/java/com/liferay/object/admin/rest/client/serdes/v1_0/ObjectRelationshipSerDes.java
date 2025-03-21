/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectRelationshipSerDes {

	public static ObjectRelationship toDTO(String json) {
		ObjectRelationshipJSONParser objectRelationshipJSONParser =
			new ObjectRelationshipJSONParser();

		return objectRelationshipJSONParser.parseToDTO(json);
	}

	public static ObjectRelationship[] toDTOs(String json) {
		ObjectRelationshipJSONParser objectRelationshipJSONParser =
			new ObjectRelationshipJSONParser();

		return objectRelationshipJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectRelationship objectRelationship) {
		if (objectRelationship == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectRelationship.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(objectRelationship.getActions()));
		}

		if (objectRelationship.getDeletionType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionType\": ");

			sb.append("\"");

			sb.append(objectRelationship.getDeletionType());

			sb.append("\"");
		}

		if (objectRelationship.getEdge() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"edge\": ");

			sb.append(objectRelationship.getEdge());
		}

		if (objectRelationship.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectRelationship.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectRelationship.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectRelationship.getId());
		}

		if (objectRelationship.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append(_toJSON(objectRelationship.getLabel()));
		}

		if (objectRelationship.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(objectRelationship.getName()));

			sb.append("\"");
		}

		if (objectRelationship.getObjectDefinitionExternalReferenceCode1() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode1\": ");

			sb.append("\"");

			sb.append(
				_escape(
					objectRelationship.
						getObjectDefinitionExternalReferenceCode1()));

			sb.append("\"");
		}

		if (objectRelationship.getObjectDefinitionExternalReferenceCode2() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode2\": ");

			sb.append("\"");

			sb.append(
				_escape(
					objectRelationship.
						getObjectDefinitionExternalReferenceCode2()));

			sb.append("\"");
		}

		if (objectRelationship.getObjectDefinitionId1() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionId1\": ");

			sb.append(objectRelationship.getObjectDefinitionId1());
		}

		if (objectRelationship.getObjectDefinitionId2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionId2\": ");

			sb.append(objectRelationship.getObjectDefinitionId2());
		}

		if (objectRelationship.getObjectDefinitionModifiable2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionModifiable2\": ");

			sb.append(objectRelationship.getObjectDefinitionModifiable2());
		}

		if (objectRelationship.getObjectDefinitionName2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionName2\": ");

			sb.append("\"");

			sb.append(_escape(objectRelationship.getObjectDefinitionName2()));

			sb.append("\"");
		}

		if (objectRelationship.getObjectDefinitionScope2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionScope2\": ");

			sb.append("\"");

			sb.append(_escape(objectRelationship.getObjectDefinitionScope2()));

			sb.append("\"");
		}

		if (objectRelationship.getObjectDefinitionSystem2() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionSystem2\": ");

			sb.append(objectRelationship.getObjectDefinitionSystem2());
		}

		if (objectRelationship.getObjectField() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectField\": ");

			sb.append(String.valueOf(objectRelationship.getObjectField()));
		}

		if (objectRelationship.getParameterObjectFieldId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameterObjectFieldId\": ");

			sb.append(objectRelationship.getParameterObjectFieldId());
		}

		if (objectRelationship.getParameterObjectFieldName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameterObjectFieldName\": ");

			sb.append("\"");

			sb.append(
				_escape(objectRelationship.getParameterObjectFieldName()));

			sb.append("\"");
		}

		if (objectRelationship.getReverse() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reverse\": ");

			sb.append(objectRelationship.getReverse());
		}

		if (objectRelationship.getSystem() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(objectRelationship.getSystem());
		}

		if (objectRelationship.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(objectRelationship.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectRelationshipJSONParser objectRelationshipJSONParser =
			new ObjectRelationshipJSONParser();

		return objectRelationshipJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ObjectRelationship objectRelationship) {

		if (objectRelationship == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectRelationship.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(objectRelationship.getActions()));
		}

		if (objectRelationship.getDeletionType() == null) {
			map.put("deletionType", null);
		}
		else {
			map.put(
				"deletionType",
				String.valueOf(objectRelationship.getDeletionType()));
		}

		if (objectRelationship.getEdge() == null) {
			map.put("edge", null);
		}
		else {
			map.put("edge", String.valueOf(objectRelationship.getEdge()));
		}

		if (objectRelationship.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(objectRelationship.getExternalReferenceCode()));
		}

		if (objectRelationship.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectRelationship.getId()));
		}

		if (objectRelationship.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(objectRelationship.getLabel()));
		}

		if (objectRelationship.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(objectRelationship.getName()));
		}

		if (objectRelationship.getObjectDefinitionExternalReferenceCode1() ==
				null) {

			map.put("objectDefinitionExternalReferenceCode1", null);
		}
		else {
			map.put(
				"objectDefinitionExternalReferenceCode1",
				String.valueOf(
					objectRelationship.
						getObjectDefinitionExternalReferenceCode1()));
		}

		if (objectRelationship.getObjectDefinitionExternalReferenceCode2() ==
				null) {

			map.put("objectDefinitionExternalReferenceCode2", null);
		}
		else {
			map.put(
				"objectDefinitionExternalReferenceCode2",
				String.valueOf(
					objectRelationship.
						getObjectDefinitionExternalReferenceCode2()));
		}

		if (objectRelationship.getObjectDefinitionId1() == null) {
			map.put("objectDefinitionId1", null);
		}
		else {
			map.put(
				"objectDefinitionId1",
				String.valueOf(objectRelationship.getObjectDefinitionId1()));
		}

		if (objectRelationship.getObjectDefinitionId2() == null) {
			map.put("objectDefinitionId2", null);
		}
		else {
			map.put(
				"objectDefinitionId2",
				String.valueOf(objectRelationship.getObjectDefinitionId2()));
		}

		if (objectRelationship.getObjectDefinitionModifiable2() == null) {
			map.put("objectDefinitionModifiable2", null);
		}
		else {
			map.put(
				"objectDefinitionModifiable2",
				String.valueOf(
					objectRelationship.getObjectDefinitionModifiable2()));
		}

		if (objectRelationship.getObjectDefinitionName2() == null) {
			map.put("objectDefinitionName2", null);
		}
		else {
			map.put(
				"objectDefinitionName2",
				String.valueOf(objectRelationship.getObjectDefinitionName2()));
		}

		if (objectRelationship.getObjectDefinitionScope2() == null) {
			map.put("objectDefinitionScope2", null);
		}
		else {
			map.put(
				"objectDefinitionScope2",
				String.valueOf(objectRelationship.getObjectDefinitionScope2()));
		}

		if (objectRelationship.getObjectDefinitionSystem2() == null) {
			map.put("objectDefinitionSystem2", null);
		}
		else {
			map.put(
				"objectDefinitionSystem2",
				String.valueOf(
					objectRelationship.getObjectDefinitionSystem2()));
		}

		if (objectRelationship.getObjectField() == null) {
			map.put("objectField", null);
		}
		else {
			map.put(
				"objectField",
				String.valueOf(objectRelationship.getObjectField()));
		}

		if (objectRelationship.getParameterObjectFieldId() == null) {
			map.put("parameterObjectFieldId", null);
		}
		else {
			map.put(
				"parameterObjectFieldId",
				String.valueOf(objectRelationship.getParameterObjectFieldId()));
		}

		if (objectRelationship.getParameterObjectFieldName() == null) {
			map.put("parameterObjectFieldName", null);
		}
		else {
			map.put(
				"parameterObjectFieldName",
				String.valueOf(
					objectRelationship.getParameterObjectFieldName()));
		}

		if (objectRelationship.getReverse() == null) {
			map.put("reverse", null);
		}
		else {
			map.put("reverse", String.valueOf(objectRelationship.getReverse()));
		}

		if (objectRelationship.getSystem() == null) {
			map.put("system", null);
		}
		else {
			map.put("system", String.valueOf(objectRelationship.getSystem()));
		}

		if (objectRelationship.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(objectRelationship.getType()));
		}

		return map;
	}

	public static class ObjectRelationshipJSONParser
		extends BaseJSONParser<ObjectRelationship> {

		@Override
		protected ObjectRelationship createDTO() {
			return new ObjectRelationship();
		}

		@Override
		protected ObjectRelationship[] createDTOArray(int size) {
			return new ObjectRelationship[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "deletionType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "edge")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode1")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode2")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionId1")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionId2")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionModifiable2")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionName2")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionScope2")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionSystem2")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "objectField")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parameterObjectFieldId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parameterObjectFieldName")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "reverse")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectRelationship objectRelationship, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					objectRelationship.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletionType")) {
				if (jsonParserFieldValue != null) {
					objectRelationship.setDeletionType(
						ObjectRelationship.DeletionType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "edge")) {
				if (jsonParserFieldValue != null) {
					objectRelationship.setEdge((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectRelationship.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectRelationship.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					objectRelationship.setLabel(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					objectRelationship.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode1")) {

				if (jsonParserFieldValue != null) {
					objectRelationship.
						setObjectDefinitionExternalReferenceCode1(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode2")) {

				if (jsonParserFieldValue != null) {
					objectRelationship.
						setObjectDefinitionExternalReferenceCode2(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionId1")) {

				if (jsonParserFieldValue != null) {
					objectRelationship.setObjectDefinitionId1(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionId2")) {

				if (jsonParserFieldValue != null) {
					objectRelationship.setObjectDefinitionId2(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionModifiable2")) {

				if (jsonParserFieldValue != null) {
					objectRelationship.setObjectDefinitionModifiable2(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionName2")) {

				if (jsonParserFieldValue != null) {
					objectRelationship.setObjectDefinitionName2(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionScope2")) {

				if (jsonParserFieldValue != null) {
					objectRelationship.setObjectDefinitionScope2(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionSystem2")) {

				if (jsonParserFieldValue != null) {
					objectRelationship.setObjectDefinitionSystem2(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectField")) {
				if (jsonParserFieldValue != null) {
					objectRelationship.setObjectField(
						ObjectFieldSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parameterObjectFieldId")) {

				if (jsonParserFieldValue != null) {
					objectRelationship.setParameterObjectFieldId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parameterObjectFieldName")) {

				if (jsonParserFieldValue != null) {
					objectRelationship.setParameterObjectFieldName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "reverse")) {
				if (jsonParserFieldValue != null) {
					objectRelationship.setReverse(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				if (jsonParserFieldValue != null) {
					objectRelationship.setSystem((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					objectRelationship.setType(
						ObjectRelationship.Type.create(
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