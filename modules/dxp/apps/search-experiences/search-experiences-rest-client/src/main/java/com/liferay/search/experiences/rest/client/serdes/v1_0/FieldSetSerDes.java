/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.Field;
import com.liferay.search.experiences.rest.client.dto.v1_0.FieldSet;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class FieldSetSerDes {

	public static FieldSet toDTO(String json) {
		FieldSetJSONParser fieldSetJSONParser = new FieldSetJSONParser();

		return fieldSetJSONParser.parseToDTO(json);
	}

	public static FieldSet[] toDTOs(String json) {
		FieldSetJSONParser fieldSetJSONParser = new FieldSetJSONParser();

		return fieldSetJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FieldSet fieldSet) {
		if (fieldSet == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fieldSet.getFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fields\": ");

			sb.append("[");

			for (int i = 0; i < fieldSet.getFields().length; i++) {
				sb.append(String.valueOf(fieldSet.getFields()[i]));

				if ((i + 1) < fieldSet.getFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FieldSetJSONParser fieldSetJSONParser = new FieldSetJSONParser();

		return fieldSetJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FieldSet fieldSet) {
		if (fieldSet == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fieldSet.getFields() == null) {
			map.put("fields", null);
		}
		else {
			map.put("fields", String.valueOf(fieldSet.getFields()));
		}

		return map;
	}

	public static class FieldSetJSONParser extends BaseJSONParser<FieldSet> {

		@Override
		protected FieldSet createDTO() {
			return new FieldSet();
		}

		@Override
		protected FieldSet[] createDTOArray(int size) {
			return new FieldSet[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "fields")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FieldSet fieldSet, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "fields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Field[] fieldsArray =
						new Field[jsonParserFieldValues.length];

					for (int i = 0; i < fieldsArray.length; i++) {
						fieldsArray[i] = FieldSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					fieldSet.setFields(fieldsArray);
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