/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectActionSerDes {

	public static ObjectAction toDTO(String json) {
		ObjectActionJSONParser objectActionJSONParser =
			new ObjectActionJSONParser();

		return objectActionJSONParser.parseToDTO(json);
	}

	public static ObjectAction[] toDTOs(String json) {
		ObjectActionJSONParser objectActionJSONParser =
			new ObjectActionJSONParser();

		return objectActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectAction objectAction) {
		if (objectAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (objectAction.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(objectAction.getActions()));
		}

		if (objectAction.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(objectAction.getActive());
		}

		if (objectAction.getConditionExpression() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"conditionExpression\": ");

			sb.append("\"");

			sb.append(_escape(objectAction.getConditionExpression()));

			sb.append("\"");
		}

		if (objectAction.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(objectAction.getDateCreated()));

			sb.append("\"");
		}

		if (objectAction.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(objectAction.getDateModified()));

			sb.append("\"");
		}

		if (objectAction.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(objectAction.getDescription()));

			sb.append("\"");
		}

		if (objectAction.getErrorMessage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append(_toJSON(objectAction.getErrorMessage()));
		}

		if (objectAction.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(objectAction.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectAction.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectAction.getId());
		}

		if (objectAction.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append(_toJSON(objectAction.getLabel()));
		}

		if (objectAction.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(objectAction.getName()));

			sb.append("\"");
		}

		if (objectAction.getObjectActionExecutorKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectActionExecutorKey\": ");

			sb.append("\"");

			sb.append(_escape(objectAction.getObjectActionExecutorKey()));

			sb.append("\"");
		}

		if (objectAction.getObjectActionTriggerKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectActionTriggerKey\": ");

			sb.append("\"");

			sb.append(_escape(objectAction.getObjectActionTriggerKey()));

			sb.append("\"");
		}

		if (objectAction.getParameters() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parameters\": ");

			sb.append(_toJSON(objectAction.getParameters()));
		}

		if (objectAction.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(objectAction.getStatus()));
		}

		if (objectAction.getSystem() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(objectAction.getSystem());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectActionJSONParser objectActionJSONParser =
			new ObjectActionJSONParser();

		return objectActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ObjectAction objectAction) {
		if (objectAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (objectAction.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(objectAction.getActions()));
		}

		if (objectAction.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(objectAction.getActive()));
		}

		if (objectAction.getConditionExpression() == null) {
			map.put("conditionExpression", null);
		}
		else {
			map.put(
				"conditionExpression",
				String.valueOf(objectAction.getConditionExpression()));
		}

		if (objectAction.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(objectAction.getDateCreated()));
		}

		if (objectAction.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(objectAction.getDateModified()));
		}

		if (objectAction.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(objectAction.getDescription()));
		}

		if (objectAction.getErrorMessage() == null) {
			map.put("errorMessage", null);
		}
		else {
			map.put(
				"errorMessage", String.valueOf(objectAction.getErrorMessage()));
		}

		if (objectAction.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(objectAction.getExternalReferenceCode()));
		}

		if (objectAction.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectAction.getId()));
		}

		if (objectAction.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(objectAction.getLabel()));
		}

		if (objectAction.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(objectAction.getName()));
		}

		if (objectAction.getObjectActionExecutorKey() == null) {
			map.put("objectActionExecutorKey", null);
		}
		else {
			map.put(
				"objectActionExecutorKey",
				String.valueOf(objectAction.getObjectActionExecutorKey()));
		}

		if (objectAction.getObjectActionTriggerKey() == null) {
			map.put("objectActionTriggerKey", null);
		}
		else {
			map.put(
				"objectActionTriggerKey",
				String.valueOf(objectAction.getObjectActionTriggerKey()));
		}

		if (objectAction.getParameters() == null) {
			map.put("parameters", null);
		}
		else {
			map.put("parameters", String.valueOf(objectAction.getParameters()));
		}

		if (objectAction.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(objectAction.getStatus()));
		}

		if (objectAction.getSystem() == null) {
			map.put("system", null);
		}
		else {
			map.put("system", String.valueOf(objectAction.getSystem()));
		}

		return map;
	}

	public static class ObjectActionJSONParser
		extends BaseJSONParser<ObjectAction> {

		@Override
		protected ObjectAction createDTO() {
			return new ObjectAction();
		}

		@Override
		protected ObjectAction[] createDTOArray(int size) {
			return new ObjectAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "conditionExpression")) {

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
			else if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				return true;
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
						jsonParserFieldName, "objectActionExecutorKey")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectActionTriggerKey")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parameters")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectAction objectAction, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					objectAction.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					objectAction.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "conditionExpression")) {

				if (jsonParserFieldValue != null) {
					objectAction.setConditionExpression(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					objectAction.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					objectAction.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					objectAction.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "errorMessage")) {
				if (jsonParserFieldValue != null) {
					objectAction.setErrorMessage(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectAction.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectAction.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					objectAction.setLabel(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					objectAction.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectActionExecutorKey")) {

				if (jsonParserFieldValue != null) {
					objectAction.setObjectActionExecutorKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectActionTriggerKey")) {

				if (jsonParserFieldValue != null) {
					objectAction.setObjectActionTriggerKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parameters")) {
				if (jsonParserFieldValue != null) {
					objectAction.setParameters(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					objectAction.setStatus(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				if (jsonParserFieldValue != null) {
					objectAction.setSystem((Boolean)jsonParserFieldValue);
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