/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectDefinitionValidationError;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectDefinitionValidationResponse;
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
public class ObjectDefinitionValidationResponseSerDes {

	public static ObjectDefinitionValidationResponse toDTO(String json) {
		ObjectDefinitionValidationResponseJSONParser
			objectDefinitionValidationResponseJSONParser =
				new ObjectDefinitionValidationResponseJSONParser();

		return objectDefinitionValidationResponseJSONParser.parseToDTO(json);
	}

	public static ObjectDefinitionValidationResponse[] toDTOs(String json) {
		ObjectDefinitionValidationResponseJSONParser
			objectDefinitionValidationResponseJSONParser =
				new ObjectDefinitionValidationResponseJSONParser();

		return objectDefinitionValidationResponseJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ObjectDefinitionValidationResponse objectDefinitionValidationResponse) {

		if (objectDefinitionValidationResponse == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectDefinitionValidationResponse.
				getObjectDefinitionValidationErrors() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionValidationErrors\": ");

			sb.append("[");

			for (int i = 0;
				 i < objectDefinitionValidationResponse.
					 getObjectDefinitionValidationErrors().length;
				 i++) {

				sb.append(
					String.valueOf(
						objectDefinitionValidationResponse.
							getObjectDefinitionValidationErrors()[i]));

				if ((i + 1) < objectDefinitionValidationResponse.
						getObjectDefinitionValidationErrors().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectDefinitionValidationResponseJSONParser
			objectDefinitionValidationResponseJSONParser =
				new ObjectDefinitionValidationResponseJSONParser();

		return objectDefinitionValidationResponseJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ObjectDefinitionValidationResponse objectDefinitionValidationResponse) {

		if (objectDefinitionValidationResponse == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectDefinitionValidationResponse.
				getObjectDefinitionValidationErrors() == null) {

			map.put("objectDefinitionValidationErrors", null);
		}
		else {
			map.put(
				"objectDefinitionValidationErrors",
				String.valueOf(
					objectDefinitionValidationResponse.
						getObjectDefinitionValidationErrors()));
		}

		return map;
	}

	public static class ObjectDefinitionValidationResponseJSONParser
		extends BaseJSONParser<ObjectDefinitionValidationResponse> {

		@Override
		protected ObjectDefinitionValidationResponse createDTO() {
			return new ObjectDefinitionValidationResponse();
		}

		@Override
		protected ObjectDefinitionValidationResponse[] createDTOArray(
			int size) {

			return new ObjectDefinitionValidationResponse[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "objectDefinitionValidationErrors")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectDefinitionValidationResponse
				objectDefinitionValidationResponse,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "objectDefinitionValidationErrors")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ObjectDefinitionValidationError[]
						objectDefinitionValidationErrorsArray =
							new ObjectDefinitionValidationError
								[jsonParserFieldValues.length];

					for (int i = 0;
						 i < objectDefinitionValidationErrorsArray.length;
						 i++) {

						objectDefinitionValidationErrorsArray[i] =
							ObjectDefinitionValidationErrorSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					objectDefinitionValidationResponse.
						setObjectDefinitionValidationErrors(
							objectDefinitionValidationErrorsArray);
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