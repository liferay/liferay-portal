/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectState;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectStateTransition;
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
public class ObjectStateSerDes {

	public static ObjectState toDTO(String json) {
		ObjectStateJSONParser objectStateJSONParser =
			new ObjectStateJSONParser();

		return objectStateJSONParser.parseToDTO(json);
	}

	public static ObjectState[] toDTOs(String json) {
		ObjectStateJSONParser objectStateJSONParser =
			new ObjectStateJSONParser();

		return objectStateJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectState objectState) {
		if (objectState == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectState.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectState.getId());
		}

		if (objectState.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(objectState.getKey()));

			sb.append("\"");
		}

		if (objectState.getObjectStateTransitions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectStateTransitions\": ");

			sb.append("[");

			for (int i = 0; i < objectState.getObjectStateTransitions().length;
				 i++) {

				sb.append(
					String.valueOf(objectState.getObjectStateTransitions()[i]));

				if ((i + 1) < objectState.getObjectStateTransitions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectStateJSONParser objectStateJSONParser =
			new ObjectStateJSONParser();

		return objectStateJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ObjectState objectState) {
		if (objectState == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectState.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectState.getId()));
		}

		if (objectState.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(objectState.getKey()));
		}

		if (objectState.getObjectStateTransitions() == null) {
			map.put("objectStateTransitions", null);
		}
		else {
			map.put(
				"objectStateTransitions",
				String.valueOf(objectState.getObjectStateTransitions()));
		}

		return map;
	}

	public static class ObjectStateJSONParser
		extends BaseJSONParser<ObjectState> {

		@Override
		protected ObjectState createDTO() {
			return new ObjectState();
		}

		@Override
		protected ObjectState[] createDTOArray(int size) {
			return new ObjectState[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectStateTransitions")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectState objectState, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectState.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					objectState.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectStateTransitions")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ObjectStateTransition[] objectStateTransitionsArray =
						new ObjectStateTransition[jsonParserFieldValues.length];

					for (int i = 0; i < objectStateTransitionsArray.length;
						 i++) {

						objectStateTransitionsArray[i] =
							ObjectStateTransitionSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					objectState.setObjectStateTransitions(
						objectStateTransitionsArray);
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